package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 钱包;
 * @TableName crm_wallet
 */
@Data
@TableName(value ="waas_wallet")
public class Wallet extends BaseEntity implements Serializable {
    /**
     * 账户名称
     */
    @NotBlank(message = "钱包名称不能为空！", groups = AddGroup.class)
    private String walletName;
    /**
     * 类型;1、默认钱包 2、自定义钱包
     */
    @NotBlank(message = "钱包类型不能为空！", groups = AddGroup.class)
    private Integer type;

    /**
     * 状态;1、正常 2、冻结
     */
    private Integer status;
    /**
     * 分类 1、MVP 2、多签
     */
    @NotBlank(message = "钱包分类不能为空！", groups = AddGroup.class)
    private Integer category;
    /**
     * 环境 EVM
     */
    @NotBlank(message = "钱包环境不能为空！", groups = AddGroup.class)
    private String env;
    /**
     * 账户标签;NONE 无  DEPOSIT 寄存(用户资金归集)  GATHER 归集
     */
    @NotBlank(message = "钱包标签不能为空！", groups = AddGroup.class)
    private String walletTag;
    /**
     * 钱包余额
     */
    private String balance;
    /**
     * 外部用户标识
     */
    private String outUserId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 业务角色类型;1、平台 2、商户 3、代理商 4、用户
     */
    @NotBlank(message = "业务角色类型不能为空！", groups = AddGroup.class)
    private Integer bizRoleType;
    /**
     * 业务角色序号
     */
    @NotBlank(message = "业务角色序号不能为空！", groups = AddGroup.class)
    private String bizRoleTypeNo;
    /**
     * 租户
     */
    @NotBlank(message = "租户ID不能为空！", groups = AddGroup.class)
    private String tenantId;
}
