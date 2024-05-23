import type { ProColumns } from '@ant-design/pro-table'

export type columnsFunctionSubscribeDataType = {
  order: number
  acName: string
  custNo: string
  acNo: string
  balance: string
  custType: string
  openAcDate: string
  status: string
  startTime: string
  endTime: string
}

const columnsFunctionSubscribeData: ProColumns<
  columnsFunctionSubscribeDataType
>[] = [
  // 配置搜索框
  {
    title: '服务ID',
    dataIndex: 'id',
    hideInTable: true,
    fieldProps: {
      maxLength: 20,
    },
  },
  // 应用|功能状态：0、待缴费 1、正常 2、欠费停止 3、冻结
  {
    title: '状态',
    dataIndex: 'status',
    valueType: 'select',
    hideInTable: true,
    valueEnum: {
      '0': {
        text: '待缴费',
      },
      '1': {
        text: '待审核',
      },
      '2': {
        text: '正常',
      },
      '3': {
        text: '欠费停止',
      },
      '4': {
        text: '冻结',
      },
    },
  },

  // table里面的内容
  {
    title: 'ID',
    width: 170,
    dataIndex: 'serialNo',
    fixed: 'left',
    hideInSearch: true,
  },
  {
    title: '服务|功能名称',
    width: 170,
    dataIndex: 'name',
    hideInSearch: true,
  },
  {
    title: '类型',
    width: 120,
    dataIndex: 'type',
    valueType: 'select',
    hideInSearch: true,
    valueEnum: {
      '0': {
        text: '功能模版',
      },
      '1': {
        text: '应用',
      },
      '2': {
        text: '功能',
      },
      '3': {
        text: '平台基础服务',
      },
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 80,
    valueType: 'select',
    hideInSearch: true,
    valueEnum: {
      '0': {
        text: '待缴费',
      },
      '1': {
        text: '待审核',
      },
      '2': {
        text: '正常',
      },
      '3': {
        text: '欠费停止',
      },
      '4': {
        text: '冻结',
      },
    },
  },
  {
    title: '智能体数量',
    width: 100,
    dataIndex: 'copilotNum',
    hideInSearch: true,
  },
  {
    title: '知识库数量',
    width: 100,
    dataIndex: 'knowledgeBaseNum',
    hideInSearch: true,
  },
  {
    title: '知识库文件数量',
    width: 140,
    dataIndex: 'knowledgeBaseFileNum',
    hideInSearch: true,
  },
  {
    title: '公众号数量',
    width: 100,
    dataIndex: 'mpNum',
    hideInSearch: true,
  },
  {
    title: '企业微信数量',
    width: 120,
    dataIndex: 'cpNum',
    hideInSearch: true,
  },
  {
    title: '个人微信数量',
    width: 120,
    dataIndex: 'wechatNum',
    hideInSearch: true,
  },
  {
    title: '支持群聊回复',
    width: 120,
    dataIndex: 'groupChat',
    valueType: 'switch',
    hideInSearch: true,
  },
  {
    title: '历史聊天记录总结',
    hideInSearch: true,
    width: 140,
    valueType: 'switch',
    dataIndex: 'historyChatSummary',
  },
  {
    title: '小程序数量',
    width: 120,
    dataIndex: 'miniappNum',
    hideInSearch: true,
  },
  {
    title: '菜单模版数量',
    width: 120,
    dataIndex: 'menuTemplateNum',
    hideInSearch: true,
  },
  {
    title: '敏感词数量',
    width: 120,
    dataIndex: 'sensitiveWordsNum',
    hideInSearch: true,
  },
  {
    title: 'token余额',
    width: 100,
    dataIndex: 'tokenBalance',
    hideInSearch: true,
  },
  {
    title: '花费的token',
    width: 100,
    dataIndex: 'tokenUsed',
    hideInSearch: true,
  },
  {
    title: '订阅价格(￥)',
    width: 120,
    dataIndex: 'price',
    hideInSearch: true,
  },
  {
    title: '服务时间(天)',
    width: 120,
    dataIndex: 'serviceDuration',
    hideInSearch: true,
  },
  {
    title: '服务开始时间',
    width: 160,
    dataIndex: 'startTime',
    hideInSearch: true,
  },
  {
    title: '服务结束时间',
    width: 160,
    dataIndex: 'endTime',
    hideInSearch: true,
  },
  {
    title: '支付凭证',
    width: 100,
    dataIndex: 'payReceipt',
    valueType: 'image',
    hideInSearch: true,
  },

  {
    title: '操作',
    width: 190,
    hideInSearch: true,
    dataIndex: 'option',
    fixed: 'right',
  },
]

export default columnsFunctionSubscribeData
