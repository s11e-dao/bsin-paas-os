import React, { useState } from 'react';
import ProtocolList from './list';
import ProtocolDetail from './detail';
import ProtocolDoc from './doc';
import ProtocolDeploy from './deploy';

export default () => {
  // 控制是否展示详情组件
  const [currentContent, setCurrentContent] = useState('list');

  const [currentRecord, setCurrentRecord] = useState(false);

  const contactProtocolRouter = (record, value) => {
    setCurrentContent(value);
    setCurrentRecord(record);
  };

  const Conent = () => {
    let conentComp = (
      <ProtocolList
        addCurrentRecord={currentRecord}
        contactProtocolRouter={contactProtocolRouter}
      />
    );
    if (currentContent == 'detail') {
      conentComp = (
        <ProtocolDetail
          currentRecord={currentRecord}
          contactProtocolRouter={contactProtocolRouter}
        />
      );
    } else if (currentContent == 'doc') {
      conentComp = (
        <ProtocolDoc
          currentRecord={currentRecord}
          contactProtocolRouter={contactProtocolRouter}
        />
      );
    } else if (currentContent == 'deploy') {
      conentComp = (
        <ProtocolDeploy
          currentRecord={currentRecord}
          contactProtocolRouter={contactProtocolRouter}
        />
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
