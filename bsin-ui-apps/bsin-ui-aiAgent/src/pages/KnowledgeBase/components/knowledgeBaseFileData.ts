import type { ProColumns } from '@ant-design/pro-table'

// 定义请求返回数据类型
export type KnowledgeBaseFileColumnsItem = {
  updateTime: string
  createBy: string
  createTime: string
  option: string
  updateBy: string
}

// 定义表头
let KnowledgeBaseFileColumnsData: ProColumns<KnowledgeBaseFileColumnsItem>[] = [
  {
    title: 'ID',
    dataIndex: 'serialNo',
    hideInTable: true,
  },
  // 类型：1、url 2、文件(FileSystemDocumentLoader)-前端先上传至OSS，url的形式提交至后台 3、文件夹 4、公众号
  {
    title: '类型',
    hideInTable: true,
    dataIndex: 'type',
    valueEnum: {
      '1': {
        text: '文件',
      },
      '2': {
        text: '文件',
      },
      '3': {
        text: '文件夹',
      },
      '4': {
        text: '公众号',
      },
    },
  },
  // 文件类型：1、pdf 2、markdown 3、doc
  {
    title: '文件类型',
    hideInTable: true,
    dataIndex: 'fileType',
    // valueEnum: {
    //   '1': {
    //     text: 'pdf',
    //   },
    //   '2': {
    //     text: 'markdown',
    //   },
    //   '3': {
    //     text: 'doc',
    //   },
    // },
  },
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
  // {
  //   title: '绑定知识库ID',
  //   width: 220,
  //   hideInSearch: true,
  //   dataIndex: 'knowledgeBaseNo',
  // },

  {
    title: '名称',
    hideInSearch: true,
    dataIndex: 'name',
    width: 200,
    ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
  },
  // 类型：1、url-不需要上传文件，直接提供url 2、文件(FileSystemDocumentLoader)-前端先上传至OSS，url的形式提交至后台 3、文件夹 4、公众号 */
  {
    title: '类型',
    hideInSearch: true,
    width: 80,
    dataIndex: 'type',
    valueEnum: {
      '1': {
        text: 'url',
      },
      '2': {
        text: '文件',
      },
      '3': {
        text: '文件夹',
      },
      '4': {
        text: '公众号',
      },
    },
  },
  // 文件类型：1、pdf 2、markdown 3、doc
  {
    title: '文件类型',
    hideInSearch: true,
    width: 150,
    dataIndex: 'fileType',
  },
  // 数据总量: 分段后的数量
  {
    title: '数据总量',
    hideInSearch: true,
    width: 80,
    dataIndex: 'chunkNum',
  },
  {
    title: 'OSS存储路径',
    hideInSearch: true,
    width: 400,
    dataIndex: 'fileUri',
    ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
    tooltip: 'fileUri',
  },
  {
    title: '本地存储路径',
    hideInSearch: true,
    width: 400,
    dataIndex: 'localPath',
    ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
  },
  {
    title: '描述',
    hideInSearch: true,
    width: 200,
    dataIndex: 'description',
    ellipsis: true, //ellipsis设置成true ，那么表格就会变成fixed，不会被内容撑开，会一直按照自己定义的宽度
  },
  {
    title: '创建人',
    hideInSearch: true,
    width: 200,
    dataIndex: 'createBy',
  },
  {
    title: '创建时间',
    width: 200,
    hideInSearch: true,
    dataIndex: 'createTime',
  },
  {
    title: '更新人',
    hideInSearch: true,
    width: 200,
    dataIndex: 'updateBy',
  },
  {
    title: '更新时间',
    hideInSearch: true,
    width: 200,
    dataIndex: 'updateTime',
  },
  {
    title: '操作',
    width: 200,
    fixed: 'right',
    hideInSearch: true,
    dataIndex: 'option',
  },
]
export default KnowledgeBaseFileColumnsData
