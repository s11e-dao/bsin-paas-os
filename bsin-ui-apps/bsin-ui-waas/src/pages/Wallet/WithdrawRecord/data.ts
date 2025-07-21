import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  serialNo: string;
  customerNo: string;
  payeeAccount: string;
  amount: number;
  status: string;
  createTime: string;
  order?: number;
  acName?: string;
  custNo?: string;
  acNo?: string;
  balance?: string;
  custType?: string;
  openAcDate?: string;
  startTime?: string;
  endTime?: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '客户号',
    dataIndex: 'customerNo',
    hideInTable: true,
  },
  {
    title: '收款账户',
    dataIndex: 'payeeAccount',
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
    title: '客户号',
    width: 170,
    dataIndex: 'customerNo',
    hideInSearch: true,
  },
  {
    title: '收款账号',
    width: 190,
    dataIndex: 'payeeAccount',
    hideInSearch: true,
  },
  {
    title: '提现金额',
    width: 160,
    dataIndex: 'amount',
    hideInSearch: true,
  },
  {
    title: '提现状态',
    width: 160,
    dataIndex: 'status',
    hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      "0": {
        text: '提现中',
      },
      "1": {
        text: '提现成功',
      },
      "2": {
        text: '提现失败',
      },
    },
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
