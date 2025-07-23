package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.MerchantAuth;

import java.util.Map;

public interface MerchantAuthService {

    /**
     * 商户资料进件认证
     */
    public void authentication(Map<String, Object> requestMap);

    /**
     * 审核
     * 审核通过可以访问具体功能
     */
    public void audit(Map<String, Object> requestMap) throws Exception;

    /**
     * 查询商户进件的资料信息
     */
    public MerchantAuth getDetail(Map<String, Object> requestMap) throws Exception;

}
