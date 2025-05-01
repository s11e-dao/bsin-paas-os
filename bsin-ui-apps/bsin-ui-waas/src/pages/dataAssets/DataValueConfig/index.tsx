import React, { useState } from 'react';
import {
  Row,
  Col,
} from 'antd';

import DataCategoryTree from './DataCategoryTree';
import DataLevelTable from './DataLevelTable';

export default () => {


  return (
    <Row gutter={16}>
      <Col span={5}>
        <DataCategoryTree />
      </Col>
      <Col span={19}>
        {/* 重新赋值为orgId*/}
        <DataLevelTable />
      </Col>
    </Row>
  );
};
