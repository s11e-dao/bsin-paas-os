
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
      },
      {
        name: '非基座运行',
        path: '/uncontainer',
        component: '@/pages/uncontainer'
      },
      {
        name: '应用管理',
        path: '/application-management',
        component: '@/pages/ApplicationManagement/index',
      },
      {
        name: '产品管理',
        path: '/product-management',
        component: '@/pages/ProductManagement/index',
      },
      {
        name: '租户管理',
        path: '/tenant-management',
        component: '@/pages/TenantManagement/index',
      },
      {
        name: '机构管理',
        path: '/organizational-management',
        component: '@/pages/OrganizationalManagement/index',
      },
      {
        name: '机构岗位',
        path: '/organization-post',
        component: '@/pages/OrganizationPost/index',
      },
      {
        name: '用户管理',
        path: '/user-management',
        component: '@/pages/UserManagement/index',
      },
      {
        name: '岗位管理',
        path: '/post-management',
        component: '@/pages/PostManagement/index',
      },
      {
        name: '角色管理',
        path: '/role-management',
        component: '@/pages/RoleManagement/index',
      },
      {
        name: '菜单管理',
        path: '/menu-management',
        component: '@/pages/MenuManagement/index',
      },
      {
        name: '字典管理',
        path: '/dict-management',
        component: '@/pages/DictManagement/index',
      },
      {
        name: '区域管理',
        path: '/district-management',
        component: '@/pages/DistrictManagement/index',
      },
      {
        name: '用户中心',
        path: '/userCenter',
        component: '@/pages/UserCenter/index',
      },
      // ************日志 sys-log*************
      {
        path: 'operate-log',
        component: '@/pages/OperateLog/index',
      },
      {
        path: '/login-log',
        component: '@/pages/LoginLog/index',
      },
    ]
  }

];
export default routes;
