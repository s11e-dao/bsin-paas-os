package me.flyray.bsin.domain.entity.customer;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 商户配置表;
 * @author : yang qianjin
 * @date : 2024-4-20
 */
@Data
@TableName("crm_customer_config")
public class CustomerConfig extends BaseEntity implements Serializable {
    /**
     * logo
     */
    private String logoUrl ;
    /**
     * 联系人姓名
     */
    private String contactName ;
    /**
     * 联系人手机号
     */
    private String contactPhone ;
    /**
     * 是否开启回调;0、否 1、是
     */
    private Integer enableCallback;
    /**
     * 回调地址
     */
    private String loopbackAddress ;
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
