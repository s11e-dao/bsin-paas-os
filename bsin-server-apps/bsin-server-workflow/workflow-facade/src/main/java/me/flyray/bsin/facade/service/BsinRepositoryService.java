package me.flyray.bsin.facade.service;

import java.util.Map;


public interface BsinRepositoryService {

    /**
     * 部署流程定义和表单定义
     */
    void importProcessDefinition(Map<String, Object> requestMap);

}
