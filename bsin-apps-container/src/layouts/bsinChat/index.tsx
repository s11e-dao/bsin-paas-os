import {
  AppstoreAddOutlined,
  CloudUploadOutlined,
  CommentOutlined,
  CopyOutlined,
  DeleteOutlined,
  UserOutlined,
  EditOutlined,
  EllipsisOutlined,
  FileSearchOutlined,
  HeartOutlined,
  LikeOutlined,
  PaperClipOutlined,
  PlusOutlined,
  ProductOutlined,
  QuestionCircleOutlined,
  ReloadOutlined,
  ScheduleOutlined,
  ShareAltOutlined,
  SmileOutlined,
  OpenAIFilled,
  ApiOutlined, LinkOutlined, SearchOutlined
} from '@ant-design/icons';
import {
  Attachments,
  Bubble,
  Conversations,
  Prompts,
  Sender,
  Suggestion,
  Welcome,
  useXAgent,
  useXChat,
} from '@ant-design/x';
import { Avatar, Button, Flex, Space, Spin, message, theme, Divider, Switch, Dropdown, FloatButton, Drawer } from 'antd';
import type { DrawerProps } from 'antd'
import { createStyles } from 'antd-style';
import dayjs from 'dayjs';
import React, { useEffect, useRef, useState } from 'react';
import { getSessionStorageInfo, getLocalStorageInfo } from '@/utils/localStorageInfo';
import WebSocketManager from '@/utils/WebSocketManager';
import {
  chatWithCopilot,
  getAppAgent,
  getChatHistoryList,
} from '../service'

const items = [
  {
      label: (
          <>
              <Flex align="center" justify="space-between" style={{ width: '100%' }}>
                  <span>天气查询</span>
                  <Switch defaultChecked onChange={(checked) => console.log('Switch changed:', checked)} />
              </Flex>
          </>
      ),
      key: '0',
  },
  {
      label: (
          <>
              <Flex align="center" justify="space-between" style={{ width: '100%' }}>
                  <span>会员数据查询</span>
                  <Switch defaultChecked onChange={(checked) => console.log('Switch changed:', checked)} />
              </Flex>
          </>
      ),
      key: '1',
  },
  {
      label: (
          <>
              <Flex align="center" justify="space-between" style={{ width: '100%' }}>
                  <span>曲线值计算</span>
                  <Switch defaultChecked onChange={(checked) => console.log('Switch changed:', checked)} />
              </Flex>
          </>
      ),
      key: '3',
  },
];

const agentItem = [
  {
      label: (
          <>
              <Flex align="center" justify="space-between" style={{ width: '100%' }}>
                  <span>绘画师</span>
              </Flex>
          </>
      ),
      key: '0',
  },
  {
      label: (
          <>
              <Flex align="center" justify="space-between" style={{ width: '100%' }}>
                  <span>品牌官</span>
              </Flex>
          </>
      ),
      key: '1',
  },
  {
      label: (
          <>
              <Flex align="center" justify="space-between" style={{ width: '100%' }}>
                  <span>数字分身</span>
              </Flex>
          </>
      ),
      key: '3',
  },
];

const promptsItem = [
  {
      label: (
          <>
              <Flex align="center" justify="space-between" style={{ width: '100%' }}>
                  <span>数据图表</span>
              </Flex>
          </>
      ),
      key: '0',
  },
  {
      label: (
          <>
              <Flex align="center" justify="space-between" style={{ width: '100%' }}>
                  <span>诙谐幽默</span>
              </Flex>
          </>
      ),
      key: '1',
  },
  {
      label: (
          <>
              <Flex align="center" justify="space-between" style={{ width: '100%' }}>
                  <span>论文</span>
              </Flex>
          </>
      ),
      key: '3',
  },
];


const suggestions = [
  { label: 'Write a report', value: 'report' },
  { label: 'Draw a picture', value: 'draw' },
  {
      label: 'Check some knowledge',
      value: 'knowledge',
      icon: <OpenAIFilled />,
      children: [
          {
              label: 'About React',
              value: 'react',
          },
          {
              label: 'About Ant Design',
              value: 'antd',
          },
      ],
  },
];

