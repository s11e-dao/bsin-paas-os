package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import me.flyray.bsin.utils.BsinSnowflake;

import java.util.Date;

import static dev.langchain4j.internal.Utils.getOrDefault;

/**
 * @TableName ai_chat_memory
 */
@Data
@TableName(value = "ai_app_agent")
public class AppAgent {

  @TableId(value = "serial_no", type = IdType.ASSIGN_ID)
  private String serialNo;

  /** 租户id */
  private String tenantId;

  /** 商户号 */
  private String merchantNo;

  /** 客户编号 */
  private String customerNo;

  /** 提示词模版名称 */
  private String name;

  /** copilot类型：1、品牌官 2、数字分身 3、通用copilot */
  private String type;

  /**
   * ai编排json数据
   */
  private String appAgentModel;

  /** 智能体头像 */
  private String avatar;

  /** 状态： 0：禁用 1:启用 */
  private String status;

  /** 访问权限： 1-private 2-public */
  private String accessAuthority;

  /** 是否为默认商户或者用户copilot */
  private Boolean defaultFlag;

  /** 是否可编辑|删除 */
  private Boolean editable;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 创建人 */
  private String createBy;

  /** 模型描述 */
  private String description;

  /** 流式返回 */
  //  @TableField(exist = false)
  private boolean streaming;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date createTime;

  /** 更新时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date updateTime;

}
