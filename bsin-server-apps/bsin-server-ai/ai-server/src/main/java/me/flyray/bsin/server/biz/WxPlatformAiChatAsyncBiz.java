package me.flyray.bsin.server.biz;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.flyray.bsin.domain.entity.WxPlatform;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.AiChatDTO;

/**
 * @author leonard
 * @description
 * @createDate 22023/01/20 00:40
 */
@Slf4j
public class WxPlatformAiChatAsyncBiz implements Runnable {
  private final AiChatDTO aiChatDTOReq;
  private final WxPlatform wxPlatform;
  private final WxMpXmlMessage wxMessage;
  private final WxMpService wxMpService;
  private final ChatBiz chatBiz;

  public WxPlatformAiChatAsyncBiz(
      AiChatDTO aiChatDTOReq,
      ChatBiz chatBiz,
      WxPlatform wxPlatform,
      WxMpXmlMessage wxMessage,
      WxMpService wxMpService) {
    this.aiChatDTOReq = aiChatDTOReq;
    this.chatBiz = chatBiz;
    this.wxPlatform = wxPlatform;
    this.wxMessage = wxMessage;
    this.wxMpService = wxMpService;
  }

  @Override
  public void run() {
    try {
      this.chatBiz.chat(this.aiChatDTOReq, wxPlatform, wxMessage, wxMpService);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
  }
}
