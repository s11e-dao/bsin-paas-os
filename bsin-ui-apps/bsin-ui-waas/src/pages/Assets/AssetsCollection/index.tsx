import React, { useState } from 'react';
import AssetsCollection from './assetsCollection';
import IssueAssets from './issueAssets';
import PutOnShelvesAssets from './putOnShelvesAssets';
import ConfigEquity from '../../ConditionAndEquity/EquityList/configEquity';

import Editor from './editor';

export default () => {
  const [currentContent, setCurrentContent] = useState('assetsCollection');
  const [assetsCollectionRecord, setAssetsCollectionRecord] = useState(null);

  const putOnShelves = (record: any) => {
    setAssetsCollectionRecord(record);
    setCurrentContent('putOnShelvesAssets');
  };

  const configEquity = (record: any, type: string) => {
    setAssetsCollectionRecord(record);
    setCurrentContent('configEquity');
  };

  const Conent = () => {
    let conentComp = (
      <AssetsCollection
        setCurrentContent={setCurrentContent}
        putOnShelves={putOnShelves}
        configEquity={configEquity}
      />
    );
    if (currentContent == 'putOnShelvesAssets') {
      conentComp = (
        <PutOnShelvesAssets
          setCurrentContent={setCurrentContent}
          assetsCollectionRecord={assetsCollectionRecord}
        />
      );
    } else if (currentContent == 'issueAssets') {
      conentComp = <IssueAssets setCurrentContent={setCurrentContent} />;
    } else if (currentContent == 'editor') {
      conentComp = <Editor />;
    } else if (currentContent == 'configEquity') {
      conentComp = (
        <ConfigEquity 
          setCurrentContent={setCurrentContent} 
          record={assetsCollectionRecord} 
        />
      );
    }

    return <>{conentComp}</>;
  };

  return (
    <div>
      {/* CastingDetail组件 Casting铸造组件*/}
      <Conent />
    </div>
  );
};
