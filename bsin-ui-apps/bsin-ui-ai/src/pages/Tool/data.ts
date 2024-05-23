import type { ProColumns } from '@ant-design/pro-table'
import { Space, Table, Tag } from 'antd'

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
  templateEnable: boolean
  systemRoleEnable: boolean
  contextEnable: boolean
  createTime: string
  option: string
  updateBy: string
}

// 定义表头
let columnsData: ProColumns<AppColumnsItem>[] = [
  {
    title: '工具名称',
    dataIndex: 'name',
    hideInTable: true,
  },
  // 上方查询，下方表头
  {
    title: '工具ID',
    fixed: 'left',
    width: 200,
    hideInSearch: true,
    dataIndex: 'serialNo',
  },
  {
    title: '工具名称',
    hideInSearch: true,
    dataIndex: 'name',
    width: 140,
  },
  {
    title: '工具编码',
    width: 200,
    hideInSearch: true,
    dataIndex: 'code',
  },
  {
    title: '请求地址',
    hideInSearch: true,
    width: 140,
    dataIndex: 'url',
  },
  {
    title: '接口异常回复',
    hideInSearch: true,
    width: 140,
    dataIndex: 'exceptionResponse',
  },
  {
    title: '是否可编辑',
    hideInSearch: true,
    width: 120,
    dataIndex: 'editable',
    valueType: 'switch',
  },
  {
    title: '创建时间',
    width: 180,
    hideInSearch: true,
    dataIndex: 'createTime',
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
