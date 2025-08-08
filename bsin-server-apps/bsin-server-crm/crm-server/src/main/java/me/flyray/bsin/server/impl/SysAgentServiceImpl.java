package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.BizRoleStatus;
import me.flyray.bsin.domain.enums.CustomerType;
import me.flyray.bsin.domain.enums.SysAgentCategory;
import me.flyray.bsin.domain.enums.SysAgentLevel;
import me.flyray.bsin.domain.request.SysUserDTO;
import me.flyray.bsin.domain.response.UserResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisTeamRelationService;
import me.flyray.bsin.facade.service.SysAgentService;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.mapper.CustomerIdentityMapper;
import me.flyray.bsin.infrastructure.mapper.MemberMapper;
import me.flyray.bsin.infrastructure.mapper.MerchantConfigMapper;
import me.flyray.bsin.infrastructure.mapper.SysAgentMapper;
import me.flyray.bsin.security.authentication.AuthenticationProvider;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.security.enums.TenantMemberModel;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
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

import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.text.CharSequenceUtil.NULL;
import static me.flyray.bsin.constants.ResponseCode.*;

/** 系统合伙人服务 */
@Slf4j
@ShenyuDubboService(path = "/sysAgent", timeout = 15000)
@ApiModule(value = "sysAgent")
@Service
public class SysAgentServiceImpl implements SysAgentService {

  @Value("${bsin.security.authentication-secretKey}")
  private String authSecretKey;

  @Value("${bsin.security.authentication-expiration}")
  private int authExpiration;

  @Autowired SysAgentMapper sysAgentMapper;
  @Autowired private MemberMapper memberMapper;
  @Autowired private CustomerIdentityMapper customerIdentityMapper;
  @Autowired
  private MerchantConfigMapper merchantConfigMapper;

  @DubboReference(version = "${dubbo.provider.version}")
  private DisTeamRelationService disTeamRelationService;
  @DubboReference(version = "${dubbo.provider.version}")
  private UserService userService;

  /**
   * 企业合伙人登录，个人合伙人无法登录PC端
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "login")
  @ShenyuDubboClient("/login")
  @Override
  public Map<String, Object> login(Map<String, Object> requestMap) {
    String tenantId = MapUtils.getString(requestMap, "tenantId");
    String username = MapUtils.getString(requestMap, "username");
    String password = MapUtils.getString(requestMap, "password");

    // 查询合伙人信息
    LambdaQueryWrapper<SysAgent> warapper = new LambdaQueryWrapper<>();
    warapper.eq(SysAgent::getTenantId, tenantId);
    warapper.eq(SysAgent::getUsername, username);
    warapper.eq(SysAgent::getType, CustomerType.ENTERPRISE.getCode());
    SysAgent sysAgent = sysAgentMapper.selectOne(warapper);

    // TODO 判断是否是合伙人员工
    if (sysAgent == null) {
      throw new BusinessException(ResponseCode.SYS_AGENT_NOT_EXISTS);
    }
    // 判断是否是企业客户，非企业客户无法登录
    if ("2".equals(sysAgent.getType())) {
      throw new BusinessException("999","该账号不支持PC登录");
    }

    // 校验用户密码
    if (!sysAgent.getPassword().equals(password)) {
      throw new BusinessException(ResponseCode.USERNAME_PASSWORD_ERROR);
    }

    LoginUser loginUser = new LoginUser();
    BeanUtil.copyProperties(sysAgent, loginUser);

    // 查询upms用户, 菜单权限是根据upms用户处理的
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

    loginUser.setUsername(sysAgent.getUsername());
    loginUser.setPhone(sysAgent.getPhone());
    loginUser.setBizRoleTypeNo(sysAgent.getSerialNo());
    loginUser.setBizRoleType(BizRoleType.SYS_AGENT.getCode());
    String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
    res.put("sysAgentInfo", sysAgent);
    res.put("token", token);
    // 查询商户信息
    return res;
  }

  /**
   * 后台添加 C端
   * 开通合伙人： 开卡或申请成为会员之后才能成为合伙人角色
   * 开通个人合伙人： 推广员（推广商家）和分销员（分销商品）的区别
   * */
  @ApiDoc(desc = "openSysAgent")
  @ShenyuDubboClient("/openSysAgent")
  @Override
  public SysAgent openSysAgent(Map<String, Object> requestMap) {
    SysAgent sysAgent = BsinServiceContext.getReqBodyDto(SysAgent.class, requestMap);
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    // 判断用户是否成为了平台会员，平台直属商户，会员都属于品牌商户和门店，平台会员挂给直属商户
    String merchantNo = LoginInfoContextHelper.getTenantMerchantNo();

    if (merchantNo == null) {
      throw new BusinessException(MERCHANT_NO_IS_NULL);
    }

    // 会员所属商户包含在 loginUser.getMerchantNo
    if (!memberMapper.exists(
            new LambdaQueryWrapper<Member>()
                    .eq(Member::getTenantId, sysAgent.getTenantId())
                    .eq(Member::getMerchantNo, merchantNo)
                    .eq(Member::getCustomerNo, customerNo))) {
      throw new BusinessException(ResponseCode.MEMBER_NOT_EXISTS);
    }

    // 默认密码 = 空
    if (sysAgent.getPassword() == null) {
      sysAgent.setPassword("123456");
    }
    // 默认用户名 = admin
    if (sysAgent.getUsername() == null) {
      sysAgent.setUsername("admin");
    }
    sysAgent.setType(CustomerType.PERSONAL.getCode());
    sysAgent = addSysAgent(sysAgent, merchantNo, customerNo);
    return sysAgent;
  }

