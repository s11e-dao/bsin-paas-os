import type { ProColumns } from '@ant-design/pro-table';

// 定义请求返回数据类型
export type FunctionColumnsItem = {
  appId: string;
  appFunctionId: string;
  functionName: string;
  functionCode: string;
  remark: string;
};

// 定义表头
let columnsData: ProColumns<FunctionColumnsItem>[] = [

  // 上方查询，下方表头
  {
    title: '应用ID',
    fixed: 'left',
    width: 200,
    hideInSearch: true,
    dataIndex: 'appId',
  },
  {
    title: '功能ID',
    width: 180,
    hideInSearch: true,
    dataIndex: 'appFunctionId',
  },
  {
    title: '功能名称',
    width: 120,
    hideInSearch: true,
    dataIndex: 'functionName',
  },
  {
    title: '功能类型',
    width: 90,
    hideInSearch: true,
    dataIndex: 'baseFlag',
    valueEnum: {
      true: { text: '基础功能' },
      false: { text: '增值功能' },
    },
  },
  {
    title: '功能编号',
    width: 80,
    hideInSearch: true,
    dataIndex: 'functionCode',
  },
  {
    title: '功能描述',
    width: 180,
    hideInSearch: true,
    dataIndex: 'remark',
  },
  {
    title: '操作',
    fixed: 'right',
    width: 100,
    hideInSearch: true,
    dataIndex: 'option',
  },
];
export default columnsData;
