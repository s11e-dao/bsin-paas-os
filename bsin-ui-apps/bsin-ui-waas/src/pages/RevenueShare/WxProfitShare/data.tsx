import { ProColumns } from '@ant-design/pro-components';
import { Button, Space, Tag, Tooltip } from 'antd';
import { QuestionCircleOutlined } from '@ant-design/icons';

// 平台应用列表列定义
export const bizRoleAppColumns: ProColumns<any>[] = [
  {
    title: '应用名称',
    dataIndex: 'appName',
    width: 200,
    ellipsis: true,
  },
  {
    title: '应用描述',
    dataIndex: 'appDescription',
    width: 300,
    ellipsis: true,
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    width: 150,
    ellipsis: true,
  },
  {
    title: '应用通道',
    dataIndex: 'appChannel',
    width: 120,
    valueEnum: {
      '1': { text: '支付宝', status: 'Processing' },
      '2': { text: '微信支付', status: 'Success' },
      '3': { text: '银联', status: 'Default' },
      '4': { text: '微信小程序', status: 'Warning' },
    },
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    valueEnum: {
      '0': { text: '停用', status: 'Error' },
      '1': { text: '启用', status: 'Success' },
    },
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    valueType: 'dateTime',
  },
  {
    title: '操作',
    valueType: 'option',
    width: 200,
    render: (_, record: any) => [
      <Button
        key="config"
        type="link"
        size="small"
        onClick={() => record.onConfigClick?.(record)}
      >
        配置分账
      </Button>,
    ],
  },
];

// 微信分账API操作配置
export const wxProfitShareActions = [
  {
    key: 'request',
    label: '请求分账',
    description: '向微信支付平台发起分账请求',
    icon: '📤',
    color: 'blue',
  },
  {
    key: 'query',
    label: '查询分账结果',
    description: '查询分账请求的处理结果',
    icon: '🔍',
    color: 'green',
  },
  {
    key: 'return',
    label: '请求分账回退',
    description: '请求分账回退，将已分账资金退回',
    icon: '↩️',
    color: 'orange',
  },
  {
    key: 'returnQuery',
    label: '查询分账回退结果',
    description: '查询分账回退请求的处理结果',
    icon: '🔍',
    color: 'purple',
  },
  {
    key: 'unfreeze',
    label: '解冻剩余资金',
    description: '解冻订单中剩余未分账资金',
    icon: '💧',
    color: 'cyan',
  },
  {
    key: 'remaining',
    label: '查询剩余待分金额',
    description: '查询订单中剩余可分账金额',
    icon: '💰',
    color: 'gold',
  },
  {
    key: 'addReceiver',
    label: '添加分账接收方',
    description: '添加分账接收方信息',
    icon: '➕',
    color: 'lime',
  },
  {
    key: 'deleteReceiver',
    label: '删除分账接收方',
    description: '删除已添加的分账接收方',
    icon: '➖',
    color: 'red',
  },
  {
    key: 'applyBill',
    label: '申请分账账单',
    description: '申请分账账单文件',
    icon: '📋',
    color: 'geekblue',
  },
  {
    key: 'downloadBill',
    label: '下载账单',
    description: '下载分账账单文件',
    icon: '📥',
    color: 'volcano',
  },
];

// 分账接收方表单字段
export const receiverFormFields = [
  {
    name: 'receiverType',
    label: '接收方类型',
    type: 'select',
    required: true,
    options: [
      { label: '商户号', value: 'MERCHANT_ID' },
      { label: '个人openid', value: 'PERSONAL_OPENID' },
    ],
  },
  {
    name: 'receiverAccount',
    label: '接收方账号',
    type: 'input',
    required: true,
    placeholder: '请输入接收方账号',
  },
  {
    name: 'receiverName',
    label: '接收方姓名',
    type: 'input',
    required: true,
    placeholder: '请输入接收方姓名',
  },
  {
    name: 'relationType',
    label: '分账关系类型',
    type: 'select',
    required: true,
    options: [
      { label: '服务商', value: 'SERVICE_PROVIDER' },
      { label: '商户', value: 'MERCHANT' },
      { label: '分销商', value: 'DISTRIBUTOR' },
      { label: '供应商', value: 'SUPPLIER' },
      { label: '平台', value: 'PLATFORM' },
      { label: '其他', value: 'OTHERS' },
    ],
  },
  {
    name: 'customRelation',
    label: '自定义分账关系',
    type: 'input',
    placeholder: '当关系类型选择"其他"时必填',
  },
];

