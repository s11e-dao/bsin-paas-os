package me.flyray.bsin.domain.entity.customer;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 商户api秘钥
 * @author : yang qianjin
 * @date : 2024-4-20
 */
@Data
@TableName("crm_customer_api_key")
public class CustomerApiKey extends BaseEntity implements Serializable {
    /** api秘钥名称 */
    @NotBlank(message = "秘钥名称不能为空",groups = AddGroup.class)
    private String name ;
    /** 公钥 */
    @NotBlank(message = "公钥不能为空",groups = AddGroup.class)
    private String publicKey ;
    /**
     *  状态;1.启用 2.禁用
     **/
    private Integer status ;
    /**
     * API 类型;0.不限 1.查询 2.充值 3.提现
     **/
    private Integer apiType ;
    /**
     * ip白名单
     **/
    private String ipWhitList ;
    /**
     * 备注
     **/
    private String remark ;
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
