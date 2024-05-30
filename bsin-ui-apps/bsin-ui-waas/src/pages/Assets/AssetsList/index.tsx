import React, { useState } from 'react';
import {
  Form,
  Tabs,
  Card,
  Button,
  Modal,
  message,
  Popconfirm,
  Descriptions,
  Input,
  Select,
} from 'antd';
import AssetsList from './assetsList';
import ConfigCondition from '../../Customer/ConditionList/configCondition';
import ConfigEquity from '../../Customer/EquityList/configEquity';

export default () => {
  // 控制是否展示详情组件
  const [currentContent, setCurrentContent] = useState('assetsList');
  const [record, setRecord] = useState(null);

  const configAssetsItem = (record, value) => {
    setRecord(record);
    setCurrentContent(value);
  };

  const Conent = () => {
    let conentComp = (
      <AssetsList
        setCurrentContent={setCurrentContent}
        configAssetsItem={configAssetsItem}
      />
    );
    if (currentContent == 'configCondition') {
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
