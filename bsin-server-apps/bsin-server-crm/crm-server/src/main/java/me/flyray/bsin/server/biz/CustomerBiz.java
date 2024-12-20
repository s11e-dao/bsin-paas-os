package me.flyray.bsin.server.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import me.flyray.bsin.domain.entity.CustomerIdentity;
import me.flyray.bsin.domain.entity.DisInviteRelation;
import me.flyray.bsin.domain.entity.SysAgent;
import me.flyray.bsin.enums.AuthMethod;
import me.flyray.bsin.infrastructure.mapper.CustomerIdentityMapper;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.domain.enums.CustomerType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.EquityService;
import me.flyray.bsin.infrastructure.mapper.CustomerBaseMapper;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.utils.UniqueInviteCodeGenerator;

import static me.flyray.bsin.constants.ResponseCode.INVITE_CODE_ERROR;

/**
 * @author bolei
 * @date 2023/8/7 10:33
 * @desc
 */
@Component
public class CustomerBiz {

  @Autowired private CustomerBaseMapper customerBaseMapper;
  @Autowired private CustomerIdentityMapper customerIdentityMapper;

  @Value("${bsin.jiujiu.tenantId}")
  private String jiujiuTenantId;

  @Value("${bsin.jiujiu.merchantNo}")
  private String jiujiumerchantNo;

  @DubboReference(version = "dev")
  private EquityService equityService;

  @Autowired
  private InviteRelationBiz inviteRelationBiz;

  public CustomerBase login(CustomerBase customerBase) {
    // 客户用户名唯一，查询客户信息
    LambdaQueryWrapper<CustomerBase> warapper = new LambdaQueryWrapper<>();
    warapper.eq(CustomerBase::getTenantId, customerBase.getTenantId());
    warapper.eq(CustomerBase::getUsername, customerBase.getUsername());
    CustomerBase customerInfo = customerBaseMapper.selectOne(warapper);
    if (customerInfo == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_ERROR);
    }
    if (!customerInfo.getPassword().equals(customerBase.getPassword())) {
      throw new BusinessException(ResponseCode.USERNAME_PASSWORD_ERROR);
    }
    return customerInfo;
  }

  public CustomerBase getCustomerByOpenId(String openId) {
    // 客户用户名唯一，查询客户信息
    LambdaQueryWrapper<CustomerBase> warapper = new LambdaQueryWrapper<>();
    warapper.eq(CustomerBase::getCredential, openId);
    return customerBaseMapper.selectOne(warapper);
  }

  public void updateCustomerBase(CustomerBase customerBase) {
    customerBaseMapper.updateById(customerBase);
  }

  /**
   * @param customerBase
   * @return
   */
  public CustomerBase register(CustomerBase customerBase, SysAgent sysAgent) throws UnsupportedEncodingException {
    String inviteCode = customerBase.getInviteCode();
    // 客户用户名在商户下唯一，查询客户信息
    LambdaQueryWrapper<CustomerBase> warapper = new LambdaQueryWrapper<>();
    warapper.eq(CustomerBase::getTenantId, customerBase.getTenantId());
    if (AuthMethod.WECHAT.getType().equals(customerBase.getAuthMethod())) {
      warapper.eq(CustomerBase::getCredential, customerBase.getCredential());
      if (customerBase.getUsername() == null) {
        customerBase.setUsername(customerBase.getCredential());
      }
    } else if (AuthMethod.QQ.getType().equals(customerBase.getAuthMethod())) {
      warapper.eq(CustomerBase::getCredential, customerBase.getCredential());
      if (customerBase.getUsername() == null) {
        customerBase.setUsername(customerBase.getCredential());
      }

    } else if (AuthMethod.PHONE.getType().equals(customerBase.getAuthMethod())) {
      warapper.eq(CustomerBase::getPhone, customerBase.getPhone());
      if (customerBase.getUsername() == null) {
        customerBase.setUsername(customerBase.getPhone());
      }

    } else if (AuthMethod.EMAIL.getType().equals(customerBase.getAuthMethod())) {
      warapper.eq(CustomerBase::getEmail, customerBase.getEmail());
      if (customerBase.getUsername() == null) {
        customerBase.setUsername(customerBase.getEmail());
      }
    } else if (AuthMethod.USERNAME.getType().equals(customerBase.getAuthMethod())) {
      warapper.eq(CustomerBase::getUsername, customerBase.getUsername());
    }

    CustomerBase customerInfo = customerBaseMapper.selectOne(warapper);

    if (customerInfo != null) {
      if (ObjectUtils.isNotEmpty(customerBase.getPassword())) {
        if (!customerBase.getPassword().equals(customerInfo.getPassword())) {
          throw new BusinessException(ResponseCode.PASSWORD_ERROR);
        }
      } else {
        if (!ObjectUtils.isNotEmpty(customerBase.getSessionKey())) {
          throw new BusinessException(ResponseCode.PASSWORD_ERROR);
        }
        if (!customerBase.getSessionKey().equals(customerInfo.getSessionKey())) {
          throw new BusinessException(ResponseCode.PASSWORD_ERROR);
        }
      }
      return customerInfo;
    }

    customerBase.setCustomerNo(BsinSnowflake.getId());
    customerBase.setTenantId(customerBase.getTenantId());
    customerBase.setType(CustomerType.PERSONAL.getCode());
    customerBase.setPassword(customerBase.getPassword());
    //    if (customerBase.getNickname() == null) {
    //      customerBase.setNickname(jiujiumerchantNo);
    //    }
    customerBase.setInviteCode(UniqueInviteCodeGenerator.generateUniqueInviteCode(6));
    customerBaseMapper.insert(customerBase);

    // 身份表: crm_customer_identity插入数据
    CustomerIdentity customerIdentity = new CustomerIdentity();
    customerIdentity.setCustomerNo(customerBase.getCustomerNo());
    customerIdentity.setTenantId(customerBase.getTenantId());
    //    customerIdentity.setMerchantNo(customerBase.getMerchantNo());
    customerIdentity.setName(BizRoleType.CUSTOMER.getDesc());
    customerIdentity.setUsername(customerBase.getUsername());
    customerIdentity.setBizRoleType(BizRoleType.CUSTOMER.getCode());
    customerIdentity.setBizRoleTypeNo(customerBase.getCustomerNo());
    customerIdentityMapper.insert(customerIdentity);

    // 根据 inviteCode 插入邀请关系
    if (inviteCode != null) {
      // 根据邀请码，找到邀请人,写入邀请关系
      CustomerBase parentCustomer =
          customerBaseMapper.selectOne(
              new LambdaQueryWrapper<CustomerBase>()
                  .eq(CustomerBase::getTenantId, customerBase.getTenantId())
                  .eq(CustomerBase::getInviteCode, inviteCode));
      if (parentCustomer == null) {
        throw new BusinessException(INVITE_CODE_ERROR);
      }
      // 添加邀请关系
      inviteRelationBiz.addInvite(customerBase,parentCustomer, sysAgent);
    }
    return customerBase;
  }

  /**
   * @param customerNo
   * @return
   */
  public List<CustomerIdentity> getCustomerIdentityList(String customerNo) {
    LambdaUpdateWrapper<CustomerIdentity> wrapper = new LambdaUpdateWrapper<>();
    wrapper.eq(CustomerIdentity::getCustomerNo, customerNo);
    wrapper.eq(CustomerIdentity::getStatus, "1");
    return customerIdentityMapper.selectList(wrapper);
  }


}
