package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.entity.DigitalAssetsItemObtainCode;

/**
* @author bolei
* @description 针对表【da_digital_assets_item_obtain_code】的数据库操作Mapper
* @createDate 2023-06-27 17:09:54
* @Entity generator.domain.DaDigitalAssetsItemObtainCode
*/
@Repository
@Mapper
public interface DigitalAssetsItemObtainCodeMapper extends BaseMapper<DigitalAssetsItemObtainCode> {

}




