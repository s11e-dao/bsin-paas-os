package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.Equity;
import me.flyray.bsin.domain.entity.EventModel;

import java.util.Map;

public interface EventModelService {

    /**
     * 添加
     */
    public EventModel add(Map<String, Object> requestMap);

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
    public EventModel getDetail(Map<String, Object> requestMap);

}
