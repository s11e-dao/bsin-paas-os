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
import me.flyray.bsin.domain.response.UserResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.*;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.redis.provider.BsinCacheProvider;
import me.flyray.bsin.security.authentication.AuthenticationProvider;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.biz.MerchantBiz;
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

    @Value("${bsin.security.authentication-secretKey}")
    private String authSecretKey;
    @Value("${bsin.security.authentication-expiration}")
    private int authExpiration;

    @Autowired public MerchantMapper merchantMapper;
    @Autowired public StoreMapper storeMapper;
    @Autowired public MerchantAuthMapper merchantAuthMapper;
    @Autowired private CustomerIdentityMapper customerIdentityMapper;
    @Autowired public MerchantSubscribeJournalMapper merchantSubscribeJournalMapper;
    @Autowired private MemberMapper memberMapper;
    @Autowired private SysAgentMapper sysAgentMapper;
    @DubboReference(version = "${dubbo.provider.version}")
    private UserService userService;
    @Autowired
    private MerchantBiz merchantBiz;

    /**
     * 在租户下注册商户信息 1、添加商户信息
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "register")
    @ShenyuDubboClient("/register")
    @Override
    @Transactional
    public Merchant register(Map<String, Object> requestMap) {
        Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
        // sys_agent对应的编码 是哪个地推邀请
        String mpVerifyCode = MapUtils.getString(requestMap, "verifyCode");
        String username = MapUtils.getString(requestMap, "username");

        String registerMethod = MapUtils.getString(requestMap, "registerMethod");
        String merchantNo = BsinSnowflake.getId();

        // 公众号验证注册
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

        if (registerNotNeedAudit) {
          merchant.setAuthenticationStatus(AuthenticationStatus.CERTIFIED.getCode());
          merchant.setStatus(BizRoleStatus.NORMAL.getCode());
        } else {
          // 普通注册逻辑
          merchant.setAuthenticationStatus(AuthenticationStatus.TOBE_CERTIFIED.getCode());
          merchant.setStatus(BizRoleStatus.TOBE_CERTIFIED.getCode());
        }

        // 判断邀请码是否有效
        if (StringUtils.isNotEmpty(mpVerifyCode)){
          SysAgent sysAgent = sysAgentMapper.selectById(mpVerifyCode);
          if(sysAgent != null){
            merchant.setSysAgentNo(mpVerifyCode);
          }
        }

        if (merchantMapper.insert(merchant) == 0) {
          throw new BusinessException(ResponseCode.DATA_BASE_UPDATE_FAILED);
        }

        return merchant;

    }

    /**
     * 1、查询商户信息
     * <p>2、查询upms用户
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
        // 查询商户信息
        LambdaQueryWrapper<Merchant> warapper = new LambdaQueryWrapper<>();
        warapper.eq(Merchant::getTenantId, tenantId);
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

        loginUser.setUsername(merchant.getUsername());
        loginUser.setPhone(merchant.getPhone());
        loginUser.setMerchantNo(merchant.getSerialNo());
        Store store = storeMapper.selectOne(new LambdaQueryWrapper<Store>().eq(Store::getTenantId, tenantId)
                .eq(Store::getMerchantNo, merchant.getSerialNo())
                .eq(Store::getType, StoreType.MAIN_STORE.getCode()));
        if(store != null){
            loginUser.setMerchantStoreNo(store.getSerialNo());
            res.put("merchantStoreNo", store.getSerialNo());
        }
        loginUser.setBizRoleType(BizRoleType.MERCHANT.getCode());
        loginUser.setBizRoleTypeNo(merchant.getSerialNo());
        String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
        res.put("merchantInfo", merchant);
        res.put("token", token);
        // 查询商户信息
        return res;
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
          if (!member.getStatus().equals(BizRoleStatus.NORMAL.getCode())) {
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
        merchant = merchantBiz.addMerchant(merchant, customerNo);
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
        merchant = merchantBiz.addMerchant(merchant, NULL);
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

    @ApiDoc(desc = "updateMerchantPayMode")
    @ShenyuDubboClient("/updateMerchantPayMode")
    @Override
    public Merchant updateMerchantPayMode(Map<String, Object> requestMap) {
        String merchantMode = MapUtils.getString(requestMap, "merchantMode");
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");

        if (merchantMode == null || merchantNo == null) {
            throw new BusinessException(PARAM_ERROR);
        }
        Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getSerialNo, merchantNo));
        if (merchant == null) {
            throw new BusinessException(ResponseCode.MERCHANT_NOT_EXISTS);
        }
        merchant.setMerchantMode(merchantMode);
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
            StringUtils.isNotEmpty(merchant.getBusinessType()),Merchant::getBusinessType, merchant.getBusinessType());
        warapper.eq(
            StringUtils.isNotEmpty(merchant.getStatus()), Merchant::getStatus, merchant.getStatus());

        // 合伙伙伴只能看到自己的商户
        if(BizRoleType.SYS_AGENT.getCode().equals(LoginInfoContextHelper.getBizRoleType())){
          merchant.setSysAgentNo(LoginInfoContextHelper.getLoginUser().getBizRoleTypeNo());
        }
        warapper.eq(
                StringUtils.isNotEmpty(merchant.getSysAgentNo()), Merchant::getSysAgentNo, merchant.getSysAgentNo());

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

}
