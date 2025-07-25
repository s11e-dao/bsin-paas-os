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

package me.flyray.bsin.server;

import me.flyray.bsin.server.context.DecisionEngineContextBuilder;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * TestDubboApplication.
 */

@EnableDubbo
// @EnableTransactionManagement // 默认开启
@MapperScan("me.flyray.bsin.infrastructure.mapper")
@SpringBootApplication(scanBasePackages = "me.flyray.bsin")
public class BsinBrmsApplication {
    
    /**
     * Main Entrance.
     *
     * @param args startup arguments
     */
    public static void main(final String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(BsinBrmsApplication.class, args);
        DecisionEngineContextBuilder decisionEngineContextBuilder = (DecisionEngineContextBuilder) applicationContext.getBean("decisionEngineContextBuilder");
        // 应用启动的时候，加载数据库中规则在规则库中
        decisionEngineContextBuilder.decisionEngineInitial();
    }

}
