import React from 'react';
import { Segmented, Tabs, Steps, Card, Button, Space } from 'antd';
import type { TabsProps } from 'antd';


const steps = (
  <Steps
    direction="vertical"
    current={3}
    items={[
      {
        title: '微信小程序认证',
        description: <Space direction="vertical" size="middle" style={{ display: 'flex' }}>
          <p>点击注册打开微信小程序官方，注册微信小程序后点击企业认证，申请之后等待微信官方审核</p>
          <Button type="primary">点击注册</Button>
        </Space>
      },
      {
        title: '微信小程序配置',
        description:
          <Space direction="vertical" size="middle" style={{ display: 'flex' }}>
            <p>微信小程序认证之后按照官方提供的手册进行配置</p>
            <Button type="primary">点击配置微信小程序</Button>
          </Space>
      },
      {
        title: '上传小程序',
        description:
          <Space direction="vertical" size="middle" style={{ display: 'flex' }}>
            <p>版本管理中存放上传的小程序历更版本，通过uni-app版本编译小程序然后打包上传提交</p>
            <Button color="primary" variant="outlined" >版本管理</Button>
          </Space>
      },
      { title: '提交审核', description: "版本管理中存放上传的小程序历更版本，通过uni-app版本编译小程序然后打包上传提交" },
    ]}
  />
);

const tabItems: TabsProps['items'] = [
  { key: '1', label: '接入流程', children: steps },
  { key: '2', label: '消息订阅', children: 'Content of Tab Pane 2' },
  { key: '3', label: '版本管理', children: 'Content of Tab Pane 3' },
];


export default ({ setCurrentContent }) => {

  return (
    <Card
      title="渠道应用配置"
      extra={
        <Button
          type="primary"
          onClick={() => setCurrentContent('list')}
        >
          返回
        </Button>
      }
    >
      {/* 不同渠道应用内容不一样 */}
      <Tabs items={tabItems} />
    </Card>
  );
};
