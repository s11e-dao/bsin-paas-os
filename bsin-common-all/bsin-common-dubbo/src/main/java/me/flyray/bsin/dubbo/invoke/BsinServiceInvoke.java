package me.flyray.bsin.dubbo.invoke;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bsin rpc 服务调用工具类
 */

@Slf4j
@Service
public class BsinServiceInvoke {

    private static ConcurrentHashMap<String, GenericService> concurrentHashMapBolt = new ConcurrentHashMap<>();

    public Object genericInvoke(String serviceName, String methodName, String version, Map<String, Object> reqParam) {

        ApplicationConfig application = new ApplicationConfig();
        application.setName("api-generic-provider");

        //注册中心
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("nacos://127.0.0.1:8848");
        application.setRegistry(registry);

        // 引用远程服务
        ReferenceConfig<GenericService> reference = new ReferenceConfig<GenericService>();
        reference.setApplication(application);
        reference.setInterface("me.flyray.bsin.facade.service." + serviceName); // 服务接口名
        reference.setGeneric(true); // 开启泛化调用
        reference.setUrl("dubbo://localhost:20880"); // 服务提供方URL

        // 引用服务
        GenericService genericService = reference.get();

        // 泛化调用
        Object result = genericService.$invoke(methodName, new String[]{"java.lang.String"}, new Object[]{reqParam});
        return result;
    }

    /**
     * 接口计费服务
     * {
     *   "serviceName":"TenantApiConsumingRecordService",
     *   "methodName": "apiConsuming",
     *   "bizParams": {
     *   // 消费记录参数
     *     "tenantId":"租户ID"，
     *     "appId":"应用ID"，
     *     "apiName":"调用接口名称"，
     *     // 出账参数
     *     "serialNo":"账户编号"，
     *     "customerNo":"客户编号"，
     *     "amount":"金额"，
     *     "orderType":订单类型 0、支付 1、退款 2、出售  3、充值 4、转账 5、提现，
     *     "orderNo":"订单号"
     *   }
     * }
     * @return
     */
    public Object billingService(Map<String, Object> req){
        Object result = this.genericInvoke("TenantApiConsumingRecordService", "apiConsuming", "1.0", req);
        //TODO 如果是异常需要抛出异常
        log.info("调用接口消费计费服务结果:{}", JSON.toJSONString(result));
        return result;
    }


}
