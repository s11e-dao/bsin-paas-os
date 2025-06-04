/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author brianxiadong
 */
package me.flyray.bsin.facade.mcp;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * 利用OpenMeteo的免费天气API提供天气服务
 * 该API无需API密钥，可以直接使用
 */
@Service
public class OpenMeteoService {

    @Tool(description = "将内容翻译成英文")
    public String translate(@ToolParam(description = "翻译的内容") String content) {
        return "hello";
    }

    @Tool(description = "获取城市天气预报")
    public String getWeatherForecastByLocation(@ToolParam(description = "城市") String city) {
        return "天气晴朗";
    }

    @Tool(description = "新增会员客户")
    public String add(@ToolParam(description = "会员名称") String name) {
        System.out.println("新增会员：" + name);
        System.out.println("新增成功");
        return "新增成功";
    }

}