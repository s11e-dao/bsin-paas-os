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
  getAppAgent,
  getChatHistoryList,
} from '../service'

import {
  CloudUploadOutlined,
  EllipsisOutlined,
  FireOutlined,
  HeartOutlined,
  PaperClipOutlined,
  PlusOutlined,
  ReadOutlined,
  SmileOutlined,
} from '@ant-design/icons';
import { Badge } from 'antd';

import { createStyles } from 'antd-style';

import {
  Attachments,
  Bubble,
  Conversations,
  Prompts,
  Sender,
  Welcome,
  useXAgent,
  useXChat,
} from '@ant-design/x';

const renderTitle = (icon, title) => (
  <Space align="start">
    {icon}
    <span>{title}</span>
  </Space>
);
const defaultConversationsItems = [
  {
    key: '0',
    label: 'What is bsin-paas app agent?',
  },
];
const useStyle = createStyles(({ token, css }) => {
  return {
    layout: css`
      width: 100%;
      min-width: 340px;
      height: 822px;
      border-radius: ${token.borderRadius}px;
      display: flex;
      background: ${token.colorBgContainer};
      font-family: AlibabaPuHuiTi, ${token.fontFamily}, sans-serif;

      .ant-prompts {
        color: ${token.colorText};
      }
    `,
    menu: css`
      background: ${token.colorBgLayout}80;
      width: 280px;
      height: 100%;
      display: flex;
      flex-direction: column;
    `,
    conversations: css`
      padding: 0 12px;
      flex: 1;
      overflow-y: auto;
    `,
    chat: css`
      height: 100%;
      width: 100%;
      max-width: 700px;
      margin: 0 auto;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      padding: ${token.paddingLG}px;
      gap: 16px;
    `,
    messages: css`
      flex: 1;
    `,
    placeholder: css`
      padding-top: 32px;
    `,
    sender: css`
      box-shadow: ${token.boxShadow};
    `,
    logo: css`
      display: flex;
      height: 72px;
      align-items: center;
      justify-content: start;
      padding: 0 24px;
      box-sizing: border-box;

      img {
        width: 24px;
        height: 24px;
        display: inline-block;
      }

      span {
        display: inline-block;
        margin: 0 8px;
        font-weight: bold;
        color: ${token.colorText};
        font-size: 16px;
      }
    `,
    addBtn: css`
      background: #1677ff0f;
      border: 1px solid #1677ff34;
      width: calc(100% - 24px);
      margin: 0 12px 24px 12px;
    `,
  };
});

const placeholderPromptsItems = [
  {
    key: '1',
    label: renderTitle(
      <FireOutlined
        style={{
          color: '#FF4D4F',
        }}
      />,
      'Hot Topics',
    ),
    description: 'What are you interested in?',
    children: [
      {
        key: '1-3',
        description: `Where is the doc?`,
      },
    ],
  },
  {
    key: '2',
    label: renderTitle(
      <ReadOutlined
        style={{
          color: '#1890FF',
        }}
      />,
      'Design Guide',
    ),
    description: 'How to design a good product?',
    children: [
      {
        key: '2-1',
        icon: <HeartOutlined />,
        description: `Know the well`,
      },
    ],
  },
];
const senderPromptsItems = [
  {
    key: '1',
    description: 'Hot Topics',
    icon: (
      <FireOutlined
        style={{
          color: '#FF4D4F',
        }}
      />
    ),
  },
  {
    key: '2',
    description: 'Design Guide',
    icon: (
      <ReadOutlined
        style={{
          color: '#1890FF',
        }}
      />
    ),
  },
];
const roles = {
  ai: {
    placement: 'start',
    typing: {
      step: 5,
      interval: 20,
    },
    styles: {
      content: {
        borderRadius: 16,
      },
    },
  },
  local: {
    placement: 'end',
    variant: 'shadow',
  },
};

import bsinBot from '../../assets/image/bsin-bot.svg'

