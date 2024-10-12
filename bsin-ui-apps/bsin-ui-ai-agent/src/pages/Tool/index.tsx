import React, { useState } from 'react'
import { Descriptions, Tabs } from 'antd'
import { PageContainer } from '@ant-design/pro-layout'
import ToolList from './toolList'
import WxPlatform from './wxPlatform'
import WxPlatformMenulist from './wxPlatformMenuList'

export default () => {
  const [currentContent, setCurrentContent] = useState('toolList')
  const [
    wxPlatformMenuTemplateRecord,
    setWxPlatformMenuTemplateRecord,
  ] = useState(null)

  const routerChange = (record, components) => {
    setWxPlatformMenuTemplateRecord(record)
    setCurrentContent(components)
  }

  const Conent = () => {
    let conentComp = (
      <PageContainer>
        <Descriptions title="我的工具集合"></Descriptions>
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab="微信平台" key="1">
            <WxPlatform routerChange={routerChange} />
          </Tabs.TabPane>
          <Tabs.TabPane tab="通用工具集" key="3">
            <ToolList />
          </Tabs.TabPane>
        </Tabs>
      </PageContainer>
    )
    if (currentContent == 'wxPlatformMenuEdit') {
      conentComp = (
        <WxPlatformMenulist
          currentRecord={wxPlatformMenuTemplateRecord}
          routerChange={routerChange}
        />
      )
    }
    return <>{conentComp}</>
  }

  return (
    <div>
      <Conent />
    </div>
  )
}
