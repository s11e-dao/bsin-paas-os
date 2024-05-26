package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.domain.BondingCurveTokenJournal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author bolei
* @description 针对表【crm_bonding_curve_token_journal】的数据库操作Mapper
* @createDate 2023-08-06 13:47:44
* @Entity me.flyray.bsin.infrastructure.domain.BondingCurveTokenJournal
*/

@Repository
@Mapper
public interface BondingCurveTokenJournalMapper extends BaseMapper<BondingCurveTokenJournal> {
    BondingCurveTokenJournal selectLastOne(@Param("tenantId") String tenantId, @Param("merchantNo") String merchantNo);

    List<BondingCurveTokenJournal> selectCurveList(@Param("tenantId") String tenantId, @Param("merchantNo") String merchantNo, int limit);

}




