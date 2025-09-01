/**
 * bsin智能聊天组件
 * 基于Ant Design X构建的AI聊天界面
 * 支持WebSocket实时通信、多种智能体选择、文件上传等功能
 */

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
  ApiOutlined, 
  LinkOutlined, 
  SearchOutlined,
  CloseOutlined
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
import { GPTVis } from '@antv/gpt-vis';
import { Avatar, Button, Flex, Space, Spin, message, theme, Divider, Switch, Dropdown, FloatButton, Modal } from 'antd';
import { createStyles } from 'antd-style';
import dayjs from 'dayjs';
import React, { useEffect, useRef, useState, useCallback } from 'react';
import { getSessionStorageInfo, getLocalStorageInfo } from '@/utils/localStorageInfo';
import WebSocketManager from '@/utils/WebSocketManager';
import {
  chatWithCopilot,
  getAppAgent,
  getChatHistoryList,
} from '../service'

const markdownContent = `
## GPT-VIS
Components for GPTs, generative AI, and LLM projects. Not only UI Components.

 \`\`\`vis-chart
  {
    "type": "pie",
    "data": [
      { "category": "分类一", "value": 27 },
      { "category": "分类二", "value": 25 },
      { "category": "分类三", "value": 18 },
      { "category": "分类四", "value": 15 },
      { "category": "分类五", "value": 10 },
      { "category": "其他", "value": 5 }
    ]
  }
\`\`\``;

// MCP工具配置 - 可选择的工具列表
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
  label: '热门话题',
  children: [
      {
          key: '1-1',
          description: 'bsin-paas的应用场景',
          icon: <span style={{ color: '#f93a4a', fontWeight: 700 }}>1</span>,
      },
      {
          key: '1-2',
          description: '火源社区是做什么的',
          icon: <span style={{ color: '#ff6565', fontWeight: 700 }}>2</span>,
      },
      {
          key: '1-3',
          description: 'AI时代如何学习?',
          icon: <span style={{ color: '#ff8f1f', fontWeight: 700 }}>3</span>,
      },
  ],
};

