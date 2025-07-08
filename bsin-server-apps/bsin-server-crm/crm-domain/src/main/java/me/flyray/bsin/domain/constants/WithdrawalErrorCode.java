package me.flyray.bsin.domain.constants;

/**
 * @author bsin
 * @date 2024/12/19
 * @desc 提现业务异常常量
 */
public class WithdrawalErrorCode {

    /**
     * 提现记录ID不能为空
     */
    public static final String WITHDRAWAL_RECORD_ID_NOT_EXISTS = "WITHDRAWAL_RECORD_ID_NOT_EXISTS";

    /**
     * 审核状态不能为空
     */
    public static final String AUDIT_STATUS_NOT_EXISTS = "AUDIT_STATUS_NOT_EXISTS";

    /**
     * 提现记录不存在
     */
    public static final String WITHDRAWAL_RECORD_NOT_EXISTS = "WITHDRAWAL_RECORD_NOT_EXISTS";

    /**
     * 提现记录已审核，不能重复审核
     */
    public static final String WITHDRAWAL_RECORD_ALREADY_AUDITED = "WITHDRAWAL_RECORD_ALREADY_AUDITED";

    /**
     * 审核状态值无效
     */
    public static final String INVALID_AUDIT_STATUS = "INVALID_AUDIT_STATUS";

    /**
     * 提现金额不能为空
     */
    public static final String WITHDRAWAL_AMOUNT_NOT_EXISTS = "WITHDRAWAL_AMOUNT_NOT_EXISTS";

    /**
     * 提现金额必须大于0
     */
    public static final String WITHDRAWAL_AMOUNT_MUST_GREATER_THAN_ZERO = "WITHDRAWAL_AMOUNT_MUST_GREATER_THAN_ZERO";

    /**
     * 提现账号不能为空
     */
    public static final String WITHDRAWAL_ACCOUNT_NOT_EXISTS = "WITHDRAWAL_ACCOUNT_NOT_EXISTS";

    /**
     * 账户余额不足
     */
    public static final String ACCOUNT_BALANCE_INSUFFICIENT = "ACCOUNT_BALANCE_INSUFFICIENT";

    /**
     * 提现申请失败
     */
    public static final String WITHDRAWAL_APPLY_FAILED = "WITHDRAWAL_APPLY_FAILED";

    /**
     * 提现处理失败
     */
    public static final String WITHDRAWAL_PROCESS_FAILED = "WITHDRAWAL_PROCESS_FAILED";
} 