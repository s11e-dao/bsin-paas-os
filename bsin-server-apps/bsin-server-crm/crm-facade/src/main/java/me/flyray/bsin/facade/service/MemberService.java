package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.domain.entity.Member;

import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/8/6 10:09
 * @desc 个人客户
 */

public interface MemberService {

    /**
     * 开通会员
     */
    public Map<String, Object> openMember(Map<String, Object> requestMap);

    /**
     * 编辑会员
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);

    /**
     * 商户下所有会员：会员挂在商户下
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 等级下所有会员
     */
    public List<?> getGradeMemberList(Map<String, Object> requestMap);

    /**
     * 分页查询等级下所有会员
     */
    public IPage<?> getGradeMemberPageList(Map<String, Object> requestMap);

    /**
     * 详情
     */
    public Member getDetail(Map<String, Object> requestMap);

    /**
     * 详情
     */
    public Grade getMemberGradeDetail(Map<String, Object> requestMap);


    public List<String> getCustomerNoByGradeNos(Map<String, Object> requestMap);


}
