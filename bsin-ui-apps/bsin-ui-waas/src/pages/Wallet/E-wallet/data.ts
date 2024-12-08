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

  // table里面的内容
  {
    title: 'ID',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '账号',
    width: 160,
    dataIndex: 'accountNo',
    hideInSearch: true,
  },
  {
    title: '业务角色号',
    width: 160,
    dataIndex: 'bizRoleTypeNo',
    hideInSearch: true,
  },
  {
    title: '金额',
    width: 160,
    dataIndex: 'amount',
    hideInSearch: true,
  },
  {
    title: '交易类型',
    width: 120,
    hideInSearch: true,
    dataIndex: 'orderType',
    valueType: 'select',
    valueEnum: {
      "10": {
        text: '待支付',
      },
      "20": {
        text: '已取消',
      },
      "30": {
        text: '已完成',
      },
    },
  },
  {
    title: '订单号',
    width: 160,
    dataIndex: 'orderNo',
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
];

export default columnsData;
