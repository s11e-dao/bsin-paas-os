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
    title: '客户编号',
    dataIndex: 'type',
    hideInTable: true,
  },
  // 客户类型 0、个人客户 1、租户商家客户 2、租户(dao)客户 3、顶级平台商家客户
  {
    title: '客户类型',
    dataIndex: 'type',
    hideInTable: true,
    valueType: 'select',
    valueEnum: {
      '0': {
        text: '个人客户',
      },
      '1': {
        text: '租户商家客户',
      },
      '2': {
        text: '租户(dao)客户',
      },
      '3': {
        text: '顶级平台商家客户',
      },
    },
    fieldProps: {
      maxLength: 20,
    },
  },
  //是否是白名单 0、否 1、是
  {
    title: 'VIP',
    dataIndex: 'vipFlag',
    hideInTable: true,
    valueType: 'select',
    valueEnum: {
      '0': {
        text: '否',
      },
      '1': {
        text: '是',
      },
    },
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '客户号',
    width: 170,
    dataIndex: 'customerNo',
    hideInSearch: true,
  },
  {
    title: '租户ID',
    width: 170,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '昵称',
    width: 190,
    dataIndex: 'nickname',
    hideInSearch: true,
  },
  {
    title: '身份证号',
    width: 190,
    dataIndex: 'identityCard',
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
    width: 200,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
