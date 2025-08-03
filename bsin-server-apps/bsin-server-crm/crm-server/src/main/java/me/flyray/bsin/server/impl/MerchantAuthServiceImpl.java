package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.Merchant;
import me.flyray.bsin.domain.entity.MerchantAuth;
import me.flyray.bsin.domain.entity.SettlementAccount;
import me.flyray.bsin.domain.enums.AuthenticationStatus;
import me.flyray.bsin.domain.enums.BusinessModel;
import me.flyray.bsin.domain.enums.MerchantStatus;
import me.flyray.bsin.domain.enums.StoreType;
import me.flyray.bsin.domain.request.SysUserDTO;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.MerchantAuthService;
import me.flyray.bsin.facade.service.MerchantService;
import me.flyray.bsin.facade.service.StoreService;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.mapper.MerchantAuthMapper;
import me.flyray.bsin.infrastructure.mapper.MerchantMapper;
import me.flyray.bsin.infrastructure.mapper.SettlementAccountMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.biz.MerchantBiz;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户认证服务
 */
@Slf4j
@ShenyuDubboService(path = "/merchantAuth", timeout = 6000)
@ApiModule(value = "merchantAuth")
@Service
public class MerchantAuthServiceImpl implements MerchantAuthService {

    @Autowired
    private BsinServiceInvoke bsinServiceInvoke;
    @Autowired
    public MerchantMapper merchantMapper;
    @Autowired public MerchantAuthMapper merchantAuthMapper;
    @Autowired private SettlementAccountMapper settlementAccountMapper;
    @DubboReference(version = "${dubbo.provider.version}")
    private UserService userService;
    @Autowired
    private MerchantBiz merchantBiz;
    @DubboReference(version = "${dubbo.provider.version}")
    private StoreService storeService;
    @Autowired
    private MerchantService merchantService;

