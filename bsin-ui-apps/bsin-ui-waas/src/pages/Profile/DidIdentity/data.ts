import type { ProColumns } from '@ant-design/pro-table';

export type columnsDataType = {
  serialNo: string;
  name: string;
  did: string;
  symbol: string;
  description: string;
  createTime: string;
  updateTime: string;
  bizRoleType: string;
  bizRoleTypeNo: string;
};

const columnsData: ProColumns<columnsDataType>[] = [
  // 配置搜索框
  {
    title: 'DID名称',
    dataIndex: 'name',
    hideInTable: true,
    fieldProps: {
      maxLength: 50,
    },
  },
  {
    title: 'DID标识符',
    dataIndex: 'did',
    hideInTable: true,
    fieldProps: {
      maxLength: 100,
    },
  },
  {
    title: '符号',
    dataIndex: 'symbol',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },

  // table里面的内容
  {
    title: 'ID',
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
    width: 170,
  },
  {
    title: 'DID名称',
    width: 160,
    dataIndex: 'name',
    hideInSearch: true,
    fixed: 'left',
  },
  {
    title: 'DID标识符',
    width: 280,
    dataIndex: 'did',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '符号',
    width: 120,
    dataIndex: 'symbol',
    hideInSearch: true,
  },
  {
    title: '描述',
    width: 220,
    dataIndex: 'description',
    hideInSearch: true,
    ellipsis: true,
  },
  {
    title: '业务角色类型',
    width: 120,
    dataIndex: 'bizRoleType',
    hideInSearch: true,
  },
  {
    title: '业务角色编号',
    width: 150,
    dataIndex: 'bizRoleTypeNo',
    hideInSearch: true,
  },
  {
    title: '创建时间',
    width: 160,
    dataIndex: 'createTime',
    hideInSearch: true,
  },
  {
    title: '更新时间',
    width: 160,
    dataIndex: 'updateTime',
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
