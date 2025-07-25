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

package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DecisionRule;
import me.flyray.bsin.domain.entity.DubboTest;
import me.flyray.bsin.utils.BsinResultEntity;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * DubboTestService.
 */
@Validated
public interface DecisionRuleService {

    public BsinResultEntity<DubboTest> sendMq(DubboTest bean);

    public DecisionRule add(final DecisionRule decisionRule) throws IOException;

    public DecisionRule edit(final DecisionRule decisionRule) throws IOException;

    /**
     * 所有
     */
    public List<DecisionRule> getList(Map<String, Object> requestMap);

    /**
     * 所有
     */
    public List<DecisionRule> getListByNos(Map<String, Object> requestMap);

    /** 分页所有 */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * find by id.
     * body：{"id":"1223"}
     * @return DubboTest dubbo test
     */
    public DubboTest testRule(Map<String, Object> requestMap);

}
