package me.flyray.bsin.server.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.domain.ChainCoin;
import me.flyray.bsin.domain.domain.Wallet;
import me.flyray.bsin.domain.domain.WalletAccount;
import me.flyray.bsin.domain.request.WalletAccountDTO;
import me.flyray.bsin.domain.response.WalletAccountVO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.WalletAccountService;
import me.flyray.bsin.infrastructure.biz.WalletAccountBiz;
import me.flyray.bsin.infrastructure.mapper.ChainCoinMapper;
import me.flyray.bsin.infrastructure.mapper.WalletAccountMapper;
import me.flyray.bsin.infrastructure.mapper.WalletMapper;
import me.flyray.bsin.infrastructure.utils.QrCodeUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
* @author Admin
* @description 针对表【crm_wallet_account(钱包账户;)】的数据库操作Service实现
* @createDate 2024-04-24 20:35:43
*/
@Slf4j
@DubboService
@ApiModule(value = "walletAccount")
@ShenyuDubboService("/walletAccount")
public class WalletAccountServiceImpl implements WalletAccountService {

    @Autowired
    private WalletAccountMapper walletAccountMapper;
    @Autowired
    private ChainCoinMapper chainCoinMapper;
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private WalletAccountBiz walletAccountBiz;

    @Override
    @ShenyuDubboClient("/add")
    @ApiDoc(desc = "add")
    @Transactional(rollbackFor = Exception.class)
    public void addWalletAccount(WalletAccountDTO walletAccountDTO) {
        log.debug("请求WalletAccountService.addWalletAccount,参数:{}", walletAccountDTO);
        try{
            String chainCoinId = walletAccountDTO.getChainCoinNo();
            String walletNo = walletAccountDTO.getWalletNo();
            ChainCoin chainCoin = chainCoinMapper.selectById(chainCoinId);
            if(chainCoin == null ||chainCoin.getStatus()== 0) {
                throw new BusinessException("CHAIN_COIN_NOT_EXISTS_OR_OFF_SHELVES");
            }
            Wallet wallet = walletMapper.selectById(walletNo);
            if (wallet == null) {
                throw new BusinessException("WALLET_NOT_EXISTS");
            }
            // 创建钱包账户
            walletAccountBiz.createWalletAccount(wallet,chainCoinId);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/updateStatus")
    @ApiDoc(desc = "updateStatus")
    public void updateAccountStatus(WalletAccount params) {
        log.debug("请求WalletAccountService.updateAccountStatus,参数:{}", params);
        try{
            WalletAccount walletAccount = new WalletAccount();
            walletAccount.setSerialNo(params.getSerialNo());
            walletAccount.setStatus(params.getStatus());
            walletAccountMapper.updateById(walletAccount);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/pageList")
    @ApiDoc(desc = "pageList")
    public Page<WalletAccountVO> pageList(WalletAccountDTO walletAccountDTO) {
        log.debug("请求WalletAccountService.pageList,参数:{}", walletAccountDTO);
        int current = walletAccountDTO.getCurrent() == null?1:walletAccountDTO.getCurrent();
        int size = walletAccountDTO.getSize() == null?10:walletAccountDTO.getSize();
        return walletAccountMapper.pageList(new Page<>(current,size),walletAccountDTO);
    }


    @Override
    @ShenyuDubboClient("/getAddressQrCode")
    @ApiDoc(desc = "getAddressQrCode")
    public Map<String, Object> getAddressQrCode(String serialNo) {
        log.debug("请求WalletAccountService.getAddressQrCode,参数:{}", serialNo);
        Map<String,Object> map = new HashMap<>();
        try{
            WalletAccountVO walletAccountVO = walletAccountMapper.selectBySerialNo(serialNo);
            String base64Pic = QrCodeUtils.creatRrCode(walletAccountVO.getAddress(), 200,200);
            map.put("qrCode",base64Pic);
            map.put("coin",walletAccountVO.getCoin());
            map.put("chainName",walletAccountVO.getChainName());
            map.put("address",walletAccountVO.getAddress());
            return map;
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }
}




