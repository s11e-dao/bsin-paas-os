package me.flyray.bsin.server.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import cn.binarywang.wx.miniapp.message.WxMaXmlOutMessage;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.flyray.bsin.enums.WxPlatformType;
import me.flyray.bsin.thirdauth.wx.utils.*;
import me.flyray.bsin.domain.entity.WxPlatform;
import me.flyray.bsin.enums.WxPlatformType;
import me.flyray.bsin.infrastructure.mapper.WxPlatformMapper;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;

import me.flyray.bsin.redis.provider.BsinCacheProvider;
import me.flyray.bsin.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static me.chanjar.weixin.common.api.WxConsts.EventType.*;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.EVENT;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType.TEXT;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Slf4j
@RestController
public class WxPortalController {


  @Autowired
  BsinWxMaServiceUtil bsinWxMaServiceUtil;
  @Autowired
  BsinWxMpServiceUtil bsinWxMpServiceUtil;
  @Autowired
  BsinWxCpServiceUtil bsinWxCpServiceUtil;
  @Autowired private WxPlatformMapper wxPlatformMapper;

  private final WxMaMessageRouter wxMaMessageRouter;

  @Value("${bsin.crm.aesKey}")
  private String aesKey;

  @Value("${wx.mp.config-storage.redis.host}")
  private String wxRedisHost;

  @Value("${wx.mp.config-storage.redis.port}")
  private Integer wxRedisPort;

