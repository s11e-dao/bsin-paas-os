import React from 'react';
import { Button, message } from 'antd';

function Approve() {
  const handleApprove = () => {
    message.info('授权功能暂未实现');
  };

  return (
    <Button onClick={handleApprove} type="primary">
      授权
    </Button>
  );
}

export default Approve;