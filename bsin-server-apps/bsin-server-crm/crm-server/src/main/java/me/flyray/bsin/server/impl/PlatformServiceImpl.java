package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.domain.entity.ChainCoin;
import me.flyray.bsin.domain.domain.Platform;
import me.flyray.bsin.domain.entity.Wallet;
import me.flyray.bsin.domain.request.PlatformDTO;
import me.flyray.bsin.infrastructure.mapper.PlatformMapper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
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
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.domain.CustomerBase;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.request.SysTenantDTO;
import me.flyray.bsin.domain.response.SysUserVO;
import me.flyray.bsin.enums.CustomerType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.PlatformService;
import me.flyray.bsin.facade.service.TenantService;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.mapper.CustomerBaseMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author bolei
 * @date 2023/11/1
 * @desc
 */

@Slf4j
@ShenyuDubboService(path = "/platform",timeout = 15000)
@ApiModule(value = "platform")
@Service
public class PlatformServiceImpl implements PlatformService {

    @Autowired
    private CustomerBaseMapper customerBaseMapper;
    @Autowired
    private PlatformMapper platformMapper;

    @DubboReference(version = "dev")
    private TenantService tenantService;
    @DubboReference(version = "dev")
    private UserService userService;

    @Override
    @ShenyuDubboClient("/register")
    @ApiDoc(desc = "register")
    @Transactional(rollbackFor = BusinessException.class)
    public void createPlatform(PlatformDTO platformDTO) {
        log.debug("请求PlatformService.createPlatform,参数:{}", platformDTO);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();
            // 1、校验信息
            QueryWrapper<Platform> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("platform_name", platformDTO.getPlatformName());
            List<Platform> platforms = platformMapper.selectList(queryWrapper);
            if(platforms.size() > 0){
                throw new BusinessException("PLATFORM_NAME_ALREADY_EXIST");
            }

            // 2、添加租户
            SysTenantDTO tenantDTO = new SysTenantDTO();
            tenantDTO.setTenantCode(platformDTO.getTenantCode());
            tenantDTO.setTenantName(platformDTO.getPlatformName());
            tenantDTO.setPassword(platformDTO.getPassword());
            tenantDTO.setProductCode("zgpt"); // 资管平台
            SysTenant tenant = tenantService.add(tenantDTO);
            String tenantId = tenant.getTenantId();

            // 3、创建平台
            String platformNo = BsinSnowflake.getId();
            Platform platform = new Platform();
            BeanUtils.copyProperties(platformDTO, platform);
            platform.setSerialNo(platformNo); // 序列号
            platform.setStatus(1); // 正常
            platform.setType(platform.getType()==null?1:platform.getType());
            platform.setTxPasswordStatus(0);    // 0、未设置支付密码
            platform.setTenantId(tenantId);
            platform.setCreateBy(user.getUserId());
            platform.setCreateTime(new Date());
            platformMapper.insert(platform);

            // 4、创建默认钱包
            String walletNo = BsinSnowflake.getId();
            Wallet wallet = new Wallet();
            wallet.setSerialNo(walletNo);
            wallet.setWalletName(platformDTO.getPlatformName());  // 默认钱包名称
            wallet.setBizRoleType(BizRoleType.TENANT.getCode());   // 客户类型：1、平台
            wallet.setBizRoleTypeNo(platformNo);
            wallet.setType(1);  // 1、默认钱包
            wallet.setWalletTag("GATHER");
            wallet.setStatus(1);    // 正常
            wallet.setCategory(1);  // 钱包分类 1、MVP 2、多签
            wallet.setEnv("EVM");
            wallet.setTenantId(tenantId);
            wallet.setCreateBy(user.getUserId());
            wallet.setCreateTime(new Date());
            // walletMapper.insert(wallet);

            QueryWrapper<ChainCoin> queryCoin = new QueryWrapper();
            queryCoin.eq("status", 1);      // 上架
            queryCoin.eq("type", 1);        // 平台默认
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
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    /**
     * 开通业务租户
     * 1、添加客户信息
     * 2、添加upms租户
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "openTenant")
    @ShenyuDubboClient("/openTenant")
    @Override
    public Map<String, Object> openTenant(Map<String, Object> requestMap) {
        // 参数校验
        CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);
        String tenantName = MapUtils.getString(requestMap, "tenantName");
        String tenantCode = MapUtils.getString(requestMap, "tenantCode");
        String productCode = MapUtils.getString(requestMap, "productCode");
        if (StringUtils.isEmpty(tenantName)){
            throw new BusinessException("租户名称不能为空！");
        }
        // 租户信息
        requestMap.put("tenantName", tenantName);
        requestMap.put("password", customerBase.getPassword());
        requestMap.put("tenantCode", tenantCode);
        // 标识，用于upms中创建租户时判断是否是商户注册，是则用户名和租户名称相等
        requestMap.put("productCode", productCode);
        SysTenantDTO sysTenantDTO = new SysTenantDTO();
        BeanUtil.copyProperties(requestMap,sysTenantDTO);
        SysTenant sysTenant = tenantService.add(sysTenantDTO);
        // 添加客户类型
        customerBase.setType(CustomerType.TENANT.getCode());
        customerBase.setUsername(tenantName);
        customerBase.setTenantId(sysTenant.getTenantId());
        customerBaseMapper.insert(customerBase);
        return RespBodyHandler.setRespBodyDto(sysTenant);
    }

    /**
     * 节点租户登录
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "login")
    @ShenyuDubboClient("/login")
    @Override
    public Map<String, Object> login(Map<String, Object> requestMap) {
        CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);

        LambdaQueryWrapper<CustomerBase> warapper = new LambdaQueryWrapper<>();
        warapper.eq(CustomerBase::getTenantId, customerBase.getTenantId());
        warapper.eq(CustomerBase::getUsername, customerBase.getUsername());
        warapper.eq(CustomerBase::getPassword, customerBase.getPassword());
        CustomerBase customerInfo = customerBaseMapper.selectOne(warapper);
        if(customerInfo == null){
            throw new BusinessException(ResponseCode.USER_PASSWORD_IS_FALSE);
        }

        Map res = new HashMap<>();
        // userService
        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(requestMap,sysUser);
        sysUser.setBizRoleType(BizRoleType.TENANT.getCode());
        SysUserVO sysUserVO = userService.login(sysUser);
        BeanUtil.beanToMap(sysUserVO);
        res.putAll(BeanUtil.beanToMap(sysUserVO));
        res.put("customerInfo",BeanUtil.beanToMap(customerInfo));
        return res;
    }

    /**
     * 编辑
     * tenantService信息
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public Map<String, Object> edit(Map<String, Object> requestMap) {

        return null;
    }

    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public Map<String, Object> getDetail(Map<String, Object> requestMap) {
        String customerNo = MapUtils.getString(requestMap, "customerNo");
        if (customerNo == null) {
            customerNo = LoginInfoContextHelper.getCustomerNo();
        }
        CustomerBase customerInfo = customerBaseMapper.selectById(customerNo);
        //        customerInfo.setWalletPrivateKey(null);
        return RespBodyHandler.setRespBodyDto(customerInfo);
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<CustomerBase> getPageList(Map<String, Object> requestMap) {
        CustomerBase customerBase = BsinServiceContext.getReqBodyDto(CustomerBase.class, requestMap);
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<CustomerBase> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaUpdateWrapper<CustomerBase> warapper = new LambdaUpdateWrapper<>();
        warapper.orderByDesc(CustomerBase::getCreateTime);
        warapper.eq(CustomerBase::getType, CustomerType.TENANT.getCode());
        IPage<CustomerBase> pageList = customerBaseMapper.selectPage(page,warapper);
        return pageList;
    }

}
