package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.domain.entity.Member;
import me.flyray.bsin.domain.entity.TokenParam;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.MemberService;
import me.flyray.bsin.facade.service.TokenParamService;
import me.flyray.bsin.infrastructure.mapper.CustomerBaseMapper;
import me.flyray.bsin.infrastructure.mapper.MemberGradeMapper;
import me.flyray.bsin.infrastructure.mapper.MemberMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static me.flyray.bsin.constants.ResponseCode.*;

/**
 * @author bolei
 * @date 2023/8/6 10:12
 * @desc
 */
@Slf4j
@ShenyuDubboService(path = "/member", timeout = 6000)
@ApiModule(value = "member")
@Service
public class MemberServiceImpl implements MemberService {

  @Autowired private MemberGradeMapper memberGradeMapper;

  @Autowired private MemberMapper memberMapper;

  @Autowired private CustomerBaseMapper customerBaseMapper;

  @DubboReference(version = "${dubbo.provider.version}")
  private TokenParamService tokenParamService;

  /**
   * 开通会员，创建会员购买订单后调用
   *
   * @param requestMap 包含会员信息的请求映射
   * @return 会员对象
   * @throws BusinessException 如果客户编号为空或客户信息不存在，则抛出业务异常
   */
  @ApiDoc(desc = "openMember")
  @ShenyuDubboClient("/openMember")
  @Override
  public Map<String, Object> openMember(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Member member = BsinServiceContext.getReqBodyDto(Member.class, requestMap);
    String customerNo = loginUser.getCustomerNo();
    if (customerNo == null) {
      customerNo = member.getCustomerNo();
      if (customerNo == null) {
        throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
      }
    }
    member.setCustomerNo(customerNo);
    if (member.getTenantId() == null) {
      member.setTenantId(LoginInfoContextHelper.getTenantId());
    }
    String merchantNo = MapUtils.getString(requestMap, "merchantNo");
    if (merchantNo == null) {
      merchantNo = loginUser.getMerchantNo();
    }
    member.setMerchantNo(merchantNo);
    // 检查客户信息是否存在
    if (!customerBaseMapper.exists(
        new LambdaUpdateWrapper<CustomerBase>()
            .eq(CustomerBase::getTenantId, member.getTenantId())
            .eq(CustomerBase::getCustomerNo, customerNo))) {
      throw new BusinessException(CUSTOMER_ERROR);
    }
    if (!memberMapper.exists(
        new LambdaUpdateWrapper<Member>()
            .eq(Member::getTenantId, member.getTenantId())
            .eq(Member::getMerchantNo, merchantNo)
            .eq(Member::getCustomerNo, customerNo))) {
      memberMapper.insert(member);
    } else {
      member =
          memberMapper.selectOne(
              new LambdaUpdateWrapper<Member>()
                  .eq(Member::getTenantId, member.getTenantId())
                  .eq(Member::getMerchantNo, merchantNo)
                  .eq(Member::getCustomerNo, customerNo));
    }
    return BeanUtil.beanToMap(member);
  }

  /**
   * 编辑会员，支付成功后回调
   *
   * @param requestMap 包含会员信息的请求映射
   * @return 会员对象
   * @throws BusinessException 如果客户编号为空或客户信息不存在，则抛出业务异常
   */
  @ApiDoc(desc = "edit")
  @ShenyuDubboClient("/edit")
  @Override
  public Map<String, Object> edit(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    Member member = BsinServiceContext.getReqBodyDto(Member.class, requestMap);
    if (memberMapper.updateById(member) < 1) {
      throw new BusinessException(DATA_BASE_UPDATE_FAILED);
    }
    return BeanUtil.beanToMap(member);
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public IPage<?> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Member member = BsinServiceContext.getReqBodyDto(Member.class, requestMap);
    Object paginationObj = requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj, pagination);
    Page<Member> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaQueryWrapper<Member> warapper = new LambdaQueryWrapper<>();
    warapper.orderByDesc(Member::getCreateTime);
    warapper.eq(Member::getTenantId, loginUser.getTenantId());
    warapper.eq(
        ObjectUtils.isNotEmpty(loginUser.getMerchantNo()),
        Member::getMerchantNo,
        loginUser.getMerchantNo());
    IPage<Member> pageList = memberMapper.selectPage(page, warapper);
    return pageList;
  }

  /**
   * 查询某一等级下的会员
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getGradeMemberList")
  @ShenyuDubboClient("/getGradeMemberList")
  @Override
  public List<?> getGradeMemberList(Map<String, Object> requestMap) {
    String gradeNo = MapUtils.getString(requestMap, "gradeNo");
    if ((String) requestMap.get("merchantNo") == null) {
      requestMap.put("merchantNo", LoginInfoContextHelper.getMerchantNo());
    }
    // 1.商户发行的数字积分(查询tokenParam)
    TokenParam tokenParamMap = tokenParamService.getDetailByMerchantNo(requestMap);
    String ccy = tokenParamMap.getSymbol();

    List<CustomerBase> memberList = memberGradeMapper.selectMemberListByGrade(gradeNo, ccy);
    for (CustomerBase customer : memberList) {}

    return memberList;
  }

  @ApiDoc(desc = "getGradeMemberPageList")
  @ShenyuDubboClient("/getGradeMemberPageList")
  @Override
  public IPage<?> getGradeMemberPageList(Map<String, Object> requestMap) {
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<CustomerBase> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    String gradeNo = MapUtils.getString(requestMap, "gradeNo");
    IPage<CustomerBase> memberList = memberGradeMapper.selectMemberPageListByGrade(page, gradeNo);
    return memberList;
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Member getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    Member member = memberMapper.selectById(serialNo);
    return member;
  }

  /**
   * 获取社区居民数据 1、查询会员的等级
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getMemberGradeDetail")
  @ShenyuDubboClient("/getMemberGradeDetail")
  @Override
  public Grade getMemberGradeDetail(Map<String, Object> requestMap) {
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
    }
    Grade memberGrade = memberGradeMapper.selectMemberGrade(customerNo);
    return memberGrade;
  }

  @Override
  public List<String> getCustomerNoByGradeNos(Map<String, Object> requestMap) {
    List<String> gradeNos = (List<String>) requestMap.get("gradeNos");
    return memberMapper.getCustomerNoByGradeNos(gradeNos);
  }
}
