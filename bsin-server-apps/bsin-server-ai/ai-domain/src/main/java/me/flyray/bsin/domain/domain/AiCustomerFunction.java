package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import me.flyray.bsin.validate.AddGroup;

/**
 * @TableName ai_customer_function
 */
@TableName(value = "ai_customer_function")
@Data
public class AiCustomerFunction implements Serializable {
  /** ID */
  @TableId private String serialNo;

  /** 客户号 */
  private String customerNo;

  /** 租户id */
  private String tenantId;
  /** 商户id */
  private String merchantNo;

  /** 服务功能名称 */
  private String name;

  /** 类型 0、租户上架的功能(可供客户订阅的功能模版) 1、客户订阅服务 2、客户订阅功能 3、系统自待基础服务 */
  private String type;

  /** 是否可编辑|删除 */
  private Boolean editable;

  /** 可创建智能体数量 */
  private Integer copilotNum;

  /** 可创建知识库数量 */
  private Integer knowledgeBaseNum;

  /** 每个知识库文件数量 */
  private Integer knowledgeBaseFileNum;

  /** 公众号数量 */
  private Integer mpNum;

  /** 小程序数量 */
  private Integer miniappNum;

  /** 菜单模版数量 */
  private Integer menuTemplateNum;

  /** 企业微信数量 */
  private Integer cpNum;

  /** 个人微信数量 */
  private Integer wechatNum;

  /** 是否支持群聊@ */
  private Boolean groupChat;

  /** 是否支持历史聊天记录总结 */
  private Boolean historyChatSummary;

  /** 敏感词数量 */
  private Integer sensitiveWordsNum;

  /** 定价 */
  private BigDecimal price;

  /** 支付凭证 */
  private String payReceipt;

  /** token余额 */
  private Integer tokenBalance;

  /** 花费的token */
  private Integer tokenUsed;

  /** 服务时间(day)：正对租户上架的功能(1天|1周|1个月|1年) */
  private Integer serviceDuration;

  /** 开始时间 */
  private Date startTime;

  /** 结束时间 */
  private Date endTime;

  /** 创建时间 */
  private Date createTime;

  /** 修改时间 */
  private Date updateTime;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 应用|功能状态：0、待缴费 1、待审核 2、正常 3、欠费停止 4、冻结 */
  private String status;

  /** 描述 */
  private String description;

  /** 邀请码 */
  private String inviteCode;

  @TableField(exist = false)
  private Date expirationTime;
}
