package me.flyray.bsin.server.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.customer.CustomerApiKey;
import me.flyray.bsin.domain.entity.customer.SettlementAccount;
import me.flyray.bsin.domain.request.customer.SettlementAccountDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.customer.SettlementAccountService;
import me.flyray.bsin.infrastructure.biz.SmsBiz;
import me.flyray.bsin.infrastructure.mapper.customer.MerchantMapper;
import me.flyray.bsin.infrastructure.mapper.customer.SettlementAccountMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinResultEntity;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Slf4j
@DubboService
@ApiModule(value = "settlementAccount")
@ShenyuDubboService("/settlementAccount")
public class SettlementAccountServiceImpl implements SettlementAccountService {
    @Autowired
    private SettlementAccountMapper settlementAccountMapper;
    @Autowired
    private SmsBiz smsBiz;

    @Override
    @ShenyuDubboClient("/save")
    @ApiDoc(desc = "save")
    public void saveSettlementAccount(SettlementAccountDTO settlementAccountDTO) {
        log.debug("请求MerchantService.saveSettlementAccount,参数:{} ",settlementAccountDTO);
        try{
            // 短信验证
            smsBiz.verifyCode(settlementAccountDTO.getUniqueKey(), settlementAccountDTO.getValidateCode());
            SettlementAccount settlementAccount = new SettlementAccount();
            BeanUtils.copyProperties(settlementAccountDTO,settlementAccount);

            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            settlementAccount.setSerialNo(BsinSnowflake.getId());
            settlementAccount.setTenantId(user.getTenantId());
            settlementAccount.setBizRoleNo(user.getBizRoleNo());
            settlementAccount.setBizRoleType(user.getBizRoleType());
            settlementAccount.setCreateBy(user.getUserId());
            settlementAccount.setCreateTime(new Date());
            settlementAccountMapper.insert(settlementAccount);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            log.debug("save settlement account error: " , e.getMessage());
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Override
    public void editSettlementAccount(SettlementAccountDTO settlementAccountDTO) {
        log.debug("请求MerchantService.editSettlementAccount,参数:{} " , settlementAccountDTO);
        try{
            // 短信验证
            smsBiz.verifyCode(settlementAccountDTO.getUniqueKey(), settlementAccountDTO.getValidateCode());
            SettlementAccount settlementAccount = new SettlementAccount();
            BeanUtils.copyProperties(settlementAccountDTO,settlementAccount);

            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            settlementAccount.setUpdateBy(user.getUpdateBy());
            settlementAccount.setUpdateTime(new Date());
            settlementAccountMapper.updateById(settlementAccount);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            e.printStackTrace();
            log.debug("edit settlement account error: ", e.getMessage());
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @ShenyuDubboClient("/delete")
    @ApiDoc(desc = "delete")
    @Override
    public void deleteSettlementAccount(SettlementAccountDTO settlementAccountDTO) {
        log.debug("请求MerchantService.deleteSettlementAccount,参数: {}" , settlementAccountDTO);
        try{
            // 短信验证
            smsBiz.verifyCode(settlementAccountDTO.getUniqueKey(), settlementAccountDTO.getValidateCode());

            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            SettlementAccount settlementAccount = settlementAccountMapper.selectById(settlementAccountDTO.getSerialNo());
            if(settlementAccount == null ){
                throw new BusinessException("SETTLEMENT_ACCOUNT_NOT_FOUND");
            }
            settlementAccount.setUpdateBy(user.getUpdateBy());
            settlementAccount.setUpdateTime(new Date());
            settlementAccountMapper.updateDelFlag(settlementAccount);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            log.debug("delete settlement account error: " , e.getMessage());
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @ShenyuDubboClient("/pageList")
    @ApiDoc(desc = "pageList")
    @Override
    public Page<SettlementAccount> pageList(SettlementAccountDTO settlementAccountDTO) {
        log.debug("请求MerchantService.pageList,参数:{} ", settlementAccountDTO);
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            int current = settlementAccountDTO.getCurrent()==null?1:settlementAccountDTO.getCurrent();
            int size = settlementAccountDTO.getSize()==null?10:settlementAccountDTO.getSize();
            QueryWrapper queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("business_role_no",user.getBizRoleNo());
            queryWrapper.eq("business_role_type",user.getBizRoleType());
            queryWrapper.eq("tenant_id",user.getTenantId());
            queryWrapper.orderByDesc("create_time");
            return settlementAccountMapper.selectPage(new Page<>(current,size),queryWrapper);
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            log.debug("get settlement account error: " , e.getMessage());
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

}
