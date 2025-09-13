import React, { useState } from 'react'
import TableTitle from '@/components/TableTitle'
import {
  Button,
  Card,
  Row,
  Col,
  Space,
  Typography,
  message,
} from 'antd'
import { 
  PlusOutlined,
} from '@ant-design/icons'
import AppManagement from './components/AppManagement'
import { ChatBox } from 'bsin-agent-ui'
import { getAppConfig, AppConfig, customerOperationApps, collaborationApps, AppDisplayItem } from './config/appConfigs'
import './index.less'

const { Title, Text } = Typography


// 应用卡片组件
interface AppCardProps {
  app: AppDisplayItem
  onConnect: (app: AppDisplayItem) => void
  onCustom: (app: AppDisplayItem) => void
}

const AppCard = ({ app, onConnect, onCustom }: AppCardProps) => {
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
  // 页面状态管理
  const [currentView, setCurrentView] = useState('list') // 'list' | 'management' | 'chat'
  const [currentAppConfig, setCurrentAppConfig] = useState<AppConfig | null>(null)
  const [currentApp, setCurrentApp] = useState<any>(null) // 当前聊天的应用

  // 处理接入操作
  const handleConnect = (app: AppDisplayItem) => {
    const appConfig = getAppConfig(app.id)
    if (appConfig) {
      // 跳转到应用管理页面
      setCurrentAppConfig(appConfig)
      setCurrentView('management')
    } else {
      message.info(`正在接入${app.title}...`)
      // 这里可以添加具体的接入逻辑
    }
  }

  // 处理自建操作
  const handleCustom = (app: AppDisplayItem) => {
    message.info(`正在创建自建${app.title}...`)
    // 这里可以添加具体的自建逻辑
  }


  // 返回应用列表
  const handleBackToList = () => {
    setCurrentView('list')
    setCurrentAppConfig(null)
    setCurrentApp(null)
  }

  // 处理聊天功能
  const handleChat = (app: any) => {
    setCurrentApp(app)
    setCurrentView('chat')
  }

  // 从聊天返回管理页面
  const handleBackToManagement = () => {
    setCurrentView('management')
  }

  // 如果当前是聊天页面，显示聊天组件
  if (currentView === 'chat' && currentApp) {
    return (
      <ChatBox
        chatSiderShow={false}
        currentRecord={currentApp}
        setCurrentContent={handleBackToManagement}
      />
    )
  }

  // 如果当前是管理页面，显示应用管理组件
  if (currentView === 'management' && currentAppConfig) {
    return (
      <AppManagement
        appConfig={currentAppConfig}
        onBack={handleBackToList}
        onChat={handleChat}
      />
    )
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
