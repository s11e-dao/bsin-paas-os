package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.entity.ChainCoin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Admin
* @description 针对表【crm_coin(币;)】的数据库操作Mapper
* @createDate 2024-04-24 20:36:46
* @Entity
*/
public interface ChainCoinMapper extends BaseMapper<ChainCoin> {
    int updateDelFlag(@Param("params") ChainCoin chainCoin);

    List<String> coinDropDown();

    List<String> chainDropDown();
}