const DESIGN_GUIDE = {
  key: '2',
  label: '设计文档',
  children: [
      {
          key: '2-1',
          icon: <HeartOutlined />,
          label: 'bsin-paas-os',
          description: 'AI智能理解用户需求并提供解决方案',
      },
      {
          key: '2-3',
          icon: <CommentOutlined />,
          label: 'bsin-paas-os-ui',
          description: 'AI如何以用户易懂的方式表达自己',
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
      padding: ${token.paddingLG}px;
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


let webScoketUrl = process.env.webScoketUrl;
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

const BsinChatModal = ({ customerInfo }) => {

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
  
  // 防抖优化 - 避免频繁的状态更新
  const debouncedSetMessages = useRef(null);

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

  // 动态获取token并创建WebSocketManager
  const [wsManager, setWsManager] = useState(null);
  
  useEffect(() => {
    const currentToken = getSessionStorageInfo('token')?.token;
    console.log('WebSocket初始化 - token:', currentToken ? '存在' : '不存在');
    
    if (currentToken && webScoketUrl) {
      const manager = new WebSocketManager(webScoketUrl, currentToken);
      setWsManager(manager);
      console.log('WebSocketManager已创建');
    }
  }, []); // 只在组件挂载时执行一次

  const connectionKey = "bolei" + "/0"; // 定义统一的连接key

  // 格式化业务消息 - 使用useCallback优化
  const formatBusinessMessage = useCallback((data) => {
    const { description, content, bizType } = data;
    let formattedMessage = `${description}\n\n`;
    
    // 检查是否为错误响应
    if (content && typeof content === 'object' && content.success === false) {
      formattedMessage += `❌ **操作失败**\n`;
      if (content.error) {
        formattedMessage += `⚠️ 错误信息: ${content.error}\n`;
      }
      return formattedMessage;
    }
    
    switch (bizType) {
      case '1': // 商品推荐
        // 处理新的数据结构：content直接是商品数组
        if (Array.isArray(content) && content.length > 0) {
          content.forEach((goods, index) => {
            formattedMessage += `**${index + 1}. ${goods.goodsName}**\n`;
            formattedMessage += `🆔 商品编号: ${goods.goodsNo}\n`;
            formattedMessage += `💰 价格: ${goods.price || '待定'}\n`;
            formattedMessage += `📱 品牌: ${goods.brand || '未知'}\n`;
            formattedMessage += `🏷️ 分类: ${goods.category || '未分类'}\n`;
            formattedMessage += `📦 库存: ${goods.stock || '未知'}\n`;
            formattedMessage += `✨ 特色: ${goods.features && goods.features.length > 0 ? goods.features.join(', ') : '暂无'}\n`;
            formattedMessage += `📝 描述: ${goods.description || '暂无描述'}\n\n`;
          });
        }
        break;
        
      case '4': // 客户管理
        if (content.customers && content.customers.length > 0) {
          content.customers.forEach((customer, index) => {
            formattedMessage += `**${index + 1}. ${customer.customerName}**\n`;
            formattedMessage += `🆔 客户编号: ${customer.customerNo}\n`;
            formattedMessage += `📞 电话: ${customer.phone || '待补充'}\n`;
            formattedMessage += `📧 邮箱: ${customer.email || '待补充'}\n\n`;
          });
        }
        break;
        
      default:
        // 其他业务类型，使用通用格式
        if (content) {
          formattedMessage += JSON.stringify(content, null, 2);
        }
    }
    
    return formattedMessage;
  }, []);

  useEffect(() => {
    if (!chatStatus) return; // Only proceed if chatStatus is true
    
    // 连接 WebSocket
    if (!wsManager) {
      console.error('WebSocketManager未初始化');
      return;
    }
    
    console.log('开始WebSocket连接:', connectionKey);
    
    // 添加清理标志，防止内存泄漏
    let isActive = true;
    let reconnectTimer = null;
    
    const socket = wsManager.connect(
          connectionKey,
          (message) => {
              // 检查组件是否仍然活跃
              if (!isActive) return;
              
              console.log('收到WebSocket消息:', message);
              
              // 过滤掉结束标记和空消息
              if (message === '[DONE]' || message.trim() === '[DONE]' || !message || message.trim() === '') {
                  console.log('收到结束标记或空消息，跳过处理:', message);
                  return;
              }
              
              let processedMessage = message;
              let messageType = 'text'; // 默认消息类型

              try {
                  // 如果收到的消息是字符串形式的 JSON，先解析它
                  if (typeof message === 'string' && message.startsWith('{')) {
                      const parsed = JSON.parse(message);
                      console.log('解析后的消息结构:', parsed);
                      
                      // 根据消息类型处理
                      if (parsed.type === '3' && ['1', '2', '3', '4', '5'].includes(parsed.bizType)) {
                          // 业务消息
                          messageType = 'business_message';
                          processedMessage = formatBusinessMessage(parsed);
                      } else if (parsed.content) {
                          // 普通文本消息
                          processedMessage = parsed.content;
                      } else if (parsed.message) {
                          processedMessage = parsed.message;
                      } else {
                          processedMessage = JSON.stringify(parsed, null, 2);
                      }
                  }

                  // 处理转义字符
                  processedMessage = processedMessage
                      .replace(/\\n/g, '\n')
                      .replace(/\\"/g, '"')
                      .replace(/\\`/g, '`')
                      .replace(/\\\\/g, '\\');

                  console.log('处理后的消息:', processedMessage);
                  console.log('消息类型:', messageType);

                  setMessageId((prevId) => {
                      const newId = prevId + 1;
                      setMessages((prevMessages) => {
                          // 检查是否已存在相同ID的消息，防止重复
                          const exists = prevMessages.some(msg => msg.id === newId);
                          if (exists) return prevMessages;
                          
                          // 限制消息数量，防止内存无限增长
                          const MAX_MESSAGES = 100;
                          let newMessages = [
                              ...prevMessages,
                              {
                                  id: newId,
                                  message: processedMessage,
                                  content: processedMessage,
                                  status: 'ai',
                                  messageType: messageType,
                                  originalData: message,
                                  timestamp: Date.now()
                              }
                          ];
                          
                          // 如果消息数量超过限制，删除最旧的消息
                          if (newMessages.length > MAX_MESSAGES) {
                              newMessages = newMessages.slice(-MAX_MESSAGES);
                          }
                          
                          return newMessages;
                      });
                      return newId;
                  });
              } catch (error) {
                  console.error('消息处理错误:', error);
                  // 如果处理失败，使用原始消息
                  setMessageId((prevId) => {
                      const newId = prevId + 1;
                      setMessages((prevMessages) => [
                          ...prevMessages,
                          { 
                              id: newId, 
                              message, 
                              content: message, 
                              status: 'ai',
                              messageType: 'text',
                              originalData: message
                          }
                      ]);
                      return newId;
                  });
              }
          },
          () => {
            if (!isActive) return;
            console.log('WebSocket连接成功');
            setConnected(true);
          }, // 连接成功回调
          () => {
            if (!isActive) return;
            console.log('WebSocket连接关闭');
            setConnected(false);
            
            // 自动重连逻辑
            if (chatStatus && isActive) {
              reconnectTimer = setTimeout(() => {
                if (isActive && chatStatus) {
                  console.log('尝试重新连接...');
                  // 这里可以触发重新连接
                }
              }, 3000);
            }
          }, // 连接关闭回调
          (error) => {
            if (!isActive) return;
            console.error('WebSocket错误:', error);
            setConnected(false);
          }
      );

      return () => {
          // 清理函数 - 防止内存泄漏
          isActive = false;
          if (reconnectTimer) {
            clearTimeout(reconnectTimer);
            reconnectTimer = null;
          }
          if (wsManager) {
            wsManager.close(connectionKey); // 组件卸载时关闭 WebSocket 连接
          }
      };
  }, [chatStatus, wsManager, connectionKey, formatBusinessMessage]);

  // ==================== Event ====================
  // 发送消息
  // 发送消息 - 使用useCallback优化
  const onSubmit = useCallback((nextContent) => {
      if (wsManager) {
        wsManager.sendMessage(connectionKey, { type: 'ai_chat', content: nextContent });
      } else {
        console.error('WebSocketManager未初始化');
      }
      onRequest(nextContent);
      setContent('');
  }, [wsManager, connectionKey, onRequest]);

  const globalSearchClick = useCallback(() => {
      setGlobalSearch(prev => !prev);
  }, []);


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
                  items={messages.map(({ id, message, status, content, messageType, originalData }) => ({
                    key: id,
                    loading: status === 'loading',
                    role: status === 'local' ? 'local' : 'ai',
                    content: content || message,
                    messageRender: (content) => (
                        <div style={{ whiteSpace: 'pre-wrap' }}>
                            {messageType === 'business_message' ? (
                                <div>
                                    <div style={{ 
                                        background: '#f8f9fa', 
                                        padding: '12px', 
                                        borderRadius: '8px', 
                                        marginBottom: '8px',
                                        border: '1px solid #e9ecef'
                                    }}>
                                        <GPTVis>{content}</GPTVis>
                                    </div>
                                </div>
                            ) : (
                                <GPTVis>{content}</GPTVis>
                            )}
                        </div>
                    )
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


  const [chatModalOpen, setChatModalOpen] = useState(false)
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

  const showChatModal = useCallback(() => {
    setMessages([])
    setChatStatus(true)
    setChatModalOpen(true)

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
  }, [defaultMerchantNo, customerInfo?.customerNo]);

  const onChatModalClose = useCallback(() => {
    setChatStatus(false)
    setChatModalOpen(false)
  }, []);

  // ==================== Render =================
  return (
    <>
    {/* bsin-copilot vuca */}
    <FloatButton
        style={{ right: 24 }}
        type="primary"
        icon={<CommentOutlined />}
        onClick={showChatModal}
      />

      {chatModalOpen && (
        <div
          style={{
            position: 'fixed',
            right: 24,
            bottom: 24,
            width: 580,
            height: 800,
            maxWidth: 'calc(100vw - 48px)',
            maxHeight: 'calc(100vh - 48px)',
            backgroundColor: '#fff',
            borderRadius: 8,
            boxShadow: '0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 9px 28px 8px rgba(0, 0, 0, 0.05)',
            zIndex: 1000,
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden',
            pointerEvents: 'auto'
          }}
        >
          {/* 标题栏 */}
          <div
            style={{
              padding: '16px 24px',
              borderBottom: '1px solid #f0f0f0',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              backgroundColor: '#fafafa',
              borderTopLeftRadius: 8,
              borderTopRightRadius: 8
            }}
          >
            <span style={{ fontSize: 16, fontWeight: 600 }}>bsin app agent</span>
            <Button
              type="text"
              icon={<CloseOutlined />}
              onClick={onChatModalClose}
              style={{
                border: 'none',
                padding: 0,
                width: 24,
                height: 24,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#666'
              }}
            />
          </div>
          
          {/* 聊天内容 */}
          <div style={{ flex: 1, overflow: 'hidden' }}>
            <div className={styles.layout} style={{ height: '100%' }}>
              <div className={styles.chat} style={{ height: '100%' }}>
                  {chatList}
                  {chatSender}
              </div>
            </div>
          </div>
        </div>
      )}
    </>
      
  );
};

export default BsinChatModal;