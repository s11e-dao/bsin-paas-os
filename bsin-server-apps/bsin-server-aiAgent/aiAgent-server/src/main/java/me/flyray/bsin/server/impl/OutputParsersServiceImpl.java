package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.OutputParsers;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.OutputParsersService;
import me.flyray.bsin.infrastructure.mapper.OutputParsersMapper;
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
 * @description 针对表【ai_output_parsers】的数据库操作Service实现
 * @createDate 2023-12-09 01:50:35
 */

@ShenyuDubboService(path = "/outputParsers", timeout = 6000)
@ApiModule(value = "outputParsers")
@Service
@Slf4j
public class OutputParsersServiceImpl implements OutputParsersService {

  @Autowired private OutputParsersMapper outputParsersMapper;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public OutputParsers add(Map<String, Object> requestMap) {
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
    OutputParsers outputParsers = BsinServiceContext.getReqBodyDto(OutputParsers.class, requestMap);
    outputParsers.setSerialNo(BsinSnowflake.getId());
    outputParsers.setTenantId(tenantId);
    outputParsers.setMerchantNo(merchantNo);
    outputParsers.setCustomerNo(customerNo);
    outputParsersMapper.insert(outputParsers);
    return outputParsers;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String outputParsersNo = MapUtils.getString(requestMap, "serialNo");
    if (outputParsersNo == null) {
      outputParsersNo = MapUtils.getString(requestMap, "outputParsersNo");
    }
    outputParsersMapper.deleteById(outputParsersNo);
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public void edit(Map<String, Object> requestMap) {
    OutputParsers OutputParsers = BsinServiceContext.getReqBodyDto(OutputParsers.class, requestMap);
    outputParsersMapper.updateById(OutputParsers);
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public OutputParsers getDetail(Map<String, Object> requestMap) {
    String outputParsersNo = MapUtils.getString(requestMap, "serialNo");
    if (outputParsersNo == null) {
      outputParsersNo = MapUtils.getString(requestMap, "outputParsersNo");
    }
    OutputParsers outputParsers = outputParsersMapper.selectById(outputParsersNo);
    return outputParsers;
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<OutputParsers> getPageList(Map<String, Object> requestMap) {
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

    OutputParsers outputParsers = BsinServiceContext.getReqBodyDto(OutputParsers.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<OutputParsers> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<OutputParsers> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(OutputParsers::getCreateTime);
    wrapper.eq(OutputParsers::getTenantId, tenantId);
    wrapper.eq(OutputParsers::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), OutputParsers::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(outputParsers.getType()),
        OutputParsers::getType,
        outputParsers.getType());
    wrapper.eq(
            StringUtils.isNotBlank(outputParsers.getStatus()),
        OutputParsers::getStatus,
        outputParsers.getStatus());
    IPage<OutputParsers> pageList = outputParsersMapper.selectPage(page, wrapper);
    return pageList;
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public List<OutputParsers> getList(Map<String, Object> requestMap) {
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

    OutputParsers outputParsers = BsinServiceContext.getReqBodyDto(OutputParsers.class, requestMap);

    LambdaUpdateWrapper<OutputParsers> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(OutputParsers::getCreateTime);
    wrapper.eq(OutputParsers::getTenantId, tenantId);
    wrapper.eq(OutputParsers::getMerchantNo, merchantNo);
    wrapper.eq(StringUtils.isNotBlank(customerNo), OutputParsers::getCustomerNo, customerNo);
    wrapper.eq(
            StringUtils.isNotBlank(outputParsers.getType()),
        OutputParsers::getType,
        outputParsers.getType());
    wrapper.eq(
            StringUtils.isNotBlank(outputParsers.getStatus()),
        OutputParsers::getStatus,
        outputParsers.getStatus());
    List<OutputParsers> embeddingModelList = outputParsersMapper.selectList(wrapper);
    return embeddingModelList;
  }

}
