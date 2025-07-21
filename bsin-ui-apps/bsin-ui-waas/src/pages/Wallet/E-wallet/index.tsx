import React, { useState } from 'react';
import Overview from './overview';
import Withdraw from './withdraw';
import Recharge from './recharge';

export default () => {
  
  const [currentContent, setCurrentContent] = useState('overview');
  
  const renderContent = () => {
    switch (currentContent) {
      case 'withdraw':
        return <Withdraw setCurrentContent={setCurrentContent} />;
      case 'recharge':
        return <Recharge setCurrentContent={setCurrentContent} />;
      default:
        return <Overview setCurrentContent={setCurrentContent} />;
    }
  };

  return (
    <div>
      {renderContent()}
    </div>
  );
};
