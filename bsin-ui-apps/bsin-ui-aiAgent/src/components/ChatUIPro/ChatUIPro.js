import React, { useEffect, useRef } from 'react'
import './styles.css'

export default function ChatUIPro() {
  const wrapper = useRef()

  useEffect(() => {
    const bot = new window.ChatSDK({
      root: wrapper.current,
      config: {
        navbar: {
          logo:
            'https://gw.alicdn.com/tfs/TB1Wbldh7L0gK0jSZFxXXXWHVXa-168-33.svg',
          title: '浙江政务服务网',
        },
        // 头像白名单
        avatarWhiteList: ['knowledge', 'recommend'],
        // 机器人信息
        robot: {
          avatar:
            'https://gw.alicdn.com/tfs/TB1U7FBiAT2gK0jSZPcXXcKkpXa-108-108.jpg',
        },
        // 首屏消息
        messages: [
          {
            type: 'system',
            content: {
              text: '智能助理进入对话，为您服务',
            },
          },
          {
            type: 'text',
            content: {
              text: '浙江智能助理为您服务，请问有什么可以帮您？',
            },
          },
          {
            type: 'card',
            content: {
              code: 'switch-location',
            },
          },
        ],
        // 快捷短语
        quickReplies: [
          { name: '健康码颜色' },
          { name: '入浙通行申报' },
          { name: '健康码是否可截图使用' },
          { name: '健康通行码适用范围' },
          { name: '最美战疫人有哪些权益' },
          { name: '我要查社保' },
          { name: '办理居住证需要什么材料' },
          { name: '公共支付平台' },
          { name: '浙江省定点医院清单' },
          { name: '智能问诊' },
        ],
        // 输入框占位符
        placeholder: '输入任何您想办理的服务',
        // 侧边栏
        sidebar: [
          {
            code: 'sidebar-suggestion',
            data: [
              {
                label: '疫情问题',
                list: [
                  '身边有刚从湖北来的人，如何报备',
                  '接触过武汉人，如何处理？',
                  '发烧或咳嗽怎么办？',
                  '去医院就医时注意事项',
                  '个人防护',
                  '传播途径',
                  '在家消毒',
                ],
              },
              {
                label: '法人问题',
                list: [
                  '企业设立',
                  '企业运行',
                  '企业变更',
                  '企业服务',
                  '企业注销',
                  '社会团体',
                  '民办非企业',
                ],
              },
            ],
          },
          {
            code: 'sidebar-tool',
            title: '常用工具',
            data: [
              {
                name: '咨询表单',
                icon: 'clipboard-list',
                url:
                  'http://www.zjzxts.gov.cn/wsdt.do?method=suggest&xjlb=0&ymfl=1&uflag=1',
              },
              {
                name: '投诉举报',
                icon: 'exclamation-shield',
                url: 'http://www.zjzxts.gov.cn/wsdt.do?method=suggest&xjlb=1',
              },
              {
                name: '办事进度',
                icon: 'history',
                url:
                  'http://www.zjzwfw.gov.cn/zjzw/search/progress/query.do?webId=1',
              },
            ],
          },
          {
            code: 'sidebar-phone',
            title: '全省统一政务服务热线',
            data: ['12345'],
          },
        ],
      },
    })
    bot.run()
  }, [])

  // 注意 wrapper 的高度
  return <div style={{ height: '100%' }} ref={wrapper} />
}
