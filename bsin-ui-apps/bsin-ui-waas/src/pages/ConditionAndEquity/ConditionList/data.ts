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
    title: '条件名称',
    dataIndex: 'name',
    hideInTable: true,
  },
  {
    title: '条件类型',
    dataIndex: 'type',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
    // 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、账户-联合曲线(BC)  7：满减 8：权限
    valueEnum: {
      '1': {
        text: '数字徽章',
      },
      '2': {
        text: 'PFP',
      },
      '3': {
        text: '账户-DP',
      },
      '4': {
        text: '数字门票',
      },
      '5': {
        text: 'Pass卡',
      },
      '6': {
        text: '账户-BC',
      },
      '7': {
        text: '满减',
      },
      '8': {
        text: '权限',
      },
      '9': {
        text: '会员等级',
      },
    },
  },
  {
    title: '条件类型协议',
    dataIndex: 'typeProtocol',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
    valueEnum: {
      ERC20: {
        text: 'ERC20',
      },
      ERC721: {
        text: 'ERC721',
      },
      ERC1155: {
        text: 'ERC1155',
      },
      ERC3525: {
        text: 'ERC3525',
      },
    },
  },
  {
    title: '会员等级',
    dataIndex: 'grade',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
    valueEnum: {
      '1': {
        text: '等级1',
      },
      '2': {
        text: '等级2',
      },
      '3': {
        text: '等级3',
      },
      '4': {
        text: '等级4',
      },
    },
  },

  // table里面的内容
  {
    title: '条件名称',
    width: 170,
    dataIndex: 'name',
    hideInSearch: true,
  },
  {
    title: '条件类型',
    width: 170,
    dataIndex: 'type',
    valueType: 'select',
    hideInSearch: true,
    // 1、数字徽章 2、PFP 3、数字积分 4、数字门票 5、pass卡 6、账户-联合曲线(BC)  7：满减 8：权限
    valueEnum: {
      '1': {
        text: '数字徽章',
      },
      '2': {
        text: 'PFP',
      },
      '3': {
        text: '账户-DP',
      },
      '4': {
        text: '数字门票',
      },
      '5': {
        text: 'Pass卡',
      },
      '6': {
        text: '账户-BC',
      },
      '7': {
        text: '满减',
      },
      '8': {
        text: '权限',
      },
    },
  },
  {
    title: '类型编号',
    width: 170,
    dataIndex: 'typeNo',
    hideInSearch: true,
  },
  {
    title: '会员等级',
    width: 170,
    dataIndex: 'grade',
    hideInSearch: true,
  },
  {
    title: '类型协议',
    width: 170,
    dataIndex: 'typeProtocol',
    hideInSearch: true,
  },
  {
    title: '条件值',
    width: 170,
    dataIndex: 'amount',
    hideInSearch: true,
  },
  {
    title: '备注',
    width: 160,
    dataIndex: 'remark',
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
