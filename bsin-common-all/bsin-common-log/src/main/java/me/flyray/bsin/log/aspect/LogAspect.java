package me.flyray.bsin.log.aspect;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.flyray.bsin.log.annotation.Log;
import me.flyray.bsin.log.enums.OperateStatus;
import me.flyray.bsin.log.event.OperLogEvent;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.ServletUtils;
import me.flyray.bsin.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.dubbo.rpc.RpcContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;


/**
 * 操作日志记录处理
 *
 * @author Lion Li
 */
@Slf4j
@Aspect
public class LogAspect {

    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = {"password", "oldPassword", "newPassword", "confirmPassword"};

    /**
     * 计时 key
     */
    private static final ThreadLocal<StopWatch> KEY_CACHE = new ThreadLocal<>();

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerLog)")
    public void doBefore(JoinPoint joinPoint, Log controllerLog) {
        StopWatch stopWatch = new StopWatch();
        KEY_CACHE.set(stopWatch);
        stopWatch.start();
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "outputParams")
    public void doAfterReturning(JoinPoint joinPoint, Log controllerLog, Object outputParams) {
        handleLog(joinPoint, controllerLog, null, outputParams);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, Log controllerLog, final Exception e, Object outputParams) {
        try {
            Map<String, Object> attachments = RpcContext.getServiceContext().getObjectAttachments();
            // *========数据库日志=========*//
            OperLogEvent operLog = new OperLogEvent();
            operLog.setTenantId(LoginInfoContextHelper.getTenantId());
            operLog.setStatus(OperateStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = MapUtil.getStr(attachments, "clientIp", null);
            operLog.setOperIp(ip);
            String requestUrl = MapUtil.getStr(attachments, "requestUrl", "");
            operLog.setRequestUrl(StringUtils.substring(requestUrl, 0, 255));
            LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
            operLog.setOperBy(loginUser.getUsername());
            if (e != null) {
                operLog.setStatus(OperateStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operLog.setRequestMethod(MapUtil.getStr(attachments, "method", null));
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog, outputParams);
            // 设置消耗时间
            StopWatch stopWatch = KEY_CACHE.get();
            stopWatch.stop();
            operLog.setCostTime(stopWatch.getTime());
            final UserAgent userAgent = UserAgentUtil.parse(MapUtil.getStr(attachments, "userAgent", ""));
            operLog.setOs(userAgent != null ? userAgent.getOs().getName() : "");
            // 获取客户端浏览器
            String browser = userAgent != null ? userAgent.getBrowser().getName() : "";
            operLog.setBrowser(browser);
            // 发布事件保存数据库
            SpringUtil.context().publishEvent(operLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("异常信息", exp.getMessage());
            exp.printStackTrace();
        } finally {
            KEY_CACHE.remove();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, OperLogEvent operLog, Object outputParams) throws Exception {
        // 设置标题
        operLog.setMethodTitle(log.title());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog, log.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (log.isSaveResponseData() && ObjectUtil.isNotNull(outputParams)) {
            operLog.setOutputParam(StringUtils.substring(JSON.toJSONString(outputParams), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, OperLogEvent operLog, String[] excludeParamNames) throws Exception {
        HttpServletRequest request = null;
        try {
            request = ServletUtils.getRequest();
        } catch (Exception e) {
            log.info("获取HttpServletRequest失败");
        }
        if (request != null) {
            Map<String, String> paramsMap = ServletUtils.getParamMap(request);
            String requestMethod = operLog.getRequestMethod();
            if (MapUtil.isEmpty(paramsMap)
                    && HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
                String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
                operLog.setInputParam(StringUtils.substring(params, 0, 2000));
            } else {
                MapUtil.removeAny(paramsMap, EXCLUDE_PROPERTIES);
                MapUtil.removeAny(paramsMap, excludeParamNames);
                operLog.setOutputParam(StringUtils.substring(JSON.toJSONString(paramsMap), 0, 2000));
            }
        } else {
            //RPC框架
            String requestArgs = null;
            Map<String, Object> attachments = RpcContext.getServiceContext().getObjectAttachments();
            String requestParams = MapUtil.getStr(attachments, "requestParams", null);
            if (StringUtils.isNotBlank(requestParams)) {
                requestParams = requestParams.replace("\r", "").replace("\n", "").replace(" ", "");
                if (JSONUtil.isTypeJSONObject(requestParams)) {
                    requestArgs = JSON.parseObject(requestParams).toJSONString();
                } else if (JSONUtil.isTypeJSONArray(requestParams)) {
                    requestArgs = JSON.parseArray(requestParams).toJSONString();
                }
                operLog.setInputParam(requestArgs);
            }
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        StringJoiner params = new StringJoiner(" ");
        if (ArrayUtil.isEmpty(paramsArray)) {
            return params.toString();
        }
        for (Object o : paramsArray) {
            if (ObjectUtil.isNotNull(o) && !isFilterObject(o)) {
                String str = JSON.toJSONString(o);
                Dict dict = JSON.parseObject(str, Dict.class);
                if (MapUtil.isNotEmpty(dict)) {
                    MapUtil.removeAny(dict, EXCLUDE_PROPERTIES);
                    MapUtil.removeAny(dict, excludeParamNames);
                    str = JSON.toJSONString(dict);
                }
                params.add(str);
            }
        }
        return params.toString();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.values()) {
                return value instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }
}
