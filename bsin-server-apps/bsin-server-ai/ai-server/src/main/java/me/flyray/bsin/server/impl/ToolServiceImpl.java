package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.domain.entity.CopilotInfo;
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
import me.flyray.bsin.domain.entity.ToolInfo;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.ToolService;
import me.flyray.bsin.infrastructure.mapper.ToolMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

/**
 * @author leonard
 * @description 针对表【ai_tool】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */

@ShenyuDubboService(path = "/tool", timeout = 6000)
@ApiModule(value = "tool")
@Service
@Slf4j
public class ToolServiceImpl implements ToolService {

  @Autowired private ToolMapper toolMapper;

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
    ToolInfo toolInfo = BsinServiceContext.getReqBodyDto(ToolInfo.class, requestMap);
    toolInfo.setSerialNo(BsinSnowflake.getId());
    toolInfo.setTenantId(tenantId);
    toolInfo.setMerchantNo(merchantNo);
    toolInfo.setCustomerNo(customerNo);
    toolMapper.insert(toolInfo);
    return RespBodyHandler.setRespBodyDto(toolInfo);
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Map<String, Object> delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    toolMapper.deleteById(serialNo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    ToolInfo toolInfo = BsinServiceContext.getReqBodyDto(ToolInfo.class, requestMap);
    toolMapper.updateById(toolInfo);
    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    ToolInfo toolInfo = toolMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(toolInfo);
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
    Page<ToolInfo> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<ToolInfo> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(ToolInfo::getCreateTime);
    wrapper.eq(ToolInfo::getTenantId, tenantId);
    wrapper.eq(ToolInfo::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), ToolInfo::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(type), ToolInfo::getType, type);
    wrapper.eq(StringUtils.isNotBlank(status), ToolInfo::getStatus, status);
    // 匹配系统资源
    wrapper.or().eq(ToolInfo::getEditable, false);
    IPage<ToolInfo> pageList = toolMapper.selectPage(page, wrapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");

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

    LambdaUpdateWrapper<ToolInfo> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(ToolInfo::getCreateTime);
    wrapper.eq(ToolInfo::getTenantId, tenantId);
    // 区分租户平台和商户登录情况判断
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        // 查询平台租户的数据
        wrapper.isNull(ToolInfo::getMerchantNo);
      }else {
        wrapper.eq(ToolInfo::getMerchantNo, merchantNo);
      }
    }
    wrapper.eq(StringUtils.isNotBlank(customerNo), ToolInfo::getCustomerNo, customerNo);
    wrapper.eq(StringUtils.isNotBlank(type), ToolInfo::getType, type);
    wrapper.eq(StringUtils.isNotBlank(status), ToolInfo::getStatus, status);
    // 匹配系统资源
    wrapper.or().eq(ToolInfo::getEditable, false);
    List<ToolInfo> embeddingModelList = toolMapper.selectList(wrapper);
    return RespBodyHandler.setRespBodyListDto(embeddingModelList);
  }

}
