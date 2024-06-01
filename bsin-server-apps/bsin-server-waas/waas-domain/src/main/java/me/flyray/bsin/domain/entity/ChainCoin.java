package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 链上货币;
 * @TableName crm_coin
 */
@Data
@TableName(value ="waas_chain_coin")
public class ChainCoin extends BaseEntity implements Serializable {

    /**
     * 链上货币key
     */
    @NotBlank(message = "链上货币key不能为空！", groups = AddGroup.class)
    private String chainCoinKey;
    /**
     * 币种名称
     */
    @NotBlank(message = "币种名称不能为空！", groups = AddGroup.class)
    private String chainCoinName;
    /**
     * 币种简称
     */
    private String shortName;
    /**
     * 币种
     */
    @NotBlank(message = "币种不能为空！", groups = AddGroup.class)
    private String coin;

    /**
     * 链名
     */
    @NotBlank(message = "链名不能为空！", groups = AddGroup.class)
    private String chainName;

    /**
     * 智能合约地址
     */
    @NotBlank(message = "智能合约地址不能为空！", groups = AddGroup.class)
    private String contractAddress;

    /**
     * 币种精度
     */
    @NotBlank(message = "币种精度不能为空！", groups = AddGroup.class)
    private BigDecimal coinDecimal;

    /**
     * 单位
     */
    private String unit;

    /**
     * 状态;0、下架 1、上架
     */
    private Integer status;

    /**
     * 类型;1、默认 2、自定义
     */
    private Integer type;

    /**
     * 备注
     */
    private String remark;

    /**
     * 币种logo
     */
    private String logoUrl;
    /**
     * 业务角色类型;0、官方平台 1、平台 2、商户 3、代理商 4、用户
     */
    @NotBlank(message = "业务角色类型不能为空！", groups = AddGroup.class)
    private Integer bizRoleType;
    /**
     * 业务角色序号
     */
    @NotBlank(message = "业务角色序号不能为空！", groups = AddGroup.class)
    private String bizRoleTypeNo;

}