export default ({ customerInfo }) => {

  let defaultMerchantNo = process.env.defaultMerchantNo
  
  const proChatRef = useRef<ProChatInstance>()
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [size, setSize] = useState<DrawerProps['size']>()
  const [loading, setLoading] = useState(false)
  const [defaultCopilot, setDefaultCopilot] = useState({})

  const showDrawer = () => {
    setSize('large')
    setDrawerOpen(true)

    let params = {
      merchantNo: defaultMerchantNo,
      type: '1',
    }
    //TODO: 获取租户对应的appAgent
    getAppAgent(params).then((res) => {
      if (res?.code == 0) {
        setDefaultCopilot(res?.data)
        console.log(res?.data)
        let params = {
          receiver: res?.data.serialNo,
          sender: customerInfo?.customerNo,
          chatType: 'chat',
        }
        getChatHistoryList(params).then((res) => {
          console.log(res?.data)
          if (res?.code == 0) {
            let i = 0
            res?.data.map((historyChat) => {
              let historyChatTmp = {
                content: historyChat.message,
                createAt: historyChat.timestamp,
                // id: historyChat.sender + customerInfo.customerNo,
                id: 'ZGxiX2p4',
                role:
                  historyChat.sender == customerInfo.customerNo
                    ? 'user'
                    : 'assistant',
                updateAt: 1697862243540,
              }
              let id = historyChat.sender + customerInfo.customerNo + i
              i++
              chatData.chats.ZGxiX2p4 = historyChatTmp
            })
          }
        })
        console.log(chatData)
      } else {
        message.error('查询默认copilot失败！！！')
      }
    })
    // 查询默认智能体
    // defaultCopilot=
  }

  const onDrawerClose = () => {
    setDrawerOpen(false)
  }

  // useEffect(() => {
  //   historyChatList()
  // }, [])

  // ==================== Style ====================
  const { styles } = useStyle();

  // ==================== State ====================
  const [headerOpen, setHeaderOpen] = React.useState(false);
  const [content, setContent] = React.useState('');
  const [conversationsItems, setConversationsItems] = React.useState(defaultConversationsItems);
  const [activeKey, setActiveKey] = React.useState(defaultConversationsItems[0].key);
  const [attachedFiles, setAttachedFiles] = React.useState([]);

  // ==================== Runtime ====================
  const [agent] = useXAgent({
    request: async ({ message }, { onSuccess }) => {
      onSuccess(`Mock success return. You said: ${message}`);
    },
  });
  const { onRequest, messages, setMessages } = useXChat({
    agent,
  });
  useEffect(() => {
    if (activeKey !== undefined) {
      setMessages([]);
    }
  }, [activeKey]);

  // ==================== Event ====================
  const onSubmit = (nextContent) => {
    if (!nextContent) return;
    onRequest(nextContent);
    setContent('');
  };
  const onPromptsItemClick = (info) => {
    onRequest(info.data.description);
  };
  const onAddConversation = () => {
    setConversationsItems([
      ...conversationsItems,
      {
        key: `${conversationsItems.length}`,
        label: `New Conversation ${conversationsItems.length}`,
      },
    ]);
    setActiveKey(`${conversationsItems.length}`);
  };
  const onConversationClick = (key) => {
    setActiveKey(key);
  };
  const handleFileChange = (info) => setAttachedFiles(info.fileList);

  // ==================== Nodes ====================
  const placeholderNode = (
    <Space direction="vertical" size={16} className={styles.placeholder}>
      <Welcome
        variant="borderless"
        icon="https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*s5sNRo5LjfQAAAAAAAAAAAAADgCCAQ/fmt.webp"
        title="Hello, bsin-paas app agent"
        description="基于bsin-paas和大模型打造的应用智能体~"
        extra={
          <Space>
            <Button icon={<ShareAltOutlined />} />
          </Space>
        }
      />
      <Prompts
        title="Do you want?"
        items={placeholderPromptsItems}
        styles={{
          list: {
            width: '100%',
          },
          item: {
            flex: 1,
          },
        }}
        onItemClick={onPromptsItemClick}
      />
    </Space>
  );
  const items = messages.map(({ id, message, status }) => ({
    key: id,
    loading: status === 'loading',
    role: status === 'local' ? 'local' : 'ai',
    content: message,
  }));
  const attachmentsNode = (
    <Badge dot={attachedFiles.length > 0 && !headerOpen}>
      <Button type="text" icon={<PaperClipOutlined />} onClick={() => setHeaderOpen(!headerOpen)} />
    </Badge>
  );
  const senderHeader = (
    <Sender.Header
      title="Attachments"
      open={headerOpen}
      onOpenChange={setHeaderOpen}
      styles={{
        content: {
          padding: 0,
        },
      }}
    >
      <Attachments
        beforeUpload={() => false}
        items={attachedFiles}
        onChange={handleFileChange}
        placeholder={(type) =>
          type === 'drop'
            ? {
              title: 'Drop file here',
            }
            : {
              icon: <CloudUploadOutlined />,
              title: 'Upload files',
              description: 'Click or drag files to this area to upload',
            }
        }
      />
    </Sender.Header>
  );
  const logoNode = (
    <div className={styles.logo}>
      <img
        src="https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*eco6RrQhxbMAAAAAAAAAAAAADgCCAQ/original"
        draggable={false}
        alt="logo"
      />
      <span>Ant Design X</span>
    </div>
  );

  const chatTheme = useTheme()

  const copilotChat = async (question) => {
    let params = {
      copilotNo: defaultCopilot?.serialNo,
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
        maskClosable={false}
        title="bsin app agent"
        onClose={onDrawerClose}
        open={drawerOpen}
        size={size}
      >
        <div style={{ background: chatTheme.colorBgLayout }}>

          <div className={styles.layout}>

            <div className={styles.chat}>
              {/* 🌟 消息列表 */}
              <Bubble.List
                items={
                  items.length > 0
                    ? items
                    : [
                      {
                        content: placeholderNode,
                        variant: 'borderless',
                      },
                    ]
                }
                roles={roles}
                className={styles.messages}
              />
              {/* 🌟 提示词 */}
              <Prompts items={senderPromptsItems} onItemClick={onPromptsItemClick} />
              {/* 🌟 输入框 */}
              <Sender
                value={content}
                header={senderHeader}
                onSubmit={onSubmit}
                onChange={setContent}
                prefix={attachmentsNode}
                loading={agent.isRequesting()}
                className={styles.sender}
              />
            </div>
          </div>
        </div>
      </Drawer>
    </>
  )
}
