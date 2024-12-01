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
    title: '业务角色编号',
    dataIndex: 'bizRoleTypeNo',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '账户号',
    dataIndex: 'accountNo',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  {
    title: '订单类型',
    dataIndex: 'orderType',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
    valueType: 'select',
    valueEnum: {
      '0': {
        text: '支付',
      },
      '1': {
        text: '退款',
      },
      '2': {
        text: '出售',
      },
      '3': {
        text: '充值',
      },
      '4': {
        text: '转账',
      },
      '5': {
        text: '提现',
      },
    },
  },

  {
    title: '账户类型',
    dataIndex: 'accountType',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
    valueType: 'select',
    // 账户类型 0、个人账户 1、企业账户
    valueEnum: {
      '0': {
        text: '个人账户',
      },
      '1': {
        text: '企业账户',
      },
    },
  },

  // table里面的内容
  {
    title: '业务角色编号',
    width: 160,
    dataIndex: 'bizRoleTypeNo',
    hideInSearch: true,
  },
  {
    title: '账户号',
    width: 160,
    dataIndex: 'accountNo',
    hideInSearch: true,
  },
  {
    title: '金额',
    width: 120,
    dataIndex: 'amount',
    hideInSearch: true,
  },
  {
    title: '出账入账标志',
    width: 120,
    hideInSearch: true,
    dataIndex: 'inOutFlag',
    valueType: 'select',
    valueEnum: {
      0: {
        text: '出账',
      },
      1: {
        text: '入账',
      },
    },
  },

  {
    title: '订单类型',
    dataIndex: 'orderType',
    hideInSearch: true,
    width: 120,
    valueType: 'select',
    valueEnum: {
      '0': {
        text: '支付',
      },
      '1': {
        text: '退款',
      },
      '2': {
        text: '出售',
      },
      '3': {
        text: '充值',
      },
      '4': {
        text: '转账',
      },
      '5': {
        text: '提现',
      },
    },
  },

  {
    title: '账户类型',
    dataIndex: 'accountType',
    hideInSearch: true,
    width: 120,
    valueType: 'select',
    // 账户类型 0、个人账户 1、企业账户
    valueEnum: {
      '0': {
        text: '个人账户',
      },
      '1': {
        text: '企业账户',
      },
    },
  },
  {
    title: '币种',
    width: 120,
    dataIndex: 'ccy',
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
