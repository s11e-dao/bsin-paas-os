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
    title: '登录地址',
    dataIndex: 'operIp',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '用户名称',
    dataIndex: 'operBy',
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
    title: '方法标题',
    width: 160,
    dataIndex: 'methodTitle',
    hideInSearch: true,
  },
  {
    title: '操作方法',
    width: 320,
    ellipsis: true,
    dataIndex: 'method',
    hideInSearch: true,
  },
  {
    title: '请求方式',
    width: 100,
    dataIndex: 'requestMethod',
    hideInSearch: true,
  },
  {
    title: '操作人',
    width: 160,
    dataIndex: 'operBy',
    hideInSearch: true,
  },
  {
    title: '主机地址',
    width: 160,
    dataIndex: 'operIp',
    hideInSearch: true,
  },
  {
    title: '请求参数',
    width: 140,
    ellipsis: true,
    dataIndex: 'inputParam',
    hideInSearch: true,
  },
  {
    title: '响应参数',
    width: 140,
    ellipsis: true,
    dataIndex: 'outputParam',
    hideInSearch: true,
  },
  {
    title: '浏览器信息',
    width: 140,
    dataIndex: 'browser',
    hideInSearch: true,
  },
  {
    title: '操作地点',
    width: 160,
    dataIndex: 'operLocation',
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
