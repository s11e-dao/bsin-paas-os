
const routes = [
  {
    path: '/',
    component: '@/layouts/BasicLayout',
    routes: [
      { path: '/', redirect: 'square' },
      { path: '/square', component: '@/pages/Square/index' },
      { path: '/knowledge', component: '@/pages/Knowledge/index' },
      { path: '/task', component: '@/pages/Task/index' },
      { path: '/flow', component: '@/pages/Flow/index' },
      { path: '/d-Human', component: '@/pages/DHuman/index' },
      { path: '/draw', component: '@/pages/Draw/index' },
      { path: '/account', component: '@/pages/Account/index' },
      { path: '/subscribeService', component: '@/pages/SubscribeService/index' },
      
    ]
  },
];
export default routes;
