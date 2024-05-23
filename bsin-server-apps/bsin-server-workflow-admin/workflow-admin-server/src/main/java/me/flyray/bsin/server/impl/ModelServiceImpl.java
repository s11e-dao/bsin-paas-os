package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.ActDeModel;
import me.flyray.bsin.domain.entity.BsinFormSaveRepresentation;
import me.flyray.bsin.domain.request.ModelRepresentationDTO;
import me.flyray.bsin.domain.response.DefinitionResp;
import me.flyray.bsin.domain.response.ModelReq;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BsinAdminModelService;
import me.flyray.bsin.infrastructure.biz.ModelBiz;
import me.flyray.bsin.infrastructure.mapper.ActDeModelMapper;
import me.flyray.bsin.infrastructure.mapper.ModelTypeConnectionMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.form.api.FormRepositoryService;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.common.service.exception.ConflictingRequestException;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.domain.ModelHistory;
import org.flowable.ui.modeler.model.ModelKeyRepresentation;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.repository.ModelHistoryRepository;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.repository.ModelRepositoryImpl;
import org.flowable.ui.modeler.service.FlowableFormService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 模型设计
 */
@Slf4j
@DubboService
@ApiModule(value = "model")
@ShenyuDubboService("/model")
public class ModelServiceImpl implements BsinAdminModelService {
    public static final int MODEL_TYPE_DECISION_SERVICE = 6;
    @Autowired
    private org.flowable.ui.modeler.serviceapi.ModelService modelService;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ModelRepository modelRepository;
    @Autowired
    protected ModelRepositoryImpl modelRepositoryService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    private ModelBiz modelBiz;
    @Autowired
    private ActDeModelMapper actDeModelMapper;
    @Autowired
    protected ModelHistoryRepository modelHistoryRepository;
    @Autowired
    protected FlowableFormService flowableFormService;
    @Autowired
    private FormRepositoryService formRepositoryService;
    @Autowired
    private ModelTypeConnectionMapper modelTypeConnectionMapper;

    /**
     * 保存对应model的属性信息
     * @return
     */
    @Override
    @ShenyuDubboClient("/saveModel")
    @ApiDoc(desc = "saveModel")
    public ModelRepresentation saveModel(ModelRepresentationDTO modelRepresentationDTO) {
        String modelTypeId = modelRepresentationDTO.getModelTypeId();
        ModelRepresentation modelRepresentation = new ModelRepresentation();
        BeanUtils.copyProperties(modelRepresentationDTO, modelRepresentation);
        modelRepresentation.setKey(modelRepresentationDTO.getKey().replaceAll(" ", ""));

        String json = modelService.createModelJson(modelRepresentation);
        Model newModel = modelService.createModel(modelRepresentation, json, SecurityUtils.getCurrentUserId());
        // 手动设置租户id
        actDeModelMapper.setActDeModelTenantId(newModel.getId(),modelRepresentation.getTenantId());
        try{
            checkForDuplicateKey(newModel);
        }catch (Exception e){
            throw new BusinessException(ResponseCode.MODEL_KEY_ALREADY_EXIST);
        }
        modelRepresentation = new ModelRepresentation(newModel);
        modelTypeConnectionMapper.insert(modelTypeId, modelRepresentation.getId());
        return modelRepresentation;
    }

