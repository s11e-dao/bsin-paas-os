package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Merchant;
import me.flyray.bsin.domain.request.MerchantDTO;

import java.util.Map;

/**
* @description 针对表【crm_merchant_pay_entry(商户支付资料进件信息表)】的数据库操作Service
* @createDate 2025-07-22 14:22:59
*/
public interface MerchantPayEntryService {

    /**
     * 商户支付进件
     */


    /**
     * 商户支付进件状态查询
     */


    /**
     * 商户支付进件列表查询
     * 商户表关联支付进件表
     */
    public IPage<MerchantDTO> getPageList(Map<String, Object> requestMap);

}
