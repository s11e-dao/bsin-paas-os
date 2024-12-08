package me.flyray.bsin.server.impl;

import static me.flyray.bsin.constants.ResponseCode.*;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.GradeVO;
import me.flyray.bsin.facade.service.CustomerIdentityService;
import me.flyray.bsin.facade.service.TokenParamService;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.biz.AccountBiz;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author leonard
 * @description 针对表【crm_customer_identity(客户身份表)】的数据库操作Service实现
 * @createDate 2024-10-13 00:06:17
 */
@Slf4j
@ShenyuDubboService(path = "/customerIdentity", timeout = 6000)
@ApiModule(value = "customerIdentity")
@Service
public class CustomerIdentityServiceImpl implements CustomerIdentityService {

  @Autowired private CustomerIdentityMapper customerIdentityMapper;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public CustomerIdentity add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    CustomerIdentity customerIdentity =
        BsinServiceContext.getReqBodyDto(CustomerIdentity.class, requestMap);
    customerIdentity.setTenantId(loginUser.getTenantId());
    customerIdentity.setMerchantNo(loginUser.getMerchantNo());
    customerIdentityMapper.insert(customerIdentity);
    return customerIdentity;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    if (customerIdentityMapper.deleteById(serialNo) == 0) {
      throw new BusinessException(GRADE_NOT_EXISTS);
    }
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public CustomerIdentity edit(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    CustomerIdentity customerIdentity =
        BsinServiceContext.getReqBodyDto(CustomerIdentity.class, requestMap);
    customerIdentity.setTenantId(loginUser.getTenantId());
    customerIdentity.setMerchantNo(loginUser.getMerchantNo());
    if (customerIdentityMapper.updateById(customerIdentity) == 0) {
      throw new BusinessException(CUSTOMER_ERROR);
    }
    return customerIdentity;
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public List<CustomerIdentity> getList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    LambdaQueryWrapper<CustomerIdentity> warapper = new LambdaQueryWrapper<>();
    String customerNo = loginUser.getCustomerNo();
    warapper.orderByDesc(CustomerIdentity::getCreateTime);
    warapper.eq(CustomerIdentity::getTenantId, loginUser.getTenantId());
    warapper.eq(CustomerIdentity::getCustomerNo, customerNo);
    List<CustomerIdentity> gradeList = customerIdentityMapper.selectList(warapper);
    return gradeList;
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<?> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<CustomerIdentity> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    CustomerIdentity customerIdentity =
        BsinServiceContext.getReqBodyDto(CustomerIdentity.class, requestMap);
    if (customerIdentity.getCustomerNo() == null) {
      throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
    }
    LambdaQueryWrapper<CustomerIdentity> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(CustomerIdentity::getCreateTime);
    warapper.eq(CustomerIdentity::getTenantId, loginUser.getTenantId());
    warapper.eq(
        StringUtils.isNotEmpty(loginUser.getMerchantNo()),
        CustomerIdentity::getMerchantNo,
        loginUser.getMerchantNo());
    warapper.eq(CustomerIdentity::getCustomerNo, customerIdentity.getCustomerNo());

    IPage<CustomerIdentity> pageList = customerIdentityMapper.selectPage(page, warapper);
    return pageList;
  }

  /**
   * 客户身份权益详情
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public CustomerIdentity getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "customerNo");
    CustomerIdentity customerIdentity = customerIdentityMapper.selectById(serialNo);
    if (customerIdentity == null) {
      throw new BusinessException(CUSTOMER_ERROR);
    }
    return customerIdentity;
  }
}
