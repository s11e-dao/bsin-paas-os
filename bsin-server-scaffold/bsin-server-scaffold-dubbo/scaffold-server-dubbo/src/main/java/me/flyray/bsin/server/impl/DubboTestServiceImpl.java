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


import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.apache.shenyu.common.utils.GsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Collections;
import java.util.Random;

import lombok.Setter;
import me.flyray.bsin.domain.entity.DubboTest;
import me.flyray.bsin.domain.entity.ListResp;
import me.flyray.bsin.facade.service.DubboTestService;
import me.flyray.bsin.mq.producer.RocketMQProducer;
import me.flyray.bsin.utils.BsinResultEntity;

/**
 * The type Dubbo service.
 */
@DubboService
@ApiModule(value = "dubboTestService")
public class DubboTestServiceImpl implements DubboTestService {

    @Autowired
    private RocketMQProducer rocketMQProducer;
    @Value("${rocketmq.consumer.topic}")
    private String topic;

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboTestServiceImpl.class);

    @ShenyuDubboClient("/add")
    @ApiDoc(desc = "add")
    @Override
    public BsinResultEntity<DubboTest> add(DubboTest bean) {
        return BsinResultEntity.ok(bean);
    }

    @ShenyuDubboClient("/sendMq")
    @ApiDoc(desc = "sendMq")
    @Override
    public BsinResultEntity<DubboTest> sendMq(DubboTest bean) {
        Message<String> msg = MessageBuilder.withPayload("Hello,RocketMQ").build();
        // 延时消息等级分为18个：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        SendCallback callback = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("123");
            }
            @Override
            public void onException(Throwable throwable) {
                System.out.println("456");
            }
        };
        rocketMQProducer.sendDelay(topic,"张三", callback,7);
        return BsinResultEntity.ok(bean);
    }

    @Override
    @ShenyuDubboClient("/findById")
    @ApiDoc(desc = "findById")
    // @GlobalTransactional
    public DubboTest findById(final String id) {
       /* if ("1".equals(id)) {
            throw new BusinessException(new I18eCode("exception.insert.data.to.db"));
        }*/
        LOGGER.info(GsonUtils.getInstance().toJson(RpcContext.getContext().getAttachments()));
        return new DubboTest(id, "hello world shenyu Apache, findById");
    }
    
    @Override
    @ShenyuDubboClient("/findAll")
    @ApiDoc(desc = "findAll")
    public DubboTest findAll() {
        return new DubboTest(String.valueOf(new Random().nextInt()), "hello world shenyu Apache, findAll");
    }
    
    @Override
    @ShenyuDubboClient("/insert")
    @ApiDoc(desc = "insert")
    public DubboTest insert(final DubboTest dubboTest) {
        dubboTest.setName("hello world shenyu Apache Dubbo: " + dubboTest.getName());
        return dubboTest;
    }
    
    @Override
    @ShenyuDubboClient("/findList")
    @ApiDoc(desc = "findList")
    public ListResp findList() {
        return new ListResp(1, Collections.singletonList(new DubboTest("1", "test")));
    }

}
