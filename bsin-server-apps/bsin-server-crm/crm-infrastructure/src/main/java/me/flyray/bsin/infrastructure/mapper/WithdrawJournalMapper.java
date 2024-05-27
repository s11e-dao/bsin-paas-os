package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.domain.WithdrawOrder;


/**
* @author bolei
* @description 针对表【market_withdraw_journal】的数据库操作Mapper
* @createDate 2023-09-12 17:49:04
* @Entity me.flyray.bsin.domain.WithdrawJournal
*/

@Repository
@Mapper
public interface WithdrawJournalMapper extends BaseMapper<WithdrawOrder> {

}




