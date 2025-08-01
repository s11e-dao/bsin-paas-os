package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.*;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.MemberService;
import me.flyray.bsin.infrastructure.mapper.CustomerBaseMapper;
import me.flyray.bsin.infrastructure.mapper.MemberGradeMapper;
import me.flyray.bsin.infrastructure.mapper.MemberMapper;
import me.flyray.bsin.infrastructure.mapper.MerchantConfigMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.security.enums.TenantMemberModel;
import me.flyray.bsin.server.utils.Pagination;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
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

    @Autowired
    private BsinServiceInvoke bsinServiceInvoke;

    @Autowired
    private MemberGradeMapper memberGradeMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private CustomerBaseMapper customerBaseMapper;

    @Autowired
    private MerchantConfigMapper merchantConfigMapper;

    /**
     * 开通会员，创建会员购买订单后调用，也可以直接调用（付费和非付费）
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
        String tenantId = member.getTenantId();
        if (tenantId == null) {
            tenantId = loginUser.getTenantId();
            if (tenantId == null) {
                throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
            }
        }
        String customerNo = member.getCustomerNo();
        if (customerNo == null) {
            customerNo = loginUser.getCustomerNo();
            if (customerNo == null) {
                throw new BusinessException(CUSTOMER_NO_NOT_ISNULL);
            }
        }
        member.setCustomerNo(customerNo);
        if (member.getTenantId() == null) {
            member.setTenantId(LoginInfoContextHelper.getTenantId());
        }
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        if (ObjectUtils.isEmpty(merchantNo)) {
            member.setMerchantNo(LoginInfoContextHelper.getTenantMerchantNo());
        }else {
            String storeNo = MapUtils.getString(requestMap, "storeNo");
            // 根据商户号查询会员配置信息表的会员模型
            LambdaQueryWrapper<MerchantConfig> memberConfigWrapper = new LambdaQueryWrapper<>();
            memberConfigWrapper.eq(MerchantConfig::getMerchantNo, merchantNo);
            memberConfigWrapper.eq(MerchantConfig::getTenantId, tenantId);
            memberConfigWrapper.last("limit 1");
            MerchantConfig memberConfig = merchantConfigMapper.selectOne(memberConfigWrapper);
            if (TenantMemberModel.UNDER_MERCHANT.getCode().equals(memberConfig.getMemberModel())) {
                member.setMerchantNo(merchantNo);
            } else if (TenantMemberModel.UNDER_STORE.getCode().equals(memberConfig.getMemberModel())) {
                member.setStoreNo(storeNo);
            }
        }
        LambdaQueryWrapper<CustomerBase> customerBaseWrapper = new LambdaQueryWrapper<>();
        customerBaseWrapper.eq(CustomerBase::getTenantId, tenantId);
        customerBaseWrapper.eq(CustomerBase::getCustomerNo, customerNo);
        CustomerBase customerBase = customerBaseMapper.selectOne(customerBaseWrapper);
        // 检查客户信息是否存在
        if (customerBase == null) {
            throw new BusinessException(CUSTOMER_ERROR);
        }
        if (!memberMapper.exists(
                new LambdaQueryWrapper<Member>()
                        .eq(Member::getTenantId, member.getTenantId())
                        .eq(Member::getMerchantNo, merchantNo)
                        .eq(Member::getCustomerNo, customerNo))) {
            memberMapper.insert(member);
        } else {
            member =
                    memberMapper.selectOne(
                            new LambdaQueryWrapper<Member>()
                                    .eq(Member::getTenantId, member.getTenantId())
                                    .eq(Member::getMerchantNo, merchantNo)
                                    .eq(Member::getCustomerNo, customerNo));
        }
        // 更新客户会员信息状态
        customerBase.setVipFlag(1);
        customerBaseMapper.updateById(customerBase);
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
        String type = MapUtils.getString(requestMap, "type");
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        Member member = BsinServiceContext.getReqBodyDto(Member.class, requestMap);
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        if (merchantNo == null) {
            merchantNo = loginUser.getMerchantNo();
            if (merchantNo == null) {
                merchantNo = loginUser.getTenantMerchantNo();
            }
        }
        Object paginationObj = requestMap.get("pagination");
        Pagination pagination = new Pagination();
        BeanUtil.copyProperties(paginationObj, pagination);
        Page<Member> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        LambdaQueryWrapper<Member> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(Member::getCreateTime);
        warapper.eq(Member::getTenantId, loginUser.getTenantId());
        warapper.eq(Member::getMerchantNo,loginUser.getMerchantNo());
        warapper.eq(
                ObjectUtils.isNotEmpty(merchantNo),
                Member::getMerchantNo, merchantNo);
        warapper.eq(
                ObjectUtils.isNotEmpty(loginUser.getStoreNo()),
                Member::getMerchantNo,loginUser.getStoreNo());
        warapper.eq(
                ObjectUtils.isNotEmpty(type),
                Member::getType, type);
        IPage<Member> pageList = memberMapper.selectPage(page, warapper);
        return pageList;
    }

    /**
     * 查询某一等级下的会员
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
        Object object = bsinServiceInvoke.genericInvoke("TokenParamService", "getDetailByMerchantNo", "dev", requestMap);
        Map<String, Object> tokenParamMap = BeanUtil.beanToMap(object);
        String ccy = (String) tokenParamMap.get("symbol");

        List<CustomerBase> memberList = memberGradeMapper.selectMemberListByGrade(gradeNo);
        for (CustomerBase customer : memberList) {

        }

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

    /**
     * 商户门店支付即锁客处理
     * @param requestMap
     */
    @Override
    public void lockCustomer(Map<String, Object> requestMap) {
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        String merchantNo = MapUtils.getString(requestMap, "merchantNo");
        String storeNo = MapUtils.getString(requestMap, "storeNo");
        String customerNo = MapUtils.getString(requestMap, "customerNo");
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getTenantId, tenantId);
        wrapper.eq(Member::getMerchantNo, merchantNo);
        wrapper.eq(Member::getStoreNo, storeNo);
        wrapper.eq(Member::getCustomerNo, customerNo);
        Member member = memberMapper.selectOne(wrapper);
        if(member == null){
            member = new Member();
            member.setTenantId(tenantId);
            member.setMerchantNo(merchantNo);
            member.setCustomerNo(customerNo);
            member.setStoreNo(storeNo);
            member.setType("2");
            memberMapper.insert(member);
        }
    }

}
