import React, { useState } from 'react'
import CopilotList from './list'
import CopilotDetail from './detail'

import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '../../utils/localStorageInfo'
export default () => {
  // 控制是否展示详情组件
  const [isLoadCopilotDetail, setIsLoadCopilotDetail] = useState(false)

  const [currentRecord, setCurrentRecord] = useState(false)
  const [copilotList, setCopilotList] = useState([])
  const [chatUIProps, setChatUIprops] = useState({})

  const addCurrentRecord = (record) => {
    setIsLoadCopilotDetail(true)
    setCurrentRecord(record)
    const userInformationTmp = getLocalStorageInfo('userInfo')
    console.log('userInformation')
    console.log(userInformationTmp)
    const merchantInfoTmp = getLocalStorageInfo('merchantInfo')
    console.log('merchantInfo')
    console.log(merchantInfoTmp)

    const customerInfoTmp = getLocalStorageInfo('customerInfo')
    console.log('customerInfo')
    console.log(customerInfoTmp)
    let chatUIPropsTmp = {
      merchantNo: merchantInfoTmp.serialNo,
      marchantName: merchantInfoTmp.merchantName,
      merchantAvatar: merchantInfoTmp.logoUrl,
      aiNo: record.serialNo,
      aiAvatar: record.avatar,
      customerNo: customerInfoTmp.customerNo,
      customerAvatar: customerInfoTmp.avatar,
    }
    console.log(chatUIPropsTmp)
    setChatUIprops(chatUIPropsTmp)
  }
  const addCopilotList = (copilotList) => {
    setCopilotList(copilotList)
  }
  return (
    <div>
      {isLoadCopilotDetail ? (
        <CopilotDetail
          currentRecord={currentRecord}
          setIsLoadCopilotDetail={setIsLoadCopilotDetail}
          copilotList={copilotList}
          chatUIProps={chatUIProps}
        />
      ) : (
        <CopilotList
          addCurrentRecord={addCurrentRecord}
          addCopilotList={addCopilotList}
        />
      )}
    </div>
  )
}
