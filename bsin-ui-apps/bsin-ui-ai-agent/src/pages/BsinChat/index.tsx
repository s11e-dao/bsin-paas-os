import React, { useState, useEffect } from 'react'
import { Card } from 'antd';

import { ChatBox } from 'bsin-agent-ui'

// 样式设计参考：https://fastgpt.run/dataset/list

export default () => {
  // 获取appId

  return (
    <Card style={{ }}>
      <ChatBox></ChatBox>
    </Card>
  )
}
