package me.flyray.bsin.log.event;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.log.enums.LoginActionEnum;
import me.flyray.bsin.log.enums.LoginStatusEnum;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.utils.AddressUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 异步调用日志服务
 *
 * @author ruoyi
 */

@Component
@Slf4j
public class LogEventListener {

    @Autowired
    private BsinServiceInvoke bsinServiceInvoke;

    /**
     * 保存系统日志记录
     */
    @EventListener
    public void saveOperLog(OperLogEvent operLogEvent) {
        System.out.println("收到自定义的operLogEvent事件了-------");
        Map<String, Object> requestMap = BeanUtil.beanToMap(operLogEvent);
        // 远程查询操作地点
        String ip = AddressUtils.getRealAddressByIP(operLogEvent.getOperIp());
        requestMap.put("ip", ip);
        // 异步调用（泛化调用解耦）订单完成方法统一处理： 根据订单类型后续处理
        bsinServiceInvoke.genericInvoke("LogOperateService", "add", "dev", requestMap);

    }

    /**
     * 保存系统访问记录
     */
    @EventListener
    public void saveLoginLog(LoginInfoEvent logininforEvent) {
        System.out.println("收到自定义的logininforEvent事件了-------");
        Map<String, Object> attachments = RpcContext.getServiceContext().getObjectAttachments();
        log.info("attachments：{}", attachments);
        final UserAgent userAgent = UserAgentUtil.parse(MapUtil.getStr(attachments, "userAgent", ""));
        String ip = MapUtil.getStr(attachments, "clientIp", null);
        String address = AddressUtils.getRealAddressByIP(ip);
        StringBuilder logInfo = new StringBuilder();
        logInfo.append(getBlock(ip));
        logInfo.append(address);
        logInfo.append(getBlock(logininforEvent.getUsername()));
        logInfo.append(getBlock(logininforEvent.getStatus()));
        logInfo.append(getBlock(logininforEvent.getMessage()));
        // 打印信息到日志
        log.info(logInfo.toString(), logininforEvent.getArgs());
        // 获取客户端操作系统
        String os = userAgent != null ? userAgent.getOs().getName() : "";
        // 获取客户端浏览器
        String browser = userAgent != null ? userAgent.getBrowser().getName() : "";
        Map<String, Object> requestMap = BeanUtil.beanToMap(logininforEvent);
        // 日志状态
        if (StringUtils.equalsAny(logininforEvent.getStatus(), LoginActionEnum.LOGIN_SUCCESS.name(), LoginActionEnum.LOGOUT.name(), LoginActionEnum.REGISTER.name())) {
            requestMap.put("status", LoginStatusEnum.SUCCESS.getStatus());
        } else if (LoginActionEnum.LOGIN_FAIL.name().equals(logininforEvent.getStatus())) {
            requestMap.put("status", LoginStatusEnum.SUCCESS.getStatus());
        }
        // 异步调用（泛化调用解耦）订单完成方法统一处理： 根据订单类型后续处理
        bsinServiceInvoke.genericInvoke("LogLoginService", "add", "dev", requestMap);

    }

    private String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg + "]";
    }

}
