import React, { useState } from 'react';
import Profile from './profile';
import CreateProfile from './createProfile';
import ConfigCondition from '../conditionAndEquity/ConditionList/configCondition';
import ConfigEquity from '../conditionAndEquity/EquityList/configEquity';

export default () => {
  
  const [currentContent, setCurrentContent] = useState('profile');
  const [record, setRecord] = useState(null);
  
  const configAssetsItem = (record, value) => {
    setRecord(record);
    setCurrentContent(value);
  };

  const renderContent = () => {
    switch (currentContent) {
      case 'createProfile':
        return <CreateProfile setCurrentContent={setCurrentContent} />;
      case 'configCondition':
        return (
          <ConfigCondition
            setCurrentContent={setCurrentContent}
            record={record}
          />
        );
      case 'configEquity':
        return (
          <ConfigEquity setCurrentContent={setCurrentContent} record={record} />
        );
      default:
        return (
          <Profile
            setCurrentContent={setCurrentContent}
            configAssetsItem={configAssetsItem}
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
