import { Button, Tag, Space } from 'antd';
import { ProColumns } from '@ant-design/pro-components';
import { 
  ApiOutlined, 
  SearchOutlined, 
  RollbackOutlined, 
  UnlockOutlined, 
  UserAddOutlined, 
  UserDeleteOutlined, 
  FileTextOutlined, 
  DownloadOutlined,
  DollarOutlined,
  HistoryOutlined
} from '@ant-design/icons';
import type { STATUS_CONFIG } from '@/types/common';

/**
 * 平台应用数据类型
 */
export interface BizRoleAppType {
  appName: string;
  appId: string;
  appDescription: string;
  status: string;
  onConfigClick: (record: BizRoleAppType) => void;
}

/**
 * 操作历史数据类型
 */
export interface OperationHistoryType {
  operationType: string;
  requestParams: string;
  responseResult: string;
  operationTime: string;
  status: string;
}

/**
 * 平台应用列表列配置
 */
export const bizRoleAppColumns: ProColumns<BizRoleAppType>[] = [
  {
    title: '应用名称',
    dataIndex: 'appName',
    key: 'appName',
    width: 200,
    ellipsis: true,
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    key: 'appId',
    width: 150,
    ellipsis: true,
  },
  {
    title: '应用描述',
    dataIndex: 'appDescription',
    key: 'appDescription',
    ellipsis: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    render: (_, record) => (
      <Tag color={record.status === '1' ? 'success' : 'error'}>
        {record.status === '1' ? '启用' : '停用'}
      </Tag>
    ),
  },
  {
    title: '操作',
    key: 'action',
    width: 120,
    render: (_, record) => (
      <Button 
        type="primary" 
        size="small"
        onClick={() => record.onConfigClick(record)}
      >
        配置分账
      </Button>
    ),
  },
];

/**
 * 微信分账操作配置
 */
export const wxProfitShareActions = [
  {
    key: 'request',
    label: '请求分账',
    description: '向微信支付发起分账请求',
    icon: <DollarOutlined />,
  },
  {
    key: 'query',
    label: '查询分账结果',
    description: '查询分账请求的处理结果',
    icon: <SearchOutlined />,
  },
  {
    key: 'return',
    label: '请求分账回退',
    description: '请求分账回退，将已分账资金退回',
    icon: <RollbackOutlined />,
  },
  {
    key: 'returnQuery',
    label: '查询分账回退结果',
    description: '查询分账回退请求的处理结果',
    icon: <HistoryOutlined />,
  },
  {
    key: 'unfreeze',
    label: '解冻剩余资金',
    description: '解冻订单中剩余未分账资金',
    icon: <UnlockOutlined />,
  },
  {
    key: 'remaining',
    label: '查询剩余待分金额',
    description: '查询订单中剩余可分账金额',
    icon: <SearchOutlined />,
  },
  {
    key: 'addReceiver',
    label: '添加分账接收方',
    description: '添加分账接收方到微信支付',
    icon: <UserAddOutlined />,
  },
  {
    key: 'deleteReceiver',
    label: '删除分账接收方',
    description: '从微信支付删除分账接收方',
    icon: <UserDeleteOutlined />,
  },
  {
    key: 'applyBill',
    label: '申请分账账单',
    description: '申请分账账单文件',
    icon: <FileTextOutlined />,
  },
  {
    key: 'downloadBill',
    label: '下载分账账单',
    description: '下载分账账单文件',
    icon: <DownloadOutlined />,
  },
];

/**
 * 接收方表单字段配置
 */
export const receiverFormFields = [
  {
    name: 'receiverId',
    label: '接收方ID',
    type: 'input' as const,
    required: true,
    placeholder: '请输入接收方ID',
    rules: [{ required: true, message: '请输入接收方ID' }],
  },
  {
    name: 'receiverName',
    label: '接收方名称',
    type: 'input' as const,
    required: true,
    placeholder: '请输入接收方名称',
    rules: [{ required: true, message: '请输入接收方名称' }],
  },
  {
    name: 'receiverType',
    label: '接收方类型',
    type: 'select' as const,
    required: true,
    options: [
      { label: '个人', value: 'PERSONAL' },
      { label: '企业', value: 'ENTERPRISE' },
    ],
    rules: [{ required: true, message: '请选择接收方类型' }],
  },
  {
    name: 'relationType',
    label: '关系类型',
    type: 'select' as const,
    required: true,
    options: [
      { label: '服务商', value: 'SERVICE_PROVIDER' },
      { label: '门店', value: 'STORE' },
      { label: '员工', value: 'STAFF' },
      { label: '店主', value: 'STORE_OWNER' },
      { label: '合作伙伴', value: 'PARTNER' },
      { label: '总部', value: 'HEADQUARTER' },
      { label: '品牌方', value: 'BRAND' },
      { label: '分销商', value: 'DISTRIBUTOR' },
      { label: '用户', value: 'USER' },
      { label: '供应商', value: 'SUPPLIER' },
      { label: '自定义', value: 'CUSTOM' },
    ],
    rules: [{ required: true, message: '请选择关系类型' }],
  },
  {
    name: 'customRelation',
    label: '自定义关系',
    type: 'input' as const,
    required: false,
    placeholder: '当关系类型为自定义时必填',
    rules: [
      {
        validator: (_: any, value: any) => {
          const relationType = _.field?.relationType;
          if (relationType === 'CUSTOM' && !value) {
            return Promise.reject(new Error('自定义关系类型时必填'));
          }
          return Promise.resolve();
        },
      },
    ],
  },
];

