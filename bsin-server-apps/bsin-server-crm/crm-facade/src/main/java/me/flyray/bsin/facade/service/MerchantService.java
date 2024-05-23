package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27 19:47
 * @desc 商户信息
 */

public interface MerchantService {

    /**
     * 商户注册
     */
    public Map<String, Object> register(Map<String, Object> requestMap);

    /**
     * 商户登录
     */
    public Map<String, Object> login(Map<String, Object> requestMap);

    /**
     * 认证
     */
    public Map<String, Object> authentication(Map<String, Object> requestMap);

    /**
     * 审核
     * 审核通过可以访问具体功能
     */
    public Map<String, Object> audit(Map<String, Object> requestMap);

    /**
     * 商户订阅功能
     */
    public Map<String, Object> subscribeFunction(Map<String, Object> requestMap);

    /** 商户客户信息查询 */
    public Map<String, Object> getMerchantCustomerInfoByUsername(Map<String, Object> requestMap);

    /**
     * 删除商户
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);

    /**
     * 修改商户
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);


    /**
     * 分页查询商户
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

    /**
     * 查询商户详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

    /**
     * 查询商户列表
     */
    Map<String, Object> getListByMerchantNos(Map<String, Object> requestMap);


}
