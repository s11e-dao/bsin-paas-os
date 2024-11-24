package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisInviteRelation;

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

    public Map<String, Integer> inviteCount(Map<String, Object> requestMap);
}
