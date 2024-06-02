package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.entity.AccountJournal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author bolei
* @description 针对表【crm_customer_account_journal】的数据库操作Mapper
* @createDate 2023-08-05 23:36:30
* @Entity me.flyray.bsin.infrastructure.domain.CustomerAccountJournal
*/

@Repository
@Mapper
public interface CustomerAccountJournalMapper extends BaseMapper<AccountJournal> {

}




