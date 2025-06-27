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

// 定义请求返回数据类型
export type AppColumnsItem = {
  serialNo: string
  name: string
  type: string
  status: string
  accessAuthority: string
  apiKey: string
  secretKey: string
  apiBaseUrl: string
  proxyUrl: string
  proxyPort: string
  temperature: number
  maxMessages: number
  maxSummaryMessages: number
  maxRequestTokens: number
  maxRespTokens: number
  streaming: boolean
  enableSearch: boolean
  editable: boolean
  description: string
  updateTime: string
  createBy: string
  createTime: string
  option: string
  updateBy: string
}

// 定义表头
let columnsData: ProColumns<AppColumnsItem>[] = [
  {
    title: '模型名称',
    dataIndex: 'name',
    hideInTable: true,
  },
  // 上方查询，下方表头
  {
    title: '模型ID',
    fixed: 'left',
    width: 200,
    hideInSearch: true,
    dataIndex: 'serialNo',
  },
  {
    title: '模型名称',
    hideInSearch: true,
    dataIndex: 'name',
    width: 140,
  },
  {
    title: '模型类型',
    hideInSearch: true,
    dataIndex: 'type',
    width: 140,
  },
  {
    title: '描述',
    hideInSearch: true,
    width: 140,
    dataIndex: 'description',
    ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
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
  // {
  //   title: '模型KEY',
  //   hideInSearch: true,
  //   width: 340,
  //   dataIndex: 'apiKey',
  //   valueType: 'password',
  //   onCellClick: copy,
  //   ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
  // },
  {
    title: 'API地址',
    hideInSearch: true,
    width: 300,
    dataIndex: 'apiBaseUrl',
    onCellClick: copy,
    ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
  },
  {
    title: '代理地址',
    hideInSearch: true,
    width: 140,
    dataIndex: 'proxyUrl',
    ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
  },
  {
    title: '代理端口',
    hideInSearch: true,
    width: 140,
    dataIndex: 'proxyPort',
  },
  {
    title: 'temperature',
    hideInSearch: true,
    dataIndex: 'temperature',
    width: 140,
  },
  {
    title: '上下文对话数量',
    hideInSearch: true,
    width: 140,
    dataIndex: 'maxMessages',
  },
  {
    title: '触发总结对话数量',
    hideInSearch: true,
    width: 140,
    dataIndex: 'maxSummaryMessages',
  },
  {
    title: '请求最多token',
    hideInSearch: true,
    width: 140,
    dataIndex: 'maxRequestTokens',
  },
  {
    title: '回复最多token',
    hideInSearch: true,
    width: 140,
    dataIndex: 'maxRespTokens',
  },
  {
    title: '流式回复',
    hideInSearch: true,
    width: 120,
    dataIndex: 'streaming',
    valueEnum: {
      false: {
        text: '禁用',
      },
      true: {
        text: '启用',
      },
    },
  },
  {
    title: '参考搜索结果',
    hideInSearch: true,
    width: 120,
    dataIndex: 'enableSearch',
    valueType: 'switch',
    // valueEnum: {
    //   false: {
    //     text: '禁用',
    //   },
    //   true: {
    //     text: '启用',
    //   },
    // },
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
    width: 160,
    hideInSearch: true,
    dataIndex: 'createTime',
  },
  {
    title: '更新时间',
    hideInSearch: true,
    width: 160,
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
