package me.flyray.bsin.facade.service;

import com.github.pagehelper.PageInfo;
import org.flowable.engine.repository.ProcessDefinition;

import java.util.Map;


public interface BsinAdminProcessDefinitionService {

    /**
     * 查询最新版本的流程定义列表
     */
    PageInfo<ProcessDefinition> getProcessDefinitionPageList(Map<String, Object> requestMap);
}
