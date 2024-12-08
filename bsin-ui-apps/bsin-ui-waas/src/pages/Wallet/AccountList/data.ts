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
    title: '客户号',
    dataIndex: 'bizRoleTypeNo',
    hideInTable: true,
  },
  {
    title: '账户名称',
    dataIndex: 'name',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '账户类别',
    dataIndex: 'category',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  // 账户状态 0、正常 1、冻结
  {
    title: '账户状态',
    dataIndex: 'status',
    hideInTable: true,
    valueEnum: {
      '0': {
        text: '正常',
      },
      '1': {
        text: '冻结',
      },
    },
  },
  // 账户类型 0、个人账户 1、企业账户 2 租户(dao)账户
  {
    title: '账户类型',
    dataIndex: 'type',
    hideInTable: true,
    valueEnum: {
      '0': {
        text: '个人账户',
      },
      '1': {
        text: '企业账户',
      },
      '2': {
        text: '租户(dao)账户',
      },
    },
  },

  // table里面的内容
  {
    title: '业务角色编号',
    width: 190,
    dataIndex: 'bizRoleTypeNo',
    hideInSearch: true,
  },
  {
    title: '账户号',
    width: 190,
    dataIndex: 'serialNo',
    hideInSearch: true,
  },
  {
    title: '账户名称',
    width: 100,
    dataIndex: 'name',
    hideInSearch: true,
  },
  // 1:余额账户 2:累计收入 3:累计支出 4:在途账户 5:累计支出 6:释放账户 7:数字积分账户'
  {
    title: '账户类别',
    width: 90,
    dataIndex: 'category',
    hideInSearch: true,
    valueEnum: {
      '1': {
        text: '余额账户',
      },
      '2': {
        text: '累计收入',
      },
      '3': {
        text: '累计支出',
      },
      '4': {
        text: '在途账户',
      },
      '5': {
        text: '待结算账户',
      },
      '6': {
        text: '待分佣账户',
      },
    },
  },
  // 账户类型 0、个人账户 1、企业账户 2 租户(dao)账户
  {
    title: '账户类型',
    width: 90,
    dataIndex: 'type',
    hideInSearch: true,
    valueEnum: {
      '0': {
        text: '个人账户',
      },
      '1': {
        text: '企业账户',
      },
      '2': {
        text: '租户(dao)账户',
      },
    },
  },
  {
    title: '账户余额',
    width: 160,
    dataIndex: 'balance',
    hideInSearch: true,
  },
  {
    title: '累计金额',
    width: 160,
    dataIndex: 'cumulativeAmount',
    hideInSearch: true,
  },
  {
    title: '冻结金额',
    width: 160,
    dataIndex: 'freezeAmount',
    hideInSearch: true,
  },
  // 账户状态 0、正常 1、冻结
  {
    title: '账户状态',
    width: 90,
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
    width: 150,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsData;
