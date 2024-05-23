import React, { useState, useEffect } from 'react';
import { Button, message, Steps, theme, Card, Radio, Col, Row, Divider } from 'antd';
import { CheckCard } from '@ant-design/pro-components';
import { CheckCircleOutlined } from '@ant-design/icons';
import type { RadioChangeEvent } from 'antd';


const steps = [
  {
    title: '套餐选择',
    content: 'First-content',
  },
  {
    title: '订单确定',
    content: 'Second-content',
  },
  {
    title: '订阅完成',
    content: 'Last-content',
  },
];

export default function () {
  const { token } = theme.useToken();
  const [current, setCurrent] = useState(0);

  const next = () => {
    setCurrent(current + 1);
  };

  const prev = () => {
    setCurrent(current - 1);
  };

  const items = steps.map((item) => ({ key: item.title, title: item.title }));

  const [value, setValue] = useState(1);

  const onChange = (e: RadioChangeEvent) => {
    console.log('radio checked', e.target.value);
    setValue(e.target.value);
  };

  return (
    <div style={{ marginTop: "150px" }}>
      <Steps style={{ margin: "auto", marginBottom: "30px", width: "600px" }} current={current} items={items} />
      <Card>
      <Divider orientation="left" orientationMargin="0">套餐</Divider>
        <CheckCard.Group style={{ width: '100%' }} size="small">
          <Row>
            <Col span={8}>
              <CheckCard
              style={{width: "90%"}}
                title="Card A"
                description="This is the description"
                value="A"
              />
            </Col>
            <Col span={8}>
              <CheckCard
                style={{width: "90%"}}
                title="Card B"
                description="This is the description"
                value="B"
              />
            </Col>
            <Col span={8}>
              <CheckCard
              style={{width: "90%"}}
                title="Card C"
                description="This is the description"
                value="C"
              />
            </Col>
          </Row>
        </CheckCard.Group>
        <Divider orientation="left" orientationMargin="0">时长</Divider>
        <Radio.Group onChange={onChange} value={value}>
          <Radio value={1}>1个月</Radio>
          <Radio value={2}>3个月</Radio>
          <Radio value={3}>6个月</Radio>
          <Radio value={4}>12个月</Radio>
          <Radio value={5}>其他</Radio>
        </Radio.Group>
      </Card>
      <div style={{ marginTop: 24, textAlign: "right" }}>
        {current < steps.length - 2 && (
          <Button type="primary" onClick={() => next()}>
            立即下单
          </Button>
        )}
        {current === steps.length - 1 && (
          <Button type="primary" onClick={() => message.success('Processing complete!')}>
            完成
          </Button>
        )}
        {current > 0 && current < steps.length - 1 && (
          <div>

            <Button style={{ margin: '0 8px' }} onClick={() => prev()}>
              上一步
            </Button>
            <Button type="primary" onClick={() => next()}>
              确认订单
            </Button>
          </div>


        )}
      </div>
    </div>
  );
};
