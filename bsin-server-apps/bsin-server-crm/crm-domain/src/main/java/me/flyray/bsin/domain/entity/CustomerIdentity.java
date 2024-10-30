package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import me.flyray.bsin.validate.AddGroup;

/**
 * @TableName crm_customer_identity
 */
@TableName(value = "crm_customer_identity")
@Data
public class CustomerIdentity implements Serializable {
  /** 客户号 */
  @TableId
  @NotBlank(
      message = "客户ID不能为空！",
      groups = {AddGroup.class})
  private String customerNo;

  /** 身份名称 */
  private String name;

  /** 身份用户名 */
  private String username;

  /**
   * 身份类型：商户|客户|代理商
   *
   * @see me.flyray.bsin.security.enums.BizRoleType
   */
  private String bizRoleType;

  /** 身份类型编号：商户号|代理商号 */
  private String bizRoleTypeNo;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 租户id */
  @NotBlank(
      message = "租户ID不能为空！",
      groups = {AddGroup.class})
  private String tenantId;

  /** 商户id */
  @NotBlank(
      message = "商户ID不能为空！",
      groups = {AddGroup.class})
  private String merchantNo;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 创建时间 */
  private Date createTime;

  /** 修改时间 */
  private Date updateTime;
}
