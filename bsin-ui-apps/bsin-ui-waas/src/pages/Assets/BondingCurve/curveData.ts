import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  // 基础字段
  serialNo: string;
  merchantNo: string;
  name: string;
  symbol: string;
  decimals: number;
  version: string;
  cap: string;
  initialPrice: string;
  finalPrice: string;
  flexible: number;
  type: string;
  status: string;
  description: string;
  createTime: string;
  tenantId: string;
  accountNo: string;
  
  // 分段衰减释放参数
  totalTargetToken: number;
  estimatedLaborValue: number;
  decayFactor: number;
  levelWidth: number;
  totalLevels: number;
  firstLevelReward: number;
  releaseThreshold: number;
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
      '2': {
        text: 'cny',
      },
    },
  },
  {
    title: '曲线名称',
    dataIndex: 'name',
    hideInTable: true,
    width: 120,
    fieldProps: {
      maxLength: 50,
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
    width: 100,
    dataIndex: 'decimals',
    hideInSearch: true,
  },
  {
    title: '版本号',
    width: 100,
    hideInSearch: true,
    dataIndex: 'version',
  },
  {
    title: '供应上限',
    width: 120,
    hideInSearch: true,
    dataIndex: 'cap',
  },
  {
    title: '初始定价',
    width: 100,
    hideInSearch: true,
    dataIndex: 'initialPrice',
  },
  {
    title: '最终定价',
    width: 100,
    hideInSearch: true,
    dataIndex: 'finalPrice',
  },
  {
    title: '拉伸变换',
    width: 100,
    hideInSearch: true,
    dataIndex: 'flexible',
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
      '2': {
        text: 'cny',
      },
    },
    fixed: 'right',
  },
  {
    title: '状态',
    width: 80,
    hideInSearch: true,
    valueEnum: {
      '0': {
        text: '冻结',
      },
      '1': {
        text: '正常',
      },
    },
    dataIndex: 'status',
  },
  {
    title: '描述',
    width: 160,
    dataIndex: 'description',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '总积分目标',
    width: 120,
    dataIndex: 'totalTargetToken',
    hideInSearch: true,
    render: (_, record) => record.totalTargetToken ? record.totalTargetToken.toLocaleString() : '-',
  },
  {
    title: '预估劳动价值',
    width: 120,
    dataIndex: 'estimatedLaborValue',
    hideInSearch: true,
    render: (_, record) => record.estimatedLaborValue ? record.estimatedLaborValue.toLocaleString() : '-',
  },
  {
    title: '衰减系数',
    width: 100,
    dataIndex: 'decayFactor',
    hideInSearch: true,
    render: (_, record) => record.decayFactor ? record.decayFactor.toFixed(4) : '-',
  },
  {
    title: '档位宽度',
    width: 100,
    dataIndex: 'levelWidth',
    hideInSearch: true,
    render: (_, record) => record.levelWidth ? record.levelWidth.toLocaleString() : '-',
  },
  {
    title: '档位总数',
    width: 100,
    dataIndex: 'totalLevels',
    hideInSearch: true,
    render: (_, record) => record.totalLevels ? `${record.totalLevels}档` : '-',
  },
  {
    title: '首档奖励',
    width: 120,
    dataIndex: 'firstLevelReward',
    hideInSearch: true,
    render: (_, record) => record.firstLevelReward ? record.firstLevelReward.toLocaleString() : '-',
  },
  {
    title: '释放阈值',
    width: 100,
    dataIndex: 'releaseThreshold',
    hideInSearch: true,
    render: (_, record) => record.releaseThreshold ? record.releaseThreshold.toLocaleString() : '-',
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
