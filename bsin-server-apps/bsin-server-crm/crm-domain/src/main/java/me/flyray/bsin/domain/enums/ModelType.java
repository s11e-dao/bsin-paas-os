package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 1、流程模型 2、表单模型 3、规则模型 4、推理模型
 */
public enum ModelType {

  FLOW_MODEL("1", "流程模型"),

  FORM_MODEL("2", "表单模型"),

  RULE_MODEL("3", "规则模型"),

  /** Inference */
  INFERENCE_MODEL("3", "推理模型");

  private String code;

  private String desc;

  ModelType(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  /** Json 枚举序列化 */
  @JsonCreator
  public static ModelType getInstanceById(String id) {
    if (id == null) {
      return null;
    }
    for (ModelType status : values()) {
      if (id.equals(status.getCode())) {
        return status;
      }
    }
    return null;
  }
}
