import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  payChannelCode: string;
  payChannelName: string;
  configPageType: number;
  params: string;
  wayCode: string;
  icon: string;
  status: number;
  isNormalMerchanMode: boolean;
  isServiceSubMerchantMode: boolean;
  normalMerchantParams: string;
  specialMerchantParams: string;
  serviceSubMerchantParams: string;
  remark: string;
  createTime: string;
  updateTime: string;
  tenantId: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 搜索字段
  {
    title: '通道代码',
    dataIndex: 'payChannelCode',
    hideInTable: true,
    fieldProps: {
      maxLength: 32,
    },
    tooltip: '支付通道的唯一标识代码，如：wxpay、alipay',
  },
  {
    title: '通道名称',
    dataIndex: 'payChannelName',
    hideInTable: true,
    fieldProps: {
      maxLength: 50,
    },
    tooltip: '支付通道的显示名称',
  },
  {
    title: '状态',
    dataIndex: 'status',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      0: { text: '停用', status: 'Error' },
      1: { text: '启用', status: 'Success' },
    },
    tooltip: '接口的启用状态',
  },

  // 表格显示字段
  {
    title: '通道代码',
    width: 120,
    dataIndex: 'payChannelCode',
    fixed: 'left',
    hideInSearch: true,
    tooltip: '支付通道的唯一标识代码',
  },
  {
    title: '通道名称',
    width: 150,
    dataIndex: 'payChannelName',
    hideInSearch: true,
    tooltip: '支付通道的显示名称',
  },
  {
    title: '配置页面类型',
    width: 120,
    dataIndex: 'configPageType',
    hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      1: { text: 'JSON渲染' },
      2: { text: '自定义' },
    },
    tooltip: '支付参数配置页面的渲染方式',
  },
  {
    title: '支付参数',
    width: 200,
    dataIndex: 'params',
    hideInSearch: true,
    ellipsis: true,
    tooltip: '支付通道的参数配置定义，JSON格式',
  },
  {
    title: '支付方式',
    width: 200,
    dataIndex: 'wayCode',
    hideInSearch: true,
    ellipsis: true,
    tooltip: '支持的支付方式列表，如：["wxpay_jsapi", "wxpay_bar"]',
  },
  {
    title: '图标',
    width: 80,
    dataIndex: 'icon',
    hideInSearch: true,
    tooltip: '页面展示的卡片图标',
  },
  {
    title: '状态',
    width: 80,
    dataIndex: 'status',
    hideInSearch: true,
    valueType: 'select',
    valueEnum: {
      0: { text: '停用', status: 'Error' },
      1: { text: '启用', status: 'Success' },
    },
    tooltip: '通道的启用状态',
  },
  {
    title: '备注',
    width: 150,
    dataIndex: 'remark',
    hideInSearch: true,
    ellipsis: true,
    tooltip: '通道的备注信息',
  },
  {
    title: '租户ID',
    width: 120,
    dataIndex: 'tenantId',
    hideInSearch: true,
    tooltip: '所属租户ID',
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
    valueType: 'dateTime',
    tooltip: '通道创建时间',
  },
  {
    title: '更新时间',
    width: 160,
    dataIndex: 'updateTime',
    hideInSearch: true,
    valueType: 'dateTime',
    tooltip: '通道最后更新时间',
  },
  {
    title: '操作',
    width: 150,
    hideInSearch: true,
    dataIndex: 'action',
    fixed: 'right',
    tooltip: '对通道的操作选项',
  },
];

export default columnsData;
