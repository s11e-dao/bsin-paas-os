package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.QuickReplyMessage;
import me.flyray.bsin.domain.entity.RedisChatMessage;

import java.util.List;
import java.util.Map;

public interface ChatService {


  /** getChatList */
  public List<RedisChatMessage> getChatHistoryList(Map<String, Object> requestMap);


  /** 知识库问答聊天：
   * 1、copilot会根据配置的知识库和提示词惊醒回答
   * 2、使用BufferWindowMemory的memory方式，上下文中携带最近k次的聊天信息
   * */
  public Map<String, Object> chatWithKnowledgeBase(Map<String, Object> requestMap);

  /** 和copilot聊天 */
  public Map<String, Object> chatWithCopilot(Map<String, Object> requestMap);

  /** 和首席品牌官聊天 */
  public Map<String, Object> chatWithChiefBrandOfficer(Map<String, Object> requestMap);

  /** 和数字分身聊天 */
  public Map<String, Object> chatWithDigitalAvatar(Map<String, Object> requestMap);

  /** 快捷回复：
   * 根据上下文聊天内容和知识库内容，生成快捷回复
   * */
  public List<QuickReplyMessage> getQuickReplies(Map<String, Object> requestMap);

  public Map<String, Object> chatWithAppAgent(Map<String, Object> requestMap);

}
