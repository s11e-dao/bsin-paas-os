package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.CrmTransaction;

import java.util.Map; /**
 * 基于crm账户的交易操作
 * @see me.flyray.bsin.enums.TransactionType
 */
public interface CrmTransactionService {

    /**
     * 支付
     * @param requestMap
     * @return
     */
    public CrmTransaction pay(Map<String, Object> requestMap);

    /**
     * 充值
     * @param requestMap
     * @return
     */
    public CrmTransaction recharge(Map<String, Object> requestMap);

    /**
     * 转账
     * @param requestMap
     * @return
     */
    public CrmTransaction transfer(Map<String, Object> requestMap);

    /**
     * 提现
     * @param requestMap
     * @return
     */
    public CrmTransaction withdraw(Map<String, Object> requestMap);

    /**
     * 提现申请
     * @param requestMap
     * @return
     */
    public CrmTransaction withdrawApply(Map<String, Object> requestMap);

    /**
     * 提现审核
     * @param requestMap
     * @return
     */
    public CrmTransaction withdrawAudit(Map<String, Object> requestMap);

    /**
     * 退款
     * @param requestMap
     * @return
     */
    public CrmTransaction refund(Map<String, Object> requestMap);

    /**
     * 结算
     * @param requestMap
     * @return
     */
    public CrmTransaction settlement(Map<String, Object> requestMap);

    /**
     * 收入
     * @param requestMap
     * @return
     */
    public CrmTransaction income(Map<String, Object> requestMap);

    /**
     * 赎回
     * @param requestMap
     * @return
     */
    public CrmTransaction redeem(Map<String, Object> requestMap);

    public IPage<?> getPageList(Map<String, Object> requestMap);

    public CrmTransaction getDetail(Map<String, Object> requestMap);

}