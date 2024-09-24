
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
      // ****************************事件模型***********************
      {
        path: '/event-model',
        component: '@/pages/EventModel/index',
      },
      // ****************************模型***********************
      {
        name: '模型管理',
        path: '/model',
        redirect: '/model/model-category',
      },
      {
        name: '模型分类',
        path: '/model/model-category',
        component: '@/pages/modelManagementAdmin/ModelTypeAdmin',
      },
      {
        name: '表单模型',
        path: '/model/form-model',
        component: '@/pages/FormManagementAdmin',
      },
      // 规则模型
      {
        name: '规则模型',
        path: '/model/decision-model',
        component: '@/pages/DecisionSet/index',
      },
      // 推理模型
      {
        path: '/model/inference-model',
        component: '@/pages/DecisionModel/index',
      },
      {
        name: '流程模型',
        path: '/model/flow-model',
        component: '@/pages/modelManagementAdmin/ModelDefinitionAdmin',
      },
      {
        name: '流程定义',
        path: '/process-definition-admin',
        component: '@/pages/processManagementAdmin/ProcessDefinitionAdmin',
      },
      {
        name: '流程任务',
        path: '/process-task-admin',
        component: '@/pages/processManagementAdmin/ProcessTaskAdmin',
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
      // ****************************待办***********************
      {
        name: '待办',
        path: '/todo',
        redirect: '/todo/todo-task',
      },
      {
        name: '待认领任务',
        path: '/todo/todo-claim-task',
        component: '@/pages/taskManagementAdmin/TodoClaimTaskAdmin',
      },
      {
        name: '待办任务',
        path: '/todo/todo-task',
        component: '@/pages/taskManagementAdmin/TodoTaskAdmin',
      },
      {
        name: '我的发起',
        path: '/todo/my-launch-task',
        component: '@/pages/taskManagementAdmin/TodoTaskAdmin',
      },
      // ****************************决策指标***********************
      {
        path: '/decision-metrics',
        redirect: '/decision-metrics/metrics-object',
      },
      // 决策指标集
      {
        path: '/decision-metrics/metrics-object',
        component: '@/pages/DecisionIndicator/index',
      },
      // 决策指标字段
      {
        path: '/decision-metrics/metrics-field',
        component: '@/pages/DecisionIndicator/index',
      },
    
      // 决策表
      {
        path: '/decision-table',
        component: '@/pages/DecisionTable/index',
      },
      // 模型测试
      {
        path: '/decision-model-test',
        component: '@/pages/DecisionTest/index',
      },
      // ****************************监控***********************
      {
        path: '/monitor',
        redirect: '/monitor/hit-rule',
      },
      {
        path: '/monitor/hit-rule',
        component: '@/pages/DecisionMonitor/index',
      },
      {
        name: '404',
        path: '/*',
        component: '@/pages/NotFound/index',
      },
    ]
  }

];
export default routes;
