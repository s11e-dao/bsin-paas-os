package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.DisCommissionRule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @description 针对表【crm_dis_brokerage_rule】的数据库操作Mapper
* @createDate 2024-10-25 17:14:01
* @Entity generator.domain.DisBrokerageRule
*/

@Repository
@Mapper
public interface DisCommissionRuleMapper extends BaseMapper<DisCommissionRule> {

}




