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
  Select,
  Radio,
  QRCode,
  Spin,
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
  LoginOutlined,
  LogoutOutlined,
} from '@ant-design/icons'
import { getBizRoleAppPageList, addBizRoleApp, editBizRoleApp, deleteBizRoleApp, getAgentPagetList, wechatAgentLogin } from '../service'
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
  appDescription: string | null
  status: string
  appChannel: string
  appSecret: string
  token: string | null
  aesKey: string | null
  notifyUrl: string | null
  agentId: string | null
  corpId: string | null
  mchId: string | null
  tenantId: string
  bizRoleType: string
  bizRoleTypeNo: string
  appStatus: string | null
  createTime: string
  updateTime: string
  delFlag: number
}

interface ApiResponse<T> {
  code: number
  data: T
  message: string
  pagination?: {
    pageNum: number
    pageSize: number
    totalSize: number
  }
}

interface AgentItem {
  serialNo: string
  name: string
  description: string
  agentType: string | null
  status: number
  capabilities: number
  defaultFlag: number
  issueFlag: number
  accessAuthority: number | null
  createBy: string | null
  updateBy: string | null
  createTime: string | null
  updateTime: string | null
  tenantId: string
  bizRoleType: string | null
  bizRoleTypeNo: string | null
  roleDefinition: string | null
  prologue: string | null
  skills: string
  choreographyContent: string | null
  iconUrl: string | null
  version: string | null
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
  agentId: { label: '智能体', placeholder: '请选择智能体（可选）', required: false, type: 'select' },
  appDescription: { label: '应用描述', placeholder: '请输入应用描述', required: true, type: 'textarea' },
}

