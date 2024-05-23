import React, { FC, useEffect, useState, useMemo, useRef } from 'react'

import {
  Button,
  ConfigProvider,
  Divider,
  Dropdown,
  Input,
  Popover,
  theme,
  Drawer,
  Space,
  Modal,
  Switch,
  FloatButton,
  message,
} from 'antd'
import type { DrawerProps } from 'antd'
import './index.less'
import {
  CaretDownFilled,
  DoubleRightOutlined,
  GithubFilled,
  ChromeFilled,
  InfoCircleFilled,
  LogoutOutlined,
  PlusCircleFilled,
  CommentOutlined,
  CustomerServiceOutlined,
  SearchOutlined,
  PieChartOutlined,
  UserOutlined,
  AreaChartOutlined,
  NodeExpandOutlined,
  DotChartOutlined,
  UserSwitchOutlined,
  SubnodeOutlined,
  ShareAltOutlined,
  ScheduleOutlined,
  PrinterOutlined,
  IdcardOutlined,
  DollarOutlined,
  BgColorsOutlined,
} from '@ant-design/icons'

// import { chatData } from '../mocks/threebody'
import { ProChat, ProChatInstance, ChatMessageMap } from '@ant-design/pro-chat'
import { useTheme } from 'antd-style'
import { MockResponse } from '../mocks/streamResponse'
import {
  chatWithCopilot,
  getDefaultCopilot,
  getMerchantDefault,
  getChatHistoryList,
} from '../service'

import bsinBot from '../../assets/image/bsin-bot.svg'

