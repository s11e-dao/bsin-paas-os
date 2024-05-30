package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.entity.MetadataFile;

import java.math.BigInteger;

/**
* @author bolei
* @description 针对表【da_digital_assets_metadata_file】的数据库操作Mapper
* @createDate 2023-06-27 17:10:02
* @Entity generator.domain.DaDigitalAssetsMetadataFile
*/

@Repository
@Mapper
public interface MetadataFileMapper extends BaseMapper<MetadataFile> {

    MetadataFile getByFolderNoAndTokenId(@Param("parentNo") String parentNo, @Param("tokenId") BigInteger tokenId);

}




