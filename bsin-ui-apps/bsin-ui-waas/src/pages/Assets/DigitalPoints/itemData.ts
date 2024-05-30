import type { ProColumns } from '@ant-design/pro-table';

export type columnsItemDataType = {
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

const columnsItemData: ProColumns<columnsItemDataType>[] = [
  // 配置搜索框
  {
    title: '资产名称',
    dataIndex: 'assetsName',
    hideInTable: true,
  },
  {
    title: '资产类型',
    dataIndex: 'assetsType',
    hideInTable: true,
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
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '资产ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '资产名称',
    width: 160,
    dataIndex: 'assetsName',
    hideInSearch: true,
  },
  {
    title: '资产类型',
    width: 120,
    dataIndex: 'assetsType',
    hideInSearch: true,
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
    title: '领取方式',
    width: 160,
    dataIndex: 'obtainMethod',
    // hideInSearch: true,
    valueEnum: {
      '1': {
        text: '免费领取/空投',
      },
      '2': {
        text: '购买',
      },
      '3': {
        text: '固定口令领取',
      },
      '4': {
        text: '随机口令',
      },
      '5': {
        text: '盲盒',
      },
    },
  },
  {
    title: '价格',
    width: 100,
    dataIndex: 'price',
    hideInSearch: true,
  },
  {
    title: '资产数量',
    width: 160,
    dataIndex: 'quantity',
    hideInSearch: true,
  },
  {
    title: '库存',
    width: 120,
    dataIndex: 'inventory',
    hideInSearch: true,
  },
  {
    title: '协议描述',
    width: 320,
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
    width: 220,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
  },
];

export default columnsItemData;
