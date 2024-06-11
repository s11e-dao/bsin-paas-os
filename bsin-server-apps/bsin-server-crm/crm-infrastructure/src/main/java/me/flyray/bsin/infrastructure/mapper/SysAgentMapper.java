package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.SysAgent;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author bolei
* @description 针对表【market_merchant】的数据库操作Mapper
* @createDate 2023-08-07 16:42:42
* @Entity me.flyray.bsin.infrastructure.domain.Merchant
*/

@Repository
@Mapper
public interface SysAgentMapper extends BaseMapper<SysAgent> {

}




