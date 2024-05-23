import React, { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import { LoadingOutlined } from '@ant-design/icons'
import ProTable from '@ant-design/pro-table'
import { Modal, Popconfirm, message, Form, Input, Divider, Spin } from 'antd'
import SessionList from './SessionList'
import io, { Socket } from 'socket.io-client'
import { Layout, Row, Col } from 'antd'
const { Header, Content, Footer, Sider } = Layout
import ChatUI from '@/components/ChatUI'

import {
  getQuickReplies,
  getChatHistoryList,
  getDefaultCopilot,
} from './service'
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'

export default () => {
  const [customerInfo, setCustomerInfo] = useState({})
  const [merchantInfo, setMerchantInfo] = useState({})
  const [userInformation, setUserInformation] = useState({})
  const [defaultCopilot, setDefaultCopilot] = useState('')
  const [loading, setLoading] = useState(false)

  const [chatUIProps, setChatUIprops] = useState({})

  useEffect(() => {
    const userInformationTmp = getLocalStorageInfo('userInformation')
    setUserInformation(userInformationTmp)
    console.log('userInformation')
    console.log(userInformationTmp)
    const merchantInfoTmp = getLocalStorageInfo('merchantInfo')
    console.log('merchantInfo')
    setMerchantInfo(merchantInfoTmp)
    console.log(merchantInfoTmp)

    const customerInfoTmp = getLocalStorageInfo('customerInfo')
    setCustomerInfo(customerInfoTmp)
    console.log('customerInfo')
    console.log(customerInfoTmp)
    let chatUIPropsTmp = {
      merchantNo: merchantInfoTmp.merchantNo,
      marchantName: merchantInfoTmp.merchantName,
      merchantAvatar: merchantInfoTmp.logoUrl,
      aiNo: String,
      aiAvatar: String,
      customerNo: customerInfoTmp.customerNo,
      customerAvatar: customerInfoTmp.avatar,
    }

    let params = {
      merchantNo: merchantInfoTmp.merchantNo,
    }

    //TODO: 获取商户的默认copilot
    getDefaultCopilot(params).then((res) => {
      if (res?.code == '000000') {
        setDefaultCopilot(res?.data.serialNo)
        chatUIPropsTmp.aiNo = res?.data.serialNo
        chatUIPropsTmp.aiAvatar = res?.data.avatar
      }
    })

    setChatUIprops(chatUIPropsTmp)
  }, [])

  return (
    <Layout className="body">
      {/* <Header
        className="header"
        style={{ background: '#fff', padding: 0, height: 600 }}
      >
        <Chat
          navbar={{ title: '智能助理' }}
          messages={messages}
          renderMessageContent={renderMessageContent}
          quickReplies={defaultQuickReplies}
          onQuickReplyClick={handleQuickReplyClick}
          onSend={handleSend}
        />
      </Header> */}
      <Content style={{ background: 'none', padding: 0, margin: 24 }}>
        <div className="content">
          <Row>
            <Col span="4">
              <SessionList />
            </Col>
            {/* <Col span="10"></Col>
            <div style={{ height: '100%' }} ref={wrapper} />
            </Col> */}
            <Col loading={loading} span="8">
              <ChatUI props={chatUIProps} />
            </Col>
          </Row>
        </div>
      </Content>
    </Layout>
  )
}
