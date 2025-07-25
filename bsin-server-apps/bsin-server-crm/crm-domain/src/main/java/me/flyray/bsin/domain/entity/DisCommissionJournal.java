package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName crm_dis_brokerage_journal
 */
@Data
@TableName(value ="crm_dis_commission_journal")
public class DisCommissionJournal implements Serializable {
    /**
     * 
     */
    @TableId
    private String serialNo;

    /**
     * 租户号
     */
    private String tenantId;

    /**
     * 订单ID
     */
    private String orderNo;

    /**
     * 门店ID
     */
    private String merchantNo;

    /**
     * 商品ID
     */
    private String goodNo;

    /**
     * 政策ID
     */
    private String policyNo;

    /**
     * 规则ID

     */
    private String ruleNo;

    /**
     * 分佣金额

     */
    private BigDecimal disAmount;

    /**
     * 分佣时间
     */
    private Date createTime;

    /**
     * 触发分佣的事件编码, 收款: PAY_SUCCESS, 发货: TRANSFER
     */
    private String triggerEventCode;

    /**
     * 不进行分佣的资金: 运费: carriage, 自定义: custom
     */
    private String excludeFeeType;

    /**
     * 自定义扣除费用比例
     */
    private BigDecimal excludeCustomPer;

    /**
     * 几级分佣  一级分佣:1, 二级分佣:2
     */
    private Integer disLevel;

    /**
     * 分销员ID\n
     */
    private String sysAgentNo;

    /**
     * 佣金是否打入分销员账户打入:1/未打入:0
     */
    private Integer status;

    /**
     * 订单金额
     */
    private Integer payAmount;

    /**
     * 分佣比例
     */
    private BigDecimal rate;

    /**
     * 分佣条件达成后隔几天结算佣金
     */
    private Integer pointDays;

    private String goodSkuNo;

    private BigDecimal goodsSkuAmount;

    private String goodsCategoryNo;

    private Integer isPreview;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}