export default ({ appConfig, onBack, onChat }: AppManagementProps) => {
  const [appList, setAppList] = useState<AppItem[]>([])
  const [agentList, setAgentList] = useState<AgentItem[]>([])
  const [loading, setLoading] = useState(false)
  const [isModalVisible, setIsModalVisible] = useState(false)
  const [editingApp, setEditingApp] = useState<AppItem | null>(null)
  const [form] = Form.useForm()
  
  // 登录相关状态
  const [isLoginModalVisible, setIsLoginModalVisible] = useState(false)
  const [operationAction, setOperationAction] = useState<'loginWechat' | 'logoutWechat'>('loginWechat')
  const [qrCodeUrl, setQrCodeUrl] = useState<string>('')
  const [loginLoading, setLoginLoading] = useState(false)
  const [currentLoginApp, setCurrentLoginApp] = useState<AppItem | null>(null)

  // 获取应用列表
  const fetchAppList = async () => {
    setLoading(true)
    try {
      const params = {
        pageNum: 1,
        pageSize: 99,
        appChannel: appConfig.appChannel,
      }
      const res: ApiResponse<AppItem[]> = await getBizRoleAppPageList(params)
      if (res && res.code === 0) {
        setAppList(res.data || [])
      } else {
        message.error(res?.message || '获取应用列表失败')
      }
    } catch (error) {
      console.error('获取应用列表失败:', error)
      message.error('获取应用列表失败')
    } finally {
      setLoading(false)
    }
  }

  // 获取智能体列表
  const fetchAgentList = async () => {
    try {
      const params = {
        pageNum: 1,
        pageSize: 99,
      }
      const res: ApiResponse<AgentItem[]> = await getAgentPagetList(params)
      if (res && res.code === 0) {
        setAgentList(res.data || [])
      }
    } catch (error) {
      console.error('获取智能体列表失败:', error)
    }
  }

  useEffect(() => {
    fetchAppList()
    fetchAgentList()
  }, [appConfig.appChannel])

  // 新增/编辑应用
  const handleSubmit = async (values: Record<string, any>) => {
    try {
      const appData = {
        ...values,
        appChannel: appConfig.appChannel,
        status: '1', // 审核通过
        appStatus: '1', // 在线
        delFlag: 0, // 未删除
        bizRoleType: '1', // 默认业务角色类型
      }

      if (editingApp) {
        const res = await editBizRoleApp({ ...appData, serialNo: editingApp.serialNo })
        if (res && res.code === 0) {
          message.success('应用更新成功！')
        } else {
          message.error(res?.message || '应用更新失败')
          return
        }
      } else {
        const res = await addBizRoleApp(appData)
        if (res && res.code === 0) {
          message.success('应用创建成功！')
        } else {
          message.error(res?.message || '应用创建失败')
          return
        }
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
      const res = await deleteBizRoleApp({ serialNo: app.serialNo })
      if (res && res.code === 0) {
        message.success('应用删除成功！')
        fetchAppList()
      } else {
        message.error(res?.message || '应用删除失败')
      }
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
      delFlag: 0,
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

  // 打开登录模态框
  const openLoginModal = (app: AppItem) => {
    setCurrentLoginApp(app)
    setOperationAction('loginWechat')
    setQrCodeUrl('')
    setIsLoginModalVisible(true)
  }

  // 关闭登录模态框
  const closeLoginModal = () => {
    setIsLoginModalVisible(false)
    setCurrentLoginApp(null)
    setQrCodeUrl('')
    setLoginLoading(false)
  }

  // 处理登录/退出操作
  const handleLoginAction = async () => {
    if (!currentLoginApp) return

    setLoginLoading(true)
    try {
      const params = {
        serialNo: currentLoginApp.serialNo,
        operation: operationAction, // 'loginWechat' 或 'logoutWechat'
      }

      const res = await wechatAgentLogin(params)
      if (res && res.code === 0) {
        if (operationAction === 'loginWechat' && res.data?.notifyUrl) {
          setQrCodeUrl(res.data.notifyUrl)
          message.success('请使用微信扫描二维码登录')
        } else {
          message.success(operationAction === 'loginWechat' ? '登录成功！' : '退出成功！')
          closeLoginModal()
        }
      } else {
        message.error(res?.message || `${operationAction === 'loginWechat' ? '登录' : '退出'}失败`)
      }
    } catch (error) {
      console.error(`${operationAction === 'loginWechat' ? '登录' : '退出'}失败:`, error)
      message.error(`${operationAction === 'loginWechat' ? '登录' : '退出'}失败`)
    } finally {
      setLoginLoading(false)
    }
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
                  // 个人微信应用添加登录按钮
                  ...(appConfig.id === 'wechat' ? [
                    <LoginOutlined
                      key="login"
                      onClick={() => openLoginModal(app)}
                      title="登录/退出"
                    />
                  ] : []),
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
                  <div>创建时间: {app.createTime}</div>
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
              ) : fieldConfig[field]?.type === 'select' ? (
                <Select
                  placeholder={fieldConfig[field]?.placeholder}
                  allowClear
                  showSearch
                  optionFilterProp="children"
                  filterOption={(input, option) => {
                    const agent = agentList.find(a => a.serialNo === option?.value)
                    if (!agent) return false
                    return agent.name.toLowerCase().includes(input.toLowerCase()) ||
                           (agent.description ? agent.description.toLowerCase().includes(input.toLowerCase()) : false)
                  }}
                  optionLabelProp="label"
                >
                  {agentList.map((agent) => (
                    <Select.Option 
                      key={agent.serialNo} 
                      value={agent.serialNo}
                      label={agent.name}
                    >
                      <div style={{ padding: '4px 0' }}>
                        <div style={{ 
                          fontWeight: 'bold', 
                          fontSize: '14px',
                          color: '#262626',
                          marginBottom: '2px'
                        }}>
                          {agent.name}
                        </div>
                        {agent.description && (
                          <div style={{ 
                            fontSize: '12px', 
                            color: '#8c8c8c',
                            lineHeight: '1.4',
                            maxWidth: '200px',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap'
                          }}>
                            {agent.description}
                          </div>
                        )}
                      </div>
                    </Select.Option>
                  ))}
                </Select>
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
              ) : fieldConfig[field]?.type === 'select' ? (
                <Select
                  placeholder={fieldConfig[field]?.placeholder}
                  allowClear
                  showSearch
                  optionFilterProp="children"
                  filterOption={(input, option) => {
                    const agent = agentList.find(a => a.serialNo === option?.value)
                    if (!agent) return false
                    return agent.name.toLowerCase().includes(input.toLowerCase()) ||
                           (agent.description ? agent.description.toLowerCase().includes(input.toLowerCase()) : false)
                  }}
                  optionLabelProp="label"
                >
                  {agentList.map((agent) => (
                    <Select.Option 
                      key={agent.serialNo} 
                      value={agent.serialNo}
                      label={agent.name}
                    >
                      <div style={{ padding: '4px 0' }}>
                        <div style={{ 
                          fontWeight: 'bold', 
                          fontSize: '14px',
                          color: '#262626',
                          marginBottom: '2px'
                        }}>
                          {agent.name}
                        </div>
                        {agent.description && (
                          <div style={{ 
                            fontSize: '12px', 
                            color: '#8c8c8c',
                            lineHeight: '1.4',
                            maxWidth: '200px',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap'
                          }}>
                            {agent.description}
                          </div>
                        )}
                      </div>
                    </Select.Option>
                  ))}
                </Select>
              ) : (
                <Input placeholder={fieldConfig[field]?.placeholder} />
              )}
            </Form.Item>
          ))}
        </Form>
      </Modal>

      {/* 登录/退出模态框 */}
      <Modal
        title={`${currentLoginApp?.appName || '应用'} - 登录/退出`}
        open={isLoginModalVisible}
        onCancel={closeLoginModal}
        onOk={handleLoginAction}
        okText="确定"
        cancelText="取消"
        confirmLoading={loginLoading}
        width={500}
      >
        <div style={{ padding: '20px 0' }}>
          <div style={{ marginBottom: '20px' }}>
            <Text strong>请选择操作：</Text>
            <Radio.Group
              value={operationAction}
              onChange={(e) => setOperationAction(e.target.value)}
              style={{ marginTop: '10px' }}
            >
              <Radio value="loginWechat">
                <Space>
                  <LoginOutlined />
                  登录
                </Space>
              </Radio>
              <Radio value="logoutWechat">
                <Space>
                  <LogoutOutlined />
                  退出
                </Space>
              </Radio>
            </Radio.Group>
          </div>

          {qrCodeUrl && (
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
              <Text strong style={{ display: 'block', marginBottom: '15px' }}>
                请使用微信扫描下方二维码登录
              </Text>
              <div style={{ 
                display: 'flex', 
                justifyContent: 'center', 
                alignItems: 'center',
                minHeight: '200px',
                backgroundColor: '#f5f5f5',
                borderRadius: '8px',
                padding: '20px'
              }}>
                {loginLoading ? (
                  <Spin size="large" />
                ) : (
                  <QRCode
                    value={qrCodeUrl}
                    size={200}
                    status="active"
                    onRefresh={() => handleLoginAction()}
                  />
                )}
              </div>
              <Text type="secondary" style={{ display: 'block', marginTop: '10px', fontSize: '12px' }}>
                二维码链接：{qrCodeUrl}
              </Text>
            </div>
          )}
        </div>
      </Modal>
    </div>
  )
}
