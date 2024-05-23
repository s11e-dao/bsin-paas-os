package me.flyray.bsin.server.biz;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import me.flyray.bsin.thirdauth.wx.builder.TextBuilder;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.flyray.bsin.cache.BsinCacheProvider;
import me.flyray.bsin.domain.domain.CopilotInfo;
import me.flyray.bsin.domain.domain.WxPlatform;
import me.flyray.bsin.domain.domain.WxPlatformUser;
import me.flyray.bsin.infrastructure.mapper.CopilotMapper;
import me.flyray.bsin.infrastructure.mapper.WxPlatformMapper;
import me.flyray.bsin.infrastructure.mapper.WxPlatformUserMapper;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.flyray.bsin.thirdauth.wx.handler.AbstractHandler;
import me.flyray.bsin.thirdauth.wx.utils.BsinWxMpServiceUtil;
import me.flyray.bsin.thirdauth.wx.utils.WxMpProperties;
import me.flyray.bsin.utils.BsinSnowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.EventType.*;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
public class WxPlatformSubscribeHandlerBiz extends AbstractHandler {

  @Autowired private WxPlatformMapper wxPlatformMapper;
  @Autowired private WxPlatformUserMapper wxPlatformUserMapper;
  @Autowired BsinWxMpServiceUtil bsinWxMpServiceUtil;
  @Autowired private BsinCacheProvider bsinCacheProvider;
  @Autowired private CopilotMapper copilotMapper;

  @Value("${bsin.ai.aesKey}")
  private String aesKey;

  @Value("${wx.mp.config-storage.redis.host}")
  private String wxRedisHost;

  @Value("${wx.mp.config-storage.redis.port}")
  private Integer wxRedisPort;

  @Value("${wx.mp.config-storage.redis.password}")
  private String wxRedisPassword;
  private WxMpProperties.RedisConfig redisConfig;
  /**
   * @description: 公众号关注订阅处理
   * @param wxMessage
   * @param context
   * @param wxMpService
   * @param sessionManager
   * @return
   * @throws WxErrorException
   */
  @Override
  public WxMpXmlOutMessage handle(
      WxMpXmlMessage wxMessage,
      Map<String, Object> context,
      WxMpService wxMpService,
      WxSessionManager sessionManager)
      throws WxErrorException {

    this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());
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

    if (redisConfig == null) {
      redisConfig = new WxMpProperties.RedisConfig();
      redisConfig.setHost(wxRedisHost);
      redisConfig.setPort(wxRedisPort);
      redisConfig.setPassword(wxRedisPassword);
    }
    WxMpService weixinService = bsinWxMpServiceUtil.getWxMpService(config,redisConfig);

    // 获取微信用户基本信息
    if (wxMessage.getEvent().equals(SUBSCRIBE)) {
      try {
        WxMpUser userWxInfo =
            weixinService.getUserService().userInfo(wxMessage.getFromUser(), null);
        if (userWxInfo != null) {
          System.out.println("插入用户数据表");
          WxPlatformUser wxPlatformUser = wxPlatformUserMapper.selectByOpenId(openId);
          if (wxPlatformUser == null) {
            wxPlatformUser = new WxPlatformUser();
            wxPlatformUser.setTenantId(wxPlatform.getTenantId());
            wxPlatformUser.setOpenId(openId);
            wxPlatformUser.setAppId(appId);
            wxPlatformUser.setSerialNo(BsinSnowflake.getId());
            wxPlatformUserMapper.insert(wxPlatformUser);
          }
        }
      } catch (WxErrorException e) {
        if (e.getError().getErrorCode() == 48001) {
          this.logger.info("该公众号没有获取用户信息权限！");
        }
      }
    }
    WxMpXmlOutMessage responseResult = null;
    try {
      responseResult = this.handleSpecial(wxMessage);
      if (responseResult != null) {
        return responseResult;
      }
      if (wxMessage.getEvent().equals(SUBSCRIBE)) {
        // 关注公众号自动回复文本
        System.out.println("关注公众号自动回复文本: " + wxPlatform.getSubscribeResponse());
        return new TextBuilder().build(wxPlatform.getSubscribeResponse(), wxMessage, weixinService);
      } else if (wxMessage.getEvent().equals(UNSUBSCRIBE)) {
        // 取消关注公众号自动回复文本
        System.out.println("取消关注公众号自动回复文本：有缘再相见");
        return new TextBuilder().build("有缘再相见！", wxMessage, weixinService);
      }
    } catch (Exception e) {
      this.logger.error(e.getMessage(), e);
    }

    return null;
  }

  /** 处理特殊请求，比如如果是扫码进来的，可以做相应处理 */
  private WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage) throws Exception {
    // TODO
    return null;
  }
}