/**
 * 分账请求表单字段配置
 */
export const profitShareFormFields = [
  {
    name: 'transactionNo',
    label: '交易编号',
    type: 'input' as const,
    required: true,
    placeholder: '请输入交易编号',
    rules: [{ required: true, message: '请输入交易编号' }],
  },
];

/**
 * 分账回退表单字段配置
 */
export const returnFormFields = [
  {
    name: 'orderId',
    label: '订单号',
    type: 'input' as const,
    required: true,
    placeholder: '请输入订单号',
    rules: [{ required: true, message: '请输入订单号' }],
  },
  {
    name: 'outReturnNo',
    label: '回退单号',
    type: 'input' as const,
    required: true,
    placeholder: '请输入回退单号',
    rules: [{ required: true, message: '请输入回退单号' }],
  },
  {
    name: 'returnMchid',
    label: '回退商户号',
    type: 'input' as const,
    required: true,
    placeholder: '请输入回退商户号',
    rules: [{ required: true, message: '请输入回退商户号' }],
  },
  {
    name: 'amount',
    label: '回退金额',
    type: 'number' as const,
    required: true,
    placeholder: '请输入回退金额',
    rules: [{ required: true, message: '请输入回退金额' }],
  },
  {
    name: 'description',
    label: '回退原因',
    type: 'textarea' as const,
    required: true,
    placeholder: '请输入回退原因',
    rules: [{ required: true, message: '请输入回退原因' }],
  },
];

/**
 * 分账回退查询表单字段配置
 */
export const returnQueryFormFields = [
  {
    name: 'orderId',
    label: '订单号',
    type: 'input' as const,
    required: true,
    placeholder: '请输入订单号',
    rules: [{ required: true, message: '请输入订单号' }],
  },
  {
    name: 'outReturnNo',
    label: '回退单号',
    type: 'input' as const,
    required: true,
    placeholder: '请输入回退单号',
    rules: [{ required: true, message: '请输入回退单号' }],
  },
];

/**
 * 解冻资金表单字段配置
 */
export const unfreezeFormFields = [
  {
    name: 'transactionId',
    label: '交易ID',
    type: 'input' as const,
    required: true,
    placeholder: '请输入交易ID',
    rules: [{ required: true, message: '请输入交易ID' }],
  },
  {
    name: 'outOrderNo',
    label: '商户分账单号',
    type: 'input' as const,
    required: true,
    placeholder: '请输入商户分账单号',
    rules: [{ required: true, message: '请输入商户分账单号' }],
  },
  {
    name: 'description',
    label: '解冻原因',
    type: 'textarea' as const,
    required: false,
    placeholder: '请输入解冻原因',
  },
];

/**
 * 查询剩余金额表单字段配置
 */
export const remainingFormFields = [
  {
    name: 'transactionId',
    label: '交易ID',
    type: 'input' as const,
    required: true,
    placeholder: '请输入交易ID',
    rules: [{ required: true, message: '请输入交易ID' }],
  },
];

/**
 * 删除接收方表单字段配置
 */
export const deleteReceiverFormFields = [
  {
    name: 'receiverId',
    label: '接收方ID',
    type: 'input' as const,
    required: true,
    placeholder: '请输入接收方ID',
    rules: [{ required: true, message: '请输入接收方ID' }],
  },
];

/**
 * 账单表单字段配置
 */
export const billFormFields = [
  {
    name: 'billDate',
    label: '账单日期',
    type: 'datePicker' as const,
    required: true,
    placeholder: '请选择账单日期',
    rules: [{ required: true, message: '请选择账单日期' }],
  },
  {
    name: 'tarType',
    label: '压缩类型',
    type: 'select' as const,
    required: true,
    options: [
      { label: 'GZIP', value: 'GZIP' },
      { label: 'LZMA', value: 'LZMA' },
    ],
    rules: [{ required: true, message: '请选择压缩类型' }],
  },
];

/**
 * 状态标签配置
 */
export const statusTagConfig = {
  SUCCESS: { color: 'success', text: '成功' },
  FAILED: { color: 'error', text: '失败' },
  PENDING: { color: 'processing', text: '处理中' },
  UNKNOWN: { color: 'default', text: '未知' },
};

/**
 * 操作历史表格列配置
 */
export const operationResultColumns: ProColumns<OperationHistoryType>[] = [
  {
    title: '操作类型',
    dataIndex: 'operationType',
    key: 'operationType',
    width: 150,
    render: (_, record) => {
      const action = wxProfitShareActions.find(a => a.key === record.operationType);
      return action ? action.label : record.operationType;
    },
  },
  {
    title: '请求参数',
    dataIndex: 'requestParams',
    key: 'requestParams',
    ellipsis: true,
    width: 200,
    render: (_, record) => (
      <div style={{ maxWidth: 200, wordBreak: 'break-all' }}>
        {record.requestParams}
      </div>
    ),
  },
  {
    title: '响应结果',
    dataIndex: 'responseResult',
    key: 'responseResult',
    ellipsis: true,
    width: 200,
    render: (_, record) => (
      <div style={{ maxWidth: 200, wordBreak: 'break-all' }}>
        {record.responseResult}
      </div>
    ),
  },
  {
    title: '操作时间',
    dataIndex: 'operationTime',
    key: 'operationTime',
    width: 180,
    valueType: 'dateTime',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    render: (_, record) => {
      const config = statusTagConfig[record.status as keyof typeof statusTagConfig] || statusTagConfig.UNKNOWN;
      return <Tag color={config.color}>{config.text}</Tag>;
    },
  },
];