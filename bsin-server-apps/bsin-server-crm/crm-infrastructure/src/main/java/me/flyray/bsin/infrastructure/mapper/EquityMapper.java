package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.Equity;

/**
* @author bolei
* @description 针对表【crm_equity(客户等级权益)】的数据库操作Mapper
* @createDate 2023-07-26 16:04:25
* @Entity me.flyray.bsin.infrastructure.domain.Equity
*/

@Repository
@Mapper
public interface EquityMapper extends BaseMapper<Equity> {

    List<Equity> getEquityList(@Param("typeNo") String typeNo);

}




