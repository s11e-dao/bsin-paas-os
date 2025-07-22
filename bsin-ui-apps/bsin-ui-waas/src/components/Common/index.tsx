import React from 'react';
import { Button, Space, Tag, Tooltip, Popconfirm, message } from 'antd';
import { 
  EditOutlined, 
  DeleteOutlined, 
  EyeOutlined, 
  DownloadOutlined,
  ReloadOutlined,
  PlusOutlined
} from '@ant-design/icons';
import type { ButtonProps } from 'antd';

/**
 * 操作按钮类型
 */
export interface ActionButtonProps {
  key: string;
  label: string;
  icon?: React.ReactNode;
  type?: ButtonProps['type'];
  size?: ButtonProps['size'];
  danger?: boolean;
  disabled?: boolean;
  confirm?: {
    title: string;
    description?: string;
  };
  onClick: (record: any) => void;
}

/**
 * 操作按钮组组件
 */
interface ActionButtonsProps {
  actions: ActionButtonProps[];
  record: any;
  maxCount?: number;
}

export const ActionButtons: React.FC<ActionButtonsProps> = ({
  actions,
  record,
  maxCount = 3
}) => {
  const visibleActions = actions.slice(0, maxCount);
  const hiddenActions = actions.slice(maxCount);

  const renderButton = (action: ActionButtonProps) => {
    const button = (
      <Button
        key={action.key}
        type={action.type || 'link'}
        size={action.size || 'small'}
        danger={action.danger}
        disabled={action.disabled}
        icon={action.icon}
        onClick={() => action.onClick(record)}
      >
        {action.label}
      </Button>
    );

    if (action.confirm) {
      return (
        <Popconfirm
          key={action.key}
          title={action.confirm.title}
          description={action.confirm.description}
          onConfirm={() => action.onClick(record)}
          okText="确定"
          cancelText="取消"
        >
          {button}
        </Popconfirm>
      );
    }

    return button;
  };

  return (
    <Space size="small">
      {visibleActions.map(renderButton)}
      {hiddenActions.length > 0 && (
        <Tooltip
          title={
            <div>
              {hiddenActions.map(action => (
                <div key={action.key}>
                  <Button
                    type="link"
                    size="small"
                    icon={action.icon}
                    onClick={() => action.onClick(record)}
                  >
                    {action.label}
                  </Button>
                </div>
              ))}
            </div>
          }
        >
          <Button type="link" size="small">
            更多
          </Button>
        </Tooltip>
      )}
    </Space>
  );
};

/**
 * 状态标签组件
 */
interface StatusTagProps {
  status: string | number;
  statusMap: Record<string | number, { text: string; color: string }>;
  defaultColor?: string;
}

export const StatusTag: React.FC<StatusTagProps> = ({
  status,
  statusMap,
  defaultColor = 'default'
}) => {
  const config = statusMap[status] || { text: String(status), color: defaultColor };
  
  return <Tag color={config.color}>{config.text}</Tag>;
};

/**
 * 金额显示组件
 */
interface AmountDisplayProps {
  amount: number | string;
  currency?: string;
  decimals?: number;
  showZero?: boolean;
}

export const AmountDisplay: React.FC<AmountDisplayProps> = ({
  amount,
  currency = '¥',
  decimals = 2,
  showZero = true
}) => {
  const num = typeof amount === 'string' ? parseFloat(amount) : amount;
  
  if (isNaN(num)) {
    return <span>--</span>;
  }
  
  if (num === 0 && !showZero) {
    return <span>--</span>;
  }
  
  return <span>{currency}{num.toFixed(decimals)}</span>;
};

/**
 * 表格工具栏组件
 */
interface TableToolbarProps {
  title?: React.ReactNode;
  actions?: React.ReactNode[];
  extra?: React.ReactNode;
}

export const TableToolbar: React.FC<TableToolbarProps> = ({
  title,
  actions = [],
  extra
}) => {
  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'space-between', 
      alignItems: 'center',
      marginBottom: 16 
    }}>
      <div>{title}</div>
      <Space>
        {actions}
        {extra}
      </Space>
    </div>
  );
};

/**
 * 常用操作按钮
 */
export const CommonActions = {
  View: (onClick: (record: any) => void): ActionButtonProps => ({
    key: 'view',
    label: '查看',
    icon: <EyeOutlined />,
    type: 'link',
    onClick
  }),
  
  Edit: (onClick: (record: any) => void): ActionButtonProps => ({
    key: 'edit',
    label: '编辑',
    icon: <EditOutlined />,
    type: 'link',
    onClick
  }),
  
  Delete: (onClick: (record: any) => void): ActionButtonProps => ({
    key: 'delete',
    label: '删除',
    icon: <DeleteOutlined />,
    type: 'link',
    danger: true,
    confirm: {
      title: '确定要删除这条记录吗？',
      description: '删除后无法恢复，请谨慎操作。'
    },
    onClick
  }),
  
  Download: (onClick: (record: any) => void): ActionButtonProps => ({
    key: 'download',
    label: '下载',
    icon: <DownloadOutlined />,
    type: 'link',
    onClick
  }),
  
  Refresh: (onClick: () => void): ActionButtonProps => ({
    key: 'refresh',
    label: '刷新',
    icon: <ReloadOutlined />,
    type: 'link',
    onClick
  }),
  
  Add: (onClick: () => void): ActionButtonProps => ({
    key: 'add',
    label: '新增',
    icon: <PlusOutlined />,
    type: 'primary',
    onClick
  })
};

/**
 * 常用状态配置
 */
export const CommonStatusConfig = {
  // 通用状态
  common: {
    '1': { text: '启用', color: 'success' },
    '0': { text: '停用', color: 'error' },
    'true': { text: '是', color: 'success' },
    'false': { text: '否', color: 'error' },
  },
  
  // 处理状态
  process: {
    'PENDING': { text: '待处理', color: 'processing' },
    'PROCESSING': { text: '处理中', color: 'processing' },
    'SUCCESS': { text: '成功', color: 'success' },
    'FAILED': { text: '失败', color: 'error' },
    'CANCELLED': { text: '已取消', color: 'default' },
  },
  
  // 审核状态
  audit: {
    'PENDING': { text: '待审核', color: 'processing' },
    'APPROVED': { text: '已通过', color: 'success' },
    'REJECTED': { text: '已拒绝', color: 'error' },
  },
  
  // 支付状态
  payment: {
    'PENDING': { text: '待支付', color: 'processing' },
    'PAID': { text: '已支付', color: 'success' },
    'REFUNDED': { text: '已退款', color: 'warning' },
    'FAILED': { text: '支付失败', color: 'error' },
  }
}; 