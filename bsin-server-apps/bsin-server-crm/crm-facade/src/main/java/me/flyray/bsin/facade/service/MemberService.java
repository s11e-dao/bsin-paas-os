package me.flyray.bsin.facade.service;

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
     * 租户下所有
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

    /**
     * 等级下所有会员
     */
    public Map<String, Object> getGradeMemberList(Map<String, Object> requestMap);

    /**
     * 分页查询等级下所有会员
     */
    public Map<String, Object> getGradeMemberPageList(Map<String, Object> requestMap);

    /**
     * 详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

    /**
     * 详情
     */
    public Map<String, Object> getMemberGradeDetail(Map<String, Object> requestMap);


}
