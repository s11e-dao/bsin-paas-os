package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.facade.response.DigitalAssetsItemRes;
import me.flyray.bsin.domain.domain.Orderbook;

/**
* @author bolei
* @description 针对表【da_orderbook】的数据库操作Mapper
* @createDate 2023-06-27 17:10:34
* @Entity generator.domain.DaOrderbook
*/

@Repository
@Mapper
public interface OrderbookMapper extends BaseMapper<Orderbook> {

    IPage<DigitalAssetsItemRes> selectOrderbookPage(@Param("page") IPage<?> page, @Param("query") Orderbook orderbook);

}




