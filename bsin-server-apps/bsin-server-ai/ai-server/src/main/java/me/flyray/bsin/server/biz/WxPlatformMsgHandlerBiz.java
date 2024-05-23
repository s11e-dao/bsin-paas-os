package me.flyray.bsin.server.biz;

import lombok.AllArgsConstructor;
import me.flyray.bsin.domain.domain.*;
import me.flyray.bsin.facade.response.AiChatDTO;
import me.flyray.bsin.facade.response.QuestionPreProcessDTO;
import me.flyray.bsin.infrastructure.mapper.CopilotMapper;
import me.flyray.bsin.infrastructure.mapper.WxPlatformMapper;
import me.flyray.bsin.infrastructure.mapper.WxPlatformUserMapper;
import me.flyray.bsin.server.document.split.SplitterProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import me.flyray.bsin.thirdauth.wx.builder.TextBuilder;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.flyray.bsin.cache.BsinCacheProvider;
import me.flyray.bsin.thirdauth.wx.handler.AbstractHandler;
import me.flyray.bsin.thirdauth.wx.utils.BsinWxMpServiceUtil;
import me.flyray.bsin.thirdauth.wx.utils.WxMpProperties;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
public class WxPlatformMsgHandlerBiz extends AbstractHandler {

  private static ExecutorService wxPlatformAiChatAsyncExecutorService =
      Executors.newFixedThreadPool(10);
  private static ApplicationContext applicationContext;
  @Autowired private WxPlatformMapper wxPlatformMapper;
  @Autowired private WxPlatformUserMapper wxPlatformUserMapper;
  @Autowired private CopilotMapper copilotMapper;
  private static long lastTime = 0;
  private static long currentTime = 0;
  private final Environment environment;
  @Autowired BsinWxMpServiceUtil bsinWxMpServiceUtil;
  @Autowired private BsinCacheProvider bsinCacheProvider;

  @Value("${bsin.ai.aesKey}")
  private String aesKey;

  @Value("${wx.mp.config-storage.redis.host}")
  private String wxRedisHost;

  @Value("${wx.mp.config-storage.redis.port}")
  private Integer wxRedisPort;

  @Value("${wx.mp.config-storage.redis.password}")
  private String wxRedisPassword;
  private WxMpProperties.RedisConfig redisConfig;

  public WxPlatformMsgHandlerBiz(Environment environment) {
    this.environment = environment;
  }

  public static void setApplicationContext(ApplicationContext context) {
    applicationContext = context;
  }

  @Override
  public WxMpXmlOutMessage handle(
      WxMpXmlMessage wxMessage,
      Map<String, Object> context,
      WxMpService wxMpService,
      WxSessionManager sessionManager) {

    // 根据用户的openid从缓存里面获取对应的appId
    String appId = bsinCacheProvider.get(wxMessage.getFromUser());
    String openId = wxMessage.getFromUser();
    WxPlatform wxPlatform = wxPlatformMapper.selectByAppId(appId);
    WxMpProperties.MpConfig config = new WxMpProperties.MpConfig();
    config.setAesKey(wxPlatform.getAesKey());
    config.setAppId(appId);
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
    String wxAppSecret = aes.decryptStr(wxPlatform.getAppSecret(), CharsetUtil.CHARSET_UTF_8);
    config.setSecret(wxAppSecret);
    config.setToken(wxPlatform.getToken());
    System.out.println("appId:" + appId);
    System.out.println("ori wxAppSecret:" + wxPlatform.getAppSecret());
    System.out.println("desc wxAppSecret:" + wxAppSecret);
    System.out.println("token:" + wxPlatform.getToken());
    System.out.println("AesKey:" + wxPlatform.getAesKey());

    WxPlatformUser wxPlatformUser = wxPlatformUserMapper.selectByOpenId(openId);
    if (wxPlatformUser == null) {
      wxPlatformUser = new WxPlatformUser();
      wxPlatformUser.setTenantId(wxPlatform.getTenantId());
      wxPlatformUser.setOpenId(openId);
      wxPlatformUser.setAppId(appId);
      wxPlatformUser.setSerialNo(BsinSnowflake.getId());
      wxPlatformUser.setTokenBalance(100000);
      wxPlatformUser.setTokenUsed(0);
      wxPlatformUserMapper.insert(wxPlatformUser);
    }

    if (redisConfig == null) {
      redisConfig = new WxMpProperties.RedisConfig();
      redisConfig.setHost(wxRedisHost);
      redisConfig.setPort(wxRedisPort);
      redisConfig.setPassword(wxRedisPassword);
    }
    WxMpService weixinService = bsinWxMpServiceUtil.getWxMpService(config,redisConfig);

    // 获取公众号配置的对应智能体
    CopilotInfo copilotInfo = copilotMapper.selectById(wxPlatform.getCopilotNo());

    // token计费
    //    if (!bsinCacheProvider.exist(appId + "TokenBalance")) {
    //      bsinCacheProvider.set(
    //          appId + "TokenBalance", wxPlatformUser.getTokenBalance().toString());
    //    }
    //    if (!bsinCacheProvider.exist(appId + "TokenUsed")) {
    //      bsinCacheProvider.set(appId + "TokenUsed", wxPlatformUser.getTokenUsed().toString());
    //    }
    //    long userTokenBalance = Long.parseLong(bsinCacheProvider.get(appId + "TokenBalance"));
    //    long userTokenUsed = Long.parseLong(bsinCacheProvider.get(appId + "TokenUsed"));

    // 非超时请求才做回复：limit时间
    currentTime = System.currentTimeMillis();
    String preResponse = wxPlatform.getPreResp();
    String exceptionResp = wxPlatform.getExceptionResp();
    QuestionPreProcessDTO questionPreProcessDTO =
        QuestionPreProcessBiz.preProcess(wxMessage.getContent());
    if (questionPreProcessDTO.isSensitiveWord()) {
      return new TextBuilder().build("请求中涉及敏感词汇，拒绝答复！！！", wxMessage, weixinService);
    } else if (currentTime - lastTime > wxPlatform.getRequestIntervalLimit() * 1000) {
      AiChatDTO aiChatDTOReq =
          AiChatDTO.newBuilder()
              .withType("1")
              .withQuestion(wxMessage.getContent())
              .withFromNo(wxMessage.getFromUser())
              .withToNo(wxPlatform.getCopilotNo())
              .build();
      ChatBiz chatBiz = applicationContext.getBean(ChatBiz.class);
      wxPlatformAiChatAsyncExecutorService.submit(
          new WxPlatformAiChatAsyncBiz(
              aiChatDTOReq, chatBiz, wxPlatform, wxMessage, weixinService));
      lastTime = currentTime;
    } else {
      System.out.println("**************调用过于频繁或者是触发微信公众号retry机制****************" + exceptionResp);
      preResponse = exceptionResp;
    }
    if (StringUtils.isEmpty(preResponse)) {
      return null;
    } else {
      return new TextBuilder().build(preResponse, wxMessage, weixinService);
    }
  }
}