export default ({ customerInfo, defaultCopilot }) => {
  const proChatRef = useRef<ProChatInstance>()
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [size, setSize] = useState<DrawerProps['size']>()
  const [loading, setLoading] = useState(false)

  const showDrawer = () => {
    setSize('large')
    setDrawerOpen(true)
  }

  const onDrawerClose = () => {
    setDrawerOpen(false)
  }

  // useEffect(() => {
  //   historyChatList()
  // }, [])

  const [chats, setChats] = useState<ChatMessageMap>()

  const chatTheme = useTheme()

  const copilotChat = async (question) => {
    let params = {
      copilotNo: defaultCopilot.serialNo,
      quickReplies: true, //返回生成快捷回复
      question: question,
    }
    let resp = await chatWithCopilot(params)
    return resp
  }
  let chatData = {
    chats: {},
    // chats: {
    //   ZGxiX2p4: {
    //     content: '历史聊天记录 user',
    //     createAt: 1697862242452,
    //     id: 'ZGxiX2p4',
    //     role: 'user',
    //     updateAt: 1697862243540,
    //   },
    //   Sb5pAzLL: {
    //     content: '历史聊天记录 assistant',
    //     createAt: 1697862247302,
    //     id: 'Sb5pAzLL',
    //     parentId: 'ZGxiX2p4',
    //     role: 'assistant',
    //     updateAt: 1697862249387,
    //     model: 'gpt-3.5-turbo',
    //   },
    // },
  }

  const historyChatList = async () => {
    let params = {
      receiver: defaultCopilot.serialNo,
      sender: customerInfo.customerNo,
      chatType: 'chat',
    }
    getChatHistoryList(params).then((res) => {
      if (res?.code == 0) {
        let i = 0
        res?.data.map((historyChat) => {
          let historyChatTmp = {
            content: historyChat.message,
            createAt: historyChat.timestamp,
            id: historyChat.sender + customerInfo.customerNo + i,
            // id: 'ZGxiX2p4',
            role:
              historyChat.sender == customerInfo.customerNo
                ? 'user'
                : 'assistant',
            updateAt: 1697862243540,
          }
          let id = historyChat.sender + customerInfo.customerNo + i
          i++
          // chatData.chats.put(1,historyChatTmp)
        })
        setLoading(true)
      }
    })
    console.log(chatData)
  }
  return (
    <>
      {/* bsin-copilot vuca */}
      <FloatButton
        style={{ right: 24 }}
        type="primary"
        icon={<CommentOutlined />}
        onClick={showDrawer}
      />

      <Drawer
        title="bsin app agent"
        onClose={onDrawerClose}
        open={drawerOpen}
        size={size}
      >
        <div style={{ background: chatTheme.colorBgLayout }}>
          <ProChat
            loading={loading}
            style={{ minHeight: '80vh' }}
            showTitle
            // 用户消息
            userMeta={{
              avatar:
                customerInfo?.avatar == null ? bsinBot : customerInfo?.avatar,
              title:
                customerInfo?.username == null
                  ? '访客'
                  : customerInfo?.username,
            }}
            helloMessage={
              '您好，我是' + defaultCopilot?.name + '，有什么可以帮助您呢？'
            }
            actions={{
              render: (defaultDoms) => {
                return [
                  <a
                    key="user"
                    onClick={() => {
                      window.open('https://github.com/ant-design/pro-chat')
                    }}
                  >
                    人工服务
                  </a>,
                  <a
                    key="user"
                    onClick={() => {
                      if (!proChatRef.current) return
                      const messages = proChatRef.current.getChatMessages()
                      const { id, content } = messages[0] || {}

                      if (!id) return
                      proChatRef.current.setMessageContent(id, content + '👋')
                    }}
                  >
                    修改首条消息，添加表情：👋
                  </a>,
                  <a
                    key="user"
                    onClick={async () => {
                      let messages = proChatRef.current.getChatMessages()
                      let params = {
                        receiver: defaultCopilot.serialNo,
                        sender: customerInfo.customerNo,
                        chatType: 'chat',
                      }
                      getChatHistoryList(params).then((res) => {
                        if (res?.code == 0) {
                          let i = 0
                          res?.data.map((historyChat) => {
                            let historyChatTmp = {
                              content: historyChat.message,
                              createAt: historyChat.timestamp,
                              id: historyChat.sender + customerInfo.customerNo + i,
                              // id: 'ZGxiX2p4',
                              role:
                                historyChat.sender == customerInfo.customerNo
                                  ? 'user'
                                  : 'assistant',
                              updateAt: historyChat.timestamp,
                            }
                            i++
                            // chatData.chats.ZGxiX2p4 = historyChatTmp
                            messages.push(historyChatTmp)
                            proChatRef.current?.setMessageContent(
                              historyChatTmp.id,
                              historyChatTmp.content,
                            )
                          })
                          console.log(messages)
                        }
                      })
                    }}
                  >
                    加载历史聊天记录
                  </a>,
                  ...defaultDoms,
                ]
              },
              flexConfig: {
                gap: 24,
                direction: 'horizontal',
                justify: 'space-between',
              },
            }}
            // 聊天记录变化回调函数
            onChatsChange={(chats) => {
              console.log(chats)
              setChats(chats)
            }}
            // 用户消息
            assistantMeta={{
              avatar:
                defaultCopilot?.avatar == null
                  ? bsinBot
                  : defaultCopilot?.avatar,
              title:
                defaultCopilot?.name == null ? '火源兽' : defaultCopilot?.name,
              backgroundColor: '#67dedd',
            }}
            // 初始化历史聊天数据
            initialChats={chatData.chats}
            chatRef={proChatRef}
            request={async (messages) => {
              //   const mockedData: string = `这是一段模拟的流式字符串数据。本次会话传入了${messages.length}条消息， ${messages}`
              //   const mockResponse = new MockResponse(mockedData, 100)
              //   return mockResponse.getResponse()
              const response = await copilotChat(
                messages[messages.length - 1].content,
              )
              if (response.code == 0) {
                return new Response(response?.data?.answer)
              } else {
                return new Response(response?.message)
              }
            }}
          />
        </div>
      </Drawer>
    </>
  )
}
