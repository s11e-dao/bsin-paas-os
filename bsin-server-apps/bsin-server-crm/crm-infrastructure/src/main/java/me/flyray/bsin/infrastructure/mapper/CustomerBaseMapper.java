package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.entity.CustomerBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author bolei
* @description 针对表【crm_customer_base】的数据库操作Mapper
* @createDate 2023-06-30 17:15:13
* @Entity me.flyray.bsin.infrastructure.domain.CustomerBase
*/

@Repository
@Mapper
public interface CustomerBaseMapper extends BaseMapper<CustomerBase> {

    CustomerBase getCustomerInfoByTenantIdAndType(@Param("tenantId") String tenantId,
                                                      @Param("type") Integer type
    );

    List<CustomerBase> selectInviteeList(@Param("customerNo") String customerNo);

}




