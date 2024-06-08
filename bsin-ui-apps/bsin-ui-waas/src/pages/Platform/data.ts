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
    title: '平台名称',
    dataIndex: 'username',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '平台号',
    dataIndex: 'tenantId',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '节点号',
    width: 190,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '平台节点名称',
    width: 160,
    dataIndex: 'platformName',
    hideInSearch: true,
  },
  {
    title: '登录名称',
    width: 160,
    dataIndex: 'username',
    hideInSearch: true,
  },
  {
    title: '节点描述',
    width: 160,
    dataIndex: 'description',
    hideInSearch: true,
  },
  {
    title: '创建者',
    width: 160,
    dataIndex: 'createBy',
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
    width: 100,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