  @Value("${wx.mp.config-storage.redis.password}")
  private String wxRedisPassword;
  private WxMaProperties.RedisConfig maRedisConfig;
  private WxMpProperties.RedisConfig mpRedisConfig;
  /**
   * 公众号服务器验证
   *
   * @param appid
   * @param signature
   * @param timestamp
   * @param nonce
   * @param echostr
   * @return
   */
  @GetMapping(value = "/{appid}", produces = "text/plain;charset=utf-8")
  public String authGet(
      @PathVariable String appid,
      @RequestParam(name = "signature", required = false) String signature,
      @RequestParam(name = "timestamp", required = false) String timestamp,
      @RequestParam(name = "nonce", required = false) String nonce,
      @RequestParam(name = "echostr", required = false) String echostr) {

    log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature, timestamp, nonce, echostr);
    if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
      throw new IllegalArgumentException("请求参数非法，请核实!");
    }
    WxPlatform wxPlatform = wxPlatformMapper.selectByAppId(appid);
    if (wxPlatform == null) {
      return "未找到微信平台配置信息";
    }
    if (StringUtils.equals(wxPlatform.getType(), WxPlatformType.MP.getType())) {
      log.info("微信公众号请求验证");
      WxMpProperties.MpConfig config = new WxMpProperties.MpConfig();
      config.setAesKey(wxPlatform.getAesKey());
      config.setAppId(appid);
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      config.setSecret(aes.decryptStr(wxPlatform.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setToken(wxPlatform.getToken());
      if (mpRedisConfig == null) {
        mpRedisConfig = new WxMpProperties.RedisConfig();
        mpRedisConfig.setHost(wxRedisHost);
        mpRedisConfig.setPort(wxRedisPort);
        mpRedisConfig.setPassword(wxRedisPassword);
      }
      WxMpService wxService = bsinWxMpServiceUtil.getWxMpService(config, mpRedisConfig);

      if (!wxService.switchover(appid)) {
        throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
      }
      // TODO: 校验失败
      if (wxService.checkSignature(timestamp, nonce, signature)) {
        log.info("微信公众号请求验证成功:=[%s]", echostr);
        return echostr;
      }
    } else if (wxPlatform.getType().equals(WxPlatformType.CP.getType())) {
      log.debug("微信企业号|企业微信请求验证");
    }else if (wxPlatform.getType().equals(WxPlatformType.MINIAPP.getType())) {
      log.debug("小程序请求验证");
      WxMaProperties.MaConfig config = new WxMaProperties.MaConfig();
      config.setAesKey(wxPlatform.getAesKey());
      config.setAppId(appid);
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      config.setSecret(aes.decryptStr(wxPlatform.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setToken(wxPlatform.getToken());
      if (maRedisConfig == null) {
        maRedisConfig = new WxMaProperties.RedisConfig();
        maRedisConfig.setHost(wxRedisHost);
        maRedisConfig.setPort(wxRedisPort);
        maRedisConfig.setPassword(wxRedisPassword);
      }
      WxMaService wxService = bsinWxMaServiceUtil.getWxMaService(config, maRedisConfig);

      if (!wxService.switchover(appid)) {
        throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
      }
      // TODO: 校验失败
      if (wxService.checkSignature(timestamp, nonce, signature)) {
        log.info("微信公众号请求验证成功:=[%s]", echostr);
        WxMaConfigHolder.remove();//清理ThreadLocal
        return echostr;
      }
    }
    return "非法请求";
  }

  /**
   * 公众号请求
   *
   * @param appid
   * @param requestBody
   * @param signature
   * @param timestamp
   * @param nonce
   * @param openid
   * @param encType
   * @param msgSignature
   * @return
   */
  @PostMapping(value = "/{appid}", produces = "application/xml; charset=UTF-8")
  public String post(
      @PathVariable String appid,
      @RequestBody String requestBody,
      @RequestParam("signature") String signature,
      @RequestParam("timestamp") String timestamp,
      @RequestParam("nonce") String nonce,
      @RequestParam("openid") String openid,
      @RequestParam(name = "encrypt_type", required = false) String encType,
      @RequestParam(name = "msg_signature", required = false) String msgSignature) {
    log.debug(
        "\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
            + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
        openid,
        signature,
        encType,
        msgSignature,
        timestamp,
        nonce,
        requestBody);

    String out = null;

    WxPlatform wxPlatform = wxPlatformMapper.selectByAppId(appid);
    if (wxPlatform == null) {
      throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
    }
    if (StringUtils.equals(wxPlatform.getType(), WxPlatformType.MP.getType())) {
      WxMpXmlOutMessage outMessage = null;
      WxMpProperties.MpConfig config = new WxMpProperties.MpConfig();
      config.setAesKey(wxPlatform.getAesKey());
      config.setAppId(appid);
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      config.setSecret(aes.decryptStr(wxPlatform.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setToken(wxPlatform.getToken());
      if (mpRedisConfig == null) {
        mpRedisConfig = new WxMpProperties.RedisConfig();
        mpRedisConfig.setHost(wxRedisHost);
        mpRedisConfig.setPort(wxRedisPort);
        mpRedisConfig.setPassword(wxRedisPassword);
      }
      WxMpService wxService = bsinWxMpServiceUtil.getWxMpService(config, mpRedisConfig);
      if (!wxService.switchover(appid)) {
        //            new TextBuilder().build(String.format("未找到对应appid=[%s]的配置，请核实！", appid), null,
        // wxService);
        throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
      }
      if (!wxService.checkSignature(timestamp, nonce, signature)) {
        //            return String.format("非法请求，可能属于伪造的请求！");
        throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
      }
      if (encType == null) {
        // 明文传输的消息
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
        inMessage.getFromUser();
        // 设置用户的 公众号 appId
        BsinCacheProvider.put("ai",inMessage.getFromUser(), appid);
        outMessage = this.mpRoute(inMessage, wxService);
        if (outMessage == null) {
          return "";
        }
        out = outMessage.toXml();
      } else if ("aes".equalsIgnoreCase(encType)) {
        // aes加密的消息
        WxMpXmlMessage inMessage =
                WxMpXmlMessage.fromEncryptedXml(
                        requestBody, wxService.getWxMpConfigStorage(), timestamp, nonce, msgSignature);
        log.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
        // 设置用户的 公众号 appId
        BsinCacheProvider.put("ai",inMessage.getFromUser(), appid);
        outMessage = this.mpRoute(inMessage, wxService);
        if (outMessage == null) {
          return "";
        }
        out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
      }
    }else if (StringUtils.equals(wxPlatform.getType(), WxPlatformType.MINIAPP.getType())) {

      WxMaProperties.MaConfig config = new WxMaProperties.MaConfig();
      config.setAesKey(wxPlatform.getAesKey());
      config.setAppId(appid);
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      config.setSecret(aes.decryptStr(wxPlatform.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setToken(wxPlatform.getToken());
      if (maRedisConfig == null) {
        maRedisConfig = new WxMaProperties.RedisConfig();
        maRedisConfig.setHost(wxRedisHost);
        maRedisConfig.setPort(wxRedisPort);
        maRedisConfig.setPassword(wxRedisPassword);
      }
      WxMaService wxMaService = bsinWxMaServiceUtil.getWxMaService(config, maRedisConfig);

      final boolean isJson = Objects.equals(wxMaService.getWxMaConfig().getMsgDataFormat(),
              WxMaConstants.MsgDataFormat.JSON);
      if (StringUtils.isBlank(encType)) {
        // 明文传输的消息
        WxMaMessage inMessage;
        if (isJson) {
          inMessage = WxMaMessage.fromJson(requestBody);
        } else {//xml
          inMessage = WxMaMessage.fromXml(requestBody);
        }

        this.maRoute(inMessage);
        WxMaConfigHolder.remove();//清理ThreadLocal
        return "success";
      }

      if ("aes".equals(encType)) {
        // 是aes加密的消息
        WxMaMessage inMessage;
        if (isJson) {
          inMessage = WxMaMessage.fromEncryptedJson(requestBody, wxMaService.getWxMaConfig());
        } else {//xml
          inMessage = WxMaMessage.fromEncryptedXml(requestBody, wxMaService.getWxMaConfig(),
                  timestamp, nonce, msgSignature);
        }

        this.maRoute(inMessage);
        WxMaConfigHolder.remove();//清理ThreadLocal
        return "success";
      }
      WxMaConfigHolder.remove();//清理ThreadLocal
      throw new RuntimeException("不可识别的加密类型：" + encType);
    }
    log.debug("\n组装回复信息：{}", out);
    return out;
  }
  /**
   * 公众号消息事件路由
   *
   * @param message
   * @param wxMpService
   * @return
   */
  private WxMpXmlOutMessage mpRoute(WxMpXmlMessage message, WxMpService wxMpService) {
//    final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
//    // 默认文本消息处理
//    newRouter.rule().async(false).msgType(TEXT).handler(wxPlatformMsgHandlerBiz).end();
//    // 关注事件
//    newRouter
//            .rule()
//            .async(false)
//            .msgType(EVENT)
//            .event(SUBSCRIBE)
//            .handler(wxPlatformSubscribeHandlerBiz)
//            .end();
//    // 取消关注事件
//    newRouter
//            .rule()
//            .async(false)
//            .msgType(EVENT)
//            .event(UNSUBSCRIBE)
//            .handler(wxPlatformSubscribeHandlerBiz)
//            .end();
//    // click事件
//    newRouter
//            .rule()
//            .async(false)
//            .msgType(EVENT)
//            .event(CLICK)
//            .handler(wxPlatformClickHandlerBiz)
//            .end();
//    // view事件:貌似view事件公众号自动跳转到相应链接了
//    newRouter
//            .rule()
//            .async(false)
//            .msgType(VIEW)
//            .event(CLICK)
//            .handler(wxPlatformViewHandlerBiz)
//            .end();
//    try {
//      return newRouter.route(message);
//    } catch (Exception e) {
//      log.error("路由消息时出现异常！", e);
//    }
    return null;
  }
  private void maRoute(WxMaMessage message) {
    try {
      wxMaMessageRouter.route(message);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }


  //    @Bean
  //    public WxMpMessageRouter messageRouter(WxMpService wxMpService) {
  //        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
  //        // 记录所有事件的日志 （异步执行）
  //        newRouter.rule().handler(this.logHandler).next();
  //        // 接收客服会话管理事件
  //        newRouter.rule().async(false).msgType(EVENT).event(KF_CREATE_SESSION)
  //                .handler(this.kfSessionHandler).end();
  //        newRouter.rule().async(false).msgType(EVENT).event(KF_CLOSE_SESSION)
  //                .handler(this.kfSessionHandler).end();
  //        newRouter.rule().async(false).msgType(EVENT).event(KF_SWITCH_SESSION)
  //                .handler(this.kfSessionHandler).end();
  //        // 门店审核事件
  //
  // newRouter.rule().async(false).msgType(EVENT).event(POI_CHECK_NOTIFY).handler(this.storeCheckNotifyHandler).end();
  //        // 自定义菜单事件
  //
  // newRouter.rule().async(false).msgType(EVENT).event(EventType.CLICK).handler(this.menuHandler).end();
  //        // 点击菜单连接事件
  //
  // newRouter.rule().async(false).msgType(EVENT).event(EventType.VIEW).handler(this.nullHandler).end();
  //        // 关注事件
  //
  // newRouter.rule().async(false).msgType(EVENT).event(SUBSCRIBE).handler(this.subscribeHandler).end();
  //        // 取消关注事件
  //
  // newRouter.rule().async(false).msgType(EVENT).event(UNSUBSCRIBE).handler(this.unsubscribeHandler).end();
  //        // 上报地理位置事件
  //
  // newRouter.rule().async(false).msgType(EVENT).event(EventType.LOCATION).handler(this.locationHandler).end();
  //        // 接收地理位置消息
  //
  // newRouter.rule().async(false).msgType(XmlMsgType.LOCATION).handler(this.locationHandler).end();
  //        // 扫码事件
  //
  // newRouter.rule().async(false).msgType(EVENT).event(EventType.SCAN).handler(this.scanHandler).end();
  //        // 默认
  //        newRouter.rule().async(false).handler(this.msgHandler).end();
  //        return newRouter;
  //    }

  /**
   * 企业微信验证
   *
   * @param corpId
   * @param agentId
   * @param signature
   * @param timestamp
   * @param nonce
   * @param echostr
   * @return
   */
  @GetMapping(value = "/{corpId}/{agentId}", produces = "text/plain;charset=utf-8")
  public String authGet(
          @PathVariable String corpId,
          @PathVariable Integer agentId,
          @RequestParam(name = "msg_signature", required = false) String signature,
          @RequestParam(name = "timestamp", required = false) String timestamp,
          @RequestParam(name = "nonce", required = false) String nonce,
          @RequestParam(name = "echostr", required = false) String echostr) {
    log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature, timestamp, nonce, echostr);
    if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
      throw new IllegalArgumentException("请求参数非法，请核实!");
    }
    WxPlatform wxPlatform = wxPlatformMapper.selectByCorpAgentId(corpId, agentId.toString());
    if (StringUtils.equals(wxPlatform.getType(), WxPlatformType.CP.getType())) {
      log.debug("微信企业号|企业微信请求验证");
      WxCpProperties.CpConfig config = new WxCpProperties.CpConfig();
      config.setAesKey(wxPlatform.getAesKey());
      config.setCorpId(corpId);
      config.setAgentId(agentId);
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      config.setSecret(aes.decryptStr(wxPlatform.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setToken(wxPlatform.getToken());
      WxCpService wxCpService = bsinWxCpServiceUtil.getWxCpService(config);
      if (wxCpService == null) {
        throw new IllegalArgumentException(String.format("未找到对应agentId=[%d]的配置，请核实！", agentId));
      }
      if (wxCpService.checkSignature(signature, timestamp, nonce, echostr)) {
        return new WxCpCryptUtil(wxCpService.getWxCpConfigStorage()).decrypt(echostr);
      }
    }
    return "非法请求";
  }

  /**
   * 企业微信请求
   *
   * @param corpId
   * @param agentId
   * @param requestBody
   * @param signature
   * @param timestamp
   * @param nonce
   * @return
   */
  @PostMapping(produces = "application/xml; charset=UTF-8")
  public String post(
          @PathVariable String corpId,
          @PathVariable Integer agentId,
          @RequestBody String requestBody,
          @RequestParam("msg_signature") String signature,
          @RequestParam("timestamp") String timestamp,
          @RequestParam("nonce") String nonce) {
    log.info(
            "\n接收微信请求：[signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
            signature,
            timestamp,
            nonce,
            requestBody);
    WxPlatform wxPlatform = wxPlatformMapper.selectByAppId(corpId + agentId.toString());
    WxCpProperties.CpConfig config = new WxCpProperties.CpConfig();
    config.setAesKey(wxPlatform.getAesKey());
    config.setCorpId(corpId);
    config.setAgentId(agentId);

    final WxCpService wxCpService = bsinWxCpServiceUtil.getWxCpService(config);
    if (wxCpService == null) {
      throw new IllegalArgumentException(String.format("未找到对应agentId=[%d]的配置，请核实！", agentId));
    }
    WxCpXmlMessage inMessage =
            WxCpXmlMessage.fromEncryptedXml(
                    requestBody, wxCpService.getWxCpConfigStorage(), timestamp, nonce, signature);
    log.debug("\n消息解密后内容为：\n{} ", JsonUtils.toJson(inMessage));
    //        WxCpXmlOutMessage outMessage = this.route(corpId, agentId, inMessage);
    //        if (outMessage == null) {
    //            return "";
    //        }
    //        String out = outMessage.toEncryptedXml(wxCpService.getWxCpConfigStorage());
    //        log.debug("\n组装回复信息：{}", out);
    //        return out;
    return null;
  }

  //    private WxCpXmlOutMessage route(String corpId, Integer agentId, WxCpXmlMessage message) {
  //
  //        final WxCpMessageRouter newRouter = new WxCpMessageRouter(wxMpService);
  //        try {
  //            return WxCpConfiguration.getRouters().get(corpId + agentId).route(message);
  //        } catch (Exception e) {
  //            log.error(e.getMessage(), e);
  //        }
  //
  //        return null;
  //    }
}
