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


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import me.flyray.bsin.infrastructure.config.DefaultMenuConfig;
import me.flyray.bsin.infrastructure.config.TenantConfig;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * TestDubboApplication.
 */

@EnableFileStorage
@SpringBootApplication(scanBasePackages = "me.flyray.bsin")
@EnableDubbo
@MapperScan("me.flyray.bsin.infrastructure.mapper")
@EnableConfigurationProperties({TenantConfig.class, DefaultMenuConfig.class})
@EnableAspectJAutoProxy(exposeProxy = true)
public class BsinUpmsApplication {
    
    /**
     * Main Entrance.
     * @param args startup arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(BsinUpmsApplication.class, args);
    }

}
