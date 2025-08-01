package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.blockchain.utils.Web3WalletUtil;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.AccountCategory;
import me.flyray.bsin.domain.enums.CustomerType;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.enums.AuthMethod;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.response.CustomerAccountVO;
import me.flyray.bsin.facade.service.*;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.security.authentication.AuthenticationProvider;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.security.enums.TenantMemberModel;
import me.flyray.bsin.server.biz.AccountBiz;
import me.flyray.bsin.server.biz.CustomerBiz;
import me.flyray.bsin.server.controller.WxPortalController;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.SignUtils;
import me.flyray.bsin.utils.StringUtils;
import me.flyray.bsin.validate.AddGroup;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static me.flyray.bsin.constants.ResponseCode.*;

/**
 * @author bolei
 * @date 2023/6/28 16:38
 * @desc
 */
@Slf4j
@ShenyuDubboService(path = "/customer", timeout = 6000)
@ApiModule(value = "customer")
@Service
public class CustomerServiceImpl implements CustomerService {

  @Autowired
  private BsinServiceInvoke bsinServiceInvoke;

  @Value("${bsin.security.authentication-secretKey}")
  private String authSecretKey;

  @Value("${bsin.security.authentication-expiration}")
  private int authExpiration;

  @Value("${bsin.jiujiu.aesKey}")
  private String aesKey;

  @Autowired PlatformMapper platformMapper;
  @Autowired private CustomerBaseMapper customerBaseMapper;
  @Autowired public MerchantMapper merchantMapper;
  @Autowired SysAgentMapper sysAgentMapper;
  @Autowired private CustomerBiz customerBiz;
  @Autowired private MemberMapper memberMapper;
  @Autowired private AccountBiz customerAccountBiz;
  @Autowired private SignUtils signUtils;
  @Autowired private WxPortalController wxPortalController;
  @Autowired
  private MerchantConfigMapper merchantConfigMapper;
  @Autowired
  private DisInviteRelationMapper disInviteRelationMapper;

  @DubboReference(version = "${dubbo.provider.version}")
  private TenantService tenantService;

  @DubboReference(version = "${dubbo.provider.version}")
  private UserService userService;

