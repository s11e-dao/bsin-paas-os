import routes from './config/routes'

export default {
  // 开启request
  request: {
    dataField: '', //空为拿到后端的原始数据
  },
  define: {
    // 后台访问地址
    'process.env.baseUrl': process.env.BSIN_GATEWAY_BASE_URL || 'http://127.0.0.1:9195',
    'process.env.ipfsApiUrl': process.env.BSIN_IPFS_API_URL || 'https://ipfsadmin.s11edao.com/api/v0', // ipfs API
    'process.env.ipfsGatewauUrl': process.env.BSIN_IPFS_GATEWAY_URL || 'https://ipfs.s11edao.com/ipfs/', // ipfs Gateway
    'process.env.fileUrl': process.env.BSIN_FILE_URL || 'http://file.s11edao.com/jiujiu/', // 本地服务器文件前缀地址
    'process.env.bsinFileUploadUrl':  process.env.BSIN_FILE_UPLOAF_URL || 'http://127.0.0.1:9195/http/upload/aliOssUpload', // IPFS存储且OSS备份
    'process.env.storeMethod': '3', //  1.IPFS存储：需要同时指定 backup 存储平台  2.aliOSS存储：  4.服务器本地存储：  3.both IPFS and aliOSS:  5.both IPFS and 服务器本地存储
    'process.env.biganH5Url': process.env.BSIN_BIGAN_H5_URL ||'http://127.0.0.1:8080/', // biganH5 url local test
    'process.env.tenantAppType': 'ai', //
    'process.env.webScoketUrl':  process.env.BSIN_WEBSOCKET_BASE_URL || 'ws://127.0.0.1:9195/ws-waas/myWs',
    'process.env.contextPath_upms': '/upms',  // upms应用
    'process.env.contextPath_crm': '/crm',  // crm应用
    'process.env.contextPath_oms': '/oms',  // oms应用
    'process.env.contextPath_waas': '/waas',  // 钱包应用
    'process.env.contextPath_brms': '/brms',  // 规则引擎应用
    'process.env.contextPath_aiAgent': '/app-agent',  // 智能知识库
  },
  model: {},
  qiankun: {
    master: {
      // 注册权限管理信息
      apps: [
        {
          name: 'bsin-ui-upms', // 唯一 id
          entry: process.env.BSIN_UPMS_BASE_URL || 'http://127.0.0.1:8001', // html entry
          props: {
            // bsin-ui-waas的应用ID
            appId: "1784859132596195328",
          },
        },
      ],
      routes: [
        {
          path: '/bsin-ui-upms',
          microApp: 'bsin-ui-upms',
        },
      ],
    },
    slave: {},
  },
  base: '/bsin-ui-waas',
  routes,
  // 路由模式
  hash: true,
  history: {
    type: 'hash',
  },
  locale: {
    // 默认使用 src/locales/zh-CN.ts 作为多语言文件
    default: 'zh-CN',
    baseSeparator: '-',
  },
};
