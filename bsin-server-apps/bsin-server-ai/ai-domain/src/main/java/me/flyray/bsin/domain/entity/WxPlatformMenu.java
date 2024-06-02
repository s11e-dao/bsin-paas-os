package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

/**
 * @TableName ai_wx_platform_menu
 */
@Data
@TableName(value = "ai_wx_platform_menu")
// @Builder
public class WxPlatformMenu {

  @TableId(value = "serial_no", type = IdType.ASSIGN_ID)
  private String serialNo;

  /** 菜单名称 */
  private String name;

  /** 类型：click|view|event|miniprogram 菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型 */
  private String type;

  /** 菜单等级 */
  private String level;

  /** 上级ID */
  private String parentId;

  /** 网页、链接、用户点击菜单可打开链接，，不超过1024字节。 type为miniprogram时，不支持小程序的老版本客户端将打开本url。 view、miniprogram类型必须 */
  private String url;

  /** 排序 */
  private String sort;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 绑定的微信菜单模版 */
  private String wxPlatformMenuTemplateNo;

  /** 菜单key值，用于消息推送： 不超过128字节 click等点击类型必须 */
  private String menuKey;
  /** miniprogram类型必须 小程序的appid（仅认证公众号可配置） */
  private String appid;

  /** miniprogram类型必须 小程序的页面路径 */
  private String pagePath;

  /** article_id类型和article_view_limited类型必须 发布后获得的合法 article_id */
  private String articleId;

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

  public WxPlatformMenu() {}
}
