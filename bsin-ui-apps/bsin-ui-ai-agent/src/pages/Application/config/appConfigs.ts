import React from 'react'
import { WechatOutlined, CustomerServiceOutlined, TeamOutlined, DingdingOutlined, CloudOutlined, AppstoreOutlined } from '@ant-design/icons'

export interface AppConfig {
  id: string
  title: string
  description: string
  icon: React.ReactNode
  appChannel: string
  requiredFields: string[]
  optionalFields: string[]
}

export interface AppDisplayItem {
  id: string
  icon: React.ReactNode
  title: string
  description: string
  hasCustom: boolean
  hasConnect: boolean
}

// 应用配置定义
export const appConfigs: Record<string, AppConfig> = {
  'wechat': {
    id: 'wechat',
    title: '个人微信',
    description: '打造你的微信专属智能机器人',
    icon: React.createElement(WechatOutlined, { style: { fontSize: 32, color: '#07C160' } }),
    appChannel: '8', // 个人微信
    requiredFields: ['appName', 'appId', 'appSecret', 'token', 'aesKey', 'notifyUrl', 'appDescription'],
    optionalFields: ['agentId'],
  },
  'wechat-public-enterprise': {
    id: 'wechat-public-enterprise',
    title: '微信公众号（企业）',
    description: '在企业公众号中接入智能回复',
    icon: React.createElement(WechatOutlined, { style: { fontSize: 32, color: '#07C160' } }),
    appChannel: '3', // 公众号
    requiredFields: ['appName', 'appId', 'appSecret', 'token', 'aesKey', 'notifyUrl', 'appDescription'],
    optionalFields: ['agentId'],
  },
  'wechat-public-personal': {
    id: 'wechat-public-personal',
    title: '微信公众号（个人）',
    description: '在个人订阅号中接入智能回复',
    icon: React.createElement(WechatOutlined, { style: { fontSize: 32, color: '#07C160' } }),
    appChannel: '3', // 公众号
    requiredFields: ['appName', 'appId', 'appSecret', 'token', 'aesKey', 'notifyUrl', 'appDescription'],
    optionalFields: ['agentId'],
  },
  'wechat-service': {
    id: 'wechat-service',
    title: '微信客服',
    description: '打造你的微信智能客服',
    icon: React.createElement(CustomerServiceOutlined, { style: { fontSize: 32, color: '#07C160' } }),
    appChannel: '8', // 微信客服
    requiredFields: ['appName', 'appId', 'appSecret', 'token', 'aesKey', 'notifyUrl', 'appDescription'],
    optionalFields: ['agentId'],
  },
  'enterprise-wechat': {
    id: 'enterprise-wechat',
    title: '企微微信',
    description: '打造你的企业微信数字员工',
    icon: React.createElement(TeamOutlined, { style: { fontSize: 32, color: '#4F8EF0' } }),
    appChannel: '5', // 企业微信
    requiredFields: ['appName', 'appId', 'appSecret', 'token', 'aesKey', 'notifyUrl', 'appDescription'],
    optionalFields: ['agentId'],
  },
  'dingtalk': {
    id: 'dingtalk',
    title: '钉钉',
    description: '制作你的钉钉智能机器人',
    icon: React.createElement(DingdingOutlined, { style: { fontSize: 32, color: '#0088FF' } }),
    appChannel: '20', // 接口
    requiredFields: ['appName', 'appId', 'appSecret', 'notifyUrl', 'appDescription'],
    optionalFields: ['agentId', 'token'],
  },
  'feishu': {
    id: 'feishu',
    title: '飞书',
    description: '在飞书上运行你的企业助手',
    icon: React.createElement(CloudOutlined, { style: { fontSize: 32, color: '#00D6B9' } }),
    appChannel: '15', // 接口
    requiredFields: ['appName', 'appId', 'appSecret', 'notifyUrl', 'appDescription'],
    optionalFields: ['agentId', 'token'],
  },
  'enterprise-wechat-app': {
    id: 'enterprise-wechat-app',
    title: '企微应用',
    description: '打造你的企业微信智能应用',
    icon: React.createElement(AppstoreOutlined, { style: { fontSize: 32, color: '#4F8EF0' } }),
    appChannel: '5', // 企业微信
    requiredFields: ['appName', 'appId', 'appSecret', 'token', 'aesKey', 'notifyUrl', 'appDescription'],
    optionalFields: ['agentId'],
  },
}

// 获取应用配置
export const getAppConfig = (appId: string): AppConfig | undefined => {
  return appConfigs[appId]
}

// 获取所有应用配置
export const getAllAppConfigs = (): AppConfig[] => {
  return Object.values(appConfigs)
}

// 客户运营应用显示数据
export const customerOperationApps: AppDisplayItem[] = [
  {
    id: 'wechat-public-enterprise',
    icon: React.createElement(WechatOutlined, { style: { fontSize: 32, color: '#07C160' } }),
    title: '微信公众号（企业）',
    description: '在企业公众号中接入智能回复',
    hasCustom: true,
    hasConnect: true,
  },
  {
    id: 'wechat-service',
    icon: React.createElement(CustomerServiceOutlined, { style: { fontSize: 32, color: '#07C160' } }),
    title: '微信客服',
    description: '打造你的微信智能客服',
    hasCustom: false,
    hasConnect: true,
  },
  {
    id: 'wechat-public-personal',
    icon: React.createElement(WechatOutlined, { style: { fontSize: 32, color: '#07C160' } }),
    title: '微信公众号（个人）',
    description: '在个人订阅号中接入智能回复',
    hasCustom: true,
    hasConnect: true,
  },
  {
    id: 'wechat',
    icon: React.createElement(WechatOutlined, { style: { fontSize: 32, color: '#07C160' } }),
    title: '微信',
    description: '打造你的微信专属智能机器人',
    hasCustom: false,
    hasConnect: true,
  },
  {
    id: 'enterprise-wechat',
    icon: React.createElement(TeamOutlined, { style: { fontSize: 32, color: '#4F8EF0' } }),
    title: '企微微信',
    description: '打造你的企业微信数字员工',
    hasCustom: false,
    hasConnect: true,
  },
]

// 协同办公应用显示数据
export const collaborationApps: AppDisplayItem[] = [
  {
    id: 'dingtalk',
    icon: React.createElement(DingdingOutlined, { style: { fontSize: 32, color: '#0088FF' } }),
    title: '钉钉',
    description: '制作你的钉钉智能机器人',
    hasCustom: true,
    hasConnect: true,
  },
  {
    id: 'feishu',
    icon: React.createElement(CloudOutlined, { style: { fontSize: 32, color: '#00D6B9' } }),
    title: '飞书',
    description: '在飞书上运行你的企业助手',
    hasCustom: true,
    hasConnect: true,
  },
  {
    id: 'enterprise-wechat-app',
    icon: React.createElement(AppstoreOutlined, { style: { fontSize: 32, color: '#4F8EF0' } }),
    title: '企微应用',
    description: '打造你的企业微信智能应用',
    hasCustom: true,
    hasConnect: true,
  },
]