package me.flyray.bsin.infrastructure.util;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.common.Constants;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author chenj
 * @date 2020.8.24
 * @description 流程引擎使用类
 */
public class WorkflowUtils {

    /**
     * 判断字符串是否为空
     * @param str
     */
    public static boolean hasText(String str) {
        return StringUtils.hasText(str) && str.trim().length() >= 1;
    }

    /**
     * 空入参异常处理
     * @param param 入参
     * @param description 异常描述（字段（中文描述））
     */
    public static void detectNullParam(Object param, String description) {
        if (ExtOptional.isLogicalEmptyObject(param))
            throw new BusinessException(ResponseCode.NULL_PARAMETER);
    }

    /**
     * 挂起检查及异常处理
     * @param object 对象
     */
    public static void detectInvalidSuspension(Object object) {
        if (!validateSuspend(object))
            throw new BusinessException(ResponseCode.SUSPEND_FAILURE);
    }

    public static void detectInvalidOperationInSuspendedInstance(Object object) {
        if (!validateSuspend(object))
            throw new BusinessException(ResponseCode.TASK_OPERATION_NOT_ALLOWED_IN_SUSPENDED_INSTANCE);
    }

    public static void detectInvalidOperationInSuspendedTask(Object object) {
        if (!validateSuspend(object))
            throw new BusinessException(ResponseCode.TASK_OPERATION_NOT_ALLOWED_IN_SUSPENDED_TASK);
    }

    private static boolean validateSuspend(Object object) {
        if (object instanceof TaskEntity)
            return !((TaskEntity) object).isSuspended();
        if (object instanceof ProcessInstance)
            return !((ProcessInstance) object).isSuspended();
        if (object instanceof ProcessDefinition)
            return !((ProcessDefinition) object).isSuspended();
        return false;
    }

    /**
     * 激活检查及异常处理
     * @param object 对象
     */
    public static void detectInvalidActivation(Object object) {
        if (!validateActivate(object))
            throw new BusinessException(ResponseCode.ACTIVATE_FAILURE);
    }

    public static boolean validateActivate(Object object) {
        if (object instanceof TaskEntity)
            return ((TaskEntity) object).getSuspensionState() == 0;
        if (object instanceof ProcessInstance)
            return ((ProcessInstance) object).isSuspended();
        if (object instanceof ProcessDefinition)
            return ((ProcessDefinition) object).isSuspended();
        return false;
    }

    /**
     * 格式转换：date转String
     * @param date 日期
     * @return yyyy-MM-dd HH:mm:ss格式的字符串
     */
    public static String date2String(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 判断字符串是否符合NCName和QName标准
     * @param str 字符串
     */
    public static boolean validateName(String str) {
        return !(str.contains(":") || str.contains("@") || str.contains("$") || str.contains("%")
                || str.contains("&") || str.contains("/") || str.contains("+") || str.contains(",")
                || str.contains(";") || str.charAt(0) == '.' || str.charAt(0) == '-'
                || (str.charAt(0) >= '0' && str.charAt(0) <= '9') );
    }

    /**
     * 获取map属性值
     * @param key 属性key
     */
    public static <T> T getAttr(Map<String,Object> map, String key) {
        return (T) map.get(key);
    }

    /**
     * 用冒号拼接字符串数组strs
     * @param strs 字符串数组
     * @return 拼接后的结果
     */
    public static String joinStringWithColon(String... strs){
        StringBuilder stringBuilder=new StringBuilder();
        for (String str:
             strs) {
            stringBuilder.append(str).append(":");
        }
        int lastIndex=stringBuilder.length();
        stringBuilder.delete(lastIndex-1,lastIndex);
        return stringBuilder.toString();
    }

    /**
     * 去除变量map中的内部key
     */
    public static Map<String, Object> innerKeyFilter(Map<String, Object> var) {
        Map<String, Object> variables = Optional.ofNullable(var).orElse(new HashMap<>());
        if (!variables.isEmpty())
            for (String key : Constants.INNER_KEYS)
                variables.remove(key);
        return variables;
    }

}