    /**
     * 商户认证进件：基础信息、营业执照信息、法人信息分步进件
     * @param requestMap
     */
    @ApiDoc(desc = "apply")
    @ShenyuDubboClient("/apply")
    @Override
    @Transactional
    public void apply(Map<String, Object> requestMap) {
        String tenantId =  MapUtils.getString(requestMap, "tenantId");
        String merchantNo =  MapUtils.getString(requestMap, "merchantNo");
        // 1是注册之后正常认证进件 2是注册同时进件
        String authType =  MapUtils.getString(requestMap, "authType");
        Merchant merchant;
        // 处理不同的认证类型 1 注册之后的认证 2 注册认证一起
        if ("2".equals(authType)) {
            // 2则注册商户信息
            log.info("检测到注册同时进件模式，开始调用商户注册方法");
            try {
                // 调用商户注册方法
                 requestMap.put("registerMethod", "operatorRegister");
                 merchant = merchantService.register(requestMap);
                log.info("商户注册成功，继续处理认证信息");
                
                // 注册成功后，从请求参数中获取商户号（注册方法会设置到requestMap中）
                String registeredMerchantNo = MapUtils.getString(requestMap, "merchantNo");
                if (StringUtils.isNotBlank(registeredMerchantNo)) {
                    merchantNo = registeredMerchantNo;
                    log.info("从注册结果中获取到商户号：{}", merchantNo);
                }
            } catch (Exception e) {
                log.error("商户注册失败：{}", e.getMessage(), e);
                throw new BusinessException("MERCHANT_REGISTER_ERROR", "商户注册失败：" + e.getMessage());
            }
        } else {
            // 1则查询商户信息
            log.info("检测到注册之后正常认证进件模式，查询现有商户信息");
            if (StringUtils.isBlank(merchantNo)) {
                throw new BusinessException("MERCHANT_NO_EMPTY", "商户号不能为空");
            }
            
            // 验证商户是否存在
            merchant = merchantMapper.selectById(merchantNo);
        }

        if (merchant == null) {
            throw new BusinessException("MERCHANT_NOT_EXISTS", "商户不存在，请先注册");
        }

        // 获取或创建认证信息
        MerchantAuth merchantAuth = merchantAuthMapper.selectById(merchantNo);
        if (merchantAuth == null) {
            merchantAuth = BsinServiceContext.getReqBodyDto(MerchantAuth.class, requestMap);
            merchantAuth.setTenantId(tenantId);
            merchantAuth.setSerialNo(BsinSnowflake.getId());
        }

        merchantAuth.setUpdateTime(new Date());
        merchantAuth.setDelFlag("0");

        boolean hasUpdate = false;

        // 处理基础信息
        Object baseInfoObj = MapUtils.getObject(requestMap, "baseInfo");
        if (baseInfoObj != null) {
            MerchantAuth temp = BsinServiceContext.getReqBodyDto(MerchantAuth.class, (Map<String, Object>) baseInfoObj);
            // 只复制基础信息相关字段
            if (temp.getMerchantName() != null) merchantAuth.setMerchantName(temp.getMerchantName());
            if (temp.getLogoUrl() != null) merchantAuth.setLogoUrl(temp.getLogoUrl());
            if (temp.getContactPhone() != null) merchantAuth.setContactPhone(temp.getContactPhone());
            if (temp.getWebsite() != null) merchantAuth.setWebsite(temp.getWebsite());
            if (temp.getMerchantAddress() != null) merchantAuth.setMerchantAddress(temp.getMerchantAddress());
            if (temp.getDescription() != null) merchantAuth.setDescription(temp.getDescription());
            if (temp.getCategory() != null) merchantAuth.setCategory(temp.getCategory());
            if (temp.getMerchantType() != null) merchantAuth.setMerchantType(temp.getMerchantType());

            merchantAuth.setBaseInfoAuthStatus(AuthenticationStatus.TOBE_CERTIFIED.getCode());
            hasUpdate = true;
        }

        // 处理法人信息
        Object legalEntityInfoObj = MapUtils.getObject(requestMap, "legalEntityInfo");
        if (legalEntityInfoObj != null) {
            MerchantAuth temp = BsinServiceContext.getReqBodyDto(MerchantAuth.class, (Map<String, Object>) legalEntityInfoObj);
            // 只复制法人信息相关字段
            if (temp.getLegalPersonName() != null) merchantAuth.setLegalPersonName(temp.getLegalPersonName());
            if (temp.getLegalPersonCredType() != null) merchantAuth.setLegalPersonCredType(temp.getLegalPersonCredType());
            if (temp.getLegalPersonCredNo() != null) merchantAuth.setLegalPersonCredNo(temp.getLegalPersonCredNo());

            merchantAuth.setLegalPersonInfoAuthStatus(AuthenticationStatus.TOBE_CERTIFIED.getCode());
            hasUpdate = true;
        }

        // 处理营业信息
        Object businessInfoObj = MapUtils.getObject(requestMap, "businessInfo");
        if (businessInfoObj != null) {
            MerchantAuth temp = BsinServiceContext.getReqBodyDto(MerchantAuth.class, (Map<String, Object>) businessInfoObj);
            // 只复制营业信息相关字段
            if (temp.getBusinessLicenceImg() != null) merchantAuth.setBusinessLicenceImg(temp.getBusinessLicenceImg());
            if (temp.getBusinessNo() != null) merchantAuth.setBusinessNo(temp.getBusinessNo());
            if (temp.getBusinessScope() != null) merchantAuth.setBusinessScope(temp.getBusinessScope());
            if (temp.getBusinessType() != null) merchantAuth.setBusinessType(temp.getBusinessType());

            merchantAuth.setBusinessInfoAuthStatus(AuthenticationStatus.TOBE_CERTIFIED.getCode());
            hasUpdate = true;
        }

        // 处理结算信息
        Object settlementInfo = MapUtils.getObject(requestMap, "settlementInfo");
        if (settlementInfo != null) {
            SettlementAccount settlementAccount = BsinServiceContext.getReqBodyDto(SettlementAccount.class, (Map<String, Object>) settlementInfo);
            merchantAuth.setBusinessInfoAuthStatus(AuthenticationStatus.TOBE_CERTIFIED.getCode());
            hasUpdate = true;
            settlementAccount.setTenantId(tenantId);
            settlementAccount.setBizRoleType(BizRoleType.MERCHANT.getCode());
            settlementAccount.setBizRoleTypeNo(merchantNo);
            settlementAccount.setDefaultFlag("1");
            settlementAccount.setCategory("1");
            settlementAccountMapper.insert(settlementAccount);
        }

        // 有更新就设置为待审核
        if (hasUpdate) {
            merchantAuth.setAuthStatus(AuthenticationStatus.TOBE_CERTIFIED.getCode());
            merchantAuth.setStatus(MerchantStatus.TOBE_CERTIFIED.getCode());
        }

        // 保存数据
        if (merchantAuth.getCreateTime() == null) {
            merchantAuth.setCreateTime(new Date());
            merchantAuth.setMerchantNo(merchant.getSerialNo());
            merchantAuthMapper.insert(merchantAuth);
        } else {
            merchantAuthMapper.updateById(merchantAuth);
        }
    }

