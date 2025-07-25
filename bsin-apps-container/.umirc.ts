import routes from './config/routes'

export default {
  // 开启request
  request: {
    dataField: '', //空为拿到后端的原始数据
  },
  define: {
    'process.env.title': 'Bsin-Paas',
    'process.env.subtitle': 'BsinPaaS一站式开发平台',
    'process.env.paasDescription': 's11e network web3品牌',
    'process.env.saasTitle': "bigan", // 一站式分布式商业生态构建服务网络
    'process.env.saasDescrition': "分布式可信产业生态数据价值网络",
    // 后台访问地址
    'process.env.baseUrl': process.env.BSIN_GATEWAY_BASE_URL || 'http://127.0.0.1:9195',
    'process.env.ipfsApiUrl': 'https://ipfsadmin.s11edao.com/api/v0', // ipfs API
    'process.env.ipfsGatewauUrl': 'https://ipfs.s11edao.com/ipfs/', // ipfs Gateway
    'process.env.fileUrl': 'http://file.s11edao.com/jiujiu/', // 本地服务器文件前缀地址
    'process.env.bsinFileUploadUrl': process.env.BSIN_FILE_UPLOAF_URL || 'http://127.0.0.1:9195/http/upload/aliOssUpload', // IPFS存储且OSS备份
    'process.env.storeMethod': '3', //  1.IPFS存储：需要同时指定 backup 存储平台  2.aliOSS存储：  4.服务器本地存储：  3.both IPFS and aliOSS:  5.both IPFS and 服务器本地存储
    'process.env.biganH5Url': 'http://localhost:8080/', // biganH5 url local test
    'process.env.webScoketUrl': process.env.BSIN_WEBSOCKET_BASE_URL || 'ws://localhost:9195/ws-oms/myWs',
    'process.env.registerNotNeedAudit': true,  // 针对copilot微信分身产品，用户注册无需审核，可直接使用基础产品
    'process.env.defaultTenantNo': '6345824413764157440',  // 默认租户号
    'process.env.defaultMerchantNo': '1737853502828482561',  // 默认商户号
    'process.env.contextPath_upms': '/upms',  // upms应用
    'process.env.contextPath_crm': '/crm',  // crm应用
    'process.env.contextPath_appAgent': '/appAgent',  // ai应用
    'process.env.contextPath_oms': '/oms',  // oms应用
    
  },
  antd: {
  },
  locale: {
    // 默认使用 src/locales/zh-CN.ts 作为多语言文件
    default: 'zh-CN',
    baseSeparator: '-',
  },
  qiankun: {
    master: {},
  },
  base: '/',
  routes,
  // 路由模式
  hash: true,
  history: {
    type: 'hash',
  },
  extraPostCSSPlugins: [
    require('postcss-import'),
    require('tailwindcss')({
      config: './tailwind.config.js',
    }),
    require('postcss-nested'),
    require('autoprefixer'),
  ],
  // 子应用表单引擎配置
  externals: {
    'react': 'React',
    'react-dom': 'ReactDOM'
  },
  headScripts: [
    'https://unpkg.com/react@18.0.0/umd/react.production.min.js',
    'https://unpkg.com/react-dom@18.0.0/umd/react-dom.production.min.js',
  ],
  esbuildMinifyIIFE: true
}
