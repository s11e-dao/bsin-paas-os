import React, { useState, useEffect } from 'react'
import {
  Card,
  Button,
  Space,
  Form,
  Input,
  Row,
  Col,
  Avatar,
  Modal,
  message,
  Popconfirm,
  Typography,
} from 'antd'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  MessageOutlined,
  SettingOutlined,
  ReloadOutlined,
  WechatOutlined,
  CustomerServiceOutlined,
  TeamOutlined,
  DingdingOutlined,
  CloudOutlined,
  AppstoreOutlined,
} from '@ant-design/icons'
import { getBizRoleAppPageList, addBizRoleApp, editBizRoleApp, deleteBizRoleApp } from '../service'
import { AppConfig } from '../config/appConfigs'

const { Meta } = Card
const { Title, Text } = Typography
const { TextArea } = Input

interface AppManagementProps {
  appConfig: AppConfig
  onBack: () => void
  onChat?: (app: AppItem) => void
}

interface AppItem {
  serialNo: string
  appName: string
  appId: string
  appDescription: string
  status: string
  [key: string]: any
}

// 应用图标映射
const iconMap: Record<string, React.ReactNode> = {
  'wechat': <WechatOutlined style={{ fontSize: 32, color: '#07C160' }} />,
  'wechat-public-enterprise': <WechatOutlined style={{ fontSize: 32, color: '#07C160' }} />,
  'wechat-public-personal': <WechatOutlined style={{ fontSize: 32, color: '#07C160' }} />,
  'wechat-service': <CustomerServiceOutlined style={{ fontSize: 32, color: '#07C160' }} />,
  'enterprise-wechat': <TeamOutlined style={{ fontSize: 32, color: '#4F8EF0' }} />,
  'dingtalk': <DingdingOutlined style={{ fontSize: 32, color: '#0088FF' }} />,
  'feishu': <CloudOutlined style={{ fontSize: 32, color: '#00D6B9' }} />,
  'enterprise-wechat-app': <AppstoreOutlined style={{ fontSize: 32, color: '#4F8EF0' }} />,
}

// 字段配置
const fieldConfig: Record<string, { label: string; placeholder: string; required: boolean; type?: string }> = {
  appName: { label: '应用名称', placeholder: '请输入应用名称', required: true },
  appId: { label: '应用ID', placeholder: '请输入应用ID', required: true },
  appSecret: { label: '应用密钥', placeholder: '请输入应用密钥', required: true },
  token: { label: 'Token', placeholder: '请输入Token', required: true },
  aesKey: { label: 'AES密钥', placeholder: '请输入AES密钥', required: true },
  notifyUrl: { label: '回调地址', placeholder: '请输入回调地址', required: true },
  agentId: { label: '智能体ID', placeholder: '请输入智能体ID（可选）', required: false },
  appDescription: { label: '应用描述', placeholder: '请输入应用描述', required: true, type: 'textarea' },
}

