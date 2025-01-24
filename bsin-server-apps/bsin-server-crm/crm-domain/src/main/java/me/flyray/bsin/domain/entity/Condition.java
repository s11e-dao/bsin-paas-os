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
 * 客户等级达成条件
 * @TableName crm_condition
 */

@Data
@TableName(value ="crm_condition")
public class Condition implements Serializable {
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
     * 条件值
     */
    private Integer value;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商户编码
     */
    private String merchantNo;

    /**
     * 条件名称
     */
    private String name;

    /**
     * 条件类型编号
     */
    private String typeNo;


    /**
     * 条件类型协议(ERC20 ERC721 ERC1155 ERC3525)
     */
    private String typeProtocol;


    /**
     * 条件资产tokenId--ERC1155
     */
    private String typeTokenId;

    /**
     * 条件类型：1:数字资产 2:PFP  3：账户-数字token(DP) 4:数字门票 5：Paas卡   6：账户-联合曲线(BC)  ) 7：满减 8：权限 9：会员等级
     */
    private String type;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String  conditionRelationshipNo;

}