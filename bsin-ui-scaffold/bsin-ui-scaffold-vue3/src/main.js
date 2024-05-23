import "./public-path";
import { createApp } from "vue";
import { createRouter, createWebHashHistory } from "vue-router";
import App from "./App.vue";
import routes from "./router";
import store from "./store";
// 导入qiankun.js
import { registerMicroApps, start } from "qiankun";

let router = null;
let instance = null;
let history = null;

function render(props = {}) {
  const { container } = props;
  history = createWebHashHistory();
  // window.__POWERED_BY_QIANKUN__ ? "#/vue3" : "#/"
  router = createRouter({
    // base: "/vue3",
    history,
    routes,
  });

  instance = createApp(App);
  instance.use(router);
  instance.use(store);
  instance.mount(container ? container.querySelector("#app") : "#app");
}

if (!window.__POWERED_BY_QIANKUN__) {
  render();
}

export async function bootstrap() {
  console.log("vue");
  window.localStorage.setItem("bsin-microAppMount", "3");
}

function storeTest(props) {
  props.onGlobalStateChange &&
    props.onGlobalStateChange(
      (value, prev) =>
        console.log(`[onGlobalStateChange - ${props.name}]:`, value, prev),
      true
    );
  props.setGlobalState &&
    props.setGlobalState({
      ignore: props.name,
      user: {
        name: props.name,
      },
    });
}

export async function mount(props) {
  storeTest(props);
  render(props);
  instance.config.globalProperties.$onGlobalStateChange =
    props.onGlobalStateChange;
  instance.config.globalProperties.$setGlobalState = props.setGlobalState;
}

export async function unmount() {
  console.log("unvue");
  window.localStorage.setItem("bsin-microAppMount", "2");
  instance.unmount();
  instance._container.innerHTML = "";
  instance = null;
  router = null;
  history.destroy();
}

// 注册子应用;
registerMicroApps([
  {
    name: "bsin-ui-upms", // 子应用名称
    entry: "http://114.116.93.253:32475", // 子应用入口
    container: "#react", // 子应用所在容器
    activeRule: "#/vue3/bsin-ui-upms", // 子应用触发规则（路径）
  },
]);

// 开启服务,配置项见官方文档
start();
