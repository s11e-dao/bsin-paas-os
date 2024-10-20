package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.enums.WalletCategory;
import me.flyray.bsin.domain.enums.WalletStatus;
import me.flyray.bsin.domain.enums.WalletType;
import me.flyray.bsin.domain.request.PlatformDTO;
import me.flyray.bsin.domain.request.SysTenantDTO;
import me.flyray.bsin.domain.response.SysUserVO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.PlatformService;
import me.flyray.bsin.facade.service.TenantService;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.mapper.CustomerBaseMapper;
import me.flyray.bsin.infrastructure.mapper.PlatformMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 1、校验平台信息是否重复
     * 2、添加租户信息
     * 3、保存平台信息
     * 4、创建钱包
     * @param platformDTO
     */
    @Override
    @ShenyuDubboClient("/create")
    @ApiDoc(desc = "create")
    @Transactional(rollbackFor = BusinessException.class)
    public void createPlatform(PlatformDTO platformDTO) {
        log.debug("请求PlatformService.createPlatform,参数:{}", platformDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        // 1、校验信息
        LambdaQueryWrapper<Platform> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Platform::getPlatformName, platformDTO.getPlatformName());
        List<Platform> platforms = platformMapper.selectList(queryWrapper);
        if(platforms.size() > 0){
            throw new BusinessException("PLATFORM_NAME_ALREADY_EXIST");
        }
        String platformNo = BsinSnowflake.getId();
        // 2、添加租户
        SysTenantDTO sysTenantDTO = new SysTenantDTO();
        sysTenantDTO.setTenantCode(platformNo);
        sysTenantDTO.setTenantName(platformDTO.getPlatformName());
        sysTenantDTO.setUsername(platformDTO.getUsername());
        sysTenantDTO.setPassword(platformDTO.getPassword());
        sysTenantDTO.setProductCode(platformDTO.getProductCode());
        SysTenant sysTenant = tenantService.add(sysTenantDTO);

        String tenantId = sysTenant.getTenantId();

        // 3、创建平台
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
        wallet.setType(WalletType.DEFAULT.getCode());  // 1、默认钱包
        wallet.setWalletTag("GATHER");
        wallet.setStatus(WalletStatus.NORMAL.getCode());    // 正常
        wallet.setCategory(WalletCategory.MPC.getCode());
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
        Platform platformReq = BsinServiceContext.getReqBodyDto(Platform.class, requestMap);
        LambdaQueryWrapper<Platform> warapper = new LambdaQueryWrapper<>();
        warapper.eq(Platform::getTenantId, platformReq.getTenantId());
        warapper.eq(Platform::getUsername, platformReq.getUsername());
        Platform platform = platformMapper.selectOne(warapper);
        if(platform == null){
            throw new BusinessException(ResponseCode.USERNAME_PASSWORD_ERROR);
        }

        Map res = new HashMap<>();
        // userService
        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(requestMap,sysUser);
        sysUser.setBizRoleType(BizRoleType.TENANT.getCode());
        SysUserVO sysUserVO = userService.login(sysUser);
        BeanUtil.beanToMap(sysUserVO);
        res.putAll(BeanUtil.beanToMap(sysUserVO));
        res.put("platformInfo", BeanUtil.beanToMap(platform));
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
    public Platform getDetail(Map<String, Object> requestMap) {
        String platformNo = MapUtils.getString(requestMap, "serialNo");
        Platform platform = platformMapper.selectById(platformNo);
        //        customerInfo.setWalletPrivateKey(null);
        return platform;
    }

    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(Map<String, Object> requestMap) {
        Object paginationObj =  requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj,pagination);
        Page<Platform> page = new Page<>(pagination.getPageNum(),pagination.getPageSize());
        LambdaQueryWrapper<Platform> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(Platform::getCreateTime);
        IPage<Platform> pageList = platformMapper.selectPage(page,warapper);
        return pageList;
    }

}
