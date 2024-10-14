package me.flyray.bsin.server.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import cn.binarywang.wx.miniapp.message.WxMaXmlOutMessage;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.service.WxService;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.enums.AppType;
// import me.flyray.bsin.server.biz.WxMpClickHandlerBiz;
// import me.flyray.bsin.server.biz.WxMpMsgHandlerBiz;
// import me.flyray.bsin.server.biz.WxMpSubscribeHandlerBiz;
// import me.flyray.bsin.server.biz.WxMpViewHandlerBiz;
import me.flyray.bsin.enums.AuthMethod;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.mapper.BizRoleAppMapper;
import me.flyray.bsin.server.biz.CustomerBiz;
import me.flyray.bsin.thirdauth.wx.utils.*;
import me.flyray.bsin.domain.entity.BizRoleApp;

import me.flyray.bsin.redis.provider.BsinCacheProvider;
import me.flyray.bsin.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Slf4j
@RestController
public class WxPortalController {

  //  @Autowired private WxMpMsgHandlerBiz wxMpMsgHandlerBiz;
  //  @Autowired private WxMpSubscribeHandlerBiz wxMpSubscribeHandlerBiz;
  //  @Autowired private WxMpClickHandlerBiz wxMpClickHandlerBiz;
  //  @Autowired private WxMpViewHandlerBiz wxMpViewHandlerBiz;

  @Autowired BsinWxMaServiceUtil bsinWxMaServiceUtil;
  @Autowired BsinWxMpServiceUtil bsinWxMpServiceUtil;
  @Autowired BsinWxCpServiceUtil bsinWxCpServiceUtil;

  @Autowired private CustomerBiz customerBiz;

  @Autowired private BizRoleAppMapper bzRoleAppMapper;

  @Value("${bsin.crm.aesKey}")
  private String aesKey;

  @Value("${wx.mp.config-storage.redis.host}")
  private String wxRedisHost;

  @Value("${wx.mp.config-storage.redis.port}")
  private Integer wxRedisPort;

  @Value("${wx.mp.config-storage.redis.password}")
  private String wxRedisPassword;

  private static WxRedisConfig wxRedisConfig;

