package me.flyray.bsin.mqtt.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Unified Topic response format
 *
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonTopicResponse<T> {

  /**
   * The command is sent and the response is matched by the tid and bid fields in the message, and
   * the reply should keep the tid and bid the same.
   */
  private String tid;

  private String bid;

  private String method;

  private T data;

  private Long timestamp;
}
