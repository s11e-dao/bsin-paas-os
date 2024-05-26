package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.domain.DigitalAssetsCollection;

/**
* @author bolei
* @description 针对表【da_digital_assets】的数据库操作Mapper
* @createDate 2023-06-27 11:12:35
* @Entity generator.domain.DaDigital assets
*/

@Repository
@Mapper
public interface DigitalAssetsCollectionMapper extends BaseMapper<DigitalAssetsCollection> {

}