const DEFAULT_CONVERSATIONS_ITEMS = [
  {
      key: 'default-0',
      label: 'What is bsin app agent?',
      group: 'Today',
  },
  {
      key: 'default-1',
      label: 'How to quickly install and import components?',
      group: 'Today',
  },
  {
      key: 'default-2',
      label: 'New AGI Hybrid Interface',
      group: 'Yesterday',
  },
];

const HOT_TOPICS = {
  key: '1',
  label: 'Hot Topics',
  children: [
      {
          key: '1-1',
          description: 'What has bsin app agent upgraded?',
          icon: <span style={{ color: '#f93a4a', fontWeight: 700 }}>1</span>,
      },
      {
          key: '1-2',
          description: 'New AGI Hybrid Interface',
          icon: <span style={{ color: '#ff6565', fontWeight: 700 }}>2</span>,
      },
      {
          key: '1-3',
          description: 'What components are in bsin app agent?',
          icon: <span style={{ color: '#ff8f1f', fontWeight: 700 }}>3</span>,
      },
      {
          key: '1-4',
          description: 'Come and discover the new design paradigm of the AI era.',
          icon: <span style={{ color: '#00000040', fontWeight: 700 }}>4</span>,
      },
      {
          key: '1-5',
          description: 'How to quickly install and import components?',
          icon: <span style={{ color: '#00000040', fontWeight: 700 }}>5</span>,
      },
  ],
};

const DESIGN_GUIDE = {
  key: '2',
  label: 'Design Guide',
  children: [
      {
          key: '2-1',
          icon: <HeartOutlined />,
          label: 'Intention',
          description: 'AI understands user needs and provides solutions.',
      },
      {
          key: '2-3',
          icon: <CommentOutlined />,
          label: 'Chat',
          description: 'How AI Can Express Itself in a Way Users Understand',
      },
      {
          key: '2-4',
          icon: <PaperClipOutlined />,
          label: 'Interface',
          description: 'AI balances "chat" & "do" behaviors.',
      },
  ],
};

const SENDER_PROMPTS = [
  {
      key: '1',
      description: '说明文档',
      icon: <ScheduleOutlined />,
  },
  {
      key: '2',
      description: '热门话题',
      icon: <ProductOutlined />,
  }
];

const useStyle = createStyles(({ token, css }) => {
  return {
      layout: css`
      width: 100%;
      min-width: 460px;
      height: 70vh;
      display: flex;
      background: ${token.colorBgContainer};
      font-family: AlibabaPuHuiTi, ${token.fontFamily}, sans-serif;
    `,
      // sider 样式
      sider: css`
      background: ${token.colorBgLayout}80;
      width: 280px;
      height: 100%;
      display: flex;
      flex-direction: column;
      padding: 0 12px;
      box-sizing: border-box;
    `,
      logo: css`
      display: flex;
      align-items: center;
      justify-content: start;
      padding: 0 24px;
      box-sizing: border-box;
      gap: 8px;
      margin: 24px 0;

      span {
        font-weight: bold;
        color: ${token.colorText};
        font-size: 16px;
      }
    `,
      addBtn: css`
      background: #1677ff0f;
      border: 1px solid #1677ff34;
      height: 40px;
    `,
      conversations: css`
      flex: 1;
      overflow-y: auto;
      margin-top: 12px;
      padding: 0;

      .ant-conversations-list {
        padding-inline-start: 0;
      }
    `,
      siderFooter: css`
      border-top: 1px solid ${token.colorBorderSecondary};
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    `,
      // chat list 样式
      chat: css`
      height: 100%;
      width: 100%;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      padding-block: ${token.paddingLG}px;
      gap: 16px;
    `,
      chatPrompt: css`
      .ant-prompts-label {
        color: #000000e0 !important;
      }
      .ant-prompts-desc {
        color: #000000a6 !important;
        width: 100%;
      }
      .ant-prompts-icon {
        color: #000000a6 !important;
      }
    `,
      chatList: css`
      flex: 1;
      overflow: auto;
    `,
      loadingMessage: css`
      background-image: linear-gradient(90deg, #ff6b23 0%, #af3cb8 31%, #53b6ff 89%);
      background-size: 100% 2px;
      background-repeat: no-repeat;
      background-position: bottom;
    `,
      placeholder: css`
      padding-top: 32px;
    `,
      // sender 样式  
      sender: css`
      width: 100%;
      margin: 0 auto;
    `,
      speechButton: css`
      font-size: 18px;
      color: ${token.colorText} !important;
    `,
      senderPrompt: css`
      width: 100%;
      margin: 0 auto;
      color: ${token.colorText};
    `,
  };
});