  /**
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "login")
  @ShenyuDubboClient("/login")
  @Override
  public Map<String, Object> login(Map<String, Object> requestMap) {
    CustomerBase customerBaseReq = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);
    CustomerBase customerInfo = customerBiz.login(customerBaseReq);
    LoginUser loginUser = new LoginUser();
    loginUser.setTenantId(customerInfo.getTenantId());
    loginUser.setUsername(customerInfo.getUsername());
    loginUser.setPhone(customerInfo.getPhone());
    loginUser.setCustomerNo(customerInfo.getCustomerNo());
    loginUser.setCredential(customerInfo.getCredential());
    loginUser.setBizRoleType(BizRoleType.CUSTOMER.getCode());
    loginUser.setBizRoleTypeNo(customerInfo.getCustomerNo());

    // 查询租户的默认直属商户号
    Merchant merchant =
        merchantMapper.selectOne(
            new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getTenantId, customerInfo.getTenantId())
                .eq(Merchant::getType, CustomerType.UNDER_TENANT_MEMBER.getCode()));
    loginUser.setTenantMerchantNo(merchant.getSerialNo());

    // 查询会员信息
    Member member =
        memberMapper.selectOne(
            new LambdaQueryWrapper<Member>()
                .eq(Member::getTenantId, customerInfo.getTenantId())
                .eq(Member::getMerchantNo, loginUser.getMerchantNo())
                .eq(Member::getCustomerNo, customerInfo.getCustomerNo()));
    // 查新询客户身份信息
    List<CustomerIdentity> identityList =
        customerBiz.getCustomerIdentityList(customerInfo.getCustomerNo());
    log.info("identityList: " + identityList.toString());
    // 合伙人信息
    SysAgent sysAgentInfo = disInviteRelationMapper.selectSysAgent(customerInfo.getCustomerNo());

    String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);

    Map res = new HashMap<>();
    res.put("customerInfo", customerInfo);
    res.put("memberInfo", member);
    res.put("sysAgentInfo", sysAgentInfo);
    res.put("token", token);
    res.put("identityList", identityList);

    return res;
  }

  /**
   * 登录切换，需要用户名，根据 type:
   *
   * @see BizRoleType 来切换登录角色，返回token
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "loginSwitch")
  @ShenyuDubboClient("/loginSwitch")
  @Override
  public Map<String, Object> loginSwitch(Map<String, Object> requestMap) {
    LoginUser loginOrinUser = LoginInfoContextHelper.getLoginUser();
    String bizRoleType = MapUtils.getString(requestMap, "bizRoleType");
    String bizRoleTypeNo = MapUtils.getString(requestMap, "bizRoleTypeNo");
    String tenantId = LoginInfoContextHelper.getTenantId();
    String username = MapUtils.getString(requestMap, "username");
    if (StringUtils.isEmpty(bizRoleTypeNo)) {
      if (StringUtils.isEmpty(username)) {
        throw new BusinessException(ResponseCode.USER_NAME_ISNULL);
      }
    }
    String customerNo = loginOrinUser.getCustomerNo();
    // 商户单独一个应用登录需要 merchantNo 字段
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");

    String phone = null;
    Map res = new HashMap<>();
    if (BizRoleType.CUSTOMER.getCode().equals(bizRoleType)) {
      // 查询客户信息
      CustomerBase customerInfo = null;
      if (StringUtils.isNotEmpty(bizRoleTypeNo)) {
        customerInfo = customerBaseMapper.selectById(bizRoleTypeNo);
      } else {
        LambdaQueryWrapper<CustomerBase> wrapperCustomerBase = new LambdaQueryWrapper<>();
        wrapperCustomerBase.eq(
            StringUtils.isNotEmpty(loginOrinUser.getTenantId()),
            CustomerBase::getTenantId,
            loginOrinUser.getTenantId());
        wrapperCustomerBase.eq(
            StringUtils.isNotEmpty(username), CustomerBase::getUsername, username);
        customerInfo = customerBaseMapper.selectOne(wrapperCustomerBase);
      }
      if (customerInfo == null) {
        throw new BusinessException(ResponseCode.CUSTOMER_ERROR);
      }
      phone = customerInfo.getPhone();
      res.put("customerInfo", customerInfo);
      // 合伙人信息
      SysAgent sysAgentInfo = disInviteRelationMapper.selectSysAgent(customerInfo.getCustomerNo());
      res.put("sysAgentInfo", sysAgentInfo);
    } else if (BizRoleType.MERCHANT.getCode().equals(bizRoleType)) {
      // 查询商户信息
      Merchant merchantInfo = null;
      if (StringUtils.isNotEmpty(bizRoleTypeNo)) {
        merchantInfo = merchantMapper.selectById(bizRoleTypeNo);
      } else {
        LambdaQueryWrapper<Merchant> wrapperMerchant = new LambdaQueryWrapper<>();
        wrapperMerchant.eq(
            StringUtils.isNotEmpty(loginOrinUser.getTenantId()),
            Merchant::getTenantId,
            loginOrinUser.getTenantId());
        wrapperMerchant.eq(StringUtils.isNotEmpty(username), Merchant::getUsername, username);
        merchantInfo = merchantMapper.selectOne(wrapperMerchant);
      }
      if (merchantInfo == null) {
        throw new BusinessException(ResponseCode.MERCHANT_NOT_EXISTS);
      }
      res.put("merchantInfo", merchantInfo);
      phone = merchantInfo.getPhone();
    } else if (BizRoleType.SYS_AGENT.getCode().equals(bizRoleType)) {
      // 查询合伙人信息
      SysAgent sysAgentInfo = null;
      if (StringUtils.isNotEmpty(bizRoleTypeNo)) {
        sysAgentInfo = sysAgentMapper.selectById(bizRoleTypeNo);
      } else {
        LambdaQueryWrapper<SysAgent> wrapperSysAgent = new LambdaQueryWrapper<>();
        wrapperSysAgent.eq(
            StringUtils.isNotEmpty(loginOrinUser.getTenantId()),
            SysAgent::getUsername,
            loginOrinUser.getTenantId());
        wrapperSysAgent.eq(StringUtils.isNotEmpty(username), SysAgent::getUsername, username);
        sysAgentInfo = sysAgentMapper.selectOne(wrapperSysAgent);
      }
      if (sysAgentInfo == null) {
        throw new BusinessException(ResponseCode.SYS_AGENT_NOT_EXISTS);
      }
      res.put("sysAgentInfo", sysAgentInfo);
      phone = sysAgentInfo.getPhone();
    } else {
      throw new BusinessException(ResponseCode.BIZ_ROLE_TYPE_ERROR);
    }
    loginOrinUser.setUsername(username);
    loginOrinUser.setPhone(phone);
    loginOrinUser.setCustomerNo(customerNo);
    loginOrinUser.setBizRoleType(bizRoleType);
    loginOrinUser.setBizRoleTypeNo(bizRoleTypeNo);
    // 根据商户号查询会员配置信息表的会员模型
    LambdaQueryWrapper<MerchantConfig> memberConfigWrapper = new LambdaQueryWrapper<>();
    memberConfigWrapper.eq(MerchantConfig::getTenantId, tenantId);
    memberConfigWrapper.last("limit 1");
    MerchantConfig memberConfig = merchantConfigMapper.selectOne(memberConfigWrapper);
    loginOrinUser.setMemberModel(memberConfig.getMemberModel());
    // 查询租户的默认直属商户号
    Merchant merchant =
        merchantMapper.selectOne(
            new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getTenantId, loginOrinUser.getTenantId())
                .eq(Merchant::getType, CustomerType.UNDER_TENANT_MEMBER.getCode()));
    loginOrinUser.setTenantMerchantNo(merchant.getSerialNo());
    loginOrinUser.setTenantMerchantNo(merchant.getSerialNo());
    if (TenantMemberModel.UNDER_MERCHANT.getCode().equals(memberConfig.getMemberModel()) || TenantMemberModel.UNDER_STORE.getCode().equals(memberConfig.getMemberModel())) {
      loginOrinUser.setMerchantNo(merchantNo);
    }
    String token = AuthenticationProvider.createToken(loginOrinUser, authSecretKey, authExpiration);

    // 查询会员信息
    Member member =
        memberMapper.selectOne(
            new LambdaUpdateWrapper<Member>()
                .eq(Member::getTenantId, loginOrinUser.getTenantId())
                .eq(Member::getMerchantNo, loginOrinUser.getTenantMerchantNo())
                .eq(Member::getCustomerNo, loginOrinUser.getCustomerNo()));
    // 查新询客户身份信息
    List<CustomerIdentity> identityList = customerBiz.getCustomerIdentityList(customerNo);
    log.info("identityList: " + identityList.toString());
    res.put("memberInfo", member);
    res.put("token", token);
    res.put("identityList", identityList);

    return res;
  }

  /**
   * 不对外暴露接口调用，作为rpc服务对内提供
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "register")
  @ShenyuDubboClient("/register")
  @Transactional
  @Override
  public CustomerBase register(Map<String, Object> requestMap) throws UnsupportedEncodingException {
    String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
    CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);
    SysAgent sysAgent = new SysAgent();
    sysAgent.setSerialNo(sysAgentNo);
    customerBiz.register(customerBase, sysAgent);
    return customerBase;
  }

  /**
   * 手机验证码登录 SMS verification code
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getLoginVerifycode")
  @ShenyuDubboClient("/getLoginVerifycode")
  @Override
  public Map<String, Object> getLoginVerifycode(Map<String, Object> requestMap) {
    String phone = (String) requestMap.get("phone");
    // 调用login验证码模板发短信

    // 将验证码存在缓存里面 phone:eventType verifycode

    return null;
  }

  /**
   * 手机验证码登录
   * <p>1、判断用户是否存在
   * <p>2、不存在则注册并登录
   * <p>3、存在则直接登录 返回是否有数字分身
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "registerOrLogin")
  @ShenyuDubboClient("/registerOrLogin")
  @Override
  public Map<String, Object> registerOrLogin(Map<String, Object> requestMap)
      throws UnsupportedEncodingException {
    CustomerBase customerBase =
        BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap, AddGroup.class);
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    String openId = MapUtils.getString(requestMap, "openId");
    String appId = MapUtils.getString(requestMap, "appId");
    String encryptedData = MapUtils.getString(requestMap, "encryptedData");
    String iv = MapUtils.getString(requestMap, "iv");
    if (ObjectUtil.isEmpty(customerBase.getTenantId())) {
      throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
    }
    if (ObjectUtil.isEmpty(customerBase.getAuthMethod())) {
      throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
    }
    customerBase.setCredential(openId);
    CustomerBase customerBaseRegister = customerBiz.register(customerBase, new SysAgent());

    LoginUser loginUser = new LoginUser();
    loginUser.setTenantId(customerBaseRegister.getTenantId());
    loginUser.setUsername(customerBaseRegister.getUsername());
    loginUser.setPhone(customerBaseRegister.getPhone());
    loginUser.setCustomerNo(customerBaseRegister.getCustomerNo());
    loginUser.setCredential(customerBaseRegister.getCredential());
    loginUser.setBizRoleType(BizRoleType.CUSTOMER.getCode());
    loginUser.setBizRoleTypeNo(customerBaseRegister.getCustomerNo());

    // 查询租户的默认直属商户号
    Merchant merchant =
        merchantMapper.selectOne(
            new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getTenantId, customerBaseRegister.getTenantId())
                .eq(Merchant::getType, CustomerType.UNDER_TENANT_MEMBER.getCode()));
    loginUser.setTenantMerchantNo(merchant.getSerialNo());
    if (AuthMethod.WECHAT.getType().equals(customerBase.getAuthMethod())) {
      // 获取手机号
      if (ObjectUtil.isEmpty(customerBaseRegister.getPhone())) {
        customerBaseRegister =
            wxPortalController.bindingPhoneNumber(tenantId, openId, appId, encryptedData, iv);
      }
      // 获取微信用户信息
      if (ObjectUtil.isEmpty(customerBaseRegister.getNickname())) {
        customerBaseRegister =
            wxPortalController.bindingUserInfo(tenantId, openId, appId, encryptedData, iv);
      }
    }
    // TODO 查询会员信息
    Member member =
        memberMapper.selectOne(
            new LambdaUpdateWrapper<Member>()
                .eq(Member::getTenantId, customerBaseRegister.getTenantId())
                .eq(Member::getMerchantNo, loginUser.getMerchantNo())
                .eq(Member::getCustomerNo, customerBaseRegister.getCustomerNo()));

    // 查新询客户身份信息
    List<CustomerIdentity> identityList =
        customerBiz.getCustomerIdentityList(customerBaseRegister.getCustomerNo());

    String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
    log.info("identityList: " + identityList.toString());
    // 合伙人信息
    SysAgent sysAgentInfo = disInviteRelationMapper.selectSysAgent(customerBaseRegister.getCustomerNo());
    // 查询客户的角色信息，是否是dao创建者
    // 查询用户加入的dao信息
    Map data = new HashMap<>();
    data.put("customerInfo", customerBaseRegister);
    data.put("memberInfo", member);
    data.put("sysAgentInfo", sysAgentInfo);
    data.put("token", token);
    data.put("identityList", identityList);
    return data;
  }

  /**
   * 微信平台授权登录 微信小程序获取openId和sessionKey
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getOpenId")
  @ShenyuDubboClient("/getOpenId")
  @Override
  public Map<String, Object> getOpenId(Map<String, Object> requestMap)
      throws UnsupportedEncodingException {
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    String code = MapUtils.getString(requestMap, "code");
    String appId = MapUtils.getString(requestMap, "appId");
    log.info("code:{},appId:{}", code, appId);
    CustomerBase customerBase = wxPortalController.authorizedLogin(tenantId, appId, code);
    if (customerBase == null) {
      throw new BusinessException(GET_OPENID_FAIL);
    }
    Map res = new HashMap<>();
    res.put("openId", customerBase.getCredential());
    res.put("sessionKey", customerBase.getSessionKey());

    //    LoginUser loginUser = new LoginUser();
    //    loginUser.setTenantId(customerBase.getTenantId());
    //    loginUser.setUsername(customerBase.getUsername());
    //    loginUser.setPhone(customerBase.getPhone());
    //    loginUser.setCustomerNo(customerBase.getCustomerNo());
    //    loginUser.setCredential(customerBaseRegister.getCredential());
    //    loginUser.setBizRoleType(BizRoleType.CUSTORMER.getCode());
    //    loginUser.setBizRoleTypeNo(customerBase.getCustomerNo());
    //    String token = AuthenticationProvider.createToken(loginUser, authSecretKey,
    // authExpiration);
    //    res.put("customerInfo", customerBase);
    //    // 查选是否是会员
    //    Member member =
    //            memberMapper.selectOne(
    //                    new LambdaUpdateWrapper<Member>()
    //                            .eq(ObjectUtil.isNotNull(customerBase.getTenantId()),
    // Member::getTenantId, customerBase.getTenantId())
    //                            .eq(ObjectUtil.isNotNull(merchantNo), Member::getMerchantNo,
    // merchantNo)
    //                            .eq(Member::getCustomerNo, customerBase.getCustomerNo()));
    //    res.put("memberInfo", member);
    //    res.put("token",token);
    return res;
  }

  /**
   * 通过前端钱包签名后端验签之后获取token信息
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "web3Login")
  @ShenyuDubboClient("/web3Login")
  @Override
  public Map<String, Object> web3Login(Map<String, Object> requestMap)
      throws SignatureException, UnsupportedEncodingException {
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    String signature = MapUtils.getString(requestMap, "signature");
    String message = MapUtils.getString(requestMap, "message");
    String username = MapUtils.getString(requestMap, "username");
    Boolean signFlag = Web3WalletUtil.web3jValidate(signature, message, username);
    if (!signFlag) {
      throw new BusinessException(ResponseCode.WEB3_LOGIN_FAIL);
    }
    CustomerBase customerBase = new CustomerBase();
    customerBase.setTenantId(tenantId);
    customerBase.setUsername(username);
    customerBase.setPassword(message);
    customerBase.setEvmWalletAddress(username);
    customerBase.setWalletAddress(username);
    CustomerBase customerBaseRegister = customerBiz.register(customerBase, new SysAgent());
    Member member =
        memberMapper.selectOne(
            new LambdaQueryWrapper<Member>()
                .eq(Member::getMerchantNo, merchantNo)
                .eq(Member::getCustomerNo, customerBaseRegister.getCustomerNo()));
    if (member == null) {
      member = new Member();
      BeanUtil.copyProperties(customerBase, member);
      member.setTenantId(tenantId);
      member.setMerchantNo(merchantNo);
      member.setNickname(username);
      memberMapper.insert(member);
    }

    LoginUser loginUser = new LoginUser();
    loginUser.setTenantId(customerBaseRegister.getTenantId());
    loginUser.setUsername(customerBaseRegister.getUsername());
    loginUser.setPhone(customerBaseRegister.getPhone());
    loginUser.setCustomerNo(customerBaseRegister.getCustomerNo());
    loginUser.setCredential(customerBaseRegister.getCredential());
    loginUser.setBizRoleType(BizRoleType.CUSTOMER.getCode());
    loginUser.setBizRoleTypeNo(customerBaseRegister.getCustomerNo());
    String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);

    Map data = new HashMap<>();
    data.put("customerInfo", customerBaseRegister);
    data.put("memberInfo", member);
    data.put("token", token);
    return data;
  }

  /** 身份认证 */
  @ApiDoc(desc = "identityVerification")
  @ShenyuDubboClient("/identityVerification")
  /** 身份认证 */
  @Override
  public void identityVerification(Map<String, Object> requestMap) {
    CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);
    customerBaseMapper.updateById(customerBase);
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<?> getPageList(Map<String, Object> requestMap) {
    CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);
    String tenantId = customerBase.getTenantId();
    if (tenantId == null) {
      tenantId = LoginInfoContextHelper.getTenantId();
    }
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<CustomerBase> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<CustomerBase> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(CustomerBase::getCreateTime);
    if(!BizRoleType.SYS.getCode().equals(LoginInfoContextHelper.getBizRoleType())){
      warapper.eq(CustomerBase::getTenantId, tenantId);
    }
    warapper.eq(
        ObjectUtil.isNotNull(customerBase.getCustomerNo()),
        CustomerBase::getCustomerNo,
        customerBase.getCustomerNo());
    warapper.eq(
        ObjectUtil.isNotNull(customerBase.getType()),
        CustomerBase::getType,
        customerBase.getType());
    IPage<CustomerBase> pageList = customerBaseMapper.selectPage(page, warapper);
    return pageList;
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public CustomerBase getDetail(Map<String, Object> requestMap) {
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
    }
    CustomerBase customerInfo = customerBaseMapper.selectById(customerNo);
    //        customerInfo.setWalletPrivateKey(null);
    return customerInfo;
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public CustomerBase edit(Map<String, Object> requestMap) {
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
      }
      customerBase.setCustomerNo(customerNo);
    }
    customerBaseMapper.updateById(customerBase);
    return customerBase;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public Boolean delete(Map<String, Object> requestMap) {
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
        throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
    }
    if (customerBaseMapper.deleteById(customerNo) == 0) {
      throw new BusinessException(CUSTOMER_ERROR);
    }
    return true;
  }

  /**
   * 根据事件判断用户是否需要发放激励 需要则进行激励发放
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "incentiveDistribution")
  @ShenyuDubboClient("/incentiveDistribution")
  @Override
  public Map<String, Object> incentiveDistribution(Map<String, Object> requestMap) {
    return null;
  }

  @ApiDoc(desc = "getListByCustomerNos")
  @ShenyuDubboClient("/getListByCustomerNos")
  @Override
  public List<?> getListByCustomerNos(Map<String, Object> requestMap) {
    //        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    //        log.info(loginUser.toString());
    List<String> customerNos = (List<String>) requestMap.get("customerNos");
    if (customerNos.size() < 1) {
      throw new BusinessException("200000", "请求参数不能为空！");
    }
    List<CustomerBase> customerBaseList = customerBaseMapper.selectBatchIds(customerNos);
    return customerBaseList;
  }

  /** 实名认证 */
  @ApiDoc(desc = "certification")
  @ShenyuDubboClient("/certification")
  @Override
  public CustomerBase certification(Map<String, Object> requestMap) throws Exception {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);

    // 泛化调用解耦
    Object object = bsinServiceInvoke.genericInvoke("WalletService", "createWallet", "dev", requestMap);
    Map<String, Object> walletData = BeanUtil.beanToMap(object);
    // 查找资产客户 --> 获取私钥
    customerBase.setWalletAddress((String) walletData.get("walletAddress"));
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
    customerBase.setWalletPrivateKey(aes.encryptHex((String) walletData.get("walletPrivateKey")));
    // 更新客户信息
    customerBase.setTxPassword((String) requestMap.get("password"));
    customerBase.setTxPasswordStatus("1");
    customerBase.setCertificationStatus(true);
    customerBase.setCustomerNo(loginUser.getCustomerNo());
    customerBaseMapper.updateById(customerBase);
    return customerBase;
  }

  /**
   * 设置钱包信息
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "settingWallet")
  @ShenyuDubboClient("/settingWallet")
  @Override
  public void settingWallet(Map<String, Object> requestMap) {
    CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);
    customerBase.setCustomerNo(LoginInfoContextHelper.getCustomerNo());
    SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, aesKey.getBytes());
    if (customerBase.getWalletPrivateKey() != null) {
      customerBase.setWalletPrivateKey(aes.encryptHex(customerBase.getWalletPrivateKey()));
    }
    if (customerBase.getEvmWalletPrivateKey() != null) {
      customerBase.setEvmWalletPrivateKey(aes.encryptHex(customerBase.getEvmWalletPrivateKey()));
    }
    customerBaseMapper.updateById(customerBase);
  }

  /**
   * 设置profile信息
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "settingProfile")
  @ShenyuDubboClient("/settingProfile")
  @Override
  public CustomerBase settingProfile(Map<String, Object> requestMap) {
    CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);
    if (customerBase.getCustomerNo() == null) {
      customerBase.setCustomerNo(LoginInfoContextHelper.getCustomerNo());
      if (customerBase.getCustomerNo() == null) {
        throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
      }
    }
    if (customerBaseMapper.updateById(customerBase) <= 0) {
      throw new BusinessException(DATA_BASE_UPDATE_FAILED);
    }

    return customerBase;
  }

  /**
   * 根据商户号查询用户在该品牌商户下的钱包资产 1、曲线积分 2、数字积分 3、平台储值账户余额 4、钱包地址
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getWalletInfo")
  @ShenyuDubboClient("/getWalletInfo")
  @Override
  public CustomerAccountVO getWalletInfo(Map<String, Object> requestMap) {
    String customerNo = LoginInfoContextHelper.getCustomerNo();
    // 1.获取商户号
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if(StringUtils.isEmpty(merchantNo)){
      merchantNo = LoginInfoContextHelper.getTenantMerchantNo();
    }

    CustomerAccountVO customerAccountVO = new CustomerAccountVO();

    // 2.商户发行的数字积分(查询tokenParam)

    // 泛化调用解耦
    Object object = bsinServiceInvoke.genericInvoke("TokenParamService", "getDetailByMerchantNo", "dev", requestMap);
    Map<String, Object> tokenParamMap = BeanUtil.beanToMap(object);

    BigDecimal balance = new BigDecimal("0");
    if (tokenParamMap != null) {
      String digitalPointsSymbol = (String) tokenParamMap.get("symbol");
      String digitalPointsName = (String) tokenParamMap.get("name");
      Account digitalPointsAccount =
          customerAccountBiz.getAccountDetail(
              merchantNo, customerNo, digitalPointsSymbol, AccountCategory.BALANCE.getCode());
      // 设置数字积分余额
      customerAccountVO.setDigitalPointsBalance(
          digitalPointsAccount != null
              ? digitalPointsAccount
                  .getBalance()
                  .divide(
                      BigDecimal.valueOf(
                          Math.pow(10, digitalPointsAccount.getDecimals().longValue())),
                      2,
                      ROUND_HALF_UP)
              : balance);
      customerAccountVO.setDigitalPointsName(digitalPointsName);
      customerAccountVO.setDigitalPointsSymbol(digitalPointsSymbol);
      customerAccountVO.setDigitalPointsSupply(
              new BigDecimal((String) tokenParamMap.get("totalSupply"))
              .divide(
                  BigDecimal.valueOf(Math.pow(10, Double.parseDouble((String) tokenParamMap.get("decimals")))),
                  2,
                  ROUND_HALF_UP));
      // 设置数字积分流通量
      customerAccountVO.setDigitalPointsCirculation(
              new BigDecimal((String) tokenParamMap.get("circulation"))
              .divide(
                  BigDecimal.valueOf(Math.pow(10,Double.parseDouble((String) tokenParamMap.get("decimals")))),
                  2,
                  ROUND_HALF_UP));
      // TODO: 浮点数比较
      if (customerAccountVO.getDigitalPointsCirculation().equals(new BigDecimal("0.00"))) {
        customerAccountVO.setDigitalPointsProportion(new BigDecimal("0.0"));
      } else {
        customerAccountVO.setDigitalPointsProportion(
            customerAccountVO
                .getDigitalPointsBalance()
                .divide(customerAccountVO.getDigitalPointsCirculation(), 6, ROUND_HALF_UP));
      }
    } else {
      customerAccountVO.setDigitalPointsBalance(balance);
    }

    // 2.商户发行的劳动价值捕获积分(基于联合曲线发行)
    Map bondingCurveTokenParam = new HashMap<>();
//        bondingCurveTokenParamMapper.selectOne(
//            new LambdaQueryWrapper<BondingCurveTokenParam>()
//                .eq(BondingCurveTokenParam::getMerchantNo, merchantNo));
    if (bondingCurveTokenParam != null) {
      // 根据币种查询对应账户余额
      // .1 联合曲线余额账户： 扣除释放的账户
      Account bondingCurveBalanceAccount =
          customerAccountBiz.getAccountDetail(
              merchantNo,
              customerNo,
                  (String) bondingCurveTokenParam.get("symbol"),
              AccountCategory.BALANCE.getCode());
      // .2 联合曲线累计账户： 累计贡献值
      Account bondingCurveAccumulatedIncomeBalanceAccount =
          customerAccountBiz.getAccountDetail(
              merchantNo,
              customerNo,
                  (String) bondingCurveTokenParam.get("symbol"),
              AccountCategory.ACCUMULATED_INCOME.getCode());

      if (bondingCurveBalanceAccount != null) {
        balance =
            bondingCurveBalanceAccount
                .getBalance()
                .divide(
                    BigDecimal.valueOf(Math.pow(10, Double.parseDouble((String) bondingCurveTokenParam.get("decimals")))),
                    2,
                    ROUND_HALF_UP);

        customerAccountVO.setBondingCurveCirculation(
                new BigDecimal((String) tokenParamMap.get("circulation"))
                .divide(
                    BigDecimal.valueOf(Math.pow(10, Double.parseDouble((String) bondingCurveTokenParam.get("decimals")))),
                    2,
                    ROUND_HALF_UP));
      }
      if (bondingCurveAccumulatedIncomeBalanceAccount != null) {
        customerAccountVO.setBondingCurveAccumulatedBalance(
            bondingCurveAccumulatedIncomeBalanceAccount
                .getBalance()
                .divide(
                    BigDecimal.valueOf(Math.pow(10, Double.parseDouble((String) bondingCurveTokenParam.get("decimals")))),
                    2,
                    ROUND_HALF_UP));

        customerAccountVO.setBondingCurveProportion(
            customerAccountVO
                .getBondingCurveAccumulatedBalance()
                .divide(customerAccountVO.getBondingCurveCirculation(), 6, ROUND_HALF_UP));
      }

      customerAccountVO.setBondingCurveSymbol((String) bondingCurveTokenParam.get("symbol"));
      customerAccountVO.setBondingCurveName((String) bondingCurveTokenParam.get("name"));
      customerAccountVO.setBondingCurveSupply(new BigDecimal((String) bondingCurveTokenParam.get("cap")));
    }
    customerAccountVO.setBondingCurveBalance(balance);

    // 3.法币(CNY)余额
    balance = new BigDecimal("0");
    Account cnyAccount =
        customerAccountBiz.getAccountDetail(
            merchantNo, customerNo, "cny", AccountCategory.BALANCE.getCode());
    if (cnyAccount != null) {
      if (cnyAccount.getDecimals().longValue() > 0) {
        balance =
            cnyAccount
                .getBalance()
                .divide(
                    BigDecimal.valueOf(Math.pow(10, cnyAccount.getDecimals())), 2, ROUND_HALF_UP);
      } else {
        balance = cnyAccount.getBalance();
      }
    }
    customerAccountVO.setCnyBalance(balance);

    // 4. 商户PassCard的TBA账户地址 泛化调用解耦
    Object digitalAssetsDetailObject = bsinServiceInvoke.genericInvoke("CustomerPassCardService", "getDetail", "dev", requestMap);
    Map<String, Object> digitalAssetsDetailRes = BeanUtil.beanToMap(digitalAssetsDetailObject);
    if (digitalAssetsDetailRes != null) {
      Map customerPassCard = (Map) digitalAssetsDetailRes.get("customerPassCard");
      customerAccountVO.setTbaAddress((String) customerPassCard.get("tbaAddress"));
    }

    return customerAccountVO;
  }

  /**
   * 本月连续签到次数
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getContinuousSignCount")
  @ShenyuDubboClient("/getContinuousSignCount")
  @Override
  public Map<String, Object> getContinuousSignCount(Map<String, Object> requestMap)
      throws ParseException {
    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
    }
    // 定义输出格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 将字符串转化为日期
    Date date = sdf.parse((String) requestMap.get("date"));
    Map<String, Object> responseMap = new HashMap<String, Object>();
    responseMap.put("continuousSignCount", signUtils.getContinuousSignCount(customerNo, date));
    return responseMap;
  }

  /**
   * 获取累计签到数
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getSumSignCount")
  @ShenyuDubboClient("/getSumSignCount")
  @Override
  public Map<String, Object> getSumSignCount(Map<String, Object> requestMap) throws ParseException {
    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
    }
    // 定义输出格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 将字符串转化为日期
    Date date = sdf.parse((String) requestMap.get("date"));
    Map<String, Object> responseMap = new HashMap<String, Object>();
    responseMap.put("continuousSignCount", signUtils.getSumSignCount(customerNo, date));
    return responseMap;
  }

  /**
   * 签到
   *
   * @return
   */
  @ApiDoc(desc = "sign")
  @ShenyuDubboClient("/sign")
  @Override
  public String sign(Map<String, Object> requestMap) throws ParseException {
    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
    }
    // 定义输出格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 将字符串转化为日期
    Date date = sdf.parse((String) requestMap.get("date"));
    if (date == null) {
      date = new DateTime();
    }
    return signUtils.sign(customerNo, date);
  }

  /**
   * 签到结果
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getSignResult")
  @ShenyuDubboClient("/getSignResult")
  @Override
  public boolean getSignResult(Map<String, Object> requestMap) throws ParseException {
    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
    }
    // 定义输出格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 将字符串转化为日期
    Date date = sdf.parse((String) requestMap.get("date"));
    if (date == null) {
      date = new DateTime();
    }
    return signUtils.checkSign(customerNo, date);
  }

  /**
   * 签到信息
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getSignInfo")
  @ShenyuDubboClient("/getSignInfo")
  @Override
  public Map<String, String> getSignInfo(Map<String, Object> requestMap) throws ParseException {

    String customerNo = (String) requestMap.get("customerNo");
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
    }
    // 定义输出格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // 将字符串转化为日期
    Date date = sdf.parse((String) requestMap.get("date"));
    if (date == null) {
      date = new DateTime();
    }
    return signUtils.getSignInfo(customerNo, date);
  }

  @ApiDoc(desc = "getInviteeList")
  @ShenyuDubboClient("/getInviteeList")
  @Override
  public List<?> getInviteeList(Map<String, Object> requestMap) {
    String customerNo = LoginInfoContextHelper.getCustomerNo();
    List<CustomerBase> memberList = customerBaseMapper.selectInviteeList(customerNo);
    return memberList;
  }
}
