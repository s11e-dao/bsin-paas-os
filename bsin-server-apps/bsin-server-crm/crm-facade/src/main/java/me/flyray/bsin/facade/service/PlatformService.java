package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/11/1
 * @desc 租户平台信息
 */

public interface PlatformService {

    /**
     * 开通租户
     */
    public Map<String, Object> openTenant(Map<String, Object> requestMap);

    /**
     * 节点租户登录
     */
    public Map<String, Object> login(Map<String, Object> requestMap);


    public Map<String, Object> edit(Map<String, Object> requestMap);

    /**
     * 根据tenantId获取租户详情
     * @return
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

    /**
     * 查询系统租户平台
     * 场景：查询各业态场景的平台账户
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

}
