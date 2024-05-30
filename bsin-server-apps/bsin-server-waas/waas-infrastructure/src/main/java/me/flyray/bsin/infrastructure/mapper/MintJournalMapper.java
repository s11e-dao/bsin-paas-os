package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.entity.MintJournal;

/**
* @author bolei
* @description 针对表【da_digital_assets_mint】的数据库操作Mapper
* @createDate 2023-06-27 17:10:19
* @Entity generator.domain.DaDigitalAssetsMint
*/

@Repository
@Mapper
public interface MintJournalMapper extends BaseMapper<MintJournal> {

}




