package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 商户链上货币;
 * @TableName merchant_chain_coin
 */

@Data
@TableName(value ="crm_customer_chain_coin")
public class CustomerChainCoin extends  BaseEntity implements Serializable {
    /**
     * 链币种序号
     */
    @NotBlank(message = "链币种序号不能为空" ,groups = {AddGroup.class})
    private String chainCoinNo;
    /**
     * 是否创建校色钱包账户标识;0、否 1、是
     */
    private Integer createRoleAccountFlag;

    /**
     * 是否创建用户钱包账户标识;0、否 1、是
     */
    private Integer createUserAccountFlag;
    /**
     * 业务角色类型;1、平台 2、商户 3、代理商 4、用户
     */
    @NotBlank(message = "业务角色类型不能为空！", groups = AddGroup.class)
    private Integer bizRoleType;
    /**
     * 业务角色序号
     */
    @NotBlank(message = "业务角色序号不能为空！", groups = AddGroup.class)
    private String bizRoleNo;
    /**
     * 租户
     */
    @NotBlank(message = "租户ID不能为空！", groups = AddGroup.class)
    private String tenantId;
}
