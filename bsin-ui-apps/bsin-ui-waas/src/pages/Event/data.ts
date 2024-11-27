import type { ProColumns } from '@ant-design/pro-table';
import type { DictColumnsItem, DictItemColumnsItem } from './data.d';

// 定义表头
let DictColumnsData: ProColumns<DictColumnsItem>[] = [
  // 查询
  {
    title: '事件名称',
    dataIndex: 'eventName',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '事件编码',
    dataIndex: 'eventCode',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '事件级别',
    dataIndex: 'eventLevel',
    hideInTable: true,
    valueType: 'select',
    valueEnum: {
      1: { text: '平台级' },
      2: { text: '商户级' },
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
    title: '事件名称',
    hideInSearch: true,
    dataIndex: 'eventName',
    ellipsis: true,
    width: 160,
    fixed: 'left',
  },
  {
    title: '事件编码',
    hideInSearch: true,
    dataIndex: 'eventCode',
    ellipsis: true,
    width: 160,
    fixed: 'left',
  },
  {
    title: '事件级别',
    hideInSearch: true,
    dataIndex: 'eventLevel',
    width: 160,
    valueType: 'select',
    valueEnum: {
      1: { text: '平台级' },
      2: { text: '商户级' },
    },
  },
  {
    title: '备注信息',
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
    width: 150,
  },
];

// 字典项查看表头
export const DictItemColumnsData: ProColumns<DictItemColumnsItem>[] = [
  {
    title: '序号',
    dataIndex: 'index',
    valueType: 'indexBorder',
    width: 50,
    fixed: 'left',
  },
  {
    title: '事件编码',
    hideInSearch: true,
    dataIndex: 'eventCode',
    ellipsis: true,
    width: 160,
  },
  {
    title: '模型类型',
    hideInSearch: true,
    dataIndex: 'modelType',
    ellipsis: true,
    width: 160,
    fixed: 'left',
  },
  {
    title: '模型ID',
    hideInSearch: true,
    dataIndex: 'modelNo',
    width: 160,
  },
  {
    title: '操作',
    fixed: 'right',
    valueType: 'option',
    width: 140,
  },
];

export default DictColumnsData;
