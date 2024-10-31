import React, { useState } from 'react';
import List from './list';
import AppConfig from './appConfig';

const CONTENT_COMPONENTS = {
  list: List,
  appConfig: AppConfig,
};

export default () => {
  const [currentContent, setCurrentContent] = useState('list');
  const [record, setRecord] = useState(null);

  const configAssetsItem = (record, value) => {
    setRecord(record);
    setCurrentContent(value);
  };

  const CurrentComponent = CONTENT_COMPONENTS[currentContent];

  return (
    <div>
      {CurrentComponent && (
        <CurrentComponent setCurrentContent={setCurrentContent} record={record} />
      )}
    </div>
  );
};
