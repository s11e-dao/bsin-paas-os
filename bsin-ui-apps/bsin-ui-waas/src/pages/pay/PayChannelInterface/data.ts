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
    title: '接口名称',
    dataIndex: 'payInterfaceName',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '接口代码',
    dataIndex: 'payInterfaceCode',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  // {
  //   title: 'ID',
  //   width: 190,
  //   dataIndex: 'serialNo',
  //   hideInSearch: true,
  // },
  {
    title: '接口代码',
    width: 160,
    dataIndex: 'payInterfaceCode',
    hideInSearch: true,
  },
  {
    title: '接口名称',
    width: 160,
    dataIndex: 'payInterfaceName',
    hideInSearch: true,
  },
  {
    title: '支付方式',
    width: 160,
    dataIndex: 'wayCode',
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
