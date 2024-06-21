import type { ProColumns } from '@ant-design/pro-table'

// 定义请求返回数据类型
export type KnowledgeBaseFileChunkColumnsItem = {
  updateTime: string
  createBy: string
  createTime: string
  option: string
  updateBy: string
}

// 定义表头
let KnowledgeBaseFileChunkColumnsData: ProColumns<
  KnowledgeBaseFileChunkColumnsItem
>[] = [
  {
    title: 'chunkNo',
    dataIndex: 'chunkNo',
    hideInTable: true,
  },
  // 上方查询，下方表头
  {
    title: 'chunkNo',
    fixed: 'left',
    width: 100,
    hideInSearch: true,
    dataIndex: 'chunkNo',
  },
  // {
  //   title: '绑定知识库文件ID',
  //   width: 100,
  //   hideInSearch: true,
  //   dataIndex: 'knowledgeBaseFileNo',
  // },

  {
    title: '索引内容(向量片段内容)',
    hideInSearch: true,
    dataIndex: 'chunkText',
    width: 200,
    // ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
  },
  {
    title: 'chunk补充内容',
    hideInSearch: true,
    width: 200,
    dataIndex: 'chunkContent',
  },
  {
    title: '操作',
    width: 100,
    fixed: 'right',
    hideInSearch: true,
    dataIndex: 'option',
  },
]
export default KnowledgeBaseFileChunkColumnsData
