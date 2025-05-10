import React, { useState, useEffect, useRef } from 'react';
import {
  Card,
  Row,
  Col,
  Tabs,
  Button,
  Space,
  Tag,
  Table,
  Drawer,
  Descriptions,
  Steps,
  Typography,
  Divider,
  Badge,
  Statistic,
  Timeline,
  Tooltip,
  List,
  Input,
  Spin,
  Radio,
  message,
  Popover
} from 'antd';
import {
  NodeIndexOutlined,
  InfoCircleOutlined,
  HistoryOutlined,
  CodeOutlined,
  FileSearchOutlined,
  UnorderedListOutlined,
  ApartmentOutlined,
  CalendarOutlined,
  ClockCircleOutlined,
  UserOutlined,
  DatabaseOutlined,
  ApiOutlined,
  AreaChartOutlined,
  SearchOutlined,
  DownloadOutlined,
  FilterOutlined,
  PlusOutlined,
  ArrowLeftOutlined
} from '@ant-design/icons';
import { PageContainer, ProTable, StatisticCard } from '@ant-design/pro-components';
import { Graph, register, ExtensionCategory } from '@antv/g6';
import { ReactNode } from '@antv/g6-extension-react';
import { history } from 'umi';

const { TabPane } = Tabs;
const { Title, Paragraph, Text } = Typography;
const { Step } = Steps;
const { Search } = Input;

// 注册React节点
register(ExtensionCategory.NODE, 'react', ReactNode);

// 模拟的数据节点详情数据
const nodeDetail = {
  id: 'node2',
  name: '中间表A',
  tableName: 'dwd_user_behavior_detail',
  database: 'dwd',
  owner: '张三',
  createTime: '2025-01-15 14:30:22',
  updateTime: '2025-05-09 09:15:33',
  description: '用户行为明细表，存储用户的点击、收藏、加购、购买等行为数据',
  fields: [
    { name: 'user_id', type: 'string', description: '用户ID', fromSource: 'ods_user_base' },
    { name: 'item_id', type: 'string', description: '商品ID', fromSource: 'ods_item_base' },
    { name: 'behavior_type', type: 'string', description: '行为类型', fromSource: '转换' },
    { name: 'behavior_time', type: 'timestamp', description: '行为时间', fromSource: 'ods_user_log' },
    { name: 'session_id', type: 'string', description: '会话ID', fromSource: 'ods_user_log' },
    { name: 'location', type: 'string', description: '地理位置', fromSource: '转换' },
  ],
  upstream: [
    { id: 'node1', name: 'ods_user_base', relationship: '直接依赖', fields: ['user_id', 'age', 'gender'] },
    { id: 'node6', name: 'ods_item_base', relationship: '直接依赖', fields: ['item_id', 'category_id'] },
    { id: 'node7', name: 'ods_user_log', relationship: '直接依赖', fields: ['behavior_time', 'session_id', 'raw_location'] },
  ],
  downstream: [
    { id: 'node3', name: '中间表B', relationship: '直接依赖', fields: ['user_id', 'item_id', 'behavior_type'] },
    { id: 'node4', name: '结果表X', relationship: '直接依赖', fields: ['user_id', 'behavior_type', 'behavior_time'] },
  ],
  tasks: [
    { id: 'task1', name: 'user_behavior_etl_daily', type: 'Hive SQL', status: 'success', lastRunTime: '2025-05-09 08:00:00', duration: '15分钟', owner: '李四' },
    { id: 'task2', name: 'behavior_aggregation', type: 'Spark', status: 'warning', lastRunTime: '2025-05-09 08:20:00', duration: '8分钟', owner: '李四' },
  ],
  quality: {
    completeness: 98.5,
    accuracy: 97.2,
    timeliness: 99.1,
    consistency: 96.8,
    issues: [
      { type: '空值', field: 'location', count: 1250, percentage: '1.5%', trend: 'down' },
      { type: '异常值', field: 'behavior_time', count: 87, percentage: '0.1%', trend: 'up' },
    ]
  },
  history: [
    { time: '2025-05-09 09:15:33', event: '字段更新', details: '新增字段: location', operator: '张三' },
    { time: '2025-04-22 14:30:45', event: '数据质量告警', details: 'behavior_time字段异常值超过阈值', operator: '系统' },
    { time: '2025-03-15 10:22:18', event: '依赖变更', details: '新增上游依赖: ods_user_log', operator: '王五' },
    { time: '2025-01-15 14:30:22', event: '表创建', details: '初始创建表结构', operator: '张三' },
  ],
  sql: `CREATE TABLE dwd.dwd_user_behavior_detail (
  user_id STRING COMMENT '用户ID',
  item_id STRING COMMENT '商品ID',
  behavior_type STRING COMMENT '行为类型',
  behavior_time TIMESTAMP COMMENT '行为时间',
  session_id STRING COMMENT '会话ID',
  location STRING COMMENT '地理位置'
) COMMENT '用户行为明细表'
PARTITIONED BY (dt STRING)
STORED AS PARQUET;

INSERT OVERWRITE TABLE dwd.dwd_user_behavior_detail PARTITION (dt='\${bizdate}')
SELECT
  u.user_id,
  i.item_id,
  CASE l.action_type
    WHEN 1 THEN 'click'
    WHEN 2 THEN 'favor'
    WHEN 3 THEN 'cart'
    WHEN 4 THEN 'purchase'
    ELSE 'unknown'
  END AS behavior_type,
  l.action_time AS behavior_time,
  l.session_id,
  COALESCE(l.city, 'unknown') AS location
FROM ods_user_base u
JOIN ods_user_log l ON u.user_id = l.user_id
JOIN ods_item_base i ON l.item_id = i.item_id
WHERE l.dt = '\${bizdate}'
  AND u.dt = '\${bizdate}'
  AND i.dt = '\${bizdate}';`
};

