package me.flyray.bsin.infrastructure.mapper.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.BlackWhiteListAddress;
import me.flyray.bsin.domain.entity.customer.CustomerConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CustomerConfigMapper extends BaseMapper<CustomerConfig> {

    int batchDeleteByMerchantId(@Param("merchantId") String merchantId);

    int updateDelFlag(@Param("params") CustomerConfig customerConfig);
}
