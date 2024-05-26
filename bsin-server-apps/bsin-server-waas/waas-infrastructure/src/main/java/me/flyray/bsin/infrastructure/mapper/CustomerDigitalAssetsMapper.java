package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.domain.CustomerDigitalAssets;
import me.flyray.bsin.facade.response.DigitalAssetsItemRes;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bolei
 * @description 针对表【da_customer_digital_assets】的数据库操作Mapper
 * @createDate 2023-07-19 14:57:09 @Entity
 *     me.flyray.bsin.infrastructure.domain.CustomerDigitalAssets
 */
@Repository
@Mapper
public interface CustomerDigitalAssetsMapper extends BaseMapper<CustomerDigitalAssets> {

  List<DigitalAssetsItemRes> selectCustomerDigitalAssetsList(
      @Param("tenantId") String tenantId,
      @Param("merchantNo") String merchantNo,
      @Param("customerNo") String customerNo,
      @Param("assetsType") String assetsType,
      @Param("tokenId") String tokenId);

  DigitalAssetsItemRes selectCustomerDigitalAssetsDetail(@Param("serialNo") String serialNo);
}
