package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.CustomerType;
import me.flyray.bsin.domain.enums.StoreType;
import me.flyray.bsin.domain.request.PlatformDTO;
import me.flyray.bsin.domain.request.SysTenantDTO;
import me.flyray.bsin.domain.response.UserResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.MerchantService;
import me.flyray.bsin.facade.service.PlatformService;
import me.flyray.bsin.facade.service.TenantService;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.mapper.MerchantMapper;
import me.flyray.bsin.infrastructure.mapper.PlatformMapper;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static me.flyray.bsin.constants.ResponseCode.GRADE_NOT_EXISTS;

/**
 * 平台服务实现类
 * 提供平台的创建、登录、查询等核心功能
 * 
 * @author bolei
 * @date 2023/11/1
 */
@Slf4j
@ShenyuDubboService(path = "/platform", timeout = 15000)
@ApiModule(value = "platform")
@Service
public class PlatformServiceImpl implements PlatformService {

    // 常量定义
    private static final int PLATFORM_STATUS_NORMAL = 1;
    private static final int DEFAULT_PLATFORM_TYPE = 1;
    private static final int TX_PASSWORD_NOT_SET = 0;
    private static final String PLATFORM_NAME_ALREADY_EXIST = "PLATFORM_NAME_ALREADY_EXIST";

    // 配置属性
    @Value("${bsin.security.authentication-secretKey}")
    private String authSecretKey;

    @Value("${bsin.security.authentication-expiration}")
    private int authExpiration;

    // 依赖注入
    @Autowired
    private PlatformMapper platformMapper;
    
    @Autowired
    private MerchantMapper merchantMapper;

    @DubboReference(version = "dev")
    private TenantService tenantService;
    
    @DubboReference(version = "dev")
    private UserService userService;
    
    @Autowired
    private MerchantService merchantService;

    /**
     * 创建平台
     * 包含以下步骤：
     * 1. 校验平台名称是否重复
     * 2. 创建租户信息
     * 3. 保存平台信息
     * 4. 为平台添加默认商户
     * 
     * @param platformDTO 平台创建请求数据
     * @throws BusinessException 当平台名称已存在时抛出
     */
    @Override
    @ShenyuDubboClient("/create")
    @ApiDoc(desc = "create")
    @Transactional(rollbackFor = BusinessException.class)
    public void createPlatform(PlatformDTO platformDTO) {
        log.info("开始创建平台，平台名称：{}", platformDTO.getPlatformName());
        
        final LoginUser currentUser = LoginInfoContextHelper.getLoginUser();
        
        // 1. 校验平台名称是否重复
        LambdaQueryWrapper<Platform> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Platform::getPlatformName, platformDTO.getPlatformName());
        List<Platform> existingPlatforms = platformMapper.selectList(queryWrapper);
        
        if (!existingPlatforms.isEmpty()) {
            log.warn("平台创建失败：平台名称已存在，platformName: {}", platformDTO.getPlatformName());
            throw new BusinessException(PLATFORM_NAME_ALREADY_EXIST);
        }
        
        // 2. 生成平台序列号
        final String platformNo = BsinSnowflake.getId();
        
        // 3. 创建租户
        SysTenantDTO sysTenantDTO = new SysTenantDTO();
        sysTenantDTO.setTenantCode(platformNo);
        sysTenantDTO.setTenantName(platformDTO.getPlatformName());
        sysTenantDTO.setUsername(platformDTO.getUsername());
        sysTenantDTO.setPassword(platformDTO.getPassword());
        sysTenantDTO.setProductCode(platformDTO.getProductCode());
        SysTenant sysTenant = tenantService.add(sysTenantDTO);
        
        final String tenantId = sysTenant.getTenantId();
        
        // 4. 创建平台实体
        Platform platform = new Platform();
        BeanUtils.copyProperties(platformDTO, platform);
        platform.setSerialNo(platformNo);
        platform.setStatus(PLATFORM_STATUS_NORMAL);
        platform.setType(Optional.ofNullable(platform.getType()).orElse(DEFAULT_PLATFORM_TYPE));
        platform.setTxPasswordStatus(TX_PASSWORD_NOT_SET);
        platform.setTenantId(tenantId);
        platform.setCreateBy(currentUser.getUserId());
        platform.setCreateTime(new Date());
        platformMapper.insert(platform);

