import MyTabs from './tabs'

const bot = new window.ChatSDK({
  components: {
    'my-tabs': MyTabs, //这里的my-tabs是设置的code唯一标识
  },
  sidebar: [
    //在sidebar中的code中配置，相当于使用组件
    {
      title: '测试',
      code: 'my-tabs',
      data: [
        {
          lable: '疫情问题',
          list: ['传播途径', '在家消毒'],
        },
        {
          lable: '法人问题',
          list: ['社会团体', '民办非企业'],
        },
      ],
    },
  ],
})
