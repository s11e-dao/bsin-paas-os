package me.flyray.bsin.server.impl;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.stereotype.Service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.engine.CustomerBehaviorIncentiveEngine;
import me.flyray.bsin.server.utils.RespBodyHandler;

/**
 * @author bolei
 * @date 2023/8/23
 * @desc
 */


@Slf4j
@ShenyuDubboService(path = "/customerBehaviorIncentiveEngine", timeout = 6000)
@ApiModule(value = "customerBehaviorIncentiveEngine")
@Service
public class CustomerBehaviorIncentiveEngineImpl implements CustomerBehaviorIncentiveEngine {


    /**
     * 限制调用权限，防止恶意调用
     * 事件激励检查：eventType 动作事件 活动事件 任务事件
     * 1、完成活动、任务、发放激励调用
     * 2、完成事件触发活动规则判断
     * 3、调用激励发放
     * 4、如果涉及账户和资产调用激励发放，
     * 5、触发等级升级
     * 6、等级升级后的激励发放
     */
    @Override
    public Map<String, Object> excute(Map<String, Object> requestMap) {
        System.out.println("===== 激励开始执行 =====");


        System.out.println("===== 激励执行结束 =====");
        return RespBodyHandler.RespBodyDto();
    }

}
