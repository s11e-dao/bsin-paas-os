import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Row, 
  Col, 
  Statistic, 
  Progress, 
  Table, 
  Badge, 
  Tag,
  Typography,
  Space,
  Alert,
  Avatar,
  List,
  Timeline,
  Tabs
} from 'antd';
import {
  UserOutlined,
  SecurityScanOutlined,
  RocketOutlined,
  AimOutlined,
  AppstoreOutlined,
  AlertOutlined,
  MonitorOutlined,
  HistoryOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  ClockCircleOutlined
} from '@ant-design/icons';

const { Title, Text } = Typography;
const { TabPane } = Tabs;

// 模拟数据
const mockData = {
  overview: {
    totalUsers: 1248,
    activeUsers: 892,
    totalActions: 5672,
    completedTargets: 234,
    runningModels: 12,
    activeEvents: 8,
    monitoringAlerts: 3
  },
  recentActions: [
    { id: 1, user: '张三', action: '数据分析', time: '2分钟前', status: 'success' },
    { id: 2, user: '李四', action: '模型训练', time: '5分钟前', status: 'processing' },
    { id: 3, user: '王五', action: '决策审批', time: '10分钟前', status: 'success' },
    { id: 4, user: '赵六', action: '风险评估', time: '15分钟前', status: 'warning' }
  ],
  events: [
    { id: 1, title: '系统升级完成', type: 'success', time: '1小时前' },
    { id: 2, title: '数据同步异常', type: 'error', time: '2小时前' },
    { id: 3, title: '新用户注册', type: 'info', time: '3小时前' },
    { id: 4, title: '模型部署成功', type: 'success', time: '4小时前' }
  ],
  modelStatus: [
    { name: '风险预测模型', status: 'running', accuracy: 95.8, lastUpdate: '2024-06-22 10:30' },
    { name: '用户行为分析', status: 'running', accuracy: 92.3, lastUpdate: '2024-06-22 09:45' },
    { name: '市场趋势预测', status: 'stopped', accuracy: 88.7, lastUpdate: '2024-06-21 18:20' },
    { name: '智能推荐引擎', status: 'running', accuracy: 96.2, lastUpdate: '2024-06-22 11:15' }
  ]
};

