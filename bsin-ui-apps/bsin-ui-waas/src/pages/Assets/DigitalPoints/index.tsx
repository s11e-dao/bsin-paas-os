import React, { useState } from 'react';
import DigitadlPoints from './digitalPoints';
import IssueDigitalPoints from './issueDigitalPoints';
import PutOnShelvesAssets from '../AssetsCollection/putOnShelvesAssets';
import ConfigCondition from '../../conditionAndEquity/ConditionList/configCondition';
import ConfigEquity from '../../conditionAndEquity/EquityList/configEquity';

export default () => {
  const [currentContent, setCurrentContent] = useState('digitalPoints');
  const [assetsCollectionRecord, setAssetsCollectionRecord] = useState(null);
  const [record, setRecord] = useState(null);

  const putOnShelves = (record) => {
    setAssetsCollectionRecord(record);
    setCurrentContent('putOnShelvesAssets');
  };
  const configAssetsItem = (record, value) => {
    setRecord(record);
    setCurrentContent(value);
  };
  const Conent = () => {
    let conentComp = (
      <DigitadlPoints
        setCurrentContent={setCurrentContent}
        putOnShelves={putOnShelves}
        configAssetsItem={configAssetsItem}
      />
    );
    if (currentContent == 'putOnShelvesAssets') {
      conentComp = (
        <PutOnShelvesAssets
          setCurrentContent={setCurrentContent}
          assetsCollectionRecord={assetsCollectionRecord}
        />
      );
    } else if (currentContent == 'issueDigitalPoints') {
      conentComp = <IssueDigitalPoints setCurrentContent={setCurrentContent} />;
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
    <div style={{marginBottom: "20px"}}>
      <Conent />
    </div>
  );
};
