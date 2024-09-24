// /bsin-ui-digital-assets-management
const routes = [
  {
    path: '/',
    component: '@/layouts/index',
    routes: [
      {
        path: '/',
        redirect: '/home',
      },
      {
        path: '/home',
        component: '@/pages/Home/index',
      },
      // LLM模型
      {
        path: '/ai-llm',
        component: '@/pages/LLM/index',
      },
      // 索引模型
      {
        path: '/ai-embedding-model',
        component: '@/pages/EmbeddingModel/index',
      },
      // 分词模型
      {
        path: '/ai-chunk-model',
        component: '@/pages/ChunkModel/index',
      },
      // 提示词模版
      {
        path: '/ai-prompt-template',
        component: '@/pages/PromptTemplate/index',
      },
      // ai编排
      {
        path: 'ai-flow',
        component: '@/pages/Flow',
      },
      // Copilot
      {
        path: '/ai-copilot',
        // component: '@/pages/Copilot',
        component: '@/pages/Agent',
      },
      // Tool
      {
        path: '/ai-tool',
        component: '@/pages/Tool',
      },
      // Agent
      {
        path: '/ai-agent',
        component: '@/pages/Agent',
      },
      // 知识库
      {
        path: '/ai-knowledge-base',
        component: '@/pages/KnowledgeBase/index',
      },
      // 生成式聊天
      {
        path: '/bsin-chat',
        component: '@/pages/BsinChat/index',
      },
      // 敏感词
      {
        path: '/ai-sensitive-words',
        component: '@/pages/SensitiveWords/index',
      },
      // 会员
      {
        path: '/ai-member',
        component: '@/pages/Member/index',
      },
      // ****************************设置***********************
      {
        path: '/ai-setting',
        component: '@/pages/Setting/index',
      },
      // ****************************系统监控***********************
      {
        path: '/ai-admin-monitor',
        component: '@/pages/AdminMonitor/index',
      },
      // ****************************个人中心***********************
      {
        path: '/userCenter',
        component: '@/pages/Setting/index',
      },
    ],
  },
]
export default routes
