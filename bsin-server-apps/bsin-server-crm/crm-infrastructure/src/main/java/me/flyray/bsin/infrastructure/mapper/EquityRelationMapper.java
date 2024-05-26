package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.domain.EquityRelation;


/**
* @author bolei
* @description 针对表【crm_equity_relationship(客户等级权益)】的数据库操作Mapper
* @createDate 2023-09-05 15:54:39
* @Entity me.flyray.bsin.domain.EquityRelationship
*/

@Repository
@Mapper
public interface EquityRelationMapper extends BaseMapper<EquityRelation> {

}




