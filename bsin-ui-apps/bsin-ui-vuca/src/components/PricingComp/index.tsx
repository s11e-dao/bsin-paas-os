import React from 'react';
import { Card, Row, Col, Button, List, Typography } from 'antd';
import { CheckOutlined } from '@ant-design/icons';
import { history } from 'umi'

export default () => {

  const data = [
    {
      title: '标准版',
      price: '30',
      features: [
        { text: 'Agent数量: 3个', key: '1' },
        { text: '每月Yeah访问: 400次', key: '2' },
        // Add more features...
      ],
    },
    {
        title: '专业版',
        price: '60',
        features: [
          { text: 'Agent数量: 3个', key: '1' },
          { text: '每月Yeah访问: 400次', key: '2' },
          // Add more features...
        ],
      },
      {
        title: '豪华版',
        price: '99',
        features: [
          { text: 'Agent数量: 3个', key: '1' },
          { text: '每月Yeah访问: 400次', key: '2' },
          // Add more features...
        ],
      },
    // ...other pricing options
  ];

  // 登录
  const subscribe = ()=> {
    history.push("/subscribeService")
  }

  return (
    <Row gutter={16}>
      {data.map((plan) => (
        <Col span={8} key={plan.title}>
          <Card title={plan.title} bordered={false}>
            <Typography.Title level={2}>&yen;{plan.price}/月</Typography.Title>
            <Button type="primary" onClick={subscribe}>立即订购</Button>
            <List
              dataSource={plan.features}
              renderItem={item => (
                <List.Item>
                  <CheckOutlined style={{ color: 'green' }} />
                  {item.text}
                </List.Item>
              )}
            />
          </Card>
        </Col>
      ))}
    </Row>
  );
};
