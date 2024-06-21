package me.flyray.bsin.server.biz;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.AiChatDTO;
import me.flyray.bsin.server.websocket.WebSocketServer;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/28 /14/08
 */
@Slf4j
public class WebSocketAiChatAsyncBiz implements Runnable {
  private AiChatDTO aiChatDTORes;
  private final AiChatDTO aiChatDTOReq;
  private final WebSocketServer webSocketServer;

  private final ChatBiz chatBiz;

  public WebSocketAiChatAsyncBiz(
      AiChatDTO aiChatDTOReq, WebSocketServer webSocketServer, ChatBiz chatBiz) {
    this.aiChatDTOReq = aiChatDTOReq;
    this.webSocketServer = webSocketServer;
    this.chatBiz = chatBiz;
  }

  @Override
  public void run() {
    try {
      this.chatBiz.chat(this.aiChatDTOReq, this.webSocketServer);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
  }
}