    protected void checkForDuplicateKey(Model model) {
        ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(model, model.getModelType(), model.getKey());
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new ConflictingRequestException("Provided model key already exists: " + model.getKey());
        }
    }


    /**
     * 保存对应model的xml信息
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/saveModelXML")
    @ApiDoc(desc = "saveModelXML")
    public Model saveModelXML(Map<String, Object> requestMap) {
        ModelReq modelReq = BsinServiceContext.getReqBodyDto(ModelReq.class, requestMap);
        String updatedBy = (String) requestMap.get("updatedBy");
        if (!CollectionUtils.isEmpty(modelBiz.validationErrors(modelBiz.StringXMLToBpmnModel(modelReq.getId(), modelReq.getBpmnModelXml())))) {
            throw new BusinessException("模型定义错误");
        }
        Model model = modelBiz.saveModelXML(modelReq.getId(), modelReq.getBpmnModelXml(), updatedBy, true);
        return model;
    }

    /**
     * 修改模型
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/updateModel")
    @ApiDoc(desc = "updateModel")
    public ModelRepresentation updateModel(Map<String, Object> requestMap) {
        //获取模型id
        String modelId = (String) requestMap.get("id");
        String key = (String) requestMap.get("key");
        String name = (String) requestMap.get("name");
        //更新的模型
        ModelRepresentation updatedModel = new ModelRepresentation();
        //通过模型id得到模型对象
        Model model = modelService.getModel(modelId);
        if(model == null){
            throw new BusinessException(ResponseCode.FAIL);
        }
        //校验模型id和模型类型、key查出来的模型id是否相同，不同设置KeyAlreadyExists为true
        ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(model, model.getModelType(), key);
        //KeyAlreadyExists为true代表提供密钥的模型已存在
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new BadRequestException("提供密钥的模型已经存在" + key);
        }
        try {
            //更新模板
            updatedModel.updateModel(model);
            if (model.getModelType() != null) {
                //获取模型节点
                ObjectNode modelNode = (ObjectNode) objectMapper.readTree(model.getModelEditorJson());
                modelNode.put("name", model.getName());
                modelNode.put("key", model.getKey());
                //判断模型类型为 BPMN/模型类型决策服务 put不同的字段
                if (Model.MODEL_TYPE_BPMN == model.getModelType() || MODEL_TYPE_DECISION_SERVICE == model.getModelType()) {
                    //获取属性节点
                    ObjectNode propertiesNode = (ObjectNode) modelNode.get("properties");
                    if (Model.MODEL_TYPE_BPMN == model.getModelType()) {
                        propertiesNode.put("process_id", model.getKey());
                    } else if (MODEL_TYPE_DECISION_SERVICE == model.getModelType()) {
                        propertiesNode.put("drd_id", model.getKey());
                    }
                    propertiesNode.put("name", model.getName());
                    if (StringUtils.isNotEmpty(model.getDescription())) {
                        propertiesNode.put("documentation", model.getDescription());
                    }
                    //设置属性节点
                    modelNode.set("properties", propertiesNode);
                }
                //设置模型编辑器 Json
                model.setModelEditorJson(modelNode.toString());
            }
            model.setKey(key);
            model.setName(name);
            //保存
            modelRepository.save(model);
            ModelRepresentation result = new ModelRepresentation(model);
            return result;
        } catch (Exception e) {
            throw new BadRequestException("无法更新模型：" + modelId);
        }
    }

    /**
     * 删除模型
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/deleteModel")
    @ApiDoc(desc = "deleteModel")
    public void deleteModel(Map<String, Object> requestMap) {
        //获取模型id
        String modelId = (String) requestMap.get("id");
        try {
            //删除模型
            modelService.deleteModel(modelId);
        } catch (Exception e) {
            throw new BadRequestException("无法删除模型： " + modelId);
        }
    }

    /**
     * 分页查询所有模型
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getPageListModel")
    @ApiDoc(desc = "getPageListModel")
    public PageInfo<ActDeModel> getPageListModel(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        String modelTypeId = (String) requestMap.get("modelTypeId");
        String tenantId = (String) requestMap.get("tenantId");
        Long pageNum = (Long) pagination.get("pageNum");
        Long pageSize = (Long) pagination.get("pageSize");
        PageHelper.startPage(Math.toIntExact(pageNum), Math.toIntExact(pageSize));
        List<ActDeModel> allModel = actDeModelMapper.getProcessModelByType(tenantId,modelTypeId);
        PageInfo<ActDeModel> pageInfo = new PageInfo<ActDeModel>(allModel);
        return pageInfo;
    }

    /**
     * 部署模型
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/deployModel")
    @ApiDoc(desc = "deployModel")
    public Map<String, Object> deployModel(Map<String, Object> requestMap) {
        String modelId = (String) requestMap.get("id");
        String tenantId = (String) requestMap.get("tenantId");
        //获取模型
        Model model = modelRepositoryService.get(modelId);
        if(model == null){
            throw new BusinessException(ResponseCode.FAIL);
        }
        //获取对应modelId的bpmn模型
        BpmnModel bpmnModel = modelService.getBpmnModel(model);
        //将bpmn模型转换为string类型的xml输出
        byte[] bpmnBytes = modelService.getBpmnXML(bpmnModel);
        String processName = model.getName() + ".bpmn20.xml";
        Deployment deploy;
        try {
            //部署对应的bpmn模型
            deploy = repositoryService.createDeployment()
                    .key(model.getKey())
                    .name(model.getName())
                    .tenantId(tenantId)
                    .addBytes(processName, bpmnBytes)
                    .deploy();
        } catch (Exception e) {
            throw new BusinessException(ResponseCode.FAIL);
        }

        HashSet<String> formKeySet = new HashSet<>();
        List<Process> processes = bpmnModel.getProcesses();
        for (Process process : processes) {
            Collection<FlowElement> flowElements = process.getFlowElements();
            for (FlowElement flowElement : flowElements) {
                if (flowElement instanceof UserTask) {
                    if(StringUtils.isNotBlank(((UserTask) flowElement).getFormKey())){
                        formKeySet.add(((UserTask) flowElement).getFormKey());
                    }
                }
                if (flowElement instanceof StartEvent) {
                    if(StringUtils.isNotBlank(((StartEvent) flowElement).getFormKey())){
                        formKeySet.add(((StartEvent) flowElement).getFormKey());
                    }
                }
            }
        }
        List<Model> models = new ArrayList<>();
        for (String formKey : formKeySet) {
            ModelKeyRepresentation mkp = modelService.validateModelKey(null, 2, formKey);
            models.add(modelService.getModel(mkp.getId()));
        }

        //部署表单
        for (Model m : models) {
            formRepositoryService.createDeployment()
                    .name(m.getName())
                    .tenantId(tenantId)
                    .addFormBytes(m.getKey() + "_pro.form", m.getModelEditorJson().getBytes())//必须是.form结尾
                    .parentDeploymentId(deploy.getId())
                    .deploy();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("modelId", modelId);
        return map;
    }

    /**
     * 模型XML预览
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getModelById")
    @ApiDoc(desc = "getModelById")
    public Map<String, Object> getModelById(Map<String, Object> requestMap) {
        String modelId = (String) requestMap.get("id");
        Model model;
        if(modelId == null){
            String processDefinitionId = (String) requestMap.get("processDefinitionId");
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
            ActDeModel actDeModel = actDeModelMapper.getActDeModelKey(processDefinition.getKey());
             model = modelService.getModel(actDeModel.getId());
        }else {
             model = modelService.getModel(modelId);
        }
        if(model == null){
            throw new BusinessException(ResponseCode.FAIL);
        }
        byte[] bpmnXML = modelService.getBpmnXML(model);
        String bpmnModel = new String(bpmnXML);
        Map<String, Object> map = new HashMap<>();
        map.put("bpmnModel", bpmnModel);
        return map;
    }

    /**
     * 获取历史版本
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getHistoryVersions")
    @ApiDoc(desc = "getHistoryVersions")
    public ResultListDataRepresentation getHistoryVersions(Map<String, Object> requestMap) {
        String modelId = (String) requestMap.get("id");
        //获取模型
        Model model = modelService.getModel(modelId);
        if(model == null){
            throw new BusinessException(ResponseCode.FAIL);
        }
        //根据id查找模型历史储存库里的历史型号
        List<ModelHistory> history = modelHistoryRepository.findByModelId(model.getId());
        //结果列表数据分页展示
        ResultListDataRepresentation result = new ResultListDataRepresentation();
        List<ModelRepresentation> representations = new ArrayList<>();
        if (history.size() > 0) {
            for (ModelHistory modelHistory : history) {
                representations.add(new ModelRepresentation(modelHistory));
            }
            result.setData(representations);
        }
        //设置大小和总数
        result.setSize(representations.size());
        result.setTotal(Long.valueOf(representations.size()));
        result.setStart(0);
        return result;
    }

    /**
     * 获取发布的历史版本
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getModelDefinitionVersions")
    @ApiDoc(desc = "getModelDefinitionVersions")
    public DefinitionResp getModelDefinitionVersions(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        //标识，区分查询状态 挂起、激活
        String modelStatus = (String) requestMap.get("modelStatus");
        Integer pageNum = (Integer) pagination.get("pageNum");
        Integer pageSize = (Integer) pagination.get("pageSize");
        long total = 0L;
        //创建模型定义查询实例
        ProcessDefinitionQuery definitionQuery = repositoryService.createProcessDefinitionQuery();
        //设置查询最新的模型定义
        definitionQuery.latestVersion();
        List<ProcessDefinition> definitions = null;
        //根据模型状态查询对应的models 1--启用中的模型定义 2--挂起状态中的模型定义 没传值则查全部模型定义
        if ("1".equals(modelStatus)) {
            //分页查询激活中的模型定义
            definitions = definitionQuery.active().listPage((pageNum - 1) * pageSize, pageSize);
            total = definitionQuery.active().count();
        } else if ("2".equals(modelStatus)) {
            //分页查询挂起的模型定义
            definitions = definitionQuery.suspended().listPage((pageNum - 1) * pageSize, pageSize);
            total = definitionQuery.active().count();
        } else {
            //分页查询所有模型定义
            definitions = definitionQuery.listPage((pageNum - 1) * pageSize, pageSize);
            total = definitionQuery.count();
        }
        //对查询出来的模型定义进行dto转换
        ResultListDataRepresentation result = new ResultListDataRepresentation(modelBiz.convertDefinitionList(definitions));
        //构建返回参数值
        DefinitionResp definitionResp = new DefinitionResp();
        definitionResp.setDeployModels(result.getData());
        definitionResp.setTotal(total);
        definitionResp.setPageNum(pageNum);
        definitionResp.setPageSize(pageSize);
        return definitionResp;
    }

    //-------------------------------------------------表单模型---------------------------------------------------------

    /**
     * 保存表单
     */
    @Override
    @ShenyuDubboClient("/saveForm")
    @ApiDoc(desc = "saveForm")
    public void saveForm(Map<String, Object> requestMap) throws ClassNotFoundException {
        Map<String, Object> saveRepresentationMap = (Map<String, Object>) requestMap.get("saveRepresentation");
        BsinFormSaveRepresentation saveRepresentation = BsinServiceContext.getReqBodyDto(BsinFormSaveRepresentation.class, saveRepresentationMap);
        Map<String, Object> formRepresentation = (Map<String, Object>)saveRepresentationMap.get("formRepresentation");
        String editorJson = JSONObject.toJSONString(formRepresentation.get("formDefinition"));
        //        flowableFormService.saveForm(saveRepresentation.getFormRepresentation().getId(), saveRepresentation);
        String user = SecurityUtils.getCurrentUserId();
        String formId = saveRepresentation.getFormRepresentation().getId();
        Model model = this.modelService.getModel(formId);
        if(model == null){
            throw new BusinessException(ResponseCode.FAIL);
        }
        checkForDuplicateKey(model);
        this.modelService.saveModel(model, editorJson, null, saveRepresentation.isNewVersion(), saveRepresentation.getComment(), user);
    }

    /**
     * 保存表单model的属性信息
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/saveFormModel")
    @ApiDoc(desc = "saveFormModel")
    public ModelRepresentation saveFormModel(Map<String, Object> requestMap) {
        ModelRepresentation modelRepresentation = BsinServiceContext.getReqBodyDto(ModelRepresentation.class, requestMap);
        modelRepresentation.setKey(modelRepresentation.getKey().replaceAll(" ", ""));
        String json = modelService.createModelJson(modelRepresentation);
        Model newModel = modelService.createModel(modelRepresentation, json, SecurityUtils.getCurrentUserId());
        // 手动设置租户id
        actDeModelMapper.setActDeModelTenantId(newModel.getId(),modelRepresentation.getTenantId());
        try{
            checkForDuplicateKey(newModel);
        }catch (Exception e){
            throw new BusinessException(ResponseCode.MODEL_KEY_ALREADY_EXIST);
        }
        modelRepresentation = new ModelRepresentation(newModel);
        return modelRepresentation;
    }


    /**
     * 分页查询所有表单模型
     *
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getPageListFromModel")
    @ApiDoc(desc = "getPageListFromModel")
    public PageInfo<ActDeModel> getPageListFromModel(Map<String, Object> requestMap) {
//        ActDeModel actDeModel = new ActDeModel();
        ActDeModel actDeModel = objectMapper.convertValue(requestMap, ActDeModel.class);

        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        Long pageNum = (Long) pagination.get("pageNum");
        Long pageSize = (Long) pagination.get("pageSize");
        PageHelper.startPage(Math.toIntExact(pageNum), Math.toIntExact(pageSize));
        List<ActDeModel> allModel = actDeModelMapper.getFormModel(actDeModel);
        PageInfo<ActDeModel> pageInfo = new PageInfo<ActDeModel>(allModel);
        return pageInfo;
    }


    /**
     * 获取表单模型信息
     */
    @Override
    @ShenyuDubboClient("/getForm")
    @ApiDoc(desc = "getForm")
    public Model getFormInfo(Map<String, Object> requestMap) throws ClassNotFoundException {
        String formId = (String) requestMap.get("formId");
        Model model = this.modelService.getModel(formId);
        return model;
    }

    /**
     * 部署form表单
     */
    @Override
    @ShenyuDubboClient("/deployForm")
    @ApiDoc(desc = "deployForm")
    public void deployForm(Map<String, Object> requestMap) throws ClassNotFoundException {
        String modelId = (String) requestMap.get("id");
        String processDeploymentId = (String) requestMap.get("processDeploymentId");
        //获取模型
        Model model = modelRepositoryService.get(modelId);
        if(model == null){
            throw new BusinessException(ResponseCode.FAIL);
        }
        if (model.getModelType() == 2) {
            String modelEditorJson = model.getModelEditorJson();
            byte[] formBytes = modelEditorJson.getBytes(StandardCharsets.UTF_8);
            String processName = model.getName() + ".form";
            try {
                //部署对应的bpmn模型
                formRepositoryService.createDeployment()
                        .name(model.getName())
                        .addFormBytes(processName, formBytes)
                        .parentDeploymentId(processDeploymentId)
                        .deploy();
            } catch (Exception e) {
                throw new BusinessException(ResponseCode.FAIL);
            }
        } else {
            throw new BadRequestException("模型类型不匹配，无法部署");
        }
    }
}
