package me.flyray.bsin.server.impl;

import static me.flyray.bsin.constants.ResponseCode.CUSTOMER_NO_NOT_ISNULL;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.cache.BsinCacheProvider;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.AiCustomerFunction;
import me.flyray.bsin.domain.enums.FunctionSubscribeStatus;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.AiCustomerFunctionService;
import me.flyray.bsin.infrastructure.mapper.AiCustomerFunctionMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.server.utils.VerficationCodeUtil;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leoanrd
 * @date 2024/01/24 16:38
 * @desc
 */
@ShenyuDubboService(path = "/customerFunction", timeout = 6000)
@ApiModule(value = "customerFunction")
@Service
@Slf4j
public class AiCustomerFunctionServiceImpl implements AiCustomerFunctionService {

  @Autowired private AiCustomerFunctionMapper aiCustomerFunctionMapper;
  @Autowired private BsinCacheProvider bsinCacheProvider;

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
    AiCustomerFunction aiCustomerFunction =
        BsinServiceContext.getReqBodyDto(AiCustomerFunction.class, requestMap);
    aiCustomerFunction.setTenantId(tenantId);
    aiCustomerFunction.setMerchantNo(merchantNo);
    aiCustomerFunction.setCustomerNo(customerNo);
    aiCustomerFunctionMapper.insert(aiCustomerFunction);
    return RespBodyHandler.setRespBodyDto(aiCustomerFunction);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    aiCustomerFunctionMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    AiCustomerFunction aiCustomerFunction =
        BsinServiceContext.getReqBodyDto(AiCustomerFunction.class, requestMap);
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
      }
      aiCustomerFunction.setCustomerNo(customerNo);
    }
    aiCustomerFunctionMapper.updateById(aiCustomerFunction);
    return RespBodyHandler.setRespBodyDto(aiCustomerFunction);
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
    }
    AiCustomerFunction customerInfo = aiCustomerFunctionMapper.selectById(customerNo);
    //        customerInfo.setWalletPrivateKey(null);
    return RespBodyHandler.setRespBodyDto(customerInfo);
  }

  @ApiDoc(desc = "createOrder")
  @ShenyuDubboClient("/createOrder")
  @Override
  public Map<String, Object> createOrder(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();
    AiCustomerFunction aiCustomerFunction =
        BsinServiceContext.getReqBodyDto(AiCustomerFunction.class, requestMap);
    aiCustomerFunction.setTenantId(tenantId);
    aiCustomerFunction.setMerchantNo(merchantNo);
    aiCustomerFunction.setCustomerNo(customerNo);
    aiCustomerFunction.setSerialNo(BsinSnowflake.getId());
    aiCustomerFunction.setStatus(FunctionSubscribeStatus.PENDING_AUDIT.getCode());
    aiCustomerFunction.setType("2");
    aiCustomerFunctionMapper.insert(aiCustomerFunction);
    return RespBodyHandler.setRespBodyDto(aiCustomerFunction);
  }

  @ApiDoc(desc = "auditOrder")
  @ShenyuDubboClient("/auditOrder")
  @Override
  public Map<String, Object> auditOrder(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
    }
    String tenantId = loginUser.getTenantId();
    AiCustomerFunction aiCustomerFunction =
        BsinServiceContext.getReqBodyDto(AiCustomerFunction.class, requestMap);
    if (aiCustomerFunction.getStatus().equals(FunctionSubscribeStatus.PENDING_AUDIT.getCode())) {
      aiCustomerFunction.setStatus(FunctionSubscribeStatus.NORMAL.getCode());
      Date currentDate = new Date();
      aiCustomerFunction.setStartTime(currentDate);
      aiCustomerFunction.setEndTime(
          new Date(
              currentDate.getTime()
                  + (long) aiCustomerFunction.getServiceDuration() * 24 * 60 * 60 * 1000));
      aiCustomerFunctionMapper.updateById(aiCustomerFunction);
    } else {
      throw new BusinessException("100000", "请先支付");
    }
    return RespBodyHandler.setRespBodyDto(aiCustomerFunction);
  }

  @ApiDoc(desc = "getSubscribableList")
  @ShenyuDubboClient("/getSubscribableList")
  @Override
  public Map<String, Object> getSubscribableList(Map<String, Object> requestMap) {
    AiCustomerFunction aiCustomerFunction =
        BsinServiceContext.getReqBodyDto(AiCustomerFunction.class, requestMap);
    LambdaUpdateWrapper<AiCustomerFunction> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(AiCustomerFunction::getCreateTime);
    wrapper.eq(
        ObjectUtil.isNotNull(aiCustomerFunction.getTenantId()),
        AiCustomerFunction::getTenantId,
        aiCustomerFunction.getTenantId());
    wrapper.eq(
        ObjectUtil.isNotNull(aiCustomerFunction.getStatus()),
        AiCustomerFunction::getStatus,
        aiCustomerFunction.getStatus());
    wrapper.eq(AiCustomerFunction::getType, "0");
    List<AiCustomerFunction> list = aiCustomerFunctionMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(list);
  }

  @ApiDoc(desc = "getAllFunctionSubscribeList")
  @ShenyuDubboClient("/getAllFunctionSubscribeList")
  @Override
  public Map<String, Object> getAllFunctionSubscribeList(Map<String, Object> requestMap) {
    AiCustomerFunction aiCustomerFunction =
        BsinServiceContext.getReqBodyDto(AiCustomerFunction.class, requestMap);
    LambdaUpdateWrapper<AiCustomerFunction> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(AiCustomerFunction::getCreateTime);
    wrapper.eq(
        ObjectUtil.isNotNull(aiCustomerFunction.getTenantId()),
        AiCustomerFunction::getTenantId,
        aiCustomerFunction.getTenantId());
    wrapper.eq(
        ObjectUtil.isNotNull(aiCustomerFunction.getStatus()),
        AiCustomerFunction::getStatus,
        aiCustomerFunction.getStatus());
    wrapper.eq(AiCustomerFunction::getType, "2");
    // 系统自带基础服务功能
    wrapper.or().eq(AiCustomerFunction::getType, "3");
    List<AiCustomerFunction> list = aiCustomerFunctionMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(list);
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

    AiCustomerFunction aiCustomerFunction =
        BsinServiceContext.getReqBodyDto(AiCustomerFunction.class, requestMap);
    LambdaUpdateWrapper<AiCustomerFunction> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(AiCustomerFunction::getCreateTime);
    wrapper.eq(ObjectUtil.isNotNull(tenantId), AiCustomerFunction::getTenantId, tenantId);
    wrapper.eq(ObjectUtil.isNotNull(merchantNo), AiCustomerFunction::getMerchantNo, merchantNo);
    wrapper.eq(ObjectUtil.isNotNull(customerNo), AiCustomerFunction::getCustomerNo, customerNo);
    wrapper.eq(
        ObjectUtil.isNotNull(aiCustomerFunction.getStatus()),
        AiCustomerFunction::getStatus,
        aiCustomerFunction.getStatus());
    wrapper.ne(
        ObjectUtil.isNotNull(aiCustomerFunction.getType()), AiCustomerFunction::getType, "0");
    // 匹配系统资源
    wrapper.or().eq(AiCustomerFunction::getEditable, false);
    List<AiCustomerFunction> list = aiCustomerFunctionMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(list);
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
    AiCustomerFunction aiCustomerFunction =
        BsinServiceContext.getReqBodyDto(AiCustomerFunction.class, requestMap);

    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<AiCustomerFunction> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<AiCustomerFunction> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(AiCustomerFunction::getCreateTime);
    wrapper.eq(ObjectUtil.isNotNull(tenantId), AiCustomerFunction::getTenantId, tenantId);
    wrapper.eq(ObjectUtil.isNotNull(merchantNo), AiCustomerFunction::getMerchantNo, merchantNo);
    wrapper.eq(ObjectUtil.isNotNull(customerNo), AiCustomerFunction::getCustomerNo, customerNo);
    //    wrapper.ne(
    //        ObjectUtil.isNotNull(aiCustomerFunction.getType()), AiCustomerFunction::getType, "0");
    // 匹配系统资源
    wrapper.or().eq(AiCustomerFunction::getEditable, false);
    // 匹配系统资源: 类型 0、租户上架的功能(可供客户订阅的功能模版) 1、客户订阅服务 2、客户订阅功能 3、系统自待基础服务
    wrapper.or().eq(AiCustomerFunction::getType, "3");
    IPage<AiCustomerFunction> pageList = aiCustomerFunctionMapper.selectPage(page, wrapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  /**
   * 手机验证码登录 SMS verification code
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getLoginVerifyCode")
  @ShenyuDubboClient("/getLoginVerifyCode")
  @Override
  public Map<String, Object> getLoginVerifyCode(Map<String, Object> requestMap) {
    String phone = (String) requestMap.get("phone");
    // 调用login验证码模板发短信

    // 将验证码存在缓存里面 phone:eventType verifycode

    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getMpVerifyCode")
  @ShenyuDubboClient("/getMpVerifyCode")
  @Override
  public Map<String, Object> getMpVerifyCode(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
      }
    }
    // 1.generate 验证码
    String mpVerifyCode = VerficationCodeUtil.getVerficationCode(6);
    // 2.调用公众号推送模版 TODO:

    // 3.将验证码存在缓存里面 mpVerifyCode:customerNo verifycode
    bsinCacheProvider.set("mpVerifyCodeWithCustomerNo:" + customerNo, mpVerifyCode, 120);
    // 测试用
    requestMap.put("mpVerifyCode", mpVerifyCode);
    return RespBodyHandler.setRespBodyDto(requestMap);
  }

  @ApiDoc(desc = "verifyMpCode")
  @ShenyuDubboClient("/verifyMpCode")
  @Override
  public Map<String, Object> verifyMpCode(Map<String, Object> requestMap) {

    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String tenantId = loginUser.getTenantId();
    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      customerNo = loginUser.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
      }
    }
    String mpVerifyCode = (String) requestMap.get("mpVerifyCode");
    if (mpVerifyCode.isEmpty()) {
      throw new BusinessException("100000", "验证码为空！！！");
    }
    if (!bsinCacheProvider.get("mpVerifyCodeWithCustomerNo:" + customerNo).equals(mpVerifyCode)) {
      throw new BusinessException("100000", "验证码错误");
    }
    return RespBodyHandler.RespBodyDto();
  }

}
