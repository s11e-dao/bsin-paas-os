package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisInviteRelation;
import me.flyray.bsin.domain.response.DistributionRoleAndRateDTO;

import java.util.List;
import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_invite_relation(邀请关系表)】的数据库操作Service
* @createDate 2024-10-25 17:14:05
*/
public interface DisInviteRelationService {

    /**
     * 编辑
     */
    public DisInviteRelation edit(Map<String, Object> requestMap);

    /**
     * 详情
     */
    public DisInviteRelation getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    public Map<String, Integer> inviteStatistics(Map<String, Object> requestMap);

    /**
     * 查询合伙人下面的客户列表
     * @param requestMap
     * @return
     */
    public IPage<?> getSysAgentCustormerPageList(Map<String, Object> requestMap);

    /**
     * 查询我邀请的客户列表
     * @param requestMap
     * @return
     */
    public IPage<?> getMyInviteCustormerPageList(Map<String, Object> requestMap);

    /**
     * 获取分销角色和等级及分佣比列数据
     * @param requestMap
     * @return
     */
    public List<DistributionRoleAndRateDTO> getDistributionRoleAndRateList(Map<String, Object> requestMap);

}
