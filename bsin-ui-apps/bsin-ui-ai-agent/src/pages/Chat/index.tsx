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
  const [messages, setMessages] = useState<string[]>([]);
  const [connected, setConnected] = useState(false);


  useEffect(() => {

    let newSocket = new WebSocket("ws://192.168.198.197:9195/ws-annotation/myWs");

    setSocket(newSocket)


    // 清理函数
    return () => {
      newSocket.close();
    };

  }, []);

  // 发送消息
  const sendMessage = (message: string) => {
    if (socket) {
      socket.send(message);
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
