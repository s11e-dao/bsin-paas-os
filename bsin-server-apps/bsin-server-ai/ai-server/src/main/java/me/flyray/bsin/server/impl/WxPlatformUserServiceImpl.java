package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.WxPlatformUser;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.WxPlatformUserService;
import me.flyray.bsin.infrastructure.mapper.WxPlatformUserMapper;
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

import java.util.Map;

/**
 * @author bolei
 * @description 针对表【ai_tenant_wxmp_user】的数据库操作Service实现
 * @createDate 2023-04-28 14:02:38
 */
@ShenyuDubboService(path = "/wxPlatformUser", timeout = 6000)
@ApiModule(value = "wxPlatformUser")
@Service
@Slf4j
public class WxPlatformUserServiceImpl implements WxPlatformUserService {

  @Autowired private WxPlatformUserMapper wxPlatformUserMapper;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public WxPlatformUser add(Map<String, Object> requestMap) {
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
    WxPlatformUser wxPlatformUser =
        BsinServiceContext.getReqBodyDto(WxPlatformUser.class, requestMap);
    wxPlatformUser.setSerialNo(BsinSnowflake.getId());
    wxPlatformUser.setTenantId(tenantId);
    wxPlatformUserMapper.insert(wxPlatformUser);
    return wxPlatformUser;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String serialNo = (String) requestMap.get("serialNo");
    wxPlatformUserMapper.deleteById(serialNo);
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public void edit(Map<String, Object> requestMap) {
    WxPlatformUser wxPlatformUser =
        BsinServiceContext.getReqBodyDto(WxPlatformUser.class, requestMap);
    wxPlatformUserMapper.updateById(wxPlatformUser);
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public WxPlatformUser getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    WxPlatformUser wxPlatformUser = wxPlatformUserMapper.selectById(serialNo);
    return wxPlatformUser;
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<WxPlatformUser> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    String tenantId = loginUser.getTenantId();
    String name = (String) requestMap.get("name");
    String phone = (String) requestMap.get("phone");

    WxPlatformUser wxPlatformUser =
        BsinServiceContext.getReqBodyDto(WxPlatformUser.class, requestMap);
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<WxPlatformUser> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<WxPlatformUser> wrapper = new LambdaUpdateWrapper<>();
    wrapper.orderByDesc(WxPlatformUser::getCreateTime);
    wrapper.eq(WxPlatformUser::getTenantId, tenantId);
    wrapper.eq(
        StringUtils.isNotBlank(wxPlatformUser.getSerialNo()),
        WxPlatformUser::getSerialNo,
        wxPlatformUser.getSerialNo());
    wrapper.eq(StringUtils.isNotBlank(name), WxPlatformUser::getName, wxPlatformUser.getName());
    wrapper.eq(StringUtils.isNotBlank(phone), WxPlatformUser::getPhone, wxPlatformUser.getPhone());
    wrapper.eq(
            StringUtils.isNotBlank(wxPlatformUser.getStatus()),
        WxPlatformUser::getStatus,
        wxPlatformUser.getStatus());
    IPage<WxPlatformUser> pageList = wxPlatformUserMapper.selectPage(page, wrapper);
    return pageList;
  }

}
