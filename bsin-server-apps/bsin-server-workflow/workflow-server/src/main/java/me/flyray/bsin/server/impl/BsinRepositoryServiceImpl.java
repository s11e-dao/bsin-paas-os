package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BsinRepositoryService;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.form.api.FormRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Slf4j
@DubboService
@ApiModule(value = "repository")
@ShenyuDubboService("/repository")
public class BsinRepositoryServiceImpl implements BsinRepositoryService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private FormRepositoryService formRepositoryService;


    /**
     * 部署流程定义
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/importProcessDefinition")
    @ApiDoc(desc = "importProcessDefinition")
    public void importProcessDefinition(Map<String, Object> requestMap) {
        try {
            // 1、部署流程
            Deployment deployment = repositoryService.createDeployment()
                    // .key()
                    // .name(name)
                    // .category(category)
                    // .tenantId()
                    // 通过压缩包的形式一次行多个发布
                    // .addZipInputStream()
                    // 通过InputStream的形式发布
//                    .addInputStream(file.getOriginalFilename(), file.getInputStream())
                    // 通过存放在classpath目录下的文件进行发布
                     .addClasspathResource("请假流程模型.bpmn20.xml")
                    // 通过xml字符串的形式
                    // .addString()
                    .deploy();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
            if (processDefinition == null) {
                final ResponseCode processDefinitionDeployFail = ResponseCode.PROCESS_DEFINITION_DEPLOY_FAIL;
                throw new BusinessException(processDefinitionDeployFail);
            }

        } catch (Exception e) {
            throw new RuntimeException("导入流程定义失败：" + e.getMessage());
        }
        System.out.println("流程定义部署成功");
    }

}
