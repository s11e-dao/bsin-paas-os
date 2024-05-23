package me.flyray.bsin.server.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.AiCustomerFunction;
import me.flyray.bsin.domain.domain.WxPlatform;
import me.flyray.bsin.domain.domain.WxPlatformMenu;
import me.flyray.bsin.domain.enums.WxPlatformType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.WxPlatformService;
import me.flyray.bsin.infrastructure.mapper.WxPlatformMapper;
import me.flyray.bsin.infrastructure.mapper.WxPlatformMenuMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.AccountAvailableResourcesBiz;
import me.flyray.bsin.server.utils.HttpUtils;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.ReqBodyHandler;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author bolei
 * @description 针对表【ai_tenant_wxmp】的数据库操作Service实现
 * @createDate 2023-04-25 18:41:19
 */

@ShenyuDubboService(path = "/tenantWxPlatform", timeout = 6000)
@ApiModule(value = "tenantWxPlatform")
@Service
@Slf4j
public class WxPlatformServiceImpl implements WxPlatformService {

  @Autowired private WxPlatformMapper wxPlatformMapper;
  @Autowired private WxPlatformMenuMapper wxPlatformMenuMapper;
  @Autowired private AccountAvailableResourcesBiz accountAvailableResourcesBiz;

  @Value("${bsin.ai.aesKey}")
  private String aesKey;

  @Value("${bsin.go.base}")
  private String bsinGoBase;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public Map<String, Object> add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      //      if (customerNo == null) {
      //        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      //      }
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
      if (tenantId == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }

    String type = MapUtils.getString(requestMap, "type");
    String isCopyStr = MapUtils.getString(requestMap, "isCopy");
    boolean isCopy = false;
    if (isCopyStr != null) {
      isCopy = Boolean.parseBoolean(isCopyStr);
    }

    if (type == null) {
      throw new BusinessException("100000", "微信平台类型：type 为空！！");
    }
    WxPlatform wxPlatform = BsinServiceContext.getReqBodyDto(WxPlatform.class, requestMap);
    String motherWxPlatformNo = wxPlatform.getSerialNo();
    wxPlatform.setSerialNo(BsinSnowflake.getId());
    wxPlatform.setTenantId(tenantId);
    wxPlatform.setMerchantNo(merchantNo);
    wxPlatform.setCustomerNo(customerNo);
    if (wxPlatform.getAppSecret() != null) {
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      wxPlatform.setAppSecret(aes.encryptHex(wxPlatform.getAppSecret()));
    }
    AiCustomerFunction validFunction =
        accountAvailableResourcesBiz.functionServiceCheck(tenantId, merchantNo, customerNo);

    // 查询出当前客户已经创建的资源数量
    LambdaUpdateWrapper<WxPlatform> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(WxPlatform::getTenantId, tenantId);
    wrapper.eq(WxPlatform::getMerchantNo, merchantNo);
    wrapper.eq(WxPlatform::getCustomerNo, customerNo);
    wrapper.eq(WxPlatform::getType, type);
    List<WxPlatform> wxPlatformList = wxPlatformMapper.selectList(wrapper);

    switch (WxPlatformType.getInstanceById(wxPlatform.getType())) {
      case MP:
        if (wxPlatformList.size() >= validFunction.getMpNum()) {
          throw new BusinessException(
              "100000", "可创建的公众号资源超过限制：" + wxPlatformList.size() + ">=" + validFunction.getMpNum());
        }
        break;
      case CP:
        if (wxPlatformList.size() >= validFunction.getCpNum()) {
          throw new BusinessException(
              "100000",
              "可创建的企业微信资源超过限制：" + wxPlatformList.size() + ">=" + validFunction.getCpNum());
        }
        break;
      case MINIAPP:
        if (wxPlatformList.size() >= validFunction.getMiniappNum()) {
          throw new BusinessException(
              "100000",
              "可创建的小程序资源超过限制：" + wxPlatformList.size() + ">=" + validFunction.getMiniappNum());
        }
        break;
      case WECHAT:
        if (wxPlatformList.size() >= validFunction.getWechatNum()) {
          throw new BusinessException(
              "100000",
              "可创建的个人微信资源超过限制：" + wxPlatformList.size() + ">=" + validFunction.getWechatNum());
        }
        break;
      case MENU:
        if (wxPlatformList.size() >= validFunction.getMenuTemplateNum()) {
          throw new BusinessException(
              "100000",
              "可创建的菜单模版资源超过限制："
                  + wxPlatformList.size()
                  + ">="
                  + validFunction.getMenuTemplateNum());
        }
        break;

      default:
        throw new BusinessException("100000", "不支持的微信平台类型：" + wxPlatform.getType());
    }
    // 防止查询的时候查出来多条
    if (isCopy) {
      wxPlatform.setAppId(null);
      wxPlatform.setAesKey(null);
      wxPlatform.setAgentId(null);
    }
    wxPlatformMapper.insert(wxPlatform);
    // 插入菜单模版的菜单选项
    if (isCopy && motherWxPlatformNo != null) {
      List<WxPlatformMenu> wxPlatformMenus =
          wxPlatformMenuMapper.selectByWxPlatformMenuTemplateNo(motherWxPlatformNo);
      for (WxPlatformMenu wxPlatformMenu : wxPlatformMenus) {
        wxPlatformMenu.setSerialNo(BsinSnowflake.getId());
        wxPlatformMenu.setWxPlatformMenuTemplateNo(wxPlatform.getSerialNo());
        wxPlatformMenuMapper.insert(wxPlatformMenu);
      }
    }

