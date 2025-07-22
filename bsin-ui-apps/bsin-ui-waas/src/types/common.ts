/**
 * 通用类型定义
 */

/**
 * 分页参数类型
 */
export interface PageParams {
  current?: number;
  pageSize?: number;
  [key: string]: any;
}

/**
 * 分页响应类型
 */
export interface PageResponse<T = any> {
  data: T[];
  total: number;
  success: boolean;
}

/**
 * 基础响应类型
 */
export interface BaseResponse<T = any> {
  code: number;
  message: string;
  data: T;
  success: boolean;
}

/**
 * 详情查询参数类型
 */
export interface DetailParams {
  serialNo: string;
  [key: string]: any;
}

/**
 * 删除参数类型
 */
export interface DeleteParams {
  serialNo: string;
  [key: string]: any;
}

/**
 * 状态枚举
 */
export enum StatusEnum {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
}

/**
 * 状态配置
 */
export const STATUS_CONFIG = {
  [StatusEnum.PENDING]: {
    text: '待处理',
    status: 'Processing',
  },
  [StatusEnum.SUCCESS]: {
    text: '成功',
    status: 'Success',
  },
  [StatusEnum.FAILED]: {
    text: '失败',
    status: 'Error',
  },
} as const;

/**
 * 表格操作类型
 */
export interface TableAction {
  key: string;
  label: string;
  onClick: (record: any) => void;
}

/**
 * 表单字段类型
 */
export interface FormField {
  name: string;
  label: string;
  type: 'input' | 'select' | 'textarea' | 'number' | 'datePicker' | 'array';
  required?: boolean;
  placeholder?: string;
  options?: Array<{ label: string; value: any }>;
  rules?: any[];
  [key: string]: any;
}

/**
 * 模态框配置类型
 */
export interface ModalConfig {
  title: string;
  visible: boolean;
  width?: number;
  onCancel: () => void;
  onOk?: () => void;
}

/**
 * 请求配置类型
 */
export interface RequestConfig {
  serviceName: string;
  methodName: string;
  version: string;
  bizParams: any;
} 