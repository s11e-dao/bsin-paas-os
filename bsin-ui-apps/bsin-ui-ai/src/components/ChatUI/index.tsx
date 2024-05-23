import React, { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import { LoadingOutlined } from '@ant-design/icons'
import ProTable from '@ant-design/pro-table'
import { Modal, Popconfirm, message, Form, Input, Divider, Spin } from 'antd'
import {
  getQuickReplies,
  getChatHistoryList,
  getDefaultCopilot,
} from '../../pages/Chat/service'
import io, { Socket } from 'socket.io-client'
import '@chatui/core/es/styles/index.less'
import './icon.js'
// 引入组件
import Chat, {
  Bubble,
  MessageProps,
  useMessages,
  QuickReplyItemProps,
  useQuickReplies,
  Card,
  CardMedia,
  CardTitle,
  CardText,
  CardActions,
  Button,
  List,
  ListItem,
  Flex,
  FlexItem,
  ScrollView,
  ToolbarItemProps,
  RateActions,
} from '@chatui/core'
import OrderSelector from './OrdderSelector'
// 引入样式
import '@chatui/core/dist/index.css'
import { Layout, Row, Col } from 'antd'
import { useRequest } from 'umi'
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'
const { Header, Content, Footer, Sider } = Layout

type MessageWithoutId = Omit<MessageProps, '_id'>

// 默认快捷短语，可选
const defaultQuickReplies = [
  // {
  //   icon: 'shopping-bag',
  //   name: '订单选择-购物袋',
  //   code: 'orderSelector',
  //   isHighlight: true,
  // },
  {
    icon: 'message',
    name: '你好',
    code: 'text',
    isNew: true,
    isHighlight: true,
  },
  {
    icon: 'image',
    name: '图片',
    code: 'image',
    isNew: true,
    isHighlight: true,
  },
]
const skillList = [
  { title: '话费充值', desc: '智能充值智能充值' },
  { title: '评价管理', desc: '我的评价' },
  { title: '联系商家', desc: '急速联系' },
  { title: '红包卡券', desc: '使用优惠' },
  { title: '修改地址', desc: '修改地址' },
]

// eslint-disable-next-line @typescript-eslint/no-redeclare
const toolbar: ToolbarItemProps[] = [
  {
    type: 'smile',
    icon: 'smile',
    title: '表情',
  },
  {
    type: 'orderSelector',
    icon: 'shopping-bag',
    title: '宝贝',
  },
  {
    type: 'image',
    icon: 'image',
    title: '图片',
  },
  {
    type: 'camera',
    icon: 'camera',
    title: '拍照',
  },
  {
    type: 'photo',
    title: 'Photo',
    img: '//gw.alicdn.com/tfs/TB1eDjNj.T1gK0jSZFrXXcNCXXa-80-80.png',
  },
]

// props = {
//   aiName: '聊天对象名字'
//   aiAvatar: 'AI头像'
//   aiNo: 'AI编号'
//   customerNo: '聊天对象名字'
//   customerAvatar: '客户头像'
// }

export default ({ props }) => {
  let webScoketUrl = process.env.webScoketUrl

  const initialMessages: MessageWithoutId[] = [
    {
      type: 'text',
      content: { text: '您好，我是' + props.merchantName },
      user: {
        avatar:
          props.aiAvatar == null
            ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
            : props.aiAvatar,
      },
    },
    // {
    //   type: 'image',
    //   content: {
    //     picUrl:
    //       'https://gw.alicdn.com/tfs/TB1j2Y3xUY1gK0jSZFMXXaWcVXa-602-337.png',
    //   },
    // },
    {
      type: 'card',
      content: {
        code: 'promotion',
        data: {
          array: [
            {
              image:
                '//alime-base.oss-cn-beijing.aliyuncs.com/avatar/alime-base.oss-cn-beijing-internal.aliyuncs.com1569811067816-首页推荐卡底图（售前）.jpg',
              toggle:
                'https://gw.alicdn.com/tfs/TB1D79ZXAL0gK0jSZFtXXXQCXXa-100-100.png',
              type: 'recommend',
              list: [
                {
                  title: '收到商品不新鲜怎么办？',
                  hot: true,
                  content: '收到商品不新鲜怎么办？',
                },
                {
                  title: '怎么改配送时间/地址/电话？',
                  hot: true,
                  content: '配送时间/地址/电话错了，怎么修改',
                },
                {
                  title: '我的订单什么时间配送',
                  content: '我的订单什么时间配送',
                },
                {
                  title: '已下单，还能临时加/减商品吗？',
                  content: '已下单，还能临时加/减商品吗？',
                },
              ],
            },
            {
              image:
                'https://gw.alicdn.com/tfs/TB114P3XHY1gK0jSZTEXXXDQVXa-400-372.jpg',
              action: 'send',
              text: '点此学习美食做法',
              type: 'default',
              title: '热门菜谱',
              params: {
                content: '热门菜谱',
              },
            },
            {
              image:
                'https://gw.alicdn.com/tfs/TB1rsT0Xxv1gK0jSZFFXXb0sXXa-400-358.jpg',
              action: 'send',
              text: '看看你家的天气吧',
              type: 'default',
              title: '天气查询',
              params: {
                content: '天气查询',
              },
            },
          ],
        },
      },
    },
  ]

  const wrapper = useRef()

  const clientRef = useRef<Socket | null>(null)
  // 消息列表
  const { messages, appendMsg, setTyping, prependMsgs } = useMessages(
    initialMessages,
  )
  const { quickReplies, replace } = useQuickReplies(defaultQuickReplies)
  const msgRef = React.useRef(null)

  const navigate = useNavigate()
  window.appendMsg = appendMsg
  window.msgRef = msgRef

  // 快捷回复列表
  const [quickRepliy, setQuickReply] = useState(defaultQuickReplies)

  const [customerInfo, setCustomerInfo] = useState({})
  const [merchantInfo, setMerchantInfo] = useState({})
  const [userInformation, setUserInformation] = useState({})
  // const [defaultCopilot, setDefaultCopilot] = useState('')

  useEffect(() => {
    console.log(props)
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

    // //TODO: 获取商户的默认copilot
    // let params = {
    //   merchantNo: merchantInfoTmp.merchantNo,
    // }
    // getDefaultCopilot(params).then((res) => {
    //   if (res?.code == '000000') {
    //     setDefaultCopilot(res?.data.serialNo)
    //   }
    // })

    // 创建客户端实例
    const client = io(webScoketUrl, {
      transports: ['websocket'],
      // 在查询字符串参数中传递 token
      // query: {
      //   token: token所在位置
      // }
    })

    // 监听连接成功的事件
    client.on('connect', () => {
      // 向聊天记录中添加一条消息
      // setMessageList(messageList => [
      //   ...messageList,
      //   { type: 'robot', text: '我现在恭候着您的提问。' }
      // ])
    })

    // 监听收到消息的事件
    client.on('message', (data) => {
      console.log('>>>>收到 socket.io 消息:', data)
    })

    // 将客户端实例缓存到 ref 引用中
    clientRef.current = client

    // 在组件销毁时关闭 socket.io 的连接
    return () => {
      client.close()
    }
  }, [])

  // 发送回调
  async function handleSend(type, val) {
    if (type === 'text' && val.trim()) {
      appendMsg({
        type: 'text',
        content: { text: val },
        user: {
          avatar:
            props.customerAvatar == null
              ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
              : props.customerAvatar,
        },
        position: 'right',
      })

      setTyping(true)
      // 模拟回复消息
      setTimeout(() => {
        appendMsg({
          type: 'text',
          content: { text: '亲，您遇到什么问题啦？请简要描述您的问题~' },
          user: {
            avatar:
              props.aiAvatar == null
                ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
                : props.aiAvatar,
          },
        })
      }, 1000)
    } else if (type === 'image' && val.trim()) {
      appendMsg({
        type: 'image',
        content: {
          picUrl:
            'https://gw.alicdn.com/tfs/TB1j2Y3xUY1gK0jSZFMXXaWcVXa-602-337.png',
        },
        user: {
          avatar:
            props.customerAvatar == null
              ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
              : props.customerAvatar,
        },
        position: 'right',
      })

      setTyping(true)
      // 模拟回复消息
      setTimeout(() => {
        appendMsg({
          type: 'image',
          content: {
            picUrl:
              'https://gw.alicdn.com/tfs/TB1j2Y3xUY1gK0jSZFMXXaWcVXa-602-337.png',
          },
          user: {
            avatar:
              props.aiAvatar == null
                ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
                : props.aiAvatar,
          },
        })
      }, 1000)
    }
  }

  // 快捷短语回调，可根据 item 数据做出不同的操作，这里以发送文本消息为例
  async function handleQuickReplyClick(item) {
    console.log(item)
    handleSend(item.code, item.name)

    let params = {
      current: '1',
      pageSize: '99',
    }
    if (item.code === 'replace') {
      let res = await getQuickReplies(params).then((res) => {
        replace(res?.data)
      })
    } else if (item.code === 'orderSelector') {
      appendMsg({
        type: 'order-selector',
        content: {},
        position: 'pop',
      })
    }
  }

  function handleRefresh() {
    return new Promise((resolve) => {
      setTimeout(async () => {
        const now = Date.now()
        let params = {
          receiver: props.aiNo,
          sender: props.customerNo,
        }
        let historyChatList:
          | MessageProps[]
          | {
              _id: number
              type: string
              content: { text: any }
              user: { avatar: any }
            }[] = []
        let res = await getChatHistoryList(params).then((res) => {
          if (res?.code == '000000') {
            res?.data.map((historyChat) => {
              let historyChatTmp = {
                _id: now,
                type: 'text',
                content: {
                  text: historyChat.message,
                },
                user: {
                  avatar:
                    historyChat.sender == props.customerNo
                      ? props.customerAvatar
                      : props.aiAvatar,
                },
                position:
                  historyChat.sender == chatUIProps.customerNo
                    ? 'right'
                    : 'left',
              }
              historyChatList.push(historyChatTmp)
            })
          }
        })
        prependMsgs(historyChatList)
        resolve({})
      }, 800)
    })
  }

  function handleToolbarClick(item: ToolbarItemProps) {
    console.log('handleToolbarClick:\n')
    console.log(item)
    if (item.type === 'orderSelector') {
      appendMsg({
        type: 'order-selector',
        content: {},
      })
    }
  }

  function renderMessageContent(msg) {
    const { type, content } = msg

    // 根据消息类型来渲染
    switch (type) {
      case 'text':
        return <Bubble content={content.text} />
      case 'guess-you':
        return (
          <Card fluid>
            <Flex>
              <div className="guess-you-aside">
                <h1>猜你想问</h1>
              </div>
              <FlexItem>
                <List>
                  <ListItem
                    content="我的红包退款去哪里?"
                    as="a"
                    rightIcon="chevron-right"
                  />
                  <ListItem
                    content="我的红包退款去哪里?"
                    as="a"
                    rightIcon="chevron-right"
                  />
                  <ListItem
                    content="如何修改评价?"
                    as="a"
                    rightIcon="chevron-right"
                  />
                  <ListItem
                    content="物流问题咨询"
                    as="a"
                    rightIcon="chevron-right"
                  />
                </List>
              </FlexItem>
            </Flex>
          </Card>
        )
      case 'skill-cards':
        return (
          <ScrollView
            className="skill-cards"
            data={skillList}
            fullWidth
            renderItem={(item) => (
              <Card>
                <CardTitle>{item.title}</CardTitle>
                <CardText>{item.desc}</CardText>
              </Card>
            )}
          />
        )
      case 'order-selector':
        return <OrderSelector />
      case 'image':
        return (
          <Bubble type="image">
            <img src={content.picUrl} alt="" />
          </Bubble>
        )
      case 'image-text-button':
        return (
          <Flex>
            <Card fluid>
              <CardMedia image="//gw.alicdn.com/tfs/TB1Xv5_vlr0gK0jSZFnXXbRRXXa-427-240.png" />
              <CardTitle>Card title</CardTitle>
              <CardText>
                如您希望卖家尽快给您发货，可以进入【我的订单】找到该笔交易，点击【提醒发货】或点击【联系卖家】与卖家进行旺旺沟通尽快发货给您哦，若卖家明确表示无法发货，建议您申请退款重新选购更高品质的商品哦商品。申请退款重新选购更高品质的商品哦商品。
              </CardText>
              <CardActions>
                <Button>次要按钮</Button>merchantName
                <Button color="primary">主要按钮</Button>
              </CardActions>
            </Card>
            <RateActions onClick={console.log} />
          </Flex>
        )
      default:
        return null
    }
  }

  return (
    <div className="content">
      <Chat
        elderMode
        onRefresh={handleRefresh}
        navbar={{
          leftContent: {
            icon: 'chevron-left',
            title: 'Back',
            onClick() {
              navigate('/')
            },
          },
          rightContent: [
            {
              icon: 'apps',
              title: 'Applications',
            },
            {
              icon: 'ellipsis-h',
              title: 'More',
            },
          ],
          title: merchantInfo.merchantName + '品牌官',
          desc: '客服热线9510211(7:00-次日1:00)',
          logo:
          chatUIProps.merchantAvatar == null
            ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
            : chatUIProps.merchantAvatar,
          align: 'left',
        }}
        toolbar={toolbar}
        messagesRef={msgRef}
        onToolbarClick={handleToolbarClick}
        recorder={{ canRecord: true }}
        wideBreakpoint="600px"
        rightAction={{ icon: 'compass' }}
        messages={messages}
        renderMessageContent={renderMessageContent}
        quickReplies={quickReplies}
        onQuickReplyClick={handleQuickReplyClick}
        onSend={handleSend}
        onImageSend={() => Promise.resolve()}
        loadMoreText="加载更多"
        placeholder="Type message here......"
        inputOptions={{ rows: 3 }}
      />
    </div>
  )
}
