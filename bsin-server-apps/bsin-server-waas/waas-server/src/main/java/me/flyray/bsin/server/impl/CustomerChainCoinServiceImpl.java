package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.CustomerChainCoin;
import me.flyray.bsin.domain.request.CustomerChainCoinDTO;
import me.flyray.bsin.domain.response.CustomerChainCoinVO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.CustomerChainCoinService;
import me.flyray.bsin.infrastructure.mapper.CustomerChainCoinMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
* @author Admin
* @description 针对表【merchant_chain_coin(商户链上货币;)】的数据库操作Service实现
* @createDate 2024-04-29 15:44:26
*/
@Slf4j
@DubboService
@ApiModule(value = "customerChainCoin")
@ShenyuDubboService("/customerChainCoin")
public class CustomerChainCoinServiceImpl implements CustomerChainCoinService {
    @Autowired
    private CustomerChainCoinMapper customerChainCoinMapper;

    @Override
    @ShenyuDubboClient("/save")
    @ApiDoc(desc = "save")
    public void saveCustomerChainCoin(CustomerChainCoinDTO customerChainCoinDTO) {
        log.debug("请求CustomerChainCoinService.saveCustomerChainCoin,参数:{}", customerChainCoinDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            CustomerChainCoin customerChainCoin = new CustomerChainCoin();
            BeanUtils.copyProperties(customerChainCoinDTO,customerChainCoin);

            customerChainCoin.setTenantId(user.getTenantId());
            customerChainCoin.setBizRoleTypeNo(user.getBizRoleTypeNo());
            customerChainCoin.setBizRoleType(user.getBizRoleType());
            customerChainCoin.setCreateTime(new Date());
            customerChainCoin.setCreateBy(user.getCreateBy());
            customerChainCoinMapper.insert(customerChainCoin);
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
    public void updateCustomerChainCoin(CustomerChainCoinDTO customerChainCoinDTO) {
        log.debug("请求CustomerChainCoinService.updateCustomerChainCoin,参数:{}", customerChainCoinDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        try{
            CustomerChainCoin customerChainCoin = new CustomerChainCoin();
            BeanUtils.copyProperties(customerChainCoinDTO,customerChainCoin);

            customerChainCoin.setUpdateTime(new Date());
            customerChainCoin.setUpdateBy(user.getCreateBy());
            customerChainCoinMapper.updateById(customerChainCoin);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/delete")
    @ApiDoc(desc = "delete")
    public void deleteCustomerChainCoin(CustomerChainCoinDTO customerChainCoinDTO) {
        log.debug("请求CustomerChainCoinService.deleteCustomerChainCoin,参数:{}", customerChainCoinDTO);
        try{
            CustomerChainCoin customerChainCoin = new CustomerChainCoin();
            BeanUtils.copyProperties(customerChainCoinDTO,customerChainCoin);

            LoginUser user = LoginInfoContextHelper.getLoginUser();
            customerChainCoin.setUpdateTime(new Date());
            customerChainCoin.setUpdateBy(user.getUpdateBy());
            customerChainCoinMapper.updateDelFlag(customerChainCoin);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/getList")
    @ApiDoc(desc = "getList")
    public  List<CustomerChainCoin> getCustomerChainCoinList(CustomerChainCoinDTO customerChainCoinDTO) {
        log.debug("请求CustomerChainCoinService.getCustomerChainCoinList,参数:{}", customerChainCoinDTO);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();
            QueryWrapper<CustomerChainCoin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("business_role_no", user.getBizRoleTypeNo());
            queryWrapper.eq("business_role_type", user.getBizRoleType());
            queryWrapper.eq("tenant_id", user.getTenantId());
            queryWrapper.orderByDesc("create_time");
            return customerChainCoinMapper.selectList(queryWrapper);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @Override
    @ShenyuDubboClient("/pageList")
    @ApiDoc(desc = "pageList")
    public Page<CustomerChainCoinVO> pageList(CustomerChainCoinDTO customerChainCoinDTO) {
        log.debug("请求CustomerChainCoinService.pageListByCustomerId,参数:{}", customerChainCoinDTO);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();
            int current = customerChainCoinDTO.getCurrent() == null ? 1 : customerChainCoinDTO.getCurrent();
            int size = customerChainCoinDTO.getSize() == null ? 10 : customerChainCoinDTO.getSize();
            customerChainCoinDTO.setTenantId(user.getTenantId());
            customerChainCoinDTO.setBizRoleTypeNo(user.getBizRoleTypeNo());
            customerChainCoinDTO.setBizRoleType(user.getBizRoleType());
            return customerChainCoinMapper.pageList(new Page<>(current, size), customerChainCoinDTO);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException("SYSTEM_ERROR");
        }
    }
}




