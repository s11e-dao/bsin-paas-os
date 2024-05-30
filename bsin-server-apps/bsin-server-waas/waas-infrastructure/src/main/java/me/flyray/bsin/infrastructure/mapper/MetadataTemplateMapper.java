package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.entity.MetadataTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author bolei
* @description 针对表【da_metadata_template(元数据模板)】的数据库操作Mapper
* @createDate 2023-08-13 11:28:46
* @Entity me.flyray.bsin.infrastructure.domain.MetadataTemplate
*/

@Repository
@Mapper
public interface MetadataTemplateMapper extends BaseMapper<MetadataTemplate> {

}




