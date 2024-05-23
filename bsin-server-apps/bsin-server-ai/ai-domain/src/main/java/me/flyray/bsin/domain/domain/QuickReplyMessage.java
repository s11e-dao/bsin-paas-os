package me.flyray.bsin.domain.domain;

import static dev.langchain4j.internal.Utils.getOrDefault;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/19 /00/35
 */
@Data
public class QuickReplyMessage implements Serializable {
  /*
   {
     icon: 'shopping-bag',
     name: '咨询订单问题（高亮）',
     code: 'orderSelector',
     isHighlight: true,
   },
   {
     icon: 'shopping-bag',
     name: '如何申请退款（高亮）',
     code: 'orderSelector',
     isHighlight: true,
   },
   {
     icon: 'message',
     name: '联系人工服务（高亮+新）',
     code: 'q1',
     isNew: true,
     isHighlight: true,
   },
  */
  private String icon;
  private String name; // 和前端 chatUI 统一字段  可见文本
  private String text; // 实际发送的文本
  private String type; // url|cmd
  private String cmd; //  { code: 'agent_join' }  object
  private boolean isHighlight;
  private boolean isNew;
  private String code; // text|image|orderSelector
  // 反序列化用
  public QuickReplyMessage() {}

  public QuickReplyMessage(
      String icon, String name, String code, boolean isHighlight, boolean isNew) {
    Builder builder =
        newBuilder()
            .withIcon(getOrDefault(icon, "message"))
            .withName(getOrDefault(name, ""))
            .withCode(getOrDefault(code, "text"))
            .withIsHighlight(getOrDefault(isHighlight, true))
            .withIsNew(getOrDefault(isNew, true));
    this.icon = builder.getIcon();
    this.name = builder.getName();
    this.isHighlight = builder.isHighlight();
    this.isNew = builder.isNew();
    this.code = builder.getCode();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Data
  public static class Builder {
    private String icon;
    private String name;
    private boolean isHighlight;
    private boolean isNew;
    private String code;

    protected Builder() {}

    public Builder withIcon(String icon) {
      this.icon = icon;
      return this;
    }

    public Builder withCode(String code) {
      this.code = code;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withIsHighlight(boolean isHighlight) {
      this.isHighlight = isHighlight;
      return this;
    }

    public Builder withIsNew(boolean isNew) {
      this.isNew = isNew;
      return this;
    }

    public QuickReplyMessage build() {
      return new QuickReplyMessage(icon, name, code, isHighlight, isNew);
    }
  }
}
