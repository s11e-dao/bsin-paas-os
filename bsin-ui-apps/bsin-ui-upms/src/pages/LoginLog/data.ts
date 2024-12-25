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
    title: '登录账号',
    dataIndex: 'username',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
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
    title: '登录账号',
    width: 160,
    dataIndex: 'username',
    hideInSearch: true,
  },
  {
    title: '用户昵称',
    width: 320,
    dataIndex: 'nickname',
    hideInSearch: true,
  },
  {
    title: '登录IP',
    width: 100,
    dataIndex: 'ip',
    hideInSearch: true,
  },
  {
    title: '登录地点',
    width: 100,
    dataIndex: 'loginLocation',
    hideInSearch: true,
  },
  {
    title: '登录时间',
    width: 100,
    dataIndex: 'loginTime',
    hideInSearch: true,
  },
  {
    title: '浏览器信息',
    width: 100,
    dataIndex: 'browser',
    hideInSearch: true,
  },
  {
    title: '状态',
    width: 100,
    hideInSearch: true,
    dataIndex: 'status',
    valueType: 'select',
    valueEnum: {
      '0': {
        text: '禁用',
      },
      '1': {
        text: '启用',
      },
    },
  },
  {
    title: '创建时间',
    width: 140,
    dataIndex: 'createTime',
    hideInSearch: true,
  },
  // {
  //   title: '操作',
  //   width: 60,
  //   hideInSearch: true,
  //   dataIndex: 'action',
  //   fixed: 'right',
  // },
];

export default columnsData;
