package me.flyray.bsin.server.websocket;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.response.AiChatDTO;
import me.flyray.bsin.server.biz.ChatBiz;
import me.flyray.bsin.server.biz.WebSocketAiChatAsyncBiz;

/**
 * @author leonard
 * @param fromNo 发起客户
 * @param toNO 目标AI
 * @param chatType 聊天类型
 * @description
 * @createDate 2023/12/2023/12/28 /14/01
 */
@ServerEndpoint(value = "/websocket/{fromNo}/{toNo}/{chatType}")
@Component
@Slf4j
@Data
// @SofaService(
//    uniqueId = "${com.alipay.sofa.rpc.version}",
//    bindings = {
//      @SofaServiceBinding(bindingType = RpcConstants.PROTOCOL_TYPE_BOLT),
//      @SofaServiceBinding(bindingType = "rest")
//    })
// @Service("webSocketServer")
public class WebSocketServer {

  private static ExecutorService webSocketAiChatAsyncExecutorService =
      Executors.newFixedThreadPool(10);

  //  private static ChatBiz chatBiz = new ChatBiz();

  private static ApplicationContext applicationContext;

  // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
  private static AtomicInteger onlineCount = new AtomicInteger(0);
  // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
  private static CopyOnWriteArraySet<WebSocketServer> wsClientMap = new CopyOnWriteArraySet<>();
  // session集合,存放对应的session
  private static ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();

  // 与某个客户端的连接会话，需要通过它来给客户端发送数据
  private Session session;
  private String idWork;
  private Long status;
  /** 发起人用户id */
  private String fromNo;
  /** 目标对象id */
  private String toNo;
  /** 目标对象id */
  private String chatType;
  /** question */
  private String question;

  public static void setApplicationContext(ApplicationContext context) {
    applicationContext = context;
  }

  /**
   * 连接建立成功调用的方法
   *
   * @param fromNo 发起用户
   * @param toNo 目标用户
   * @param chatType 聊天类型： 1-BrandCopilot 2-个人数字分身 3-知识库
   * @param session 当前会话session
   */
  @OnOpen
  public void onOpen(
      @PathParam("fromNo") String fromNo,
      @PathParam("toNo") String toNo,
      @PathParam("chatType") String chatType,
      Session session) {
    this.session = session;
    this.status = 1L;
    this.fromNo = fromNo;
    this.toNo = toNo;
    this.chatType = chatType;
    try {
      Session historySession = sessionPool.get(fromNo);
      // historySession不为空,说明已经有人登陆账号,应该删除登陆的WebSocket对象
      if (historySession != null) {
        sessionPool.remove(historySession);
        historySession.close();
      }
    } catch (IOException e) {
      log.error("重复登录异常,错误信息：" + e.getMessage(), e);
    }
    sessionPool.put(fromNo, session);
    wsClientMap.add(this);
    addOnlineCount();
    log.info(
        session.getId()
            + "有新链接加入，chatType："
            + chatType
            + " fromNo: "
            + fromNo
            + " toNo: "
            + toNo
            + "，当前链接数为："
            + wsClientMap.size());
  }

  /** 连接关闭 */
  @OnClose
  public void onClose() {
    wsClientMap.remove(this);
    subOnlineCount();
    log.info("有一链接关闭,fromNo: " + fromNo + "，当前链接数为：" + wsClientMap.size());
  }

  /**
   * 收到客户端消息
   *
   * @param message 客户端发送过来的消息
   * @param session 当前会话session
   * @throws IOException
   */
  @OnMessage
  public void onMessage(
      @PathParam("fromNo") String fromNo,
      @PathParam("toNo") String toNo,
      @PathParam("chatType") String chatType,
      String message,
      Session session)
      throws IOException {
    log.info("收到客户端消息:" + message);
    AiChatDTO aiChatDTOReq =
        AiChatDTO.newBuilder()
            .withType(chatType)
            .withQuestion(message)
            .withFromNo(fromNo)
            .withToNo(toNo)
            .build();
    WebSocketServer webSocketServer = this.getWebSocket(fromNo, toNo, chatType);
    ChatBiz chatBiz = applicationContext.getBean(ChatBiz.class);
    webSocketAiChatAsyncExecutorService.submit(
        new WebSocketAiChatAsyncBiz(aiChatDTOReq, webSocketServer, chatBiz));
  }

