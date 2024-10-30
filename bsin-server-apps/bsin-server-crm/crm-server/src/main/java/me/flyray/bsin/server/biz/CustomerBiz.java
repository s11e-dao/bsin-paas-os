package me.flyray.bsin.server.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import me.flyray.bsin.domain.entity.CustomerIdentity;
import me.flyray.bsin.domain.entity.DisInviteRelation;
import me.flyray.bsin.enums.AuthMethod;
import me.flyray.bsin.infrastructure.mapper.CustomerIdentityMapper;
import me.flyray.bsin.infrastructure.mapper.DisInviteRelationMapper;
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

  @Autowired private DisInviteRelationMapper disInviteRelationMapper;

  @DubboReference(version = "dev")
  private EquityService equityService;

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
  public CustomerBase register(CustomerBase customerBase) throws UnsupportedEncodingException {
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
      DisInviteRelation disInviteRelation = new DisInviteRelation();
      disInviteRelation.setTenantId(customerBase.getCustomerNo());
      // 被邀请人序列号
      disInviteRelation.setCustomerNo(customerBase.getCustomerNo());
      // 父级邀请人序列号
      disInviteRelation.setParentNo(parentCustomer.getCustomerNo());
      disInviteRelation.setInviteLevel(1);
      addInviteRelation(disInviteRelation);

      //      // 查询平台的邀请注册积分奖励规则
      //      Map<String, Object> requestMap = new HashMap<>();
      //      requestMap.put("tenantId", jiujiuTenantId);
      //      requestMap.put("merchantNo", jiujiumerchantNo);
      //      // 找到老用户
      //      LambdaQueryWrapper<CustomerBase> warapperOldCustomer = new LambdaQueryWrapper<>();
      //      warapperOldCustomer.eq(CustomerBase::getTenantId, customerBase.getTenantId());
      //      warapperOldCustomer.eq(CustomerBase::getInviteCode, inviteCode);
      //      CustomerBase oldCustomerInfo = customerBaseMapper.selectOne(warapperOldCustomer);
      //      if (oldCustomerInfo != null) {
      //        requestMap.put("customerNo", oldCustomerInfo.getCustomerNo());
      //        equityService.grant(requestMap);
      //      } else {
      //        // TODO: warning
      //      }
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

  public void addInviteRelation(DisInviteRelation disInviteRelation) {
    // 查看当前邀请人是否还有父级邀请人
    LambdaQueryWrapper<DisInviteRelation> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(DisInviteRelation::getCustomerNo, disInviteRelation.getParentNo());
    // 根据时间排序，确保查出来的第一个是第一父级
    queryWrapper.orderByDesc(DisInviteRelation::getCreateTime);
    List<DisInviteRelation> inviteRelations = disInviteRelationMapper.selectList(queryWrapper);
    // 如果没有就直接将邀请关系存入
    if (inviteRelations.isEmpty()) {
      disInviteRelationMapper.insert(disInviteRelation);
    } else {
      disInviteRelationMapper.insert(disInviteRelation);
      // 添加第二、第三父级 (只有三级，所以最多只取前面2个)
      int count = 2;
      for (DisInviteRelation inviteRelation : inviteRelations) {
        if (count > 3) {
          return;
        }
        inviteRelation.setCustomerNo(disInviteRelation.getCustomerNo());
        // 针对当前被邀请人父级等级上升
        inviteRelation.setInviteLevel(count);
        inviteRelation.setSerialNo(BsinSnowflake.getId());
        inviteRelation.setCreateTime(new Date());
        disInviteRelationMapper.insert(inviteRelation);
        count++;
      }
    }
  }
}
