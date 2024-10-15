package me.flyray.bsin.server.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import me.flyray.bsin.enums.AuthMethod;
import me.flyray.bsin.security.enums.BizRoleType;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.CustomerInviteRelation;
import me.flyray.bsin.enums.CustomerType;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.EquityService;
import me.flyray.bsin.infrastructure.mapper.CustomerBaseMapper;
import me.flyray.bsin.infrastructure.mapper.CustomerInviteRelationMapper;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.utils.UniqueInviteCodeGenerator;

/**
 * @author bolei
 * @date 2023/8/7 10:33
 * @desc
 */
@Component
public class CustomerBiz {

  @Autowired private CustomerBaseMapper customerBaseMapper;

  @Value("${bsin.jiujiu.tenantId}")
  private String jiujiuTenantId;

  @Value("${bsin.jiujiu.merchantNo}")
  private String jiujiumerchantNo;

  @Autowired private CustomerInviteRelationMapper inviteRelationMapper;

  @DubboReference(version = "dev")
  private EquityService equityService;

  public CustomerBase login(CustomerBase customerBase) {
    // 客户用户名唯一，查询客户信息
    LambdaQueryWrapper<CustomerBase> warapper = new LambdaQueryWrapper<>();
    warapper.eq(CustomerBase::getTenantId, customerBase.getTenantId());
    warapper.eq(CustomerBase::getUsername, customerBase.getUsername());
    warapper.eq(CustomerBase::getPassword, customerBase.getPassword());
    CustomerBase customerInfo = customerBaseMapper.selectOne(warapper);
    if (customerInfo == null) {
      throw new BusinessException(ResponseCode.CUSTOMER_ERROR);
    }
    return customerInfo;
  }


  public CustomerBase getCustomerByOpenId(String openId) {
    // 客户用户名唯一，查询客户信息
    LambdaQueryWrapper<CustomerBase> warapper = new LambdaQueryWrapper<>();
    warapper.eq(CustomerBase::getCredential, openId);
    return customerBaseMapper.selectOne(warapper);
  }


  public  void updateCustomerBase(CustomerBase customerBase) {
    customerBaseMapper.updateById(customerBase);
  }

  /**
   * @param customerBase
   * @return
   */
  public CustomerBase register(CustomerBase customerBase) throws UnsupportedEncodingException {
    String inviteCode = customerBase.getInviteCode();
    // 客户用户名唯一，查询客户信息
    LambdaQueryWrapper<CustomerBase> warapper = new LambdaQueryWrapper<>();
    warapper.eq(CustomerBase::getTenantId, customerBase.getTenantId());

    if (AuthMethod.WECHAT.getType().equals(customerBase.getAuthMethod())) {
      warapper.eq(CustomerBase::getCredential, customerBase.getCredential());

    } else if (AuthMethod.QQ.getType().equals(customerBase.getAuthMethod())) {
      warapper.eq(CustomerBase::getCredential, customerBase.getCredential());

    } else if (AuthMethod.PHONE.getType().equals(customerBase.getAuthMethod())) {
      warapper.eq(CustomerBase::getPhone, customerBase.getPhone());

    } else if (AuthMethod.EMAIL.getType().equals(customerBase.getAuthMethod())) {
      warapper.eq(CustomerBase::getEmail, customerBase.getEmail());

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
        if (ObjectUtils.isNotEmpty(customerBase.getSessionKey())) {
          if (!customerBase.getSessionKey().equals(customerInfo.getSessionKey())) {
            throw new BusinessException(ResponseCode.PASSWORD_ERROR);
          }
        } else {
          throw new BusinessException(ResponseCode.PASSWORD_ERROR);
        }
      }
      return customerInfo;
    }

    customerBase.setCustomerNo(BsinSnowflake.getId());
    customerBase.setTenantId(customerBase.getTenantId());
    customerBase.setType(BizRoleType.CUSTORMER.getCode());
    customerBase.setPassword(customerBase.getPassword());
    customerBase.setInviteCode(UniqueInviteCodeGenerator.generateUniqueInviteCode(6));
    customerBaseMapper.insert(customerBase);

    // 根据 inviteCode 老用户mint积分
    if (inviteCode != null) {
      // 查询平台的邀请注册积分奖励规则
      Map<String, Object> requestMap = new HashMap<>();
      requestMap.put("tenantId", jiujiuTenantId);
      requestMap.put("merchantNo", jiujiumerchantNo);
      // 根据邀请码，找到邀请人,写入邀请关系
      CustomerBase parentCustomer =
          customerBaseMapper.selectOne(
              new LambdaQueryWrapper<CustomerBase>().eq(CustomerBase::getInviteCode, inviteCode));
      if (parentCustomer == null) {
        throw new BusinessException("800000", "邀请码错误！");
      }
      // 添加邀请关系
      CustomerInviteRelation customerInviteRelation = new CustomerInviteRelation();
      customerInviteRelation.setCustomerNo(customerBase.getCustomerNo());
      customerInviteRelation.setParentNo(String.valueOf(parentCustomer.getCustomerNo()));
      customerInviteRelation.setInviteLevel(1);
      addInviteRelation(customerInviteRelation);

      // 找到老用户
      LambdaQueryWrapper<CustomerBase> warapperOldCustomer = new LambdaQueryWrapper<>();
      warapperOldCustomer.eq(CustomerBase::getTenantId, customerBase.getTenantId());
      warapperOldCustomer.eq(CustomerBase::getInviteCode, inviteCode);
      CustomerBase oldCustomerInfo = customerBaseMapper.selectOne(warapperOldCustomer);
      if (oldCustomerInfo != null) {
        requestMap.put("customerNo", oldCustomerInfo.getCustomerNo());
        equityService.grant(requestMap);
      } else {
        // TODO: warning
      }
    }
    return customerBase;
  }

  public void addInviteRelation(CustomerInviteRelation customerInviteRelation) {
    // 查看当前邀请人是否还有父级邀请人
    LambdaQueryWrapper<CustomerInviteRelation> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(CustomerInviteRelation::getCustomerNo, customerInviteRelation.getParentNo());
    // 根据时间排序，确保查出来的第一个是第一父级
    queryWrapper.orderByDesc(CustomerInviteRelation::getCreateTime);
    List<CustomerInviteRelation> inviteRelations = inviteRelationMapper.selectList(queryWrapper);
    // 如果没有就直接将邀请关系存入
    if (inviteRelations.isEmpty()) {
      inviteRelationMapper.insert(customerInviteRelation);
    } else {
      inviteRelationMapper.insert(customerInviteRelation);
      // 添加第二、第三父级 (只有三级，所以最多只取前面2个)
      int count = 2;
      for (CustomerInviteRelation inviteRelation : inviteRelations) {
        if (count > 3) {
          return;
        }
        inviteRelation.setCustomerNo(customerInviteRelation.getCustomerNo());
        // 针对当前被邀请人父级等级上升
        inviteRelation.setInviteLevel(count);
        inviteRelation.setSerialNo(BsinSnowflake.getId());
        inviteRelation.setCreateTime(new Date());
        inviteRelationMapper.insert(inviteRelation);
        count++;
      }
    }
  }
}
