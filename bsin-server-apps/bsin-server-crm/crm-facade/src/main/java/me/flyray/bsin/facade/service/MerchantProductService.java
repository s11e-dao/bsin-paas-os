package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * 商户添加访问服务的应用，该应用用于签名校验和计费服务
 */

public interface MerchantProductService {

    /**
     * 添加
     */
    public Map<String,Object> add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public Map<String,Object> delete(Map<String, Object> requestMap);

    /**
     * 修改
     */
    public Map<String,Object> edit(Map<String, Object> requestMap);

    Map<String, Object> getDetail(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public Map<String,Object> getPageList(Map<String, Object> requestMap);

}
