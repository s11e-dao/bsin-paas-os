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
    title: '等级Id',
    dataIndex: 'gradeNo',
  },
  {
    title: '等级编号',
    dataIndex: 'gradeNum',
  },
  {
    title: '等级名称',
    dataIndex: 'name',
  },
  // table里面的内容
  {
    title: '会员编号',
    width: 190,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '租户ID',
    width: 160,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '商户ID',
    width: 160,
    dataIndex: 'merchantNo',
    hideInSearch: true,
  },
  {
    title: '客户ID',
    width: 160,
    dataIndex: 'customerNo',
    hideInSearch: true,
  },
  {
    title: '昵称',
    width: 160,
    dataIndex: 'nickname',
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
