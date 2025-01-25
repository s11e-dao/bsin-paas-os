package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author bolei
* @description 针对表【crm_event(客户事件表)】的数据库操作Mapper
* @createDate 2023-07-26 16:04:14
* @Entity me.flyray.bsin.infrastructure.domain.Event
*/

@Repository
@Mapper
public interface EventMapper extends BaseMapper<Event> {

}




