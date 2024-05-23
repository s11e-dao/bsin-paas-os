import type { ProColumns } from '@ant-design/pro-table';
import type { DictColumnsItem, DictItemColumnsItem } from './data.d';

// 定义表头
let productColumnsData: ProColumns<DictColumnsItem>[] = [
  // 查询
  {
    title: '名称',
    dataIndex: 'productName',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '编码',
    dataIndex: 'productCode',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // 表头
  {
    title: '序号',
    dataIndex: 'index',
    valueType: 'indexBorder',
    width: 50,
    fixed: 'left',
  },
  {
    title: 'ID',
    hideInSearch: true,
    dataIndex: 'productId',
    ellipsis: true,
    width: 160,
  },
  {
    title: '产品名称',
    hideInSearch: true,
    dataIndex: 'productName',
    ellipsis: true,
    width: 160,
  },
  {
    title: '产品编码',
    hideInSearch: true,
    dataIndex: 'productCode',
    ellipsis: true,
    width: 160,
  },
  {
    title: '产品描述',
    hideInSearch: true,
    dataIndex: 'remark',
    width: 160,
  },
  {
    title: '创建时间',
    hideInSearch: true,
    dataIndex: 'createTime',
    valueType: 'dateTime',
    width: 160,
  },
  {
    title: '操作',
    fixed: 'right',
    valueType: 'option',
    width: 170,
  },
];

// 字典项查看表头
export const productAppColumnsData: ProColumns<DictItemColumnsItem>[] = [
  {
    title: '序号',
    dataIndex: 'index',
    valueType: 'indexBorder',
    width: 50,
    fixed: 'left',
  },
  {
    title: '应用编号',
    width: 180,
    hideInSearch: true,
    dataIndex: 'appCode',
  },
  {
    title: '应用名称',
    width: 120,
    hideInSearch: true,
    dataIndex: 'appName',
  },
  {
    title: '应用类型',
    width: 90,
    hideInSearch: true,
    dataIndex: 'baseFlag',
    valueEnum: {
      1: { text: '基础应用' },
      0: { text: '普通应用' },
    },
  },
  {
    title: '业务角色类型',
    width: 90,
    hideInSearch: true,
    dataIndex: 'bizRoleType',
    valueEnum: {
      1: { text: '运营平台' },
      2: { text: '租户平台' },
      3: { text: '商户' },
      4: { text: '客户' },
      99: { text: '无' },
    },
  },
  {
    title: '应用状态',
    width: 90,
    hideInSearch: true,
    dataIndex: 'status',
    valueEnum: {
      1: { text: '启用', status: 'Processing' },
      0: { text: '未启用', status: 'Default' },
    },
  },
  {
    title: '应用描述',
    width: 200,
    hideInSearch: true,
    dataIndex: 'remark',
  },
  {
    title: '操作',
    fixed: 'right',
    valueType: 'option',
    width: 80,
  },
];

export default productColumnsData;
