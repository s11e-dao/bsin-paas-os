package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.MerchantHyperledgerParam;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
* @author bolei
* @description 针对表【community_hyperledger_setting】的数据库操作Mapper
* @createDate 2023-06-27 17:33:02
* @Entity generator.domain.HyperledgerSetting
*/

@Repository
@Mapper
public interface MerchantHyperledgerParamMapper extends BaseMapper<MerchantHyperledgerParam> {

}




