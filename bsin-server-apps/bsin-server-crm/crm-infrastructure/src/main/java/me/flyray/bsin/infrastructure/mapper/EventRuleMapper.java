package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.domain.EventRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author bolei
* @description 针对表【crm_event_rule(客户等级事件规则表)】的数据库操作Mapper
* @createDate 2023-07-26 16:04:14
* @Entity me.flyray.bsin.infrastructure.domain.EventRule
*/

@Repository
@Mapper
public interface EventRuleMapper extends BaseMapper<EventRule> {

}




