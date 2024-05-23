package me.flyray.bsin.server.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.customer.CustomerConfig;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.customer.CustomerConfigService;
import me.flyray.bsin.infrastructure.mapper.customer.CustomerConfigMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinResultEntity;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
@DubboService
@ApiModule(value = "customerConfig")
@ShenyuDubboService("/customerConfig")
public class CustomerConfigServiceImpl implements CustomerConfigService {
    @Autowired
    private CustomerConfigMapper customerConfigMapper;

    @Override
    @ShenyuDubboClient("/save")
    @ApiDoc(desc = "save")
    public void save(CustomerConfig customerConfig) {
        log.debug("请求CustomerConfigService.save,参数:{} ", customerConfig);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();
            String serialNo = BsinSnowflake.getId();
            customerConfig.setSerialNo(serialNo);
            customerConfig.setTenantId(user.getTenantId());
            customerConfig.setCreateTime(new Date());
            customerConfig.setCreateBy(user.getUserId());
            customerConfig.setBizRoleNo(user.getBizRoleNo());
            customerConfig.setBizRoleType(user.getBizRoleType());
            customerConfigMapper.insert(customerConfig);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/getConfigInfo")
    @ApiDoc(desc = "getConfigInfo")
    public CustomerConfig getCustomerConfig(CustomerConfig customerConfig) {
        log.debug("请求CustomerConfigService.getMerchantConfig,参数:{} ", customerConfig);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();
            QueryWrapper<CustomerConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("business_role_no", user.getBizRoleNo());
            queryWrapper.eq("business_role_type", user.getBizRoleType());
            // TODO 租户ID暂时前端传
            queryWrapper.eq("tenant_id", user.getTenantId());
            customerConfig = customerConfigMapper.selectOne(queryWrapper);
            return customerConfig;
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Override
    public void update(CustomerConfig customerConfig) {
        log.debug("请求CustomerConfigService.update,参数:{} " , customerConfig);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            customerConfig.setUpdateBy(user.getUserId());
            customerConfig.setUpdateTime(new Date());
            customerConfigMapper.updateById(customerConfig);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }
}
