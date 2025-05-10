import React, { useState } from 'react';
import List from './list';
import Detail from './detail';

export default () => {
  
  const [currentContent, setCurrentContent] = useState('list');
  const [record, setRecord] = useState(null);
  

  const renderContent = () => {
    switch (currentContent) {
      case 'list':
        return <List setCurrentContent={setCurrentContent} />;
      case 'detail':
        return (
          <Detail
            setCurrentContent={setCurrentContent}
            record={record}
          />
        );
      default:
        return (
            <List
            setCurrentContent={setCurrentContent}
          />
        );
    }
  };

  return (
    <div>
      {renderContent()}
    </div>
  );
};
