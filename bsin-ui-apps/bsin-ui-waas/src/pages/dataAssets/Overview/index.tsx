import React, { useState } from 'react';
import { Layout, Card, Typography, Button, Space, Tag, Divider, Avatar, Tooltip } from 'antd';
import { CopyOutlined, StarOutlined, MenuOutlined, ArrowUpOutlined } from '@ant-design/icons';
import { Link, history } from 'umi';
import { ProCard, StatisticCard } from '@ant-design/pro-components';
const { Statistic } = StatisticCard;
import RcResizeObserver from 'rc-resize-observer';

const imgStyle = {
  display: 'block',
  width: 42,
  height: 42,
};

const { Text } = Typography;

// 使用示例
const currentDateAndWeekday = () => {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    weekday: 'long' as const
  };
  const currentDate = new Date().toLocaleDateString('zh-CN', options);
  return currentDate;
}

export default function SaaSDashboard() {

  const [responsive, setResponsive] = useState(false);

  return (
    <Card>
      <header>
        <h1 style={{ marginBottom: "0" }}>RDA数据资产总览</h1>
        <p style={{ color: "#6b7280" }} className="text-gray-500 mb-4">实时监控链上真实数据资产的表现与趋势</p>
      </header>

      <div style={{}}>
        {/* Header Section */}
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <Avatar style={{ backgroundColor: '#1890ff', marginRight: '8px' }}>C</Avatar>
            <Text strong style={{ fontSize: '16px', marginRight: '8px' }}>合约地址</Text>
            <Text type="secondary" style={{ marginRight: '4px' }}>0x3fC9F717222b385e2F7CB3827751916160D5ea68f</Text>
            <Button type="text" icon={<CopyOutlined />} size="small" />
          </div>
          <div style={{ marginLeft: 'auto' }}>
            <Space>
              <Button type="primary">购买</Button>
              <Button onClick={() => history.push('/assets/assets-collection')}>发行</Button>
              <Button>溯源</Button>
            </Space>
          </div>
        </div>


        {/* Content Cards */}
        <div style={{ display: 'flex', gap: '16px' }}>
          {/* Overview Card */}
          <Card title="RDA资产" style={{ flex: 1 }}>
            <div>
              <Text type="secondary" style={{ fontSize: '12px', display: 'block' }}>余额</Text>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Avatar size="small" style={{ backgroundColor: '#faad14', marginRight: '8px' }}>R</Avatar>
                <Text>0 RDA</Text>
              </div>
            </div>
            <Divider style={{ margin: '12px 0' }} />
            <div>
              <Text type="secondary" style={{ fontSize: '12px', display: 'block' }}>价值</Text>
              <Text>$0.00</Text>
            </div>
          </Card>

          {/* More Info Card */}
          <Card title="资产详情" style={{ flex: 1 }}>
            <div>
              <Text type="secondary" style={{ fontSize: '12px', display: 'block' }}>PRIVATE NAME TAGS</Text>
              <Button type="dashed" size="small" style={{ marginTop: '8px' }}>+ Add</Button>
            </div>
            <Divider style={{ margin: '12px 0' }} />
            <div>
              <Text type="secondary" style={{ fontSize: '12px', display: 'block' }}>发行方</Text>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Text style={{ color: '#1890ff' }}>0x42238292...04EfBd982</Text>
                <Button type="text" size="small" icon={<CopyOutlined />} />
                <Text style={{ marginLeft: '8px' }}>680 days ago</Text>
                <ArrowUpOutlined style={{ marginLeft: '4px' }} />
              </div>
            </div>
          </Card>

          {/* Multichain Info Card */}
          <Card title="多链信息" style={{ flex: 1 }}>
            <div style={{ marginBottom: '16px' }}>
              <Tag style={{ display: 'flex', alignItems: 'center' }}>
                💼 $0 (Multichain Portfolio)
              </Tag>
            </div>
            <Text type="secondary">No addresses found</Text>
          </Card>
        </div>
      </div>

      <RcResizeObserver
        key="resize-observer"
        onResize={(offset) => {
          setResponsive(offset.width < 596);
        }}
      >

        <ProCard split="vertical"
          style={{ margin: "20px 0px" }}
          bordered
        >
          <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
            <StatisticCard
              statistic={{
                title: '可信身份',
                value: 2176,
                icon: (
                  <img
                    style={imgStyle}
                    src="https://gw.alipayobjects.com/mdn/rms_7bc6d8/afts/img/A*dr_0RKvVzVwAAAAAAAAAAABkARQnAQ"
                    alt="icon"
                  />
                ),
              }}
            />
            <StatisticCard
              statistic={{
                title: '消费数据',
                value: 475,
                icon: (
                  <img
                    style={imgStyle}
                    src="https://gw.alipayobjects.com/mdn/rms_7bc6d8/afts/img/A*-jVKQJgA1UgAAAAAAAAAAABkARQnAQ"
                    alt="icon"
                  />
                ),
              }}
            />
            <StatisticCard
              statistic={{
                title: '产品数据',
                value: 87,
                icon: (
                  <img
                    style={imgStyle}
                    src="https://gw.alipayobjects.com/mdn/rms_7bc6d8/afts/img/A*FPlYQoTNlBEAAAAAAAAAAABkARQnAQ"
                    alt="icon"
                  />
                ),
              }}
            />
            <StatisticCard
              statistic={{
                title: '物流数据',
                value: 1754,
                icon: (
                  <img
                    style={imgStyle}
                    src="https://gw.alipayobjects.com/mdn/rms_7bc6d8/afts/img/A*pUkAQpefcx8AAAAAAAAAAABkARQnAQ"
                    alt="icon"
                  />
                ),
              }}
            />
          </StatisticCard.Group>
        </ProCard>

        <ProCard
          title="数据概览"
          extra={currentDateAndWeekday()}
          split={responsive ? 'horizontal' : 'vertical'}
          headerBordered
          bordered
        >


          <ProCard split="horizontal">
            <ProCard split="vertical">
              <StatisticCard
                statistic={{
                  title: '昨日全部流量',
                  value: 234,
                  description: (
                    <Statistic
                      title="较本月平均流量"
                      value="8.04%"
                      trend="down"
                    />
                  ),
                }}
              />
              <StatisticCard
                statistic={{
                  title: '本月累计流量',
                  value: 234,
                  description: (
                    <Statistic title="月同比" value="8.04%" trend="up" />
                  ),
                }}
              />
            </ProCard>
            <ProCard split="vertical">
              <StatisticCard
                statistic={{
                  title: '运行中实验',
                  value: '12/56',
                  suffix: '个',
                }}
              />
              <StatisticCard
                statistic={{
                  title: '历史实验总数',
                  value: '134',
                  suffix: '个',
                }}
              />
            </ProCard>
          </ProCard>
          <StatisticCard
            title="资产价值趋势"
            chart={
              <img
                src="https://gw.alipayobjects.com/zos/alicdn/_dZIob2NB/zhuzhuangtu.svg"
                width="100%"
              />
            }
          />
        </ProCard>
      </RcResizeObserver>
    </Card>
  );
}