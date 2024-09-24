import React, { useState, useEffect } from 'react'
import { AiAgentManagement } from 'bsin-agent-ui'

import { useModel } from 'umi'

// 样式设计参考：https://fastgpt.run/dataset/list

export default () => {
  // 获取appId
  const { appId } = useModel('@@qiankunStateFromMaster')

  return (
    <>
      <AiAgentManagement></AiAgentManagement>
    </>
  )
}
