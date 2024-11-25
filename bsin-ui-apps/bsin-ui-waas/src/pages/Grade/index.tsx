import React, { useState, useEffect } from 'react';
import {
  Card,
  Tabs,
  Button,
  Space,
  Table,
  Tag,
  Form,
  Modal,
  Input,
  Descriptions,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import columnsData, { columnsDataType } from './data';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';

import { getGradeList, addGrade, deleteGrade, getGradeDetail } from './service';

import ConfigCondition from '../conditionAndEquity/ConditionList/configCondition';
import ConfigEquity from '../conditionAndEquity/EquityList/configEquity';
import MemberGrade from './memberGrade';
import GradeList from './gradeList';
import GradeMemberList from './gradeMemberList';

export default () => {
  // 控制是否展示详情组件
  const [currentContent, setCurrentContent] = useState('gradeList');
  const [record, setRecord] = useState(null);

  const configGrade = (record, value) => {
    setRecord(record);
    setCurrentContent(value);
  };

  const Conent = () => {
    let conentComp = (
      <GradeList
        setCurrentContent={setCurrentContent}
        configGrade={configGrade}
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
        <ConfigEquity
          setCurrentContent={setCurrentContent}
          record={record}
        />
      );
    }

    return <>{conentComp}</>;
  };

  return (
    <div>
      <Card>
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab="等级列表" key="1">
            <Conent />
          </Tabs.TabPane>
          <Tabs.TabPane tab="等级会员" key="2">
            <GradeMemberList
              setCurrentContent={setCurrentContent}
              record={record}
            />
          </Tabs.TabPane>
        </Tabs>
      </Card>
    </div>
  );
};
