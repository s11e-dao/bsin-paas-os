package me.flyray.bsin.server.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.BlackWhiteListAddress;
import me.flyray.bsin.domain.entity.ChainCoin;
import me.flyray.bsin.domain.entity.Wallet;
import me.flyray.bsin.domain.entity.WalletAccount;
import me.flyray.bsin.domain.entity.customer.CustomerChainCoin;
import me.flyray.bsin.domain.entity.customer.Merchant;
import me.flyray.bsin.domain.request.WalletCreateRequest;
import me.flyray.bsin.domain.request.WalletDTO;
import me.flyray.bsin.domain.response.WalletVO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.WalletService;
import me.flyray.bsin.infrastructure.biz.SmsBiz;
import me.flyray.bsin.infrastructure.biz.WalletAccountBiz;
import me.flyray.bsin.infrastructure.mapper.ChainCoinMapper;
import me.flyray.bsin.infrastructure.mapper.WalletMapper;
import me.flyray.bsin.infrastructure.mapper.customer.CustomerChainCoinMapper;
import me.flyray.bsin.infrastructure.mapper.customer.MerchantMapper;
import me.flyray.bsin.infrastructure.utils.OkHttpUtils;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinResultEntity;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.utils.I18eCode;
import okhttp3.Response;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author Admin
* @description 针对表【crm_wallet(钱包;)】的数据库操作Service实现
* @createDate 2024-04-24 20:22:04
*/
@Slf4j
@DubboService
@ApiModule(value = "wallet")
@ShenyuDubboService(value = "/wallet",timeout = 30000)
public class WalletServiceImpl implements WalletService {
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private WalletAccountBiz walletAccountBiz;
    @Autowired
    private CustomerChainCoinMapper customerChainCoinMapper;
    @Autowired
    private SmsBiz smsBiz;


    @Override
    @ApiDoc(desc = "create")
    @ShenyuDubboClient("/create")
    @Transactional(rollbackFor = Exception.class)
    public void createWallet(WalletDTO walletDTO) {
        log.debug("请求WalletService.createWallet,参数:{}", walletDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            // 1、短信验证
            smsBiz.verifyCode(walletDTO.getUniqueKey(), walletDTO.getValidateCode());
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
            wallet.setBizRoleNo(user.getBizRoleNo());
            wallet.setBizRoleType(user.getBizRoleType());
            walletMapper.insert(wallet);

            List<ChainCoin> chainCoinList = new ArrayList<>();
            // 默认钱包
            if(wallet.getType() == 1){
                CustomerChainCoin customerChainCoin = new CustomerChainCoin();
                customerChainCoin.setTenantId(wallet.getTenantId());
                customerChainCoin.setBizRoleType(wallet.getBizRoleType());
                customerChainCoin.setBizRoleNo(wallet.getBizRoleNo());
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

            // 3、创建钱包地址，并创建钱包账户
            if(chainCoinList != null && !chainCoinList.isEmpty()){
                for(ChainCoin chainCoin : chainCoinList){
                    walletAccountBiz.createWalletAccount(wallet,chainCoin.getSerialNo());
                }
            }
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM ERROR");
        }
    }

    @Override
    @ApiDoc(desc = "outCreateWallet")
    @ShenyuDubboClient("/outCreateWallet")
    @Transactional(rollbackFor = Exception.class)
    public void outCreateWallet(Wallet wallet) {
        log.debug("请求WalletService.outCreateWallet,参数:{}", wallet);
        try{
            // 1、创建钱包
            wallet.setSerialNo(BsinSnowflake.getId());
            if(wallet.getWalletName()==null){
                wallet.setWalletName(wallet.getSerialNo());
            }
            wallet.setStatus(1);    // 状态：1、正常
            wallet.setType(1);  // 钱包类型：1、默认
            wallet.setCategory(1); // 钱包种类：1、MPC
            wallet.setEnv("EVM");  // 钱包环境：EVM
            wallet.setWalletTag("DEPOSIT");  // 钱包标识：DEPOSIT 寄存
            wallet.setCreateTime(new Date());
            walletMapper.insert(wallet);

            // 2、查询用户所属的平台的关联币种
            CustomerChainCoin customerChainCoin = new CustomerChainCoin();
            customerChainCoin.setTenantId(wallet.getTenantId());
            customerChainCoin.setBizRoleType(1);
            // TODO 二期 根据用户所属的商户、代理商确定关联币种
            if(wallet.getBizRoleType() != 4){
                customerChainCoin.setCreateRoleAccountFlag(1);
            }else {
                customerChainCoin.setCreateUserAccountFlag(1);
            }
            List<ChainCoin>  chainCoinList = customerChainCoinMapper.selectChainCoinList(customerChainCoin);
            // 3、创建钱包地址，并创建钱包账户
            if(chainCoinList != null && !chainCoinList.isEmpty()){
                for(ChainCoin chainCoin : chainCoinList){
                    walletAccountBiz.createWalletAccount(wallet, chainCoin.getSerialNo());
                }
            }
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(new I18eCode("SYSTEM_ERROR"));
        }
    }

    @Override
    @ApiDoc(desc = "pageList")
    @ShenyuDubboClient("/pageList")
    public Page<WalletVO> pageList(WalletDTO walletDTO) {
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
    public void editWallet(WalletDTO walletDTO) {
        log.debug("请求WalletService.editWallet,参数:{}", walletDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        Wallet updateWallet = new Wallet();
        try{
            // 短信验证
            smsBiz.verifyCode(walletDTO.getUniqueKey(), walletDTO.getValidateCode());
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
    public void delWallet(WalletDTO walletDTO) {
        log.debug("请求WalletService.delWallet,参数:{}", walletDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            // 短信验证
            smsBiz.verifyCode(walletDTO.getUniqueKey(), walletDTO.getValidateCode());
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




