import React, { useState } from 'react';
import List from './list';
import AppConfig from './appConfig';

export default () => {

  const [currentContent, setCurrentContent] = useState('list');

  const [assetsCollectionRecord, setAssetsCollectionRecord] = useState(null);
  const [record, setRecord] = useState(null);
  
  const configAssetsItem = (record, value) => {
    setRecord(record);
    setCurrentContent(value);
  };

  const Conent = () => {
    let conentComp = (
      <List
        setCurrentContent={setCurrentContent}
      />
    );
    if (currentContent == 'appConfig') {
      conentComp = <AppConfig setCurrentContent={setCurrentContent} />;
    }
    return <>{conentComp}</>;
  };

  return (
    <div>
      <Conent />
    </div>
  );
};