        // 4、创建默认钱包
        String walletNo = BsinSnowflake.getId();
//        Wallet wallet = new Wallet();
//        wallet.setSerialNo(walletNo);
//        wallet.setWalletName(platformDTO.getPlatformName());  // 默认钱包名称
//        wallet.setBizRoleType(BizRoleType.TENANT.getCode());   // 客户类型：1、平台
//        wallet.setBizRoleTypeNo(platformNo);
//        wallet.setType(WalletType.DEFAULT.getCode());  // 1、默认钱包
//        wallet.setWalletTag("GATHER");
//        wallet.setStatus(WalletStatus.NORMAL.getCode());    // 正常
//        wallet.setCategory(WalletCategory.MPC.getCode());
//        wallet.setEnv("EVM");
//        wallet.setTenantId(tenantId);
//        wallet.setCreateBy(user.getUserId());
//        wallet.setCreateTime(new Date());
        // walletMapper.insert(wallet);

//        QueryWrapper<ChainCoin> queryCoin = new QueryWrapper();
//        queryCoin.eq("status", 1);      // 上架
//        queryCoin.eq("type", 1);        // 平台默认
//            List<ChainCoin> chainCoinList = chainCoinMapper.selectList(queryCoin);
//            for(ChainCoin chainCoin : chainCoinList) {
//                // 5、建立平台币种关联关系
//                String customerChainCoinNo = BsinSnowflake.getId();
//                CustomerChainCoin customerChainCoin = new CustomerChainCoin();
//                customerChainCoin.setSerialNo(customerChainCoinNo);
//                customerChainCoin.setChainCoinNo(chainCoin.getSerialNo());
//                customerChainCoin.setBizRoleType(1);       // 1、平台
//                customerChainCoin.setCreateRoleAccountFlag(1); // 是否创建角色钱包账户标识;0、否 1、是
//                customerChainCoin.setCreateUserAccountFlag(1); // 是否创建角色钱包账户标识;0、否 1、是
//                customerChainCoin.setTenantId(tenantId);
//                customerChainCoin.setCreateTime(new Date());
//                customerChainCoin.setCreateBy(user.getUserId());
//                customerChainCoinMapper.insert(customerChainCoin);
//                // 6、创建钱包账户（以平台上架币种为准）
//                // walletAccountBiz.createWalletAccount(wallet,chainCoin.getSerialNo());
//            }
        
        // 5. 为平台添加默认商户
        Map<String, Object> merchantRequestMap = new HashMap<>();
        BeanUtils.copyProperties(platformDTO, merchantRequestMap);
        merchantRequestMap.put("type", CustomerType.UNDER_TENANT_MEMBER.getCode());
        merchantRequestMap.put("merchantName", platformDTO.getPlatformName());
        merchantService.add(merchantRequestMap);
        
