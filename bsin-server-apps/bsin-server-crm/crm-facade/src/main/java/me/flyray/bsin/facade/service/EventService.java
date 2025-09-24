package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.BsinEvent;

import java.util.Map;

/**
* @description 针对表【brms_event(事件表：存储系统中所有可触发奖励的事件定义)】的数据库操作Service
* @createDate 2025-09-24 14:50:16
*/
public interface EventService {

    /**
     * 添加
     */
    public BsinEvent add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public BsinEvent edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public BsinEvent getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

}
