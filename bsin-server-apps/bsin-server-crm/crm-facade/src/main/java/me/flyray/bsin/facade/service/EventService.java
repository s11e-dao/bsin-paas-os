package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.Event;

import java.util.Map;

/**
 * 事件服务
 */

public interface EventService {

    /**
     * 添加
     */
    public Event add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public void edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public Event getDetail(Map<String, Object> requestMap);

}
