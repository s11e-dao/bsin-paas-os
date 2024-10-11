package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.BizRoleAppApiFeeConfig;

@Mapper
@Repository
public interface BizRoleAppApiFeeConfigMapper extends BaseMapper<BizRoleAppApiFeeConfig> {

    List<BizRoleAppApiFeeConfig> getPageList(@Param("tenantId") String tenantId,
                                             @Param("appId")String appId
    );

    BizRoleAppApiFeeConfig getTenantApiFeeConfigById(@Param("serialNo") String serialNo);

    BizRoleAppApiFeeConfig getTenantApiFeeConfig(@Param("productId") String productId, @Param("tenantId") String tenantId);
}