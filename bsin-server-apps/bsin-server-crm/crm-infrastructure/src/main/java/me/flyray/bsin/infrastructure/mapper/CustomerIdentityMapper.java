package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.CustomerIdentity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author leonard
 * @description 针对表【crm_customer_identity(客户身份表)】的数据库操作Service实现
 * @createDate 2024-10-13 00:06:17 @Entity me.flyray.bsin.domain.CustomerIdentity
 */
@Repository
@Mapper
public interface CustomerIdentityMapper extends BaseMapper<CustomerIdentity> {
}
