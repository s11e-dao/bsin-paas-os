package me.flyray.bsin.infrastructure.biz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.flyray.bsin.domain.entity.ModelDefinition;
import me.flyray.bsin.exception.BusinessException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.idm.api.User;
import org.flowable.ui.common.model.RemoteUser;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelKeyRepresentation;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Service
public class ModelBiz {
    public static final int MODEL_TYPE_DECISION_SERVICE = 6;
    @Autowired
    private org.flowable.ui.modeler.serviceapi.ModelService modelService;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected ModelRepository repository;

    /**
     * 更新模型
     * @param model
     * @param values
     * @param forceNewVersion
     * @return
     */
    public ModelRepresentation updateModel(Model model, MultiValueMap<String, String> values, boolean forceNewVersion) {
        String name = values.getFirst("name");
        String key = values.getFirst("key").replaceAll(" ", "");
        String description = values.getFirst("description");
        String isNewVersionString = values.getFirst("newversion");
        String json = values.getFirst("json_xml");
        String newVersionComment = null;
        //校验模型id和模型类型、key查出来的模型id是否相同，不同设置KeyAlreadyExists为true
        ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(model, model.getModelType(), key);
        if (modelKeyInfo.isKeyAlreadyExists()) {
            throw new BadRequestException("提供密钥的模型已经存在 " + key);
        }
        //判断保存的时候是否是新版本
        boolean newVersion = false;
        //是新版本就获取comment
        if (forceNewVersion) {
            newVersion = true;
            newVersionComment = values.getFirst("comment");
        } else {
            if (isNewVersionString != null) {
                newVersion = "true".equals(isNewVersionString);
                newVersionComment = values.getFirst("comment");
            }
        }
        try {
            //读取json
            ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(json);
            //获取属性节点
            ObjectNode propertiesNode = (ObjectNode) editorJsonNode.get("properties");
            //通过判断选择put不同的字段
            if (Model.MODEL_TYPE_BPMN == model.getModelType()) {
                propertiesNode.put("process_id", key);
            } else if (MODEL_TYPE_DECISION_SERVICE == model.getModelType()) {
                propertiesNode.put("drd_id", key);
            } if (Model.MODEL_TYPE_CMMN == model.getModelType()) {
                propertiesNode.put("case_id", key);
            }
            propertiesNode.put("name", name);
            if (StringUtils.isNotEmpty(description)) {
                propertiesNode.put("documentation", description);
            }
            editorJsonNode.set("properties", propertiesNode);
            /*model = modelService.saveModel(model.getId(), name, key, description, editorJsonNode.toString(), newVersion,
                    newVersionComment, SecurityUtils.getCurrentUserObject());*/
            return new ModelRepresentation(model);

        } catch (Exception e) {
            throw new BadRequestException("无法保存模型 " + model.getId());
        }
    }

