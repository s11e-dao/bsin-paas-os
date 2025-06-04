package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.*;
import me.flyray.bsin.domain.request.SysUserDTO;
import me.flyray.bsin.domain.response.UserResp;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.*;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.redis.provider.BsinCacheProvider;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.text.CharSequenceUtil.NULL;
import static me.flyray.bsin.constants.ResponseCode.*;

/**
 * @author bolei
 * @date 2023/6/28 16:41
 * @desc
 */
@Slf4j
@ShenyuDubboService(path = "/merchant", timeout = 6000)
@ApiModule(value = "merchant")
@Service
public class MerchantServiceImpl implements MerchantService {

  @Autowired
  private BsinServiceInvoke bsinServiceInvoke;

  @Value("${bsin.security.authentication-secretKey}")
  private String authSecretKey;

  @Value("${bsin.security.authentication-expiration}")
  private int authExpiration;

  @Autowired public MerchantMapper merchantMapper;
  @Autowired private CustomerIdentityMapper customerIdentityMapper;
  @Autowired public MerchantSubscribeJournalMapper merchantSubscribeJournalMapper;
  @Autowired private MemberMapper memberMapper;

  @DubboReference(version = "${dubbo.provider.version}")
  private CustomerService customerService;

  @DubboReference(version = "${dubbo.provider.version}")
  private UserService userService;

  @DubboReference(version = "${dubbo.provider.version}")
  private StoreService storeService;