  /** 发生错误 */
  @OnError
  public void onError(Session session, Throwable error) {
    log.info("wsClientMap发生错误!");
    error.printStackTrace();
  }

  /** 根据当前socketPool中自动选择聊天机器人，消息请求和推送 */
  public WebSocketServer matchChat() {
    if (wsClientMap.size() <= 1) {
      return null;
    }
    String id = IdWorker.getId() + "";
    for (WebSocketServer pkSocket : wsClientMap) {
      if (pkSocket.fromNo.equals(fromNo)) {
        if (pkSocket.toNo != null) {
          return pkSocket;
        }
      }
    }
    List<WebSocketServer> userPk =
        wsClientMap.stream()
            .filter(item -> item.status == 1L && !item.fromNo.equals(fromNo))
            .collect(Collectors.toList());
    if (CollUtil.isNotEmpty(userPk)) {
      for (WebSocketServer item : wsClientMap) {
        if (item.fromNo.equals(userPk.get(0).fromNo)) {
          item.status = 2L;
          item.toNo = fromNo;
          item.idWork = id;
        }
        if (item.fromNo.equals(fromNo)) {
          item.idWork = id;
          item.status = 2L;
          item.toNo = userPk.get(0).fromNo;
        }
      }
      return userPk.get(0);
    } else {
      return null;
    }
  }

  /**
   * 获取当前聊天用户数组
   *
   * @return 用户id数组
   */
  public List<String> getUseIds() {
    return wsClientMap.stream()
        .filter(item -> 1L == item.getStatus())
        .map(WebSocketServer::getFromNo)
        .collect(Collectors.toList());
  }
  /**
   * 获取当前聊天用户数字
   *
   * @return 用户id数组
   */
  public WebSocketServer getWebSocket(String fromNo, String toNo, String chatType) {
    if (wsClientMap.size() < 1) {
      return null;
    }
    for (WebSocketServer wkSocket : wsClientMap) {
      if (wkSocket.fromNo.equals(fromNo)) {
        if (wkSocket.toNo.equals(toNo)) {
          return wkSocket;
        }
      }
    }
    return null;
  }

  /**
   * 给所有客户端群发消息
   *
   * @param message 消息内容
   * @throws IOException
   */
  public void sendMsgToAll(String message) throws IOException {
    for (WebSocketServer item : wsClientMap) {
      item.session.getBasicRemote().sendText(message);
    }
    log.info("成功群送一条消息:" + wsClientMap.size());
  }

  public void sendMessage(String message) throws IOException {
    try {
      this.session.getBasicRemote().sendText(message);
      log.info("成功发送一条消息:" + message);
    } catch (IOException e) {
      log.error("推送消息到指定用户发生错误：" + e.getMessage(), e);
    }
  }

  public void sendMessageToUser(String message, String fromNo) throws IOException {
    for (WebSocketServer item : wsClientMap) {
      if (item.fromNo.equals(fromNo)) {
        item.session.getBasicRemote().sendText(message);
      }
    }
    log.info("成功发送一条消息给用户:" + message + ",用户id为:" + fromNo);
  }

  public static synchronized int getOnlineCount() {
    return WebSocketServer.onlineCount.get();
  }

  public static synchronized void addOnlineCount() {
    WebSocketServer.onlineCount.addAndGet(1);
  }

  public static synchronized void subOnlineCount() {
    WebSocketServer.onlineCount.getAndDecrement();
  }

  public static CopyOnWriteArraySet<WebSocketServer> getWsClientMap() {
    return wsClientMap;
  }
}
