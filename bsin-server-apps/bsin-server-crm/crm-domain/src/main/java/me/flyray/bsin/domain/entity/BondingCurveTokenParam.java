package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 
 * @TableName crm_bonding_curve_token_param
 */

@Data
@TableName(value ="crm_bonding_curve_token_param")
public class BondingCurveTokenParam implements Serializable {
    /**
     * bonding curve编号
     */
    @TableId
    private String serialNo;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 品牌商户
     */
    private String merchantNo;

    /**
     * curve name(代币名称)
     */
    private String name;

    /**
     * curve symbol(代币符号)   CCY
     */
    private String symbol;

    /**
     * 参数版本号
     */
    private String version;

    /**
     * curve name(代币小数点位数)
     */
    private Integer decimals;

    /**
     * 积分供应量上限
     */
    private BigDecimal cap;

    /**
     * 积分流通量：mint增肌 burn减少
     */
    private BigDecimal circulation;

    /**
     * 积分初始定价
     */
    private BigDecimal initialPrice;

    /**
     * 绑定的数字积分资产编号：1：1释放铸造
     */
    private String digitalPointNo;


    /**
     * 积分最终价格
     */
    private BigDecimal finalPrice;

    /**
     * 曲线的拉伸变换，越大代表压缩的最厉害，中间（x坐标cap/2点周围）加速度越大；越小越接近匀加速。理想的S曲线 flexible的取值为4-6。
     */
    private BigDecimal flexible;

    /**
     * 积分曲线类型：0、bancor curve  1、sigmoid curve 
     */
    private String type;

    /**
     * 曲线状态 0、正常 1、冻结
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}