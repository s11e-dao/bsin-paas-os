package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.AccountFreezeJournal;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 冻结流水表 Mapper 接口
 *
 * @author leonard
 * @since 2023-10-20
 */

@Repository
@Mapper
public interface AccountFreezeJournalMapper extends BaseMapper<AccountFreezeJournal> {}
