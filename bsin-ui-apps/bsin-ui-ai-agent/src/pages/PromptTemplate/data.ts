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
    title: '名称',
    dataIndex: 'name',
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
    width: 160,
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
    width: 80,
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
    width: 80,
  },
  // 提示词模版类型：0、客户  1、 系统提示词
  {
    title: '提示词类型',
    hideInSearch: true,
    width: 100,
    dataIndex: 'type',
    valueEnum: {
      '0': {
        text: '个人模版',
      },
      '1': {
        text: '系统模版',
      },
    },
  },
  {
    title: '角色定义',
    hideInSearch: true,
    width: 300,
    dataIndex: 'systemRole',
  },

  {
    title: '限定词',
    hideInSearch: true,
    width: 300,
    dataIndex: 'determiner',
  },
  {
    title: '系统提示词',
    hideInSearch: true,
    width: 400,
    dataIndex: 'systemPromptTemplate',
  },
  {
    title: '知识库引用提示词模版',
    hideInSearch: true,
    width: 400,
    dataIndex: 'knowledgeBase',
  },
  {
    title: '历史聊天记录引用提示词模版',
    hideInSearch: true,
    dataIndex: 'chatHistorySummary',
    width: 600,
  },
  {
    title: '历史聊天记录总结提示词模版',
    hideInSearch: true,
    dataIndex: 'summaryPromptTemplate',
    width: 600,
  },
  {
    title: '聊天上下文引用提示词模版',
    hideInSearch: true,
    width: 600,
    dataIndex: 'chatBufferWindow',
  },
  {
    title: '封面图片',
    hideInSearch: true,
    width: 140,
    dataIndex: 'coverImage',
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
