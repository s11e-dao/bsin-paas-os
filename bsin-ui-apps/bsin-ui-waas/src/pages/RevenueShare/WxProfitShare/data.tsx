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

// 平台应用列表列配置
export const bizRoleAppColumns: ProColumns<any>[] = [
  {
    title: '应用名称',
    dataIndex: 'appName',
    key: 'appName',
    width: 200,
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    key: 'appId',
    width: 150,
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

// 微信分账操作配置
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
    description: '添加分账接收方信息',
    icon: <UserAddOutlined />,
  },
  {
    key: 'deleteReceiver',
    label: '删除分账接收方',
    description: '删除已添加的分账接收方',
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
    label: '下载账单',
    description: '下载分账账单文件',
    icon: <DownloadOutlined />,
  },
];

// 分账请求表单字段
export const profitShareFormFields = [
  {
    name: 'transactionNo',
    label: '交易单号',
    type: 'input',
    required: true,
    placeholder: '请输入交易单号',
    rules: [
      { required: true, message: '请输入交易单号' },
    ],
  },
];

// 分账回退表单字段
export const returnFormFields = [
  {
    name: 'orderId',
    label: '微信分账单号',
    type: 'input',
    required: true,
    placeholder: '请输入微信分账单号',
    rules: [
      { required: true, message: '请输入微信分账单号' },
    ],
  },
  {
    name: 'outReturnNo',
    label: '商户回退单号',
    type: 'input',
    required: true,
    placeholder: '请输入商户回退单号',
    rules: [
      { required: true, message: '请输入商户回退单号' },
    ],
  },
  {
    name: 'returnMchid',
    label: '回退商户号',
    type: 'input',
    required: true,
    placeholder: '请输入回退商户号',
    rules: [
      { required: true, message: '请输入回退商户号' },
    ],
  },
  {
    name: 'amount',
    label: '回退金额',
    type: 'number',
    required: true,
    placeholder: '请输入回退金额',
    min: 0.01,
    precision: 2,
    rules: [
      { required: true, message: '请输入回退金额' },
      { type: 'number', min: 0.01, message: '金额必须大于0' },
    ],
  },
  {
    name: 'description',
    label: '回退原因',
    type: 'textarea',
    required: true,
    placeholder: '请输入回退原因',
    rules: [
      { required: true, message: '请输入回退原因' },
    ],
  },
];

// 分账回退查询表单字段
export const returnQueryFormFields = [
  {
    name: 'orderId',
    label: '微信分账单号',
    type: 'input',
    required: true,
    placeholder: '请输入微信分账单号',
    rules: [
      { required: true, message: '请输入微信分账单号' },
    ],
  },
  {
    name: 'outReturnNo',
    label: '商户回退单号',
    type: 'input',
    required: true,
    placeholder: '请输入商户回退单号',
    rules: [
      { required: true, message: '请输入商户回退单号' },
    ],
  },
];

// 解冻剩余资金表单字段
export const unfreezeFormFields = [
  {
    name: 'transactionId',
    label: '微信支付订单号',
    type: 'input',
    required: true,
    placeholder: '请输入微信支付订单号',
    rules: [
      { required: true, message: '请输入微信支付订单号' },
    ],
  },
  {
    name: 'outOrderNo',
    label: '商户订单号',
    type: 'input',
    required: true,
    placeholder: '请输入商户订单号',
    rules: [
      { required: true, message: '请输入商户订单号' },
    ],
  },
  {
    name: 'description',
    label: '解冻原因',
    type: 'textarea',
    required: false,
    placeholder: '请输入解冻原因（可选）',
  },
];

// 查询剩余待分金额表单字段
export const remainingFormFields = [
  {
    name: 'transactionId',
    label: '微信支付订单号',
    type: 'input',
    required: true,
    placeholder: '请输入微信支付订单号',
    rules: [
      { required: true, message: '请输入微信支付订单号' },
    ],
  },
];

// 添加分账接收方表单字段
export const receiverFormFields = [
  {
    name: 'receiverId',
    label: '接收方账号',
    type: 'input',
    required: true,
    placeholder: '请输入接收方账号',
    rules: [
      { required: true, message: '请输入接收方账号' },
    ],
  },
  {
    name: 'receiverName',
    label: '接收方姓名',
    type: 'input',
    required: true,
    placeholder: '请输入接收方姓名',
    rules: [
      { required: true, message: '请输入接收方姓名' },
    ],
  },
  {
    name: 'receiverType',
    label: '接收方类型',
    type: 'select',
    required: true,
    options: [
      { label: '商户号', value: 'MERCHANT_ID' },
      { label: '个人openid', value: 'PERSONAL_OPENID' },
    ],
    rules: [
      { required: true, message: '请选择接收方类型' },
    ],
  },
  {
    name: 'relationType',
    label: '分账关系',
    type: 'select',
    required: true,
    options: [
      { label: '店主', value: 'STORE_OWNER' },
      { label: '员工', value: 'STAFF' },
      { label: '合作伙伴', value: 'PARTNER' },
      { label: '品牌方', value: 'BRAND' },
      { label: '分销商', value: 'DISTRIBUTOR' },
      { label: '用户', value: 'USER' },
      { label: '供应商', value: 'SUPPLIER' },
      { label: '自定义', value: 'CUSTOM' },
    ],
    rules: [
      { required: true, message: '请选择分账关系' },
    ],
  },
  {
    name: 'customRelation',
    label: '自定义分账关系',
    type: 'input',
    required: false,
    placeholder: '当选择自定义分账关系时填写',
  },
];

// 删除分账接收方表单字段
export const deleteReceiverFormFields = [
  {
    name: 'receiverId',
    label: '接收方账号',
    type: 'input',
    required: true,
    placeholder: '请输入要删除的接收方账号',
    rules: [
      { required: true, message: '请输入接收方账号' },
    ],
  },
];

// 账单申请表单字段
export const billFormFields = [
  {
    name: 'billDate',
    label: '账单日期',
    type: 'datePicker',
    required: true,
    placeholder: '请选择账单日期',
    rules: [
      { required: true, message: '请选择账单日期' },
    ],
  },
  {
    name: 'tarType',
    label: '压缩类型',
    type: 'select',
    required: true,
    options: [
      { label: 'GZIP', value: 'GZIP' },
      { label: 'LZMA', value: 'LZMA' },
    ],
    rules: [
      { required: true, message: '请选择压缩类型' },
    ],
  },
];

// 状态标签配置
export const statusTagConfig = {
  SUCCESS: { color: 'success', text: '成功' },
  FAILED: { color: 'error', text: '失败' },
  PROCESSING: { color: 'processing', text: '处理中' },
  PENDING: { color: 'warning', text: '待处理' },
};

// 操作历史记录列配置
export const operationResultColumns: ProColumns<any>[] = [
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
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    render: (_, record) => {
      const config = statusTagConfig[record.status as keyof typeof statusTagConfig];
      return config ? (
        <Tag color={config.color}>{config.text}</Tag>
      ) : (
        <Tag>{record.status}</Tag>
      );
    },
  },
  {
    title: '操作时间',
    dataIndex: 'operationTime',
    key: 'operationTime',
    width: 180,
    render: (_, record) => new Date(record.operationTime).toLocaleString(),
  },
];