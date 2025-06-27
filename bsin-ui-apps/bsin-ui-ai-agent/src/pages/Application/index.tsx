import columnsData, { AppColumnsItem } from './data'
import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import {
  getLLMPageList,
  getLLMList,
  delLLMInfo,
  addLLMInfo,
  editLLMInfo,
  getLLMDetail,
} from './service'
import TableTitle from '@/components/TableTitle'
import {
  Button,
  Modal,
  Popconfirm,
  message,
  Form,
  Input,
  Divider,
  Switch,
  InputNumber,
  Radio,
  Select,
  Card,
  Row,
  Col,
  Space,
  Typography,
} from 'antd'
import { 
  PlusOutlined,
  WechatOutlined,
  CustomerServiceOutlined,
  MessageOutlined,
  TeamOutlined,
  DingdingOutlined,
  CloudOutlined,
  AppstoreOutlined,
} from '@ant-design/icons'
import { useModel } from 'umi'
import './index.less'

const { Option } = Select
const { Title, Text } = Typography

// 应用数据定义
const customerOperationApps = [
  {
    id: 'wechat-public-enterprise',
    icon: <WechatOutlined style={{ fontSize: 32, color: '#07C160' }} />,
    title: '微信公众号（企业）',
    description: '在企业公众号中接入智能回复',
    hasCustom: true,
    hasConnect: true,
  },
  {
    id: 'wechat-service',
    icon: <CustomerServiceOutlined style={{ fontSize: 32, color: '#07C160' }} />,
    title: '微信客服',
    description: '打造你的微信智能客服',
    hasCustom: false,
    hasConnect: true,
  },
  {
    id: 'wechat-public-personal',
    icon: <WechatOutlined style={{ fontSize: 32, color: '#07C160' }} />,
    title: '微信公众号（个人）',
    description: '在个人订阅号中接入智能回复',
    hasCustom: true,
    hasConnect: true,
  },
  {
    id: 'wechat',
    icon: <WechatOutlined style={{ fontSize: 32, color: '#07C160' }} />,
    title: '微信',
    description: '打造你的微信专属智能机器人',
    hasCustom: false,
    hasConnect: true,
  },
  {
    id: 'enterprise-wechat',
    icon: <TeamOutlined style={{ fontSize: 32, color: '#4F8EF0' }} />,
    title: '企微微信',
    description: '打造你的企业微信数字员工',
    hasCustom: false,
    hasConnect: true,
  },
]

const collaborationApps = [
  {
    id: 'dingtalk',
    icon: <DingdingOutlined style={{ fontSize: 32, color: '#0088FF' }} />,
    title: '钉钉',
    description: '制作你的钉钉智能机器人',
    hasCustom: true,
    hasConnect: true,
  },
  {
    id: 'feishu',
    icon: <CloudOutlined style={{ fontSize: 32, color: '#00D6B9' }} />,
    title: '飞书',
    description: '在飞书上运行你的企业助手',
    hasCustom: true,
    hasConnect: true,
  },
  {
    id: 'enterprise-wechat-app',
    icon: <AppstoreOutlined style={{ fontSize: 32, color: '#4F8EF0' }} />,
    title: '企微应用',
    description: '打造你的企业微信智能应用',
    hasCustom: true,
    hasConnect: true,
  },
]

// 应用卡片组件
const AppCard = ({ app, onConnect, onCustom }: any) => {
  return (
    <Card 
      className="app-card" 
      hoverable
      bodyStyle={{ padding: '24px' }}
    >
      <div className="app-card-content">
        <div className="app-icon">
          {app.icon}
        </div>
        <div className="app-info">
          <Title level={5} className="app-title">{app.title}</Title>
          <Text type="secondary" className="app-description">
            {app.description}
          </Text>
        </div>
        <div className="app-actions">
          <Space>
            {app.hasCustom && (
              <Button 
                icon={<PlusOutlined />} 
                onClick={() => onCustom(app)}
              >
                自建
              </Button>
            )}
            <Button 
              type="primary" 
              onClick={() => onConnect(app)}
            >
              接入
            </Button>
          </Space>
        </div>
      </div>
    </Card>
  )
}

export default () => {
  // 处理接入操作
  const handleConnect = (app: any) => {
    message.info(`正在接入${app.title}...`)
    // 这里可以添加具体的接入逻辑
  }

  // 处理自建操作
  const handleCustom = (app: any) => {
    message.info(`正在创建自建${app.title}...`)
    // 这里可以添加具体的自建逻辑
  }

  return (
    <div className="application-page">
      {/* 客户运营部分 */}
      <div className="section">
        <TableTitle title="客户运营" />
        <Row gutter={[24, 24]} style={{ marginTop: 24 }}>
          {customerOperationApps.map((app) => (
            <Col key={app.id} xs={24} sm={12} md={12} lg={12} xl={12}>
              <AppCard 
                app={app} 
                onConnect={handleConnect}
                onCustom={handleCustom}
              />
            </Col>
          ))}
        </Row>
      </div>

      {/* 协同办公部分 */}
      <div className="section">
        <TableTitle title="协同办公" />
        <Row gutter={[24, 24]} style={{ marginTop: 24 }}>
          {collaborationApps.map((app) => (
            <Col key={app.id} xs={24} sm={12} md={12} lg={12} xl={12}>
              <AppCard 
                app={app} 
                onConnect={handleConnect}
                onCustom={handleCustom}
              />
            </Col>
          ))}
        </Row>
      </div>
    </div>
  )
}
