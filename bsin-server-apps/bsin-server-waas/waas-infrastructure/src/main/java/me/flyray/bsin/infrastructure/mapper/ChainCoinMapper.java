package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.ChainCoin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author Admin
* @Entity
*/

@Repository
@Mapper
public interface ChainCoinMapper extends BaseMapper<ChainCoin> {

    int updateDelFlag(@Param("params") ChainCoin chainCoin);

    List<String> coinDropDown();

    List<String> chainDropDown();
}




