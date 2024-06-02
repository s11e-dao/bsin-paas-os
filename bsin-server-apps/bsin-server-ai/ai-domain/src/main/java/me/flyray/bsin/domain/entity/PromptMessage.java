package me.flyray.bsin.domain.entity;

import static dev.langchain4j.internal.Utils.getOrDefault;

import lombok.Data;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * 参考：https://platform.openai.com/docs/api-reference/chat/create
 * messages=[
 *          {"role": "system", * "content": "You are a helpful assistant."},
 *          {"role": "user", "content": "Knock knock."},
 *          {"role": "assistant", "content": "Who's there?"},
 *          {"role": "user", "content": "Orange."}
 *          ]
 */
@Data
public class PromptMessage {

  private String role;

  /** 租户id */
  private String content;
  public PromptMessage(){}
  public PromptMessage(String role, String content) {
    PromptMessage.Builder builder =
        newBuilder()
            .withRole(getOrDefault(role, BsinSnowflake.getId()))
            .withContent(getOrDefault(content, "content"));
    this.role = builder.getRole();
    this.content = builder.getContent();
  }

  public static PromptMessage.Builder newBuilder() {
    return new PromptMessage.Builder();
  }

  @Data
  public static class Builder {
    private String role;
    private String content;

    protected Builder() {}

    public PromptMessage.Builder withRole(String role) {
      this.role = role;
      return this;
    }

    public PromptMessage.Builder withContent(String content) {
      this.content = content;
      return this;
    }

    public PromptMessage build() {
      return new PromptMessage(role, content);
    }
  }
}
