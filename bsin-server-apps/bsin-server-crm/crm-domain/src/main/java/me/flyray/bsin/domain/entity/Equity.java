package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 客户等级权益利益表
 * @TableName crm_equity
 */

@Data
@TableName(value ="crm_equity")
public class Equity implements Serializable {
    /**
     * 序列号
     */
    @TableId
    private String serialNo;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 逻辑删除 0、未删除 1、已删除
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private Integer delFlag;

    /**
     * 租户
     */
    private String tenantId;

    /**
     * 商户编码
     */
    private String merchantNo;

    /**
     * 权益名称
     */
    private String name;

    /**
     * 资产类型：1:数字徽章 2:PFP  3：账户-数字token(DP) 4:数字门票 5：Paas卡   6：账户-联合曲线(BC)  ) 7：满减 8：权限 9：会员等级
     */
    private String type;

    /**
     * 资产类型编号
     */
    private String typeNo;

    /**
     * 满减总金额
     */
    private BigDecimal totalAmount;

    /**
     * 满减金额、赠送金额、赠送数量 折扣值
     */
    private BigDecimal amount;

    /**
     * 叠加使用标识(1:是,2:否)
     */
    private String overFlag;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String equityRelationshipNo;



}