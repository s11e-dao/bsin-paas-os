package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisTeamRelation;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_team_relation(分销团队关系表)】的数据库操作Service
* @createDate 2024-10-25 17:14:20
*/
public interface DisTeamRelationService {

    /**
     * 添加
     */
    public DisTeamRelation add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisTeamRelation edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisTeamRelation getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
