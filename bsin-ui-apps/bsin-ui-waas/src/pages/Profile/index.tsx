import React, { useState } from 'react';
import Profile from './profile';
import CreateProfile from './createProfile';
import ConfigCondition from '../conditionAndEquity/ConditionList/configCondition';
import ConfigEquity from '../conditionAndEquity/EquityList/configEquity';

export default () => {
  const [currentContent, setCurrentContent] = useState('profile');
  const [assetsCollectionRecord, setAssetsCollectionRecord] = useState(null);
  const [record, setRecord] = useState(null);
  
  const configAssetsItem = (record, value) => {
    setRecord(record);
    setCurrentContent(value);
  };

  const Conent = () => {
    let conentComp = (
      <Profile
        setCurrentContent={setCurrentContent}
        configAssetsItem={configAssetsItem}
      />
    );
    if (currentContent == 'createProfile') {
      conentComp = <CreateProfile setCurrentContent={setCurrentContent} />;
    } else if (currentContent == 'configCondition') {
      conentComp = (
        <ConfigCondition
          setCurrentContent={setCurrentContent}
          record={record}
        />
      );
    } else if (currentContent == 'configEquity') {
      conentComp = (
        <ConfigEquity setCurrentContent={setCurrentContent} record={record} />
      );
    }

    return <>{conentComp}</>;
  };

  return (
    <div>
      <Conent />
    </div>
  );
};
