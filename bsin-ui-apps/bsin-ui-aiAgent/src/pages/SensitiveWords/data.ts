import type { ProColumns } from '@ant-design/pro-table'

// 定义请求返回数据类型
export type AppColumnsItem = {
  roleName: string
  roleCode: string
  appId: string
  appName: string
  roleId: string
  remark: string
  updateTime: string
  createBy: string
  createTime: string
  option: string
  updateBy: string
}

// 定义表头
let columnsData: ProColumns<AppColumnsItem>[] = [
  {
    title: 'ID',
    dataIndex: 'serialNo',
    hideInTable: true,
  },
  // 上方查询，下方表头
  {
    title: 'ID',
    fixed: 'left',
    width: 200,
    hideInSearch: true,
    dataIndex: 'serialNo',
  },
  {
    title: '名称',
    hideInSearch: true,
    dataIndex: 'name',
    width: 140,
  },
  {
    title: 'content',
    hideInSearch: true,
    width: 100,
    valueType: 'password',
    ellipsis: true,
    dataIndex: 'content',
  },
  {
    // 0：禁用 1:启用
    title: '状态',
    hideInSearch: true,
    dataIndex: 'status',
    valueEnum: {
      '0': {
        text: '禁用',
      },
      '1': {
        text: '启用',
      },
    },
    width: 140,
  },
  {
    //： 1-private 2-public
    title: '访问权限',
    hideInSearch: true,
    dataIndex: 'accessAuthority',
    valueEnum: {
      '1': {
        text: 'private',
      },
      '2': {
        text: 'public',
      },
    },
    width: 140,
  },
  {
    title: '描述',
    hideInSearch: true,
    width: 140,
    dataIndex: 'description',
  },
  {
    title: '是否可编辑',
    hideInSearch: true,
    width: 120,
    dataIndex: 'editable',
    valueType: 'switch',
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
]
export default columnsData
