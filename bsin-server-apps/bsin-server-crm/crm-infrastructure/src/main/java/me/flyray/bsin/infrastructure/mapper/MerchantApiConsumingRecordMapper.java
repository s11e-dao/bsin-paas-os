package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.domain.MerchantApiConsumingRecord;
import me.flyray.bsin.domain.domain.MerchantApiFeeConfig;


@Mapper
@Repository
public interface MerchantApiConsumingRecordMapper extends BaseMapper<MerchantApiConsumingRecord> {

    List<MerchantApiConsumingRecord> getPageList(@Param("tenantId") String tenantId,
                                    @Param("appId")String appId,
                                    @Param("apiName")String apiName,
                                                   @Param("customerNo")String customerNo
    );
}