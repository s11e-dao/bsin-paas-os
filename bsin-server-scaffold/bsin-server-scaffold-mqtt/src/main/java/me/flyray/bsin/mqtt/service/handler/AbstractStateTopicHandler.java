package me.flyray.bsin.mqtt.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import me.flyray.bsin.mqtt.domain.request.CommonTopicReceiver;

/**
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 * @description 此处的过滤链相关的东西，没有进行实现啦，具体可参考大疆的开源项目
 */
public abstract class AbstractStateTopicHandler {

  protected AbstractStateTopicHandler handler;

  //  @Autowired protected ObjectMapper mapper;

  protected AbstractStateTopicHandler(AbstractStateTopicHandler handler) {
    this.handler = handler;
  }

  /**
   * Passing dataNode data, using different processing methods depending on the data selection.
   *
   * @param dataNode
   * @param stateReceiver
   * @param sn
   * @return
   * @throws JsonProcessingException
   */
  public abstract CommonTopicReceiver handleState(
      Map<String, Object> dataNode, CommonTopicReceiver stateReceiver, String sn)
      throws JsonProcessingException;
}
