import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Progress, Avatar, List, Badge, Tag, Table, Typography, Space, Button } from 'antd';
import { 
  UserOutlined, 
  TeamOutlined, 
  SettingOutlined, 
  AppstoreOutlined,
  SafetyOutlined,
  ClusterOutlined,
  EyeOutlined,
  HistoryOutlined,
  RiseOutlined,
  FallOutlined
} from '@ant-design/icons';
import { Line, Pie, Column } from '@ant-design/charts';

const { Title, Text } = Typography;

const PermissionDashboard = () => {
  // 模拟数据
  const [dashboardData, setDashboardData] = useState({
    totalUsers: 1248,
    activeUsers: 892,
    totalRoles: 28,
    totalPermissions: 156,
    totalApps: 12,
    onlineUsers: 324
  });

  // 用户趋势数据
  const userTrendData = [
    { name: '1月', 用户数: 856, 活跃用户: 642 },
    { name: '2月', 用户数: 923, 活跃用户: 698 },
    { name: '3月', 用户数: 1045, 活跃用户: 785 },
    { name: '4月', 用户数: 1156, 活跃用户: 834 },
    { name: '5月', 用户数: 1198, 活跃用户: 856 },
    { name: '6月', 用户数: 1248, 活跃用户: 892 }
  ];

  // 角色分布数据
  const roleDistributionData = [
    { name: '超级管理员', value: 5, color: '#ff4d4f' },
    { name: '系统管理员', value: 12, color: '#fa8c16' },
    { name: '部门管理员', value: 45, color: '#fadb14' },
    { name: '普通用户', value: 856, color: '#52c41a' },
    { name: '访客用户', value: 330, color: '#1890ff' }
  ];

  // 权限使用情况
  const permissionUsageData = [
    { name: '区域管理', 使用次数: 2340 },
    { name: '用户管理', 使用次数: 1890 },
    { name: '角色管理', 使用次数: 1560 },
    { name: '应用管理', 使用次数: 1234 },
    { name: '机构管理', 使用次数: 980 },
    { name: '岗位管理', 使用次数: 756 }
  ];

  // 最近登录用户
  const recentLoginUsers = [
    { id: 1, name: '张三', role: '系统管理员', loginTime: '2分钟前', status: 'online' },
    { id: 2, name: '李四', role: '部门管理员', loginTime: '5分钟前', status: 'online' },
    { id: 3, name: '王五', role: '普通用户', loginTime: '8分钟前', status: 'offline' },
    { id: 4, name: '赵六', role: '普通用户', loginTime: '12分钟前', status: 'online' },
    { id: 5, name: '钱七', role: '访客用户', loginTime: '15分钟前', status: 'offline' }
  ];

  // 系统日志
  const systemLogs = [
    { id: 1, action: '用户登录', user: '张三', time: '2024-06-22 14:30:25', type: 'success' },
    { id: 2, action: '权限修改', user: '李四', time: '2024-06-22 14:25:18', type: 'warning' },
    { id: 3, action: '角色创建', user: '王五', time: '2024-06-22 14:20:42', type: 'info' },
    { id: 4, action: '用户注销', user: '赵六', time: '2024-06-22 14:15:33', type: 'default' }
  ];

  const getStatusColor = (status: string) => {
    return status === 'online' ? '#52c41a' : '#d9d9d9';
  };

  const getLogTypeColor = (type: string) => {
    const colors: { [key: string]: string } = {
      success: 'green',
      warning: 'orange',
      info: 'blue',
      default: 'default'
    };
    return colors[type] || 'default';
  };

  return (
    <div style={{ padding: '24px', background: '#f0f2f5', minHeight: '100vh' }}>
      <div style={{ marginBottom: '24px' }}>
        <Title level={2}>权限管理系统概览</Title>
        <Text type="secondary">实时监控系统状态和用户活动</Text>
      </div>

      {/* 统计卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="总用户数"
              value={dashboardData.totalUsers}
              prefix={<UserOutlined style={{ color: '#1890ff' }} />}
              suffix={
                <div style={{ fontSize: '12px', color: '#52c41a' }}>
                  <RiseOutlined /> +12%
                </div>
              }
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="活跃用户"
              value={dashboardData.activeUsers}
              prefix={<TeamOutlined style={{ color: '#52c41a' }} />}
              suffix={
                <div style={{ fontSize: '12px', color: '#52c41a' }}>
                  <RiseOutlined /> +8%
                </div>
              }
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="在线用户"
              value={dashboardData.onlineUsers}
              prefix={<SafetyOutlined style={{ color: '#fa8c16' }} />}
              suffix={
                <div style={{ fontSize: '12px', color: '#fa8c16' }}>
                  <FallOutlined /> -3%
                </div>
              }
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="系统应用"
              value={dashboardData.totalApps}
              prefix={<AppstoreOutlined style={{ color: '#722ed1' }} />}
              suffix={
                <div style={{ fontSize: '12px', color: '#52c41a' }}>
                  <RiseOutlined /> +2
                </div>
              }
            />
          </Card>
        </Col>
      </Row>

      {/* 详细统计 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="角色总数"
              value={dashboardData.totalRoles}
              prefix={<ClusterOutlined />}
            />
            <Progress 
              percent={Math.round((dashboardData.totalRoles / 50) * 100)} 
              strokeColor="#52c41a"
              size="small"
              style={{ marginTop: '8px' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="权限总数"
              value={dashboardData.totalPermissions}
              prefix={<SettingOutlined />}
            />
            <Progress 
              percent={Math.round((dashboardData.totalPermissions / 200) * 100)} 
              strokeColor="#1890ff"
              size="small"
              style={{ marginTop: '8px' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="用户活跃度"
              value={Math.round((dashboardData.activeUsers / dashboardData.totalUsers) * 100)}
              suffix="%"
              prefix={<EyeOutlined />}
            />
            <Progress 
              percent={Math.round((dashboardData.activeUsers / dashboardData.totalUsers) * 100)} 
              strokeColor="#fa8c16"
              size="small"
              style={{ marginTop: '8px' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 图表区域 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} lg={16}>
          <Card title="用户增长趋势" extra={<Button size="small">详细报告</Button>}>
            <Line 
              data={[
                ...userTrendData.map(item => ({ ...item, type: '总用户数', value: item.用户数 })),
                ...userTrendData.map(item => ({ ...item, type: '活跃用户', value: item.活跃用户 }))
              ].map(item => ({
                name: item.name,
                type: item.type,
                value: item.value
              }))}
              height={300}
              xField="name"
              yField="value"
              seriesField="type"
              color={['#1890ff', '#52c41a']}
              smooth={true}
              lineStyle={{
                lineWidth: 2,
              }}
            />
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card title="角色分布" extra={<Button size="small">管理角色</Button>}>
            <Pie
              data={roleDistributionData}
              height={300}
              angleField="value"
              colorField="name"
              radius={0.8}
              innerRadius={0.4}
              label={{
                type: 'outer',
                content: '{name}: {percentage}',
              }}
              interactions={[{ type: 'element-active' }]}
            />
          </Card>
        </Col>
      </Row>

      {/* 权限使用统计 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24}>
          <Card title="权限模块使用统计" extra={<Button size="small">查看详情</Button>}>
            <Column
              data={permissionUsageData}
              height={250}
              xField="name"
              yField="使用次数"
              color="#1890ff"
              columnStyle={{
                radius: [4, 4, 0, 0],
              }}
            />
          </Card>
        </Col>
      </Row>

      {/* 活动信息 */}
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card 
            title="最近登录用户" 
            extra={<Button size="small" icon={<EyeOutlined />}>查看全部</Button>}
          >
            <List
              itemLayout="horizontal"
              dataSource={recentLoginUsers}
              renderItem={item => (
                <List.Item>
                  <List.Item.Meta
                    avatar={
                      <Badge 
                        dot 
                        color={getStatusColor(item.status)}
                        offset={[-2, 2]}
                      >
                        <Avatar icon={<UserOutlined />} />
                      </Badge>
                    }
                    title={
                      <Space>
                        {item.name}
                        <Tag color="blue">{item.role}</Tag>
                      </Space>
                    }
                    description={item.loginTime}
                  />
                </List.Item>
              )}
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card 
            title="系统操作日志" 
            extra={<Button size="small" icon={<HistoryOutlined />}>查看全部</Button>}
          >
            <List
              itemLayout="horizontal"
              dataSource={systemLogs}
              renderItem={item => (
                <List.Item>
                  <List.Item.Meta
                    title={
                      <Space>
                        <Tag color={getLogTypeColor(item.type)}>{item.action}</Tag>
                        <Text>{item.user}</Text>
                      </Space>
                    }
                    description={
                      <Text type="secondary" style={{ fontSize: '12px' }}>
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
    </div>
  );
};

export default PermissionDashboard;