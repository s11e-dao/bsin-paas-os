package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.MerchantApiFeeConfig;

@Mapper
@Repository
public interface MerchantApiFeeConfigMapper extends BaseMapper<MerchantApiFeeConfig> {

    List<MerchantApiFeeConfig> getPageList(@Param("tenantId") String tenantId,
                                    @Param("appId")String appId
    );

    MerchantApiFeeConfig getTenantApiFeeConfigById(@Param("serialNo") String serialNo);

    MerchantApiFeeConfig getTenantApiFeeConfig(@Param("productId") String productId, @Param("tenantId") String tenantId);
}