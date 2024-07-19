package me.flyray.bsin.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alipay.sofa.rpc.api.GenericService;
import com.alipay.sofa.rpc.common.json.JSON;
import com.alipay.sofa.rpc.config.ApplicationConfig;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.registry.RegistryFactory;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * bsin rpc 服务调用工具类
 */

@Slf4j
@Service
public class BsinServiceInvokeUtil {

    private static ConcurrentHashMap<String, GenericService> concurrentHashMapBolt = new ConcurrentHashMap<>();

    /**
     * sofa泛化调用
     */
    public Map genericInvokeWithBolt(String serviceName, String methodName, String version, Map<String, Object> reqParam) {

        ConsumerConfig<GenericService> consumerConfig = new ConsumerConfig<GenericService>()
                .setInterfaceId("me.flyray.bsin.facade.service." + serviceName)
                .setGeneric(true)
                .setProtocol("bolt")
                .setSubscribe(true)
                .setRegister(true)
                .setTimeout(300000)
                .setRepeatedReferLimit(-1)
                .setRegistry(RegistryFactory.getRegistryConfigs());

        if (StringUtils.isNotBlank(version)){
            // 设置接口版本，兼容之前未加版本接口
            consumerConfig.setUniqueId(version);
        }
        // 解决sofa consumerConfig重复实例化不能超过三次问题
        String genericServiceKey = consumerConfig.buildKey();
        GenericService genericService = concurrentHashMapBolt.get(genericServiceKey);
        if (null == genericService) {
            synchronized (GenericService.class) {
                genericService = concurrentHashMapBolt.get(genericServiceKey);
                if (null == genericService) {
                    genericService = consumerConfig.refer();
                    concurrentHashMapBolt.put(genericServiceKey, genericService);
                }
            }
        }
        // 根据服务和方法及参数进行远程RPC调用
        Map result = (Map) genericService.$invoke(methodName, new String[]{"java.util.Map"}, new Object[]{reqParam});
        return result;
    }

    public Map genericInvoke(String serviceName, String methodName, String version, Map<String, Object> reqParam) {
        String protocol = String.valueOf(reqParam.get("protocol"));
        boolean zkFLag = false;
        List<RegistryConfig> registryConfigs = RegistryFactory.getRegistryConfigs();
        for (RegistryConfig registryConfig : registryConfigs) {
            if ("zookeeper".equals(registryConfig.getProtocol())) {
                zkFLag = true;
                break;
            }
        }
        if ("dubbo".equals(protocol) && !zkFLag) {
            log.error("dubbo调用仅限注册中心为zookeeper！");
            protocol = "bolt";
        }
        Map result = this.genericInvokeWithBolt(serviceName, methodName, version, reqParam);
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
    public Map<String ,Object> billingService(Map<String, Object> req){
        Map<String ,Object> result = this.genericInvoke("TenantApiConsumingRecordService", "apiConsuming", "1.0", req);
        //TODO 如果是异常需要抛出异常
        log.info("调用接口消费计费服务结果:{}", JSON.toJSONString(result));
        return result;
    }


}
