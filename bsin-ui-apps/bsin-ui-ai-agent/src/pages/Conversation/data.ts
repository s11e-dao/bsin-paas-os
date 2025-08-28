import type { ProColumns } from '@ant-design/pro-table'
import { message } from 'antd'

//点击复制
const copy = (e: any) => {
  console.log(e)
  const range = document.createRange()

  window.getSelection()?.removeAllRanges()

  //这个地方有时候会进行省略,做下判断如果是省略部分,就直接return,否则复制
  if (e.target.innerText.indexOf('...') != -1) {
    return
  } else {
    range.selectNode(e.target)
  }
  window.getSelection()?.addRange(range)
  const sucful = document.execCommand('copy')
  if (sucful) {
    message.success('复制成功')
  }
}

// 定义对话请求返回数据类型
export type AppColumnsItem = {
  serialNo: number
  tenantId: string
  bizRoleTypeNo: string
  bizRoleType: string
  agentNo: string
  agentName: string
  agentIconUrl: string
  status: number
  title: string
  unreadCount: string
  lastMessageNo: string
  lastMessageContent: string
  delFlag: boolean
  createTime: string
  option: string
}

// 定义对话表头
let columnsData: ProColumns<AppColumnsItem>[] = [
  {
    title: '会话标题',
    dataIndex: 'title',
    hideInTable: true,
  },
  // 上方查询，下方表头
  {
    title: '对话ID',
    fixed: 'left',
    width: 120,
    hideInSearch: true,
    dataIndex: 'serialNo',
  },
  {
    title: '会话标题',
    hideInSearch: true,
    dataIndex: 'title',
    width: 200,
    ellipsis: true,
  },
  {
    title: '智能体名称',
    hideInSearch: true,
    dataIndex: 'agentName',
    width: 140,
  },
  {
    title: '用户类型',
    hideInSearch: true,
    dataIndex: 'bizRoleType',
    width: 120,
  },
  {
    title: '最后消息',
    hideInSearch: true,
    width: 250,
    dataIndex: 'lastMessageContent',
    ellipsis: true,
  },
  {
    title: '未读消息数',
    hideInSearch: true,
    width: 100,
    dataIndex: 'unreadCount',
  },
  {
    // 1-活跃，2-已结束，3-已删除，4-已归档
    title: '状态',
    hideInSearch: true,
    dataIndex: 'status',
    valueEnum: {
      1: {
        text: '活跃',
        status: 'Success',
      },
      2: {
        text: '已结束',
        status: 'Default',
      },
      3: {
        text: '已删除',
        status: 'Error',
      },
      4: {
        text: '已归档',
        status: 'Warning',
      },
    },
    width: 100,
  },
  {
    title: '创建时间',
    width: 160,
    hideInSearch: true,
    dataIndex: 'createTime',
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
