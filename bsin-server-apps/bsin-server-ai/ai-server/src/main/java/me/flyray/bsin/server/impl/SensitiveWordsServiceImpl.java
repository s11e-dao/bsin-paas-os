package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.AiCustomerFunction;
import me.flyray.bsin.domain.domain.SensitiveWords;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.SensitiveWordsService;
import me.flyray.bsin.infrastructure.mapper.SensitiveWordsMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.AccountAvailableResourcesBiz;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leonard
 * @description 针对表【ai_sensitive_words】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */
@ShenyuDubboService(path = "/sensitiveWords", timeout = 6000)
@ApiModule(value = "sensitiveWords")
@Service
@Slf4j
public class SensitiveWordsServiceImpl implements SensitiveWordsService {

  @Autowired private SensitiveWordsMapper sensitiveWordsMapper;
  @Autowired private AccountAvailableResourcesBiz accountAvailableResourcesBiz;

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
      if (customerNo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      }
    }
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
      if (tenantId == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }
    SensitiveWords sensitiveWords =
        BsinServiceContext.getReqBodyDto(SensitiveWords.class, requestMap);
    sensitiveWords.setSerialNo(BsinSnowflake.getId());
    sensitiveWords.setTenantId(tenantId);
    sensitiveWords.setMerchantNo(merchantNo);
    sensitiveWords.setCustomerNo(customerNo);
    AiCustomerFunction validFunction =
        accountAvailableResourcesBiz.functionServiceCheck(tenantId, merchantNo, customerNo);

    // 查询出当前客户已经创建的资源数量
    LambdaUpdateWrapper<SensitiveWords> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(SensitiveWords::getTenantId, tenantId);
    wrapper.eq(SensitiveWords::getMerchantNo, merchantNo);
    wrapper.eq(SensitiveWords::getCustomerNo, customerNo);
    List<SensitiveWords> sensitiveWordsList = sensitiveWordsMapper.selectList(wrapper);
    if (sensitiveWordsList.size() >= validFunction.getSensitiveWordsNum()) {
      throw new BusinessException(
          "100000",
          "可创建的敏感词资源超过限制："
              + sensitiveWordsList.size()
              + ">="
              + validFunction.getSensitiveWordsNum());
    }
    sensitiveWordsMapper.insert(sensitiveWords);
    return RespBodyHandler.setRespBodyDto(sensitiveWords);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    sensitiveWordsMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    SensitiveWords sensitiveWords =
        BsinServiceContext.getReqBodyDto(SensitiveWords.class, requestMap);
    sensitiveWordsMapper.updateById(sensitiveWords);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    SensitiveWords sensitiveWords = sensitiveWordsMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(sensitiveWords);
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
    String status = MapUtils.getString(requestMap, "status");

    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<SensitiveWords> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<SensitiveWords> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(SensitiveWords::getCreateTime);
    wrapper.eq(SensitiveWords::getTenantId, tenantId);
    wrapper.eq(SensitiveWords::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), SensitiveWords::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(type), SensitiveWords::getType, type);
    wrapper.eq(StringUtils.isNotBlank(status), SensitiveWords::getStatus, status);
    //    // 匹配系统资源
    //    wrapper.or().eq(SensitiveWords::getEditable, false);
    IPage<SensitiveWords> pageList = sensitiveWordsMapper.selectPage(page, wrapper);
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
      //      if (customerNo == null) {
      //        throw new BusinessException(ResponseCode.CUSTOMER_NO_NOT_ISNULL);
      //      }
    }
    String tenantId = loginUser.getTenantId();
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
      if (tenantId == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }

    String type = MapUtils.getString(requestMap, "type");
    String status = MapUtils.getString(requestMap, "status");

    LambdaUpdateWrapper<SensitiveWords> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(SensitiveWords::getCreateTime);
    wrapper.eq(SensitiveWords::getTenantId, tenantId);
    wrapper.eq(SensitiveWords::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), SensitiveWords::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(type), SensitiveWords::getType, type);
    wrapper.eq(StringUtils.isNotBlank(status), SensitiveWords::getStatus, status);
    //    // 匹配系统资源
    //    wrapper.or().eq(SensitiveWords::getEditable, false);
    List<SensitiveWords> sensitiveWordsList = sensitiveWordsMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(sensitiveWordsList);
  }

}
