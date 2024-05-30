import {
  addGlobalUncaughtErrorHandler,
  addErrorHandler,
  removeGlobalUncaughtErrorHandler,
} from "qiankun";
import type { RequestConfig } from "umi";
import { history } from "umi";
import { notification } from "antd";

import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from "@/utils/localStorageInfo";

import { getPublishApps } from "./services/getPublishApps";

/**
 * 添加全局的未捕获异常处理器
 * 1、捕获子应用加载状态
 */
addGlobalUncaughtErrorHandler((event: any) => {
  removeGlobalUncaughtErrorHandler(event);
  console.log(event);
  if (
    (event.reason && event.reason.message === "Failed to fetch") ||
    (event.reason && event.reason.message === "Network request failed")
  ) {
    console.log(
      window.localStorage.getItem("bsin-microAppMountStatus") !== "3"
    );
    // 子应用加载失败会触发两次，只有第一次要提示，其他略过
    if (window.localStorage.getItem("bsin-microAppMountStatus") !== "3") {
      window.localStorage.setItem("bsin-microAppMountStatus", "3");
      console.log("子应加载失败");
      // history.push("/404")
    }
  }
});
addErrorHandler((err) => {
  console.log(err);
  console.log("addErrorHandler err", err);
  // history.push("/404")
});

export const qiankun = () => {
  return getPublishApps({}).then(
    (res: any) => {
      if (res && res.code === 0) {
        let apps = res.data.map((item: any) => {
          return {
            name: item.appCode,
            entry: item.url,
            activeRule: `#/${item.appCode}`,
            // 传递子应用ID
            props: {
              appId: item.appId,
            },
          };
        });
        const routes = res.data.map((item: any) => {
          // appLanguage为1是react
          if (item.appLanguage === 1) {
            return {
              path: `/${item.appCode}/*`,
              microApp: item.appCode,
              // 加载动画处理
              microAppProps: {
                autoCaptureError: true,
                autoSetLoading: true,
                className: "loading",
                wrapperClassName: "loading-wrapper",
              },
            };
          } else {
            return {
              path: `/${item.appCode}`,
              microApp: item.appCode,
              microAppProps: {
                autoSetLoading: true,
                className: "loading",
                wrapperClassName: "loading-wrapper",
              },
            };
          }
        });
        return {
          // 注册子应用信息
          apps,
          routes,
          // 开启严格的样式隔离模式。这种模式下 qiankun 会为每个微应用的容器包裹上一个 [shadow dom]节点，从而确保微应用的样式不会对全局造成影响。
          // sandbox: {
          //   // 开启严格的样式隔离模式。这种模式下 qiankun 会为每个微应用的容器包裹上一个 [shadow dom]节点，从而确保微应用的样式不会对全局造成影响。
          //   strictStyleIsolation: false,
          //   // 设置实验性的样式隔离特性，即在子应用下面的样式都会包一个特殊的选择器规则来限定其影响范围
          //   experimentalStyleIsolation: true
          // },
          // 排除乾坤样式处理
          excludeAssetFilter: (assestUrl: any) => {
            return assestUrl.endsWith(".css");
          },
        };
      }
      return {
        // 注册子应用信息
        apps: [],
        routes: [],
      };
    },
    (err) => {
      return {
        // 注册子应用信息
        apps: [],
        routes: [],
      };
    }
  );
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
  headers: { "X-Requested-With": "XMLHttpRequest" },

  // 错误处理： umi@3 的错误处理方案。
  errorConfig: {
    // 错误抛出
    errorThrower: (res: ResponseStructure) => {
      const { success, data, errorCode, errorMessage, showType } = res;
      if (!success) {
        const error: any = new Error(errorMessage);
        error.name = "BizError";
        error.info = { errorCode, errorMessage, showType, data };
        throw error; // 抛出自制的错误
      }
    },
    // 错误接收及处理
    errorHandler: (error: any, opts: any) => {
      if (opts?.skipErrorHandler) throw error;
      // 我们的 errorThrower 抛出的错误。
      if (error.name === "BizError") {
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
        notification.error({
          message: `Response status:${error.response.status}`,
          description: `Response status:${error.response.status}`,
        });
      } else if (error.request) {
        // 请求已经成功发起，但没有收到响应
        // \`error.request\` 在浏览器中是 XMLHttpRequest 的实例，
        // 而在node.js中是 http.ClientRequest 的实例
        notification.error("None response! Please retry.");
      } else {
        // 发送请求时出了点问题
        notification.error("Request error, please retry.");
      }
    },
  },

  // 请求拦截器
  requestInterceptors: [
    (config: any) => {
      // 请求方法统一为 POST
      config.method = "POST";
      // 获取userInfo和token
      let userInfo = getLocalStorageInfo("userInformation");
      let token = getSessionStorageInfo("token")?.token;

      // 判断token和用户信息是否存在
      if (
        (!token || !userInfo) &&
        config.methodName !== "getIssueApps" &&
        config.methodName !== "login"
      ) {
        // history.push('/login');
      }

      if (token == undefined) {
        token = "";
      }

      // 判断一些
      const headers = {
        "Content-Type": "application/json",
        Accept: "application/json",
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
      let data = {};
      if (config?.version) {
        headers.version = config?.version;
      }

      if (pagination) {
        data = {
          ...config.bizParams,
          pagination,
        };
      } else {
        data = {
          ...config.bizParams,
        };
      }
      config.headers = {
        ...headers,
      };
      // 拦截请求配置，进行个性化处理。
      console.log("config");
      console.log(config);
      const url = gatewayUrl + config.url;
      return { ...config, data, url };
    },
  ],

  // 响应拦截器
  responseInterceptors: [
    (response: any) => {
      // 拦截响应数据，进行个性化处理
      const { data } = response;
      console.log(response);
      if (data?.code !== 0) {
        notification.error({
          message: data?.message,
          description: data?.message,
        });
        return false;
      }
      return response;
    },
  ],
};
