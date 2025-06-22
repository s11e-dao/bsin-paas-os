import React, { useState, useEffect } from 'react';
import { 
  Card, 
  Row, 
  Col, 
  Statistic, 
  Button, 
  Table, 
  Progress, 
  Badge, 
  Tabs, 
  Space,
  Typography,
  Tag,
  Avatar,
  Steps,
  Grid
} from 'antd';
import {
  UserOutlined,
  DollarOutlined,
  TrophyOutlined,
  ShoppingOutlined,
  BarChartOutlined,
  RiseOutlined,
  SyncOutlined,
  FireOutlined,
  StarOutlined,
  ThunderboltOutlined
} from '@ant-design/icons';
import { Line, Radar, Column } from '@ant-design/plots';

const { Title, Text, Paragraph } = Typography;
const { TabPane } = Tabs;
const { Step } = Steps;
const { useBreakpoint } = Grid;

// 定义表格数据类型
interface TableDataType {
  key: string;
  assetType: string;
  tokenSymbol: string;
  dataAmount: string;
  marketValue: string;
  holding: string;
  status: '已代币化' | '交易中' | '高价值';
}

const DIDRWADashboard = () => {
  
  const [activeTab, setActiveTab] = useState('dashboard');
  const [loading, setLoading] = useState(false);
  const screens = useBreakpoint();

  // 模拟数据刷新
  const refreshData = () => {
    setLoading(true);
    setTimeout(() => setLoading(false), 2000);
  };

  // GMV与RWA趋势数据
  const gmvTrendData = [
    { month: '1月', GMV: 1200, RWA价值: 800 },
    { month: '2月', GMV: 1500, RWA价值: 1100 },
    { month: '3月', GMV: 1800, RWA价值: 1400 },
    { month: '4月', GMV: 2100, RWA价值: 1700 },
    { month: '5月', GMV: 2400, RWA价值: 2000 },
    { month: '6月', GMV: 2400, RWA价值: 2200 }
  ];

  // 雷达图数据
  const radarData = [
    { dimension: '数据质量', value: 85 },
    { dimension: '活跃度', value: 78 },
    { dimension: '贡献价值', value: 92 },
    { dimension: '交易频次', value: 67 },
    { dimension: '社区影响', value: 73 },
    { dimension: '创新指数', value: 88 }
  ];

  // GMV趋势图配置
  const gmvConfig = {
    data: gmvTrendData,
    xField: 'month',
    yField: 'value',
    seriesField: 'type',
    smooth: true,
    animation: {
      appear: {
        animation: 'path-in',
        duration: 2000,
      },
    },
    color: ['#1890ff', '#722ed1'],
    point: {
      size: 5,
      shape: 'diamond',
    },
    tooltip: {
      showMarkers: true,
    },
    state: {
      active: {
        style: {
          shadowBlur: 4,
          stroke: '#000',
          fill: 'red',
        },
      },
    },
    interactions: [{ type: 'marker-active' }],
  };

  // 处理趋势图数据
  const lineData = gmvTrendData.flatMap(item => [
    { month: item.month, value: item.GMV, type: 'GMV' },
    { month: item.month, value: item['RWA价值'], type: 'RWA价值' }
  ]);

  const lineConfig = {
    data: lineData,
    xField: 'month',
    yField: 'value',
    seriesField: 'type',
    smooth: true,
    color: ['#1890ff', '#722ed1'],
    point: {
      size: 5,
      shape: 'diamond',
      style: {
        fill: 'white',
        stroke: '#1890ff',
        lineWidth: 2,
      },
    },
    tooltip: {
      showMarkers: true,
    },
  };

  // 雷达图配置
  const radarConfig = {
    data: radarData,
    xField: 'dimension',
    yField: 'value',
    meta: {
      value: {
        alias: '分数',
        min: 0,
        max: 100,
      },
    },
    xAxis: {
      line: null,
      tickLine: null,
      grid: {
        line: {
          style: {
            lineDash: null,
          },
        },
      },
    },
    yAxis: {
      line: null,
      tickLine: null,
      grid: {
        line: {
          type: 'line',
          style: {
            lineDash: null,
          },
        },
        alternateColor: 'rgba(24, 144, 255, 0.04)',
      },
    },
    point: {
      size: 2,
    },
    area: {
      style: {
        fill: '#1890ff',
        fillOpacity: 0.25,
      },
    },
  };

  // 数据表格列配置
  const columns = [
    {
      title: '资产类型',
      dataIndex: 'assetType',
      key: 'assetType',
      width: '25%',
      ellipsis: true,
      render: (text: string) => <Text strong style={{ color: '#262626' }}>{text}</Text>
    },
    {
      title: '代币符号',
      dataIndex: 'tokenSymbol',
      key: 'tokenSymbol',
      width: '15%',
      render: (text: string) => <Tag color="blue">{text}</Tag>
    },
    {
      title: '数据量',
      dataIndex: 'dataAmount',
      key: 'dataAmount',
      width: '12%',
    },
    {
      title: '市场价值',
      dataIndex: 'marketValue',
      key: 'marketValue',
      width: '15%',
      render: (value: string) => <Text strong style={{ color: '#1890ff' }}>{value}</Text>
    },
    {
      title: '您的持有',
      dataIndex: 'holding',
      key: 'holding',
      width: '15%',
      render: (value: string) => <Text strong style={{ color: '#52c41a' }}>{value}</Text>
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: '18%',
      render: (status: '已代币化' | '交易中' | '高价值') => {
        const statusConfig = {
          '已代币化': { color: 'success' as const, icon: <ThunderboltOutlined /> },
          '交易中': { color: 'processing' as const, icon: <SyncOutlined /> },
          '高价值': { color: 'warning' as const, icon: <StarOutlined /> }
        };
        const config = statusConfig[status];
        return <Badge status={config.color} text={status} />;
      }
    }
  ];

  // 表格数据
  const tableData: TableDataType[] = [
    {
      key: '1',
      assetType: '🛒 消费行为数据',
      tokenSymbol: 'SHOP-TOKEN',
      dataAmount: '15.2K 条',
      marketValue: '$4,567',
      holding: '127 tokens',
      status: '已代币化'
    },
    {
      key: '2',
      assetType: '🎯 偏好标签数据',
      tokenSymbol: 'PREF-TOKEN',
      dataAmount: '8.7K 条',
      marketValue: '$2,341',
      holding: '89 tokens',
      status: '交易中'
    },
    {
      key: '3',
      assetType: '📱 行为轨迹数据',
      tokenSymbol: 'TRACK-TOKEN',
      dataAmount: '23.6K 条',
      marketValue: '$6,789',
      holding: '234 tokens',
      status: '高价值'
    },
    {
      key: '4',
      assetType: '🔍 搜索意图数据',
      tokenSymbol: 'INTENT-TOKEN',
      dataAmount: '12.3K 条',
      marketValue: '$3,456',
      holding: '156 tokens',
      status: '已代币化'
    }
  ];

  // RWA代币数据
  const rwaTokens = [
    { symbol: 'DATA-01', value: '$1,247', change: '+12.5%', positive: true },
    { symbol: 'BEHAV-02', value: '$892', change: '+8.7%', positive: true },
    { symbol: 'PREF-03', value: '$654', change: '-2.1%', positive: false }
  ];

  const renderDashboard = () => (
    <div style={{ padding: '0 16px' }}>
      {/* 第一行：核心统计卡片 */}
      <Row gutter={[24, 24]} style={{ marginBottom: 32 }}>
        {/* DID身份概览 */}
        <Col xs={24} sm={24} md={8} lg={8} xl={8}>
          <Card 
            title={
              <div style={{ display: 'flex', alignItems: 'center', fontSize: '16px', fontWeight: 600, color: '#1890ff' }}>
                <div style={{
                  width: '32px',
                  height: '32px',
                  borderRadius: '8px',
                  background: 'linear-gradient(135deg, #1890ff, #40a9ff)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginRight: '12px',
                  boxShadow: '0 4px 12px rgba(24, 144, 255, 0.3)'
                }}>
                  <UserOutlined style={{ color: 'white', fontSize: '16px' }} />
                </div>
                DID身份概览
              </div>
            }
            style={{ 
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(240, 247, 255, 0.8))',
              backdropFilter: 'blur(20px)',
              borderRadius: '20px',
              border: '1px solid rgba(24, 144, 255, 0.1)',
              boxShadow: '0 12px 40px rgba(24, 144, 255, 0.08)',
              height: '340px',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              cursor: 'pointer'
            }}
            bodyStyle={{ height: '280px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '20px' }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-4px)';
              e.currentTarget.style.boxShadow = '0 16px 48px rgba(24, 144, 255, 0.12)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.boxShadow = '0 12px 40px rgba(24, 144, 255, 0.08)';
            }}
          >
            <div>
              <Row gutter={20} style={{ marginBottom: 24 }}>
                <Col span={12}>
                  <div style={{
                    textAlign: 'center',
                    padding: '16px',
                    background: 'linear-gradient(135deg, rgba(24, 144, 255, 0.05), rgba(24, 144, 255, 0.02))',
                    borderRadius: '12px',
                    border: '1px solid rgba(24, 144, 255, 0.1)'
                  }}>
                    <div style={{ color: '#8c8c8c', fontSize: '11px', fontWeight: 500, marginBottom: '8px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                      活跃用户
                    </div>
                    <div style={{ 
                      color: '#1890ff', 
                      fontSize: '2em', 
                      fontWeight: 800, 
                      lineHeight: 1,
                      background: 'linear-gradient(135deg, #1890ff, #40a9ff)',
                      WebkitBackgroundClip: 'text',
                      WebkitTextFillColor: 'transparent',
                      backgroundClip: 'text'
                    }}>
                      28.5K
                    </div>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{
                    textAlign: 'center',
                    padding: '16px',
                    background: 'linear-gradient(135deg, rgba(24, 144, 255, 0.05), rgba(24, 144, 255, 0.02))',
                    borderRadius: '12px',
                    border: '1px solid rgba(24, 144, 255, 0.1)'
                  }}>
                    <div style={{ color: '#8c8c8c', fontSize: '11px', fontWeight: 500, marginBottom: '8px', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                      总GMV
                    </div>
                    <div style={{ 
                      color: '#1890ff', 
                      fontSize: '2em', 
                      fontWeight: 800, 
                      lineHeight: 1,
                      background: 'linear-gradient(135deg, #1890ff, #40a9ff)',
                      WebkitBackgroundClip: 'text',
                      WebkitTextFillColor: 'transparent',
                      backgroundClip: 'text'
                    }}>
                      $2.4M
                    </div>
                  </div>
                </Col>
              </Row>
              
              <div style={{ marginBottom: 16 }}>
                <div style={{ marginBottom: 8 }}>
                  <Text style={{ color: '#595959', fontSize: '12px', display: 'block', marginBottom: 4 }}>
                    <Text strong>当前身份:</Text>
                  </Text>
                  <Text style={{ 
                    color: '#1890ff', 
                    fontSize: '11px', 
                    display: 'block',
                    wordBreak: 'break-all',
                    lineHeight: '1.2'
                  }}>
                    did:web:premium-user.rwa.com
                  </Text>
                </div>
                <Text style={{ color: '#595959', fontSize: '12px', display: 'block', marginBottom: 8 }}>
                  <Text strong>资产等级:</Text> <Tag color="purple">钻石级</Tag>
                </Text>
                <Text style={{ color: '#595959', fontSize: '12px', display: 'block', marginBottom: 12 }}>
                  <Text strong>数据资产价值:</Text>
                </Text>
                <Progress 
                  percent={85} 
                  strokeColor={{
                    '0%': '#1890ff',
                    '100%': '#722ed1',
                  }}
                  format={() => '85%'}
                  size="small"
                  style={{ marginBottom: 8 }}
                />
              </div>
            </div>
            
            <div style={{ textAlign: 'center', paddingTop: 8, borderTop: '1px solid rgba(0, 0, 0, 0.06)', overflow: 'hidden' }}>
              <Text style={{ 
                color: '#1890ff', 
                fontWeight: 'bold', 
                fontSize: '13px',
                display: 'block',
                whiteSpace: 'nowrap',
                overflow: 'hidden',
                textOverflow: 'ellipsis'
              }}>月度收益: $3,247</Text>
            </div>
          </Card>
        </Col>

        {/* RWA资产代币 */}
        <Col xs={24} sm={24} md={8} lg={8} xl={8}>
          <Card 
            title={
              <div style={{ display: 'flex', alignItems: 'center', fontSize: '16px', fontWeight: 600, color: '#722ed1' }}>
                <div style={{
                  width: '32px',
                  height: '32px',
                  borderRadius: '8px',
                  background: 'linear-gradient(135deg, #722ed1, #9254de)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginRight: '12px',
                  boxShadow: '0 4px 12px rgba(114, 46, 209, 0.3)'
                }}>
                  <FireOutlined style={{ color: 'white', fontSize: '16px' }} />
                </div>
                RWA资产代币
              </div>
            }
            style={{ 
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(248, 243, 255, 0.8))',
              backdropFilter: 'blur(20px)',
              borderRadius: '20px',
              border: '1px solid rgba(114, 46, 209, 0.1)',
              boxShadow: '0 12px 40px rgba(114, 46, 209, 0.08)',
              height: '340px',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              cursor: 'pointer'
            }}
            bodyStyle={{ height: '280px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '20px' }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-4px)';
              e.currentTarget.style.boxShadow = '0 16px 48px rgba(114, 46, 209, 0.12)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.boxShadow = '0 12px 40px rgba(114, 46, 209, 0.08)';
            }}
          >
            <div>
              <Space direction="vertical" style={{ width: '100%' }} size="small">
                {rwaTokens.map((token, index) => (
                  <Card 
                    key={index}
                    size="small"
                    style={{ 
                      background: 'linear-gradient(135deg, rgba(24, 144, 255, 0.05), rgba(114, 46, 209, 0.05))',
                      border: '1px solid rgba(24, 144, 255, 0.15)',
                      borderRadius: '8px',
                      boxShadow: '0 2px 8px rgba(0, 0, 0, 0.04)',
                      marginBottom: '8px'
                    }}
                    bodyStyle={{ padding: '8px 12px' }}
                  >
                    <Row justify="space-between" align="middle">
                      <Col flex="auto">
                        <Text strong style={{ color: '#722ed1', fontSize: '13px', display: 'block' }}>{token.symbol}</Text>
                        <Text strong style={{ color: '#1890ff', fontSize: '15px' }}>{token.value}</Text>
                      </Col>
                      <Col flex="none">
                        <Tag color={token.positive ? 'success' : 'error'}>
                          {token.change}
                        </Tag>
                      </Col>
                    </Row>
                  </Card>
                ))}
              </Space>
            </div>
            
            <div style={{ textAlign: 'center', paddingTop: 8, borderTop: '1px solid rgba(0, 0, 0, 0.06)', overflow: 'hidden' }}>
              <Text strong style={{ 
                color: '#1890ff', 
                fontSize: '14px',
                display: 'block',
                whiteSpace: 'nowrap',
                overflow: 'hidden',
                textOverflow: 'ellipsis'
              }}>
                资产组合总值: $2,793
              </Text>
            </div>
          </Card>
        </Col>

        {/* 市场实时数据 */}
        <Col xs={24} sm={24} md={8} lg={8} xl={8}>
          <Card 
            title={
              <div style={{ display: 'flex', alignItems: 'center', fontSize: '16px', fontWeight: 600, color: '#52c41a' }}>
                <div style={{
                  width: '32px',
                  height: '32px',
                  borderRadius: '8px',
                  background: 'linear-gradient(135deg, #52c41a, #73d13d)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  marginRight: '12px',
                  boxShadow: '0 4px 12px rgba(82, 196, 26, 0.3)'
                }}>
                  <BarChartOutlined style={{ color: 'white', fontSize: '16px' }} />
                </div>
                市场实时数据
              </div>
            }
            style={{ 
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(246, 255, 237, 0.8))',
              backdropFilter: 'blur(20px)',
              borderRadius: '20px',
              border: '1px solid rgba(82, 196, 26, 0.1)',
              boxShadow: '0 12px 40px rgba(82, 196, 26, 0.08)',
              height: '340px',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              cursor: 'pointer'
            }}
            bodyStyle={{ height: '280px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '20px' }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-4px)';
              e.currentTarget.style.boxShadow = '0 16px 48px rgba(82, 196, 26, 0.12)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.boxShadow = '0 12px 40px rgba(82, 196, 26, 0.08)';
            }}
          >
            <div>
              <Row gutter={[12, 12]}>
                <Col span={12}>
                  <Statistic
                    title={<span style={{ color: '#595959', fontSize: '11px' }}>日交易量</span>}
                    value="$847K"
                    valueStyle={{ color: '#1890ff', fontWeight: 'bold', fontSize: '1.3em' }}
                  />
                </Col>
                <Col span={12}>
                  <Statistic
                    title={<span style={{ color: '#595959', fontSize: '11px' }}>活跃交易对</span>}
                    value={156}
                    valueStyle={{ color: '#1890ff', fontWeight: 'bold', fontSize: '1.3em' }}
                  />
                </Col>
                <Col span={12}>
                  <Statistic
                    title={<span style={{ color: '#595959', fontSize: '11px' }}>周涨幅</span>}
                    value="+23.4%"
                    valueStyle={{ color: '#52c41a', fontWeight: 'bold', fontSize: '1.3em' }}
                    prefix={<RiseOutlined />}
                  />
                </Col>
                <Col span={12}>
                  <Statistic
                    title={<span style={{ color: '#595959', fontSize: '11px' }}>系统稳定性</span>}
                    value="94.7%"
                    valueStyle={{ color: '#1890ff', fontWeight: 'bold', fontSize: '1.3em' }}
                  />
                </Col>
              </Row>
            </div>
            
            <div style={{ textAlign: 'center', paddingTop: 8, borderTop: '1px solid rgba(0, 0, 0, 0.06)' }}>
              <Text style={{ color: '#595959', fontSize: '12px' }}>
                实时更新 • 延迟 &lt; 100ms
              </Text>
            </div>
          </Card>
        </Col>
      </Row>

      {/* 第二行：图表区域 */}
      <Row gutter={[24, 24]} style={{ marginBottom: 32 }}>
        {/* GMV与RWA趋势图 */}
        <Col xs={24} sm={24} md={12} lg={12} xl={12}>
          <Card 
            title={
              <div style={{ display: 'flex', alignItems: 'center', fontSize: '16px', fontWeight: 600, color: '#1890ff' }}>
                <span style={{ fontSize: '18px', marginRight: '8px' }}>📈</span>
                GMV与RWA资产趋势
              </div>
            }
            style={{ 
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(240, 247, 255, 0.8))',
              backdropFilter: 'blur(20px)',
              borderRadius: '20px',
              border: '1px solid rgba(24, 144, 255, 0.1)',
              boxShadow: '0 12px 40px rgba(24, 144, 255, 0.06)',
              height: '420px',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              cursor: 'pointer'
            }}
            bodyStyle={{ height: '360px', padding: '16px' }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-2px)';
              e.currentTarget.style.boxShadow = '0 16px 48px rgba(24, 144, 255, 0.1)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.boxShadow = '0 12px 40px rgba(24, 144, 255, 0.06)';
            }}
          >
            <div style={{ 
              height: '100%', 
              width: '100%', 
              overflow: 'hidden',
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.1))',
              borderRadius: '12px',
              padding: '12px'
            }}>
              <Line {...lineConfig} />
            </div>
          </Card>
        </Col>

        {/* 多维度贡献分析 */}
        <Col xs={24} sm={24} md={12} lg={12} xl={12}>
          <Card 
            title={
              <div style={{ display: 'flex', alignItems: 'center', fontSize: '16px', fontWeight: 600, color: '#722ed1' }}>
                <span style={{ fontSize: '18px', marginRight: '8px' }}>🎯</span>
                多维度贡献分析
              </div>
            }
            style={{ 
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(248, 243, 255, 0.8))',
              backdropFilter: 'blur(20px)',
              borderRadius: '20px',
              border: '1px solid rgba(114, 46, 209, 0.1)',
              boxShadow: '0 12px 40px rgba(114, 46, 209, 0.06)',
              height: '420px',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              cursor: 'pointer'
            }}
            bodyStyle={{ height: '360px', padding: '16px' }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-2px)';
              e.currentTarget.style.boxShadow = '0 16px 48px rgba(114, 46, 209, 0.1)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.boxShadow = '0 12px 40px rgba(114, 46, 209, 0.06)';
            }}
          >
            <div style={{ 
              height: '100%', 
              width: '100%', 
              overflow: 'hidden',
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.1))',
              borderRadius: '12px',
              padding: '12px'
            }}>
              <Radar {...radarConfig} />
            </div>
          </Card>
        </Col>
      </Row>

      {/* 第三行：表格和流程 */}
      <Row gutter={[24, 24]} style={{ marginBottom: 32 }}>
        {/* 数据资产映射表 */}
        <Col xs={24} sm={24} md={16} lg={16} xl={16}>
          <Card 
            title={<span style={{ color: '#262626' }}>🗺️ 数据资产映射表</span>}
            style={{ 
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(240, 242, 245, 0.8))',
              backdropFilter: 'blur(10px)',
              borderRadius: '16px',
              border: '1px solid rgba(24, 144, 255, 0.1)',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)',
              height: '480px'
            }}
            bodyStyle={{ height: '420px', padding: '12px' }}
          >
            <div style={{ height: '100%', overflow: 'hidden' }}>
              <Table 
                columns={columns} 
                dataSource={tableData} 
                pagination={false}
                size="small"
                scroll={{ x: 'max-content', y: 350 }}
                style={{ 
                  background: 'transparent'
                }}
              />
            </div>
          </Card>
        </Col>

        {/* 代币化流程 */}
        <Col xs={24} sm={24} md={8} lg={8} xl={8}>
          <Card 
            title={<span style={{ color: '#1890ff', fontWeight: 'bold' }}>⚡ 资产代币化流程</span>}
            style={{ 
              background: 'linear-gradient(135deg, #f8fafc 0%, #ffffff 100%)',
              borderRadius: '16px',
              border: '1px solid rgba(230, 237, 243, 0.8)',
              boxShadow: '0 4px 20px rgba(0, 0, 0, 0.06)',
              height: '480px'
            }}
            bodyStyle={{ height: '420px', padding: '16px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}
          >
            <div>
              {/* 自定义流程步骤 */}
              <div style={{ marginBottom: 24 }}>
                <div style={{ 
                  display: 'flex', 
                  justifyContent: 'space-between', 
                  alignItems: 'center',
                  position: 'relative',
                  padding: '0 10px'
                }}>
                  {/* 连接线 */}
                  <div style={{
                    position: 'absolute',
                    top: '20px',
                    left: '50px',
                    right: '50px',
                    height: '2px',
                    background: 'linear-gradient(90deg, #52c41a 0%, #52c41a 33%, #1890ff 33%, #1890ff 66%, #d9d9d9 66%, #d9d9d9 100%)',
                    zIndex: 1
                  }} />
                  
                  {/* 步骤1 - 数据收集 */}
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', zIndex: 2 }}>
                    <div style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      background: 'linear-gradient(135deg, #52c41a, #73d13d)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      boxShadow: '0 4px 12px rgba(82, 196, 26, 0.3)',
                      marginBottom: '8px'
                    }}>
                      <BarChartOutlined style={{ color: 'white', fontSize: '16px' }} />
                    </div>
                    <Text style={{ fontSize: '9px', color: '#262626', textAlign: 'center', lineHeight: '1.2' }}>
                      数据收集
                    </Text>
                  </div>

                  {/* 步骤2 - 价值评估 */}
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', zIndex: 2 }}>
                    <div style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      background: 'linear-gradient(135deg, #52c41a, #73d13d)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      boxShadow: '0 4px 12px rgba(82, 196, 26, 0.3)',
                      marginBottom: '8px'
                    }}>
                      <TrophyOutlined style={{ color: 'white', fontSize: '16px' }} />
                    </div>
                    <Text style={{ fontSize: '9px', color: '#262626', textAlign: 'center', lineHeight: '1.2' }}>
                      价值评估
                    </Text>
                  </div>

                  {/* 步骤3 - 代币铸造 (当前步骤) */}
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', zIndex: 2 }}>
                    <div style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      background: 'linear-gradient(135deg, #1890ff, #40a9ff)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      boxShadow: '0 4px 12px rgba(24, 144, 255, 0.4)',
                      marginBottom: '8px',
                      border: '2px solid #ffffff'
                    }}>
                      <FireOutlined style={{ color: 'white', fontSize: '16px' }} />
                    </div>
                    <Text style={{ fontSize: '9px', color: '#1890ff', textAlign: 'center', lineHeight: '1.2', fontWeight: 'bold' }}>
                      代币铸造
                    </Text>
                  </div>

                  {/* 步骤4 - 市场交易 */}
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', zIndex: 2 }}>
                    <div style={{
                      width: '40px',
                      height: '40px',
                      borderRadius: '50%',
                      background: 'linear-gradient(135deg, #f0f0f0, #e6e6e6)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
                      marginBottom: '8px'
                    }}>
                      <DollarOutlined style={{ color: '#999999', fontSize: '16px' }} />
                    </div>
                    <Text style={{ fontSize: '9px', color: '#999999', textAlign: 'center', lineHeight: '1.2' }}>
                      市场交易
                    </Text>
                  </div>
                </div>
              </div>
            </div>
            
            <div>
              <Title level={5} style={{ color: '#1890ff', marginBottom: 12, fontSize: '13px' }}>代币化进度</Title>
              <Space direction="vertical" style={{ width: '100%' }} size="small">
                <div>
                  <div style={{ 
                    display: 'flex', 
                    justifyContent: 'space-between', 
                    alignItems: 'center', 
                    marginBottom: 4,
                    overflow: 'hidden'
                  }}>
                    <Text style={{ 
                      color: '#595959', 
                      fontSize: '10px',
                      flex: 1,
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap'
                    }}>购物数据包</Text>
                    <Text style={{ 
                      color: '#52c41a', 
                      fontSize: '10px', 
                      fontWeight: 'bold',
                      marginLeft: '8px',
                      flexShrink: 0
                    }}>100%</Text>
                  </div>
                  <Progress 
                    percent={100} 
                    strokeColor={{
                      '0%': '#52c41a',
                      '100%': '#73d13d'
                    }}
                    trailColor="#f0f0f0"
                    size="small" 
                    showInfo={false} 
                  />
                </div>
                <div>
                  <div style={{ 
                    display: 'flex', 
                    justifyContent: 'space-between', 
                    alignItems: 'center', 
                    marginBottom: 4,
                    overflow: 'hidden'
                  }}>
                    <Text style={{ 
                      color: '#595959', 
                      fontSize: '10px',
                      flex: 1,
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap'
                    }}>行为数据包</Text>
                    <Text style={{ 
                      color: '#1890ff', 
                      fontSize: '10px', 
                      fontWeight: 'bold',
                      marginLeft: '8px',
                      flexShrink: 0
                    }}>75%</Text>
                  </div>
                  <Progress 
                    percent={75} 
                    strokeColor={{
                      '0%': '#1890ff',
                      '100%': '#40a9ff'
                    }}
                    trailColor="#f0f0f0"
                    size="small" 
                    showInfo={false} 
                  />
                </div>
                <div>
                  <div style={{ 
                    display: 'flex', 
                    justifyContent: 'space-between', 
                    alignItems: 'center', 
                    marginBottom: 4,
                    overflow: 'hidden'
                  }}>
                    <Text style={{ 
                      color: '#595959', 
                      fontSize: '10px',
                      flex: 1,
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap'
                    }}>偏好数据包</Text>
                    <Text style={{ 
                      color: '#faad14', 
                      fontSize: '10px', 
                      fontWeight: 'bold',
                      marginLeft: '8px',
                      flexShrink: 0
                    }}>45%</Text>
                  </div>
                  <Progress 
                    percent={45} 
                    strokeColor={{
                      '0%': '#faad14',
                      '100%': '#ffc53d'
                    }}
                    trailColor="#f0f0f0"
                    size="small" 
                    showInfo={false} 
                  />
                </div>
              </Space>
            </div>
          </Card>
        </Col>
      </Row>

      {/* 第四行：数据交易所产品 */}
      <Row gutter={[24, 24]}>
        <Col xs={24}>
          <Card 
            title={<span style={{ color: '#262626' }}>🏪 RWA数据交易所产品</span>}
            style={{ 
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(240, 242, 245, 0.8))',
              backdropFilter: 'blur(10px)',
              borderRadius: '16px',
              border: '1px solid rgba(24, 144, 255, 0.1)',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.08)'
            }}
            bodyStyle={{ padding: '16px' }}
          >
            <Row gutter={[16, 16]}>
              <Col xs={24} sm={24} md={12} lg={12} xl={12}>
                <Card 
                  size="small"
                  title={<span style={{ color: '#262626', fontSize: '15px' }}>🛍️ 智能消费数据包</span>}
                  style={{ 
                    background: 'linear-gradient(135deg, rgba(24, 144, 255, 0.02), rgba(114, 46, 209, 0.02))',
                    border: '1px solid rgba(24, 144, 255, 0.15)',
                    borderRadius: '12px',
                    boxShadow: '0 4px 16px rgba(0, 0, 0, 0.04)',
                    height: '260px'
                  }}
                  bodyStyle={{ height: '200px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '12px' }}
                >
                  <div>
                    <div style={{ marginBottom: 12 }}>
                      <Text style={{ color: '#595959', fontSize: '11px', display: 'block', marginBottom: 3 }}>
                        <Text strong>RWA代币:</Text> SMART-CONSUME (SC)
                      </Text>
                      <Text style={{ color: '#595959', fontSize: '11px', display: 'block', marginBottom: 3 }}>
                        <Text strong>基础资产:</Text> 消费行为 + AI预测模型
                      </Text>
                      <Text style={{ color: '#595959', fontSize: '11px', display: 'block' }}>
                        <Text strong>代币供应量:</Text> 10,000 SC
                      </Text>
                    </div>
                  </div>
                  
                  <Row gutter={[8, 8]}>
                    <Col span={12}>
                      <Statistic
                        title={<span style={{ color: '#595959', fontSize: '10px' }}>单价</span>}
                        value="$12.47"
                        valueStyle={{ color: '#1890ff', fontWeight: 'bold', fontSize: '1.1em' }}
                      />
                    </Col>
                    <Col span={12}>
                      <Statistic
                        title={<span style={{ color: '#595959', fontSize: '10px' }}>年化收益</span>}
                        value="15.7%"
                        valueStyle={{ color: '#52c41a', fontWeight: 'bold', fontSize: '1.1em' }}
                      />
                    </Col>
                    <Col span={12}>
                      <Statistic
                        title={<span style={{ color: '#595959', fontSize: '10px' }}>您的持有</span>}
                        value={234}
                        valueStyle={{ color: '#1890ff', fontWeight: 'bold', fontSize: '1.1em' }}
                      />
                    </Col>
                    <Col span={12}>
                      <Statistic
                        title={<span style={{ color: '#595959', fontSize: '10px' }}>持仓价值</span>}
                        value="$2,918"
                        valueStyle={{ color: '#722ed1', fontWeight: 'bold', fontSize: '1.1em' }}
                      />
                    </Col>
                  </Row>
                </Card>
              </Col>
              
              <Col xs={24} sm={24} md={12} lg={12} xl={12}>
                <Card 
                  size="small"
                  title={<span style={{ color: '#262626', fontSize: '15px' }}>🎯 精准营销数据包</span>}
                  style={{ 
                    background: 'linear-gradient(135deg, rgba(24, 144, 255, 0.02), rgba(114, 46, 209, 0.02))',
                    border: '1px solid rgba(24, 144, 255, 0.15)',
                    borderRadius: '12px',
                    boxShadow: '0 4px 16px rgba(0, 0, 0, 0.04)',
                    height: '260px'
                  }}
                  bodyStyle={{ height: '200px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '12px' }}
                >
                  <div>
                    <div style={{ marginBottom: 12 }}>
                      <Text style={{ color: '#595959', fontSize: '11px', display: 'block', marginBottom: 3 }}>
                        <Text strong>RWA代币:</Text> TARGET-MARKET (TM)
                      </Text>
                      <Text style={{ color: '#595959', fontSize: '11px', display: 'block', marginBottom: 3 }}>
                        <Text strong>基础资产:</Text> 用户画像 + 广告效果数据
                      </Text>
                      <Text style={{ color: '#595959', fontSize: '11px', display: 'block' }}>
                        <Text strong>代币供应量:</Text> 7,500 TM
                      </Text>
                    </div>
                  </div>
                  
                  <Row gutter={[8, 8]}>
                    <Col span={12}>
                      <Statistic
                        title={<span style={{ color: '#595959', fontSize: '10px' }}>单价</span>}
                        value="$18.92"
                        valueStyle={{ color: '#1890ff', fontWeight: 'bold', fontSize: '1.1em' }}
                      />
                    </Col>
                    <Col span={12}>
                      <Statistic
                        title={<span style={{ color: '#595959', fontSize: '10px' }}>年化收益</span>}
                        value="22.3%"
                        valueStyle={{ color: '#52c41a', fontWeight: 'bold', fontSize: '1.1em' }}
                      />
                    </Col>
                    <Col span={12}>
                      <Statistic
                        title={<span style={{ color: '#595959', fontSize: '10px' }}>您的持有</span>}
                        value={156}
                        valueStyle={{ color: '#1890ff', fontWeight: 'bold', fontSize: '1.1em' }}
                      />
                    </Col>
                    <Col span={12}>
                      <Statistic
                        title={<span style={{ color: '#595959', fontSize: '10px' }}>持仓价值</span>}
                        value="$2,951"
                        valueStyle={{ color: '#722ed1', fontWeight: 'bold', fontSize: '1.1em' }}
                      />
                    </Col>
                  </Row>
                </Card>
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  );

  return (
    <div style={{ 
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #f8fafc 0%, #e3f2fd 25%, #f3e5f5 50%, #fff3e0 75%, #f8fafc 100%)',
      padding: '20px 0',
      overflow: 'hidden',
      position: 'relative'
    }}>
      {/* 背景装饰 */}
      <div style={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        background: `
          radial-gradient(circle at 20% 20%, rgba(24, 144, 255, 0.03) 0%, transparent 50%),
          radial-gradient(circle at 80% 80%, rgba(114, 46, 209, 0.03) 0%, transparent 50%),
          radial-gradient(circle at 40% 60%, rgba(82, 196, 26, 0.02) 0%, transparent 50%)
        `,
        pointerEvents: 'none',
        zIndex: 0
      }} />
      
      <div style={{ maxWidth: '100%', width: '100%', margin: '0 auto', padding: '0 16px', position: 'relative', zIndex: 1 }}>
        {/* 头部 */}
        <div style={{ 
          textAlign: 'center', 
          marginBottom: 40, 
          padding: '20px 12px',
          background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.1))',
          backdropFilter: 'blur(20px)',
          borderRadius: '24px',
          border: '1px solid rgba(255, 255, 255, 0.3)',
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.04)'
        }}>
          <Title 
            level={1} 
            style={{ 
              background: 'linear-gradient(135deg, #1890ff 0%, #722ed1 30%, #eb2f96 60%, #13c2c2 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
              fontSize: screens.xs ? '2em' : screens.sm ? '2.5em' : '3.2em',
              fontWeight: 800,
              marginBottom: 16,
              wordBreak: 'keep-all',
              letterSpacing: '-0.02em'
            }}
          >
            🌐 DID-RWA数据资产管理系统
          </Title>
          <Paragraph style={{ 
            fontSize: screens.xs ? '1.1em' : '1.3em', 
            opacity: 0.85, 
            color: '#434343',
            maxWidth: '900px',
            margin: '0 auto',
            lineHeight: '1.6',
            fontWeight: 400
          }}>
            分布式身份 × 现实世界资产 × 数据价值化生态平台
          </Paragraph>
        </div>

        {/* 导航按钮 */}
        <div style={{ 
          display: 'flex', 
          justifyContent: 'center', 
          gap: screens.xs ? 10 : 16, 
          marginBottom: 32, 
          flexWrap: 'wrap',
          padding: '0 12px'
        }}>
          <Space wrap size={screens.xs ? 10 : 16}>
            <Button 
              type={activeTab === 'dashboard' ? 'primary' : 'default'}
              icon={<BarChartOutlined />}
              onClick={() => setActiveTab('dashboard')}
              size={screens.xs ? 'middle' : 'large'}
              style={{ 
                borderRadius: 24,
                background: activeTab === 'dashboard' 
                  ? 'linear-gradient(135deg, #1890ff, #722ed1)' 
                  : 'linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7))',
                backdropFilter: 'blur(20px)',
                border: '1px solid rgba(24, 144, 255, 0.2)',
                boxShadow: activeTab === 'dashboard' 
                  ? '0 8px 24px rgba(24, 144, 255, 0.3)' 
                  : '0 6px 20px rgba(0, 0, 0, 0.08)',
                color: activeTab === 'dashboard' ? '#fff' : '#434343',
                fontWeight: 600,
                padding: '0 20px',
                height: screens.xs ? '40px' : '48px',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                transform: activeTab === 'dashboard' ? 'translateY(-2px)' : 'translateY(0)'
              }}
            >
              资产仪表板
            </Button>
            <Button 
              type="default"
              icon={<FireOutlined />}
              size={screens.xs ? 'middle' : 'large'}
              style={{ 
                borderRadius: 24,
                background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7))',
                backdropFilter: 'blur(20px)',
                border: '1px solid rgba(114, 46, 209, 0.2)',
                boxShadow: '0 6px 20px rgba(0, 0, 0, 0.08)',
                color: '#434343',
                fontWeight: 600,
                padding: '0 20px',
                height: screens.xs ? '40px' : '48px',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
              }}
            >
              资产代币化
            </Button>
            <Button 
              type="default"
              icon={<DollarOutlined />}
              size={screens.xs ? 'middle' : 'large'}
              style={{ 
                borderRadius: 24,
                background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7))',
                backdropFilter: 'blur(20px)',
                border: '1px solid rgba(82, 196, 26, 0.2)',
                boxShadow: '0 6px 20px rgba(0, 0, 0, 0.08)',
                color: '#434343',
                fontWeight: 600,
                padding: '0 20px',
                height: screens.xs ? '40px' : '48px',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
              }}
            >
              交易市场
            </Button>
            <Button 
              type="default"
              icon={<TrophyOutlined />}
              size={screens.xs ? 'middle' : 'large'}
              style={{ 
                borderRadius: 24,
                background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7))',
                backdropFilter: 'blur(20px)',
                border: '1px solid rgba(250, 173, 20, 0.2)',
                boxShadow: '0 6px 20px rgba(0, 0, 0, 0.08)',
                color: '#434343',
                fontWeight: 600,
                padding: '0 20px',
                height: screens.xs ? '40px' : '48px',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
              }}
            >
              深度分析
            </Button>
            <Button 
              type="default"
              icon={<SyncOutlined />}
              loading={loading}
              onClick={refreshData}
              size={screens.xs ? 'middle' : 'large'}
              style={{ 
                borderRadius: 24,
                background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7))',
                backdropFilter: 'blur(20px)',
                border: '1px solid rgba(19, 194, 194, 0.2)',
                boxShadow: '0 6px 20px rgba(0, 0, 0, 0.08)',
                color: '#434343',
                fontWeight: 600,
                padding: '0 20px',
                height: screens.xs ? '40px' : '48px',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)'
              }}
            >
              实时同步
            </Button>
          </Space>
        </div>

        {/* 主要内容 */}
        {renderDashboard()}
      </div>
    </div>
  );
};

export default DIDRWADashboard;