package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.entity.OrderbookMatchJournal;

/**
* @author bolei
* @description 针对表【da_orderbook_match_serial】的数据库操作Mapper
* @createDate 2023-06-27 17:10:41
* @Entity generator.domain.DaOrderbookMatchSerial
*/

@Repository
@Mapper
public interface OrderbookMatchJournalMapper extends BaseMapper<OrderbookMatchJournal> {

}