  /**
   * 在租户下注册商户信息 1、添加商户信息
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "register")
  @ShenyuDubboClient("/register")
  @Override
  @Transactional
  public void register(Map<String, Object> requestMap) {
    Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
    String mpVerifyCode = MapUtils.getString(requestMap, "verifyCode");
    String username = MapUtils.getString(requestMap, "username");

    String registerMethod = MapUtils.getString(requestMap, "registerMethod");
    String merchantNo = BsinSnowflake.getId();
    if (!"operatorRegister".equals(registerMethod)) {
      // BsinCopilot验证码注册逻辑
      String openIdWithMpVerifyCode =
          BsinCacheProvider.get("crm", "openIdWithMpVerifyCode:" + mpVerifyCode);
      if (openIdWithMpVerifyCode == null) {
        throw new BusinessException("100000", "验证码错误");
      }
      BsinCacheProvider.put(
          "crm", "bsinCopilotCustomerNoWithOpenId:" + openIdWithMpVerifyCode, merchantNo);
      BsinCacheProvider.put(
          "crm", "bsinCopilotUsernameWithOpenId:" + openIdWithMpVerifyCode, username);
    }
    // 商户名称和登录名称分开
    merchant.setMerchantName(MapUtils.getString(requestMap, "merchantName"));
    // 针对copilot微信分身产品，用户注册无需审核，可直接使用基础产品， 若前端注册消息中带 无需审核字段为 true，注册时直接审核
    boolean registerNotNeedAudit = false;
    String registerNotNeedAuditStr = MapUtils.getString(requestMap, "registerNotNeedAudit");
    if (registerNotNeedAuditStr != null) {
      registerNotNeedAudit = Boolean.parseBoolean(registerNotNeedAuditStr);
    }

    merchant.setType(CustomerType.PERSONAL.getCode());
    addMerchant(merchant, registerNotNeedAudit, NULL);
  }

  /**
   * 1、查询商户信息
   *
   * <p>2、查询upms用户
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "login")
  @ShenyuDubboClient("/login")
  @Override
  public Map<String, Object> login(Map<String, Object> requestMap) {
    String username = MapUtils.getString(requestMap, "username");
    String password = MapUtils.getString(requestMap, "password");
    // 查询商户信息
    LambdaQueryWrapper<Merchant> warapper = new LambdaQueryWrapper<>();
    warapper.eq(Merchant::getUsername, username);
    Merchant merchant = merchantMapper.selectOne(warapper);

    // TODO 判断是否是商户员工
    if (merchant == null) {
      throw new BusinessException(ResponseCode.MERCHANT_NOT_EXISTS);
    }

    if (!merchant.getPassword().equals(password)) {
      throw new BusinessException(ResponseCode.USERNAME_PASSWORD_ERROR);
    }
    LoginUser loginUser = new LoginUser();
    BeanUtil.copyProperties(merchant, loginUser);

    // 查询upms用户
    Map res = new HashMap<>();
    // userService
    SysUser sysUserReq = new SysUser();
    BeanUtil.copyProperties(requestMap, sysUserReq);
    UserResp userResp = userService.getUserInfo(sysUserReq);
    Map data = new HashMap();
    BeanUtil.copyProperties(userResp, data);
    res.putAll(data);
    SysUser sysUser = userResp.getSysUser();
    // 商户认证之后不为空
    if (sysUser != null) {
      loginUser.setUserId(sysUser.getUserId());
    }
    loginUser.setUsername(merchant.getUsername());
    loginUser.setPhone(merchant.getPhone());
    loginUser.setMerchantNo(merchant.getSerialNo());
    loginUser.setBizRoleType(BizRoleType.MERCHANT.getCode());
    loginUser.setBizRoleTypeNo(merchant.getSerialNo());
    String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
    res.put("merchantInfo", merchant);
    res.put("token", token);
    // 查询商户信息
    return res;
  }

  /**
   * 付费认证，一年认证一次
   *
   * <p>1、付费
   *
   * <p>2、为商户添加基础的功能（添加应用角色，为角色添加菜单，将角色授权给岗位）
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "authentication")
  @ShenyuDubboClient("/authentication")
  @Override
  public void authentication(Map<String, Object> requestMap) {
    String merchantNo = LoginInfoContextHelper.getMerchantNo();
    Merchant merchantReq = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
    Merchant merchant = merchantMapper.selectById(merchantNo);
    // 更新商户状态
    merchantReq.setAuthenticationStatus(AuthenticationStatus.CERTIFIED.getCode());
    merchantReq.setDelFlag("0");
    merchantReq.setCreateTime(new Date());
    merchantReq.setStatus(MerchantStatus.NORMAL.getCode());
    merchantReq.setSerialNo(merchant.getSerialNo());
    merchantMapper.updateById(merchantReq);
  }

  /**
   * 1、更新商户信息
   *
   * <p>2、开通商户钱包（开通钱包标识）
   *
   * <p>3、默认开通总店
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "audit")
  @ShenyuDubboClient("/audit")
  @Override
  @Transactional
  public void audit(Map<String, Object> requestMap) throws Exception {
    String merchantNo = (String) requestMap.get("merchantNo");
    String auditFlag = (String) requestMap.get("auditFlag");
    Merchant merchant = merchantMapper.selectById(merchantNo);
    if (merchant == null) {
      throw new BusinessException(ResponseCode.MERCHANT_NOT_EXISTS);
    }
    if ("1".equals(auditFlag)) {
      merchant.setAuthenticationStatus(AuthenticationStatus.CERTIFIED.getCode());
      // 调用upms的商户授权功能，添加权限用户同时开通基础功能, 审核需传商户使用的产品ID
      // 查询出商户信息
      // TODO 先检查支付订单是否存在
      requestMap.put("tenantId", merchant.getTenantId());
      requestMap.put("username", merchant.getMerchantName());
      SysUserDTO sysUserDTO = new SysUserDTO();
      BeanUtil.copyProperties(requestMap, sysUserDTO);
      userService.addMerchantOrStoreUser(sysUserDTO);
      merchant.setStatus(MerchantStatus.NORMAL.getCode());
    } else if ("0".equals(auditFlag)) {
      merchant.setAuthenticationStatus(AuthenticationStatus.CERTIFIED_FAILURE.getCode());
    }
    merchantMapper.updateById(merchant);

    // 如果商户是web3商户
    if(merchant.getCategory() == "2"){
      // TODO: 创建钱包
      bsinServiceInvoke.genericInvoke("WalletService", "createWallet", "dev", requestMap);
    }
    // TODO RPC调用创建总店，store
    requestMap.put("businessModel", BusinessModel.FRANCHISE.getCode());
    requestMap.put("type", StoreType.MAIN_STORE.getCode());
    requestMap.put("description", merchant.getDescription());
    storeService.openStore(requestMap);
  }

  /**
   * 只能按appId来订阅功能
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "subscribeFunction")
  @ShenyuDubboClient("/subscribeFunction")
  @Override
  public void subscribeFunction(Map<String, Object> requestMap) {
    String appId = (String) requestMap.get("appId");
    List<String> appFunctionIds = (List<String>) requestMap.get("appFunctionIds");
    String merchantNo = LoginInfoContextHelper.getMerchantNo();
    // 功能授权
    userService.authMerchantFunction(requestMap);
    for (String appFunctionId : appFunctionIds) {
      BizRoleSubscribeJournal merchantSubscribeJournal = new BizRoleSubscribeJournal();
      merchantSubscribeJournal.setMerchantNo(merchantNo);
      merchantSubscribeJournal.setAppId(appId);
      merchantSubscribeJournal.setAppFunctionId(appFunctionId);
      merchantSubscribeJournalMapper.insert(merchantSubscribeJournal);
    }
  }

  /**
   * 申请入住商户
   *  1、判断是否需要是会员
   *  2、添加商户
   * */
  @ApiDoc(desc = "openMerchant")
  @ShenyuDubboClient("/openMerchant")
  @Override
  public Merchant openMerchant(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
    merchant.setTenantId(loginUser.getTenantId());
    if (merchant.getTenantId() == null) {
      throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
    }
    String customerNo = loginUser.getCustomerNo();
    if (customerNo == null) {
      throw new BusinessException(CUSTOMER_NO_IS_NULL);
    }
    // 是否需要开通会员标识
    Boolean memberFlag = MapUtils.getBoolean(requestMap, "memberFlag");
    if(memberFlag){
      // 会员所属商户包含在 loginUser.getMerchantNo
      Member member =
              memberMapper.selectOne(
                      new LambdaUpdateWrapper<Member>()
                              .eq(Member::getTenantId, loginUser.getTenantId())
                              .eq(StringUtils.isNotEmpty(loginUser.getMerchantNo()),
                                      Member::getMerchantNo,
                                      loginUser.getMerchantNo())
                              .eq(Member::getCustomerNo, customerNo));
      if (member == null) {
        throw new BusinessException(ResponseCode.MEMBER_NOT_EXISTS);
      }
      if (!member.getStatus().equals(MemberStatus.NORMAL.getCode())) {
        throw new BusinessException(ResponseCode.MEMBER_STATUS_EXCEPTION);
      }
    }
    // 默认密码 = 空
    //    if (merchant.getPassword() == null) {
    //      merchant.setMerchantName("123456");
    //    }
    // 默认用户名 = admin
    if (merchant.getUsername() == null) {
      merchant.setUsername("admin");
    }
    merchant.setType(CustomerType.PERSONAL.getCode());
    merchant = addMerchant(merchant, true, customerNo);
    return merchant;
  }

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public Merchant add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
    if (merchant.getTenantId() == null) {
      merchant.setTenantId(loginUser.getTenantId());
      if (merchant.getTenantId() == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }
    merchant = addMerchant(merchant, true, NULL);
    return merchant;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    merchantMapper.deleteById(serialNo);
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Merchant edit(Map<String, Object> requestMap) {
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
    if (merchantNo == null) {
      merchantNo = LoginInfoContextHelper.getCustomerNo();
      if (merchantNo == null) {
        throw new BusinessException(MERCHANT_NOT_EXISTS);
      }
    }
    merchantMapper.updateById(merchant);
    return merchant;
  }

  /**
   * b端查询登录平台的商户 有token校验
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "/admin/getPageList")
  @ShenyuDubboClient("/admin/getPageList")
  @Override
  public IPage<Merchant> getPageListAdmin(Map<String, Object> requestMap) {
    Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
    String tenantId = merchant.getTenantId();
    String merchantNo = LoginInfoContextHelper.getMerchantNo();
    if (tenantId == null) {
      tenantId = LoginInfoContextHelper.getTenantId();
    }
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<Merchant> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaQueryWrapper<Merchant> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(Merchant::getCreateTime);
    warapper.eq(Merchant::getTenantId, tenantId);
    warapper.eq(
        StringUtils.isNotEmpty(merchant.getBusinessType()),
        Merchant::getBusinessType,
        merchant.getBusinessType());
    warapper.eq(
        StringUtils.isNotEmpty(merchant.getStatus()), Merchant::getStatus, merchant.getStatus());
    warapper.eq(
        StringUtils.isNotEmpty(merchant.getAuthenticationStatus()),
        Merchant::getAuthenticationStatus,
        merchant.getAuthenticationStatus());
    warapper.eq(StringUtils.isNotEmpty(merchantNo), Merchant::getSerialNo, merchantNo);
    IPage<Merchant> pageList = merchantMapper.selectPage(page, warapper);
    return pageList;
  }

  /**
   * c端查询平台的商户 没有token校验
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<Merchant> getPageList(Map<String, Object> requestMap) {
    Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
    String tenantId = merchant.getTenantId();
    String merchantNo = LoginInfoContextHelper.getMerchantNo();
    if (tenantId == null) {
      tenantId = LoginInfoContextHelper.getTenantId();
    }
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<Merchant> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaQueryWrapper<Merchant> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(Merchant::getCreateTime);
    warapper.eq(Merchant::getTenantId, tenantId);
    warapper.eq(
        StringUtils.isNotEmpty(merchant.getBusinessType()),
        Merchant::getBusinessType,
        merchant.getBusinessType());
    warapper.eq(
        StringUtils.isNotEmpty(merchant.getStatus()), Merchant::getStatus, merchant.getStatus());
    warapper.eq(StringUtils.isNotEmpty(merchantNo), Merchant::getSerialNo, merchantNo);
    IPage<Merchant> pageList = merchantMapper.selectPage(page, warapper);
    return pageList;
  }

  /**
   * c端查询平台的商户 没有token校验
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public List<Merchant> getList(Map<String, Object> requestMap) {
    Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
    String tenantId = merchant.getTenantId();
    String merchantNo = LoginInfoContextHelper.getMerchantNo();
    if (tenantId == null) {
      tenantId = LoginInfoContextHelper.getTenantId();
    }
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<Merchant> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaQueryWrapper<Merchant> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(Merchant::getCreateTime);
    warapper.eq(Merchant::getTenantId, tenantId);
    warapper.eq(
        StringUtils.isNotEmpty(merchant.getBusinessType()),
        Merchant::getBusinessType,
        merchant.getBusinessType());
    warapper.eq(
        StringUtils.isNotEmpty(merchant.getStatus()), Merchant::getStatus, merchant.getStatus());
    warapper.eq(StringUtils.isNotEmpty(merchantNo), Merchant::getSerialNo, merchantNo);
    List<Merchant> list = merchantMapper.selectList(warapper);
    return list;
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Merchant getDetail(Map<String, Object> requestMap) {
    String merchantNo = MapUtils.getString(requestMap, "serialNo");
    if(merchantNo == null){
      merchantNo = MapUtils.getString(requestMap, "merchantNo");
    }
    Merchant merchant = merchantMapper.selectById(merchantNo);
    return merchant;
  }

  @ApiDoc(desc = "getListByMerchantNos")
  @ShenyuDubboClient("/getListByMerchantNos")
  @Override
  public List<?> getListByMerchantNos(Map<String, Object> requestMap) {
    List<String> merchantNos = (List<String>) requestMap.get("merchantNos");
    if (merchantNos.size() < 1) {
      throw new BusinessException("200000", "请求参数不能为空！");
    }
    List<Merchant> merchantList = merchantMapper.selectBatchIds(merchantNos);
    return merchantList;
  }

  private Merchant addMerchant(Merchant merchant, boolean registerNotNeedAudit, String customerNo) {
    Map<String, Object> requestMap = new HashMap<>();
    merchant.setSerialNo(BsinSnowflake.getId());
    if (merchant.getUsername() == null) {
      throw new BusinessException(USER_NAME_ISNULL);
    }
    //    if (merchant.getPassword() == null) {
    //      throw new BusinessException(PASSWORD_EXISTS);
    //    }
    // 调用upms的商户授权功能，添加权限用户同时开通基础功能, 审核需传商户使用的产品ID
    // 查询出商户信息
    // TODO 先检查支付订单是否存在
    requestMap.put("tenantId", merchant.getTenantId());
    requestMap.put("username", merchant.getUsername());
    requestMap.put("merchantName", merchant.getMerchantName());
    requestMap.put("merchantNo", merchant.getSerialNo());
    requestMap.put("password", merchant.getPassword());
    SysUserDTO sysUserDTO = new SysUserDTO();
    BeanUtil.copyProperties(requestMap, sysUserDTO);
    sysUserDTO.setBizRoleType(BizRoleType.MERCHANT.getCode());
    userService.addMerchantOrStoreUser(sysUserDTO);
    if (registerNotNeedAudit) {
      merchant.setAuthenticationStatus(AuthenticationStatus.CERTIFIED.getCode());
      merchant.setStatus(MerchantStatus.NORMAL.getCode());
    } else {
      // 普通注册逻辑
      merchant.setAuthenticationStatus(AuthenticationStatus.TOBE_CERTIFIED.getCode());
      merchant.setStatus(MerchantStatus.TOBE_CERTIFIED.getCode());
    }
    if (merchantMapper.insert(merchant) == 0) {
      throw new BusinessException(ResponseCode.DATA_BASE_UPDATE_FAILED);
    }
    // 客户号不为空，则表示是会员申请成为商户(团长)，需要插入客户身份表
    if (StringUtils.isNotEmpty(customerNo)) {
      // 身份表: crm_customer_identity插入数据
      CustomerIdentity customerIdentity = new CustomerIdentity();
      customerIdentity.setCustomerNo(customerNo);
      customerIdentity.setTenantId(merchant.getTenantId());
      customerIdentity.setMerchantNo(merchant.getSerialNo());
      customerIdentity.setName(BizRoleType.MERCHANT.getDesc());
      customerIdentity.setUsername(merchant.getUsername());
      customerIdentity.setBizRoleType(BizRoleType.MERCHANT.getCode());
      customerIdentity.setBizRoleTypeNo(merchant.getSerialNo());
      customerIdentityMapper.insert(customerIdentity);
    }
    return merchant;
  }

}
