import type { ProColumns } from '@ant-design/pro-table';

// 定义请求返回数据类型
export type AppColumnsItem = {
  roleName: string;
  roleCode: string;
  appId: string;
  appName: string;
  roleId: string;
  remark: string;
  updateTime: string;
  createBy: string;
  createTime: string;
  option: string;
  updateBy: string;
};

// 定义表头
let columnsData: ProColumns<AppColumnsItem>[] = [
  {
    title: '名称',
    dataIndex: 'roleName',
    hideInTable: true,
  },
  {
    title: '编码',
    dataIndex: 'roleCode',
    hideInTable: true,
  },
  // 上方查询，下方表头
  {
    title: '决策集ID',
    fixed: 'left',
    width: 200,
    hideInSearch: true,
    dataIndex: 'appId',
  },
  {
    title: '决策集名称',
    hideInSearch: true,
    dataIndex: 'appName',
    width: 140,
  },
  {
    title: '决策集编码',
    hideInSearch: true,
    width: 200,
    dataIndex: 'roleCode',
  },
  {
    title: '决策集描述',
    hideInSearch: true,
    width: 140,
    dataIndex: 'remark',
  },
  {
    title: '创建人',
    hideInSearch: true,
    width: 140,
    dataIndex: 'createBy',
  },
  {
    title: '创建时间',
    width: 180,
    hideInSearch: true,
    dataIndex: 'createTime',
  },
  {
    title: '更新人',
    hideInSearch: true,
    width: 140,
    dataIndex: 'updateBy',
  },
  {
    title: '更新时间',
    hideInSearch: true,
    width: 180,
    dataIndex: 'updateTime',
  },
  {
    title: '操作',
    width: 180,
    fixed: 'right',
    hideInSearch: true,
    dataIndex: 'option',
  },
];
export default columnsData;
