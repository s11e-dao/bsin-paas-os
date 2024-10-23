package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
     * 分页查询
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 模型引擎根据事件code查询
     * @param requestMap
     * @return
     */
    public EventModel getDetail(Map<String, Object> requestMap);

}
