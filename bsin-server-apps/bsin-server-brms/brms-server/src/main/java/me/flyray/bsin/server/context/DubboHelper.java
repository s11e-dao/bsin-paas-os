package me.flyray.bsin.server.context;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DubboHelper {

    /**
     * 统一使用bsinInvoke
     * @param args
     */
    public static void main(String[] args) {
        DubboHelper dubboHelper = new DubboHelper();
        Object r = dubboHelper.genericCall("me.flyray.bsin.facade.ReEventService#test", "{\"test\":1}");
        System.out.println(r);
    }

    @SuppressWarnings("all")
    public Object genericCall(String coordinate, String requestBodyJson) {
        return 1;
//        return genericCall(coordinate, JSON.parseObject(requestBodyJson, Map.class));
    }

    @SuppressWarnings("all")
    public Object genericCall(String coordinate, Map<String, Object> requestBody) {
        return 1;
//        String[] cArray = coordinate.split("#");
//        RegistryConfig registryConfig = new RegistryConfig();
//        registryConfig.setAddress("nacos://127.0.0.1:8848");
//
//        ApplicationConfig applicationConfig = new ApplicationConfig();
//        applicationConfig.setName("generic-call-consumer");
//        applicationConfig.setRegistry(registryConfig);
//
//        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
//        referenceConfig.setApplication(applicationConfig);
//        referenceConfig.setGeneric(true);
//
//        referenceConfig.setInterface(cArray[0]);
//        GenericService genericService = referenceConfig.get();
//        Object resultObject = genericService.$invoke(cArray[1], new String[]{"java.util.Map"}, new Object[]{requestBody});
//        if (resultObject == null) throw new BusinessException("DUBBO_CALL_RESULT_NULL");
//        return (HashMap<String, Object>) resultObject;
    }

}
