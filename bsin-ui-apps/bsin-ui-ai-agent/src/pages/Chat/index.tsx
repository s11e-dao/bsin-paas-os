import React, { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import { LoadingOutlined } from '@ant-design/icons'
import ProTable from '@ant-design/pro-table'
import { Modal, Popconfirm, message, Form, Input, Divider, Spin } from 'antd'
import io, { Socket } from 'socket.io-client'
import { Layout, Row, Col } from 'antd'
const { Header, Content, Footer, Sider } = Layout

import {
  getQuickReplies,
  getChatHistoryList,
  getDefaultCopilot,
} from './service'
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'

export default () => {
  
  useEffect(() => {
    const socket = io("SOCKET_SERVER_URL")
    console.log(socket,'socket')
 
    socket.on('connect', () => {
      socket.emit('login', "uid");
      console.log('连接建立成功了！')
    });
   // 监听来自服务端的消息
    socket.on('new_msg', (msg) => {
      console.log('Received message:', msg);
    });
 
    socket.on('update_online_count', (onlineCount) => {
      console.log(onlineCount,'onlineCount')
    });
    socket.on('disconnect', function () {
      console.log('断开连接了')
    })
    socket.on('connect_error', (error)=>{
      console.log(error.message,'error.message')
    })
    //在组件销毁时断开连接
    return () => {
      socket.disconnect();
    };
  }, []);

  return (
    <Layout className="body">
      <Content style={{ background: 'none', padding: 0, margin: 24 }}>
        <div className="content">
          
        </div>
      </Content>
    </Layout>
  )
}
