package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.facade.response.DigitalAssetsItemRes;
import me.flyray.bsin.domain.entity.DigitalAssetsItem;

/**
 * @author bolei
 * @description 针对表【da_digital_assets_item】的数据库操作Mapper
 * @createDate 2023-06-27 17:09:40 @Entity generator.domain.DaDigitalAssetsItem
 */
@Repository
@Mapper
public interface DigitalAssetsItemMapper extends BaseMapper<DigitalAssetsItem> {

  List<DigitalAssetsItemRes> selectDigitalAssetsList(
      @Param("tenantId") String tenantId,
      @Param("merchantNo") String merchantNo,
      @Param("assetsTypes") List<String> assetsTypes);

  IPage<DigitalAssetsItem> selectDigitalAssetsPageList(
      @Param("page") IPage<?> page,
      @Param("tenantId") String tenantId,
      @Param("merchantNo") String merchantNo,
      @Param("assetsTypes") List<String> assetsTypes,
      @Param("assetsType") String assetsType,
      @Param("obtainMethod") String obtainMethod,
      @Param("digitalAssetsItemNo") String digitalAssetsItemNo);
}
