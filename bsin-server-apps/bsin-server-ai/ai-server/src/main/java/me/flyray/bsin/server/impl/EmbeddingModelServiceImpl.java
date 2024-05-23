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
import me.flyray.bsin.domain.domain.EmbeddingModel;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.EmbeddingModelService;
import me.flyray.bsin.infrastructure.mapper.EmbeddingModelMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leonard
 * @description 针对表【ai_embedding_model】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */

@ShenyuDubboService(path = "/embeddingModel", timeout = 6000)
@ApiModule(value = "embeddingModel")
@Service
@Slf4j
public class EmbeddingModelServiceImpl implements EmbeddingModelService {

  @Value("${bsin.ai.aesKey}")
  private String aesKey;

  @Autowired private EmbeddingModelMapper embeddingModelMapper;

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
    EmbeddingModel embeddingModel =
        BsinServiceContext.getReqBodyDto(EmbeddingModel.class, requestMap);
    embeddingModel.setSerialNo(BsinSnowflake.getId());
    embeddingModel.setTenantId(tenantId);
    embeddingModel.setMerchantNo(merchantNo);
    embeddingModel.setCustomerNo(customerNo);

    if (embeddingModel.getApiKey() != null) {
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      embeddingModel.setApiKey(aes.encryptHex(embeddingModel.getApiKey()));
    }
    embeddingModelMapper.insert(embeddingModel);
    return RespBodyHandler.setRespBodyDto(embeddingModel);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    embeddingModelMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    EmbeddingModel EmbeddingModel =
        BsinServiceContext.getReqBodyDto(EmbeddingModel.class, requestMap);
    if (EmbeddingModel.getApiKey() != null) {
      SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
      EmbeddingModel.setApiKey(aes.encryptHex(EmbeddingModel.getApiKey()));
    }
    embeddingModelMapper.updateById(EmbeddingModel);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    EmbeddingModel embeddingModel = embeddingModelMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(embeddingModel);
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

    EmbeddingModel embeddingModel =
        BsinServiceContext.getReqBodyDto(EmbeddingModel.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<EmbeddingModel> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<EmbeddingModel> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(EmbeddingModel::getCreateTime);
    wrapper.eq(EmbeddingModel::getTenantId, tenantId);
    wrapper.eq(EmbeddingModel::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), EmbeddingModel::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(embeddingModel.getSerialNo()),
        EmbeddingModel::getSerialNo,
        embeddingModel.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(embeddingModel.getType()),
        EmbeddingModel::getType,
        embeddingModel.getType());
    wrapper.eq(
            StringUtils.isNotBlank(embeddingModel.getStatus()),
        EmbeddingModel::getStatus,
        embeddingModel.getStatus());
    // 匹配系统资源
    wrapper.or().eq(EmbeddingModel::getEditable, false);
    IPage<EmbeddingModel> pageList = embeddingModelMapper.selectPage(page, wrapper);
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

    EmbeddingModel embeddingModel =
        BsinServiceContext.getReqBodyDto(EmbeddingModel.class, requestMap);

    LambdaUpdateWrapper<EmbeddingModel> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(EmbeddingModel::getCreateTime);
    wrapper.eq(EmbeddingModel::getTenantId, tenantId);
    wrapper.eq(EmbeddingModel::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), EmbeddingModel::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(embeddingModel.getSerialNo()),
        EmbeddingModel::getSerialNo,
        embeddingModel.getSerialNo());
    wrapper.eq(
            StringUtils.isNotBlank(embeddingModel.getType()),
        EmbeddingModel::getType,
        embeddingModel.getType());
    wrapper.eq(
            StringUtils.isNotBlank(embeddingModel.getStatus()),
        EmbeddingModel::getStatus,
        embeddingModel.getStatus());
    // 匹配系统资源
    wrapper.or().eq(EmbeddingModel::getEditable, false);
    List<EmbeddingModel> embeddingModelList = embeddingModelMapper.selectList(wrapper);
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
    LambdaQueryWrapper<EmbeddingModel> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(EmbeddingModel::getTenantId, tenantId);
    wrapper.eq(StringUtils.isNotBlank(customerNo), EmbeddingModel::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(merchantNo), EmbeddingModel::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(type), EmbeddingModel::getType, type);
    wrapper.eq(EmbeddingModel::getDefaultFlag, true);
    EmbeddingModel embeddingModel = embeddingModelMapper.selectOne(wrapper);
    return RespBodyHandler.setRespBodyDto(embeddingModel);
  }

  @ApiDoc(desc = "setDefault")
  @ShenyuDubboClient("/setDefault")
  @Override
  public Map<String, Object> setDefault(Map<String, Object> requestMap) {
    EmbeddingModel embeddingModel =
        BsinServiceContext.getReqBodyDto(EmbeddingModel.class, requestMap);
    LambdaUpdateWrapper<EmbeddingModel> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.set(EmbeddingModel::getDefaultFlag, embeddingModel.getDefaultFlag());
    lambdaUpdateWrapper.eq(EmbeddingModel::getSerialNo, embeddingModel.getSerialNo());
    if (Boolean.TRUE.equals(embeddingModel.getDefaultFlag())) {
      embeddingModelMapper.update(
          null,
          new LambdaUpdateWrapper<>(EmbeddingModel.class)
              .set(EmbeddingModel::getDefaultFlag, Boolean.FALSE)
              .eq(
                      StringUtils.isNotBlank(embeddingModel.getMerchantNo()),
                  EmbeddingModel::getMerchantNo,
                  embeddingModel.getMerchantNo())
              .eq(
                      StringUtils.isNotBlank(embeddingModel.getCustomerNo()),
                  EmbeddingModel::getCustomerNo,
                  embeddingModel.getCustomerNo()));
    }
    embeddingModelMapper.update(null, lambdaUpdateWrapper);
    return RespBodyHandler.RespBodyDto();
  }

}
