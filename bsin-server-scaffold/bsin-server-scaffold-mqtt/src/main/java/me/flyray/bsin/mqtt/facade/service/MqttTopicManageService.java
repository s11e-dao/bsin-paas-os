package me.flyray.bsin.mqtt.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.mqtt.domain.entity.MqttTopic;

import java.util.List;
import java.util.Map;

/**
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 */
public interface MqttTopicManageService {

  /** 添加 */
  public MqttTopic add(Map<String, Object> requestMap);

  /** 删除 */
  public void delete(Map<String, Object> requestMap);

  /** 编辑 */
  public MqttTopic edit(Map<String, Object> requestMap);

  /** 商户下所有 */
  public List<MqttTopic> getList(Map<String, Object> requestMap);

  /** 商户下分页所有 */
  public IPage<?> getPageList(Map<String, Object> requestMap);

  /** 查询等级、权益、条件详情 */
  public MqttTopic getDetail(Map<String, Object> requestMap);
}
