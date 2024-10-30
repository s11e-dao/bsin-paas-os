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
