import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  order: number;
  acName: string;
  custNo: string;
  acNo: string;
  balance: string;
  custType: string;
  openAcDate: string;
  status: string;
  startTime: string;
  endTime: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '应用名称',
    dataIndex: 'merchantName',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    valueType: 'select',
    hideInTable: true,
  },

  // 应用类型  1: 应用 2：接口 3:mp(公众号服务订阅号)、4.miniapp(小程序)、 5.cp(企业号|企业微信)、6.pay(微信支付)、7.open(微信开放平台) 8wechat(个人微信) 9.menu(菜单模版)
  {
    title: '应用类型',
    width: 120,
    dataIndex: 'appType',
    valueType: 'select',
    valueEnum: {
      '1': {
        text: '应用',
      },
      '2': {
        text: '接口',
      },
      '3': {
        text: '微信公众号',
      },
      '4': {
        text: '微信小程序',
      },
      '5': {
        text: '企业号',
      },
      '6': {
        text: '微信支付',
      },
      '7': {
        text: '微信开发平台',
      },
      '8': {
        text: '个人微信',
      },
      '9': {
        text: '微信菜单',
      },
    },
  },

  // table里面的内容
  {
    title: 'ID',
    width: 170,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '租户号',
    width: 170,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '商户号',
    width: 170,
    dataIndex: 'bizRoleTypeNo',
    hideInSearch: true,
  },
  {
    title: '应用名称',
    width: 170,
    dataIndex: 'appName',
    hideInSearch: true,
  },
  //
  {
    title: '应用ID',
    width: 170,
    dataIndex: 'appId',
    hideInSearch: true,
  },
  {
    title: '应用密钥',
    width: 170,
    dataIndex: 'appSecret',
    hideInSearch: true,
  },
  {
    title: '通知地址',
    width: 260,
    dataIndex: 'notifyUrl',
    hideInSearch: true,
  },
  {
    title: '应用状态',
    width: 160,
    dataIndex: 'status',
    hideInSearch: true,
    valueEnum: {
      '0': {
        text: '正常',
      },
      '1': {
        text: '冻结',
      },
    },
  },
  {
    title: '应用描述',
    width: 160,
    dataIndex: 'appDescription',
    hideInSearch: true,
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
  },
  {
    title: '操作',
    width: 290,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