export default ({ appConfig, onBack, onChat }: AppManagementProps) => {
  const [appList, setAppList] = useState<AppItem[]>([])
  const [loading, setLoading] = useState(false)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [editingApp, setEditingApp] = useState<AppItem | null>(null)
  const [form] = Form.useForm()

  // 获取应用列表
  const fetchAppList = async () => {
    setLoading(true)
    try {
      const params = {
        current: 1,
        pageSize: 99,
        appChannel: appConfig.appChannel,
      }
      const res = await getBizRoleAppPageList(params)
      if (res && (res.code === '000000' || res.code === 0)) {
        setAppList(res.data?.records || [])
      }
    } catch (error) {
      console.error('获取应用列表失败:', error)
      message.error('获取应用列表失败')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchAppList()
  }, [appConfig.appChannel])

  // 新增/编辑应用
  const handleSubmit = async (values: Record<string, any>) => {
    try {
      const appData = {
        ...values,
        appChannel: appConfig.appChannel,
        status: '1', // 审核通过
        appStatus: '1', // 在线
      }

      if (editingApp) {
        await editBizRoleApp({ ...appData, serialNo: editingApp.serialNo })
        message.success('应用更新成功！')
      } else {
        await addBizRoleApp(appData)
        message.success('应用创建成功！')
      }

      setIsModalVisible(false)
      setEditingApp(null)
      form.resetFields()
      fetchAppList()
    } catch (error) {
      console.error('保存应用失败:', error)
      message.error('保存应用失败')
    }
  }

  // 删除应用
  const handleDelete = async (app: AppItem) => {
    try {
      await deleteBizRoleApp(app.serialNo)
      message.success('应用删除成功！')
      fetchAppList()
    } catch (error) {
      console.error('删除应用失败:', error)
      message.error('删除应用失败')
    }
  }

  // 打开编辑模态框
  const openEditModal = (app: AppItem) => {
    setEditingApp(app)
    form.setFieldsValue(app)
    setIsModalVisible(true)
  }

  // 打开新增模态框
  const openAddModal = () => {
    setEditingApp(null)
    form.resetFields()
    form.setFieldsValue({
      appChannel: appConfig.appChannel,
      status: '1',
      appStatus: '1',
    })
    setIsModalVisible(true)
  }

  // 关闭模态框
  const closeModal = () => {
    setIsModalVisible(false)
    setEditingApp(null)
    form.resetFields()
  }

  // 搜索
  const handleSearch = (values: Record<string, any>) => {
    console.log('搜索参数:', values)
    // 这里可以实现搜索逻辑
    fetchAppList()
  }

  return (
    <div style={{ padding: '24px' }}>
      {/* 页面头部 */}
      <div style={{ marginBottom: '24px' }}>
        <Button onClick={onBack} style={{ marginRight: '16px' }}>
          返回
        </Button>
        <Title level={3} style={{ display: 'inline-block', margin: 0 }}>
          {appConfig.title}管理
        </Title>
      </div>

      {/* 搜索表单 */}
      <Card style={{ marginBottom: '24px' }}>
        <Form form={form} onFinish={handleSearch} layout="inline">
          <Row gutter={24} style={{ width: '100%' }}>
            <Col span={8}>
              <Form.Item name="appName" label="应用名称">
                <Input placeholder="请输入应用名称" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="appId" label="应用ID">
                <Input placeholder="请输入应用ID" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Space>
                <Button onClick={() => form.resetFields()}>重置</Button>
                <Button type="primary" htmlType="submit">
                  搜索
                </Button>
              </Space>
            </Col>
          </Row>
        </Form>
      </Card>

      {/* 应用列表 */}
      <Card
        title={`${appConfig.title}列表`}
        extra={
          <Space>
            <Button 
              icon={<ReloadOutlined />} 
              onClick={fetchAppList}
              loading={loading}
            >
              刷新
            </Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={openAddModal}>
              新增{appConfig.title}
            </Button>
          </Space>
        }
      >
        {appList.length > 0 ? (
          <Space size="middle" style={{ display: 'flex', flexWrap: 'wrap' }}>
            {appList.map((app) => (
              <Card
                key={app.serialNo}
                style={{ width: 300 }}
                actions={[
                  <EditOutlined
                    key="edit"
                    onClick={() => openEditModal(app)}
                    title="编辑"
                  />,
                  <Popconfirm
                    key="delete"
                    title="确定要删除这个应用吗？"
                    onConfirm={() => handleDelete(app)}
                    okText="确定"
                    cancelText="取消"
                  >
                    <DeleteOutlined title="删除" />
                  </Popconfirm>,
                  ...(onChat ? [
                    <MessageOutlined
                      key="chat"
                      onClick={() => onChat(app)}
                      title="聊天"
                    />
                  ] : []),
                ]}
              >
                <Meta
                  avatar={
                    <Avatar 
                      icon={iconMap[appConfig.id] || <SettingOutlined />}
                      style={{ backgroundColor: '#1890ff' }}
                    />
                  }
                  title={app.appName}
                  description={
                    <div style={{
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      lineHeight: '1.4',
                      height: '2.8em'
                    }}>
                      {app.appDescription}
                    </div>
                  }
                />
                <div style={{ marginTop: '12px', fontSize: '12px', color: '#999' }}>
                  <div>应用ID: {app.appId}</div>
                  <div>状态: {app.status === '1' ? '已启用' : '待审核'}</div>
                </div>
              </Card>
            ))}
          </Space>
        ) : (
          <div style={{
            width: '100%',
            height: '200px',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            color: '#999',
            fontSize: '16px'
          }}>
            <div style={{ marginBottom: '16px' }}>
              {iconMap[appConfig.id] || <SettingOutlined />}
            </div>
            <div>暂无{appConfig.title}数据</div>
            <Button type="primary" onClick={openAddModal} style={{ marginTop: '16px' }}>
              创建第一个{appConfig.title}
            </Button>
          </div>
        )}
      </Card>

      {/* 新增/编辑模态框 */}
      <Modal
        title={editingApp ? `编辑${appConfig.title}` : `新增${appConfig.title}`}
        open={isModalVisible}
        onCancel={closeModal}
        onOk={() => form.submit()}
        width={600}
        okText="确定"
        cancelText="取消"
      >
        <Form
          form={form}
          onFinish={handleSubmit}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
        >
          {appConfig.requiredFields.map((field) => (
            <Form.Item
              key={field}
              label={fieldConfig[field]?.label}
              name={field}
              rules={[
                { required: fieldConfig[field]?.required, message: `请输入${fieldConfig[field]?.label}!` }
              ]}
            >
              {fieldConfig[field]?.type === 'textarea' ? (
                <TextArea 
                  rows={3} 
                  placeholder={fieldConfig[field]?.placeholder}
                  maxLength={200}
                  showCount
                />
              ) : (
                <Input placeholder={fieldConfig[field]?.placeholder} />
              )}
            </Form.Item>
          ))}
          
          {appConfig.optionalFields.map((field) => (
            <Form.Item
              key={field}
              label={fieldConfig[field]?.label}
              name={field}
            >
              {fieldConfig[field]?.type === 'textarea' ? (
                <TextArea 
                  rows={3} 
                  placeholder={fieldConfig[field]?.placeholder}
                  maxLength={200}
                  showCount
                />
              ) : (
                <Input placeholder={fieldConfig[field]?.placeholder} />
              )}
            </Form.Item>
          ))}
        </Form>
      </Modal>
    </div>
  )
}
