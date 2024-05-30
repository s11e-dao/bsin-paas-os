import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  sex: string;
  metadataName: string;
  metadataUrl: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '模板名称',
    dataIndex: 'templateName',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '模板编号',
    dataIndex: 'templateCode',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '模板ID',
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
    width: 170,
    fixed: 'left',
  },
  {
    title: '模板名称',
    width: 160,
    dataIndex: 'templateName',
    hideInSearch: true,
    fixed: 'left',
  },
  {
    title: '模板编号',
    width: 120,
    dataIndex: 'templateCode',
    hideInSearch: true,
  },
  {
    title: '模板内容',
    width: 220,
    dataIndex: 'templateContent',
    hideInSearch: true,
  },
  {
    title: '模板描述',
    width: 220,
    dataIndex: 'description',
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
