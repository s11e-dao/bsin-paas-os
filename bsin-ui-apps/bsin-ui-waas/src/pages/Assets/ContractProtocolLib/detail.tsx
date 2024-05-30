import React, { useState, useEffect } from 'react';
import { Card, Button, Descriptions } from 'antd';
import type { UploadProps } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';

import styles from './index.css';

export default ({ contactProtocolRouter, currentRecord }) => {
  const [docmentContent, setDocmentContent] = useState('');
  const ctx = '# Hello, *world*!';
  useEffect(() => {
    setDocmentContent(ctx);
  }, []);
  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = currentRecord;
    if (type == `1`) {
      return '数字资产';
    } else if (type == `2`) {
      return 'PFP';
    } else if (type == `3`) {
      return '账户-DP';
    } else if (type == `4`) {
      return '数字门票';
    } else if (type == `5`) {
      return 'Pass卡';
    } else if (type == `6`) {
      return '账户-BC';
    } else if (type == `7`) {
      return '满减';
    } else if (type == `8`) {
      return '权限';
    } else if (type == `9`) {
      return '会员等级';
    } else if (type == `10`) {
      return '其他';
    } else {
      return type;
    }
  };

  const handleViewRecordOfCategory = () => {
    // 合约分类： 1、Core 2、Factory 3、Extension 4、Wrapper  5、Proxy  6、Other
    let { category } = currentRecord;
    if (category == `1`) {
      return 'Core';
    } else if (category == `2`) {
      return 'Factory';
    } else if (category == `3`) {
      return 'Extension';
    } else if (category == `4`) {
      return 'Wrapper';
    } else if (category == `5`) {
      return 'Proxy';
    } else if (category == `6`) {
      return 'Other';
    } else {
      return category;
    }
  };
  return (
    <PageContainer>
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
      </Card>

      <Descriptions title="合约协议信息">
        <Descriptions.Item label="租户ID">
          {currentRecord?.tenantId}
        </Descriptions.Item>
        <Descriptions.Item label="协议ID">
          {currentRecord?.serialNo}
        </Descriptions.Item>
        <Descriptions.Item label="合约协议类型">
          {handleViewRecordOfType()}
        </Descriptions.Item>
        <Descriptions.Item label="合约协议分类">
          {handleViewRecordOfCategory()}
        </Descriptions.Item>
        <Descriptions.Item label="封面图片">
          {currentRecord?.coverImage}
        </Descriptions.Item>
        <Descriptions.Item label="合约协议标准">
          {currentRecord?.protocolStandards}
        </Descriptions.Item>
        <Descriptions.Item label="合约协议项目编号">
          {currentRecord?.protocolCode}
        </Descriptions.Item>
        <Descriptions.Item label="协议名称">
          {currentRecord?.protocolName}
        </Descriptions.Item>
        <Descriptions.Item label="链类型">
          {currentRecord?.chainType}
        </Descriptions.Item>
        <Descriptions.Item label="创建者">
          {currentRecord?.createBy}
        </Descriptions.Item>
        <Descriptions.Item label="创建时间">
          {currentRecord?.createTime}
        </Descriptions.Item>
        <Descriptions.Item label="协议描述">
          {currentRecord?.description}
        </Descriptions.Item>
        <Descriptions.Item label="协议版本">
          {currentRecord?.version}
        </Descriptions.Item>
        <Descriptions.Item label="合约协议bytecode">
          {currentRecord?.protocolBytecode}
        </Descriptions.Item>
        <Descriptions.Item label="合约协议abi字符">
          {currentRecord?.protocolAbi}
        </Descriptions.Item>
      </Descriptions>
    </PageContainer>
  );
};
