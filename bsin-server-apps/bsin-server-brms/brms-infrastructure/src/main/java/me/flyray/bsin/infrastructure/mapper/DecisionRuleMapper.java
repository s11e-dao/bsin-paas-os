package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import me.flyray.bsin.domain.entity.DecisionRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
* @author bolei
* @description 针对表【decision_rule】的数据库操作Mapper
* @createDate 2023-08-12 08:57:02
* @Entity generator.domain.DecisionRule
*/

@Repository
@Mapper
public interface DecisionRuleMapper extends BaseMapper<DecisionRule> {

    List<DecisionRule> getDecisionRuleList(@Param("tenantId") String tenantId);

    List<DecisionRule> getAllDecisionRuleList();

}




