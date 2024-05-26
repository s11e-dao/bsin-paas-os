package me.flyray.bsin.facade.service;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_grade_condition(客户等级达成条件)】的数据库操作Service
* @createDate 2023-07-26 16:02:57
*/

public interface ConditionService {

    /**
     * 添加
     */
    public Map<String, Object> add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);

    /**
     * 修改
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);


    /**
     * 租户下所有
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

}
