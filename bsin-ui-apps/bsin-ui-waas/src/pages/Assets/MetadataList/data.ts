import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  sex: string;
  metadataName: string;
  metadataUrl: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: '主题名称',
    dataIndex: 'metadataName',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  {
    title: '性别',
    dataIndex: 'sex',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: '序号',
    dataIndex: 'order',
    valueType: 'indexBorder',
    hideInSearch: true,
    width: 50,
    fixed: 'left',
  },
  {
    title: '主题名称',
    width: 160,
    dataIndex: 'metadataName',
    hideInSearch: true,
    fixed: 'left',
  },
  {
    title: '性别',
    width: 80,
    dataIndex: 'sex',
    valueType: 'select',
    hideInSearch: true,
    valueEnum: {
      0: {
        text: '男',
      },
      1: {
        text: '女',
      },
      2: {
        text: '无',
      },
    },
  },
  {
    title: '是否为碎片',
    width: 120,
    dataIndex: 'fragmentsFlag',
    valueType: 'select',
    hideInSearch: true,
    valueEnum: {
      0: {
        text: '否',
      },
      1: {
        text: '是',
      },
    },
  },
  {
    title: '碎片编号',
    width: 120,
    dataIndex: 'fragmentsSerialNo',
    hideInSearch: true,
  },
  {
    title: '图片',
    dataIndex: 'metadataImage',
    hideInSearch: true,
    width: 120,
    valueType: 'image',
  },
  {
    title: '元数据',
    dataIndex: 'metadataUrl',
    hideInSearch: true,
    width: 450,
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
