const packageJson = require('../package.json'); // 假设package.json位于当前目录
const packageName = packageJson.name;

//判断当前运行环境是独立运行的还是在父应用里面进行运行，配置全局的公共资源路径
if (window.__POWERED_BY_QIANKUN__) {
    console.log(window.__INJECTED_PUBLIC_PATH_BY_QIANKUN__)
    console.log(__webpack_public_path__)
    __webpack_public_path__ = window.__INJECTED_PUBLIC_PATH_BY_QIANKUN__;
}

//如果是独立运行window.__POWERED_BY_QIANKUN__=undefined
if (!window.__POWERED_BY_QIANKUN__) {
    console.log("非基座运行环境")
    // 跳转到非基座运行提示页面
    window.location.href = "/#/"+ packageName +"/uncontainer"
}
