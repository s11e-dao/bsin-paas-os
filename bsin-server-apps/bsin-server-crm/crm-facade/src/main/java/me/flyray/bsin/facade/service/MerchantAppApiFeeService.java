package me.flyray.bsin.facade.service;

import java.util.Map;

public interface MerchantAppApiFeeService {

    /**
     * 查询费用配置
     */
    public Map<String, Object> getApiFeeConfigInfo(Map<String, Object> requestMap);

    /**
     * 修改费用配置
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);

    /**
     * 分页查询费用配置
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public Map<String,Object> getList(Map<String, Object> requestMap);

}
