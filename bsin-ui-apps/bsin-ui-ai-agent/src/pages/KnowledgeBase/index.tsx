import React, { useState } from 'react'
import KnowledgeBaseList from './list'
import KnowledgeBaseDetail from './detail'

import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '../../utils/localStorageInfo'

export default () => {
  
  // 控制是否展示详情组件
  const [isLoadKnowledgeBaseDetail, setIsLoadKnowledgeBaseDetail] = useState(
    false,
  )

  const [currentRecord, setCurrentRecord] = useState(false)
  const [knowledgeBaseList, setKnowledgeBaseList] = useState([])
  const [chatUIProps, setChatUIprops] = useState({})

  const addCurrentRecord = (record) => {
    setIsLoadKnowledgeBaseDetail(true)
    setCurrentRecord(record)

    const userInformationTmp = getLocalStorageInfo('userInfo')
    console.log('userInformation')
    console.log(userInformationTmp)
    const platformInfoTmp = getLocalStorageInfo('platformInfo')
    console.log('platformInfo')
    console.log(platformInfoTmp)
    const merchantInfoTmp = getLocalStorageInfo('merchantInfo')
    console.log('merchantInfo')
    console.log(merchantInfoTmp)

    let chatUIPropsTmp = {
      merchantNo: platformInfoTmp?.serialNo || merchantInfoTmp?.serialNo,
      marchantName: platformInfoTmp?.platformName || merchantInfoTmp?.merchantName,
      merchantAvatar: merchantInfoTmp?.logoUrl,
      aiNo: record.serialNo,
      aiAvatar: record.coverImage,
      customerNo: userInformationTmp.customerNo,
      customerAvatar: userInformationTmp.avatar,
    }
    setChatUIprops(chatUIPropsTmp)
  }
  const addKnowledgeBaseList = (knowledgeBaseList) => {
    setKnowledgeBaseList(knowledgeBaseList)
  }
  const addChatUIProps = (chatUIProps) => {
    setChatUIprops(chatUIProps)
  }
  return (
    <div>
      {isLoadKnowledgeBaseDetail ? (
        <KnowledgeBaseDetail
          currentRecord={currentRecord}
          setIsLoadKnowledgeBaseDetail={setIsLoadKnowledgeBaseDetail}
          knowledgeBaseList={knowledgeBaseList}
          chatUIProps={chatUIProps}
        />
      ) : (
        <KnowledgeBaseList
          addCurrentRecord={addCurrentRecord}
          addKnowledgeBaseList={addKnowledgeBaseList}
          addChatUIProps={addChatUIProps}
        />
      )}
    </div>
  )
}
