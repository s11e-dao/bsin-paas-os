package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.CustomerPassCard;
import me.flyray.bsin.facade.response.DigitalAssetsItemRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bolei
 * @description 针对表【crm_customer_pass_card(会员通行证表)】的数据库操作Mapper
 * @createDate 2023-07-07 14:58:21 @Entity me.flyray.bsin.infrastructure.domain.CustomerPassCard
 */
@Repository
@Mapper
public interface CustomerPassCardMapper extends BaseMapper<CustomerPassCard> {

  DigitalAssetsItemRes selectCustomerDigitalAssetsDetail(
      @Param("tenantId") String tenantId,
      @Param("merchantNo") String merchantNo,
      @Param("customerNo") String customerNo);

  List<CustomerPassCard> selectCustomerPassCardList(
      @Param("tenantId") String tenantId, @Param("customerNo") String customerNo);
}
