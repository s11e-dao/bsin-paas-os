package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.ConditionRelation;

import java.util.List;
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
    public ConditionRelation config(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 获取配置的条件
     */
    public List<?> getListByTypeNo(Map<String, Object> requestMap);

}
