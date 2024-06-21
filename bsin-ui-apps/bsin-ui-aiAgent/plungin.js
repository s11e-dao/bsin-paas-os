export default (api, opts) => {
  // 引用第三方css
  api.addHTMLLinks(() => {
    return [
      {
        rel: 'stylesheet',
        type: 'text/css',
        href: '//g.alicdn.com/chatui/sdk-v2/0.2.4/sdk.css',
      },
    ]
  })
  // 引用第三方js
  api.addHTMLScripts(() => {
    return [
      {
        type: 'text/javascript',
        src: '//g.alicdn.com/chatui/sdk-v2/0.2.4/sdk.js',
      },
      {
        type: 'text/javascript',
        src: '//g.alicdn.com/chatui/extensions/0.0.7/isv-parser.js',
      },
      {
        type: 'text/javascript',
        src: '//g.alicdn.com/chatui/icons/0.3.0/index.js',
        async: true,
      },
    ]
  })
}
