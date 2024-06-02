package me.flyray.bsin.server.biz;

import static me.chanjar.weixin.common.api.WxConsts.EventType.CLICK;


import me.flyray.bsin.redis.manager.BsinCacheProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.flyray.bsin.domain.entity.WxPlatform;
import me.flyray.bsin.facade.response.QuestionPreProcessDTO;
import me.flyray.bsin.facade.service.CustomerService;
import me.flyray.bsin.facade.service.WxPlatformService;
import me.flyray.bsin.infrastructure.mapper.WxPlatformMapper;
import me.flyray.bsin.server.utils.VerficationCodeUtil;
import me.flyray.bsin.thirdauth.wx.builder.TextBuilder;
import me.flyray.bsin.thirdauth.wx.handler.AbstractHandler;
import me.flyray.bsin.thirdauth.wx.utils.BsinWxMpServiceUtil;
import me.flyray.bsin.thirdauth.wx.utils.WxMpProperties;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
public class WxPlatformClickHandlerBiz extends AbstractHandler {

  @Autowired private WxPlatformMapper wxPlatformMapper;
  @Autowired BsinWxMpServiceUtil bsinWxMpServiceUtil;
  @Autowired private BsinCacheProvider bsinCacheProvider;

  private static long lastTime = 0;
  private static long currentTime = 0;

  private WxPlatformService wxPlatformService;


  private CustomerService customerService;

  @Value("${bsin.ai.aesKey}")
  private String aesKey;

  @Value("${bsin.ai.mpVerifyCodeTemplate}")
  private String mpVerifyCodeTemplate;

  @Value("${bsin.ai.defaultMerchantNo}")
  private String defaultMerchantNo;

  @Value("${bsin.ai.defaultTenantId}")
  private String defaultTenantId;

  @Value("${wx.mp.config-storage.redis.host}")
  private String wxRedisHost;

  @Value("${wx.mp.config-storage.redis.port}")
  private Integer wxRedisPort;

