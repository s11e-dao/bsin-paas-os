package org.apache.shenyu.plugin.rulesEngine;

import com.alibaba.fastjson.JSON;
import me.flyray.bsin.domain.params.ExecuteParams;
import me.flyray.bsin.facade.ReReleaseService;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

import java.util.HashMap;
import java.util.Map;

public class DubboService {

    public static Map<?, ?> call(String eventKey, Map<?, ?> params) {
        ReferenceConfig<ReReleaseService> reference = new ReferenceConfig<>();
        reference.setInterface(ReReleaseService.class);
        DubboBootstrap instance = DubboBootstrap.getInstance();
        instance.application("first-dubbo-consumer");
        instance.registry(new RegistryConfig("nacos://192.168.1.165:8848"));
        instance.reference(reference);

        ReReleaseService r = reference.get();
        ExecuteParams executeParams = new ExecuteParams();
        executeParams.setEventKey(eventKey);
        executeParams.setJsonParams(JSON.toJSONString(params));
        return r.execute(executeParams);
    }

    public static void main(String[] args) {
        Map<?, ?> userRegistration = call("user_registration", new HashMap<>() {{
            put("registrationParmas_phoneNumber", "+370123456");
            put("registrationParmas_nationality", "CN");
            put("registrationParmas_positioning", "CN");
            put("registrationParmas_LatitudeAndLongitude", "TestMessage");
        }});
        System.out.println("asdffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"+userRegistration);
    }

}
