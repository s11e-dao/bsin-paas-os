
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
        name: '全局参数',
        path: '/params',
        component: '@/pages/Params/index',
        wrappers: [
          '@/wrappers/auth',
        ],
      },
      {
        name: '用户中心',
        path: '/userCenter',
        component: '@/pages/UserCenter/index',
        wrappers: [
          '@/wrappers/auth',
        ],
      },
      {
        name: '非基座运行',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      // **************************数据模型**************************
      {
        name: '数据模型',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      // **************************数据地图**************************
      {
        name: '数据地图',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      // **************************数据治理**************************
      {
        name: '元数据管理',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      {
        name: '数据血缘',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      {
        name: '数据质量',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      {
        name: '生命周期',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      {
        name: '数据价值计量',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      // **************************数据服务**************************
      {
        name: '数据服务',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      }
    ]
  }

];
export default routes;
