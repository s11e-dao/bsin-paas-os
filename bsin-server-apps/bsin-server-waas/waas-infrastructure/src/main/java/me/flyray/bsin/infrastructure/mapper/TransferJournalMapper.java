package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.entity.TransferJournal;

/**
* @author bolei
* @description 针对表【da_digital_assets_transfer】的数据库操作Mapper
* @createDate 2023-06-27 17:10:27
* @Entity generator.domain.DaDigitalAssetsTransfer
*/

@Repository
@Mapper
public interface TransferJournalMapper extends BaseMapper<TransferJournal> {

}




