package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.domain.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * @author bolei
 * @description 针对表【crm_customer_account】的数据库操作Mapper
 * @createDate 2023-07-06 16:50:20 @Entity me.flyray.bsin.infrastructure.domain.CustomerAccount
 */
@Repository
@Mapper
public interface CustomerAccountMapper extends BaseMapper<Account> {
  int freezeAmount(@Param("query") Account customerAccount);

  int unFreezeAmount(@Param("query") Account customerAccount);

  BigDecimal getTotalAmount(@Param("query") Account customerAccount);
}
