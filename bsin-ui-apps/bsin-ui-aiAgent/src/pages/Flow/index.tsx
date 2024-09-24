import React from 'react';
import { Button, Space, Card } from 'antd';

import { AiAgentDesign } from 'bsin-agent-ui'


export default function FlowPage() {
  return <div>
    <Card
      bodyStyle={{ height: 800 }}
    >
      <AiAgentDesign />
    </Card>
  </div>;
}
