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
import AccountJournalList from './accountJournalList';
import AccountFreezeJournalList from './accountFreezeJournalList';

export default () => {
  // 控制是否展示详情组件
  const [currentContent, setCurrentContent] = useState('accountJournalList');
  const [record, setRecord] = useState(null);

  return (
    <div>
      <Card>
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab="账号流水" key="1">
            <AccountJournalList />
          </Tabs.TabPane>
          <Tabs.TabPane tab="账号冻结流水" key="2">
            <AccountFreezeJournalList />
          </Tabs.TabPane>
        </Tabs>
      </Card>
    </div>
  );
};
