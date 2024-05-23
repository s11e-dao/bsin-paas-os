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



import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.stereotype.Service;

import java.util.Random;

import me.flyray.bsin.domain.entity.SofaSimpleTypeBean;
import me.flyray.bsin.facade.service.SofaClientSingleParamService;

/**
 * Sofa single param service.
 */
@ShenyuSofaClient("/demo/**")
@Service
public class SofaClientSingleParamServiceImpl implements SofaClientSingleParamService {
    
    @Override
    public SofaSimpleTypeBean findById(final String id) {
        return new SofaSimpleTypeBean(id, "hello world shenyu Sofa, findById");
    }
    
    @Override
    public SofaSimpleTypeBean findAll() {
        return new SofaSimpleTypeBean(String.valueOf(new Random().nextInt()), "hello world shenyu Sofa , findAll");
    }
    
    @Override
    public SofaSimpleTypeBean insert(final SofaSimpleTypeBean sofaSimpleTypeBean) {
        sofaSimpleTypeBean.setName("hello world shenyu Sofa: " + sofaSimpleTypeBean.getName());
        return sofaSimpleTypeBean;
    }
}
