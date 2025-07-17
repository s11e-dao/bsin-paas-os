package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.DisCommissionJournal;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @description 针对表【crm_dis_brokerage_journal】的数据库操作Mapper
* @createDate 2024-10-25 17:13:52
* @Entity generator.domain.DisBrokerageJournal
*/

@Repository
@Mapper
public interface DisCommissionJournalMapper extends BaseMapper<DisCommissionJournal> {

}




