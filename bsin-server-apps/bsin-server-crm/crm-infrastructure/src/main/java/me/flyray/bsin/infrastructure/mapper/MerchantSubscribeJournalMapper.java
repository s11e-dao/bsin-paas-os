package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.entity.BizRoleSubscribeJournal;

/**
* @author bolei
* @description 针对表【market_merchant_subscribe_journal】的数据库操作Mapper
* @createDate 2023-11-08 14:38:51
* @Entity me.flyray.bsin.domain.MerchantSubscribeJournal
*/

@Mapper
@Repository
public interface MerchantSubscribeJournalMapper extends BaseMapper<BizRoleSubscribeJournal> {

}




