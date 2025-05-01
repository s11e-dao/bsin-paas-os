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
        <h1 style={{ marginBottom: "0" }}>RWA数字资产总览</h1>
        <p style={{ color: "#6b7280" }} className="text-gray-500 mb-4">实时监控链上真实世界资产的表现与趋势</p>
      </header>

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
                title: '会员卡数',
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
                title: 'IP集合数',
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
                title: '徽章总数',
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
                title: '数字分身',
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