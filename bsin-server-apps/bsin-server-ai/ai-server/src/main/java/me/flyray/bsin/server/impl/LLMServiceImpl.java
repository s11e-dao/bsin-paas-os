package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.LLMParam;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.LLMService;
import me.flyray.bsin.infrastructure.mapper.LLMMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leonard
 * @description 针对表【ai_llm】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */

@ShenyuDubboService(path = "/lLLM", timeout = 6000)
@ApiModule(value = "lLLM")
@Service
@Slf4j
public class LLMServiceImpl implements LLMService {

  @Autowired private LLMMapper llmMapper;

  @Value("${bsin.ai.aesKey}")
  private String aesKey;

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
    LLMParam llmParam = BsinServiceContext.getReqBodyDto(LLMParam.class, requestMap);
    llmParam.setSerialNo(BsinSnowflake.getId());
    llmParam.setTenantId(tenantId);
    llmParam.setMerchantNo(merchantNo);
    llmParam.setCustomerNo(customerNo);

    if (llmParam.getApiKey() != null) {
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      llmParam.setApiKey(aes.encryptHex(llmParam.getApiKey()));
    }
    llmMapper.insert(llmParam);
    return RespBodyHandler.setRespBodyDto(llmParam);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    llmMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    LLMParam llmParam = BsinServiceContext.getReqBodyDto(LLMParam.class, requestMap);
    if (llmParam.getApiKey() != null) {
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      llmParam.setApiKey(aes.encryptHex(llmParam.getApiKey()));
    }
    llmMapper.updateById(llmParam);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    LLMParam llmParam = llmMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(llmParam);
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
    Page<LLMParam> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<LLMParam> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(LLMParam::getCreateTime);
    wrapper.eq(LLMParam::getTenantId, tenantId);
    wrapper.eq(LLMParam::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), LLMParam::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(type), LLMParam::getType, type);
    wrapper.eq(StringUtils.isNotBlank(status), LLMParam::getStatus, status);
    // 匹配系统资源
    wrapper.or().eq(LLMParam::getEditable, false);
    IPage<LLMParam> pageList = llmMapper.selectPage(page, wrapper);
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
    if (tenantId == null) {
      tenantId = loginUser.getTenantId();
      if (tenantId == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }

    String type = MapUtils.getString(requestMap, "type");
    String status = MapUtils.getString(requestMap, "status");

    LambdaUpdateWrapper<LLMParam> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(LLMParam::getCreateTime);
    wrapper.eq(LLMParam::getTenantId, tenantId);
    wrapper.eq(LLMParam::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), LLMParam::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(type), LLMParam::getType, type);
    wrapper.eq(StringUtils.isNotBlank(status), LLMParam::getStatus, status);
    // 匹配系统资源
    wrapper.or().eq(LLMParam::getEditable, false);
    List<LLMParam> embeddingModelList = llmMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(embeddingModelList);
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
    // type为1是商户品牌官，为2是个人数字分身助手
    String type = MapUtils.getString(requestMap, "type");
    LambdaQueryWrapper<LLMParam> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(LLMParam::getTenantId, tenantId);
    wrapper.eq(StringUtils.isNotBlank(customerNo), LLMParam::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(merchantNo), LLMParam::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(type), LLMParam::getType, type);
    wrapper.eq(LLMParam::getDefaultFlag, true);
    LLMParam llmParam = llmMapper.selectOne(wrapper);
    return RespBodyHandler.setRespBodyDto(llmParam);
  }

  @ApiDoc(desc = "setDefault")
  @ShenyuDubboClient("/setDefault")
  @Override
  public Map<String, Object> setDefault(Map<String, Object> requestMap) {
    LLMParam llmParam = BsinServiceContext.getReqBodyDto(LLMParam.class, requestMap);
    LambdaUpdateWrapper<LLMParam> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.set(LLMParam::getDefaultFlag, llmParam.getDefaultFlag());
    lambdaUpdateWrapper.eq(LLMParam::getSerialNo, llmParam.getSerialNo());
    if (Boolean.TRUE.equals(llmParam.getDefaultFlag())) {
      llmMapper.update(
          null,
          new LambdaUpdateWrapper<>(LLMParam.class)
              .set(LLMParam::getDefaultFlag, Boolean.FALSE)
              .eq(
                      StringUtils.isNotBlank(llmParam.getMerchantNo()),
                  LLMParam::getMerchantNo,
                  llmParam.getMerchantNo())
              .eq(
                      StringUtils.isNotBlank(llmParam.getCustomerNo()),
                  LLMParam::getCustomerNo,
                  llmParam.getCustomerNo()));
    }
    llmMapper.update(null, lambdaUpdateWrapper);
    return RespBodyHandler.RespBodyDto();
  }

}
