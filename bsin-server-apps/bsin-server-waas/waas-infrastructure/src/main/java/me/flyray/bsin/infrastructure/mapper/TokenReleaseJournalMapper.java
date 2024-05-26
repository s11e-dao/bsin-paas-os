package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.domain.TokenReleaseJournal;

/**
* @author bolei
* @description 针对表【da_token_release_journal】的数据库操作Mapper
* @createDate 2023-08-22 11:57:49
* @Entity me.flyray.bsin.domain.TokenReleaseJournal
*/

@Repository
@Mapper
public interface TokenReleaseJournalMapper extends BaseMapper<TokenReleaseJournal> {

}




