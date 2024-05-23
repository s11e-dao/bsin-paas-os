import React, { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import { LoadingOutlined } from '@ant-design/icons'
import ProTable from '@ant-design/pro-table'
import {
  Modal,
  Popconfirm,
  message,
  Form,
  Input,
  Divider,
  Spin,
  InputNumber,
} from 'antd'
import {
  getQuickReplies,
  getChatHistoryList,
  getDefaultCopilot,
} from '../../../Chat/service'
import { knowledgeBaseRetrieval } from '../service'
import { getKnowledgeBaseFileList } from '../../service'
import io, { Socket } from 'socket.io-client'
import '@chatui/core/es/styles/index.less'
import '../../../../components/ChatUI/icon.js'

import { Select } from 'antd'
const { Option } = Select
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
  Tabs,
  Tab,
} from '@chatui/core'
import OrderSelector from './OrdderSelector'
// 引入样式
import '@chatui/core/dist/index.css'
import { Layout, Row, Col } from 'antd'
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
  // {
  //   icon: 'image',
  //   name: '图片',
  //   code: 'image',
  //   isNew: true,
  //   isHighlight: true,
  // },
]
const skillList = [
  { title: '话费充值', desc: '智能充值智能充值' },
  { title: '评价管理', desc: '我的评价' },
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

// chatUIProps = {
//   aiName: '聊天对象名字'
//   aiAvatar: 'AI头像'
//   aiNo: 'AI编号'
//   customerNo: '聊天对象名字'
//   customerAvatar: '客户头像'
// }

export default ({ chatUIProps, knowledgeBaseNo }) => {
  const initialMessages: MessageWithoutId[] = [
    {
      type: 'text',
      content: { text: '您好，可以在此Retrival您的知识库' },
      user: {
        avatar:
          chatUIProps?.aiAvatar == null
            ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
            : chatUIProps?.aiAvatar,
      },
    },
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
  const [tabIndex, setTabIndex] = useState(0)

  // 获取表单信息
  const [formRef] = Form.useForm()

  function handleTabChange(i) {
    setTabIndex(i)
  }
  // 消息列表
  const { messages, appendMsg, setTyping, prependMsgs } = useMessages(
    initialMessages,
  )
  const { quickReplies, replace } = useQuickReplies(defaultQuickReplies)
  const msgRef = React.useRef(null)

  const [selectFile, setSelectFile] = useState('')
  const [knowledgeBaseFileList, setKnowledgeBaseFileList] = useState([{}])

  const navigate = useNavigate()
  window.appendMsg = appendMsg
  window.msgRef = msgRef

  // 快捷回复列表
  const [quickRepliy, setQuickReply] = useState(defaultQuickReplies)

  useEffect(() => {
    console.log(chatUIProps)
    console.log(knowledgeBaseNo)
    getKnowledgeBaseFileList({
      knowledgeBaseNo,
    }).then((res)=>{
      setKnowledgeBaseFileList(res.data)
    })
    setSelectFile('receiver')
  }, [])

  // 发送回调
  async function handleSend(type, val) {
    if (type === 'text' && val.trim()) {
      formRef
        .validateFields()
        .then(async () => {
          appendMsg({
            type: 'text',
            content: { text: val },
            user: {
              avatar:
                chatUIProps.customerAvatar == null
                  ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
                  : chatUIProps.customerAvatar,
            },
            position: 'right',
          })
          setTyping(true)

          var formInfo = formRef.getFieldsValue()
          console.log(formInfo)
          let params = {
            knowledgeBaseFileNo: selectFile,
            knowledgeBaseNo: knowledgeBaseNo,
            minScore: formInfo.minScore,
            maxResults: formInfo.maxResults,
            text: val,
            quickReplies: true,
            vectorRet: false,
          }
          knowledgeBaseRetrieval(params).then((res) => {
            let textValue
            let notEmpty = false
            if (res.code == '000000') {
              console.log(res.data)
              console.log(res.data.length)
              if (res.data.length > 0 && res.data[0].score > 0) {
                textValue = res.data
                notEmpty = true
              } else {
                textValue = '未检索到相关内容'
              }
              console.log(res?.data.quickReplyMessages)
              if (res?.data.quickReplyMessages != null) {
                replace(res?.data.quickReplyMessages)
              }
            } else {
              textValue = '召回异常：\n' + res.message
            }
            appendMsg({
              type: notEmpty ? 'retrival-result-tabs' : 'text',
              content: { text: textValue },
              user: {
                avatar:
                  chatUIProps.aiAvatar == null
                    ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
                    : chatUIProps.aiAvatar,
              },
            })
          })
        })
        .catch(() => {
          console.log('获取表单参数失败')
        })
    } else if (type === 'image' && val.trim()) {
      appendMsg({
        type: 'image',
        content: {
          picUrl:
            'https://gw.alicdn.com/tfs/TB1j2Y3xUY1gK0jSZFMXXaWcVXa-602-337.png',
        },
        user: {
          avatar:
            chatUIProps.customerAvatar == null
              ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
              : chatUIProps.customerAvatar,
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
              chatUIProps.aiAvatar == null
                ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
                : chatUIProps.aiAvatar,
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
        console.log(knowledgeBaseNo)
        const now = Date.now()
        let params = {
          receiver: selectFile == 'receiver' ? knowledgeBaseNo : selectFile,
          sender: chatUIProps.customerNo,
          chatType: 'retrieval',
          quickReplies: true, //返回生成快捷回复
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
                    historyChat.sender == chatUIProps.customerNo
                      ? chatUIProps.customerAvatar == null
                        ? '//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg//gw.alicdn.com/tfs/TB1DYHLwMHqK1RjSZFEXXcGMXXa-56-62.svg'
                        : chatUIProps.customerAvatar
                      : chatUIProps.aiAvatar == null
                      ? '//gw.alicdn.com/tfs/TB1Xv5_vlr0gK0jSZFnXXbRRXXa-427-240.png'
                      : chatUIProps.aiAvatar,
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
    const { type, content, score, resultNum } = msg

    // 根据消息类型来渲染
    switch (type) {
      case 'text':
        return <Bubble content={content.text} />
      // 知识库检索结果
      case 'retrival-result-tabs':
        return (
          <div>
            <h3>召回结果: {content.text.length}</h3>
            <Tabs index={tabIndex} scrollable onChange={handleTabChange}>
              {content.text.map((tab) => {
                return (
                  <Tab label={tab.score.toFixed(2)}>
                    <p>{tab.text}</p>
                  </Tab>
                )
              })}
            </Tabs>
          </div>
        )
      case 'retrival-result-list':
        return (
          <Card fluid>
            <Flex>
              <div className="retrival-result">
                <h1>召回结果</h1>
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
                </List>
              </FlexItem>
            </Flex>
          </Card>
        )

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
  const knowledgeBaseFileChange = (value) => {
    console.log(value)
    setSelectFile(value)
  }

  return (
    <div className="content">
      <Form
        name="basic"
        form={formRef}
        labelCol={{ span: 7 }}
        wrapperCol={{ span: 14 }}
        initialValues={{ remember: true }}
      >
        <Form.Item
          label="召回文件选择"
          name="type"
          rules={[{ required: false, message: '请选择要召回的文件!' }]}
        >
          <Select
            style={{ width: '100%' }}
            onChange={(value) => {
              knowledgeBaseFileChange(value)
            }}
            defaultValue="receiver"
          >
            <Option value="receiver">整个知识库</Option>
            {knowledgeBaseFileList.map((knowledgeBaseFile) => {
              return (
                <Option value={knowledgeBaseFile?.serialNo}>
                  {knowledgeBaseFile?.name}
                </Option>
              )
            })}
          </Select>
        </Form.Item>
        <Form.Item label="score" name="minScore" style={{ width: '100%' }}>
          <InputNumber
            placeholder="0~1"
            min={0}
            max={1}
            step={0.1}
            style={{ width: '100%' }}
          />
        </Form.Item>
        <Form.Item label="maxResults" name="maxResults">
          <InputNumber placeholder="最多召回的条数" min={1} step={1} />
        </Form.Item>
      </Form>
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
          title: '知识库召回测试',
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
