import routes from './config/routes'
import { v4Token } from './config/antd4-token'

export default {
  request: {
    dataField: '',
  },
  define: {
    // 后台访问地址
    'process.env.baseUrl': process.env.BSIN_GATEWAY_BASE_URL || 'http://127.0.0.1:9195',
    'process.env.ipfsApiUrl': process.env.BSIN_IPFS_API_URL || 'https://ipfsadmin.s11edao.com/api/v0', // ipfs API
    'process.env.ipfsGatewauUrl': process.env.BSIN_IPFS_GATEWAY_URL || 'https://ipfs.s11edao.com/ipfs/', // ipfs Gateway
    'process.env.fileUrl': process.env.BSIN_FILE_URL || 'http://file.s11edao.com/jiujiu/', // 本地服务器文件前缀地址
    'process.env.bsinFileUploadUrl':  process.env.BSIN_FILE_UPLOAF_URL || 'http://127.0.0.1:9195/http/upload/aliOssUpload', // IPFS存储且OSS备份
    'process.env.storeMethod': '6', //  1.IPFS存储：需要同时指定 backup 存储平台  2.aliOSS存储：  4.服务器本地存储：  3.both IPFS and aliOSS:  5.both IPFS and 服务器本地存储
    'process.env.biganH5Url': process.env.BSIN_BIGAN_H5_URL || 'http://127.0.0.1:8080/', // biganH5 url local test
    'process.env.tenantAppType': 'ai', //
    'process.env.webScoketUrl': process.env.BSIN_WEBSOCKET_BASE_URL || 'ws://localhost:9195/ws-ai-agent/myWs',
    'process.env.contextPath_aiAgent': '/appAgent',  // appAgent应用    
  },
  antd: false,
  lessLoader: {
    modifyVars: v4Token,
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
            // bsin-ui-ai-admin的应用ID
            appId: "1649584597970849792",
          }
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
  base: '/bsin-ui-ai-admin/',
  routes,
  // 路由模式
  hash: true,
  history: {
    type: 'hash',
  },
  // plugins: ['./plungin.js'],
}
