package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import me.flyray.bsin.domain.entity.WxPlatform;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.MerchantApp;

@Mapper
@Repository
public interface MerchantAppMapper extends BaseMapper<MerchantApp> {

    List<MerchantApp> getPageList(@Param("tenantId") String tenantId,
                                  @Param("productName")String productName,
                                  @Param("productId")String productId,
                                  @Param("productSecret")String productSecret
                              );

    MerchantApp getAppInfo(MerchantApp merchantApp);

    MerchantApp selectByAppId(String appId);
    MerchantApp selectByCorpAgentId(String corpId, String agentId);
}