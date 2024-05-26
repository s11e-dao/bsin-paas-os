package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.ChainCoin;
import me.flyray.bsin.domain.entity.Transaction;
import me.flyray.bsin.domain.entity.TransactionAudit;
import me.flyray.bsin.domain.entity.WalletAccount;
import me.flyray.bsin.domain.request.TransactionDTO;
import me.flyray.bsin.domain.request.TransactionRequest;
import me.flyray.bsin.domain.response.TransactionVO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.TransactionService;
import me.flyray.bsin.infrastructure.biz.TransactionBiz;
import me.flyray.bsin.infrastructure.biz.TransferBiz;
import me.flyray.bsin.infrastructure.mapper.ChainCoinMapper;
import me.flyray.bsin.infrastructure.mapper.TransactionAuditMapper;
import me.flyray.bsin.infrastructure.mapper.TransactionMapper;
import me.flyray.bsin.infrastructure.mapper.WalletAccountMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
* @author Admin
* @description 针对表【crm_transaction(交易记录;)】的数据库操作Service实现
* @createDate 2024-04-24 20:36:00
*/


@Slf4j
@DubboService
@ApiModule(value = "transaction")
@ShenyuDubboService(value = "/transaction" ,timeout = 5000)
@Transactional(rollbackFor = Exception.class)
public class TransactionServiceImpl  implements TransactionService {
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private TransactionAuditMapper transactionAuditMapper;
    @Autowired
    private ChainCoinMapper chainCoinMapper;
    @Autowired
    private WalletAccountMapper walletAccountMapper;
    @Autowired
    private TransactionBiz transactionBiz;
    @Autowired
    private TransferBiz transferBiz;

    @Override
    @ShenyuDubboClient("/create")
    @ApiDoc(desc = "create")
    public void createTransaction(TransactionRequest transactionRequest) {
        log.debug("请求TransactionService.createTransaction,参数:{}", transactionRequest);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            // 查询币种是否支持
            QueryWrapper<ChainCoin> chainCoinQueryWrapper = new QueryWrapper<>();
            chainCoinQueryWrapper.eq("chain_coin_key", transactionRequest.getChainCoinKey());
            chainCoinQueryWrapper.eq("status", 1);  // 已上架
            ChainCoin chainCoin = chainCoinMapper.selectOne(chainCoinQueryWrapper);
            if(chainCoin == null){
                throw new BusinessException("CHAIN_COIN_NOT_EXIST");
            }
            // 查询用户账户状态是否正常
            QueryWrapper<WalletAccount> walletAccountQueryWrapper = new QueryWrapper<>();
            walletAccountQueryWrapper.eq("address",transactionRequest.getToAddress());
            walletAccountQueryWrapper.eq("chain_coin_no", chainCoin.getSerialNo());
            walletAccountQueryWrapper.eq("tenant_id", transactionRequest.getTenantId());
            WalletAccount walletAccounts = walletAccountMapper.selectOne(walletAccountQueryWrapper);
            if(walletAccounts == null){}

            // 创建交易记录
            String serialNo = BsinSnowflake.getId(); // 雪花算法
            Transaction transaction = new Transaction();
            transaction.setSerialNo(serialNo);
            transaction.setOutSerialNo(transactionRequest.getOutSerialNo());
            transaction.setTransactionType(2);       // 交易类型 2、转出
            transaction.setComment(transactionRequest.getComment());
            transaction.setFromAddress(transactionRequest.getFromAddress());
            transaction.setTxAmount(new BigDecimal(transactionRequest.getTxAmount()));
            transaction.setToAddress(transactionRequest.getToAddress());
            transaction.setBizRoleType(user.getBizRoleType());
            transaction.setBizRoleNo(user.getBizRoleTypeNo());
            transaction.setTenantId(transactionRequest.getTenantId());
            transaction.setCreateTime(new Date());
            transaction.setCreateBy(user.getUserId());
            transactionMapper.insert(transaction);

            // TODO 调用风控方法

            // 风控审核通过，则执行转出动作
            if(true){
                transactionBiz.transferOut();
            }else {
                // 风控拦截交易，进入人工审核
                TransactionAudit transactionAudit = new TransactionAudit();
                transactionAudit.setAuditYpe(1);      // 1、交易转出审核
                transactionAudit.setAuditStatus(1);     // 1、待审核状态
                transactionAudit.setAuditLevel(1);      // 根据风控判断风险等级，暂默认为 1、低级风险
                transactionAudit.setTransactionNo(serialNo);
                transactionAudit.setCreateTime(new Date());
                transactionAudit.setCreateBy(user.getUserId());
                transactionAuditMapper.insert(transactionAudit);
            }
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    /**
     *
     * @param transactionDTO
     * fromAddress
     * toAddress
     * txAmount
     * chainCoinNo
     *
     */
    @Override
    @ShenyuDubboClient("/transferOut")
    @ApiDoc(desc = "transferOut")
    public void transferOut(TransactionDTO transactionDTO) throws Exception {
        log.debug("请求TransactionService.transferOut,参数:{}", transactionDTO);
        String fromAddress = transactionDTO.getFromAddress();
        String toAddress = transactionDTO.getToAddress();
        String contractAddress = transactionDTO.getContractAddress();
        BigDecimal txAmount = transactionDTO.getTxAmount();

        // 查询合约币种
        QueryWrapper<ChainCoin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.eq("contract_address", contractAddress);
//        queryWrapper.eq("tenant_id",tenant_id);
        ChainCoin chainCoin = chainCoinMapper.selectOne(queryWrapper);

        // 查询转出账户
        QueryWrapper<WalletAccount> walletAccountQueryWrapper = new QueryWrapper();
        walletAccountQueryWrapper.eq("address", fromAddress);
        WalletAccount walletAccount = walletAccountMapper.selectOne(walletAccountQueryWrapper);
        if(walletAccount == null){
            throw new BusinessException("非平台钱包账户地址");
        }
        log.info("开始提现交易，账户余额为：{}",walletAccount.getBalance());
        transferBiz.tokenTransfer(fromAddress,toAddress,contractAddress,txAmount.toBigInteger(), chainCoin.getCoinDecimal().toBigInteger());
        BigDecimal balance = walletAccount.getBalance().subtract(txAmount);
        log.info("提现交易结束，账户余额为：{}",balance);
        walletAccount.setBalance(balance);
        walletAccountMapper.updateById(walletAccount);
    }

    @Override
    @ShenyuDubboClient("/pageList")
    @ApiDoc(desc = "pageList")
    public Page<TransactionVO> pageList(TransactionDTO transactionDTO) {
        log.debug("请求TransactionService.pageList,参数:{}", transactionDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        Integer current = transactionDTO.getCurrent() == null?1:transactionDTO.getCurrent();
        Integer size = transactionDTO.getSize() == null?10:transactionDTO.getSize();
        transactionDTO.setTenantId(user.getTenantId());
        return transactionMapper.pageList(new Page<>(current, size),transactionDTO );
    }



}




