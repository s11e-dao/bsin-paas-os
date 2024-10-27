package me.flyray.bsin.facade.service;


import me.flyray.bsin.domain.entity.MerchantHyperledgerParam;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27 20:00
 * @desc
 */

public interface MerchantHyperledgerService {

    /**
     * 设置账本
     * TODO：通过提案设置账本信息
     */
    public MerchantHyperledgerParam setting(Map<String, Object> requestMap);

    /**
     * 修改提案
     */
    public MerchantHyperledgerParam edit(Map<String, Object> requestMap);

    /**
     * 查询账本设置详情
     */
    public MerchantHyperledgerParam getDetail(Map<String, Object> requestMap);

}
