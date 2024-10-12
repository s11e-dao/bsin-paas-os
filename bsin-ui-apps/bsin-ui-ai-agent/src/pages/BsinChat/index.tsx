import React, { useState, useEffect } from 'react'
import { ProChat } from '@ant-design/pro-chat';
import { chats } from './mocks/threebody';
import { Card } from 'antd';

// 样式设计参考：https://fastgpt.run/dataset/list

export default () => {
  // 获取appId

  return (
    <Card style={{ }}>
      <ProChat
        style={{ minHeight: "72vh" }}
        showTitle
        userMeta={{
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg',
          title: 'Ant Design',
        }}
        assistantMeta={{ avatar: '🛸', title: '三体世界', backgroundColor: '#67dedd' }}
        initialChats={chats.chats}
      // request={async (messages) => {
      //   const response = await fetch('/api/openai', {
      //     method: 'POST',
      //     body: JSON.stringify({ messages: messages }),
      //   });
      //   return response;
      // }}
      />
    </Card>
  )
}
