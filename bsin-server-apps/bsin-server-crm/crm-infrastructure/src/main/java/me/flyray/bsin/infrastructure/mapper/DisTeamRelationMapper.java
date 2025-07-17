package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.DisTeamRelation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @description 针对表【crm_dis_team_relation(分销团队关系表)】的数据库操作Mapper
* @createDate 2024-10-25 17:14:20
* @Entity generator.domain.DisTeamRelation
*/
@Repository
@Mapper
public interface DisTeamRelationMapper extends BaseMapper<DisTeamRelation> {

}