    /**
     * 商户认证审核：分项审核，所有项通过时自动开通
     * @param requestMap
     */
    @ApiDoc(desc = "audit")
    @ShenyuDubboClient("/audit")
    @Override
    @Transactional
    public void audit(Map<String, Object> requestMap) throws Exception {
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        String settlementAccountNo = MapUtils.getString(requestMap, "settlementAccountNo");
        String auditFlag = MapUtils.getString(requestMap, "auditFlag");
        String auditType = MapUtils.getString(requestMap, "auditType");

        LambdaQueryWrapper<MerchantAuth> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantAuth::getMerchantNo, merchantNo);
        MerchantAuth merchantAuth = merchantAuthMapper.selectOne(wrapper);
        if (merchantAuth == null) {
            throw new BusinessException(ResponseCode.MERCHANT_NOT_EXISTS);
        }

        merchantAuth.setUpdateTime(new Date());
        merchantAuth.setRemark(MapUtils.getString(requestMap, "auditRemark", ""));

        boolean isApproved = "1".equals(auditFlag);

        // 更新对应的审核状态
        switch (auditType) {
            case "baseInfo":
                merchantAuth.setBaseInfoAuthStatus(isApproved ? AuthenticationStatus.CERTIFIED.getCode() : AuthenticationStatus.CERTIFIED_FAILURE.getCode());
                break;
            case "legalInfo":
                merchantAuth.setLegalPersonInfoAuthStatus(isApproved ? AuthenticationStatus.CERTIFIED.getCode() : AuthenticationStatus.CERTIFIED_FAILURE.getCode());
                break;
            case "businessInfo":
                merchantAuth.setBusinessInfoAuthStatus(isApproved ? AuthenticationStatus.CERTIFIED.getCode() : AuthenticationStatus.CERTIFIED_FAILURE.getCode());
                break;
            case "settlementInfo":
                merchantAuth.setBusinessInfoAuthStatus(isApproved ? AuthenticationStatus.CERTIFIED.getCode() : AuthenticationStatus.CERTIFIED_FAILURE.getCode());
                // 这里需要更新一下结算表状态
                SettlementAccount settlementAccount = settlementAccountMapper.selectById(settlementAccountNo);
                settlementAccount.setStatus(isApproved ? AuthenticationStatus.CERTIFIED.getCode() : AuthenticationStatus.CERTIFIED_FAILURE.getCode());
                settlementAccountMapper.updateById(settlementAccount);
                break;
        }

        // 检查整体状态
        boolean allApproved = AuthenticationStatus.CERTIFIED.getCode().equals(merchantAuth.getBaseInfoAuthStatus())
                && AuthenticationStatus.CERTIFIED.getCode().equals(merchantAuth.getLegalPersonInfoAuthStatus())
                && AuthenticationStatus.CERTIFIED.getCode().equals(merchantAuth.getBusinessInfoAuthStatus());

        if (allApproved) {
            merchantAuth.setAuthStatus(AuthenticationStatus.CERTIFIED.getCode());
            merchantAuth.setStatus(MerchantStatus.NORMAL.getCode());
        } else if (!isApproved) {
            merchantAuth.setAuthStatus(AuthenticationStatus.CERTIFIED_FAILURE.getCode());
            merchantAuth.setStatus(MerchantStatus.REBUT.getCode());
        }

        merchantAuthMapper.updateById(merchantAuth);

