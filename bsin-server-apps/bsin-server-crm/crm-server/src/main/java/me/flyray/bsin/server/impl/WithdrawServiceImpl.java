package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.BsinBlockChainEngine;
import me.flyray.bsin.blockchain.enums.ChainType;
import me.flyray.bsin.blockchain.service.BsinBlockChainEngineFactory;
import me.flyray.bsin.blockchain.utils.Java2ContractTypeParameter;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.Merchant;
import me.flyray.bsin.domain.domain.WithdrawOrder;
import me.flyray.bsin.facade.service.CustomerService;
import me.flyray.bsin.facade.service.WithdrawService;
import me.flyray.bsin.infrastructure.mapper.MerchantMapper;
import me.flyray.bsin.infrastructure.mapper.WithdrawJournalMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author bolei
 * @date 2023/9/12
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/withdraw", timeout = 6000)
@ApiModule(value = "withdraw")
@Service
public class WithdrawServiceImpl implements WithdrawService {

    @Value("${pay.aproveOper.privateKey}")
    private String privateKey;

    @Value("${pay.chainEnv}")
    private String chainEnv;

    @Value("${pay.contractAddress}")
    private String contractAddress;

    @Autowired
    private WithdrawJournalMapper withdrawJournalMapper;
    @Autowired
    private BsinBlockChainEngineFactory bsinBlockChainEngineFactory;
    @Autowired
    private MerchantMapper merchantMapper;
//    @Autowired private CustomerInfoBiz customerInfoBiz;

    @Autowired
    private CustomerService customerService;

    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        WithdrawOrder withdrawJournal = BsinServiceContext.getReqBodyDto(WithdrawOrder.class, requestMap);
        Pagination pagination = (Pagination) requestMap.get("pagination");
        Page<WithdrawOrder> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<WithdrawOrder> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(WithdrawOrder::getCreateTime);
        warapper.eq(WithdrawOrder::getTenantId, loginUser.getTenantId());
        warapper.eq(WithdrawOrder::getMerchantNo, loginUser.getMerchantNo());
        warapper.eq(Objects.nonNull(withdrawJournal.getStatus()), WithdrawOrder::getStatus, withdrawJournal.getStatus());
        IPage<WithdrawOrder> pageList = withdrawJournalMapper.selectPage(page,warapper);
        return RespBodyHandler.setRespPageInfoBodyDto(pageList);
    }

    /**
     * 提现客户类型：提现客户类型：1、个人 2、商户 3、平台
     * 提现之前授权给智能合约
     * 1、扣掉商户链上平台积分(商户自己处理)
     * 2、将商户的平台积分转账给提现账户（平台积分）
     * 3、根据商户支持的链实例化
     * a授权给合约，b调用合约扣走a授权的金额（控制只有b有权限扣授权金额）
     * @param requestMap
     * @return
     */
    @Override
    public Map<String, Object> withdraw(Map<String, Object> requestMap) throws Exception {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        WithdrawOrder withdrawJournal = BsinServiceContext.getReqBodyDto(WithdrawOrder.class, requestMap);
        // 调用链上智能合约扣
        //TODO  根据商户支持的链实例化
        BsinBlockChainEngine bsinBlockChainEngine =
                bsinBlockChainEngineFactory.getBsinBlockChainEngineInstance(ChainType.BSC.getCode());

        // 扣掉商户的平台积分给用户
        // 初始化合约调用参数
        // String chainEnv = "test";
        // 操作人
        // String privateKey = "54818949dd977e2ec85c00522ed3996936eebe83d621a635fcd032c3e8bf65bf";
        // String privateKeyAddress = "0x362919Fa458d7a2282d88C77500810654741241b";
        // String contractAddress = "0xF49cE71F645A385c003F9fc604B70e7637B40C28";
        // 授权的商户地址：0x5FDE75e7df280316E1c8763AcFBA5a275bbEfA16
        // 输入参数
        /**
         *
         * address _fromAddress, // 扣除商户
         * address _toAddress, // 用户地址
         * uint256 _amount
         */
        // 根据商户号获取商户钱包地址
        // 根据商户号获取商户钱包地址
        String merchantNo = withdrawJournal.getMerchantNo();
        Merchant merchant = merchantMapper.selectById(merchantNo);

        //        // 1.获取客户信息
        Map<String, Object> reqCustomerBase = new HashMap();
        reqCustomerBase.put("customerNo", merchant.getCustomerNo());
        Map<String, Object> customerData = customerService.getDetail(reqCustomerBase);
        Map customerBase = (Map) customerData.get("data");

        String fromAddress = (String) customerBase.get("evmWalletAddress");
        String toAddress = withdrawJournal.getPayeeAccount();
        String amount = String.valueOf(withdrawJournal.getAmount());

        // String chainEnv, String privateKey, String gasPrice, String gasLimit, String value, String contractAddress,
        // String method, Java2ContractTypeParameter functionReturnType, Java2ContractTypeParameter functionInputParams
        Java2ContractTypeParameter functionParms01 = new Java2ContractTypeParameter.Builder()
                .addValue("address", List.of(fromAddress)).addParameter()
                .addValue("address", List.of(toAddress)).addParameter()
                .addValue("uint256", List.of(String.valueOf(Convert.toWei(new BigDecimal(amount),Convert.Unit.ETHER)))).addParameter()
                .build();

        Map<String, Object> map = bsinBlockChainEngine.contractWrite(chainEnv,privateKey, null , null
                ,"0",contractAddress,"aproveTransfer"
        ,null, functionParms01,60000);
        System.out.println(map);
        String txHash = (String) map.get("txHash");
        withdrawJournal.setTxHash(txHash);
        withdrawJournal.setTenantId(loginUser.getTenantId());
        withdrawJournal.setMerchantNo(loginUser.getMerchantNo());
        withdrawJournal.setStatus("1");
        withdrawJournalMapper.insert(withdrawJournal);
        return RespBodyHandler.setRespBodyDto(withdrawJournal);
    }

    @Override
    public Map<String, Object> withdrawApply(Map<String, Object> requestMap) {
        WithdrawOrder withdrawJournal = BsinServiceContext.getReqBodyDto(WithdrawOrder.class, requestMap);
        withdrawJournal.setStatus("0");
        withdrawJournalMapper.insert(withdrawJournal);
        return RespBodyHandler.setRespBodyDto(withdrawJournal);
    }

    @Override
    public Map<String, Object> audit(Map<String, Object> requestMap) {

        return null;
    }


    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        WithdrawOrder withdrawJournal = withdrawJournalMapper.selectById(serialNo);
        return RespBodyHandler.setRespBodyDto(withdrawJournal);
    }

}
