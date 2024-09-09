
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
      {
        name: '模型管理',
        path: '/model-management-admin',
        component: '@/pages/modelManagementAdmin/ModelManagementAdmin',
      },
      {
        name: '表单管理',
        path: '/form-management-admin',
        component: '@/pages/FormManagementAdmin',
      },
      {
        name: '流程类型',
        path: '/model-type-admin',
        component: '@/pages/modelManagementAdmin/ModelTypeAdmin',
      },
      {
        name: '流程管理',
        path: '/model-definition-admin',
        component: '@/pages/modelManagementAdmin/ModelDefinitionAdmin',
      },
      {
        name: '流程任务',
        path: '/process-task-admin',
        component: '@/pages/processManagementAdmin/ProcessTaskAdmin',
      },
      {
        name: '流程定义',
        path: '/process-definition-admin',
        component: '@/pages/processManagementAdmin/ProcessDefinitionAdmin',
      },
      {
        name: '流程实例',
        path: '/process-instance-admin',
        component: '@/pages/processManagementAdmin/ProcessInstanceAdmin',
      },
      {
        name: '历史流程实例',
        path: '/history-process-instance-admin',
        component:
          '@/pages/processManagementAdmin/HistoryProcessInstanceAdmin',
      },
      {
        name: '待认领任务',
        path: '/todo-claim-task-admin',
        component: '@/pages/taskManagementAdmin/TodoClaimTaskAdmin',
      },
      {
        name: '待办任务',
        path: '/todo-task-admin',
        component: '@/pages/taskManagementAdmin/TodoTaskAdmin',
      },
    ]
  }

];
export default routes;