    return RespBodyHandler.setRespBodyDto(wxPlatform);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String serialNo = (String) requestMap.get("serialNo");
    wxPlatformMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    WxPlatform wxPlatform = BsinServiceContext.getReqBodyDto(WxPlatform.class, requestMap);
    AiCustomerFunction validFunction =
        accountAvailableResourcesBiz.functionServiceCheck(
            wxPlatform.getTenantId(), wxPlatform.getMerchantNo(), wxPlatform.getCustomerNo());

    if (wxPlatform.getType().equals(WxPlatformType.WECHAT.getCode())) {
      if (wxPlatform.getGroupChat()) {
        if (!validFunction.getGroupChat()) {
          throw new BusinessException("100000", "请先订阅支持群聊的功能套餐!");
        }
      }
      if (wxPlatform.getHistoryChatSummary()) {
        if (!validFunction.getHistoryChatSummary()) {
          throw new BusinessException("100000", "请先订阅支持聊天记录总结并向量化存储的功能套餐!");
        }
      }
    }

    if (wxPlatform.getAppSecret() != null) {
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      wxPlatform.setAppSecret(aes.encryptHex(wxPlatform.getAppSecret()));
    }
    if (Boolean.TRUE.equals(wxPlatform.getDefaultFlag())) {
      setDefault(requestMap);
    }
    wxPlatformMapper.updateById(wxPlatform);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    WxPlatform wxPlatform = wxPlatformMapper.selectById(serialNo);
    //    if (wxPlatform.getAppSecret() != null) {
    //      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
    //      wxPlatform.setAppSecret(aes.decryptStr(wxPlatform.getAppSecret()));
    //    }
    return RespBodyHandler.setRespBodyDto(wxPlatform);
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();

