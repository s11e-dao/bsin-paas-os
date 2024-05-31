package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.domain.domain.*;
import me.flyray.bsin.domain.request.MerchantRegisterRequest;
import me.flyray.bsin.domain.request.WalletDTO;
import me.flyray.bsin.facade.service.WalletService;
import me.flyray.bsin.infrastructure.mapper.CustomerBaseMapper;
import me.flyray.bsin.redis.manager.BsinCacheProvider;
import me.flyray.bsin.utils.AESUtils;
import me.flyray.bsin.utils.BsinResultEntity;
import me.flyray.bsin.utils.GoogleAuthenticator;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.request.SysUserDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.CustomerService;
import me.flyray.bsin.facade.service.MerchantService;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.mapper.MerchantMapper;
import me.flyray.bsin.infrastructure.mapper.MerchantSubscribeJournalMapper;
import me.flyray.bsin.security.authentication.AuthenticationProvider;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import me.flyray.bsin.utils.BsinSnowflake;

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
    @Autowired
    private CustomerBaseMapper customerBaseMapper;
    @Autowired
    public MerchantMapper merchantMapper;
    @Autowired
    public MerchantSubscribeJournalMapper merchantSubscribeJournalMapper;
    @Autowired private BsinCacheProvider bsinCacheProvider;

    @DubboReference(version = "${dubbo.provider.version}")
    private CustomerService customerService;
    @DubboReference(version = "${dubbo.provider.version}")
    private UserService userService;
    @DubboReference(version = "${dubbo.provider.version}")
    private WalletService walletService;

    /**
     * 在租户下注册商户信息
     * 1、添加商户信息
     * 2、开通商户钱包（开通钱包标识）
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "register")
    @ShenyuDubboClient("/register")
    @Override
    @Transactional
    public Map<String, Object> register(Map<String, Object> requestMap) {
        Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
        requestMap.put("realName",MapUtils.getString(requestMap, "merchantName"));
        String mpVerifyCode = MapUtils.getString(requestMap, "verifyCode");
        String username = MapUtils.getString(requestMap, "username");
        Map<String, Object> resMap = customerService.merchantRegister(requestMap);
        Map<String, Object> data = (Map<String, Object>) resMap.get("data");
        String customerNo = (String) data.get("customerNo");
        merchant.setCustomerNo(customerNo);

        //BsinCopilot验证码注册逻辑
        String openIdWithMpVerifyCode =
            bsinCacheProvider.get("openIdWithMpVerifyCode:" + mpVerifyCode);
        if (openIdWithMpVerifyCode == null) {
          throw new BusinessException("100000", "验证码错误");
        }
        bsinCacheProvider.set(
            "bsinCopilotCustomerNoWithOpenId:" + openIdWithMpVerifyCode, customerNo);
        bsinCacheProvider.set(
                "bsinCopilotUsernameWithOpenId:" + openIdWithMpVerifyCode, username);

        // 商户名称和登录名称分开
        merchant.setMerchantName(MapUtils.getString(requestMap, "merchantName"));
        // 针对copilot微信分身产品，用户注册无需审核，可直接使用基础产品， 若前端注册消息中带 无需审核字段为 true，注册时直接审核
        boolean registerNotNeedAudit = false;
        String registerNotNeedAuditStr = MapUtils.getString(requestMap, "registerNotNeedAudit");
        if (registerNotNeedAuditStr != null){
            registerNotNeedAudit = Boolean.parseBoolean(registerNotNeedAuditStr);
        }
        if (registerNotNeedAudit){
            merchant.setSerialNo(BsinSnowflake.getId());
            // 1: 待认证  2：认证成功  3：认证失败
            merchant.setAuthenticationStatus("2");
            // 调用upms的商户授权功能，添加权限用户同时开通基础功能, 审核需传商户使用的产品ID
            // 查询出商户信息
            // TODO 先检查支付订单是否存在
            requestMap.put("tenantId",merchant.getTenantId());
            requestMap.put("username",merchant.getUsername());
            requestMap.put("merchantName",merchant.getMerchantName());
            requestMap.put("merchantNo",merchant.getSerialNo());
            SysUserDTO sysUserDTO = new SysUserDTO();
            BeanUtil.copyProperties(requestMap,sysUserDTO);
            userService.addMerchantOrStoreUser(sysUserDTO);
            // 状态：0 正常 1 冻结 2 待审核
            merchant.setStatus("0");
        }
        merchantMapper.insert(merchant);

        //TODO RPC调用创建钱包
        WalletDTO walletDTO = new WalletDTO();
        walletService.createMPCWallet(walletDTO);

        return RespBodyHandler.RespBodyDto();
    }

    @ApiDoc(desc = "login")
    @ShenyuDubboClient("/login")
    @Override
    public Map<String, Object> login(Map<String, Object> requestMap) {

        Map<String, Object> resMap = customerService.merchantLogin(requestMap);
        Map<String, Object> data = (Map<String, Object>) resMap.get("data");
        Map customerInfo = (Map) data.get("customerInfo");
        SysUser sysUser = (SysUser) data.get("sysUser");

        LambdaUpdateWrapper<Merchant> warapper = new LambdaUpdateWrapper<>();
        warapper.eq(Merchant::getCustomerNo, customerInfo.get("customerNo"));
        Merchant merchant = merchantMapper.selectOne(warapper);
        if(merchant == null){
            throw new BusinessException(ResponseCode.MERCHANT_NOT_EXISTS);
        }
        Map res = new HashMap<>();
        res.putAll(data);
        LoginUser loginUser = new LoginUser();
        BeanUtil.copyProperties(merchant, loginUser);
        loginUser.setUserId((String) sysUser.getUserId());
        loginUser.setUsername(merchant.getUsername());
        loginUser.setPhone(merchant.getPhone());
        loginUser.setMerchantNo(merchant.getSerialNo());
        String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
        res.put("merchantInfo",merchant);
        res.put("token",token);
        // 查询商户信息
        return res;
    }

    /**
     * 付费认证，一年认证一次
     * 1、付费
     * 2、为商户添加基础的功能（添加应用角色，为角色添加菜单，将角色授权给岗位）
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "authentication")
    @ShenyuDubboClient("/authentication")
    @Override
    public Map<String, Object> authentication(Map<String, Object> requestMap) {
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        Merchant merchantReq = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
        Merchant merchant = merchantMapper.selectById(merchantNo);
        // 更新商户状态
        merchantReq.setAuthenticationStatus("1");
        merchantReq.setDelFlag("0");
        merchantReq.setCreateTime(new Date());
        merchantReq.setStatus("2");
        merchantReq.setCustomerNo(merchantReq.getCustomerNo());
        merchantReq.setSerialNo(merchant.getSerialNo());
        merchantMapper.updateById(merchantReq);
        return RespBodyHandler.RespBodyDto();
    }

    @ApiDoc(desc = "audit")
    @ShenyuDubboClient("/audit")
    @Override
    @Transactional
    public Map<String, Object> audit(Map<String, Object> requestMap) {
        String merchantNo = (String) requestMap.get("merchantNo");
        String auditFlag = (String) requestMap.get("auditFlag");
        Merchant merchant = merchantMapper.selectById(merchantNo);
        String tenantId = LoginInfoContextHelper.getTenantId();
        if ("1".equals(auditFlag)) {
            // 1: 待认证  2：认证成功  3：认证失败
            merchant.setAuthenticationStatus("2");
            // 调用upms的商户授权功能，添加权限用户同时开通基础功能, 审核需传商户使用的产品ID
            // 查询出商户信息
            // TODO 先检查支付订单是否存在
            requestMap.put("tenantId",tenantId);
            requestMap.put("username",merchant.getMerchantName());
            SysUserDTO sysUserDTO = new SysUserDTO();
            BeanUtil.copyProperties(requestMap,sysUserDTO);
            userService.addMerchantOrStoreUser(sysUserDTO);
            // 状态：0 正常 1 冻结 2 待审核
            merchant.setStatus("0");
        } else if ("0".equals(auditFlag)) {
            merchant.setAuthenticationStatus("3");
        }
        merchantMapper.updateById(merchant);
        return RespBodyHandler.RespBodyDto();
    }

    /**
     * 只能按appId来订阅功能
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "subscribeFunction")
    @ShenyuDubboClient("/subscribeFunction")
    @Override
    public Map<String, Object> subscribeFunction(Map<String, Object> requestMap) {
        String appId = (String) requestMap.get("appId");
        List<String> appFunctionIds = (List<String>) requestMap.get("appFunctionIds");
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        // 功能授权
        userService.authMerchantFunction(requestMap);
        for (String appFunctionId : appFunctionIds) {
            MerchantSubscribeJournal merchantSubscribeJournal = new MerchantSubscribeJournal();
            merchantSubscribeJournal.setMerchantNo(merchantNo);
            merchantSubscribeJournal.setAppId(appId);
            merchantSubscribeJournal.setAppFunctionId(appFunctionId);
            merchantSubscribeJournalMapper.insert(merchantSubscribeJournal);
        }

        return RespBodyHandler.RespBodyDto();
    }


    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public Map<String, Object> delete(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        merchantMapper.deleteById(serialNo);
        return RespBodyHandler.RespBodyDto();
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {
        return null;
    }

    /**
     * 1、查询登录平台的商户
     * 2、c端查询平台的商户
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<Merchant> getPageList(Map<String, Object> requestMap) {
        Merchant merchant = BsinServiceContext.getReqBodyDto(Merchant.class, requestMap);
        String tenantId = LoginInfoContextHelper.getTenantId();
        String merchantNo = LoginInfoContextHelper.getMerchantNo();
        if(merchant.getTenantId() != null){
            tenantId = merchant.getTenantId();
        }
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<Merchant> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<Merchant> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(Merchant::getCreateTime);
        warapper.eq(Merchant::getTenantId, tenantId);
        warapper.eq(StringUtils.isNotEmpty(merchant.getBusinessType()),Merchant::getBusinessType, merchant.getBusinessType());
        warapper.eq(StringUtils.isNotEmpty(merchant.getStatus()),Merchant::getStatus, merchant.getStatus());
        warapper.eq(StringUtils.isNotEmpty(merchantNo),Merchant::getSerialNo, merchantNo);
        IPage<Merchant> pageList = merchantMapper.selectPage(page,warapper);
        return pageList;
    }

    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        String serialNo = MapUtils.getString(requestMap, "serialNo");
        Merchant merchant = merchantMapper.selectById(serialNo);
        return RespBodyHandler.setRespBodyDto(merchant);
    }

    @ApiDoc(desc = "getListByMerchantNos")
    @ShenyuDubboClient("/getListByMerchantNos")
    @Override
    public Map<String, Object> getListByMerchantNos(Map<String, Object> requestMap) {
        List<String> merchantNos = (List<String>) requestMap.get("merchantNos");
        if(merchantNos.size() < 1){
            throw new BusinessException("200000","请求参数不能为空！");
        }
        List<Merchant> merchantList =  merchantMapper.selectBatchIds(merchantNos);
        return RespBodyHandler.setRespBodyListDto(merchantList);
    }

}
