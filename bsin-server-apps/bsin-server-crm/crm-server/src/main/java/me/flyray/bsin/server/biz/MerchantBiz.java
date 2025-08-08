package me.flyray.bsin.server.biz;

import cn.hutool.core.bean.BeanUtil;
import me.flyray.bsin.domain.entity.CustomerIdentity;
import me.flyray.bsin.domain.entity.Merchant;
import me.flyray.bsin.domain.request.SysUserDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.mapper.CustomerIdentityMapper;
import me.flyray.bsin.infrastructure.mapper.MerchantMapper;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.utils.BsinSnowflake;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.USER_NAME_ISNULL;

@Component
public class MerchantBiz {

    @Autowired
    public MerchantMapper merchantMapper;
    @Autowired private CustomerIdentityMapper customerIdentityMapper;
    @DubboReference(version = "${dubbo.provider.version}")
    private UserService userService;

    public Merchant addMerchant(Merchant merchant, String customerNo) {
        Map<String, Object> requestMap = new HashMap<>();
        merchant.setSerialNo(BsinSnowflake.getId());
        if (merchant.getUsername() == null) {
            throw new BusinessException(USER_NAME_ISNULL);
        }
        //    if (merchant.getPassword() == null) {
        //      throw new BusinessException(PASSWORD_EXISTS);
        //    }
        // 调用upms的商户授权功能，添加权限用户同时开通基础功能, 审核需传商户使用的产品ID
        // 查询出商户信息
        // TODO 先检查支付订单是否存在
        requestMap.put("tenantId", merchant.getTenantId());
        requestMap.put("username", merchant.getUsername());
        requestMap.put("merchantName", merchant.getMerchantName());
        requestMap.put("merchantNo", merchant.getSerialNo());
        requestMap.put("password", merchant.getPassword());
        SysUserDTO sysUserDTO = new SysUserDTO();
        BeanUtil.copyProperties(requestMap, sysUserDTO);
        sysUserDTO.setBizRoleType(BizRoleType.MERCHANT.getCode());
        sysUserDTO.setBizRoleTypeNo(merchant.getSerialNo());
        userService.addMerchantOrStoreUser(sysUserDTO);

        // 客户号不为空，则表示是会员申请成为商户(团长)，需要插入客户身份表
        if (StringUtils.isNotEmpty(customerNo)) {
            // 身份表: crm_customer_identity插入数据
            CustomerIdentity customerIdentity = new CustomerIdentity();
            customerIdentity.setCustomerNo(customerNo);
            customerIdentity.setTenantId(merchant.getTenantId());
            customerIdentity.setMerchantNo(merchant.getSerialNo());
            customerIdentity.setName(BizRoleType.MERCHANT.getDesc());
            customerIdentity.setUsername(merchant.getUsername());
            customerIdentity.setBizRoleType(BizRoleType.MERCHANT.getCode());
            customerIdentity.setBizRoleTypeNo(merchant.getSerialNo());
            customerIdentityMapper.insert(customerIdentity);
        }
        return merchant;
    }

}