        // 所有项通过时开通功能
        if (allApproved) {
            try {
                // Web3商户创建钱包
                if ("2".equals(String.valueOf(merchantAuth.getCategory()))) {
                    Map<String, Object> walletParams = new HashMap<>();
                    walletParams.put("merchantNo", merchantNo);
                    walletParams.put("tenantId", merchantAuth.getTenantId());
                    bsinServiceInvoke.genericInvoke("WalletService", "createWallet", "dev", walletParams);
                }

                // 创建总店
                Map<String, Object> storeParams = new HashMap<>();
                storeParams.put("merchantNo", merchantNo);
                storeParams.put("type", "1");
                storeParams.put("businessModel", BusinessModel.FRANCHISE.getCode());
                storeParams.put("type", StoreType.MAIN_STORE.getCode());
                storeParams.put("description", merchantAuth.getDescription());
                storeParams.put("tenantId", merchantAuth.getTenantId());
                storeService.openStore(storeParams);
                Merchant merchant = merchantMapper.selectById(merchantAuth.getMerchantNo());
                // 审核通过之后像upms添加商户组织架构
                merchantBiz.addMerchant(merchant, null);
            } catch (Exception e) {
                log.error("商户功能开通失败: {}", e.getMessage());
            }
        }
    }

    /**
     * @param requestMap
     * @throws Exception
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) throws Exception {
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        String tenantId = LoginInfoContextHelper.getTenantId();
        LambdaQueryWrapper<MerchantAuth> warapper = new LambdaQueryWrapper<>();
        warapper.eq(MerchantAuth::getTenantId, tenantId);
        warapper.eq(MerchantAuth::getMerchantNo, merchantNo);
        MerchantAuth merchantAuth = merchantAuthMapper.selectOne(warapper);

        Map<String, Object> result = new HashMap<>();
        
        if (merchantAuth != null) {
            // 构建基础信息
            Map<String, Object> baseInfo = new HashMap<>();
            baseInfo.put("merchantName", merchantAuth.getMerchantName());
            baseInfo.put("logoUrl", merchantAuth.getLogoUrl());
            baseInfo.put("contactPhone", merchantAuth.getContactPhone());
            baseInfo.put("website", merchantAuth.getWebsite());
            baseInfo.put("merchantAddress", merchantAuth.getMerchantAddress());
            baseInfo.put("description", merchantAuth.getDescription());
            baseInfo.put("category", merchantAuth.getCategory());
            baseInfo.put("merchantType", merchantAuth.getMerchantType());
            baseInfo.put("baseInfoAuthStatus", merchantAuth.getBaseInfoAuthStatus());
            
            // 构建法人信息
            Map<String, Object> legalInfo = new HashMap<>();
            legalInfo.put("legalPersonName", merchantAuth.getLegalPersonName());
            legalInfo.put("legalPersonCredType", merchantAuth.getLegalPersonCredType());
            legalInfo.put("legalPersonCredNo", merchantAuth.getLegalPersonCredNo());
            legalInfo.put("legalPersonInfoAuthStatus", merchantAuth.getLegalPersonInfoAuthStatus());
            
            // 构建营业信息
            Map<String, Object> businessInfo = new HashMap<>();
            businessInfo.put("businessLicenceImg", merchantAuth.getBusinessLicenceImg());
            businessInfo.put("businessNo", merchantAuth.getBusinessNo());
            businessInfo.put("businessScope", merchantAuth.getBusinessScope());
            businessInfo.put("businessType", merchantAuth.getBusinessType());
            businessInfo.put("businessInfoAuthStatus", merchantAuth.getBusinessInfoAuthStatus());
            
            result.put("baseInfo", baseInfo);
            result.put("legalInfo", legalInfo);
            result.put("businessInfo", businessInfo);
            
            // 构建结算信息
            Map<String, Object> settlementInfo = new HashMap<>();
            // 查询商户的结算账户信息
            LambdaQueryWrapper<SettlementAccount> settlementWrapper = new LambdaQueryWrapper<>();
            settlementWrapper.eq(SettlementAccount::getTenantId, tenantId);
            settlementWrapper.eq(SettlementAccount::getBizRoleTypeNo, merchantNo);
            settlementWrapper.eq(SettlementAccount::getBizRoleType, BizRoleType.MERCHANT.getCode()); // 商户类型
            settlementWrapper.eq(SettlementAccount::getCategory, "1"); //支付结算账号
            settlementWrapper.eq(SettlementAccount::getDefaultFlag, "1");
            SettlementAccount settlementAccount = settlementAccountMapper.selectOne(settlementWrapper);
            
            if (settlementAccount != null) {
                settlementInfo.put("settlementAccountNo", settlementAccount.getSerialNo());
                settlementInfo.put("accountName", settlementAccount.getAccountName());
                settlementInfo.put("accountNum", settlementAccount.getAccountNum());
                settlementInfo.put("bankName", settlementAccount.getBankName());
                settlementInfo.put("bankBranch", settlementAccount.getBankBranch());
                settlementInfo.put("accountType", settlementAccount.getAccountType());
                settlementInfo.put("swiftCode", settlementAccount.getSwiftCode());
                settlementInfo.put("category", settlementAccount.getCategory());
                settlementInfo.put("defaultFlag", settlementAccount.getDefaultFlag());
                settlementInfo.put("settlementAuthStatus", settlementAccount.getStatus());
            }
            
            result.put("settlementInfo", settlementInfo);
            result.put("overallAuthStatus", merchantAuth.getAuthStatus());
            result.put("overallStatus", merchantAuth.getStatus());
        }
        
        return result;
    }

}
