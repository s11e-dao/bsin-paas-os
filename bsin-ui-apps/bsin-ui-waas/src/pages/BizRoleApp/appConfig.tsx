import React from 'react';
import { Segmented, Tabs, Steps, Card, Button } from 'antd';
import type { TabsProps } from 'antd';

const description = 'This is a description.';

const step = <Steps
  direction="vertical"
  current={1}
  items={[
    {
      title: 'Finished',
      description,
    },
    {
      title: 'In Progress',
      description,
    },
    {
      title: 'Waiting',
      description,
    },
  ]}
/>

const items: TabsProps['items'] = [
  { key: '1', label: '接入流程', children: step },
  { key: '2', label: '消息订阅', children: 'Content of Tab Pane 2' },
  { key: '3', label: '版本管理', children: 'Content of Tab Pane 3' },
];

type Align = '微信小程序' | '微信公众号' | 'app原生应用';


export default ({ setCurrentContent }) => {

  const [alignValue, setAlignValue] = React.useState<Align>('微信小程序');

  const [activeTab, setActiveTab] = React.useState('1');

  const onChange = (key: string) => {
    console.log(key);
    setActiveTab(key)
  };


  return (
    <>
      <Card title="渠道应用配置" extra={<Button type="primary"
        onClick={async () => {
          console.log('res');
          setCurrentContent('list');
        }}
      >返回</Button>} style={{}}>
        <Segmented
          value={alignValue}
          style={{ marginBottom: 8 }}
          onChange={setAlignValue}
          options={['微信小程序', '微信公众号', 'app原生应用']}
        />
        <Tabs
          items={items}
          activeKey={activeTab}
          onChange={onChange}
        />
      </Card>


    </>
  );

};
