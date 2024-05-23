package me.flyray.bsin.facade.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxPlatformMenuTemplateVo implements Serializable {

  private String serialNo;

  /** 租户ID */
  private String tenantId;

  /** 商户ID */
  private String merchantNo;

  /** 客户ID */
  private String customerNo;

  /** 名称 */
  private String name;

  /** 菜单等级 */
  private String level;

  /** 微信平台类别：mp(公众号服务订阅号)、miniapp(小程序)、 cp(企业号|企业微信)、pay(微信支付)、open(微信开放平台) wechat(个人微信) */
  private String type;

  /** 绑定的微信平台ID，菜单模版字段 */
  private String bindingWxPlatformNo;

  /** 状态： 0：禁用 1:启用 */
  private String status;

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

  private Integer delFlag;

  /** 是否可编辑|删除 */
  private Boolean editable;

  // 封面图片
  private String coverImage;

  private List<WxPlatformMenuTreeVo> children;
}
