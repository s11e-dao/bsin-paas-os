package me.flyray.bsin.infrastructure.mapper.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.customer.Merchant;
import me.flyray.bsin.domain.request.customer.MerchantDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @author Admin
* @description 针对表【crm_merchant(商户;)】的数据库操作Mapper
* @createDate 2024-04-22 17:40:59
* @Entity .domain.CrmMerchant
*/
@Repository
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {

    Page pageList(@Param("page") Page<Merchant> page, @Param("merchant") MerchantDTO merchant);
}




