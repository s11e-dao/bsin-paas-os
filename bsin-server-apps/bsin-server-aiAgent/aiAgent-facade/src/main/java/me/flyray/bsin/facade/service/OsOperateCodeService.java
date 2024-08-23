package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.LLMParam;

import java.util.Map;

public interface OsOperateCodeService {

    /** 添加 */
    void add(Map<String, Object> requestMap);

    /** 删除 */
    void delete(Map<String, Object> requestMap) throws Exception;

    Map<String, Object> search(Map<String, Object> requestMap);

}
