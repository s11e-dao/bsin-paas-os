package me.flyray.bsin.domain.request.transaction;

import lombok.Data;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;

@Data
public class TransactionRequest {

    // 商户业务唯一ID标识
    @NotBlank(message = "商户业务唯一ID标识不能为空")
    public String outSerialNo;
    // 交易备注
    public String comment;
    // 币种KEY
    @NotBlank(message = "币种KEY不能为空")
    public String chainCoinKey;
    // 交易金额
    @NotBlank(message = "交易金额不能为空")
    public String txAmount;
    // 交易发起方 账户ID
    @NotBlank(message = "交易发起方地址不能为空")
    public String fromAddress;
    // 交易接受方 链地址
    @NotBlank(message = "交易接受方地址不能为空")
    public String toAddress;
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
     * 租户ID
     */
    @NotBlank(message = "租户ID不能为空")
    public String tenantId;

}
