import { request } from '@umijs/max';
import { message } from 'antd';
import type { PageParams, BaseResponse } from '@/types/common';

/**
 * 分页响应类型
 */
export interface PageResponse<T = any> {
  data: T[];
  total: number;
  success: boolean;
  records?: T[];
  pagination?: {
    totalSize: number;
  };
}

/**
 * 通用请求配置
 */
interface RequestOptions {
  showError?: boolean;
  showSuccess?: boolean;
  successMessage?: string;
  errorMessage?: string;
}

/**
 * 通用API请求函数
 * @param url 请求地址
 * @param options 请求选项
 * @param requestOptions 请求配置
 * @returns Promise<T>
 */
export async function apiRequest<T = any>(
  url: string,
  options: {
    serviceName: string;
    methodName: string;
    version: string;
    bizParams: any;
  },
  requestOptions: RequestOptions = {}
): Promise<T> {
  const {
    showError = true,
    showSuccess = false,
    successMessage = '操作成功',
    errorMessage = '操作失败'
  } = requestOptions;

  try {
    const response = await request(url, options);
    
    if (response.success) {
      if (showSuccess) {
        message.success(successMessage);
      }
      return response.data;
    } else {
      if (showError) {
        message.error(response.message || errorMessage);
      }
      throw new Error(response.message || errorMessage);
    }
  } catch (error) {
    if (showError) {
      message.error(errorMessage);
    }
    throw error;
  }
}

/**
 * 分页查询通用函数
 * @param url 请求地址
 * @param serviceName 服务名
 * @param methodName 方法名
 * @param params 查询参数
 * @param requestOptions 请求配置
 * @returns Promise<PageResponse<T>>
 */
export async function pageRequest<T = any>(
  url: string,
  serviceName: string,
  methodName: string,
  params: PageParams,
  requestOptions: RequestOptions = {}
): Promise<PageResponse<T>> {
  const response = await apiRequest<BaseResponse<PageResponse<T>>>(
    url,
    {
      serviceName,
      methodName,
      version: '1.0',
      bizParams: {
        ...params,
        pagination: {
          pageNum: params.current,
          pageSize: params.pageSize,
        },
      },
    },
    { ...requestOptions, showError: false }
  );

  return {
    data: response.data?.records || response.data || [],
    total: response.data?.total || response.pagination?.totalSize || 0,
    success: true,
  };
}

/**
 * 详情查询通用函数
 * @param url 请求地址
 * @param serviceName 服务名
 * @param methodName 方法名
 * @param params 查询参数
 * @param requestOptions 请求配置
 * @returns Promise<T>
 */
export async function detailRequest<T = any>(
  url: string,
  serviceName: string,
  methodName: string,
  params: { serialNo: string; [key: string]: any },
  requestOptions: RequestOptions = {}
): Promise<T> {
  return apiRequest<T>(
    url,
    {
      serviceName,
      methodName,
      version: '1.0',
      bizParams: params,
    },
    requestOptions
  );
}

/**
 * 新增通用函数
 * @param url 请求地址
 * @param serviceName 服务名
 * @param methodName 方法名
 * @param params 新增参数
 * @param requestOptions 请求配置
 * @returns Promise<T>
 */
export async function addRequest<T = any>(
  url: string,
  serviceName: string,
  methodName: string,
  params: any,
  requestOptions: RequestOptions = {}
): Promise<T> {
  return apiRequest<T>(
    url,
    {
      serviceName,
      methodName,
      version: '1.0',
      bizParams: params,
    },
    { ...requestOptions, showSuccess: true, successMessage: '添加成功' }
  );
}

/**
 * 更新通用函数
 * @param url 请求地址
 * @param serviceName 服务名
 * @param methodName 方法名
 * @param params 更新参数
 * @param requestOptions 请求配置
 * @returns Promise<T>
 */
export async function updateRequest<T = any>(
  url: string,
  serviceName: string,
  methodName: string,
  params: any,
  requestOptions: RequestOptions = {}
): Promise<T> {
  return apiRequest<T>(
    url,
    {
      serviceName,
      methodName,
      version: '1.0',
      bizParams: params,
    },
    { ...requestOptions, showSuccess: true, successMessage: '更新成功' }
  );
}

/**
 * 删除通用函数
 * @param url 请求地址
 * @param serviceName 服务名
 * @param methodName 方法名
 * @param params 删除参数
 * @param requestOptions 请求配置
 * @returns Promise<T>
 */
export async function deleteRequest<T = any>(
  url: string,
  serviceName: string,
  methodName: string,
  params: { serialNo: string; [key: string]: any },
  requestOptions: RequestOptions = {}
): Promise<T> {
  return apiRequest<T>(
    url,
    {
      serviceName,
      methodName,
      version: '1.0',
      bizParams: params,
    },
    { ...requestOptions, showSuccess: true, successMessage: '删除成功' }
  );
}

/**
 * 列表查询通用函数
 * @param url 请求地址
 * @param serviceName 服务名
 * @param methodName 方法名
 * @param params 查询参数
 * @param requestOptions 请求配置
 * @returns Promise<T[]>
 */
export async function listRequest<T = any>(
  url: string,
  serviceName: string,
  methodName: string,
  params: any = {},
  requestOptions: RequestOptions = {}
): Promise<T[]> {
  const response = await apiRequest<BaseResponse<T[]>>(
    url,
    {
      serviceName,
      methodName,
      version: '1.0',
      bizParams: params,
    },
    { ...requestOptions, showError: false }
  );

  return response.data || [];
}

/**
 * 文件下载通用函数
 * @param url 请求地址
 * @param serviceName 服务名
 * @param methodName 方法名
 * @param params 下载参数
 * @param filename 文件名
 * @param requestOptions 请求配置
 */
export async function downloadRequest(
  url: string,
  serviceName: string,
  methodName: string,
  params: any,
  filename?: string,
  requestOptions: RequestOptions = {}
): Promise<void> {
  try {
    const response = await request(url, {
      serviceName,
      methodName,
      version: '1.0',
      bizParams: params,
      responseType: 'blob',
    });

    if (response.type === 'application/json') {
      // 如果返回的是JSON，说明有错误
      const reader = new FileReader();
      reader.onload = () => {
        const errorData = JSON.parse(reader.result as string);
        message.error(errorData.message || '下载失败');
      };
      reader.readAsText(response);
      return;
    }

    // 创建下载链接
    const blob = new Blob([response]);
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = filename || 'download';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(downloadUrl);

    if (requestOptions.showSuccess !== false) {
      message.success('下载成功');
    }
  } catch (error) {
    if (requestOptions.showError !== false) {
      message.error('下载失败');
    }
    throw error;
  }
} 