import React, { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import { LoadingOutlined } from '@ant-design/icons'
import ProTable from '@ant-design/pro-table'
import { Modal, Popconfirm, message, Form, Input, Divider, Spin } from 'antd'
import io, { Socket } from 'socket.io-client';
import { Layout, Row, Col } from 'antd'
const { Header, Content, Footer, Sider } = Layout

import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'

export default () => {

  const [socket, setSocket] = useState<WebSocket | null>(null);
  const [connected, setConnected] = useState(false);
  const [messages, setMessages] = useState<string[]>([]);
  
  useEffect(() => {

    // 创建 WebSocket 连接
    let newSocket = new WebSocket("ws://192.168.198.197:9195/ws-ai-agent/chat/f/t/1");

    // 连接成功的处理函数
    newSocket.onopen = () => {
      console.log('WebSocket连接成功');
      setConnected(true);
    };

    // 接收消息的处理函数
    newSocket.onmessage = (event) => {
      console.log('收到消息:', event.data);
      // 显示到对话框里面
      setMessages((prevMessages) => [...prevMessages, event.data]);
    };

    // 连接关闭的处理函数
    newSocket.onclose = () => {
      console.log('WebSocket连接关闭');
      setConnected(false);
    };

    // 连接错误的处理函数
    newSocket.onerror = (error) => {
      console.error('WebSocket错误:', error);
      setConnected(false);
    };

    setSocket(newSocket);

    // 清理函数
    return () => {
      newSocket.close();
    };
  }, []);

  // 发送消息的方法
  const sendMessage = (message: string) => {
    if (socket?.readyState === WebSocket.OPEN) {
      socket.send(message);
    } else {
      console.error('WebSocket未连接');
    }
  };

  return (
    <Layout className="body">
      <Content style={{ background: 'none', padding: 0, margin: 24 }}>
        <div>Connection status: {connected ? 'Connected' : 'Disconnected'}</div>
        <div>
          <button onClick={() => sendMessage('Hello!')}>Send Message</button>
        </div>
        <div>
          Messages:
          {messages.map((msg, index) => (
            <div key={index}>{msg}</div>
          ))}
        </div>
      </Content>
    </Layout>
  )
}
