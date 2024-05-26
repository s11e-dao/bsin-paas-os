package me.flyray.bsin.facade.service;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_grade(客户等级划分配置)】的数据库操作Service
* @createDate 2023-09-19 23:06:17
*/

public interface GradeService {

    /**
     * 添加
     */
    public Map<String, Object> add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public Map<String, Object> delete(Map<String, Object> requestMap);


    /**
     * 编辑
     */
    public Map<String, Object> edit(Map<String, Object> requestMap);


    /**
     * 商户下所有
     */
    public Map<String, Object> getList(Map<String, Object> requestMap);

    /**
     * 商户下分页所有
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);
    
    /**
     * 查询等级、权益、条件详情
     */
    public Map<String, Object> getDetail(Map<String, Object> requestMap);

    /**
     * 查询等级详情
     */
    public Map<String, Object> getGradeDetail(Map<String, Object> requestMap);


    public Map<String, Object> getGradeAndMemberList(Map<String, Object> requestMap);

}
