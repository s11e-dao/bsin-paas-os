package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * @TableName ai_wx_platform_user
 */
@Data
@TableName("ai_wx_platform_user")
public class WxPlatformUser {

  /** */
  private String serialNo;

  /** 微信openid */
  private String openId;

  /** 微信应用ID */
  private String appId;

  /** 租户ID */
  private String tenantId;

  /** 会员名称 */
  private String name;

  /** 性别 */
  private String sex;

  /** 年龄 */
  private Integer age;

  /** 城市 */
  private String city;

  /** 身高 */
  @TableField(exist = false)
  private Double height;

  /** 生日 */
  @TableField(exist = false)
  private String birthday;

  /** 学历 */
  @TableField(exist = false)
  private String education;

  /** 婚姻状况 */
  @TableField(exist = false)
  private String maritalStatus;

  /** 照片 */
  @TableField(exist = false)
  private String avatar;

  /** 收入 */
  private String income;

  /** 星座 */
  @TableField(exist = false)
  private String constellation;

  /** 性格 */
  @TableField(exist = false)
  private String character;

  /** 兴趣爱好 */
  @TableField(exist = false)
  private String interest;

  /** 爱情观 */
  @TableField(exist = false)
  private String loveView;

  /** 伴侣要求 */
  @TableField(exist = false)
  private String mateRequirements;

  /** 电话 */
  private String phone;

  /** 微信号 */
  private String wxNo;

  /** token余额 */
  private Integer tokenBalance;

  /** 使用的token */
  private Integer tokenUsed;

  /** 创建时间 */
  private Date createTime;

  /** 状态 */
  private String status;
}
