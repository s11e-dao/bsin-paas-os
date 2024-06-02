package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.Condition;

/**
* @author bolei
* @description 针对表【crm_condition(客户等级达成条件)】的数据库操作Mapper
* @createDate 2023-09-05 15:55:35
* @Entity me.flyray.bsin.domain.Condition
*/
@Repository
@Mapper
public interface ConditionMapper extends BaseMapper<Condition> {

    List<Condition> getConditionList(@Param("categoryNo") String categoryNo);

}




