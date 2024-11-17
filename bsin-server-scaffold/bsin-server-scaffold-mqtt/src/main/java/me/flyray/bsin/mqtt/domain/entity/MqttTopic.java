package me.flyray.bsin.mqtt.domain.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 * @description IOT mqtt 话题表
 * @TableName crm_grade
 * */
@Data
@TableName(value = "iot_mqtt_topic")
public class MqttTopic implements Serializable {
  /** 序列号 */
  @TableId private String serialNo;

  /** 创建人 */
  private String createBy;

  /** 创建时间 */
  private Date createTime;

  /** 修改人 */
  private String updateBy;

  /** 修改时间 */
  private Date updateTime;

  /** 逻辑删除 0、未删除 1、已删除 */
  @TableLogic(value = "0", delval = "1")
  private Integer delFlag;

  /** 租户 */
  private String tenantId;

  /** 商户编号 */
  private String merchantNo;

  /** 话题名称 */
  private String name;

  /** 话题描述 */
  private String description;

  /** 话题回调函数 */
  private String callback;

  @TableField(exist = false)
  private static final long serialVersionUID = 1L;
}
