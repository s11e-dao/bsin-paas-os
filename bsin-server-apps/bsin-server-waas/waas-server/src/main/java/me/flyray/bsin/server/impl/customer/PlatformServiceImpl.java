package me.flyray.bsin.server.impl.customer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.ChainCoin;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.entity.Wallet;
import me.flyray.bsin.domain.entity.customer.CustomerChainCoin;
import me.flyray.bsin.domain.entity.customer.Platform;
import me.flyray.bsin.domain.request.SysTenantDTO;
import me.flyray.bsin.domain.request.customer.PlatformDTO;
import me.flyray.bsin.domain.response.SysUserVO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.TenantService;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.facade.service.customer.PlatformService;
import me.flyray.bsin.infrastructure.biz.WalletAccountBiz;
import me.flyray.bsin.infrastructure.mapper.ChainCoinMapper;
import me.flyray.bsin.infrastructure.mapper.WalletMapper;
import me.flyray.bsin.infrastructure.mapper.customer.CustomerChainCoinMapper;
import me.flyray.bsin.infrastructure.mapper.customer.PlatformMapper;
import me.flyray.bsin.infrastructure.utils.RedisClientUtil;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* @author Admin
* @description 针对表【crm_platform(平台（租户,例：unionCash）;)】的数据库操作Service实现
* @createDate 2024-05-03 14:16:57
*/

@Slf4j
@DubboService
@ApiModule(value = "platform")
@ShenyuDubboService(value = "/platform",timeout = 120000)
public class PlatformServiceImpl implements PlatformService {
    @Autowired
    private PlatformMapper platformMapper;
    // ${dubbo.provider.version}
    @DubboReference(version = "dev")
    private TenantService tenantService;
    @DubboReference(version = "dev")
    private UserService userService;
    @Autowired
    private WalletAccountBiz walletAccountBiz;
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private ChainCoinMapper chainCoinMapper;
    @Autowired
    private CustomerChainCoinMapper customerChainCoinMapper;

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
            wallet.setBizRoleType(1);   // 客户类型：1、平台
            wallet.setBizRoleNo(platformNo);
            wallet.setType(1);  // 1、默认钱包
            wallet.setWalletTag("GATHER");
            wallet.setStatus(1);    // 正常
            wallet.setCategory(1);  // 钱包分类 1、MVP 2、多签
            wallet.setEnv("EVM");
            wallet.setTenantId(tenantId);
            wallet.setCreateBy(user.getUserId());
            wallet.setCreateTime(new Date());
            walletMapper.insert(wallet);

            QueryWrapper<ChainCoin> queryCoin = new QueryWrapper();
            queryCoin.eq("status", 1);      // 上架
            queryCoin.eq("type", 1);        // 平台默认
            List<ChainCoin> chainCoinList = chainCoinMapper.selectList(queryCoin);
            for(ChainCoin chainCoin : chainCoinList) {
                // 5、建立平台币种关联关系
                String customerChainCoinNo = BsinSnowflake.getId();
                CustomerChainCoin customerChainCoin = new CustomerChainCoin();
                customerChainCoin.setSerialNo(customerChainCoinNo);
                customerChainCoin.setChainCoinNo(chainCoin.getSerialNo());
                customerChainCoin.setBizRoleType(1);       // 1、平台
                customerChainCoin.setCreateRoleAccountFlag(1); // 是否创建角色钱包账户标识;0、否 1、是
                customerChainCoin.setCreateUserAccountFlag(1); // 是否创建角色钱包账户标识;0、否 1、是
                customerChainCoin.setTenantId(tenantId);
                customerChainCoin.setCreateTime(new Date());
                customerChainCoin.setCreateBy(user.getUserId());
                customerChainCoinMapper.insert(customerChainCoin);
                // 6、创建钱包账户（以平台上架币种为准）
                walletAccountBiz.createWalletAccount(wallet,chainCoin.getSerialNo());
            }
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    public void updatePlatform(Platform platform) {
        log.debug("请求PlatformService.updatePlatform,参数:{}", platform);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            QueryWrapper<Platform> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("platform_name", platform.getPlatformName());
            queryWrapper.notIn("serial_no", platform.getSerialNo());
            List<Platform> platforms = platformMapper.selectList(queryWrapper);
            if(platforms.size() > 0){
                throw new BusinessException("PLATFORM_NAME_ALREADY_EXIST");
            }
            platform.setUpdateBy(user.getUserId());
            platformMapper.updateById(platform);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/login")
    @ApiDoc(desc = "login")
    public Map<String,Object> login(PlatformDTO platformDTO) {
        log.debug("请求PlatformService.login,参数:{}", platformDTO);
        Map<String,Object> map = new HashMap<>();
        try{
            QueryWrapper<Platform> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("tenant_id", platformDTO.getTenantId());
            Platform platform = platformMapper.selectOne(queryWrapper);
            if(platform == null){
                throw new BusinessException("PLATFORM_NOT_EXIST");
            }
            SysUser sysUser = new SysUser();
            sysUser.setUsername(platformDTO.getUsername());
            sysUser.setPassword(platformDTO.getPassword());
            sysUser.setTenantId(platformDTO.getTenantId());
            SysUserVO sysUserVO = userService.login(sysUser);

            // 首次登录判断
            if(sysUserVO.getSysUser().getLoginNum()==0){
                map.put("loginFlag",0);
                map.put("userId", sysUserVO.getSysUser().getUserId());
            }else {
                map.put("loginFlag",1);
            }

            String tempToken = BsinSnowflake.getId();
            RedisClientUtil.append(tempToken, JSON.toJSONString(sysUserVO));
            map.put("tempToken", tempToken);
            return map;
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @ShenyuDubboClient("/pageList")
    @ApiDoc(desc = "pageList")
    public Page<Platform> pageList(PlatformDTO platformDTO) {
        log.debug("请求PlatformService.pageList,参数:{}", platformDTO);
        try{
            int current = platformDTO.getCurrent()==null?1:platformDTO.getCurrent();
            int size = platformDTO.getSize()==null?10:platformDTO.getSize();
            QueryWrapper<Platform> queryWrapper = new QueryWrapper<>();
            if(platformDTO.getStatus()!=null){
                queryWrapper.eq("status", platformDTO.getStatus());
            }
            if(platformDTO.getType()!=null){
                queryWrapper.eq("type", platformDTO.getType());
            }
            if(platformDTO.getPlatformName()!=null){
                queryWrapper.like("platform_name", platformDTO.getPlatformName());
            }
            if(platformDTO.getStartTime()!=null){
                queryWrapper.ge("create_time", platformDTO.getStartTime());
            }
            if(platformDTO.getEndTime()!=null){
                queryWrapper.le("create_time", platformDTO.getEndTime());
            }
            return platformMapper.selectPage(new Page<>(current,size),queryWrapper);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }
}




