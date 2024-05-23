import React, { useState } from 'react'
import { Descriptions, Tabs } from 'antd'
import { PageContainer } from '@ant-design/pro-layout'
import FunctionSubscribe from './functionSubscribe'
import WechatMonitor from './wechatMonitor'

export default () => {
  return (
    <div>
      <PageContainer>
        <Descriptions title="系统管理"></Descriptions>
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab="服务订阅审核" key="1">
            <FunctionSubscribe />
          </Tabs.TabPane>
          <Tabs.TabPane tab="微信平台" key="2">
            <WechatMonitor />
          </Tabs.TabPane>
        </Tabs>
      </PageContainer>
    </div>
  )
}
