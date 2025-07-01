import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Steps, Table, Tabs, Space, Typography, Badge, Tag, Button, Progress, Select, Tooltip } from 'antd';
import { 
  DatabaseOutlined, CloudUploadOutlined, ToolOutlined, SafetyCertificateOutlined, 
  LineChartOutlined, DeleteOutlined, HistoryOutlined, ApartmentOutlined,
  InfoCircleOutlined, SyncOutlined, CheckCircleOutlined, ClockCircleOutlined,
  WarningOutlined, ExclamationCircleOutlined
} from '@ant-design/icons';
import { PageContainer, ProCard, StatisticCard } from '@ant-design/pro-components';
import { Gauge, Liquid } from '@antv/g2plot';

const { Title, Text, Paragraph } = Typography;
const { TabPane } = Tabs;
const { Option } = Select;
const { Statistic: ProStatistic } = StatisticCard;

const DataLifecyclePage = () => {
  // Mock data
  const [lifecycleData, setLifecycleData] = useState({
    totalDatasets: 1284,
    activeSources: 47,
    processingJobs: 156,
    distributionEndpoints: 23,
    avgProcessingTime: '3.2小时',
    dataQualityScore: 92.5,
    storageUtilization: 78.4,
    dataStageCounts: {
      collection: 247,
      processing: 156,
      storage: 1284,
      analysis: 476,
      distribution: 352,
      archive: 95
    }
  });



  // Table data for lifecycle events
  const lifecycleEventsColumns = [
    {
      title: '数据集',
      dataIndex: 'dataset',
      key: 'dataset',
    },
    {
      title: '生命周期阶段',
      dataIndex: 'stage',
      key: 'stage',
      render: (text) => {
        const colorMap = {
          '采集': 'blue',
          '处理': 'cyan',
          '存储': 'green',
          '分析': 'purple',
          '分发': 'magenta',
          '归档': 'orange',
          '销毁': 'red',
        };
        return <Tag color={colorMap[text]}>{text}</Tag>;
      },
    },
    {
      title: '事件类型',
      dataIndex: 'eventType',
      key: 'eventType',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (text) => {
        if (text === '成功') return <Badge status="success" text="成功" />;
        if (text === '进行中') return <Badge status="processing" text="进行中" />;
        if (text === '警告') return <Badge status="warning" text="警告" />;
        if (text === '失败') return <Badge status="error" text="失败" />;
        return <Badge status="default" text={text} />;
      },
    },
    {
      title: '时间',
      dataIndex: 'time',
      key: 'time',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small">详情</Button>
          <Button type="link" size="small">日志</Button>
        </Space>
      ),
    },
  ];

  const lifecycleEventsData = [
    {
      key: '1',
      dataset: '用户行为数据',
      stage: '采集',
      eventType: '实时同步',
      status: '成功',
      time: '2025-05-10 09:23:45',
    },
    {
      key: '2',
      dataset: '销售交易记录',
      stage: '处理',
      eventType: '数据清洗',
      status: '进行中',
      time: '2025-05-10 08:45:12',
    },
    {
      key: '3',
      dataset: '历史订单数据',
      stage: '存储',
      eventType: '冷热分层',
      status: '成功',
      time: '2025-05-10 07:30:00',
    },
    {
      key: '4',
      dataset: '客户画像数据',
      stage: '分析',
      eventType: '模型训练',
      status: '警告',
      time: '2025-05-10 06:15:33',
    },
    {
      key: '5',
      dataset: '产品目录',
      stage: '分发',
      eventType: 'API发布',
      status: '成功',
      time: '2025-05-09 22:45:18',
    },
    {
      key: '6',
      dataset: '2023年财务报表',
      stage: '归档',
      eventType: '压缩归档',
      status: '成功',
      time: '2025-05-09 18:20:41',
    },
    {
      key: '7',
      dataset: '过期临时数据',
      stage: '销毁',
      eventType: '安全删除',
      status: '失败',
      time: '2025-05-09 16:05:27',
    },
  ];

  // Data governance index table
  const governanceColumns = [
    {
      title: '指标名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '当前值',
      dataIndex: 'value',
      key: 'value',
      render: (text, record) => {
        return (
          <Space>
            <span>{text}</span>
            {record.trend === 'up' && <span style={{ color: record.goodTrend === 'up' ? '#52c41a' : '#f5222d' }}>↑</span>}
            {record.trend === 'down' && <span style={{ color: record.goodTrend === 'down' ? '#52c41a' : '#f5222d' }}>↓</span>}
            {record.trend === 'flat' && <span style={{ color: '#1890ff' }}>→</span>}
          </Space>
        );
      },
    },
    {
      title: '目标值',
      dataIndex: 'target',
      key: 'target',
    },
    {
      title: '达成率',
      dataIndex: 'completion',
      key: 'completion',
      render: (text) => <Progress percent={text} size="small" />,
    },
    {
      title: '责任部门',
      dataIndex: 'department',
      key: 'department',
      render: (text) => <Tag>{text}</Tag>,
    },
  ];

  const governanceData = [
    {
      key: '1',
      name: '数据标准覆盖率',
      value: '87.5%',
      target: '95%',
      completion: 92,
      department: '数据治理',
      trend: 'up',
      goodTrend: 'up',
    },
    {
      key: '2',
      name: '数据质量评分',
      value: '92.5',
      target: '95.0',
      completion: 97,
      department: '数据质量',
      trend: 'up',
      goodTrend: 'up',
    },
    {
      key: '3',
      name: '数据源接入及时率',
      value: '94.2%',
      target: '99%',
      completion: 95,
      department: '数据集成',
      trend: 'up',
      goodTrend: 'up',
    },
    {
      key: '4',
      name: '平均数据处理时长',
      value: '3.2小时',
      target: '2小时',
      completion: 63,
      department: '数据处理',
      trend: 'down',
      goodTrend: 'down',
    },
    {
      key: '5',
      name: '存储资源利用率',
      value: '78.4%',
      target: '85%',
      completion: 92,
      department: '基础架构',
      trend: 'up',
      goodTrend: 'up',
    },
    {
      key: '6',
      name: '数据安全合规率',
      value: '98.7%',
      target: '100%',
      completion: 99,
      department: '数据安全',
      trend: 'flat',
      goodTrend: 'up',
    },
  ];

  // Initialize charts after component mounts
  useEffect(() => {
    // Data quality gauge chart
    const gaugeChart = new Gauge('quality-gauge-container', {
      percent: lifecycleData.dataQualityScore / 100,
      range: {
        color: '#30BF78',
      },
      indicator: {
        pointer: {
          style: {
            stroke: '#D0D0D0',
          },
        },
        pin: {
          style: {
            stroke: '#D0D0D0',
          },
        },
      },
      axis: {
        label: {
          formatter(v) {
            return Number(v) * 100;
          },
        },
        subTickLine: {
          count: 3,
        },
      },
      statistic: {
        content: {
          formatter: ({ percent }) => `${(percent * 100).toFixed(1)}`,
          style: {
            fontSize: '24px',
            lineHeight: 1,
          },
        },
        title: {
          content: '数据质量评分',
          style: {
            fontSize: '14px',
          }
        },
      },
    });
    gaugeChart.render();

    // Storage utilization liquid chart
    const liquidChart = new Liquid('storage-liquid-container', {
      percent: lifecycleData.storageUtilization / 100,
      outline: {
        border: 2,
        distance: 4,
        style: {
          stroke: '#5B8FF9',
          strokeOpacity: 0.65,
        },
      },
      wave: {
        length: 128,
      },
      statistic: {
        content: {
          formatter: ({ percent }) => `${(percent * 100).toFixed(1)}%`,
          style: {
            fontSize: '24px',
            lineHeight: 1,
          },
        },
        title: {
          content: '存储资源利用率',
          style: {
            fontSize: '14px',
          }
        },
      },
    });
    liquidChart.render();

    return () => {
      gaugeChart.destroy();
      liquidChart.destroy();
    };
  }, [lifecycleData]);

  const imgStyle = {
    display: 'block',
    width: 42,
    height: 42,
  };

  return (
    <PageContainer
    >
      {/* Overview Statistics */}
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <ProCard.Group direction="row">
            <ProCard>
              <ProStatistic 
                title="数据集总数"
                value={lifecycleData.totalDatasets}
                layout="vertical"
                icon={<DatabaseOutlined />}
              />
            </ProCard>
            <ProCard>
              <ProStatistic
                title="数据源数量"
                value={lifecycleData.activeSources}
                layout="vertical"
                icon={<CloudUploadOutlined />}
              />
            </ProCard>
            <ProCard>
              <ProStatistic
                title="处理任务数"
                value={lifecycleData.processingJobs}
                layout="vertical"
                icon={<ToolOutlined />}
              />
            </ProCard>
            <ProCard>
              <ProStatistic
                title="分发终端数"
                value={lifecycleData.distributionEndpoints}
                layout="vertical"
                icon={<ApartmentOutlined />}
              />
            </ProCard>
            <ProCard>
              <ProStatistic
                title="平均处理时长"
                value={lifecycleData.avgProcessingTime}
                layout="vertical"
                icon={<ClockCircleOutlined />}
              />
            </ProCard>
          </ProCard.Group>
        </Col>
      </Row>

      {/* Lifecycle Visualization */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={16}>
          <Card title="数据生命周期流程" bordered={false} style={{ height: 300 }}>
            <div className="custom-flow-container" style={{ height: '100%', padding: '20px 10px' }}>
              <Steps
                direction="horizontal"
                size="small"
                current={5}
                className="lifecycle-steps-horizontal"
                style={{ width: '100%' }}
                items={[
                  {
                    title: <span className="step-title">数据采集</span>,
                    description: (
                      <div className="step-content">
                        <Text type="secondary">从各种数据源收集原始数据</Text>
                        <div className="step-tags" style={{ marginTop: 4 }}>
                          <Tag color="blue" size="small">实时采集</Tag>
                          <Tag color="cyan" size="small">批量导入</Tag>
                        </div>
                      </div>
                    ),
                    icon: <DatabaseOutlined style={{ color: '#1890ff' }} />,
                  },
                  {
                    title: <span className="step-title">数据处理</span>,
                    description: (
                      <div className="step-content">
                        <Text type="secondary">清洗、转换和标准化数据</Text>
                        <div className="step-tags" style={{ marginTop: 4 }}>
                          <Tag color="cyan" size="small">数据清洗</Tag>
                          <Tag color="blue" size="small">格式转换</Tag>
                        </div>
                      </div>
                    ),
                    icon: <ToolOutlined style={{ color: '#13c2c2' }} />,
                  },
                  {
                    title: <span className="step-title">数据存储</span>,
                    description: (
                      <div className="step-content">
                        <Text type="secondary">将处理后的数据存储到数据仓库</Text>
                        <div className="step-tags" style={{ marginTop: 4 }}>
                          <Tag color="green" size="small">冷热分层</Tag>
                          <Tag color="lime" size="small">备份保护</Tag>
                        </div>
                      </div>
                    ),
                    icon: <SafetyCertificateOutlined style={{ color: '#52c41a' }} />,
                  },
                  {
                    title: <span className="step-title">数据分析</span>,
                    description: (
                      <div className="step-content">
                        <Text type="secondary">基于数据进行分析和挖掘</Text>
                        <div className="step-tags" style={{ marginTop: 4 }}>
                          <Tag color="purple" size="small">模型训练</Tag>
                          <Tag color="magenta" size="small">统计分析</Tag>
                        </div>
                      </div>
                    ),
                    icon: <LineChartOutlined style={{ color: '#722ed1' }} />,
                  },
                  {
                    title: <span className="step-title">数据分发</span>,
                    description: (
                      <div className="step-content">
                        <Text type="secondary">通过API和报表提供数据服务</Text>
                        <div className="step-tags" style={{ marginTop: 4 }}>
                          <Tag color="magenta" size="small">API发布</Tag>
                          <Tag color="red" size="small">报表生成</Tag>
                        </div>
                      </div>
                    ),
                    icon: <ApartmentOutlined style={{ color: '#eb2f96' }} />,
                  },
                  {
                    title: <span className="step-title">数据归档</span>,
                    description: (
                      <div className="step-content">
                        <Text type="secondary">长期保存历史数据</Text>
                        <div className="step-tags" style={{ marginTop: 4 }}>
                          <Tag color="orange" size="small">压缩存储</Tag>
                          <Tag color="gold" size="small">合规保留</Tag>
                        </div>
                      </div>
                    ),
                    icon: <HistoryOutlined style={{ color: '#fa8c16' }} />,
                  },
                ]}
              />
            </div>
          </Card>
        </Col>
        <Col span={8}>
          <Card title="阶段统计" bordered={false} style={{ height: 300 }}>
            <Space direction="vertical" style={{ width: '100%' }}>
              <Row align="middle">
                <Col span={8}>
                  <Text>数据采集</Text>
                </Col>
                <Col span={12}>
                  <Progress 
                    percent={Math.round(lifecycleData.dataStageCounts.collection / lifecycleData.totalDatasets * 100)} 
                    strokeColor="#1890ff" 
                  />
                </Col>
                <Col span={4} style={{ textAlign: 'right' }}>
                  <Text>{lifecycleData.dataStageCounts.collection}</Text>
                </Col>
              </Row>
              <Row align="middle">
                <Col span={8}>
                  <Text>数据处理</Text>
                </Col>
                <Col span={12}>
                  <Progress 
                    percent={Math.round(lifecycleData.dataStageCounts.processing / lifecycleData.totalDatasets * 100)} 
                    strokeColor="#13c2c2" 
                  />
                </Col>
                <Col span={4} style={{ textAlign: 'right' }}>
                  <Text>{lifecycleData.dataStageCounts.processing}</Text>
                </Col>
              </Row>
              <Row align="middle">
                <Col span={8}>
                  <Text>数据存储</Text>
                </Col>
                <Col span={12}>
                  <Progress 
                    percent={Math.round(lifecycleData.dataStageCounts.storage / lifecycleData.totalDatasets * 100)} 
                    strokeColor="#52c41a" 
                  />
                </Col>
                <Col span={4} style={{ textAlign: 'right' }}>
                  <Text>{lifecycleData.dataStageCounts.storage}</Text>
                </Col>
              </Row>
              <Row align="middle">
                <Col span={8}>
                  <Text>数据分析</Text>
                </Col>
                <Col span={12}>
                  <Progress 
                    percent={Math.round(lifecycleData.dataStageCounts.analysis / lifecycleData.totalDatasets * 100)} 
                    strokeColor="#722ed1" 
                  />
                </Col>
                <Col span={4} style={{ textAlign: 'right' }}>
                  <Text>{lifecycleData.dataStageCounts.analysis}</Text>
                </Col>
              </Row>
              <Row align="middle">
                <Col span={8}>
                  <Text>数据分发</Text>
                </Col>
                <Col span={12}>
                  <Progress 
                    percent={Math.round(lifecycleData.dataStageCounts.distribution / lifecycleData.totalDatasets * 100)} 
                    strokeColor="#eb2f96" 
                  />
                </Col>
                <Col span={4} style={{ textAlign: 'right' }}>
                  <Text>{lifecycleData.dataStageCounts.distribution}</Text>
                </Col>
              </Row>
              <Row align="middle">
                <Col span={8}>
                  <Text>数据归档</Text>
                </Col>
                <Col span={12}>
                  <Progress 
                    percent={Math.round(lifecycleData.dataStageCounts.archive / lifecycleData.totalDatasets * 100)} 
                    strokeColor="#fa8c16" 
                  />
                </Col>
                <Col span={4} style={{ textAlign: 'right' }}>
                  <Text>{lifecycleData.dataStageCounts.archive}</Text>
                </Col>
              </Row>
            </Space>
          </Card>
        </Col>
      </Row>

      {/* Quality and Storage */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col span={12}>
          <Card title="数据质量分布" bordered={false}>
            <div id="quality-gauge-container" style={{ height: 300 }}></div>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="存储资源状态" bordered={false}>
            <div id="storage-liquid-container" style={{ height: 300 }}></div>
          </Card>
        </Col>
      </Row>

      {/* Recent Events */}
      <Card title="最近生命周期事件" style={{ marginTop: 16 }} bordered={false}>
        <Table 
          columns={lifecycleEventsColumns} 
          dataSource={lifecycleEventsData} 
          pagination={{ pageSize: 5 }}
        />
      </Card>

      {/* Data Governance */}
      <Card title="数据治理指标" style={{ marginTop: 16 }} bordered={false}>
        <Table 
          columns={governanceColumns} 
          dataSource={governanceData} 
          pagination={false}
        />
      </Card>
    </PageContainer>
  );
};

export default DataLifecyclePage;