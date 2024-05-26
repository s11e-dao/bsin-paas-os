package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 地址黑白名单;
 * @TableName crm_address_black_white_list
 */
@Data
@TableName(value ="waas_black_white_list_address")
public class BlackWhiteListAddress extends BaseEntity implements Serializable {
    /**
     * 币种ID
     */
    @NotBlank(message = "链上货币ID不能为空！", groups = AddGroup.class)
    private String chainCoinNo;
    /**
     * 地址
     */
    @NotBlank(message = "链地址不能为空！", groups = AddGroup.class)
    private String address;
    /**
     * 状态;1、启用 2、禁用
     */
    private Integer status;
    /**
     * 类型;1、白名单 2、黑名单
     */
    @NotBlank(message = "类型不能为空！", groups = AddGroup.class)
    private Integer type;
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
    private String bizRoleNo;
    /**
     * 租户
     */
    @NotBlank(message = "租户ID不能为空！", groups = AddGroup.class)
    private String tenantId;
}
