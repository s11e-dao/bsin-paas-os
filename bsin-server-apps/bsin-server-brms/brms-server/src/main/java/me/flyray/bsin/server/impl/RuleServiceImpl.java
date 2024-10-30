/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.flyray.bsin.server.impl;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.flyray.bsin.domain.entity.DecisionRule;
import me.flyray.bsin.domain.entity.EventModel;
import me.flyray.bsin.facade.service.EventModelService;
import me.flyray.bsin.infrastructure.mapper.DecisionRuleMapper;
import me.flyray.bsin.server.context.DecisionEngineContextBuilder;
import org.apache.commons.collections4.MapUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.apache.shenyu.common.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import me.flyray.bsin.server.handler.JsonToDroolsConverter;

import me.flyray.bsin.domain.entity.DubboTest;
import me.flyray.bsin.facade.service.RuleService;
import me.flyray.bsin.mq.producer.RocketMQProducer;
import me.flyray.bsin.utils.BsinResultEntity;
import org.springframework.stereotype.Service;

/**
 * 规则管理
 */
@ShenyuDubboService(path = "/rule", timeout = 6000)
@ApiModule(value = "rule")
@Service
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RocketMQProducer rocketMQProducer;
    @Value("${rocketmq.consumer.topic}")
    private String topic;
    @Autowired
    private DecisionRuleMapper decisionRuleMapper;
    @Autowired
    private DecisionEngineContextBuilder decisionEngineContextBuilder;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleServiceImpl.class);
    @DubboReference(version = "${dubbo.provider.version}")
    private EventModelService eventModelService;

    @ShenyuDubboClient("/sendMq")
    @ApiDoc(desc = "sendMq")
    @Override
    public BsinResultEntity<DubboTest> sendMq(DubboTest bean) {

        JSONObject mQMsgReq = new JSONObject();
        mQMsgReq.put("requisitionId", "requisitionId");
        mQMsgReq.put("eventCode", bean.getEventCode());
        // 延时消息等级分为18个：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        SendCallback callback = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }
            @Override
            public void onException(Throwable throwable) {
                System.out.println("发送失败");
            }
        };
        rocketMQProducer.sendDelay(topic,mQMsgReq.toString(), callback,4);
        return BsinResultEntity.ok(bean);
    }

    @Override
    @ShenyuDubboClient("/add")
    @ApiDoc(desc = "add")
    // @GlobalTransactional
    public DecisionRule add(final DecisionRule decisionRule) throws IOException {
        // 模型转换
        JSONObject ruleJson = decisionRule.getRuleJson();
        // json 转换成drl字符串文件
        String content = JsonToDroolsConverter.convertToJsonToDrl(ruleJson.toJSONString());
        decisionRule.setContent(content);
        decisionRule.setContentJson(ruleJson.toJSONString());
        decisionRuleMapper.insert(decisionRule);
        LOGGER.info(GsonUtils.getInstance().toJson(RpcContext.getContext().getAttachments()));
        return decisionRule;
    }

    @Override
    @ShenyuDubboClient("/getList")
    @ApiDoc(desc = "getList")
    public List<DecisionRule> getList(Map<String, Object> requestMap) {
        String tenantId = MapUtils.getString(requestMap, "tenantId");
        List<DecisionRule> decisionRuleList = decisionRuleMapper.getDecisionRuleList(tenantId);
        return decisionRuleList;
    }

    @Override
    @ShenyuDubboClient("/testRule")
    @ApiDoc(desc = "testRule")
    // @GlobalTransactional
    public DubboTest testRule(Map<String, Object> requestMap) {

        String eventCode = MapUtils.getString(requestMap, "eventCode");
        // 根据事件查询对应规则
        EventModel eventModel = eventModelService.getDetail(requestMap);
        DecisionRule decisionRule = decisionRuleMapper.selectById(eventModel.getModelNo());

        // 1、构建决策引擎环境
        KieSession kieSession = decisionEngineContextBuilder.buildDecisionEngine(decisionRule,
                decisionRule.getKieBaseName() + "-session");

        // 创建globalMap
        Map<String, Object> globalMap = new HashMap<>();
        kieSession.setGlobal("globalMap", globalMap);

        //TODO 2、执行决策引擎，一个决策模型对应多个kieSession
//        StringBuilder resultInfo = new StringBuilder();
//        kieSession.setGlobal("resultInfo", resultInfo);
        // 添加fact（事实对象，接收数据的对象实体类），会员基本信息
        // 解析规则引擎before部分，根据内容获取事实数据，和判断事实是否为空

        Map<String, Object> factMap = new HashMap<>();
        factMap.put("sex", "女");
        factMap.put("userAge", "25");
        factMap.put("userName", "李四");

        kieSession.insert(factMap);
        // 触发规则
        kieSession.fireAllRules();
        // 关闭KieSession
        kieSession.dispose();
        //TODO 获取命中的规则，执行规则计算
        // 根据决策状态和决策结果触发规则事件服务
        String serviceName = (String) requestMap.get("serviceName");
        String methodName = (String) requestMap.get("methodName");
        System.out.println(serviceName);
        System.out.println(methodName);
        // 3、返回决策执行结果
        // 打印globalMap的内容
        System.out.println("globalMap内容：" + globalMap);

        return new DubboTest(eventCode, "hello world shenyu Apache, findById");
    }



}
