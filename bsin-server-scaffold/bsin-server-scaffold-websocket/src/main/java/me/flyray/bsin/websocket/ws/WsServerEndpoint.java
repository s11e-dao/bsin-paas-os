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

package me.flyray.bsin.websocket.ws;

import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.filter.WebsocketLoginInfoInterceptor;
import org.apache.shenyu.client.spring.websocket.annotation.ShenyuServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.io.IOException;

/**
 * WsServerEndpoint.
 */
@Component
@ShenyuServerEndpoint(value = "/myWs/{key}", configurator = WebsocketLoginInfoInterceptor.class)
public class WsServerEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(WsServerEndpoint.class);

    /**
     * connect successful.
     * @param session session
     */
    @OnOpen
    public void onOpen(@PathParam("key") String key, final Session session, EndpointConfig config) {
        String bizRoleType =  (String) config.getUserProperties().get("bizRoleType");
        String bizRoleTypeNo =  (String) config.getUserProperties().get("bizRoleTypeNo");
        String username =  (String) config.getUserProperties().get("username");

        LOG.info("connect bizRoleType: {}", bizRoleType);
        LOG.info("connect bizRoleTypeNo: {}", bizRoleTypeNo);

        // TODO 处理LoginInfoContextHelper，方便dubbo调用获取登录信息
        LoginInfoContextHelper.set("bizRoleType", bizRoleType);
        LoginInfoContextHelper.set("bizRoleTypeNo", bizRoleTypeNo);
        LoginInfoContextHelper.set("username", username);

        // 获取拦截器中存储的用户信息
        Boolean authenticated = (Boolean) config.getUserProperties().get("authenticated");
        if (authenticated != null && authenticated) {
            // 处理已认证的连接

        } else {
            // 处理未认证的连接
            try {
                session.close(new CloseReason(
                        CloseReason.CloseCodes.VIOLATED_POLICY,
                        "Unauthorized connection"
                ));
            } catch (IOException e) {
                // 处理关闭异常
            }
        }

        LOG.info("connect successful");
    }


    /**
     * connect close.
     * @param session session
     */
    @OnClose
    public void onClose(final Session session) {
        LOG.info("connect closed");
    }

    /**
     * received message.
     * @param text message
     * @return response
     */
    @OnMessage
    public String onMsg(@PathParam("key") String historyId, final String text) {
        LOG.info("server revice: {}", text);
        return "server send message：" + text;
    }

}
