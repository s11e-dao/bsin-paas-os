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
    width: 140,
  },
  {
    title: '描述',
    hideInSearch: true,
    width: 140,
    dataIndex: 'description',
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
  // 检索方式：1.语义检索 2.增强语义检索 3.混合检索
  {
    title: '检索方式',
    hideInSearch: true,
    width: 160,
    dataIndex: 'retrievalMethod',
    valueEnum: {
      '1': {
        text: '语义检索',
      },
      '2': {
        text: '增强语义检索',
      },
      '3': {
        text: '混合检索',
      },
    },
  },
  {
    title: 'topK',
    hideInSearch: true,
    width: 100,
    dataIndex: 'maxResults',
  },
  {
    title: '相似度',
    hideInSearch: true,
    width: 100,
    dataIndex: 'minScore',
  },
  // ：300-3000，中文1字1.7token, 英文1字=1token
  {
    title: '引用上限',
    hideInSearch: true,
    width: 80,
    dataIndex: 'quoteLimit',
  },
  {
    title: '空搜索回复',
    hideInSearch: true,
    dataIndex: 'emptyResp',
    width: 140,
  },
  {
    title: '向量维度',
    hideInSearch: true,
    width: 80,
    dataIndex: 'dimension',
  },
  {
    title: '分段最多token数',
    hideInSearch: true,
    width: 140,
    dataIndex: 'segmentSizeInTokens',
  },
  {
    title: '最大重叠token数',
    hideInSearch: true,
    width: 140,
    dataIndex: 'overlapSizeInTokens',
  },
  // 分词模型：1、OpenAi  2、Bert  3、Qwen
  {
    title: '分词模型',
    hideInSearch: true,
    width: 300,
    dataIndex: 'tokenizerModel',
    valueEnum: {
      OpenAi: {
        text: 'OpenAi',
      },
      Bert: {
        text: 'Bert',
      },
      Qwen: {
        text: 'Qwen',
      },
    },
  },
  // 文档分词器：1、ByCharacter 2、ByLine  3、ByParagraph 4、ByRegex 5、BySentence 6、ByWord
  {
    title: '文档分词器',
    hideInSearch: true,
    width: 300,
    dataIndex: 'documentSplitter',
    // valueEnum: {
    //   '1': {
    //     text: 'OpenAiTokenizer(GPT_3_5_TURBO)',
    //   },
    //   '2': {
    //     text: 'Bert',
    //   },
    //   '3': {
    //     text: '其他',
    //   },
    // },
  },

  {
    title: 'apiKey',
    hideInSearch: true,
    width: 140,
    dataIndex: 'apiKey',
  },

  {
    title: 'apiBaseUrl',
    hideInSearch: true,
    width: 140,
    dataIndex: 'apiBaseUrl',
  },

  {
    title: '代理地址',
    hideInSearch: true,
    width: 140,
    dataIndex: 'proxyUrl',
  },
  {
    title: '代理端口',
    hideInSearch: true,
    width: 140,
    dataIndex: 'proxyPort',
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