let token = getSessionStorageInfo('token')?.token;
let webScoketUrl = process.env.webScoketUrl;
const wsManager = new WebSocketManager(webScoketUrl, token);
let mockSuccess = false;

var __awaiter =
  (this && this.__awaiter) ||
  function (thisArg, _arguments, P, generator) {
      function adopt(value) {
          return value instanceof P
              ? value
              : new P(function (resolve) {
                  resolve(value);
              });
      }
      return new (P || (P = Promise))(function (resolve, reject) {
          function fulfilled(value) {
              try {
                  step(generator.next(value));
              } catch (e) {
                  reject(e);
              }
          }
          function rejected(value) {
              try {
                  step(generator['throw'](value));
              } catch (e) {
                  reject(e);
              }
          }
          function step(result) {
              result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected);
          }
          step((generator = generator.apply(thisArg, _arguments || [])).next());
      });
  };

const Independent = ({ customerInfo }) => {

  const { styles } = useStyle();
  const abortController = useRef(null);

  const [deepThinking, setDeepThinking] = useState(false);
  const [globalSearch, setGlobalSearch] = useState(false);
  const { token } = theme.useToken();

  const iconStyle = {
      fontSize: 14,
      color: token.colorText,
  };

  // ==================== State ====================
  const [messageHistory, setMessageHistory] = useState({});

  const [conversations, setConversations] = useState(DEFAULT_CONVERSATIONS_ITEMS);
  const [curConversation, setCurConversation] = useState(DEFAULT_CONVERSATIONS_ITEMS[0].key);

  const [attachmentsOpen, setAttachmentsOpen] = useState(false);
  const [attachedFiles, setAttachedFiles] = useState([]);

  const [inputValue, setInputValue] = useState('');

  const [agentDropdownOpen, setAgentDropdownOpen] = useState(false);
  const [mcpDropdownOpen, setMcpDropdownOpen] = useState(false);
  const [promptsDropdownOpen, setPromptsDropdownOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const [messageId, setMessageId] = useState(1);
  // const [messages, setMessages] = useState < MessageInfo < string > [] > ([]); // Ensure messages state is defined

  // ==================== Runtime ====================
  const roles = {
      ai: {
          placement: 'start',
          avatar: { icon: <UserOutlined />, style: { background: '#fde3cf' } },
          typing: { step: 5, interval: 20 },
          style: {
              maxWidth: 600,
          },
      },
      local: {
          placement: 'end',
          avatar: { icon: <UserOutlined />, style: { background: '#87d068' } },
      },
  };
  const sleep = () => new Promise(resolve => setTimeout(resolve, 1000));
  const [content, setContent] = React.useState('');
  // Agent for request
  const [agent] = useXAgent({
      request: (_a, _b) =>
          __awaiter(void 0, [_a, _b], void 0, function* ({ message }, { onSuccess, onUpdate }) {
              yield sleep();
              // console.log(message)
              // let currentContent = '';
              // const id = setInterval(() => {
              //     currentContent = message.slice(0, currentContent.length + 2);
              //     onUpdate(currentContent);
              //     if (currentContent === message) {
              //         clearInterval(id);
              //         onSuccess([message]);
              //     }
              // }, 100);
          }),
      // 可在更新数据时对messages做转换，同时会更新到messages
      transformMessage: info => {
          return {
              content: message,
              role: 'ai',
          };
      },
      resolveAbortController: controller => {
          abortController.current = controller;
      },
  });

  // Chat messages
  const { onRequest, messages, setMessages } = useXChat({
      agent,
  });

  const [connected, setConnected] = useState(false);
  const [chatStatus, setChatStatus] = useState(false);

  const connectionKey = "bolei" + "/1"; // 定义统一的连接key

  useEffect(() => {
    console.log('chatStatus', chatStatus)
    if (!chatStatus) return; // Only proceed if chatStatus is true
      // 连接 WebSocket
      const socket = wsManager.connect(
          connectionKey,
          (message) => {
              console.log('message', message);
              setMessageId((prevId) => prevId + 1);
              setMessages((prevMessages) => {
                  return [
                      ...prevMessages,
                      { id: messageId, message: message, status: 'ai' }, // 使用 trim() 去除末尾多余的换行符
                  ];
              });
          },
          () => setConnected(true), // 连接成功回调
          () => setConnected(false), // 连接关闭回调
          (error) => console.error('WebSocket 错误:', error)
      );

      return () => {
          wsManager.close(connectionKey); // 组件卸载时关闭 WebSocket 连接
      };
  }, [chatStatus]);

  // ==================== Event ====================
  // 发送消息
  const onSubmit = (nextContent) => {
      wsManager.sendMessage(connectionKey, { content: message });
      onRequest(nextContent);
      setContent('');
  };

  const globalSearchClick = () => {
      setGlobalSearch(!globalSearch)
  }


  // ==================== Nodes ====================
  
  const chatList = (
      <div className={styles.chatList}>
          {messages?.length ? (
              /* 🌟 消息列表 */
              // <Bubble.List
              //     items={messages?.map((i) => ({
              //         ...i.message,
              //         classNames: {
              //             content: i.status === 'loading' ? styles.loadingMessage : '',
              //         },
              //         typing: i.status === 'loading' ? { step: 5, interval: 20, suffix: <>💗</> } : false,
              //     }))}
              //     style={{ height: '100%', paddingInline: "calc(calc(100% - 700px) /2)" }}
              //     roles={roles}
              // />
              <Bubble.List
                  roles={roles}
                  style={{ maxHeight: 660 }}
                  items={messages.map(({ id, message, status }) => ({
                      key: id,
                      loading: status === 'loading',
                      role: status === 'local' ? 'local' : 'ai',
                      content: message,
                  }))}
              />
          ) : (
              <Space direction="vertical" size={16} style={{ paddingInline: "calc(calc(100% - 700px) /2)" }} className={styles.placeholder}>
                  <Welcome
                      variant="borderless"
                      icon="https://mdn.alipayobjects.com/huamei_iwk9zp/afts/img/A*s5sNRo5LjfQAAAAAAAAAAAAADgCCAQ/fmt.webp"
                      title="Hello, I'm bsin app agent"
                      description="基于bsin-paas和大模型打造的应用智能体~~"
                      extra={
                          <Space>
                              <Button icon={<ShareAltOutlined />} />
                              <Button icon={<EllipsisOutlined />} />
                          </Space>
                      }
                  />
                  <Flex gap={16}>
                      <Prompts
                          items={[HOT_TOPICS]}
                          styles={{
                              list: { height: '100%' },
                              item: {
                                  flex: 1,
                                  backgroundImage: 'linear-gradient(123deg, #e5f4ff 0%, #efe7ff 100%)',
                                  borderRadius: 12,
                                  border: 'none',
                              },
                              subItem: { padding: 0, background: 'transparent' },
                          }}
                          onItemClick={(info) => {
                              onSubmit(info.data.description);
                          }}
                          className={styles.chatPrompt}
                      />

                      <Prompts
                          items={[DESIGN_GUIDE]}
                          styles={{
                              item: {
                                  flex: 1,
                                  backgroundImage: 'linear-gradient(123deg, #e5f4ff 0%, #efe7ff 100%)',
                                  borderRadius: 12,
                                  border: 'none',
                              },
                              subItem: { background: '#ffffffa6' },
                          }}
                          onItemClick={(info) => {
                              onSubmit(info.data.description);
                          }}
                          className={styles.chatPrompt}
                      />
                  </Flex>
              </Space>
          )}
      </div>
  );
  const senderHeader = (
      <Sender.Header
          title="Upload File"
          open={attachmentsOpen}
          onOpenChange={setAttachmentsOpen}
          styles={{ content: { padding: 0 } }}
      >
          <Attachments
              beforeUpload={() => false}
              items={attachedFiles}
              onChange={(info) => setAttachedFiles(info.fileList)}
              placeholder={(type) =>
                  type === 'drop'
                      ? { title: 'Drop file here' }
                      : {
                          icon: <CloudUploadOutlined />,
                          title: 'Upload files',
                          description: 'Click or drag files to this area to upload',
                      }
              }
          />
      </Sender.Header>
  );
  const chatSender = (
      <div className={styles.sender}>
          {/* 🌟 提示词 */}
          <Prompts
              items={SENDER_PROMPTS}
              onItemClick={(info) => {
                  onSubmit(info.data.description);
              }}
              styles={{
                  item: { padding: '6px 12px', marginBottom: 10 }
              }}
              className={styles.senderPrompt}
          />
          {/* 🌟 输入框 */}
          <Suggestion
              items={suggestions}
              onSelect={itemVal => {
                  setInputValue(`[${itemVal}]:`);
              }}
          >
              {({ onTrigger, onKeyDown }) => (
                  <Sender
                      value={inputValue}
                      header={senderHeader}
                      autoSize={{ minRows: 2, maxRows: 6 }}
                      placeholder="输入 / 获取快捷建议"
                      footer={({ components }) => {
                          const { SendButton, LoadingButton, SpeechButton } = components;
                          return (
                              <Flex justify="space-between" align="center">
                                  <Flex gap="small" align="center">
                                      {/* <Button style={iconStyle} type="text" icon={<LinkOutlined />} />
                                      <span style={{ fontSize: 14 }}>沉思</span>
                                      <Switch size="small" checked={deepThinking} onChange={setDeepThinking} /> */}
                                      <Button style={iconStyle} type="text" icon={<SearchOutlined />} onClick={globalSearchClick}>
                                          <span style={{ fontSize: 14 }}>联网</span>
                                          <Switch size="small" checked={globalSearch} />
                                      </Button>
                                      <Dropdown
                                          placement='top'
                                          menu={{
                                              items: agentItem,
                                              onClick: (e) => {
                                                  // Prevent automatic closing when menu item is clicked
                                                  e.domEvent.stopPropagation();
                                              }
                                          }}
                                          trigger={['click']}
                                          dropdownStyle={{ width: 300, maxHeight: 400, overflow: 'auto' }}
                                          overlayStyle={{ minWidth: 300 }}
                                          open={agentDropdownOpen}
                                          onOpenChange={(visible) => {
                                              // Only allow manual closing through button click
                                              if (visible === false) {
                                                  // Optional: You can add conditions here to determine when to allow closing
                                                  // For now, we'll keep it open regardless of outside clicks
                                                  return;
                                              }
                                              setAgentDropdownOpen(visible);
                                          }}
                                      >
                                          <Button
                                              style={iconStyle}
                                              type="text"
                                              onClick={(e) => {
                                                  e.preventDefault();
                                                  setPromptsDropdownOpen(false)
                                                  setMcpDropdownOpen(false)
                                                  setAgentDropdownOpen(!agentDropdownOpen);
                                              }}
                                              icon={<ApiOutlined />}
                                          >
                                              智能体
                                          </Button>
                                      </Dropdown>
                                      <Dropdown
                                          placement='top'
                                          menu={{
                                              items,
                                              onClick: (e) => {
                                                  // Prevent automatic closing when menu item is clicked
                                                  e.domEvent.stopPropagation();
                                              }
                                          }}
                                          trigger={['click']}
                                          dropdownStyle={{ width: 300, maxHeight: 400, overflow: 'auto' }}
                                          overlayStyle={{ minWidth: 300 }}
                                          open={mcpDropdownOpen}
                                          onOpenChange={(visible) => {
                                              // Only allow manual closing through button click
                                              if (visible === false) {
                                                  // Optional: You can add conditions here to determine when to allow closing
                                                  // For now, we'll keep it open regardless of outside clicks
                                                  return;
                                              }
                                              setMcpDropdownOpen(visible);
                                          }}
                                      >
                                          <Button
                                              style={iconStyle}
                                              type="text"
                                              onClick={(e) => {
                                                  e.preventDefault();
                                                  setPromptsDropdownOpen(false)
                                                  setAgentDropdownOpen(false)
                                                  setMcpDropdownOpen(!mcpDropdownOpen);
                                              }}
                                              icon={<ApiOutlined />}
                                          >
                                              MCP工具
                                          </Button>
                                      </Dropdown>
                                      <Dropdown
                                          placement='top'
                                          menu={{
                                              items: promptsItem,
                                              onClick: (e) => {
                                                  // Prevent automatic closing when menu item is clicked
                                                  e.domEvent.stopPropagation();
                                              }
                                          }}
                                          trigger={['click']}
                                          dropdownStyle={{ width: 300, maxHeight: 400, overflow: 'auto' }}
                                          overlayStyle={{ minWidth: 300 }}
                                          open={promptsDropdownOpen}
                                          onOpenChange={(visible) => {
                                              // Only allow manual closing through button click
                                              if (visible === false) {
                                                  // Optional: You can add conditions here to determine when to allow closing
                                                  // For now, we'll keep it open regardless of outside clicks
                                                  return;
                                              }
                                              setPromptsDropdownOpen(visible);
                                          }}
                                      >
                                          <Button
                                              style={iconStyle}
                                              type="text"
                                              onClick={(e) => {
                                                  e.preventDefault();
                                                  setMcpDropdownOpen(false)
                                                  setAgentDropdownOpen(false)
                                                  setPromptsDropdownOpen(!promptsDropdownOpen);
                                              }}
                                              icon={<ApiOutlined />}
                                          >
                                              提示词
                                          </Button>
                                      </Dropdown>
                                  </Flex>
                                  <Flex align="center">
                                      {/* <SpeechButton style={iconStyle} /> */}
                                      <Divider type="vertical" />
                                      {loading ? (
                                          <LoadingButton type="default" />
                                      ) : (
                                          // 发送消息按纽
                                          <SendButton type="primary" disabled={false} />
                                      )}
                                  </Flex>
                              </Flex>
                          );
                      }}
                      loading={loading}
                      onSubmit={() => {
                          // 点击发送执行按纽
                          console.log("点击了发送")

                          onSubmit(inputValue);
                          setInputValue('');
                      }}
                      onChange={nextVal => {
                          if (nextVal === '/') {
                              onTrigger();
                          } else if (!nextVal) {
                              onTrigger(false);
                          }
                          setInputValue(nextVal);
                      }}
                      onCancel={() => {
                          abortController.current?.abort();
                      }}
                      actions={false}
                      onKeyDown={onKeyDown}
                  />
              )}
          </Suggestion>
      </div>
  );

  useEffect(() => {
      // history mock
      if (messages?.length) {
          setMessageHistory((prev) => ({
              ...prev,
              [curConversation]: messages,
          }));
      }
  }, [messages]);

  let defaultMerchantNo = process.env.defaultMerchantNo


  const [drawerOpen, setDrawerOpen] = useState(false)
  const [size, setSize] = useState<DrawerProps['size']>()
  const [defaultCopilot, setDefaultCopilot] = useState({})

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

  const showDrawer = () => {

    setMessages([])
    setChatStatus(true)
    setSize('large')
    setDrawerOpen(true)

    let params = {
      merchantNo: defaultMerchantNo,
      type: '1',
    }
    //TODO: 获取租户对应的appAgent
    getAppAgent(params).then((res) => {
      if (res?.code != 0) {
        return
      }
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
    })
  }

  const onDrawerClose = () => {
    setChatStatus(false)
    setDrawerOpen(false)
  }

  // ==================== Render =================
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
        <div className={styles.layout}>
          <div className={styles.chat}>
              {chatList}
              {chatSender}
          </div>
      </div>
      </Drawer>
    </>
      
  );
};

export default Independent;