// 分账请求表单字段
export const profitShareFormFields = [
  {
    name: 'transactionId',
    label: '微信订单号',
    type: 'input',
    required: true,
    placeholder: '请输入微信支付订单号',
    tooltip: '微信支付订单号，用于标识需要分账的订单',
  },
  {
    name: 'outOrderNo',
    label: '商户分账单号',
    type: 'input',
    required: true,
    placeholder: '请输入商户分账单号',
    tooltip: '商户系统内部的分账单号，要求同一个商户号下唯一',
  },
  {
    name: 'receivers',
    label: '分账接收方列表',
    type: 'array',
    required: true,
    itemType: 'object',
    itemFields: [
      {
        name: 'type',
        label: '接收方类型',
        type: 'select',
        required: true,
        options: [
          { label: '商户号', value: 'MERCHANT_ID' },
          { label: '个人openid', value: 'PERSONAL_OPENID' },
        ],
      },
      {
        name: 'account',
        label: '接收方账号',
        type: 'input',
        required: true,
      },
      {
        name: 'name',
        label: '接收方姓名',
        type: 'input',
        required: true,
      },
      {
        name: 'amount',
        label: '分账金额',
        type: 'number',
        required: true,
        min: 1,
        addonAfter: '分',
      },
      {
        name: 'description',
        label: '分账描述',
        type: 'input',
        required: true,
      },
    ],
  },
];

// 分账回退表单字段
export const returnFormFields = [
  {
    name: 'returnMchid',
    label: '回退商户号',
    type: 'input',
    required: true,
    placeholder: '请输入回退商户号',
  },
  {
    name: 'orderId',
    label: '微信分账单号',
    type: 'input',
    required: true,
    placeholder: '请输入微信分账单号',
  },
  {
    name: 'outReturnNo',
    label: '商户回退单号',
    type: 'input',
    required: true,
    placeholder: '请输入商户回退单号',
  },
  {
    name: 'amount',
    label: '回退金额',
    type: 'number',
    required: true,
    min: 1,
    addonAfter: '分',
  },
  {
    name: 'description',
    label: '回退原因',
    type: 'textarea',
    required: true,
    placeholder: '请输入回退原因',
  },
];

// 账单申请表单字段
export const billFormFields = [
  {
    name: 'billDate',
    label: '账单日期',
    type: 'input',
    required: true,
    placeholder: '请选择账单日期',
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
  },
];

// 状态标签配置
export const statusTagConfig = {
  PROCESSING: { color: 'processing', text: '处理中' },
  SUCCESS: { color: 'success', text: '成功' },
  FAILED: { color: 'error', text: '失败' },
  PENDING: { color: 'warning', text: '待处理' },
};

// 操作结果列定义
export const operationResultColumns: ProColumns<any>[] = [
  {
    title: '操作类型',
    dataIndex: 'operationType',
    width: 150,
    render: (text) => {
      const action = wxProfitShareActions.find(item => item.key === text);
      return action ? (
        <Space>
          <span>{action.icon}</span>
          <span>{action.label}</span>
        </Space>
      ) : text;
    },
  },
  {
    title: '请求参数',
    dataIndex: 'requestParams',
    width: 300,
    ellipsis: true,
    render: (text) => (
      <Tooltip title={text}>
        <span>{text}</span>
      </Tooltip>
    ),
  },
  {
    title: '响应结果',
    dataIndex: 'responseResult',
    width: 300,
    ellipsis: true,
    render: (text) => (
      <Tooltip title={text}>
        <span>{text}</span>
      </Tooltip>
    ),
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
    render: (text) => {
      const config = statusTagConfig[text as keyof typeof statusTagConfig];
      return config ? (
        <Tag color={config.color}>{config.text}</Tag>
      ) : (
        <Tag>{text}</Tag>
      );
    },
  },
  {
    title: '操作时间',
    dataIndex: 'operationTime',
    width: 180,
    valueType: 'dateTime',
  },
];