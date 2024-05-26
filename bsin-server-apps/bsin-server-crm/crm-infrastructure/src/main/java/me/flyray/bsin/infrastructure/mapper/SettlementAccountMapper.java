package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.domain.SettlementAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface SettlementAccountMapper extends BaseMapper<SettlementAccount> {

    int batchDeleteByMerchantId(@Param("merchantId") String merchantId);

    int updateDelFlag(@Param("params") SettlementAccount settlementAccount);
}
