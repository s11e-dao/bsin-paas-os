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
import me.flyray.bsin.domain.enums.AuthenticationStatus;
import me.flyray.bsin.domain.enums.CustomerType;
import me.flyray.bsin.domain.enums.MemberStatus;
import me.flyray.bsin.domain.enums.MerchantStatus;
import me.flyray.bsin.domain.request.SysUserDTO;
import me.flyray.bsin.domain.response.UserResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.DisTeamRelationService;
import me.flyray.bsin.facade.service.SysAgentService;
import me.flyray.bsin.infrastructure.mapper.CustomerIdentityMapper;
import me.flyray.bsin.infrastructure.mapper.MemberConfigMapper;
import me.flyray.bsin.infrastructure.mapper.MemberMapper;
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

import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.text.CharSequenceUtil.NULL;
import static me.flyray.bsin.constants.ResponseCode.*;

/** 系统代理商服务 */
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
  private MemberConfigMapper memberConfigMapper;

  @DubboReference(version = "${dubbo.provider.version}")
  private DisTeamRelationService disTeamRelationService;

  @ApiDoc(desc = "login")
  @ShenyuDubboClient("/login")
  @Override
  public Map<String, Object> login(Map<String, Object> requestMap) {
    String username = MapUtils.getString(requestMap, "username");
    // 查询代理商信息
    LambdaQueryWrapper<SysAgent> warapper = new LambdaQueryWrapper<>();
    warapper.eq(SysAgent::getUsername, username);
    SysAgent sysAgent = sysAgentMapper.selectOne(warapper);

    // TODO 判断是否是代理商员工
    if (sysAgent == null) {
      throw new BusinessException(ResponseCode.SYS_AGENT_NOT_EXISTS);
    }
    LoginUser loginUser = new LoginUser();
    BeanUtil.copyProperties(sysAgent, loginUser);

    // 查询upms用户
    Map res = new HashMap<>();
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

  /** 开通代理商 */
  @ApiDoc(desc = "openSysAgent")
  @ShenyuDubboClient("/openSysAgent")
  @Override
  public SysAgent openSysAgent(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    SysAgent sysAgent = BsinServiceContext.getReqBodyDto(SysAgent.class, requestMap);
    if (sysAgent.getTenantId() == null) {
      sysAgent.setTenantId(loginUser.getTenantId());
      if (sysAgent.getTenantId() == null) {
        throw new BusinessException(ResponseCode.TENANT_ID_NOT_ISNULL);
      }
    }

    String customerNo = loginUser.getCustomerNo();

    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    // 根据会员模型查询客户是否是会员，会员模型决定商户号是多少或是店铺号是多少
    if (!ObjectUtils.isEmpty(merchantNo)) {
      String storeNo = MapUtils.getString(requestMap, "storeNo");
      // 根据商户号查询会员配置信息表的会员模型
      LambdaQueryWrapper<MemberConfig> memberConfigWrapper = new LambdaQueryWrapper<>();
      memberConfigWrapper.eq(MemberConfig::getMerchantId, merchantNo);
      memberConfigWrapper.eq(MemberConfig::getTenantId, loginUser.getTenantId());
      memberConfigWrapper.last("limit 1");
      MemberConfig memberConfig = memberConfigMapper.selectOne(memberConfigWrapper);
      if (TenantMemberModel.UNDER_TENANT.getCode().equals(memberConfig.getModel())) {
        merchantNo = LoginInfoContextHelper.getTenantMerchantNo();
      }
    }else {
      merchantNo = LoginInfoContextHelper.getTenantMerchantNo();
    }

    if (merchantNo == null) {
      throw new BusinessException(MERCHANT_NO_IS_NULL);
    }

    // 会员所属商户包含在 loginUser.getMerchantNo
    if (!memberMapper.exists(
            new LambdaQueryWrapper<Member>()
                    .eq(Member::getTenantId, loginUser.getTenantId())
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

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public SysAgent add(Map<String, Object> requestMap) {
    SysAgent sysAgent = BsinServiceContext.getReqBodyDto(SysAgent.class, requestMap);
    String tenantId = LoginInfoContextHelper.getTenantId();
    sysAgent.setTenantId(tenantId);
    sysAgent.setType(CustomerType.PERSONAL.getCode());
    sysAgent = addSysAgent(sysAgent, NULL, NULL);
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
    String merchantNo = LoginInfoContextHelper.getMerchantNo();
    if (tenantId == null) {
      tenantId = LoginInfoContextHelper.getTenantId();
    }
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<SysAgent> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaQueryWrapper<SysAgent> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(SysAgent::getCreateTime);
    warapper.eq(StringUtils.isNotEmpty(tenantId), SysAgent::getTenantId, tenantId);
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
    // 客户号不为空，则表示是会员申请成为代理商，需要插入客户身份表
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

    // 加入分销团队
    requestMap.put("sysAgentNo", sysAgent.getSerialNo());
    requestMap.put("tenantId", sysAgent.getTenantId());
    DisTeamRelation disTeamRelation = disTeamRelationService.add(requestMap);

    return sysAgent;
  }
}
