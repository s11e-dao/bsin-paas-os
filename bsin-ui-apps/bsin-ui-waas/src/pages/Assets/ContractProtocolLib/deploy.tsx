import React, { useState, useEffect } from 'react';
import { Card, Button, Descriptions } from 'antd';
import type { UploadProps } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';

import DigitalPoints from '../DigitalPoints/index';
import AssetsCollection from '../AssetsCollection/index';
import Contract from '../Contract/index';

import styles from './index.css';

export default ({ contactProtocolRouter, currentRecord }) => {
  // 控制是否展示详情组件
  const [currentContent, setCurrentContent] = useState('deploy');

  useEffect(() => {
    console.log(currentRecord);
  }, []);

  const toDeployContract = () => {
    if (currentRecord.protocolStandards == 'ERC20') {
      setCurrentContent('digitalPoints');
    } else if (currentRecord.protocolStandards == 'ERC20') {
      setCurrentContent('digitalPoints');
    } else if (
      currentRecord.protocolStandards == 'ERC721' ||
      currentRecord.protocolStandards == 'ERC1155'
    ) {
      setCurrentContent('assetsCollection');
    } else if (currentRecord.protocolStandards == 'Other') {
      setCurrentContent('contract');
    }
  };

  const Conent = () => {
    let conentComp = (
      <PageContainer>
        <Descriptions title="资产发行"></Descriptions>
        <Card style={{ marginBottom: 16 }}>
          <Button
            type="primary"
            onClick={() => {
              contactProtocolRouter('list');
            }}
            className={styles.btn}
          >
            返回
          </Button>
          <Button
            type="primary"
            onClick={() => {
              toDeployContract();
            }}
            className={styles.btn}
          >
            发行
          </Button>
        </Card>
      </PageContainer>
    );
    if (currentContent == 'digitalPoints') {
      conentComp = <DigitalPoints />;
    } else if (currentContent == 'assetsCollection') {
      conentComp = <AssetsCollection />;
    } else if (currentContent == 'contract') {
      conentComp = <Contract />;
    }
    return <>{conentComp}</>;
  };

  return (
    <div>
      <Conent />
    </div>
  );
};
