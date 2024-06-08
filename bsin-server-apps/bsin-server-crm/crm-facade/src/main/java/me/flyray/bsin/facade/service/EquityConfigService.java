package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.EquityRelation;

import java.util.List;
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
    public EquityRelation config(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 获取配置的权益
     */
    public List<?> getListByCategoryNo(Map<String, Object> requestMap);

}
