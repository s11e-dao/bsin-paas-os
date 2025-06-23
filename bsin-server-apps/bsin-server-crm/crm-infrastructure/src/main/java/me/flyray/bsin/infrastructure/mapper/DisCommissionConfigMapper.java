package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.DisCommissionConfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author rednet
* @description 针对表【crm_dis_brokerage_config(参与分佣设置表)】的数据库操作Mapper
* @createDate 2024-10-25 17:13:34
* @Entity generator.domain.DisBrokerageConfig
*/
@Repository
@Mapper
public interface DisCommissionConfigMapper extends BaseMapper<DisCommissionConfig> {

}




