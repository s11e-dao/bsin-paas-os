package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author ：bolei
 * @date ：Created in 2021/11/30 16:19
 * @description：hello world
 * @modified By：
 */

public interface HelloService {

    /**
     * 新增
     */
    public Map<String, Object> add(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 查询列表
     */
    public Map<String, Object> getList(Map<String, Object> requestMap);

    /**
     * 查询分页列表
     */
    public Map<String, Object> getPageList(Map<String, Object> requestMap);

}
