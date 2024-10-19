import React, { useState } from 'react';
import { Descriptions, Tabs } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import ToolList from './toolList';
import WxPlatform from './wxPlatform';
import WxPlatformMenulist from './wxPlatformMenuList';

const contentMap = {
  toolList: (
    <PageContainer>
      <Descriptions title="我的工具集合" />
      <Tabs defaultActiveKey="1">
        <Tabs.TabPane tab="微信平台" key="1">
          <WxPlatform />
        </Tabs.TabPane>
        <Tabs.TabPane tab="通用工具集" key="3">
          <ToolList />
        </Tabs.TabPane>
      </Tabs>
    </PageContainer>
  ),
  wxPlatformMenuEdit: (record, routerChange) => (
    <WxPlatformMenulist currentRecord={record} routerChange={routerChange} />
  ),
};

export default () => {
  const [currentContent, setCurrentContent] = useState('toolList');
  const [wxPlatformMenuTemplateRecord, setWxPlatformMenuTemplateRecord] = useState(null);

  const routerChange = (record) => {
    setWxPlatformMenuTemplateRecord(record);
    setCurrentContent('wxPlatformMenuEdit');
  };

  const renderContent = () => {
    const content = contentMap[currentContent];
    return typeof content === 'function' 
      ? content(wxPlatformMenuTemplateRecord, routerChange) 
      : content;
  };

  return <div>{renderContent()}</div>;
};