// 字段列
const fieldColumns = [
  {
    title: '字段名称',
    dataIndex: 'name',
    key: 'name',
    width: 150,
  },
  {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    width: 120,
    render: (text) => <Tag color="blue">{text}</Tag>,
  },
  {
    title: '来源',
    dataIndex: 'fromSource',
    key: 'fromSource',
    width: 150,
    render: (text) => (
      text === '转换' ? <Tag color="orange">转换</Tag> : <Tag color="green">{text}</Tag>
    ),
  },
  {
    title: '描述',
    dataIndex: 'description',
    key: 'description',
  },
];

// 上下游表格列
const relationColumns = [
  {
    title: '表名称',
    dataIndex: 'name',
    key: 'name',
    render: (text) => <a>{text}</a>,
  },
  {
    title: '关系',
    dataIndex: 'relationship',
    key: 'relationship',
    render: (text) => <Tag color="blue">{text}</Tag>,
  },
  {
    title: '关联字段',
    dataIndex: 'fields',
    key: 'fields',
    render: (fields) => (
      <span>
        {fields.map((field) => (
          <Tag color="purple" key={field}>
            {field}
          </Tag>
        ))}
      </span>
    ),
  },
  {
    title: '操作',
    key: 'action',
    render: (_, record) => (
      <Space size="middle">
        <a onClick={() => message.info(`查看${record.name}详情`)}>查看详情</a>
        <a onClick={() => message.info(`分析${record.name}血缘`)}>血缘分析</a>
      </Space>
    ),
  },
];

