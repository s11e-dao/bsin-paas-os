import React, { useState } from 'react';
import PassCard from './passCard';
import IssuePassCard from './issuePassCard';
import PutOnShelvesAssets from '../AssetsCollection/putOnShelvesAssets';
import ConfigCondition from '../../Customer/ConditionList/configCondition';
import ConfigEquity from '../../Customer/EquityList/configEquity';

export default () => {
  const [currentContent, setCurrentContent] = useState('PassCard');
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
      <PassCard
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
    } else if (currentContent == 'issuePassCard') {
      conentComp = <IssuePassCard setCurrentContent={setCurrentContent} />;
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
