package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/9/11
 * @desc
 */

public interface ConditionConfigService {

    /**
     * 配置
     */
    public Map<String, Object> add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);

    /**
     * 获取配置的条件
     */
    public Map<String, Object> getListByCategoryNo(Map<String, Object> requestMap);

}
