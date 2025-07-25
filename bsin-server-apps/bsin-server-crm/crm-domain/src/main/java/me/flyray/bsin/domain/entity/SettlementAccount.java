package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import me.flyray.bsin.domain.enums.SettlementAccountType;
import me.flyray.bsin.entity.BaseEntity;
import me.flyray.bsin.validate.AddGroup;

import java.io.Serializable;

/**
 * 结算账户
 * @author : yang qianjin
 * @date : 2024-4-20
 */
@TableName("crm_settlement_account")
@Data
public class SettlementAccount extends BaseEntity implements Serializable {

    /** 账户号 */
    @NotBlank(message = "账户号不能为空！", groups = AddGroup.class)
    private String accountNum ;

    /** 账户名 */
    @NotBlank(message = "账户名称不能为空！", groups = AddGroup.class)
    private String accountName ;

    /**
     * @see SettlementAccountType
     */
    @NotBlank(message = "账户类型不能为空！", groups = AddGroup.class)
    private Integer accountType ;

    /** 银行名称 */
    private String bankName ;

    /** 开户行支行 */
    private String bankBranch ;

    /** 银行识别码;（电子账户也会制定相应的识别码） */
    private String swiftCode ;

    /** 备注 */
    private String remark ;

    /**
     * 业务角色类型;1、平台 2、商户 3、合伙人 4、用户
     */
    @NotBlank(message = "业务角色类型不能为空！", groups = AddGroup.class)
    private String bizRoleType;

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

    /**
     * 分类：1 支付结算  2体现结算
     */
    private String category;

    /**
     * 是否是默认结算账户
     */
    private String defaultFlag;

    /**
     * 删除标识
     */
    @TableLogic(
            value = "0",
            delval = "1"
    )
    private String delFlag;

    /**
     * 1 待审核 2 审核通过
     */
    private String status;

}