    WxPlatform wxPlatform = BsinServiceContext.getReqBodyDto(WxPlatform.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<WxPlatform> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<WxPlatform> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(WxPlatform::getCreateTime);
    wrapper.eq(WxPlatform::getTenantId, tenantId);
    wrapper.eq(WxPlatform::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), WxPlatform::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatform.getSerialNo()),
        WxPlatform::getSerialNo,
        wxPlatform.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatform.getType()), WxPlatform::getType, wxPlatform.getType());
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatform.getStatus()),
        WxPlatform::getStatus,
        wxPlatform.getStatus());
    // 匹配系统资源
    wrapper.or().eq(WxPlatform::getEditable, false);
    IPage<WxPlatform> pageList = wxPlatformMapper.selectPage(page, wrapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();
    WxPlatform wxPlatform = BsinServiceContext.getReqBodyDto(WxPlatform.class, requestMap);
    LambdaUpdateWrapper<WxPlatform> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(WxPlatform::getCreateTime);
    wrapper.eq(WxPlatform::getTenantId, tenantId);
    wrapper.eq(WxPlatform::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), WxPlatform::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatform.getSerialNo()),
        WxPlatform::getSerialNo,
        wxPlatform.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatform.getType()), WxPlatform::getType, wxPlatform.getType());
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatform.getStatus()),
        WxPlatform::getStatus,
        wxPlatform.getStatus());
    // 匹配系统资源
    wrapper.or().eq(WxPlatform::getEditable, false);
    List<WxPlatform> wxPlatformList = wxPlatformMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(wxPlatformList);
  }

  @ApiDoc(desc = "getDefault")
  @ShenyuDubboClient("/getDefault")
  @Override
  public Map<String, Object> getDefault(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    // 微信平台类别：mp(公众号服务订阅号)、miniapp(小程序)、 cp(企业号|企业微信)、pay(微信支付)、open(微信开放平台)
    String type = MapUtils.getString(requestMap, "type");
    LambdaQueryWrapper<WxPlatform> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(WxPlatform::getTenantId, tenantId);
    wrapper.eq(StringUtils.isNotBlank(customerNo), WxPlatform::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(merchantNo), WxPlatform::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(type), WxPlatform::getType, type);
    wrapper.eq(WxPlatform::getDefaultFlag, true);
    WxPlatform wxPlatform = wxPlatformMapper.selectOne(wrapper);
    if (wxPlatform == null) {
      LambdaQueryWrapper<WxPlatform> wrapper1 = new LambdaQueryWrapper<>();
      wrapper1.eq(WxPlatform::getTenantId, tenantId);
      wrapper1.eq(StringUtils.isNotBlank(customerNo), WxPlatform::getCustomerNo, customerNo);
      wrapper1.eq(StringUtils.isNotBlank(merchantNo), WxPlatform::getMerchantNo, merchantNo);
      wrapper1.eq(StringUtils.isNotBlank(type), WxPlatform::getType, type);
      wxPlatform = wxPlatformMapper.selectOne(wrapper1);
      if (wxPlatform != null) {
        wxPlatform.setDefaultFlag(true);
        wxPlatformMapper.updateById(wxPlatform);
      }
    }
    return RespBodyHandler.setRespBodyDto(wxPlatform);
  }

  @ApiDoc(desc = "setDefault")
  @ShenyuDubboClient("/setDefault")
  @Override
  public Map<String, Object> setDefault(Map<String, Object> requestMap) {
    WxPlatform wxPlatform = BsinServiceContext.getReqBodyDto(WxPlatform.class, requestMap);
    LambdaUpdateWrapper<WxPlatform> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.set(WxPlatform::getDefaultFlag, wxPlatform.getDefaultFlag());
    lambdaUpdateWrapper.eq(WxPlatform::getSerialNo, wxPlatform.getSerialNo());
    if (Boolean.TRUE.equals(wxPlatform.getDefaultFlag())) {
      wxPlatformMapper.update(
          null,
          new LambdaUpdateWrapper<>(WxPlatform.class)
              .set(WxPlatform::getDefaultFlag, Boolean.FALSE)
              .eq(
                      StringUtils.isNotBlank(wxPlatform.getBindingWxPlatformNo()),
                  WxPlatform::getBindingWxPlatformNo,
                  wxPlatform.getBindingWxPlatformNo())
              .eq(
                      StringUtils.isNotBlank(wxPlatform.getType()),
                  WxPlatform::getType,
                  wxPlatform.getType())
              .eq(
                      StringUtils.isNotBlank(wxPlatform.getMerchantNo()),
                  WxPlatform::getMerchantNo,
                  wxPlatform.getMerchantNo())
              .eq(
                      StringUtils.isNotBlank(wxPlatform.getCustomerNo()),
                  WxPlatform::getCustomerNo,
                  wxPlatform.getCustomerNo()));
    }
    wxPlatformMapper.update(null, lambdaUpdateWrapper);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "loginIn")
  @ShenyuDubboClient("/loginIn")
  @Override
  public Map<String, Object> loginIn(Map<String, Object> requestMap)
      throws JsonProcessingException {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      //      if (merchantNo == null) {
      //        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      //      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      //      if (customerNo == null) {
      //        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      //      }
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
      if (tenantId == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    String operation = MapUtils.getString(requestMap, "operation");
    WxPlatform wxPlatform = wxPlatformMapper.selectById(serialNo);
    if (wxPlatform == null) {
      throw new BusinessException("100000", "未找到微信机器人ID:" + serialNo);
    }

    if (!WxPlatformType.WECHAT.getCode().equals(wxPlatform.getType())) {
      throw new BusinessException("100000", "not support operation!!!");
    }

    try {
      AiCustomerFunction validFunction =
          accountAvailableResourcesBiz.functionServiceCheck(tenantId, merchantNo, customerNo);

      String exceptionRespStr = wxPlatform.getExceptionResp();
      String exceptionResp = "false";
      if (StringUtils.isNotBlank(exceptionRespStr)) {
        exceptionResp = "true";
      }

      JSONObject bizParamsObject = new JSONObject();
      bizParamsObject.put("tenantId", tenantId);
      bizParamsObject.put("merchantNo", merchantNo);
      bizParamsObject.put("customerNo", customerNo);
      bizParamsObject.put("name", wxPlatform.getName());
      bizParamsObject.put("wxNo", wxPlatform.getWxNo());
      bizParamsObject.put("wxPlatformNo", serialNo);
      bizParamsObject.put("preResp", wxPlatform.getPreResp());
      bizParamsObject.put("groupChat", wxPlatform.getGroupChat().toString());
      bizParamsObject.put("historyChatSummary", wxPlatform.getHistoryChatSummary().toString());
      bizParamsObject.put("exceptionResp", exceptionResp);
      bizParamsObject.put("copilotNo", wxPlatform.getCopilotNo());
      bizParamsObject.put("requestIntervalLimit", wxPlatform.getRequestIntervalLimit().toString());
      bizParamsObject.put("operation", operation); // 登录： loginWechat 退出：
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      bizParamsObject.put("expirationTime", sdf.format(validFunction.getExpirationTime()));
      ReqBodyHandler reqBody =
          ReqBodyHandler.builder()
              .serviceName("Wechat")
              .methodName("login")
              .version("1.0")
              .bizParams(bizParamsObject)
              .build();
      // 创建ObjectMapper对象
      ObjectMapper objectMapper = new ObjectMapper();
      String req = objectMapper.writeValueAsString(reqBody);
      String ret = HttpUtils.postJson(bsinGoBase + "wechat/login", req);
      JSONObject objectRet = JSONObject.parseObject(ret);
      if (objectRet.isEmpty()) {
        throw new BusinessException("100000", "ai-go-wechat interface exception!!!");
      }
      if ((int) objectRet.get("code") != 0) {
        throw new BusinessException("100000", (String) objectRet.get("message"));
      }
      wxPlatform.setLoginQrUrl(objectRet.getString("loginQrUrl"));
      return RespBodyHandler.setRespBodyDto(wxPlatform);
    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
  }

  @ApiDoc(desc = "updateLoginResult")
  @ShenyuDubboClient("/updateLoginResult")
  @Override
  public Map<String, Object> updateLoginResult(Map<String, Object> requestMap)
      throws JsonProcessingException {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      //      if (merchantNo == null) {
      //        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      //      }
    }
    String wxPlatformNo = MapUtils.getString(requestMap, "wxPlatformNo");
    if (wxPlatformNo == null) {
      throw new BusinessException("100000", "Invalid wxPlatformNo");
    }
    WxPlatform wxPlatform = BsinServiceContext.getReqBodyDto(WxPlatform.class, requestMap);
    wxPlatform.setSerialNo(wxPlatformNo);
    wxPlatformMapper.updateById(wxPlatform);
    return RespBodyHandler.setRespBodyDto(wxPlatform);
  }

  @ApiDoc(desc = "getLoginList")
  @ShenyuDubboClient("/getLoginList")
  @Override
  public Map<String, Object> getLoginList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();
    try {
      JSONObject bizParamsObject = new JSONObject();
      bizParamsObject.put("tenantId", tenantId);
      bizParamsObject.put("merchantNo", merchantNo);
      bizParamsObject.put("customerNo", customerNo);
      ReqBodyHandler reqBody =
          ReqBodyHandler.builder()
              .serviceName("Wechat")
              .methodName("monitor")
              .version("1.0")
              .bizParams(bizParamsObject)
              .build();
      // 创建ObjectMapper对象
      ObjectMapper objectMapper = new ObjectMapper();
      String req = objectMapper.writeValueAsString(reqBody);
      String ret = HttpUtils.postJson(bsinGoBase + "wechat/monitor", req);
      JSONObject objectRet = JSONObject.parseObject(ret);
      if (objectRet.isEmpty()) {
        throw new BusinessException("100000", "ai-go-wechat interface exception!!!");
      }
      if ((int) objectRet.get("code") != 0) {
        throw new BusinessException("100000", (String) objectRet.get("message"));
      }
      return RespBodyHandler.setRespBodyListDto((List<?>) objectRet.get("wechatBotMonitorInfo"));

    } catch (Exception e) {
      throw new BusinessException("100000", e.toString());
    }
  }

}
