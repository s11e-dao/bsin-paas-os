import React from 'react';
import { Button, message } from 'antd';

function WalletConnect() {
  const handleConnect = () => {
    message.info('钱包连接功能暂未实现');
  };

  return (
    <Button onClick={handleConnect} type="primary">
      同意
    </Button>
  );
}

export default WalletConnect;