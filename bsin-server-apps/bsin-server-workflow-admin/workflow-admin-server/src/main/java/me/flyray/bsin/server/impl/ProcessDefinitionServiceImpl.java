package me.flyray.bsin.server.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.BsinAdminProcessDefinitionService;
import me.flyray.bsin.infrastructure.mapper.ActReProcdefMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 流程定义
 */
@Slf4j
@DubboService
@ApiModule(value = "processDefinition")
@ShenyuDubboService("/processDefinition")
public class ProcessDefinitionServiceImpl implements BsinAdminProcessDefinitionService {
    @Autowired
    private ActReProcdefMapper actReProcdefMapper;

    /**
     * 查询最新的流程定义
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getProcessDefinitionPageList")
    @ApiDoc(desc = "getProcessDefinitionPageList")
    public PageInfo<ProcessDefinition> getProcessDefinitionPageList(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String tenantId =(String) requestMap.get("tenantId");
        String name =(String) requestMap.get("name");
        String key =(String) requestMap.get("key");
        Long pageNum = (Long) pagination.get("pageNum");
        Long pageSize = (Long) pagination.get("pageSize");
        PageHelper.startPage(Math.toIntExact(pageNum), Math.toIntExact(pageSize));
        List<ProcessDefinition> pageList = actReProcdefMapper.getProcessDefinitionPageList(tenantId,name,key);
        PageInfo<ProcessDefinition> pageInfo = new PageInfo<>(pageList);
        return pageInfo;

    }

}
