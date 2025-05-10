import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Tabs, Space, Typography, Select } from 'antd';
import { PageContainer } from '@ant-design/pro-components';
import { Column } from '@antv/g2plot';
import { PieChartOutlined, FileTextOutlined, ClockCircleOutlined } from '@ant-design/icons';

const { Title } = Typography;
const { TabPane } = Tabs;
const { Option } = Select;

const QualityInspectionPage = () => {
  // Mock data for the chart
  const [chartData, setChartData] = useState<Array<{date: string; type: string; value: number}>>([]);
  
  useEffect(() => {
    // Simulating the data for the chart
    const data = [
      {
        date: '5月1日',
        type: '问题总数',
        value: 20000,
      },
      {
        date: '5月1日',
        type: '问题率',
        value: 0,
      },
      {
        date: '5月1日',
        type: '待处理问题数',
        value: 0,
      },
      {
        date: '5月1日',
        type: '已解决问题数',
        value: 0,
      },
      {
        date: '5月2日',
        type: '问题总数',
        value: 20000,
      },
      {
        date: '5月2日',
        type: '问题率',
        value: 0,
      },
      {
        date: '5月2日',
        type: '待处理问题数',
        value: 0,
      },
      {
        date: '5月2日',
        type: '已解决问题数',
        value: 0,
      },
      {
        date: '5月3日',
        type: '问题总数',
        value: 20000,
      },
      {
        date: '5月3日',
        type: '问题率',
        value: 0,
      },
      {
        date: '5月3日',
        type: '待处理问题数',
        value: 0,
      },
      {
        date: '5月3日',
        type: '已解决问题数',
        value: 0,
      },
      {
        date: '5月4日',
        type: '问题总数',
        value: 20000,
      },
      {
        date: '5月4日',
        type: '问题率',
        value: 0,
      },
      {
        date: '5月4日',
        type: '待处理问题数',
        value: 0,
      },
      {
        date: '5月4日',
        type: '已解决问题数',
        value: 0,
      },
      {
        date: '5月5日',
        type: '问题总数',
        value: 20000,
      },
      {
        date: '5月5日',
        type: '问题率',
        value: 0,
      },
      {
        date: '5月5日',
        type: '待处理问题数',
        value: 0,
      },
      {
        date: '5月5日',
        type: '已解决问题数',
        value: 0,
      },
    ];
    setChartData(data);
  }, []);

  // Initialize the chart after the component mounts and when data changes
  useEffect(() => {
    if (chartData.length > 0) {
      const columnPlot = new Column('chart-container', {
        data: chartData,
        isGroup: true,
        xField: 'date',
        yField: 'value',
        seriesField: 'type',
        color: ['#1890ff', '#ffa940', '#f5222d', '#52c41a'],
        legend: {
          position: 'top-right',
        },
        xAxis: {
          label: {
            autoRotate: true,
          },
        },
        yAxis: {
          label: {
            formatter: (v) => `${v}`,
          },
          title: {
            text: '单位（个）',
          },
        },
        animation: {
          appear: {
            animation: 'fade-in',
          },
        },
        tooltip: {
          shared: true,
        },
      });

      columnPlot.render();

      return () => {
        columnPlot.destroy();
      };
    }
  }, [chartData]);

  const statisticsCards = [
    {
      title: '质量规则总数',
      value: 20,
      icon: <PieChartOutlined style={{ fontSize: '48px', color: '#1890ff' }} />,
      color: '#e6f7ff',
    },
    {
      title: '质量作业总数',
      value: 50,
      icon: <PieChartOutlined style={{ fontSize: '48px', color: '#1890ff' }} />,
      color: '#e6f7ff',
    },
    {
      title: '已处理问题数',
      value: 8,
      icon: <FileTextOutlined style={{ fontSize: '48px', color: '#52c41a' }} />,
      color: '#f6ffed',
    },
    {
      title: '当日新增已处理问题数',
      value: 0,
      icon: <FileTextOutlined style={{ fontSize: '48px', color: '#52c41a' }} />,
      color: '#f6ffed',
    },
    {
      title: '待处理问题数',
      value: 8,
      icon: <ClockCircleOutlined style={{ fontSize: '48px', color: '#fa8c16' }} />,
      color: '#fff7e6',
    },
    {
      title: '当日新增待处理问题数',
      value: 1,
      icon: <ClockCircleOutlined style={{ fontSize: '48px', color: '#fa8c16' }} />,
      color: '#fff7e6',
    },
  ];

  return (
    <PageContainer
    >
      <Card title="质量检查总览" bordered={false}>
        <Row gutter={[16, 16]}>
          {statisticsCards.map((item, index) => (
            <Col xs={24} sm={12} md={8} key={index}>
              <Card bordered={false} style={{ background: item.color, height: '100%' }}>
                <Row align="middle" gutter={16}>
                  <Col>{item.icon}</Col>
                  <Col>
                    <Statistic title={item.title} value={item.value} />
                  </Col>
                </Row>
              </Card>
            </Col>
          ))}
        </Row>
      </Card>

      <Card 
        title="近7天数据质量趋势" 
        style={{ marginTop: 16 }}
        extra={
          <Select defaultValue="全部" style={{ width: 120 }}>
            <Option value="全部">全部</Option>
            <Option value="已处理">已处理</Option>
            <Option value="待处理">待处理</Option>
          </Select>
        }
      >
        <div id="chart-container" style={{ height: 400 }}></div>
      </Card>
    </PageContainer>
  );
};

export default QualityInspectionPage;