package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.domain.CustomerProfile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author leonard
* @description 针对表【da_customer_profile】的数据库操作Mapper
* @createDate 2023-06-29 20:06:00
* @Entity generator.domain.CustomerProfile
*/

@Repository
@Mapper
public interface CustomerProfileMapper extends BaseMapper<CustomerProfile> {

}




