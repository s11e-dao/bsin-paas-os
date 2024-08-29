package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.Store;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author bolei
* @description 针对表【market_store】的数据库操作Mapper
* @createDate 2023-07-08 21:20:57
* @Entity me.flyray.bsin.infrastructure.domain.Store
*/

@Repository
@Mapper
public interface StoreMapper extends BaseMapper<Store> {

}




