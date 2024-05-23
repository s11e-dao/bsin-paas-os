import React from 'react';
import { Button, Space, Card } from 'antd';

import AiWorkflow from '../../components/Flow';


export default function FlowPage() {
  return <div>
    <Card
      bodyStyle={{ height: 800 }}
    >
      <AiWorkflow />
    </Card>
  </div>;
}
