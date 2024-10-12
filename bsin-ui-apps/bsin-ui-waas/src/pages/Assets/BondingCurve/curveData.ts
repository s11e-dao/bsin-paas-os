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

const columnsCurveData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '曲线编号',
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInTable: true,
    width: 100,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '积分曲线类型',
    dataIndex: 'type',
    hideInTable: true,
    width: 50,
    fieldProps: {
      maxLength: 20,
    },
    // bancor bondingcurve cny
    valueEnum: {
      '0': {
        text: 'bancor',
      },
      '1': {
        text: 'sigmoid',
      },
    },
  },

  // table里面的内容
  {
    title: '曲线编号',
    width: 180,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '商户编号',
    width: 180,
    dataIndex: 'merchantNo',
    hideInSearch: true,
  },
  {
    title: '名称',
    width: 160,
    dataIndex: 'name',
    hideInSearch: true,
  },
  {
    title: '符号',
    width: 160,
    dataIndex: 'symbol',
    hideInSearch: true,
  },
  {
    title: '小数点',
    width: 160,
    dataIndex: 'decimals',
    hideInSearch: true,
  },
  {
    title: '版本号',
    width: 100,
    hideInSearch: true,
    dataIndex: 'version',
    fixed: 'right',
  },
  {
    title: '供应上限',
    width: 100,
    hideInSearch: true,
    dataIndex: 'cap',
    fixed: 'right',
  },
  {
    title: '初始定价',
    width: 100,
    hideInSearch: true,
    dataIndex: 'initialPrice',
    fixed: 'right',
  },
  {
    title: '最终定价',
    width: 100,
    hideInSearch: true,
    dataIndex: 'finalPrice',
    fixed: 'right',
  },
  {
    title: '拉伸变换',
    width: 100,
    hideInSearch: true,
    dataIndex: 'flexible',
    fixed: 'right',
  },
  {
    title: '类型',
    width: 80,
    hideInSearch: true,
    dataIndex: 'type',
    // bancor bondingcurve cny
    valueEnum: {
      '0': {
        text: 'bancor',
      },
      '1': {
        text: 'sigmoid',
      },
    },
    fixed: 'right',
  },
  {
    title: '状态',
    width: 80,
    hideInSearch: true,
    //
    valueEnum: {
      '0': {
        text: '冻结',
      },
      '1': {
        text: '正常',
      },
    },
    dataIndex: 'status',
    fixed: 'right',
  },
  {
    title: '描述',
    width: 160,
    dataIndex: 'description',
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

export default columnsCurveData;
