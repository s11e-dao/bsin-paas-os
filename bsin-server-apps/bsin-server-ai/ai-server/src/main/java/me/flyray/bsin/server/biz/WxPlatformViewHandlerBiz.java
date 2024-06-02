package me.flyray.bsin.server.biz;

import static me.chanjar.weixin.common.api.WxConsts.EventType.*;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import java.util.Map;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.flyray.bsin.domain.entity.WxPlatform;
import me.flyray.bsin.infrastructure.mapper.WxPlatformMapper;
import me.flyray.bsin.redis.manager.BsinCacheProvider;
import me.flyray.bsin.thirdauth.wx.builder.TextBuilder;
import me.flyray.bsin.thirdauth.wx.handler.AbstractHandler;
import me.flyray.bsin.thirdauth.wx.utils.BsinWxMpServiceUtil;
import me.flyray.bsin.thirdauth.wx.utils.WxMpProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
public class WxPlatformViewHandlerBiz extends AbstractHandler {

  @Autowired private WxPlatformMapper wxPlatformMapper;
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
  /**
   * @description: 公众号菜单view点击事件
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

    this.logger.info("OPENID: " + wxMessage.getFromUser());
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
    WxMpXmlOutMessage responseResult = null;
    try {
      responseResult = this.handleSpecial(wxMessage);
      if (responseResult != null) {
        return responseResult;
      }
      if (wxMessage.getEvent().equals(VIEW)) {
        // VIEW事件拆分
        return new TextBuilder().build("view:" + wxMessage.getUrl(), wxMessage, weixinService);
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
