import type { ProColumns } from '@ant-design/pro-table'
import { Space, Table, Tag } from 'antd'
import { ColumnsType } from 'antd/lib/table'

// 定义请求返回数据类型
export type WxPlatformMenuColumnsItem = {
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
let columnsWxPlatformMenuData: ColumnsType<WxPlatformMenuColumnsItem> = [
  {
    title: '名称',
    width: 260,
    fixed: 'left',
    dataIndex: 'name',
  },
  // {
  //   title: '绑定的微信菜单模版',
  //   width: 200,
  //   dataIndex: 'wxPlatformMenuTemplateNo',
  // },
  {
    title: '菜单层级',
    dataIndex: 'level',
    width: 100,
  },
  {
    title: '菜单类型',
    dataIndex: 'type',
    width: 100,
  },
  {
    title: '菜单ID',
    width: 200,
    dataIndex: 'serialNo',
  },
  {
    title: '父级菜单ID',
    width: 200,
    dataIndex: 'parentId',
  },
  {
    title: '菜单跳转链接',
    width: 200,
    dataIndex: 'url',
    ellipsis: true,
  },
  {
    title: '菜单KEY',
    width: 200,
    dataIndex: 'menuKey',
  },
  {
    title: '小程序的appid',
    width: 200,
    dataIndex: 'appid',
  },
  {
    title: '小程序的页面路径',
    width: 200,
    dataIndex: 'pagepath',
  },
  {
    title: '发布文章id',
    width: 200,
    dataIndex: 'articleId',
  },
  {
    title: '排序',
    width: 80,
    dataIndex: 'sort',
    ellipsis: true,
  },
  {
    // 0：禁用 1:启用
    title: '状态',
    width: 80,
    dataIndex: 'status',
  },
  // // {
  // //   title: '是否可编辑',
  // //   hideInSearch: true,
  // //   dataIndex: 'editable',
  // //   width: 200,
  // //   valueType: 'switch',
  // // },
  // // {
  // //   title: '是否默认',
  // //   hideInSearch: true,
  // //   width: 200,
  // //   dataIndex: 'defaultFlag',
  // //   tooltip: '默认启用菜单',
  // // },
  // {
  //   title: '描述',
  //   width: 300,
  //   dataIndex: 'description',
  // },
  // {
  //   title: '创建人',
  //   width: 200,
  //   dataIndex: 'createBy',
  // },
  // {
  //   title: '创建时间',
  //   width: 200,
  //   dataIndex: 'createTime',
  // },
  // {
  //   title: '更新时间',
  //   width: 200,
  //   dataIndex: 'updateTime',
  // },
  {
    title: '操作',
    width: 200,
    fixed: 'right',
    dataIndex: 'option',
  },
]
export default columnsWxPlatformMenuData
