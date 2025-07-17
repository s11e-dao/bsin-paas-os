package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisPolicyMerchant;
import me.flyray.bsin.domain.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @description 针对表【crm_dis_policy_merchant】的数据库操作Mapper
* @createDate 2024-10-25 17:14:15
* @Entity generator.domain.DisPolicyMerchant
*/

@Repository
@Mapper
public interface DisPolicyMerchantMapper extends BaseMapper<DisPolicyMerchant> {

    IPage<Merchant> selectPageListByBrokeragePolicyNo(@Param("page") IPage<?> page, @Param("brokeragePolicyNo") String brokeragePolicyNo);

    List<Merchant> selectListByBrokeragePolicyNo(@Param("brokeragePolicyNo") String brokeragePolicyNo);

}