  /**
   * 添加一条待审核记录
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "apply")
  @ShenyuDubboClient("/apply")
  @Override
  public SysAgent apply(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    SysAgent sysAgent = BsinServiceContext.getReqBodyDto(SysAgent.class, requestMap);
    sysAgent.setSerialNo(BsinSnowflake.getId());
    if (sysAgent.getUsername() == null) {
      throw new BusinessException(USER_NAME_ISNULL);
    }
    if (sysAgent.getPassword() == null) {
      throw new BusinessException(PASSWORD_EXISTS);
    }
    sysAgent.setTenantId(loginUser.getTenantId());
    sysAgent.setStatus(BizRoleStatus.TOBE_CERTIFIED.getCode());
    sysAgent.setSerialNo(BsinSnowflake.getId());

    if (sysAgentMapper.insert(sysAgent) == 0) {
      throw new BusinessException(ResponseCode.DATA_BASE_UPDATE_FAILED);
    }
    return sysAgent;
  }

  /**
   * 更新状态
   * 添加一个合伙人身份信息
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "audit")
  @ShenyuDubboClient("/audit")
  @Override
  public void audit(Map<String, Object> requestMap) {
    // 根据审核状态判断
    String sysAgentNo = MapUtils.getString(requestMap, "sysAgentNo");
    String auditFlag = MapUtils.getString(requestMap, "auditFlag");
    String remark = MapUtils.getString(requestMap, "remark");
    boolean isApproved = "1".equals(auditFlag);
    // 客户号不为空，则表示是会员申请成为合伙人，需要插入客户身份表
    SysAgent sysAgent = sysAgentMapper.selectById(sysAgentNo);
    if(isApproved){
      sysAgent.setStatus(BizRoleStatus.NORMAL.getCode());
      sysAgentMapper.updateById(sysAgent);
      if (StringUtils.isNotEmpty(sysAgentNo)) {
        // 身份表: crm_customer_identity插入数据
        CustomerIdentity customerIdentity = new CustomerIdentity();
        customerIdentity.setTenantId(sysAgent.getTenantId());
        customerIdentity.setName(BizRoleType.SYS_AGENT.getDesc());
        customerIdentity.setUsername(sysAgent.getUsername());
        customerIdentity.setBizRoleType(BizRoleType.SYS_AGENT.getCode());
        customerIdentity.setBizRoleTypeNo(sysAgent.getSerialNo());
        customerIdentityMapper.insert(customerIdentity);
      }
      // 如果是平台代理，不需要加入分销团队
      if(SysAgentCategory.DIS_AGENT.getCode().equals(sysAgent.getCategory())){
        requestMap.put("sysAgentNo", sysAgent.getSerialNo());
        requestMap.put("tenantId", sysAgent.getTenantId());
        disTeamRelationService.add(requestMap);
      }
    }else {
      // 更新状态和备注
      sysAgent.setStatus(BizRoleStatus.REJECT.getCode());
      sysAgent.setRemark(remark);
      sysAgentMapper.updateById(sysAgent);
    }
  }

  /**
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  @Transactional
  public SysAgent add(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    SysAgent sysAgent = BsinServiceContext.getReqBodyDto(SysAgent.class, requestMap);
    String tenantId = LoginInfoContextHelper.getTenantId();
    sysAgent.setTenantId(tenantId);
    sysAgent.setType(CustomerType.ENTERPRISE.getCode());
    sysAgent = addSysAgent(sysAgent, NULL, NULL);

    // TODO 调用upms进行组织架构和权限控制处理
    SysUserDTO sysUserDTO = new SysUserDTO();
    sysUserDTO.setTenantId(loginUser.getTenantId());
    // 用户名称
    sysUserDTO.setBizRoleTypeNo(sysAgent.getSerialNo());
    sysUserDTO.setUsername(sysAgent.getUsername());
    sysUserDTO.setOrgName(sysAgent.getAgentName());
    userService.addSysAgentUser(sysUserDTO);
    return sysAgent;
  }

  @ApiDoc(desc = "delete")
  @ShenyuDubboClient("/delete")
  @Override
  public void delete(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    if (sysAgentMapper.deleteById(serialNo) == 0) {
      throw new BusinessException(DATA_BASE_UPDATE_FAILED);
    }
  }

  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public SysAgent edit(Map<String, Object> requestMap) {
    SysAgent sysAgent = BsinServiceContext.getReqBodyDto(SysAgent.class, requestMap);
    if (sysAgentMapper.updateById(sysAgent) == 0) {
      throw new BusinessException(DATA_BASE_UPDATE_FAILED);
    }
    return sysAgent;
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<?> getPageList(Map<String, Object> requestMap) {
    SysAgent sysAgent = BsinServiceContext.getReqBodyDto(SysAgent.class, requestMap);
    String tenantId = sysAgent.getTenantId();
    if (tenantId == null) {
      tenantId = LoginInfoContextHelper.getTenantId();
    }
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<SysAgent> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    String agentLevel = sysAgent.getAgentLevel();
    LambdaQueryWrapper<SysAgent> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(SysAgent::getCreateTime);
    warapper.eq(StringUtils.isNotEmpty(tenantId), SysAgent::getTenantId, tenantId);
    if (SysAgentLevel.GROUND_PROMOTION.getCode().equals(agentLevel)){
      warapper.eq(SysAgent::getAgentLevel, agentLevel);
    }else {
      warapper.ne(SysAgent::getAgentLevel, agentLevel);
    }
    warapper.eq(
        StringUtils.isNotEmpty(sysAgent.getBusinessType()), SysAgent::getBusinessType,
        sysAgent.getBusinessType());
    warapper.eq(
        StringUtils.isNotEmpty(sysAgent.getStatus()), SysAgent::getStatus, sysAgent.getStatus());
    IPage<SysAgent> pageList = sysAgentMapper.selectPage(page, warapper);
    return pageList;
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public SysAgent getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    if(StringUtils.isEmpty(serialNo)){
      serialNo = MapUtils.getString(requestMap, "sysAgentNo");
    }
    SysAgent sysAgent = sysAgentMapper.selectById(serialNo);
    if (sysAgent == null) {
      throw new BusinessException(SYS_AGENT_NOT_EXISTS);
    }
    return sysAgent;
  }

  private SysAgent addSysAgent(SysAgent sysAgent, String merchantNo, String customerNo) {
    Map<String, Object> requestMap = new HashMap<>();
    sysAgent.setSerialNo(BsinSnowflake.getId());
    if (sysAgent.getUsername() == null) {
      throw new BusinessException(USER_NAME_ISNULL);
    }
    if (sysAgent.getPassword() == null) {
      throw new BusinessException(PASSWORD_EXISTS);
    }
    sysAgent.setSerialNo(BsinSnowflake.getId());

    if (sysAgentMapper.insert(sysAgent) == 0) {
      throw new BusinessException(ResponseCode.DATA_BASE_UPDATE_FAILED);
    }
    // 客户号不为空，则表示是会员申请成为合伙人，需要插入客户身份表
    if (StringUtils.isNotEmpty(customerNo)) {
      // 身份表: crm_customer_identity插入数据
      CustomerIdentity customerIdentity = new CustomerIdentity();
      customerIdentity.setCustomerNo(customerNo);
      customerIdentity.setTenantId(sysAgent.getTenantId());
      customerIdentity.setMerchantNo(merchantNo);
      customerIdentity.setName(BizRoleType.SYS_AGENT.getDesc());
      customerIdentity.setUsername(sysAgent.getUsername());
      customerIdentity.setBizRoleType(BizRoleType.SYS_AGENT.getCode());
      customerIdentity.setBizRoleTypeNo(sysAgent.getSerialNo());
      customerIdentityMapper.insert(customerIdentity);
    }

    // 如果是平台代理，不需要加入分销团队
    if(SysAgentCategory.DIS_AGENT.getCode().equals(sysAgent.getCategory())){
      requestMap.put("sysAgentNo", sysAgent.getSerialNo());
      requestMap.put("tenantId", sysAgent.getTenantId());
      disTeamRelationService.add(requestMap);
    }

    return sysAgent;
  }

}
