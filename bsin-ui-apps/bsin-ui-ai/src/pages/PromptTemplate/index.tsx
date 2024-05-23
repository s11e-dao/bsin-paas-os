import React, { useState } from 'react'
import PromptTemplateList from './promptTemplateList'
import PromptTemplateDetail from './promptTemplateDetail'

import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '../../utils/localStorageInfo'
export default () => {
  // 控制是否展示详情组件
  const [isLoadPromptTemplateDetail, setIsLoadPromptTemplateDetail] = useState(
    false,
  )

  const [currentRecord, setCurrentRecord] = useState(false)
  const [promptTemplateList, setPromptTemplateList] = useState([])

  const addCurrentRecord = (record) => {
    setIsLoadPromptTemplateDetail(true)
    setCurrentRecord(record)
  }
  const addPromptTemplateList = (promptTemplateList) => {
    setPromptTemplateList(promptTemplateList)
  }
  return (
    <div>
      {isLoadPromptTemplateDetail ? (
        <PromptTemplateDetail
          currentRecord={currentRecord}
          setIsLoadPromptTemplateDetail={setIsLoadPromptTemplateDetail}
          promptTemplateList={promptTemplateList}
        />
      ) : (
        <PromptTemplateList
          addCurrentRecord={addCurrentRecord}
          addPromptTemplateList={addPromptTemplateList}
        />
      )}
    </div>
  )
}
