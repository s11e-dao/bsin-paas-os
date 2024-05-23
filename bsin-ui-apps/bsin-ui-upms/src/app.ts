import type { RequestConfig } from 'umi';
import { notification } from 'antd';
import { getSessionStorageInfo, getLocalStorageInfo } from '@/utils/localStorageInfo';

// src/app.ts
export const qiankun = {
    // 应用加载之前
    async bootstrap(props) {
      console.log('upms bootstrap', props);
    },
    // 应用 render 之前触发
    async mount(props) {
      console.log('upms mount', props);
      props.onGlobalStateChange((state, prev) => {
        // state: 变更后的状态; prev 变更前的状态
        console.log(state, prev);
      });
      
    },
    // 应用卸载之后触发
    async unmount(props) {
      console.log('upms unmount', props);
    },
  };

  
// 错误处理方案： 错误类型
enum ErrorShowType {
  SILENT = 0,
  WARN_MESSAGE = 1,
  ERROR_MESSAGE = 2,
  NOTIFICATION = 3,
  REDIRECT = 9,
}
// 与后端约定的响应数据格式
interface ResponseStructure {
  success: boolean;
  data: any;
  errorCode?: number;
  errorMessage?: string;
  showType?: ErrorShowType;
}

// 接口环境地址
let gatewayUrl = process.env.baseUrl;
let webScoketUrl = process.env.webScoketUrl;

export const request: RequestConfig = {
  // 统一的请求设定
  timeout: 100000,
  headers: { 'X-Requested-With': 'XMLHttpRequest' },

  // 错误处理： umi@3 的错误处理方案。
  errorConfig: {
    // 错误抛出
    errorThrower: (res: ResponseStructure) => {
      const { success, data, errorCode, errorMessage, showType } = res;
      if (!success) {
        const error: any = new Error(errorMessage);
        error.name = 'BizError';
        error.info = { errorCode, errorMessage, showType, data };
        throw error; // 抛出自制的错误
      }
    },
    // 错误接收及处理
    errorHandler: (error: any, opts: any) => {
      if (opts?.skipErrorHandler) throw error;
      // 我们的 errorThrower 抛出的错误。
      if (error.name === 'BizError') {
        const errorInfo: ResponseStructure | undefined = error.info;
        if (errorInfo) {
          const { errorMessage, errorCode } = errorInfo;
          switch (errorInfo.showType) {
            case ErrorShowType.SILENT:
              // do nothing
              break;
            case ErrorShowType.WARN_MESSAGE:
              notification.info(errorMessage);
              break;
            case ErrorShowType.ERROR_MESSAGE:
              notification.error(errorMessage);
              break;
            case ErrorShowType.NOTIFICATION:
              notification.open({
                description: errorMessage,
                message: errorCode,
              });
              break;
            case ErrorShowType.REDIRECT:
              // TODO: redirect
              break;
            default:
              notification.error(errorMessage);
          }
        }
      } else if (error.response) {
        // Axios 的错误
        // 请求成功发出且服务器也响应了状态码，但状态代码超出了 2xx 的范围
        notification.error(`Response status:${error.response.status}`);
      } else if (error.request) {
        // 请求已经成功发起，但没有收到响应
        // \`error.request\` 在浏览器中是 XMLHttpRequest 的实例，
        // 而在node.js中是 http.ClientRequest 的实例
        notification.error('None response! Please retry.');
      } else {
        // 发送请求时出了点问题
        notification.error('Request error, please retry.');
      }
    },

  },

  // 请求拦截器
  requestInterceptors: [

    (config:any) => {
      // 请求方法统一为 POST
      config.method = 'POST';
      // 获取userInfo和token
      let userInfo = getLocalStorageInfo('userInformation');
      let token = getSessionStorageInfo('token')?.token;

      // 判断token和用户信息是否存在
      if (
        (!token || !userInfo) &&
        config.methodName !== 'getIssueApps' &&
        config.methodName !== 'login'
      ) {
        // history.push('/login');
      }

      if (token == undefined) {
        token = ""
      }

      // 判断一些
      const headers = {
        'Content-Type': 'application/json',
        Accept: 'application/json',
        Authorization: token || "",
        version: "1.0.0",
      };

      let pagination;
      if (config?.bizParams?.pageSize) {
        pagination = {
          pageSize: config?.bizParams?.pageSize,
          pageNum: config?.bizParams?.current,
        };
        delete config?.bizParams?.pageSize;
        delete config?.bizParams?.current;
      }
      // 组装报文
      let data = {}
      if (config?.version) {
        headers.version = config?.version;
      }

      if (pagination) {
        data = {
          ...config.bizParams,
          pagination,
        };
      }else{
        data = {
          ...config.bizParams
        };
      }
      config.headers = {
        ...headers,
      };
      // 拦截请求配置，进行个性化处理。
      console.log("config")
      console.log(config)
      const url = gatewayUrl + config.url;
      return { ...config, data, url };
    }
  ],

  // 响应拦截器
  responseInterceptors: [
    (response : any) => {
      // 拦截响应数据，进行个性化处理
      const { data } = response;
      console.log(response)
      if (data?.code !== 0) {
        notification.error({
          message: data?.message,
          description: data?.message,
        });
        return false;
      }
      return response
    }
  ]

};
