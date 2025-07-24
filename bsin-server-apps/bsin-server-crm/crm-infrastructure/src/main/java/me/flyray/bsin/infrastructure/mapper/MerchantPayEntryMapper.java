package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.MerchantPayEntry;
import me.flyray.bsin.domain.request.MerchantDTO;
import org.apache.ibatis.annotations.Param;

/**
* @description 针对表【crm_merchant_pay_entry(商户支付资料进件信息表)】的数据库操作Mapper
* @createDate 2025-07-22 14:22:59
* @Entity generator.domain.MerchantPayEntry
*/
public interface MerchantPayEntryMapper extends BaseMapper<MerchantPayEntry> {

    IPage<MerchantDTO> selectPageList(@Param("page") IPage<?> page, @Param("tenantId") String tenantId);

    /**
     * 根据序列号查询进件记录
     */
    MerchantPayEntry selectBySerialNo(@Param("serialNo") String serialNo);

}




