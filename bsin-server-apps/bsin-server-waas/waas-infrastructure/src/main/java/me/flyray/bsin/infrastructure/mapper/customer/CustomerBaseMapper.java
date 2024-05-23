package me.flyray.bsin.infrastructure.mapper.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.customer.CustomerBase;
import org.apache.ibatis.annotations.Param;

/**
* @author Admin
* @description 针对表【crm_customer_base】的数据库操作Mapper
* @createDate 2024-04-22 14:12:30
* @Entity generator.domain.CrmCustomerBase
*/
public interface CustomerBaseMapper extends BaseMapper<CustomerBase> {

    // 删除客户基础信息
    int updateDeleteFlagById(@Param("customerId") String customerId);
}




