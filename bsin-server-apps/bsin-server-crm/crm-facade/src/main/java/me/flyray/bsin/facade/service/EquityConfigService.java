package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/9/11
 * @desc
 */

public interface EquityConfigService {

    /**
     * 配置
     */
    public Map<String, Object> add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);

    /**
     * 获取配置的权益
     */
    public Map<String, Object> getListByCategoryNo(Map<String, Object> requestMap);

}
