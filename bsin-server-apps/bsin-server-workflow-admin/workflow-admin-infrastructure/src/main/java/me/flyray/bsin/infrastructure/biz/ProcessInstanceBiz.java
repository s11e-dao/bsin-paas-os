package me.flyray.bsin.infrastructure.biz;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import me.flyray.bsin.domain.entity.ProcessDefinition;
import me.flyray.bsin.domain.entity.ProcessInstance;
import me.flyray.bsin.domain.entity.RestVariable;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.util.ExtOptional;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.idm.api.User;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ProcessInstanceBiz {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    protected RepositoryService repositoryService;


    public String getProcessDefinitionIdByInstanceId(String processInstanceId) {
        if (runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult() != null) {
            return ExtOptional.ofEmptyLogical(
                    runtimeService.createProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult()
                            .getProcessDefinitionId()
            ).orElseThrow(() -> new BusinessException("当前流程实例不存在"));
        } else {
            if (historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult() == null) {
                throw new BusinessException("当前流程实例不存在");
            }
            return ExtOptional.ofEmptyLogical(
                    historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult()
                            .getProcessDefinitionId()
            ).orElseThrow(() -> new BusinessException("当前流程实例不存在"));
        }
    }
    /**
     * 流程实例数据转换
     *
     * @param instances
     * @return
     */
    public List<ProcessInstance> convertInstanceList(List<org.flowable.engine.runtime.ProcessInstance> instances) {
        List<ProcessInstance> result = new CopyOnWriteArrayList<>();
        if (CollectionUtils.isNotEmpty(instances)) {
            for (org.flowable.engine.runtime.ProcessInstance processInstance : instances) {
                //构建用户信息
                User user = new RemoteUser();
                user.setId(processInstance.getStartUserId());
                //获取流程实例对应的模型定义信息
                ProcessDefinitionEntity procDef = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

                //构建流程实例出参dto
                ProcessInstance rep = new ProcessInstance(processInstance, procDef, true, user);

                //对开始时间赋值
                rep.setStarted(processInstance.getStartTime());
                List<RestVariable> restVariables = new ArrayList<>();
                //迭代获取流程Variable里面的数据
                Iterator<VariableInstanceEntity> variablesiterator = (((ExecutionEntityImpl) processInstance).getQueryVariables()).iterator();
                while (variablesiterator.hasNext()) {
                    VariableInstanceEntity next = variablesiterator.next();
                    RestVariable restVariable = new RestVariable();
                    //对应表里的NAME_字段
                    restVariable.setName(next.getName());
                    //对应表里的TEXT_字段
                    restVariable.setValue(next.getTextValue());
                    restVariables.add(restVariable);
                }

                //对变量值赋值
                rep.setVariables(restVariables);
                result.add(rep);
            }
        }
        return result;
    }
}