  /**
   * 微信平台服务器验证
   *
   * @param appid
   * @param signature
   * @param timestamp
   * @param nonce
   * @param echostr
   * @return
   */
  // TODO: 映射到 shenyu 网关
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
    BizRoleApp merchantWxApp = bzRoleAppMapper.selectByAppId(appid);
    if (merchantWxApp == null) {
      return "未找到微信平台配置信息";
    }
    if (StringUtils.equals(merchantWxApp.getAppType(), AppType.WX_MP.getType())) {
      log.info("微信公众号请求验证");
      WxMpService wxService = (WxMpService) getWxService(merchantWxApp);
      if (!wxService.switchover(appid)) {
        throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
      }
      // TODO: 校验失败
      if (wxService.checkSignature(timestamp, nonce, signature)) {
        log.info("微信公众号请求验证成功:=[%s]", echostr);
        return echostr;
      }
    } else if (merchantWxApp.getAppType().equals(AppType.WX_CP.getType())) {
      log.debug("微信企业号|企业微信请求验证");
      // TODO: 企业号暂时不支持
      return "暂不支持企业号";
    } else if (merchantWxApp.getAppType().equals(AppType.WX_MINIAPP.getType())) {
      log.debug("小程序请求验证");
      WxMaService wxService = (WxMaService) getWxService(merchantWxApp);
      if (!wxService.switchover(appid)) {
        throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
      }
      // TODO: 校验失败
      if (wxService.checkSignature(timestamp, nonce, signature)) {
        log.info("微信公众号请求验证成功:=[%s]", echostr);
        WxMaConfigHolder.remove(); // 清理ThreadLocal
        return echostr;
      }
    }
    return "非法请求";
  }

  /**
   * 不对外暴露接口调用，作为rpc服务对内提供
   *
   * @param appid
   * @param code
   * @return
   */
  public CustomerBase authorizedLogin(String appid, String code)
      throws UnsupportedEncodingException {
    BizRoleApp merchantWxApp = bzRoleAppMapper.selectByAppId(appid);
    if (merchantWxApp == null) {
      throw new BusinessException("100000", "未找到对应appid");
    }
    if (StringUtils.equals(merchantWxApp.getAppType(), AppType.WX_MINIAPP.getType())) {
      WxMaService wxService = (WxMaService) getWxService(merchantWxApp);

      // 调用微信sdk获取openId及sessionKey
      String sessionKey = null;
      String openId = null;
      try {
        long beginTime = System.currentTimeMillis();
        WxMaJscode2SessionResult result = wxService.getUserService().getSessionInfo(code);
        //        Thread.sleep(6000);
        long endTime = System.currentTimeMillis();
        log.info("响应时间:{}", (endTime - beginTime));
        sessionKey = result.getSessionKey(); // session id
        openId = result.getOpenid(); // 用户唯一标识 OpenID
      } catch (Exception e) {
        throw new BusinessException("100000", e.toString());
      }

      if (sessionKey == null || openId == null) {
        log.error("微信登录,调用官方接口失败：{}", code);
        throw new BusinessException("100000", "微信登录,调用官方接口失败:" + code);
      } else {
        log.info("openId={},sessionKey={}", openId, sessionKey);
      }
      // 根据openId查询 crm_customer_base 表, 如果不存在，初始化wx_user,并保存到数据库中, 如果存在，更新最后登录时间
      CustomerBase customerBase = customerBiz.getCustomerByOpenId(openId);
      if (customerBase == null) {
        customerBase = new CustomerBase();
        customerBase.setAuthMethod(AuthMethod.WECHAT.getType());
        customerBase.setSessionKey(sessionKey);
        customerBase.setCredential(openId);
        customerBase = customerBiz.register(customerBase);
      } else {
        // 更新最后登录时间
        customerBiz.updateCustomerBase(customerBase);
      }
      return customerBase;
    } else {
      throw new BusinessException("100000", "暂不支持该类型授权登录：" + merchantWxApp.getAppType() + "！！！");
    }
  }

  /**
   * 绑定微信平台用户手机号
   *
   * @param appid
   * @param encryptedData
   * @param iv
   * @return
   */
  public String bindingPhoneNumber(String openId, String appid, String encryptedData, String iv) {
    String phone = null;
    BizRoleApp merchantWxApp = bzRoleAppMapper.selectByAppId(appid);
    if (merchantWxApp == null) {
      throw new BusinessException("100000", "未找到对应appid");
    }
    if (StringUtils.equals(merchantWxApp.getAppType(), AppType.WX_MINIAPP.getType())) {
      WxMaService wxService = (WxMaService) getWxService(merchantWxApp);

      CustomerBase customerBase = customerBiz.getCustomerByOpenId(openId);
      WxMaPhoneNumberInfo phoneNumberInfo = null;
      try {
        phoneNumberInfo =
            wxService
                .getUserService()
                .getPhoneNoInfo(customerBase.getSessionKey(), encryptedData, iv);
      } catch (Exception e) {
        log.error("绑定手机号码失败,获取微信绑定的手机号码出错：{}", e.toString());
        e.printStackTrace();
        throw new BusinessException("100000", "绑定手机号码失败,获取微信绑定的手机号码出错:" + e.toString());
      }
      phone = phoneNumberInfo.getPhoneNumber();
      customerBiz.updateCustomerBase(customerBase);
    }
    return phone;
  }

  /**
   * 微信平台请求
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

    BizRoleApp merchantWxApp = bzRoleAppMapper.selectByAppId(appid);
    if (merchantWxApp == null) {
      throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
    }
    if (StringUtils.equals(merchantWxApp.getAppType(), AppType.WX_MP.getType())) {
      WxMpXmlOutMessage outMessage = null;
      WxMpProperties.MpConfig config = new WxMpProperties.MpConfig();
      config.setAesKey(merchantWxApp.getAesKey());
      config.setAppId(appid);
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      config.setSecret(aes.decryptStr(merchantWxApp.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setToken(merchantWxApp.getToken());
      if (wxRedisConfig == null) {
        wxRedisConfig = new WxRedisConfig();
        wxRedisConfig.setHost(wxRedisHost);
        wxRedisConfig.setPort(wxRedisPort);
        wxRedisConfig.setPassword(wxRedisPassword);
      }
      WxMpService wxService = bsinWxMpServiceUtil.getWxMpService(config, wxRedisConfig);
      if (!wxService.switchover(appid)) {
        throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
      }
      if (!wxService.checkSignature(timestamp, nonce, signature)) {
        throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
      }
      if (encType == null) {
        // 明文传输的消息
        WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
        inMessage.getFromUser();
        // 设置用户的 公众号 appId
        BsinCacheProvider.put("ai", inMessage.getFromUser(), appid);
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
        BsinCacheProvider.put("ai", inMessage.getFromUser(), appid);
        outMessage = this.mpRoute(inMessage, wxService);
        if (outMessage == null) {
          return "";
        }
        out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
      }
    } else if (StringUtils.equals(merchantWxApp.getAppType(), AppType.WX_MINIAPP.getType())) {
      WxMaXmlOutMessage outMessage = null;
      WxMaProperties.MaConfig config = new WxMaProperties.MaConfig();
      config.setAesKey(merchantWxApp.getAesKey());
      config.setAppId(appid);
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      config.setSecret(aes.decryptStr(merchantWxApp.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setToken(merchantWxApp.getToken());
      if (wxRedisConfig == null) {
        wxRedisConfig = new WxRedisConfig();
        wxRedisConfig.setHost(wxRedisHost);
        wxRedisConfig.setPort(wxRedisPort);
        wxRedisConfig.setPassword(wxRedisPassword);
      }
      WxMaService wxMaService = bsinWxMaServiceUtil.getWxMaService(config, wxRedisConfig);

      final boolean isJson =
          Objects.equals(
              wxMaService.getWxMaConfig().getMsgDataFormat(), WxMaConstants.MsgDataFormat.JSON);
      if (StringUtils.isBlank(encType)) {
        // 明文传输的消息
        WxMaMessage inMessage;
        if (isJson) {
          inMessage = WxMaMessage.fromJson(requestBody);
        } else { // xml
          inMessage = WxMaMessage.fromXml(requestBody);
        }

        outMessage = this.maRoute(inMessage, wxMaService);
        WxMaConfigHolder.remove(); // 清理ThreadLocal
        if (outMessage == null) {
          return "";
        }
        out = outMessage.toXml();
      } else if ("aes".equals(encType)) {
        // 是aes加密的消息
        WxMaMessage inMessage;
        if (isJson) {
          inMessage = WxMaMessage.fromEncryptedJson(requestBody, wxMaService.getWxMaConfig());
        } else { // xml
          inMessage =
              WxMaMessage.fromEncryptedXml(
                  requestBody, wxMaService.getWxMaConfig(), timestamp, nonce, msgSignature);
        }
        outMessage = this.maRoute(inMessage, wxMaService);
        WxMaConfigHolder.remove(); // 清理ThreadLocal
        if (outMessage == null) {
          return "";
        }
        out = outMessage.toEncryptedXml(wxMaService.getWxMaConfig());
      } else {
        WxMaConfigHolder.remove(); // 清理ThreadLocal
        throw new RuntimeException("不可识别的加密类型：" + encType);
      }
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
    final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
    //    // 默认文本消息处理
    //    newRouter.rule().async(false).msgType(TEXT).handler(wxMpMsgHandlerBiz).end();
    //    // 关注事件
    //    newRouter
    //            .rule()
    //            .async(false)
    //            .msgType(EVENT)
    //            .event(SUBSCRIBE)
    //            .handler(wxMpSubscribeHandlerBiz)
    //            .end();
    //    // 取消关注事件
    //    newRouter
    //            .rule()
    //            .async(false)
    //            .msgType(EVENT)
    //            .event(UNSUBSCRIBE)
    //            .handler(wxMpSubscribeHandlerBiz)
    //            .end();
    //    // click事件
    //    newRouter
    //            .rule()
    //            .async(false)
    //            .msgType(EVENT)
    //            .event(CLICK)
    //            .handler(wxMpClickHandlerBiz)
    //            .end();
    //    // view事件:貌似view事件公众号自动跳转到相应链接了
    //    newRouter
    //            .rule()
    //            .async(false)
    //            .msgType(VIEW)
    //            .event(CLICK)
    //            .handler(wxMpViewHandlerBiz)
    //            .end();
    try {
      return newRouter.route(message);
    } catch (Exception e) {
      log.error("路由消息时出现异常！", e);
    }
    return null;
  }

  /**
   * 小程序消息事件路由
   *
   * @param message
   * @param wxMaService
   * @return
   */
  private WxMaXmlOutMessage maRoute(WxMaMessage message, WxMaService wxMaService) {
    final WxMaMessageRouter newRouter = new WxMaMessageRouter(wxMaService);
    //    // 默认文本消息处理
    //    newRouter.rule().async(false).msgType(TEXT).handler(wxMaMsgHandlerBiz).end();
    //    // 关注事件
    //    newRouter
    //            .rule()
    //            .async(false)
    //            .msgType(EVENT)
    //            .event(SUBSCRIBE)
    //            .handler(wxMaSubscribeHandlerBiz)
    //            .end();
    //    // 取消关注事件
    //    newRouter
    //            .rule()
    //            .async(false)
    //            .msgType(EVENT)
    //            .event(UNSUBSCRIBE)
    //            .handler(wxMaSubscribeHandlerBiz)
    //            .end();
    //    // click事件
    //    newRouter
    //            .rule()
    //            .async(false)
    //            .msgType(EVENT)
    //            .event(CLICK)
    //            .handler(wxMaClickHandlerBiz)
    //            .end();
    //    // view事件:貌似view事件公众号自动跳转到相应链接了
    //    newRouter
    //            .rule()
    //            .async(false)
    //            .msgType(VIEW)
    //            .event(CLICK)
    //            .handler(wxMaViewHandlerBiz)
    //            .end();
    try {
      return newRouter.route(message);
    } catch (Exception e) {
      log.error("路由消息时出现异常！", e);
    }
    return null;
  }

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
    BizRoleApp merchantWxApp = bzRoleAppMapper.selectByCorpAgentId(corpId, agentId.toString());
    if (StringUtils.equals(merchantWxApp.getAppType(), AppType.WX_CP.getType())) {
      log.debug("微信企业号|企业微信请求验证");
      WxCpProperties.CpConfig config = new WxCpProperties.CpConfig();
      config.setAesKey(merchantWxApp.getAesKey());
      config.setCorpId(corpId);
      config.setAgentId(agentId);
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      config.setSecret(aes.decryptStr(merchantWxApp.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setToken(merchantWxApp.getToken());
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
    BizRoleApp merchantWxApp = bzRoleAppMapper.selectByAppId(corpId + agentId.toString());
    WxCpProperties.CpConfig config = new WxCpProperties.CpConfig();
    config.setAesKey(merchantWxApp.getAesKey());
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

  private WxCpXmlOutMessage cpRoute(String corpId, Integer agentId, WxCpXmlMessage message) {
    //        final WxCpMessageRouter newRouter = new WxCpMessageRouter(wxCpService);
    //        try {
    //            return WxCpConfiguration.getRouters().get(corpId + agentId).route(message);
    //        } catch (Exception e) {
    //            log.error(e.getMessage(), e);
    //        }
    return null;
  }

  private WxService getWxService(BizRoleApp merchantWxApp) {
    WxService wxService = null;
    if (StringUtils.equals(merchantWxApp.getAppType(), AppType.WX_MP.getType())) {
      log.info("微信公众号应用");
      WxMpProperties.MpConfig config = new WxMpProperties.MpConfig();
      config.setAesKey(merchantWxApp.getAesKey());
      config.setAppId(merchantWxApp.getAppId());
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      config.setSecret(aes.decryptStr(merchantWxApp.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setToken(merchantWxApp.getToken());
      if (wxRedisConfig == null) {
        wxRedisConfig = new WxRedisConfig();
        wxRedisConfig.setHost(wxRedisHost);
        wxRedisConfig.setPort(wxRedisPort);
        wxRedisConfig.setPassword(wxRedisPassword);
      }
      wxService = bsinWxMpServiceUtil.getWxMpService(config, wxRedisConfig);
    } else if (StringUtils.equals(merchantWxApp.getAppType(), AppType.WX_MINIAPP.getType())) {
      log.info("微信小程序应用");
      WxMaProperties.MaConfig config = new WxMaProperties.MaConfig();
      config.setAesKey(merchantWxApp.getAesKey());
      config.setAppId(merchantWxApp.getAppId());
//      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
//      config.setSecret(aes.decryptStr(merchantWxApp.getAppSecret(), CharsetUtil.CHARSET_UTF_8));
      config.setSecret(merchantWxApp.getAppSecret());
      config.setToken(merchantWxApp.getToken());
      config.setMsgDataFormat("JSON");
      if (wxRedisConfig == null) {
        wxRedisConfig = new WxRedisConfig();
        wxRedisConfig.setHost(wxRedisHost);
        wxRedisConfig.setPort(wxRedisPort);
        wxRedisConfig.setPassword(wxRedisPassword);
      }
      wxService = bsinWxMaServiceUtil.getWxMaService(config, wxRedisConfig);
    } else {
    }
    return wxService;
  }
}