    /**
     * 参数转换
     *
     * @param definitions
     * @return
     */
    public List<ModelDefinition> convertDefinitionList(List<ProcessDefinition> definitions) {
        //将传入的模型定义进行dto转换的方法
        List<ModelDefinition> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(definitions)) {
            for (ProcessDefinition processDefinition : definitions) {
                ModelDefinition rep = new ModelDefinition(processDefinition);
                result.add(rep);
            }
        }
        return result;
    }

    /**
     * 根据传入的信息创建模型
     *
     * @param model
     * @param editorJson
     * @param createdBy
     * @return
     */
    public Model createModel(ModelRepresentation model, String editorJson, User createdBy) {
        Model newModel = new Model();
        newModel.setVersion(1);
        newModel.setName(model.getName());
        newModel.setKey(model.getKey());
        newModel.setModelType(model.getModelType());
        newModel.setCreated(Calendar.getInstance().getTime());
        newModel.setCreatedBy(createdBy.getId());
        newModel.setDescription(model.getDescription());
        newModel.setModelEditorJson(editorJson);
        newModel.setLastUpdated(Calendar.getInstance().getTime());
        newModel.setLastUpdatedBy(createdBy.getId());
        newModel.setTenantId(model.getTenantId());
        newModel.setComment(model.getComment());
        this.repository.save(newModel);
        return newModel;
    }

    /**
     * 保存对应模型的流程信息
     */
    public Model saveModelXML(String modelId, String modelXML, String updatedBy, boolean isVersion) {
        //将传入的String类型的模型流程xml转换为字节流
        byte[] modelxmlToByte = modelXML.getBytes();
        InputStream modelInputStream = new ByteArrayInputStream(modelxmlToByte);
        XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
        InputStreamReader xmlIn = new InputStreamReader(modelInputStream, StandardCharsets.UTF_8);
        XMLStreamReader xtr = null;
        //再转换为XMLStreamReader
        try {
            xtr = xif.createXMLStreamReader(xmlIn);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
        BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
        //xml转换为bpmn格式后再转为bpmn
        BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
        //将前端的传过来的xml转换为ObjectNode并添加modelId属性
        ObjectNode editorJson = bpmnJsonConverter.convertToJson(bpmnModel);
        editorJson.put("id", modelId);
        //重key校验在存在model属性时可加可不加
        Model model = modelService.getModel(modelId);
        String modelKey = model.getKey();
//        ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(model, model.getModelType(), modelKey);
//        if (modelKeyInfo.isKeyAlreadyExists()) {
//            throw new BusinessException("模型键已存在");
//        }
        User user = new RemoteUser();
        if (updatedBy != null) {
            user.setId(updatedBy);
        } else {
            user.setId(model.getLastUpdatedBy());
        }

        try {
            //获取node当中的属性信息
            ObjectNode propertiesNode = (ObjectNode) editorJson.get("properties");
            //添加modelKey属性值
            propertiesNode.put("process_id", modelKey);
            propertiesNode.put("name", model.getName());
            if (StringUtils.isNotEmpty(model.getDescription())) {
                propertiesNode.put("documentation", model.getDescription());
            }
            editorJson.set("properties", propertiesNode);

            //保存模型
            model = modelService.saveModel(model.getId(), model.getName(), modelKey, model.getDescription(), editorJson.toString(), isVersion,
                    model.getComment(), String.valueOf(user));
            return model;
        } catch (Exception e) {
            throw new BusinessException("模型保存错误");
        }

    }

    public List<ValidationError> validationErrors(BpmnModel bpmnModel) {
        //利用原生校验器对需要保存的bpmn模型进行校验
        ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
        List<ValidationError> errors = validator.validate(bpmnModel);
        if (!CollectionUtils.isEmpty(errors)) {
            return errors;
        }
        return Collections.emptyList();
    }

    /**
     * 将xml转为bpmn模型
     */
    public BpmnModel StringXMLToBpmnModel(String modelId, String modelXML) {
        //将传入的String类型的模型流程xml转换为字节流
        byte[] modelxmlToByte = modelXML.getBytes();
        InputStream modelInputStream = new ByteArrayInputStream(modelxmlToByte);
        XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
        InputStreamReader xmlIn = new InputStreamReader(modelInputStream, StandardCharsets.UTF_8);
        XMLStreamReader xtr = null;
        //再转换为XMLStreamReader
        try {
            xtr = xif.createXMLStreamReader(xmlIn);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
        BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
        //xml转换为bpmn格式后再转为bpmn
        BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
        //将前端的传过来的xml转换为ObjectNode并添加modelId属性
        ObjectNode editorJson = bpmnJsonConverter.convertToJson(bpmnModel);
        //添加modelId属性
        editorJson.put("id", modelId);
        //添加modelKey属性
        Model model = modelService.getModel(modelId);
        ObjectNode propertiesNode = (ObjectNode) editorJson.get("properties");
        propertiesNode.put("process_id", model.getKey());
        //统一添加name、document属性
        propertiesNode.put("name", model.getName());
        if (StringUtils.isNotEmpty(model.getDescription())) {
            propertiesNode.put("documentation", model.getDescription());
        }
        editorJson.set("properties", propertiesNode);
        return bpmnJsonConverter.convertToBpmnModel(editorJson);
    }
}
