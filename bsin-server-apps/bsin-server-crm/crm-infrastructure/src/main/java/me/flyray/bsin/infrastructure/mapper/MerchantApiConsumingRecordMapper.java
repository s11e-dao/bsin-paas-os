package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.MerchantApiConsumingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface MerchantApiConsumingRecordMapper extends BaseMapper<MerchantApiConsumingRecord> {

}