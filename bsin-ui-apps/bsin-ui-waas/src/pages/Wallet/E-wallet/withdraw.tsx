import React, { useState } from 'react';
import { Button, Card, Typography, Space, Descriptions } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';

const { Title } = Typography;

interface WithdrawProps {
  setCurrentContent: (content: string) => void;
}

export default ({ setCurrentContent }: WithdrawProps) => {
  return (
    <>
      <Card style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          icon={<ArrowLeftOutlined />} 
          onClick={() => setCurrentContent('overview')}
          style={{ float: 'right' }}
        >
          返回
        </Button>
        <Descriptions title="提现"></Descriptions>
        <div style={{ padding: '40px 0', textAlign: 'center' }}>
          <p>提现功能开发中...</p>
        </div>
      </Card>
    </>
  );
};
