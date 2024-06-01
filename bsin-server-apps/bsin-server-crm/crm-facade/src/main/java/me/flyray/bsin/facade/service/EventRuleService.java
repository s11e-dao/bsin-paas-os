package me.flyray.bsin.facade.service;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_event_rule(客户等级事件规则表)】的数据库操作Service
* @createDate 2023-07-26 16:04:14
*/

public interface EventRuleService {

    /**
     * 添加
     */
    public Map<String, Object> add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public Map<String, Object> getList(Map<String, Object> requestMap);

    /**
     * 分页查询
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

    /**
     * 查询详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

}
