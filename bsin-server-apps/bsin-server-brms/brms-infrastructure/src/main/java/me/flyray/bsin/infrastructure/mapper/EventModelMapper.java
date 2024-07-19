package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.DecisionRule;
import me.flyray.bsin.domain.entity.EventModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
* @author bolei
* @description 针对表【event_model】的数据库操作Mapper
* @createDate 2023-08-12 08:57:02
* @Entity generator.domain.EventModel
*/

@Repository
@Mapper
public interface EventModelMapper extends BaseMapper<EventModel> {


}




