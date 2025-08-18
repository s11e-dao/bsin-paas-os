package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.domain.request.SettlementAccountDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.SettlementAccountService;
import me.flyray.bsin.infrastructure.mapper.CustomerBaseMapper;
import me.flyray.bsin.infrastructure.mapper.MerchantMapper;
import me.flyray.bsin.infrastructure.mapper.SettlementAccountMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@DubboService
@ApiModule(value = "settlementAccount")
@ShenyuDubboService("/settlementAccount")
public class SettlementAccountServiceImpl implements SettlementAccountService {

    @Autowired
    private SettlementAccountMapper settlementAccountMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private CustomerBaseMapper customerBaseMapper;

    @Override
    @ShenyuDubboClient("/setUp")
    @ApiDoc(desc = "setUp")
    public void setUp(SettlementAccountDTO settlementAccountDTO) {
        log.debug("请求MerchantService.saveSettlementAccount,参数:{} ", settlementAccountDTO);
        try {
            SettlementAccount settlementAccount = new SettlementAccount();
            BeanUtils.copyProperties(settlementAccountDTO, settlementAccount);
            LoginUser loginUser = LoginInfoContextHelper.getLoginUser();  // 用户信息
            settlementAccount.setSerialNo(BsinSnowflake.getId());
            settlementAccount.setTenantId(loginUser.getTenantId());
            settlementAccount.setBizRoleTypeNo(loginUser.getBizRoleTypeNo());
            settlementAccount.setBizRoleType(loginUser.getBizRoleType());
            settlementAccount.setCreateBy(loginUser.getBizRoleTypeNo());
            settlementAccount.setCreateTime(new Date());
            settlementAccountMapper.insert(settlementAccount);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("save settlement account error: ", e.getMessage());
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    /**
     * 根据不同业务角色设置不同业务角色的交易密码
     * @param requestMap
     */
    @ShenyuDubboClient("/setTxPassword")
    @ApiDoc(desc = "setTxPassword")
    @Override
    public void setTxPassword(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();  // 用户信息
        String bizRoleType = loginUser.getBizRoleType();

        // 两次输入密码校验
        String txPassword = MapUtils.getString(requestMap, "txPassword");
        String txPasswordConfirm = MapUtils.getString(requestMap, "txPasswordConfirm");
        if (StringUtils.isBlank(txPassword) || StringUtils.isBlank(txPasswordConfirm)) {
            throw new BusinessException("交易密码不能为空");
        }
        if (!txPassword.equals(txPasswordConfirm)) {
            throw new IllegalArgumentException("两次输入的交易密码不一致");
        }

        // 密码加密（如有加密需求，可在此加密）
        // String encryptedPassword = passwordEncoder.encode(txPassword);

        if (BizRoleType.MERCHANT.getCode().equals(bizRoleType)) {
            Merchant merchant = merchantMapper.selectById(loginUser.getBizRoleTypeNo());
            if (merchant == null) {
                throw new BusinessException("商户不存在");
            }
            merchant.setTxPassword(txPassword);
            merchantMapper.updateById(merchant);
        } else if (BizRoleType.CUSTOMER.getCode().equals(bizRoleType)) {
            CustomerBase customer = customerBaseMapper.selectById(loginUser.getBizRoleTypeNo());
            if (customer == null) {
                throw new BusinessException("用户不存在");
            }
            customer.setTxPassword(txPassword);
            customerBaseMapper.updateById(customer);
        } else {
            throw new BusinessException("不支持的业务角色类型");
        }
    }

    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    @Override
    public void edit(SettlementAccountDTO settlementAccountDTO) {
        log.debug("请求MerchantService.editSettlementAccount,参数:{} ", settlementAccountDTO);
        try {
            SettlementAccount settlementAccount = new SettlementAccount();
            BeanUtils.copyProperties(settlementAccountDTO, settlementAccount);

            LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
            settlementAccount.setUpdateBy(user.getUpdateBy());
            settlementAccount.setUpdateTime(new Date());
            settlementAccountMapper.updateById(settlementAccount);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("edit settlement account error: ", e.getMessage());
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    @ShenyuDubboClient("/delete")
    @ApiDoc(desc = "delete")
    @Override
    public void delete(SettlementAccountDTO settlementAccountDTO) {
        log.debug("请求MerchantService.deleteSettlementAccount,参数: {}", settlementAccountDTO);
        try {
            // 短信验证
            LoginUser loginUser = LoginInfoContextHelper.getLoginUser();  // 用户信息
            SettlementAccount settlementAccount = settlementAccountMapper.selectById(settlementAccountDTO.getSerialNo());
            if (settlementAccount == null) {
                throw new BusinessException("SETTLEMENT_ACCOUNT_NOT_FOUND");
            }
            settlementAccount.setUpdateBy(loginUser.getUpdateBy());
            settlementAccount.setUpdateTime(new Date());
            settlementAccountMapper.deleteById(settlementAccount);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception e) {
            log.debug("delete settlement account error: ", e.getMessage());
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    /**
     * 查询业务角色的结算信息
     * 每个业务角色只有一个账号
     *
     * @param requestMap
     * @return
     */
    @ShenyuDubboClient("/getDetail")
    @ApiDoc(desc = "getDetail")
    @Override
    public SettlementAccount getDetail(Map<String, Object> requestMap) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();  // 用户信息
        LambdaQueryWrapper<SettlementAccount> warapper = new LambdaQueryWrapper<>();
        warapper.eq(SettlementAccount::getBizRoleTypeNo, loginUser.getBizRoleTypeNo());
        SettlementAccount settlementAccount = settlementAccountMapper.selectOne(warapper);
        return settlementAccount;
    }

    @ShenyuDubboClient("/getList")
    @ApiDoc(desc = "getList")
    @Override
    public List<SettlementAccount> getList(SettlementAccountDTO settlementAccountDTO) {
        log.debug("请求MerchantService.pageList,参数:{} ", settlementAccountDTO);
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        LambdaQueryWrapper<SettlementAccount> warapper = new LambdaQueryWrapper<>();
        warapper.eq(SettlementAccount::getBizRoleTypeNo, loginUser.getBizRoleTypeNo());
        warapper.eq(SettlementAccount::getTenantId, loginUser.getTenantId());
        return settlementAccountMapper.selectList(warapper);
    }

    @ShenyuDubboClient("/getPageList")
    @ApiDoc(desc = "getPageList")
    @Override
    public IPage<?> getPageList(SettlementAccountDTO settlementAccountDTO) {
        log.debug("请求MerchantService.pageList,参数:{} ", settlementAccountDTO);
        LoginUser user = LoginInfoContextHelper.getLoginUser();  // 用户信息
        int current = settlementAccountDTO.getCurrent() == null ? 1 : settlementAccountDTO.getCurrent();
        int size = settlementAccountDTO.getSize() == null ? 10 : settlementAccountDTO.getSize();
        LambdaQueryWrapper<SettlementAccount> warapper = new LambdaQueryWrapper<>();
        warapper.eq(SettlementAccount::getBizRoleTypeNo, user.getBizRoleTypeNo());
        warapper.eq(SettlementAccount::getBizRoleType, user.getBizRoleType());
        warapper.eq(SettlementAccount::getTenantId, user.getTenantId());
        warapper.orderByDesc(SettlementAccount::getCreateTime);
        return settlementAccountMapper.selectPage(new Page<>(current, size), warapper);
    }

}
