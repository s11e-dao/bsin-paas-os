package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.ChainCoin;
import me.flyray.bsin.domain.entity.CustomerChainCoin;
import me.flyray.bsin.domain.entity.Wallet;
import me.flyray.bsin.domain.request.WalletDTO;
import me.flyray.bsin.domain.response.WalletVO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.biz.WalletAccountBiz;
import me.flyray.bsin.infrastructure.mapper.CustomerChainCoinMapper;
import me.flyray.bsin.infrastructure.mapper.WalletMapper;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.utils.I18eCode;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.dto.ChainWalletDto;
import me.flyray.bsin.blockchain.service.BsinBlockChainEngineFactory;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.facade.service.CustomerService;
import me.flyray.bsin.facade.service.WalletService;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.RespBodyHandler;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author bolei
 * @date 2023/6/26 15:21
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/wallet", timeout = 6000)
@ApiModule(value = "wallet")
@Service
public class WalletServiceImpl implements WalletService {

  @Autowired private BsinBlockChainEngineFactory bsinBlockChainEngineFactory;

  @Autowired
  private WalletMapper walletMapper;
  @Autowired
  private WalletAccountBiz walletAccountBiz;
  @Autowired
  private CustomerChainCoinMapper customerChainCoinMapper;

  @DubboReference(version = "${dubbo.provider.version}")
  private CustomerService customerService;

  /**
   * 1、生成普通账户
   * 2、生成6551智能合约钱包账户
   *
   * @param requestMap
   * @return
   * @throws Exception
   */
  @Override
  @ApiDoc(desc = "createWallet")
  @ShenyuDubboClient("/createWallet")
  public Map<String, Object> createWallet(Map<String, Object> requestMap) throws Exception {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    ChainWalletDto chainWalletDto =
        BsinServiceContext.getReqBodyDto(ChainWalletDto.class, requestMap);
    BsinBlockChainEngine bsinBlockChainEngine =
        bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(chainWalletDto.getChainType());
    Map wallet =
        bsinBlockChainEngine.createWallet(
            chainWalletDto.getPassword(), chainWalletDto.getChainEnv());
    Map<String, Object> requestWalletMap = new HashMap<>();
    requestWalletMap.put("customerNo", loginUser.getCustomerNo());
    requestWalletMap.put("walletAddress", wallet.get("address"));
    requestWalletMap.put("walletPrivateKey", wallet.get("privateKey"));
    return RespBodyHandler.setRespBodyDto(requestWalletMap);
  }

  @Override
  @ApiDoc(desc = "createMPCWallet")
  @ShenyuDubboClient("/createMPCWallet")
  @Transactional(rollbackFor = Exception.class)
  public void createMPCWallet(WalletDTO walletDTO) {
    log.info("请求WalletService.createMPCWallet,参数:{}", walletDTO);
    LoginUser user = LoginInfoContextHelper.getLoginUser();
    Wallet wallet = new Wallet();
    BeanUtils.copyProperties(walletDTO,wallet);

    // 2、创建钱包
    String serialNo = BsinSnowflake.getId();
    wallet.setSerialNo(serialNo);
    wallet.setStatus(1);    // 正常
    wallet.setWalletTag("DEPOSIT");
    wallet.setCreateBy(user.getUserId());
    wallet.setCreateTime(new Date());
    wallet.setTenantId(user.getTenantId());
    wallet.setBizRoleTypeNo(user.getBizRoleTypeNo());
    wallet.setBizRoleType(user.getBizRoleType());
    walletMapper.insert(wallet);

    List<ChainCoin> chainCoinList = new ArrayList<>();
    // 默认EVM钱包
    if(wallet.getType() == 1){
      CustomerChainCoin customerChainCoin = new CustomerChainCoin();
      customerChainCoin.setTenantId(wallet.getTenantId());
      customerChainCoin.setBizRoleType(wallet.getBizRoleType());
      customerChainCoin.setBizRoleTypeNo(wallet.getBizRoleTypeNo());
      if(wallet.getBizRoleType() != 4){
        customerChainCoin.setCreateRoleAccountFlag(1);
      }else {
        customerChainCoin.setCreateUserAccountFlag(0);
      }
      chainCoinList = customerChainCoinMapper.selectChainCoinList(customerChainCoin);
    }else{
      // 自定义钱包
      if(wallet.getEnv().equals("EVM")){
        // TODO EVM 对应的本币
      }
    }

    // 3、 根据支持的币种创建钱包地址，并创建钱包账户
    if(chainCoinList != null && !chainCoinList.isEmpty()){
      for(ChainCoin chainCoin : chainCoinList){
        walletAccountBiz.createWalletAccount(wallet,chainCoin.getSerialNo());
      }
    }

  }

  @Override
  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  public Page<WalletVO> getPageList(WalletDTO walletDTO) {
    log.debug("请求WalletService.pageList,参数:{}", walletDTO);
    try{
      LoginUser user = LoginInfoContextHelper.getLoginUser();
      if(walletDTO.getCurrent() == null){
        walletDTO.setCurrent(1);
      }
      if(walletDTO.getSize() == null){
        walletDTO.setSize(10);
      }
      walletDTO.setTenantId(user.getTenantId());
      return walletMapper.pageList(new Page<>(walletDTO.getCurrent(),walletDTO.getSize()),walletDTO);
    }catch (BusinessException be){
      throw be;
    }catch (Exception e){
      e.printStackTrace();
      throw new BusinessException("SYSTEM ERROR");
    }
  }

  @Override
  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Transactional(rollbackFor = Exception.class)
  public void edit(WalletDTO walletDTO) {
    log.debug("请求WalletService.editWallet,参数:{}", walletDTO);
    LoginUser user = LoginInfoContextHelper.getLoginUser();
    Wallet updateWallet = new Wallet();
    try{
      updateWallet.setSerialNo(walletDTO.getSerialNo());
      updateWallet.setWalletName(walletDTO.getWalletName());
      updateWallet.setStatus(walletDTO.getStatus());
      updateWallet.setWalletTag(walletDTO.getWalletTag());
      updateWallet.setRemark(walletDTO.getRemark());
      updateWallet.setOutUserId(walletDTO.getOutUserId());
      updateWallet.setUpdateTime(new Date());
      updateWallet.setCreateBy(user.getUserId());
      walletMapper.updateById(updateWallet);
    }catch (BusinessException be){
      throw be;
    }catch (Exception e){
      e.printStackTrace();
      throw new BusinessException("SYSTEM ERROR");
    }
  }

  @Override
  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Transactional(rollbackFor = Exception.class)
  public void delete(WalletDTO walletDTO) {
    log.debug("请求WalletService.delWallet,参数:{}", walletDTO);
    LoginUser user = LoginInfoContextHelper.getLoginUser();
    try{
      Wallet wallet = walletMapper.selectById(walletDTO.getSerialNo());
      if(wallet == null){
        throw new BusinessException("WALLET_NOT_EXIST");
      }
      wallet.setUpdateTime(new Date());
      wallet.setUpdateBy(user.getUserId());
      walletMapper.updateDelFlag(wallet);
    }catch (BusinessException be){
      throw be;
    }catch (Exception e){
      e.printStackTrace();
      throw new BusinessException("SYSTEM ERROR");
    }
  }

}