  @Value("${wx.mp.config-storage.redis.password}")
  private String wxRedisPassword;
  private WxMpProperties.RedisConfig redisConfig;
  /**
   * @description: 公众号菜单button点击事件
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

      // 非超时请求才做回复：limit时间
      currentTime = System.currentTimeMillis();
      String preResponse = wxPlatform.getPreResp();
      String exceptionResp = wxPlatform.getExceptionResp();

      if (wxMessage.getEvent().equals(CLICK)) {
        // CLICK事件拆分
        QuestionPreProcessDTO questionPreProcessDTO =
            QuestionPreProcessBiz.preProcess(wxMessage.getEventKey());
        // 限制70s內只能获取一次验证码
        if (currentTime - lastTime > 70000) {
          lastTime = currentTime;
          /**
           * 1、注册bsin-copilot商户的时候，需要关注火源社区公众号，发送：注册时的用户名+##(后续开率使用菜单提示实现)到公众号
           * 2、后台提取出注册用户名，并生成6位数验证码，返回到该公众号用户的同时，存入redis: key=mpVerifyCode:注册用户名 value=6位数验证码
           * validTime=120s * 3、微信用户和bsin-copilot商户绑定： 同时将映射火源社区公众号获取的微信用户的 openID 和 bsin-copilot
           * 用户名存入redis: key=s11eMpOpenId2User:openID value=注册用户名 * 4、实现微信公众号获取默认微信
           */
          //          // ! 微信公众号获取验证码功能: 用户名+##
          //          if (questionPreProcessDTO.getMpVerifyCodeUsername() != null) {
          //            // 1.generate 验证码
          //            String mpVerifyCode = VerficationCodeUtil.getVerficationCode(6);
          //            // 2.将验证码存在缓存里面 mpVerifyCode:userName verifycode
          //            bsinCacheProvider.set(
          //                "mpVerifyCodeWithUsername:" +
          // questionPreProcessDTO.getMpVerifyCodeUsername(),
          //                mpVerifyCode,
          //                90);
          //            // 2.火源社区微信openId和bsin-copilot商户登录名绑定  TODO:持久化到数据库
          //            bsinCacheProvider.set(
          //                "copilotUsernameWithOpenId:" + wxMessage.getFromUser(),
          //                questionPreProcessDTO.getMpVerifyCodeUsername());
          //            // 3.调用公众号推送模版
          //            WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
          //            // 消息模板ID
          //            wxMpTemplateMessage.setTemplateId(mpVerifyCodeTemplate);
          //            wxMpTemplateMessage.setToUser(wxMessage.getFromUser());
          //            List<WxMpTemplateData> wxMpTemplateData = new ArrayList<>();
          //            wxMpTemplateData.add(new WxMpTemplateData("verifyCode", mpVerifyCode));
          //            wxMpTemplateData.add(new WxMpTemplateData("validityTime", "90"));
          //            wxMpTemplateMessage.setData(wxMpTemplateData);
          //            try {
          //
          // weixinService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
          //            } catch (WxErrorException errorException) {
          //              logger.error("推送出现错误！");
          //              return new TextBuilder().build("推送出现错误！", wxMessage, weixinService);
          //            }
          //            return new TextBuilder()
          //                .build(
          //                    "用户名："
          //                        + questionPreProcessDTO.getMpVerifyCodeUsername()
          //                        + " openID:"
          //                        + wxMessage.getFromUser()
          //                        + " 验证码："
          //                        + mpVerifyCode,
          //                    wxMessage,
          //                    weixinService);
          //          }
          // 获取注册验证码
          if (questionPreProcessDTO.isGetMpVerifyCode()) {
            // 1.generate 验证码
            String mpVerifyCode = VerficationCodeUtil.getVerficationCode(6);
            // 2.火源社区微信openId和bsin-copilot商户登录名绑定，注册时，若能根据验证码找到 openId,
            // 即验证码有效，同时完成商户ID(或者商户登录名)和openId的绑定
            bsinCacheProvider.set(
                "openIdWithMpVerifyCode:" + mpVerifyCode, wxMessage.getFromUser(), 90);

            //            String openIdWithMpVerifyCode =
            //                bsinCacheProvider.get("openIdWithMpVerifyCode:" + mpVerifyCode);
            //            if (openIdWithMpVerifyCode.isEmpty()) {
            //              throw new BusinessException("100000", "验证码错误");
            //            }
            //            bsinCacheProvider.set(
            //                "bsinCopilotUsernameWithOpenId:" + openIdWithMpVerifyCode, username);

            WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
            wxMpTemplateMessage.setTemplateId(mpVerifyCodeTemplate);
            wxMpTemplateMessage.setToUser(wxMessage.getFromUser());
            //  测试公众号推送模版
            List<WxMpTemplateData> wxMpTemplateData = new ArrayList<>();
            wxMpTemplateData.add(new WxMpTemplateData("verifyCode", mpVerifyCode));
            wxMpTemplateData.add(new WxMpTemplateData("validityTime", "100"));

            // 火源社区公众号推送模版
            List<WxMpTemplateData> s11eWxMpTemplateData = new ArrayList<>();
            // TODO: 从缓存获取用户名
            s11eWxMpTemplateData.add(
                new WxMpTemplateData("thing6", wxMessage.getFromUser())); // 用户名 --> openId
            s11eWxMpTemplateData.add(
                new WxMpTemplateData("character_string2", mpVerifyCode)); // 账号 --> xxx
            s11eWxMpTemplateData.add(
                new WxMpTemplateData("character_string8", mpVerifyCode)); // 初始密码 --> 验证码
            s11eWxMpTemplateData.add(new WxMpTemplateData("time3", new Date().toString())); // 注册日期

            wxMpTemplateMessage.setData(s11eWxMpTemplateData);

            // 火源社区公众号申请模版：
            //            模板ID
            //            SJ0lduLimcMXC-FUeVKXPYik-3g4tcF-YVreDk0t3GA
            //            模板编号
            //            45712
            //            用户名
            //            {{thing6.DATA}}
            //            账号
            //            {{character_string2.DATA}}
            //            初始密码
            //            {{character_string8.DATA}}
            //            注册日期
            //            {{time3.DATA}}
            //            try {
            //
            // weixinService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
            //            } catch (WxErrorException errorException) {
            //              logger.error("推送出现错误！");
            ////              return new TextBuilder().build("推送出现错误！", wxMessage, weixinService);
            //            }
            return new TextBuilder()
                .build(
                    "您好，您的验证码为：" + mpVerifyCode + "，有效期90s，若非本人操作，则忽略。", wxMessage, weixinService);
          }
          // ! 获取微信登录二维码
          else if (questionPreProcessDTO.isGetWechatLoginQqCode()) {
            // 1.根据火源社区微信openId获取bsin-copilot商户登录名
            String bsinCopilotUsername =
                bsinCacheProvider.get("bsinCopilotUsernameWithOpenId:" + wxMessage.getFromUser());
            // 1.根据火源社区微信openId获取bsin-copilot商户客户号？？？？？？？？？？？？？？
            String bsinCopilotCustomerNoWithOpenId =
                bsinCacheProvider.get("bsinCopilotCustomerNoWithOpenId:" + wxMessage.getFromUser());
            if (bsinCopilotUsername == null) {
              return new TextBuilder()
                  .build(
                      "未查询到您的s11e-copilot平台账户信息，请登录 copilot.s11edao.com 注册账户！",
                      wxMessage,
                      weixinService);
            }
            // 2.调用启动wechat的接口
            Map requestMap = new HashMap<String, String>();
            requestMap.put("tenantId", defaultTenantId);
            requestMap.put("username", bsinCopilotUsername);

            try {
              // 2.1 查询出该用户的bsinCopilot用户信息
              Map<String, Object> resMap = null; //customerService.getMerchantCustomerInfoByUsername(requestMap);
              Map<String, Object> data = (Map<String, Object>) resMap.get("data");
              String customerNo = (String) data.get("customerNo");
              if (customerNo.isEmpty()) {
                return new TextBuilder()
                    .build(
                        "未查询到您的s11e-copilot平台账户信息，请登录 copilot.s11edao.com 注册账户！！",
                        wxMessage,
                        weixinService);
              }

              // 2.2 查询bsinCopilot用户的默认微信平台的wechat应用
              requestMap.put("type", "wechat");
              requestMap.put("customerNo", customerNo);
              // TODO: 无法获取merchantNo
              //              requestMap.put("merchantNo", defaultMerchantNo);
              resMap = wxPlatformService.getDefault(requestMap);

              if (resMap.get("data") == null || resMap.get("data") == "") {
                return new TextBuilder()
                    .build("未查询到s11e-copilot平台账户的微信机器人信息！", wxMessage, weixinService);
              }
              data = (Map<String, Object>) resMap.get("data");
              String wxPlatformNo = (String) data.get("serialNo");
              requestMap.put("serialNo", wxPlatformNo);
              requestMap.put("operation", "loginWechat");
              String loginQrCodeUrl = "登录链接";
              try {
                resMap = wxPlatformService.loginIn(requestMap);
                data = (Map<String, Object>) resMap.get("data");
                loginQrCodeUrl = (String) data.get("loginQrUrl");
              } catch (Exception e) {
                return new TextBuilder().build("wechatBot Service err!", wxMessage, weixinService);
                //                return new TextBuilder().build(e.toString(), wxMessage,
                // weixinService);
              }
              System.out.println("loginQrCodeUrl: " + loginQrCodeUrl);
              // 3.推动二维码图片or链接
              String content =
                  "点击以下链接，微信扫一扫，您的微信将由您的AI分身接管(由于微信平台限制，登录只能扫码，识别无法直接登录)：\n" + loginQrCodeUrl;
              return new TextBuilder().build(content, wxMessage, weixinService);
            } catch (Exception e) {
              return new TextBuilder()
                  .build(
                      "未查询到您的s11e-copilot平台账户信息，请登录 copilot.s11edao.com 注册账户！！",
                      wxMessage,
                      weixinService);
              //              return new TextBuilder().build(e.toString(), wxMessage,
              // weixinService);
            }
          } else if (questionPreProcessDTO.isSensitiveWord()) {
            return new TextBuilder().build("请求中涉及敏感词汇，拒绝答复！！！", wxMessage, weixinService);
          }
        }
      } else {
        System.out.println("**************调用过于频繁或者是触发微信公众号retry机制****************" + exceptionResp);
        preResponse = exceptionResp;
        return new TextBuilder().build(preResponse, wxMessage, weixinService);
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
