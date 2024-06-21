package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.PromptTemplateParam;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.PromptTemplateService;
import me.flyray.bsin.infrastructure.mapper.PromptTemplateMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
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

/**
 * @author leonard
 * @description 针对表【ai_prompt_template】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */

@ShenyuDubboService(path = "/promptTemplate", timeout = 6000)
@ApiModule(value = "promptTemplate")
@Service
@Slf4j
public class PromptTemplateServiceImpl implements PromptTemplateService {

  @Autowired private PromptTemplateMapper promptTemplateMapper;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public PromptTemplateParam add(Map<String, Object> requestMap) {
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
    PromptTemplateParam promptTemplateParam =
        BsinServiceContext.getReqBodyDto(PromptTemplateParam.class, requestMap);
    promptTemplateParam.setSerialNo(BsinSnowflake.getId());
    promptTemplateParam.setTenantId(tenantId);
    promptTemplateParam.setMerchantNo(merchantNo);
    promptTemplateParam.setCustomerNo(customerNo);
    promptTemplateParam.setType("0");
    promptTemplateParam.setEditable(true);
    promptTemplateMapper.insert(promptTemplateParam);
    return promptTemplateParam;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String promptTemplateNo = MapUtils.getString(requestMap, "serialNo");
    if (promptTemplateNo == null) {
      promptTemplateNo = MapUtils.getString(requestMap, "promptTemplateNo");
    }
    promptTemplateMapper.deleteById(promptTemplateNo);
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public void edit(Map<String, Object> requestMap) {
    PromptTemplateParam PromptTemplateParam =
        BsinServiceContext.getReqBodyDto(PromptTemplateParam.class, requestMap);
    promptTemplateMapper.updateById(PromptTemplateParam);
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public PromptTemplateParam getDetail(Map<String, Object> requestMap) {
    String promptTemplateNo = MapUtils.getString(requestMap, "serialNo");
    if (promptTemplateNo == null) {
      promptTemplateNo = MapUtils.getString(requestMap, "promptTemplateNo");
    }
    PromptTemplateParam promptTemplateParam = promptTemplateMapper.selectById(promptTemplateNo);
    return promptTemplateParam;
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<PromptTemplateParam> getPageList(Map<String, Object> requestMap) {
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

    PromptTemplateParam promptTemplateParam =
        BsinServiceContext.getReqBodyDto(PromptTemplateParam.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<PromptTemplateParam> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<PromptTemplateParam> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(PromptTemplateParam::getCreateTime);
    wrapper.eq(PromptTemplateParam::getTenantId, tenantId);
    wrapper.eq(PromptTemplateParam::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), PromptTemplateParam::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(promptTemplateParam.getType()),
        PromptTemplateParam::getType,
        promptTemplateParam.getType());
    wrapper.eq(
            StringUtils.isNotBlank(promptTemplateParam.getStatus()),
        PromptTemplateParam::getStatus,
        promptTemplateParam.getStatus());
    // 匹配系统资源
    wrapper.or().eq(PromptTemplateParam::getEditable, false);
    IPage<PromptTemplateParam> pageList = promptTemplateMapper.selectPage(page, wrapper);
    return pageList;
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public List<PromptTemplateParam> getList(Map<String, Object> requestMap) {
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

    PromptTemplateParam promptTemplateParam =
        BsinServiceContext.getReqBodyDto(PromptTemplateParam.class, requestMap);

    LambdaUpdateWrapper<PromptTemplateParam> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(PromptTemplateParam::getCreateTime);
    wrapper.eq(PromptTemplateParam::getTenantId, tenantId);
    wrapper.eq(PromptTemplateParam::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), PromptTemplateParam::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(promptTemplateParam.getType()),
        PromptTemplateParam::getType,
        promptTemplateParam.getType());
    wrapper.eq(
            StringUtils.isNotBlank(promptTemplateParam.getStatus()),
        PromptTemplateParam::getStatus,
        promptTemplateParam.getStatus());
    // 匹配系统资源
    wrapper.or().eq(PromptTemplateParam::getEditable, false);
    List<PromptTemplateParam> embeddingModelList = promptTemplateMapper.selectList(wrapper);
    return embeddingModelList;
  }

  @ApiDoc(desc = "getDefault")
  @ShenyuDubboClient("/getDefault")
  @Override
  public PromptTemplateParam getDefault(Map<String, Object> requestMap) {
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
    LambdaQueryWrapper<PromptTemplateParam> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(PromptTemplateParam::getTenantId, tenantId);
    wrapper.eq(StringUtils.isNotBlank(customerNo), PromptTemplateParam::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(merchantNo), PromptTemplateParam::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(type), PromptTemplateParam::getType, type);
    wrapper.eq(PromptTemplateParam::getDefaultFlag, true);
    PromptTemplateParam promptTemplateParam = promptTemplateMapper.selectOne(wrapper);
    return promptTemplateParam;
  }

  @ApiDoc(desc = "setDefault")
  @ShenyuDubboClient("/setDefault")
  @Override
  public void setDefault(Map<String, Object> requestMap) {
    PromptTemplateParam promptTemplateParam =
        BsinServiceContext.getReqBodyDto(PromptTemplateParam.class, requestMap);
    LambdaUpdateWrapper<PromptTemplateParam> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.set(
        PromptTemplateParam::getDefaultFlag, promptTemplateParam.getDefaultFlag());
    lambdaUpdateWrapper.eq(PromptTemplateParam::getSerialNo, promptTemplateParam.getSerialNo());
    if (Boolean.TRUE.equals(promptTemplateParam.getDefaultFlag())) {
      promptTemplateMapper.update(
          null,
          new LambdaUpdateWrapper<>(PromptTemplateParam.class)
              .set(PromptTemplateParam::getDefaultFlag, Boolean.FALSE)
              .eq(
                      StringUtils.isNotBlank(promptTemplateParam.getMerchantNo()),
                  PromptTemplateParam::getMerchantNo,
                  promptTemplateParam.getMerchantNo())
              .eq(
                      StringUtils.isNotBlank(promptTemplateParam.getCustomerNo()),
                  PromptTemplateParam::getCustomerNo,
                  promptTemplateParam.getCustomerNo()));
    }
    promptTemplateMapper.update(null, lambdaUpdateWrapper);

  }

}
