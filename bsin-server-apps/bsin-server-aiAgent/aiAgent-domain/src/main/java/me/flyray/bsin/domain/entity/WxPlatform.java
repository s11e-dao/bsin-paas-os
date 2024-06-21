package me.flyray.bsin.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @TableName ai_wx_platform
 */
@Data
@TableName(value = "ai_wx_platform")
// @Builder
public class WxPlatform {

  @TableId(value = "serial_no", type = IdType.ASSIGN_ID)
  private String serialNo;

  /** 租户ID */
  private String tenantId;

  /** 商户ID */
  private String merchantNo;

  /** 客户ID */
  private String customerNo;

  /** 名称 */
  private String name;

  /**
   * 微信平台类别：mp(公众号服务订阅号)、miniapp(小程序)、 cp(企业号|企业微信)、pay(微信支付)、open(微信开放平台) wechat(个人微信) menu(菜单模版)
   */
  private String type;

  /** copilot模型编号 */
  private String copilotNo;

  /** 菜单模版编号 */
  private String menuNo;

  /** 绑定的微信平台ID，菜单模版字段 */
  private String bindingWxPlatformNo;

  /** 微信公众号的appID：公众号通过此ID检索公众号参数 设置企业微信的corpId：企业微信通过此ID检索公众号参数 */
  private String appId;

  /** 企业号ID，数据库统一字段为appId，此字段废弃 */
  private String corpId;

  /** 设置企业微信应用的AgentId */
  private Integer agentId;

  /** 公众号/企业微信的secret */
  private String appSecret;

  /** 企业微信/微信公众号的EncodingAESKey */
  private String aesKey;

  /** 微信公众号/企业微信/小程序的token */
  private String token;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 是否启用模版： */
  private boolean templateEnable;

  /** 接口异常|频繁调用回复： */
  private String exceptionResp;

  /** 关注公众号自动回复的消息： */
  private String subscribeResponse;

  /** 预回复，当公众号收到消息后，先回复一条消息:为空则不预回复： */
  private String preResp;

  /** 是否支持群聊@ */
  private Boolean groupChat;

  /** 是否支持历史聊天记录总结 */
  private Boolean historyChatSummary;

  /** 是否为默认 */
  private Boolean defaultFlag;

  /** 描述 */
  private String description;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  /** 更新时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date updateTime;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 是否可编辑|删除 */
  private Boolean editable;

  @TableField(exist = false)
  String loginQrUrl;
  // 登录uuid
  private String uuid;
  // 个人微信号昵称
  private String nickname;
  // 个人微信登录状态
  private String loginStatus;
  // 个人微信号：唯一编号，与 wxPlatformNo 唯一绑定
  private String wxNo;
  // 请求间隔时间(单位s)：0则不限制
  private Integer requestIntervalLimit;

  // 封面图片
  private String coverImage;

  public WxPlatform() {}
}
