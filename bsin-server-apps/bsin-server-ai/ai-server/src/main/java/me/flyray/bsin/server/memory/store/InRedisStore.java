package me.flyray.bsin.server.memory.store;

import java.util.List;

import me.flyray.bsin.domain.entity.RedisChatMessage;
import me.flyray.bsin.redis.manager.BsinCacheProvider;
import me.flyray.bsin.server.utils.BsinMD5;
import me.flyray.bsin.server.utils.ObjectToOther;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author leonard
 * @description:存储历史聊天记录信息，用于前端获取历史记录 https://mp.weixin.qq.com/s/1OkHyF-VnW6tp4SmiZLXww key:
 *     getMD5Hex(sender+receiver) value: List<RedisChatMessage>
 * @createDate 2023/12/2023/12/14 /21/36
 */

// TODO: 序列化问题优化
@Component
public class InRedisStore {

  @Autowired private BsinCacheProvider bsinCacheProvider;

  public List<RedisChatMessage> getMessages(String prefix, String sender, String receiver) {
    String key = prefix;
    if (sender.compareTo(receiver) < 0) {
      key += BsinMD5.getMD5Hex(sender + receiver);
    } else {
      key += BsinMD5.getMD5Hex(receiver + sender);
    }
    // 获取list中的数据
    Object resultObj = bsinCacheProvider.listRange(key, 0, bsinCacheProvider.listLen(key));
    // 将Object安全的转为List
    return ObjectToOther.castList(resultObj, RedisChatMessage.class);
  }
  //  e98ab190f60d410a02b353a4fed38825
  public List<RedisChatMessage> getMessages(
      String prefix, String sender, String receiver, long start, long end) {
    String key = prefix;
    if (sender.compareTo(receiver) < 0) {
      key += BsinMD5.getMD5Hex(sender + receiver);
    } else {
      key += BsinMD5.getMD5Hex(receiver + sender);
    }
    // 获取list中的数据
    Object resultObj = bsinCacheProvider.listRange(key, start, end);
    // 将Object安全的转为List
    List<RedisChatMessage> resultList = ObjectToOther.castList(resultObj, RedisChatMessage.class);
    // 遍历获取到的结果
    if (resultList != null) {
      for (RedisChatMessage redisChatMessage : resultList) {
        System.out.println(redisChatMessage.getMessage());
      }
    }
    return null;
  }

  /**
   * 队列末尾添加记录
   *
   * @param redisChatMessage
   */
  public void rightPushMessage(String prefix, RedisChatMessage redisChatMessage) {
    String key = prefix;
    if (redisChatMessage.getSender().compareTo(redisChatMessage.getReceiver()) < 0) {
      key += BsinMD5.getMD5Hex(redisChatMessage.getSender() + redisChatMessage.getReceiver());
    } else {
      key += BsinMD5.getMD5Hex(redisChatMessage.getReceiver() + redisChatMessage.getSender());
    }
    bsinCacheProvider.listRightPush(key, redisChatMessage);
  }
  //  e98ab190f60d410a02b353a4fed38825
  /**
   * 队列末尾添加多条记录
   *
   * @param redisChatMessages
   */
  public void rightPushMessages(String prefix, List<RedisChatMessage> redisChatMessages) {
    String key = prefix;
    if (redisChatMessages.get(0).getSender().compareTo(redisChatMessages.get(0).getReceiver())
        < 0) {
      key +=
          BsinMD5.getMD5Hex(
              redisChatMessages.get(0).getSender() + redisChatMessages.get(0).getReceiver());
    } else {
      key +=
          BsinMD5.getMD5Hex(
              redisChatMessages.get(0).getReceiver() + redisChatMessages.get(0).getSender());
    }
    bsinCacheProvider.listRightPushAll(
        key, ObjectToOther.castList(redisChatMessages, Object.class));
  }

  /**
   * 队列头部添加多条记录
   *
   * @param redisChatMessage
   */
  public void leftPushMessage(String prefix, RedisChatMessage redisChatMessage) {
    String key = prefix;
    if (redisChatMessage.getSender().compareTo(redisChatMessage.getReceiver()) < 0) {
      key += BsinMD5.getMD5Hex(redisChatMessage.getSender() + redisChatMessage.getReceiver());
    } else {
      key += BsinMD5.getMD5Hex(redisChatMessage.getReceiver() + redisChatMessage.getSender());
    }
    bsinCacheProvider.listLeftPush(key, redisChatMessage);
  }

  /**
   * 队列头部添加多条记录
   *
   * @param redisChatMessages
   */
  public void leftPushMessages(String prefix, List<RedisChatMessage> redisChatMessages) {
    String key = prefix;
    if (redisChatMessages.get(0).getSender().compareTo(redisChatMessages.get(0).getReceiver())
        < 0) {
      key +=
          BsinMD5.getMD5Hex(
              redisChatMessages.get(0).getSender() + redisChatMessages.get(0).getReceiver());
    } else {
      key +=
          BsinMD5.getMD5Hex(
              redisChatMessages.get(0).getReceiver() + redisChatMessages.get(0).getSender());
    }
    bsinCacheProvider.listLeftPushAll(key, ObjectToOther.castList(redisChatMessages, Object.class));
  }

  /**
   * 删除队列头一条记录
   *
   * @param redisChatMessage
   */
  public void leftPopMessage(String prefix, RedisChatMessage redisChatMessage) {
    String key = prefix;
    if (redisChatMessage.getSender().compareTo(redisChatMessage.getReceiver()) < 0) {
      key += BsinMD5.getMD5Hex(redisChatMessage.getSender() + redisChatMessage.getReceiver());
    } else {
      key += BsinMD5.getMD5Hex(redisChatMessage.getReceiver() + redisChatMessage.getSender());
    }
    bsinCacheProvider.listPopLeftKey(key);
  }
  /**
   * 删除队列尾巴一条记录
   *
   * @param redisChatMessage
   */
  public void rightPopMessage(String prefix, RedisChatMessage redisChatMessage) {
    String key = prefix;
    if (redisChatMessage.getSender().compareTo(redisChatMessage.getReceiver()) < 0) {
      key += BsinMD5.getMD5Hex(redisChatMessage.getSender() + redisChatMessage.getReceiver());
    } else {
      key += BsinMD5.getMD5Hex(redisChatMessage.getReceiver() + redisChatMessage.getSender());
    }
    bsinCacheProvider.listPopRightKey(key);
  }

  public void updateMessage(String prefix, List<RedisChatMessage> messages) {}

  public void deleteMessages(String prefix, List<RedisChatMessage> messages) {}
}