        log.info("平台创建成功，平台号：{}，租户ID：{}", platformNo, tenantId);
    }

    /**
     * 平台租户登录
     * 
     * @param requestMap 登录请求参数，包含租户ID、用户名等信息
     * @return 包含token和平台信息的登录结果
     * @throws BusinessException 当用户名密码错误时抛出
     */
    @ApiDoc(desc = "login")
    @ShenyuDubboClient("/login")
    @Override
    public Map<String, Object> login(Map<String, Object> requestMap) {
        log.info("平台用户登录请求");
        
        final Platform platformReq = BsinServiceContext.getReqBodyDto(Platform.class, requestMap);
        
        // 验证平台用户
        LambdaQueryWrapper<Platform> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Platform::getTenantId, platformReq.getTenantId());
        wrapper.eq(Platform::getUsername, platformReq.getUsername());
        Platform platform = platformMapper.selectOne(wrapper);
        
        if (platform == null) {
            log.warn("平台用户登录失败：用户名或密码错误，tenantId: {}, username: {}", 
                    platformReq.getTenantId(), platformReq.getUsername());
            throw new BusinessException(ResponseCode.USERNAME_PASSWORD_ERROR);
        }

        // 获取用户信息
        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(requestMap, sysUser);
        sysUser.setBizRoleType(BizRoleType.TENANT.getCode());
        UserResp userResp = userService.getUserInfo(sysUser);
        sysUser = userResp.getSysUser();
        
        // 构建登录用户对象
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(platformReq.getTenantId());
        loginUser.setUserId(sysUser.getUserId());
        loginUser.setUsername(sysUser.getUsername());
        loginUser.setPhone(sysUser.getPhone());
        loginUser.setBizRoleType(BizRoleType.TENANT.getCode());
        loginUser.setBizRoleTypeNo(platformReq.getTenantId());
        
        // 设置平台对应的直属商户号
        Merchant merchant = merchantMapper.selectOne(
            new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getTenantId, platformReq.getTenantId())
                .eq(Merchant::getType, CustomerType.UNDER_TENANT_MEMBER.getCode())
        );
        loginUser.setTenantMerchantNo(merchant.getSerialNo());
        
        // 生成token
        String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>(BeanUtil.beanToMap(userResp));
        result.put("token", token);
        result.put("platformInfo", BeanUtil.beanToMap(platform));
        
        log.info("平台用户登录成功，用户ID：{}", sysUser.getUserId());
        return result;
    }

    /**
     * @param requestMap 编辑请求参数
     * @return 编辑结果
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public Platform edit(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Platform platform = BsinServiceContext.getReqBodyDto(Platform.class, requestMap);
        platform.setTenantId(loginUser.getTenantId());
        if (platformMapper.updateById(platform) == 0) {
            throw new BusinessException(GRADE_NOT_EXISTS);
        }
        return platform;
    }

    /**
     * 获取平台详情
     * 
     * @param requestMap 请求参数，包含平台序列号
     * @return 平台详细信息
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public Platform getDetail(Map<String, Object> requestMap) {
        final String platformNo = MapUtils.getString(requestMap, "serialNo");
        if (StringUtils.isEmpty(platformNo)) {
            log.warn("获取平台详情失败：平台序列号为空");
            throw new BusinessException(ResponseCode.ID_NOT_ISNULL);
        }
        Platform platform = platformMapper.selectById(platformNo);
        return platform;
    }

    /**
     * 获取平台分页列表
     * 
     * @param requestMap 请求参数，包含分页信息
     * @return 平台分页数据
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        // 获取分页参数
        Object paginationObj = requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj, pagination);
        
        Page<Platform> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapper<Platform> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Platform::getCreateTime);
        
        // 非系统角色只能查看自己租户的平台
        if (!BizRoleType.SYS.getCode().equals(LoginInfoContextHelper.getBizRoleType())) {
            wrapper.eq(Platform::getTenantId, LoginInfoContextHelper.getTenantId());
        }
        
        IPage<Platform> pageList = platformMapper.selectPage(page, wrapper);
        
        log.debug("查询平台列表，总数：{}，当前页：{}", pageList.getTotal(), pageList.getCurrent());
        return pageList;
    }

    /**
     * 获取生态价值分配模型平台信息
     * 优先通过平台号查询，如果平台号为空则通过租户ID查询
     * 
     * @param requestMap 请求参数，包含platformNo或tenantId
     * @return Platform 平台信息，如果未找到返回null
     */
    @ApiDoc(desc = "getEcologicalValueAllocationModel")
    @ShenyuDubboClient("/getEcologicalValueAllocationModel")
    @Override
    public Platform getEcologicalValueAllocationModel(Map<String, Object> requestMap) {
        final String platformNo = MapUtils.getString(requestMap, "platformNo");
        final String tenantId = MapUtils.getString(requestMap, "tenantId");
        
        log.debug("获取生态价值分配模型，platformNo: {}, tenantId: {}", platformNo, tenantId);
        Platform platform = null;
        // 优先通过平台号查询
        if (StringUtils.isNotEmpty(platformNo)) {
            log.debug("通过平台号查询平台信息: {}", platformNo);
            platform = platformMapper.selectById(platformNo);
        }
        // 平台号为空时，通过租户ID查询
        else if (StringUtils.isNotEmpty(tenantId)) {
            log.debug("通过租户ID查询平台信息: {}", tenantId);
            LambdaQueryWrapper<Platform> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Platform::getTenantId, tenantId);
            platform = platformMapper.selectOne(wrapper);
        }
        
        return platform;
    }
}
