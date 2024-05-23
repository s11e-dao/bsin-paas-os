import React from 'react';
import { Button, Space, Card } from 'antd';

import OverviewFlow from './AiWorkflow';



export default function HomePage() {
  return <div>
    <Card
      bodyStyle={{ height: 600 }}
    >
      <OverviewFlow />
    </Card>
  </div>;
}
