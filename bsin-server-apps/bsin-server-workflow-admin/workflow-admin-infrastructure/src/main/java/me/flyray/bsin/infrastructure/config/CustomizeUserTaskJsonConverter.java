package me.flyray.bsin.infrastructure.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import me.flyray.bsin.infrastructure.utils.ExtensionAttributeUtils;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.ActivityProcessor;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 新建自定义userTaskjson解析器CustomizeUserTaskJsonConverter
 *
 * @author guzt
 */
public class CustomizeUserTaskJsonConverter extends UserTaskJsonConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizeUserTaskJsonConverter.class);

    private static final String USER_INFO = "userinfo";
    private static final String ORG_ID = "orgid";
    private static final String APPROVER_STYPE = "approverstype";

    /**
     * 覆盖原 UserTaskJsonConverter 中的 fillTypes 方法
     *
     * @param convertersToBpmnMap map
     * @param convertersToJsonMap map
     * @see org.flowable.editor.language.json.converter.BpmnJsonConverter  中的静态代码块
     */
    public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap,
                                 Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {

        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    /**
     * 覆盖原 UserTaskJsonConverter 中的 fillBpmnTypes 方法
     *
     * @param convertersToJsonMap map
     * @see org.flowable.editor.language.json.converter.BpmnJsonConverter  中的静态代码块
     */
    public static void fillBpmnTypes(
            Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, CustomizeUserTaskJsonConverter.class);
    }

    /**
     * 覆盖原 UserTaskJsonConverter 中的 fillJsonTypes 方法
     *
     * @param convertersToBpmnMap map
     * @see org.flowable.editor.language.json.converter.BpmnJsonConverter  中的静态代码块
     */
    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomizeUserTaskJsonConverter.class);
    }

    // 主要用于 外部 xml 导入到 flowable画图页面中
    @Override
    public void convertToJson(BpmnJsonConverterContext converterContext, BaseElement baseElement, ActivityProcessor processor, BpmnModel model, FlowElementsContainer container, ArrayNode shapesArrayNode, double subProcessX, double subProcessY) {
        super.convertToJson( converterContext,baseElement, processor, model, container, shapesArrayNode, subProcessX, subProcessY);
        if (baseElement instanceof UserTask) {
            LOGGER.debug("userTaskId = {} 扩展属性 = {}", baseElement.getId(), baseElement.getAttributes());
            Map<String, List<ExtensionAttribute>> stringListMap = baseElement.getAttributes();
            String userinfo = "";
            String orgid = "";
            String approverstype = "";
            if(!stringListMap.isEmpty()){
                if(!stringListMap.get(USER_INFO).isEmpty()){
                    userinfo = stringListMap.get(USER_INFO).get(0).getValue();
                }
                if(!stringListMap.get(ORG_ID).isEmpty()){
                    orgid = stringListMap.get(ORG_ID).get(0).getValue();
                }
                if(!stringListMap.get(APPROVER_STYPE).isEmpty()) {
                    approverstype = stringListMap.get(APPROVER_STYPE).get(0).getValue();
                }
            }
            String finalUserinfo = userinfo;
            String finalOrgid = orgid;
            String finalApproverstype = approverstype;
            shapesArrayNode.forEach(node -> {
                if (baseElement.getId().equals(node.get("resourceId").textValue())) {
                    ObjectNode properties = (ObjectNode) node.get("properties");
                    properties.set(USER_INFO, new TextNode(finalUserinfo));
                    properties.set(ORG_ID, new TextNode(finalOrgid));
                    properties.set(APPROVER_STYPE, new TextNode(finalApproverstype));
//                    properties.set(ORG_ID, BooleanNode.valueOf(Boolean.parseBoolean(orgid)));
//                    properties.set(APPROVER_STYPE, BooleanNode.valueOf(Boolean.parseBoolean(approverstype)));
                }
            });
        }
    }

    // 主要用于 页面属性加载和导出 xml使用
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode,
                                               Map<String, JsonNode> shapeMap, BpmnJsonConverterContext converterContext) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap,converterContext);

        if (flowElement instanceof UserTask) {
            LOGGER.debug("进入自定义属性解析CustomizeUserTaskJsonConverter...");
            Map<String, List<ExtensionAttribute>> attributes = flowElement.getAttributes();
            String userinfo = getPropertyValueAsString(USER_INFO, elementNode);
            if (StringUtils.isEmpty(userinfo)) {
                LOGGER.debug("nodetype 属性为空，设置为默认值");
                userinfo = "";
            }
            attributes.put(USER_INFO, Collections.singletonList(
                    ExtensionAttributeUtils.generate(USER_INFO, userinfo)));

            String orgid = getPropertyValueAsString(ORG_ID, elementNode);
            if (StringUtils.isEmpty(orgid)) {
                LOGGER.debug("revokeflag 属性为空，设置为默认值");
                orgid = "";
            }
            attributes.put(ORG_ID, Collections.singletonList(
                    ExtensionAttributeUtils.generate(ORG_ID, orgid)));

            String approverstype = getPropertyValueAsString(APPROVER_STYPE, elementNode);
            if (StringUtils.isEmpty(approverstype)) {
                LOGGER.debug("endflag 属性为空，设置为默认值");
                approverstype = "";
            }
            attributes.put(APPROVER_STYPE, Collections.singletonList(
                    ExtensionAttributeUtils.generate(APPROVER_STYPE, approverstype)));
            LOGGER.debug("自定义属性解析CustomizeUserTaskJsonConverter 完成");
            LOGGER.debug("当前 attributes 为 {}", attributes);
        }

        return flowElement;
    }


}