const Dashboard = () => {
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => setLoading(false), 1000);
    return () => clearTimeout(timer);
  }, []);

  const getStatusColor = (status) => {
    switch (status) {
      case 'success': return 'green';
      case 'processing': return 'blue';
      case 'warning': return 'orange';
      case 'error': return 'red';
      default: return 'default';
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case 'running': return '运行中';
      case 'stopped': return '已停止';
      default: return '未知';
    }
  };

  const columns = [
    {
      title: '模型名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => (
        <Badge 
          status={status === 'running' ? 'processing' : 'default'} 
          text={getStatusText(status)} 
        />
      )
    },
    {
      title: '准确率',
      dataIndex: 'accuracy',
      key: 'accuracy',
      render: (accuracy) => `${accuracy}%`
    },
    {
      title: '最后更新',
      dataIndex: 'lastUpdate',
      key: 'lastUpdate',
    }
  ];

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      <div style={{ marginBottom: '24px' }}>
        <Title level={2} style={{ margin: 0, color: '#1890ff' }}>
          智能决策系统
        </Title>
        <Text type="secondary">实时监控系统运行状态和关键指标</Text>
      </div>

      {/* 系统提醒 */}
      {mockData.overview.monitoringAlerts > 0 && (
        <Row style={{ marginTop: '14px', marginBottom: '24px' }}>
          <Col span={24}>
            <Alert
              message="系统提醒"
              description={`当前有 ${mockData.overview.monitoringAlerts} 个监控告警需要处理，请及时查看监控模块。`}
              type="warning"
              showIcon
              closable
            />
          </Col>
        </Row>
      )}

      {/* 核心指标卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="权限管理"
              value={mockData.overview.totalUsers}
              prefix={<UserOutlined style={{ color: '#1890ff' }} />}
              suffix="个用户"
            />
            <Progress 
              percent={Math.round((mockData.overview.activeUsers / mockData.overview.totalUsers) * 100)} 
              size="small" 
              showInfo={false}
              strokeColor="#1890ff"
            />
            <Text type="secondary" style={{ fontSize: '12px' }}>
              活跃用户: {mockData.overview.activeUsers}
            </Text>
          </Card>
        </Col>
        
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="待办事项"
              value={mockData.overview.totalActions}
              prefix={<RocketOutlined style={{ color: '#52c41a' }} />}
              suffix="项任务"
            />
            <Progress 
              percent={75} 
              size="small" 
              showInfo={false}
              strokeColor="#52c41a"
            />
            <Text type="secondary" style={{ fontSize: '12px' }}>
              今日新增: 156
            </Text>
          </Card>
        </Col>
        
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="目标指标"
              value={mockData.overview.completedTargets}
              prefix={<AimOutlined style={{ color: '#faad14' }} />}
              suffix="已完成"
            />
            <Progress 
              percent={68} 
              size="small" 
              showInfo={false}
              strokeColor="#faad14"
            />
            <Text type="secondary" style={{ fontSize: '12px' }}>
              本月目标: 345
            </Text>
          </Card>
        </Col>
        
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="运行模型"
              value={mockData.overview.runningModels}
              prefix={<AppstoreOutlined style={{ color: '#722ed1' }} />}
              suffix="个模型"
            />
            <Progress 
              percent={85} 
              size="small" 
              showInfo={false}
              strokeColor="#722ed1"
            />
            <Text type="secondary" style={{ fontSize: '12px' }}>
              健康状态: 良好
            </Text>
          </Card>
        </Col>
      </Row>

      {/* 系统状态概览 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} lg={8}>
          <Card 
            title={<Space><AlertOutlined />系统事件</Space>}
            extra={<Tag color="processing">实时监控</Tag>}
          >
            <Timeline>
              {mockData.events.map(event => (
                <Timeline.Item
                  key={event.id}
                  color={getStatusColor(event.type)}
                  dot={event.type === 'error' ? <ExclamationCircleOutlined /> : <CheckCircleOutlined />}
                >
                  <div>
                    <Text strong>{event.title}</Text>
                    <br />
                    <Text type="secondary" style={{ fontSize: '12px' }}>{event.time}</Text>
                  </div>
                </Timeline.Item>
              ))}
            </Timeline>
          </Card>
        </Col>
        
        <Col xs={24} lg={8}>
          <Card 
            title={<Space><MonitorOutlined />系统监控</Space>}
            extra={<Badge status="processing" text="运行中" />}
          >
            <Space direction="vertical" style={{ width: '100%' }}>
              <div>
                <Text>CPU使用率</Text>
                <Progress percent={45} size="small" strokeColor="#1890ff" />
              </div>
              <div>
                <Text>内存使用率</Text>
                <Progress percent={67} size="small" strokeColor="#52c41a" />
              </div>
              <div>
                <Text>磁盘使用率</Text>
                <Progress percent={23} size="small" strokeColor="#faad14" />
              </div>
              <div>
                <Text>网络流量</Text>
                <Progress percent={78} size="small" strokeColor="#722ed1" />
              </div>
            </Space>
          </Card>
        </Col>
        
        <Col xs={24} lg={8}>
          <Card 
            title={<Space><HistoryOutlined />最近活动</Space>}
            extra={<Text type="secondary">最近1小时</Text>}
          >
            <List
              size="small"
              dataSource={mockData.recentActions}
              renderItem={item => (
                <List.Item>
                  <List.Item.Meta
                    avatar={<Avatar icon={<UserOutlined />} size="small" />}
                    title={
                      <Space>
                        <Text style={{ fontSize: '13px' }}>{item.user}</Text>
                        <Tag color={getStatusColor(item.status)}>
                          {item.action}
                        </Tag>
                      </Space>
                    }
                    description={
                      <Text type="secondary" style={{ fontSize: '11px' }}>
                        {item.time}
                      </Text>
                    }
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>
      </Row>

      {/* 模型状态表格 */}
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Card 
            title={<Space><AppstoreOutlined />模型运行状态</Space>}
            extra={
              <Space>
                <Badge status="processing" text={`${mockData.overview.runningModels}个运行中`} />
                <Badge status="default" text="1个已停止" />
              </Space>
            }
          >
            <Table
              columns={columns}
              dataSource={mockData.modelStatus}
              pagination={false}
              size="small"
              rowKey="name"
            />
          </Card>
        </Col>
      </Row>

      
    </div>
  );
};

export default Dashboard;