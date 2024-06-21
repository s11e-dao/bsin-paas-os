import type { ProColumns } from '@ant-design/pro-table'

// 定义请求返回数据类型
export type WechatMonitorColumnsItem = {
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
let columnsWechatMonitorData: ProColumns<WechatMonitorColumnsItem>[] = [
  {
    title: '微信平台ID',
    dataIndex: 'wxPlatformNo',
    hideInTable: true,
  },
  {
    title: '在线状态',
    hideInTable: true,
    dataIndex: 'alive',
    width: 70,
    valueEnum: {
      true: {
        text: '在线',
      },
      false: {
        text: '离线',
      },
    },
  },
  {
    title: '服务到期时间',
    hideInTable: true,
    dataIndex: 'expirationTime',
    width: 140,
  },
  // 上方查询，下方表头
  {
    title: '微信平台ID',
    hideInSearch: true,
    dataIndex: 'wxPlatformNo',
    width: 160,
  },
  {
    title: '智能体ID',
    hideInSearch: true,
    width: 160,
    dataIndex: 'copilotNo',
  },
  {
    title: '微信昵称',
    hideInSearch: true,
    width: 80,
    dataIndex: 'nickname',
  },
  {
    title: '在线状态',
    hideInSearch: true,
    dataIndex: 'alive',
    width: 70,
    // valueType: 'switch',
    valueEnum: {
      true: {
        text: '在线',
      },
      false: {
        text: '离线',
      },
    },
  },
  {
    title: '群聊支持',
    hideInSearch: true,
    width: 100,
    // valueType: 'switch',
    dataIndex: 'groupChat',
  },
  {
    title: '历史聊天记录总结',
    hideInSearch: true,
    width: 160,
    // valueType: 'switch',
    dataIndex: 'historyChatSummary',
  },
  {
    title: '预回复',
    hideInSearch: true,
    width: 200,
    dataIndex: 'preResp',
  },
  {
    title: '请求回复间隔',
    hideInSearch: true,
    width: 120,
    dataIndex: 'requestIntervalLimit',
  },
  {
    title: '服务到期时间',
    hideInSearch: true,
    dataIndex: 'expirationTime',
    width: 140,
  },
  {
    title: '操作',
    width: 180,
    fixed: 'right',
    hideInSearch: true,
    dataIndex: 'option',
  },
]
export default columnsWechatMonitorData
