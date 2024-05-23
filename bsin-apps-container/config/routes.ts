
const routes = [
  { path: '/login', component: '@/pages/Login/index' },
  {
    path: '/',
    component: '@/layouts/BasicLayout',
    routes: [
      {
        path: '/',
        redirect: 'login'
      },
      {
        path: '/home',
        component: '@/pages/Home/index',
        wrappers: [
          '@/wrappers/auth',
        ],
      },
      {
        path: '/404',
        component: '@/pages/Home/index'
      },
    ]
  },
];
export default routes;
