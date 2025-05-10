
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
        name: '首页',
        path: '/home',
        component: '@/pages/Home/index',
        wrappers: [
          '@/wrappers/auth',
        ],
      },
      {
        name: '非基座运行',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      // **************************模型**************************
      {
        path: '/data-model',
        redirect: '/data-model/list',
      },
      {
        name: '数据模型',
        path: '/data-model/list',
        component: '@/pages/dataModel/ModelList'
      },
      // **************************元数据**************************
      {
        path: '/meta-data',
        redirect: '/meta-data/data-map',
      },
      {
        name: '数据地图',
        path: '/meta-data/data-map',
        component: '@/pages/metaData/DataMap'
      },
      {
        name: '数据血缘',
        path: '/meta-data/lineage',
        component: '@/pages/metaData/Lineage'
      },
      {
        name: '数据分类',
        path: '/meta-data/catalog',
        component: '@/pages/metaData/Catalog'
      },
      // **************************治理**************************
      {
        path: '/data-governance',
        redirect: '/data-governance/lifecycle',
      },
      {
        name: '生命周期',
        path: '/data-governance/lifecycle',
        component: '@/pages/dataGovernance/Lifecycle'
      },
      {
        name: '数据质量',
        path: '/data-governance/quality',
        component: '@/pages/dataGovernance/Quality'
      },
      {
        name: '价值计量',
        path: '/data-governance/value-metrics',
        component: '@/pages/dataGovernance/ValueMetrics'
      },
      {
        name: '安全策略',
        path: '/data-governance/security',
        component: '@/pages/dataGovernance/SecurityPolicy'
      },
      // **************************服务**************************
      {
        path: '/data-service',
        redirect: '/data-service/api-market',
      },
      {
        name: 'API市场',
        path: '/data-service/api-market',
        component: '@/pages/dataService/ApiMarket'
      },
      {
        name: '数据订阅',
        path: '/data-service/subscription',
        component: '@/pages/dataService/Subscription'
      }
    ]
  }

];
export default routes;
