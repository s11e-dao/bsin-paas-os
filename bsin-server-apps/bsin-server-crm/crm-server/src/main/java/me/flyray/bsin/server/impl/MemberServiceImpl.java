package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.domain.entity.Member;
import me.flyray.bsin.facade.service.MemberService;
import me.flyray.bsin.facade.service.TokenParamService;
import me.flyray.bsin.infrastructure.mapper.MemberGradeMapper;
import me.flyray.bsin.infrastructure.mapper.MemberMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.server.utils.Pagination;
import me.flyray.bsin.server.utils.RespBodyHandler;

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

  @DubboReference(version = "${dubbo.provider.version}")
  private TokenParamService tokenParamService;

  @ApiDoc(desc = "openMember")
  @ShenyuDubboClient("/openMember")
  @Override
  public Map<String, Object> openMember(Map<String, Object> requestMap) {
    Member member = BsinServiceContext.getReqBodyDto(Member.class, requestMap);
    if (!memberMapper.exists(
        new LambdaUpdateWrapper<Member>()
            .eq(Member::getTenantId, member.getTenantId())
            .eq(Member::getMerchantNo, member.getCustomerNo())
            .eq(Member::getCustomerNo, member.getCustomerNo()))) {
      memberMapper.insert(member);
    }

    return RespBodyHandler.RespBodyDto();
  }

  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
    Member member = BsinServiceContext.getReqBodyDto(Member.class, requestMap);
    Object paginationObj =  requestMap.get("pagination");
    Pagination pagination = new Pagination();
    BeanUtil.copyProperties(paginationObj,pagination);
    Page<Member> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    LambdaUpdateWrapper<Member> warapper = new LambdaUpdateWrapper<>();
    warapper.orderByDesc(Member::getCreateTime);
    warapper.eq(Member::getTenantId, loginUser.getTenantId());
    warapper.eq(Member::getMerchantNo, loginUser.getMerchantNo());
    IPage<Member> pageList = memberMapper.selectPage(page, warapper);
    return RespBodyHandler.setRespPageInfoBodyDto(pageList);
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
  public Map<String, Object> getGradeMemberList(Map<String, Object> requestMap) {
    String gradeNo = MapUtils.getString(requestMap, "gradeNo");
    if ((String)requestMap.get("merchantNo") == null) {
      requestMap.put("merchantNo",LoginInfoContextHelper.getMerchantNo());
    }
    // 1.商户发行的数字积分(查询tokenParam)
    Map<String, Object> tokenParamMap = tokenParamService.getDetailByMerchantNo(requestMap);
    String ccy = null;
    BigDecimal balance = new BigDecimal("0");
    if (!"".equals(tokenParamMap.get("data"))) {
      Map<String, Object> tokenParam = (Map<String, Object>) tokenParamMap.get("data");
      ccy = MapUtils.getString(tokenParam, "symbol");
    }
    List<CustomerBase> memberList = memberGradeMapper.selectMemberListByGrade(gradeNo,ccy);
    for (CustomerBase customer : memberList) {

    }
    return RespBodyHandler.setRespBodyListDto(memberList);
  }

  @ApiDoc(desc = "getGradeMemberPageList")
  @ShenyuDubboClient("/getGradeMemberPageList")
  @Override
  public Map<String, Object> getGradeMemberPageList(Map<String, Object> requestMap) {
    Pagination pagination = (Pagination) requestMap.get("pagination");
    Page<CustomerBase> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
    String gradeNo = MapUtils.getString(requestMap, "gradeNo");
    IPage<CustomerBase> memberList = memberGradeMapper.selectMemberPageListByGrade(page, gradeNo);
    return RespBodyHandler.setRespPageInfoBodyDto(memberList);
  }

  @ApiDoc(desc = "getDetail")
  @ShenyuDubboClient("/getDetail")
  @Override
  public Map<String, Object> getDetail(Map<String, Object> requestMap) {
    String serialNo = MapUtils.getString(requestMap, "serialNo");
    Member member = memberMapper.selectById(serialNo);
    return RespBodyHandler.setRespBodyDto(member);
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
  public Map<String, Object> getMemberGradeDetail(Map<String, Object> requestMap) {
    String customerNo = MapUtils.getString(requestMap, "customerNo");
    if (customerNo == null) {
      customerNo = LoginInfoContextHelper.getCustomerNo();
    }
    Grade memberGrade = memberGradeMapper.selectMemberGrade(customerNo);
    return RespBodyHandler.setRespBodyDto(memberGrade);
  }

}
