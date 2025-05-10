import React, { useState, useEffect, useRef } from 'react';
import { Card, Tabs, Button, Space, Tooltip, Input, Select, Table, Spin, Badge, message } from 'antd';
import { SearchOutlined, DownloadOutlined, FullscreenOutlined, RedoOutlined, NodeIndexOutlined, DatabaseOutlined, ApartmentOutlined, FileTextOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { Graph, register, ExtensionCategory } from '@antv/g6';
import { ReactNode } from '@antv/g6-extension-react';

const { TabPane } = Tabs;
const { Search } = Input;
const { Option } = Select;

// 注册React节点
register(ExtensionCategory.NODE, 'react', ReactNode);

// 模拟的数据血缘关系数据
const mockData = {
  nodes: [
    { id: 'node1', data: { label: '原始数据表1', type: 'source', status: 'normal' }, style: { x: 100, y: 100 } },
    { id: 'node2', data: { label: '中间表A', type: 'process', status: 'normal' }, style: { x: 300, y: 100 } },
    { id: 'node3', data: { label: '中间表B', type: 'process', status: 'warning' }, style: { x: 500, y: 100 } },
    { id: 'node4', data: { label: '结果表X', type: 'target', status: 'normal' }, style: { x: 300, y: 250 } },
    { id: 'node5', data: { label: '结果表Y', type: 'target', status: 'error' }, style: { x: 500, y: 250 } },
    { id: 'node6', data: { label: '原始数据表2', type: 'source', status: 'normal' }, style: { x: 100, y: 250 } },
  ],
  edges: [
    { source: 'node1', target: 'node2', label: 'ETL任务1' },
    { source: 'node2', target: 'node3', label: 'ETL任务2' },
    { source: 'node2', target: 'node4', label: 'ETL任务3' },
    { source: 'node3', target: 'node5', label: 'ETL任务4' },
    { source: 'node6', target: 'node3', label: 'ETL任务5' },
  ],
};

// Fix the component definition
interface DataLineagePageProps {
  setCurrentContent: (content: string) => void;
}

const DataLineagePage: React.FC<DataLineagePageProps> = ({ setCurrentContent }) => {
  const [activeTab, setActiveTab] = useState('1');
  const [graphRef, setGraphRef] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [searchValue, setSearchValue] = useState('');
  const [direction, setDirection] = useState('TB'); // TB: 从上到下，LR: 从左到右
  const containerRef = useRef<HTMLDivElement>(null);

  // 初始化图形
  useEffect(() => {
    if (activeTab === '1' && containerRef.current) {
      setTimeout(() => {
        initGraph();
        setLoading(false);
      }, 500);
    }
  }, [activeTab, direction]);

  const initGraph = () => {
    if (!containerRef.current) return;
    
    // 清除之前的图形
    if (graphRef) {
      graphRef.destroy();
    }
    
    // 初始化图形
    const graph = new Graph({
      container: containerRef.current,
      data: mockData,
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
        rankdir: direction,
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
    
    // 监听边点击事件
    graph.on('edge:click', (evt: any) => {
      const { item } = evt;
      const edge = item.getModel();
      message.info(`点击了关系: ${edge.label}`);
    });
    
    graph.render();
    setGraphRef(graph);
  };

  const handleFullscreen = () => {
    const container = containerRef.current;
    if (!container) return;
    
    if (document.fullscreenElement) {
      document.exitFullscreen();
    } else {
      container.requestFullscreen();
    }
  };

  const handleSearch = (value: string) => {
    setSearchValue(value);
    message.info(`搜索: ${value}`);
    // 实际应用中这里应该进行节点过滤或高亮处理
  };

  const handleDirectionChange = (value: string) => {
    setDirection(value);
    setLoading(true);
  };

  // Define columns inside the component to access setCurrentContent
  const tableColumns = [
    {
      title: '节点名称',
      dataIndex: ['data', 'label'],
      key: 'label',
    },
    {
      title: '节点类型',
      dataIndex: ['data', 'type'],
      key: 'type',
      render: (text: string) => {
        const typeMap: Record<string, string> = {
          source: '源表',
          process: '中间表',
          target: '目标表',
        };
        return typeMap[text] || text;
      },
    },
    {
      title: '状态',
      dataIndex: ['data', 'status'],
      key: 'status',
      render: (status: string) => {
        const statusMap: Record<string, React.ReactNode> = {
          normal: <Badge status="success" text="正常" />,
          warning: <Badge status="warning" text="警告" />,
          error: <Badge status="error" text="错误" />,
        };
        return statusMap[status] || <Badge status="default" text="未知" />;
      },
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: any) => (
        <Space size="middle">
          <a onClick={() => setCurrentContent('detail')}>查看详情</a>
          <a onClick={() => message.info(`展示${record.data.label}血缘`)}>血缘分析</a>
        </Space>
      ),
    },
  ];

  // 自定义React节点组件
  const CustomNode = ({ data }) => {
    const { label, type, status } = data.data;
    
    // 根据类型设置颜色和图标
    let color = '#1890ff';
    let Icon = ApartmentOutlined;
    
    if (type === 'source') {
      color = '#52c41a';
      Icon = DatabaseOutlined;
    } else if (type === 'target') {
      color = '#722ed1';
      Icon = FileTextOutlined;
    }
    
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

  return (
    <PageContainer
      header={{
        title: '数据血缘分析',
        subTitle: '查看数据表之间的关系和依赖',
      }}
    >
      <Card>
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="血缘图" key="1">
            <div style={{ marginBottom: 16 }}>
              <Space>
                <Search
                  placeholder="输入节点名称搜索"
                  allowClear
                  enterButton={<SearchOutlined />}
                  onSearch={handleSearch}
                  style={{ width: 250 }}
                />
                <Select
                  defaultValue="TB"
                  style={{ width: 150 }}
                  onChange={handleDirectionChange}
                >
                  <Option value="TB">从上到下布局</Option>
                  <Option value="LR">从左到右布局</Option>
                  <Option value="BT">从下到上布局</Option>
                  <Option value="RL">从右到左布局</Option>
                </Select>
                <Tooltip title="全屏">
                  <Button
                    icon={<FullscreenOutlined />}
                    onClick={handleFullscreen}
                  />
                </Tooltip>
                <Tooltip title="下载">
                  <Button
                    icon={<DownloadOutlined />}
                    onClick={() => message.success('图谱下载中...')}
                  />
                </Tooltip>
                <Tooltip title="刷新">
                  <Button
                    icon={<RedoOutlined />}
                    onClick={() => {
                      setLoading(true);
                      setTimeout(() => {
                        initGraph();
                        setLoading(false);
                        message.success('刷新成功');
                      }, 500);
                    }}
                  />
                </Tooltip>
              </Space>
            </div>
            <div
              ref={containerRef}
              style={{
                height: 600,
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
          
          <TabPane tab="表格视图" key="2">
            <div style={{ marginBottom: 16 }}>
              <Space>
                <Search
                  placeholder="输入节点名称搜索"
                  allowClear
                  enterButton={<SearchOutlined />}
                  onSearch={handleSearch}
                  style={{ width: 250 }}
                />
                <Button
                  icon={<NodeIndexOutlined />}
                  onClick={() => setActiveTab('1')}
                >
                  查看图形
                </Button>
              </Space>
            </div>
            <Table
              columns={tableColumns}
              dataSource={mockData.nodes}
              rowKey="id"
              pagination={false}
            />
          </TabPane>
        </Tabs>
      </Card>
    </PageContainer>
  );
};

export default DataLineagePage;