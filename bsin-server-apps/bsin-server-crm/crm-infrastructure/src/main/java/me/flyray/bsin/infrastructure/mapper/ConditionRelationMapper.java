package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.entity.ConditionRelation;

/**
* @author bolei
* @description 针对表【crm_condition_relationship(营销任务参与条件配置表)】的数据库操作Mapper
* @createDate 2023-09-05 15:55:42
* @Entity me.flyray.bsin.domain.ConditionRelationship
*/

@Repository
@Mapper
public interface ConditionRelationMapper extends BaseMapper<ConditionRelation> {

}




