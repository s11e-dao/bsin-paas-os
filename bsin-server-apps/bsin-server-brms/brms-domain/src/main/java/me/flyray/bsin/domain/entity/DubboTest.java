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

package me.flyray.bsin.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import me.flyray.bsin.validate.AddGroup;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.StringJoiner;

/**
 * DubboTest.
 */
public class DubboTest implements Serializable {

    private String id;

    @NotBlank(message = "事件编号不能为空")
    private String eventCode;


    @JsonFormat(pattern = "HH:mm:ss")
    private Date time=new Date();

    public DubboTest() {
    }

    public DubboTest(final String id, final String eventCode) {
        this.id = id;
        this.eventCode = eventCode;
    }

    /**
     * Get id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Set id.
     *
     * @param id id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Get name.
     *
     * @return name
     */
    public String getEventCode() {
        return eventCode;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * Set name.
     *
     * @param name name
     */
    public void setEventCode(final String eventCode) {
        this.eventCode = eventCode;
    }

    @Override
    public String toString() {
        return "DubboTest{" +
                "id='" + id + '\'' +
                ", eventCode='" + eventCode + '\'' +
                ", time=" + time +
                '}';
    }
}
