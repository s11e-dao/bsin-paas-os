package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.domain.entity.Merchant;
import me.flyray.bsin.domain.entity.Store;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.enums.StoreType;
import me.flyray.bsin.domain.response.UserResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.StoreService;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.mapper.StoreMapper;
import me.flyray.bsin.security.authentication.AuthenticationProvider;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/28 16:42
 * @desc
 */
@Slf4j
@ShenyuDubboService(path = "/store", timeout = 6000)
@ApiModule(value = "store")
@Service
public class StoreServiceImpl implements StoreService {

  @Value("${bsin.security.authentication-secretKey}")
  private String authSecretKey;
  @Value("${bsin.security.authentication-expiration}")
  private int authExpiration;

  @Autowired private StoreMapper storeMapper;

  @DubboReference(version = "${dubbo.provider.version}")
  private UserService userService;

  @ShenyuDubboClient("/openStore")
  @ApiDoc(desc = "openStore")
  @Override
  public Store openStore(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Store store = BsinServiceContext.getReqBodyDto(Store.class, requestMap);
    if (store.getTenantId() == null) {
      store.setTenantId(loginUser.getTenantId());
      if (store.getTenantId() == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }
    if (store.getMerchantNo() == null) {
      store.setMerchantNo(loginUser.getMerchantNo());
      if (store.getMerchantNo() == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NO_IS_NULL);
      }
    }
    store.setSerialNo(BsinSnowflake.getId());
    storeMapper.insert(store);
    return store;
  }


  @Override
  public Map<String, Object> login(Map<String, Object> requestMap) {
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    String username = MapUtils.getString(requestMap, "username");
    String password = MapUtils.getString(requestMap, "password");
    // 查询商户信息
    LambdaQueryWrapper<Store> warapper = new LambdaQueryWrapper<>();
    warapper.eq(Store::getStoreName, username);
    Store store = storeMapper.selectOne(warapper);

    // TODO 判断是否是商户员工
    if (store == null) {
      throw new BusinessException(ResponseCode.MERCHANT_NOT_EXISTS);
    }

    if (!store.getPassword().equals(password)) {
      throw new BusinessException(ResponseCode.USERNAME_PASSWORD_ERROR);
    }
    LoginUser loginUser = new LoginUser();
    BeanUtil.copyProperties(store, loginUser);

    // 查询upms用户
    Map res = new HashMap<>();
    // userService
    SysUser sysUserReq = new SysUser();
    sysUserReq.setTenantId(tenantId);
    BeanUtil.copyProperties(requestMap, sysUserReq);

    try {
      UserResp userResp = userService.getUserInfo(sysUserReq);
      Map data = new HashMap();
      BeanUtil.copyProperties(userResp, data);
      res.putAll(data);
      SysUser sysUser = userResp.getSysUser();
      // 商户认证之后不为空
      if (sysUser != null) {
        loginUser.setUserId(sysUser.getUserId());
      }
    } catch (Exception e) {
      log.warn("获取用户信息失败: {}", e.getMessage());
      // 用户信息获取失败不影响商户登录流程
    }

    loginUser.setUsername(store.getStoreName());
    loginUser.setMerchantNo(store.getMerchantNo());
    loginUser.setBizRoleType(BizRoleType.STORE.getCode());
    loginUser.setBizRoleTypeNo(store.getSerialNo());
    String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
    res.put("storeInfo", store);
    res.put("token", token);
    // 查询商户信息
    return res;
  }


  @ShenyuDubboClient("/delete")
  @ApiDoc(desc = "delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    storeMapper.deleteById(serialNo);
  }

  @ShenyuDubboClient("/edit")
  @ApiDoc(desc = "edit")
  @Override
  public void edit(Map<String, Object> requestMap) {
    Store store = BsinServiceContext.getReqBodyDto(Store.class, requestMap);
    if (StoreType.MAIN_STORE.getCode().equals(store.getType())) {
      setMainStore(requestMap);
    }
    if (storeMapper.updateById(store) == 0) {
      throw new BusinessException(ResponseCode.DATA_BASE_UPDATE_FAILED);
    }
  }

  /**
   * 设置总店
   *
   * @param requestMap
   */
  @ApiDoc(desc = "setMainStore")
  @ShenyuDubboClient("/setMainStore")
  @Override
  public Store setMainStore(Map<String, Object> requestMap) {
    Store store = BsinServiceContext.getReqBodyDto(Store.class, requestMap);
    LambdaUpdateWrapper<Store> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
    lambdaUpdateWrapper.set(Store::getType, StoreType.MAIN_STORE.getCode());
    lambdaUpdateWrapper.eq(Store::getSerialNo, store.getSerialNo());
    if (StoreType.MAIN_STORE.getCode().equals(store.getType())) {
      storeMapper.update(
          null,
          new LambdaUpdateWrapper<>(Store.class)
              .set(Store::getType, StoreType.OTHER.getCode())
              .eq(
                  StringUtils.isNotBlank(store.getMerchantNo()),
                  Store::getMerchantNo,
                  store.getMerchantNo()));
    }
    if (storeMapper.update(null, lambdaUpdateWrapper) == 0) {
      throw new BusinessException(ResponseCode.DATA_BASE_UPDATE_FAILED);
    }
    return store;
  }

  @Override
  public List<Store> getListByIds(Map<String, Object> requestMap) {
    List<String> storeIds = (List<String>) requestMap.get("storeIds");
    if (storeIds.size() < 1) {
      throw new BusinessException("200000", "请求参数不能为空！");
    }
    List<Store> storeList = storeMapper.selectList(new LambdaQueryWrapper<Store>()
            .in(Store::getSerialNo, storeIds));
    return storeList;
  }

  @ShenyuDubboClient("/getPageList")
  @ApiDoc(desc = "getPageList")
  @Override
  public IPage<?> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Store store = BsinServiceContext.getReqBodyDto(Store.class, requestMap);
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
      if (merchantNo == null) {
        merchantNo = loginUser.getTenantMerchantNo();
      }
    }
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<Store> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaQueryWrapper<Store> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(Store::getCreateTime);
    warapper.eq(Store::getTenantId, loginUser.getTenantId());
    warapper.eq(StringUtils.isNotEmpty(merchantNo), Store::getMerchantNo, merchantNo);
    IPage<Store> pageList = storeMapper.selectPage(page, warapper);
    return pageList;
  }

  @ShenyuDubboClient("/getDetail")
  @ApiDoc(desc = "getDetail")
  @Override
  public Store getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    if(StringUtils.isEmpty(serialNo)){
      serialNo = MapUtils.getString(requestMap, "storeNo");
    }
    Store store = storeMapper.selectById(serialNo);
    return store;
  }

  /**
   * @param requestMap
   * @return
   */
  @ShenyuDubboClient("/getPageListByBizTypeCityCode")
  @ApiDoc(desc = "getPageListByBizTypeCityCode")
  @Override
  public IPage<?> getPageListByBizTypeCityCode(Map<String, Object> requestMap) {
    // 获取分页参数
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<Store> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    String businessTypeNo = MapUtils.getString(requestMap, "parentNo");
    if(StringUtils.isEmpty(businessTypeNo)){
      // 获取查询参数
      businessTypeNo = MapUtils.getString(requestMap, "businessTypeNo");
    }
    String cityCode = MapUtils.getString(requestMap, "cityCode");
    
    // 调用Mapper查询
    IPage<Store> result = storeMapper.getPageListByBizTypeCityCode(page, businessTypeNo, cityCode);
    
    return result;
  }

}
