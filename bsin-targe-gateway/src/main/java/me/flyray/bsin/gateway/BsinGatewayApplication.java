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

package me.flyray.bsin.gateway;

import org.apache.shenyu.plugin.sofa.param.SofaParamResolveService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import me.flyray.bsin.gateway.convert.CustomerSofaParamResolveHandler;

/**
 * bsin bootstrap.
 */
@SpringBootApplication
public class BsinGatewayApplication {

    /**
     * Main Entrance.
     *
     * @param args startup arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(BsinGatewayApplication.class, args);
    }

    /**
     * 自定义泛化调用请求参数组装
     * @return
     */
    @Bean
    public SofaParamResolveService sofaParamResolveService(){
        return new CustomerSofaParamResolveHandler();
    }

    /**
     * 自定义open API签名校验
     * @return
     */
//    @Bean
//    public SignService customSignService() {
//        return new CustomSignService();
//    }

}
