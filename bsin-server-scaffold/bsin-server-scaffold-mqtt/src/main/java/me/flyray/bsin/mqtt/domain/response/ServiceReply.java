package me.flyray.bsin.mqtt.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceReply<T> {

  private Integer result;

  private T info;

  private T output;
}
