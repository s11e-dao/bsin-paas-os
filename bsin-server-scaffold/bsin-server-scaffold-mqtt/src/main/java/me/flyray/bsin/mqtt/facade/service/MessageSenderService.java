package me.flyray.bsin.mqtt.facade.service;

import com.fasterxml.jackson.core.type.TypeReference;
import me.flyray.bsin.mqtt.domain.response.CommonTopicResponse;
import me.flyray.bsin.mqtt.domain.response.ServiceReply;

/**
 * @author leonard
 * @version 0.1
 * @date 2024/11/17
 */
public interface MessageSenderService {

  /**
   * Publish a message to a specific topic.
   *
   * @param topic target
   * @param response message
   */
  void publish(String topic, CommonTopicResponse response);

  /**
   * Use a specific qos to push messages to a specific topic.
   *
   * @param topic target
   * @param qos qos
   * @param response message
   */
  void publish(String topic, int qos, CommonTopicResponse response);

  /**
   * Send message and receive a response at the same time.
   *
   * @param clazz
   * @param topic
   * @param response notification of whether the start is successful.
   * @return
   */
  <T> T publishWithReply(Class<T> clazz, String topic, CommonTopicResponse response);

  /**
   * Send message and receive a response at the same time.
   *
   * @param clazz
   * @param topic
   * @param response
   * @param retryTime
   * @param <T>
   * @return
   */
  <T> T publishWithReply(Class<T> clazz, String topic, CommonTopicResponse response, int retryTime);

  /**
   * Used exclusively for sending messages for services.
   *
   * @param clazz The generic class for ServiceReply.
   * @param sn
   * @param method
   * @param data
   * @param bid
   * @param <T>
   * @return
   */
  <T> ServiceReply<T> publishServicesTopic(
      TypeReference<T> clazz, String sn, String method, Object data, String bid);

  /**
   * Used exclusively for sending messages for services, and does not set the received subtype.
   *
   * @param sn
   * @param method
   * @param data
   * @param bid
   * @return
   */
  ServiceReply publishServicesTopic(String sn, String method, Object data, String bid);

  /**
   * Used exclusively for sending messages for services.
   *
   * @param clazz The generic class for ServiceReply.
   * @param sn
   * @param method
   * @param data
   * @param <T>
   * @return
   */
  <T> ServiceReply<T> publishServicesTopic(
      TypeReference<T> clazz, String sn, String method, Object data);

  /**
   * Used exclusively for sending messages for services, and does not set the received subtype.
   *
   * @param sn
   * @param method
   * @param data
   * @return
   */
  ServiceReply publishServicesTopic(String sn, String method, Object data);
}
