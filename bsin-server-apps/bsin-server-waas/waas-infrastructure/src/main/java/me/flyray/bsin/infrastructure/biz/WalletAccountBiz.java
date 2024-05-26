package me.flyray.bsin.infrastructure.biz;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import me.flyray.bsin.domain.domain.ChainCoin;
import me.flyray.bsin.domain.domain.Platform;
import me.flyray.bsin.domain.domain.Wallet;
import me.flyray.bsin.domain.domain.WalletAccount;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.mapper.ChainCoinMapper;
import me.flyray.bsin.infrastructure.mapper.WalletAccountMapper;
import me.flyray.bsin.infrastructure.mapper.WalletMapper;
import me.flyray.bsin.infrastructure.utils.OkHttpUtils;
import me.flyray.bsin.utils.BsinSnowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WalletAccountBiz {

    private static final Logger log = LoggerFactory.getLogger(WalletAccountBiz.class);
    @Autowired
    private WalletAccountMapper walletAccountMapper;
    @Autowired
    private ChainCoinMapper chainCoinMapper;
    @Autowired
    private WalletMapper walletMapper;

    /**
     * 创建钱包账户
     *  1、创建链上钱包并返回地址
     *  2、创建钱包地址
     *  3、创建钱包账户
     * @param wallet
     * @param chainCoinNo
     */
    @Async("taskExecutor")
    public WalletAccount createWalletAccount(Wallet wallet, String chainCoinNo) {
        log.info("开始创建钱包账户，wallet:{},chainCoinNo:{}",wallet,chainCoinNo);
        try{
            ChainCoin chainCoin = chainCoinMapper.selectById(chainCoinNo);
            if(chainCoin == null || chainCoin.getStatus() == 0){
                throw new BusinessException("chain coin not exist or off shelves");
            }

            // 1、创建链上钱包
            String url = "http://192.168.1.118:8125/api/v1/mpc/keygen";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("walletName", wallet.getWalletName());
            jsonObject.put("walletType", "mpc");
            jsonObject.put("chainType", "ERC20");
            jsonObject.put("threshold", 2);
            jsonObject.put("partiyNum", 2);
            List mpcClients = new ArrayList();
            mpcClients.add(0);
            mpcClients.add(1);
            jsonObject.put("mpcClients", mpcClients);
            jsonObject.put("sync", true);
            jsonObject.put("timeout", 1000);
            JSONObject data = OkHttpUtils.httpPost(url, jsonObject);
            String pubKey = (String) data.get("pubkey");
            String address = (String)data.get("address");

            // 2、创建钱包账户
            WalletAccount walletAccount = new WalletAccount();
            String walletAccountId = BsinSnowflake.getId();
            walletAccount.setSerialNo(walletAccountId);
            walletAccount.setAddress(address);
            walletAccount.setPubKey(pubKey);
            walletAccount.setChainCoinNo(chainCoinNo);
            walletAccount.setStatus(1);  // 账户状态 1、正常
            walletAccount.setWalletNo(wallet.getSerialNo());
            walletAccount.setBalance(BigDecimal.ZERO);
            wallet.setBizRoleTypeNo(wallet.getBizRoleTypeNo());
            wallet.setBizRoleType(wallet.getBizRoleType());
            walletAccount.setTenantId(wallet.getTenantId());
            walletAccount.setCreateTime(new Date());
            walletAccountMapper.insert(walletAccount);
            log.info("结束创建钱包账户，wallet:{},chainCoinNo:{}",wallet,chainCoinNo);
            return walletAccount;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("create wallet fail");
        }
    }

    /**
     * 获取平台归集账户
     */
    public WalletAccount getGatherAccount(String tenantId,String chainCoinNo) throws Exception {
        QueryWrapper<Platform> platformQueryWrapper = new QueryWrapper<>();
        platformQueryWrapper.eq("tenant_id", tenantId);
        Platform platform = new Platform();

        QueryWrapper<Wallet> walletQueryWrapper = new QueryWrapper<>();
        walletQueryWrapper.eq("wallet_tag", "GATHER");
        walletQueryWrapper.eq("business_role_type", 1);  // 平台
        walletQueryWrapper.eq("business_role_no", platform.getSerialNo());
        walletQueryWrapper.eq("type", 1); // 默认钱包
        walletQueryWrapper.eq("tenant_id", tenantId);
        walletQueryWrapper.eq("status", 1);  // 状态正常

        Wallet wallet = walletMapper.selectOne(walletQueryWrapper);

        QueryWrapper<WalletAccount> queryWrapper = new QueryWrapper();
        queryWrapper.eq("chain_coin_no", chainCoinNo);
        queryWrapper.eq("wallet_no",wallet.getSerialNo());
        queryWrapper.eq("tenant_id",tenantId);
        WalletAccount walletAccount = walletAccountMapper.selectOne(queryWrapper);
        return walletAccount;
    };
}
