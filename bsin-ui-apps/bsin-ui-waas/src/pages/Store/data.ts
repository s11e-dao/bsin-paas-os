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
    title: '店铺名称',
    dataIndex: 'storeName',
    valueType: 'select',
    hideInTable: true,
  },
  {
    title: '所属商户编号',
    dataIndex: 'merchantNo',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '店铺ID',
    width: 170,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '店铺名称',
    width: 160,
    dataIndex: 'storeName',
    hideInSearch: true,
  },
  {
    title: '所属租户',
    width: 170,
    dataIndex: 'tenantId',
    hideInSearch: true,
  },
  {
    title: '所属商户',
    width: 170,
    dataIndex: 'merchantNo',
    hideInSearch: true,
  },
  {
    title: '店铺logo',
    width: 160,
    dataIndex: 'logo',
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
