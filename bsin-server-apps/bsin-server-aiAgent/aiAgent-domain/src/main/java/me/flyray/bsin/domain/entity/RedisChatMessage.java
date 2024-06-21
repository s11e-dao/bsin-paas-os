package me.flyray.bsin.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import static dev.langchain4j.internal.Utils.getOrDefault;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/19 /00/35
 */
@Data
public class RedisChatMessage implements Serializable {

  private String sender;
  private String receiver;
  private String message;
  private String status; // 0-代发送 1-已发送 2-已读
  private String type; // text|image|file

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date timestamp;
  // 反序列化用
  public RedisChatMessage() {}

  public RedisChatMessage(
      String sender, String receiver, String message, String type, String status, Date timestamp) {
    Builder builder =
        newBuilder()
            .withSender(getOrDefault(sender, "sender"))
            .withReceiver(getOrDefault(receiver, "receiver"))
            .withMessage(getOrDefault(message, ""))
            .withType(getOrDefault(type, "text"))
            .withStatus(getOrDefault(status, "0"))
            .withTimestamp(getOrDefault(timestamp, new Date()));
    this.sender = builder.getSender();
    this.receiver = builder.getReceiver();
    this.message = builder.getMessage();
    this.status = builder.getStatus();
    this.type = builder.getType();
    this.timestamp = builder.getTimestamp();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Data
  public static class Builder {
    private String sender;
    private String receiver;
    private String message;
    private String status;
    private String type; // text|image|file

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timestamp;

    protected Builder() {}

    public Builder withSender(String sender) {
      this.sender = sender;
      return this;
    }

    public Builder withReceiver(String receiver) {
      this.receiver = receiver;
      return this;
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder withStatus(String status) {
      this.status = status;
      return this;
    }

    public Builder withType(String type) {
      this.type = type;
      return this;
    }

    public Builder withTimestamp(Date timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public RedisChatMessage build() {
      return new RedisChatMessage(sender, receiver, message, type, status, timestamp);
    }
  }
}
