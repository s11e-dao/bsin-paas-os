import React, { useState } from 'react';
import AssetsCollection from './assetsCollection';
import IssueAssets from './issueAssets';
import PutOnShelvesAssets from './putOnShelvesAssets';
import MarkdownEdit from 'markdown-editor-reactjs';

import Editor from './editor';

export default () => {
  const [currentContent, setCurrentContent] = useState('assetsCollection');
  const [assetsCollectionRecord, setAssetsCollectionRecord] = useState(null);

  const putOnShelves = (record) => {
    setAssetsCollectionRecord(record);
    setCurrentContent('putOnShelvesAssets');
  };

  const Conent = () => {
    let conentComp = (
      <AssetsCollection
        setCurrentContent={setCurrentContent}
        putOnShelves={putOnShelves}
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