// 任务列表列
const taskColumns = [
  {
    title: '任务名称',
    dataIndex: 'name',
    key: 'name',
    render: (text) => <a>{text}</a>,
  },
  {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    render: (text) => {
      const colorMap = {
        'Hive SQL': 'blue',
        'Spark': 'orange',
        'Python': 'green',
      };
      return <Tag color={colorMap[text] || 'default'}>{text}</Tag>;
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    render: (status) => {
      const statusMap = {
        success: <Badge status="success" text="成功" />,
        warning: <Badge status="warning" text="警告" />,
        error: <Badge status="error" text="失败" />,
        running: <Badge status="processing" text="运行中" />,
      };
      return statusMap[status] || <Badge status="default" text="未知" />;
    },
  },
  {
    title: '最近执行',
    dataIndex: 'lastRunTime',
    key: 'lastRunTime',
  },
  {
    title: '耗时',
    dataIndex: 'duration',
    key: 'duration',
  },
  {
    title: '责任人',
    dataIndex: 'owner',
    key: 'owner',
  },
  {
    title: '操作',
    key: 'action',
    render: (_, record) => (
      <Space size="middle">
        <a onClick={() => message.info(`查看${record.name}详情`)}>查看详情</a>
        <a onClick={() => message.info(`查看${record.name}日志`)}>日志</a>
      </Space>
    ),
  },
];

// 质量问题列
const qualityIssueColumns = [
  {
    title: '问题类型',
    dataIndex: 'type',
    key: 'type',
    render: (text) => {
      const colorMap = {
        '空值': 'blue',
        '异常值': 'orange',
        '重复值': 'red',
        '格式错误': 'volcano',
      };
      return <Tag color={colorMap[text] || 'default'}>{text}</Tag>;
    },
  },
  {
    title: '字段',
    dataIndex: 'field',
    key: 'field',
  },
  {
    title: '问题数量',
    dataIndex: 'count',
    key: 'count',
  },
  {
    title: '占比',
    dataIndex: 'percentage',
    key: 'percentage',
  },
  {
    title: '趋势',
    dataIndex: 'trend',
    key: 'trend',
    render: (text) => {
      if (text === 'up') {
        return <Tag color="red">上升</Tag>;
      } else if (text === 'down') {
        return <Tag color="green">下降</Tag>;
      }
      return <Tag color="blue">稳定</Tag>;
    },
  },
  {
    title: '操作',
    key: 'action',
    render: (_, record) => (
      <a onClick={() => message.info(`查看${record.field}字段问题详情`)}>查看详情</a>
    ),
  },
];

// 自定义React节点组件
const CustomNode = ({ data }) => {
  const { label, type, status, isMain } = data.data;
  
  // 根据类型设置颜色和图标
  let color = '#1890ff';
  let Icon = ApartmentOutlined;
  
  if (type === 'source') {
    color = '#52c41a';
    Icon = DatabaseOutlined;
  } else if (type === 'target') {
    color = '#722ed1';
    Icon = FileSearchOutlined;
  }
  
  // 主节点特殊处理
  if (isMain) color = '#fa8c16';
  
  // 根据状态设置边框
  let borderColor = '#ffffff';
  let borderWidth = 1;
  
  if (status === 'warning') {
    borderColor = '#faad14';
    borderWidth = 2;
  } else if (status === 'error') {
    borderColor = '#f5222d';
    borderWidth = 2;
  }
  
  // 主节点特殊处理
  if (isMain) {
    borderColor = '#fa541c';
    borderWidth = 3;
  }
  
  return (
    <div
      style={{
        width: '100%',
        height: '100%',
        background: color,
        borderRadius: 4,
        border: `${borderWidth}px solid ${borderColor}`,
        color: 'white',
        padding: 8,
        display: 'flex',
        alignItems: 'center',
      }}
    >
      <Icon style={{ fontSize: 16, marginRight: 8 }} />
      <span>{label}</span>
    </div>
  );
};

// Fix the component definition to properly accept props
interface DetailProps {
  setCurrentContent: (content: string) => void;
  record: any;
}

const DataLineageDetail: React.FC<DetailProps> = ({ setCurrentContent, record }) => {
  const [activeTab, setActiveTab] = useState('1');
  const [graphRef, setGraphRef] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [nodeInfo, setNodeInfo] = useState(nodeDetail);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [drawerTitle, setDrawerTitle] = useState('');
  const [drawerContent, setDrawerContent] = useState<React.ReactNode>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  // 模拟的图数据 - 简化版，仅包含与当前节点相关的节点
  const mockGraphData = {
    nodes: [
      { id: 'node1', data: { label: 'ods_user_base', type: 'source', status: 'normal' }, style: { x: 100, y: 100 } },
      { id: 'node2', data: { label: '中间表A', type: 'process', status: 'normal', isMain: true }, style: { x: 300, y: 100 } },
      { id: 'node3', data: { label: '中间表B', type: 'process', status: 'warning' }, style: { x: 500, y: 100 } },
      { id: 'node4', data: { label: '结果表X', type: 'target', status: 'normal' }, style: { x: 300, y: 250 } },
      { id: 'node6', data: { label: 'ods_item_base', type: 'source', status: 'normal' }, style: { x: 100, y: 150 } },
      { id: 'node7', data: { label: 'ods_user_log', type: 'source', status: 'normal' }, style: { x: 100, y: 200 } },
    ],
    edges: [
      { source: 'node1', target: 'node2', label: 'ETL任务1' },
      { source: 'node6', target: 'node2', label: 'ETL任务5' },
      { source: 'node7', target: 'node2', label: 'ETL任务6' },
      { source: 'node2', target: 'node3', label: 'ETL任务2' },
      { source: 'node2', target: 'node4', label: 'ETL任务3' },
    ],
  };

  // 初始化图形
  useEffect(() => {
    if (activeTab === '2' && containerRef.current) {
      setTimeout(() => {
        initDetailGraph();
        setLoading(false);
      }, 500);
    }
  }, [activeTab]);

  const initDetailGraph = () => {
    if (!containerRef.current) return;
    
    // 清除之前的图形
    if (graphRef) {
      graphRef.destroy();
    }
    
    // 初始化图形
    const graph = new Graph({
      container: containerRef.current,
      data: mockGraphData,
      node: {
        type: 'react',
        style: {
          size: [150, 50],
          component: (data) => <CustomNode data={data} />,
        },
      },
      edge: {
        style: {
          endArrow: true,
          stroke: '#C2C8D5',
          lineWidth: 2,
        },
        labelCfg: {
          style: {
            background: {
              fill: '#ffffff',
              stroke: '#9EC9FF',
              padding: [2, 4, 2, 4],
              radius: 2,
            },
          },
        },
      },
      layout: {
        type: 'dagre',
        rankdir: 'LR',
        nodesep: 50,
        ranksep: 70,
      },
      behaviors: ['drag-element', 'zoom-canvas', 'drag-canvas'],
    });
    
    // 监听节点点击事件
    graph.on('node:click', (evt: any) => {
      const { item } = evt;
      const node = item.getModel();
      message.info(`点击了节点: ${node.data.label}`);
    });
    
    graph.render();
    setGraphRef(graph);
  };

  const showDrawer = (title: string, content: React.ReactNode) => {
    setDrawerTitle(title);
    setDrawerContent(content);
    setDrawerVisible(true);
  };

  const handleGoBack = () => {
    setCurrentContent('list');
  };

  return (
    <PageContainer
      header={{
        title: nodeInfo.name,
        subTitle: nodeInfo.tableName,
        backIcon: <ArrowLeftOutlined />,
        onBack: handleGoBack,
        tags: [
          <Tag color="blue" key="database">{nodeInfo.database}</Tag>,
        ],
        extra: [
          <Button key="1" type="primary" onClick={() => message.info('导出血缘关系')}>
            导出血缘
          </Button>,
          <Button key="2" onClick={() => message.info('刷新血缘关系')}>
            刷新
          </Button>,
        ],
      }}
    >
      <Card>
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane
            tab={
              <span>
                <InfoCircleOutlined />
                基本信息
              </span>
            }
            key="1"
          >
            <Row gutter={[16, 16]}>
              <Col span={24}>
                <Card title="表信息" bordered={false}>
                  <Descriptions bordered column={3}>
                    <Descriptions.Item label="表名" span={1}>
                      {nodeInfo.tableName}
                    </Descriptions.Item>
                    <Descriptions.Item label="数据库" span={1}>
                      {nodeInfo.database}
                    </Descriptions.Item>
                    <Descriptions.Item label="责任人" span={1}>
                      <UserOutlined /> {nodeInfo.owner}
                    </Descriptions.Item>
                    <Descriptions.Item label="创建时间" span={1}>
                      <CalendarOutlined /> {nodeInfo.createTime}
                    </Descriptions.Item>
                    <Descriptions.Item label="更新时间" span={1}>
                      <ClockCircleOutlined /> {nodeInfo.updateTime}
                    </Descriptions.Item>
                    <Descriptions.Item label="描述" span={3}>
                      {nodeInfo.description}
                    </Descriptions.Item>
                  </Descriptions>
                </Card>
              </Col>
              
              <Col span={24}>
                <Card title="字段信息" bordered={false} extra={
                  <Space>
                    <Button icon={<SearchOutlined />}>搜索</Button>
                    <Button icon={<FilterOutlined />}>筛选</Button>
                    <Button icon={<DownloadOutlined />}>导出</Button>
                  </Space>
                }>
                  <Table
                    columns={fieldColumns}
                    dataSource={nodeInfo.fields}
                    rowKey="name"
                    pagination={false}
                  />
                </Card>
              </Col>
              
              <Col span={12}>
                <Card title="上游依赖" bordered={false}>
                  <Table
                    columns={relationColumns}
                    dataSource={nodeInfo.upstream}
                    rowKey="id"
                    pagination={false}
                    size="small"
                  />
                </Card>
              </Col>
              
              <Col span={12}>
                <Card title="下游依赖" bordered={false}>
                  <Table
                    columns={relationColumns}
                    dataSource={nodeInfo.downstream}
                    rowKey="id"
                    pagination={false}
                    size="small"
                  />
                </Card>
              </Col>
            </Row>
          </TabPane>
          
          <TabPane
            tab={
              <span>
                <ApartmentOutlined />
                血缘关系
              </span>
            }
            key="2"
          >
            <div style={{ marginBottom: 16 }}>
              <Space>
                <Search
                  placeholder="搜索节点"
                  allowClear
                  style={{ width: 250 }}
                  onSearch={(value) => message.info(`搜索: ${value}`)}
                />
                <Radio.Group defaultValue="all" buttonStyle="solid">
                  <Radio.Button value="all">全部</Radio.Button>
                  <Radio.Button value="upstream">上游</Radio.Button>
                  <Radio.Button value="downstream">下游</Radio.Button>
                </Radio.Group>
                <Button
                  icon={<DownloadOutlined />}
                  onClick={() => message.success('图谱下载中...')}
                >
                  导出图片
                </Button>
              </Space>
            </div>
            <div
              ref={containerRef}
              style={{
                height: 500,
                border: '1px solid #eee',
                borderRadius: 4,
                position: 'relative',
              }}
            >
              {loading && (
                <div style={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  right: 0,
                  bottom: 0,
                  background: 'rgba(255, 255, 255, 0.7)',
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                }}>
                  <Spin size="large" tip="图谱加载中..." />
                </div>
              )}
            </div>
          </TabPane>
          
          <TabPane
            tab={
              <span>
                <ApiOutlined />
                任务信息
              </span>
            }
            key="3"
          >
            <Card title="关联任务" bordered={false} extra={
              <Button type="primary" icon={<PlusOutlined />}>
                新增任务
              </Button>
            }>
              <Table
                columns={taskColumns}
                dataSource={nodeInfo.tasks}
                rowKey="id"
                pagination={false}
              />
            </Card>
          </TabPane>
          
          <TabPane
            tab={
              <span>
                <AreaChartOutlined />
                数据质量
              </span>
            }
            key="4"
          >
            <Row gutter={[16, 16]}>
              <Col span={24}>
                <Row gutter={16}>
                  <Col span={6}>
                    <Card>
                      <Statistic
                        title="完整性"
                        value={nodeInfo.quality.completeness}
                        precision={1}
                        valueStyle={{ color: nodeInfo.quality.completeness > 95 ? '#3f8600' : '#cf1322' }}
                        suffix="%"
                      />
                      <Tooltip title="数据无缺失的程度">
                        <InfoCircleOutlined style={{ marginLeft: 8 }} />
                      </Tooltip>
                    </Card>
                  </Col>
                  <Col span={6}>
                    <Card>
                      <Statistic
                        title="准确性"
                        value={nodeInfo.quality.accuracy}
                        precision={1}
                        valueStyle={{ color: nodeInfo.quality.accuracy > 95 ? '#3f8600' : '#cf1322' }}
                        suffix="%"
                      />
                      <Tooltip title="数据符合业务规则的程度">
                        <InfoCircleOutlined style={{ marginLeft: 8 }} />
                      </Tooltip>
                    </Card>
                  </Col>
                  <Col span={6}>
                    <Card>
                      <Statistic
                        title="及时性"
                        value={nodeInfo.quality.timeliness}
                        precision={1}
                        valueStyle={{ color: nodeInfo.quality.timeliness > 95 ? '#3f8600' : '#cf1322' }}
                        suffix="%"
                      />
                      <Tooltip title="数据按时产出的程度">
                        <InfoCircleOutlined style={{ marginLeft: 8 }} />
                      </Tooltip>
                    </Card>
                  </Col>
                  <Col span={6}>
                    <Card>
                      <Statistic
                        title="一致性"
                        value={nodeInfo.quality.consistency}
                        precision={1}
                        valueStyle={{ color: nodeInfo.quality.consistency > 95 ? '#3f8600' : '#cf1322' }}
                        suffix="%"
                      />
                      <Tooltip title="数据在不同系统中保持一致的程度">
                        <InfoCircleOutlined style={{ marginLeft: 8 }} />
                      </Tooltip>
                    </Card>
                  </Col>
                </Row>
              </Col>
              
              <Col span={24}>
                <Card title="质量问题" bordered={false}>
                  <Table
                    columns={qualityIssueColumns}
                    dataSource={nodeInfo.quality.issues}
                    rowKey="field"
                    pagination={false}
                  />
                </Card>
              </Col>
            </Row>
          </TabPane>
          
          <TabPane
            tab={
              <span>
                <CodeOutlined />
                SQL信息
              </span>
            }
            key="5"
          >
            <Card bordered={false}>
              <div style={{ background: '#f5f5f5', padding: 16, borderRadius: 4 }}>
                <pre style={{ margin: 0, whiteSpace: 'pre-wrap', wordBreak: 'break-all' }}>
                  {nodeInfo.sql}
                </pre>
              </div>
              <div style={{ marginTop: 16, textAlign: 'right' }}>
                <Space>
                  <Button onClick={() => {
                    navigator.clipboard.writeText(nodeInfo.sql);
                    message.success('SQL已复制到剪贴板');
                  }}>
                    复制
                  </Button>
                  <Button type="primary" onClick={() => message.info('执行SQL')}>
                    执行
                  </Button>
                </Space>
              </div>
            </Card>
          </TabPane>
          
          <TabPane
            tab={
              <span>
                <HistoryOutlined />
                变更历史
              </span>
            }
            key="6"
          >
            <Card bordered={false}>
              <Timeline mode="left">
                {nodeInfo.history.map((item, index) => (
                  <Timeline.Item key={index} label={item.time}>
                    <div>
                      <Tag color="blue">{item.event}</Tag>
                      <span style={{ marginLeft: 8 }}>{item.details}</span>
                    </div>
                    <div style={{ color: '#999', fontSize: 12, marginTop: 4 }}>
                      操作人: {item.operator}
                    </div>
                  </Timeline.Item>
                ))}
              </Timeline>
            </Card>
          </TabPane>
        </Tabs>
      </Card>
      
      <Drawer
        title={drawerTitle}
        placement="right"
        width={600}
        onClose={() => setDrawerVisible(false)}
        visible={drawerVisible}
      >
        {drawerContent}
      </Drawer>
    </PageContainer>
  );
};

export default DataLineageDetail;