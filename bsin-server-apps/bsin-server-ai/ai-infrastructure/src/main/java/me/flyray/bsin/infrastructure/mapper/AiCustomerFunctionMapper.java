package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.domain.AiCustomerFunction;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author bolei
 * @description 针对表【ai_customer_function】的数据库操作Mapper
 * @createDate 2023-06-30 17:15:13 @Entity me.flyray.bsin.infrastructure.domain.AiCustomerFunction
 */
@Repository
@Mapper
public interface AiCustomerFunctionMapper extends BaseMapper<AiCustomerFunction> {}
