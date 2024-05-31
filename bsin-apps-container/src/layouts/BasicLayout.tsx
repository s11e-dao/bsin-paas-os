import {
  CaretDownFilled,
  DoubleRightOutlined,
  GithubFilled,
  ChromeFilled,
  InfoCircleFilled,
  LogoutOutlined,
  PlusCircleFilled,
  CommentOutlined,
  CustomerServiceOutlined,
  SearchOutlined,
  PieChartOutlined,
  UserOutlined,
  AreaChartOutlined,
  NodeExpandOutlined,
  DotChartOutlined,
  UserSwitchOutlined,
  SubnodeOutlined,
  ShareAltOutlined,
  ScheduleOutlined,
  PrinterOutlined,
  IdcardOutlined,
  DollarOutlined,
  BgColorsOutlined,
  BellOutlined,
  AndroidOutlined,
} from '@ant-design/icons'
import type { ProSettings } from '@ant-design/pro-components'
import {
  PageContainer,
  ProCard,
  ProConfigProvider,
  ProBreadcrumb,
  ProLayout,
  SettingDrawer,
} from '@ant-design/pro-components'
import {
  Button,
  ConfigProvider,
  Divider,
  Dropdown,
  Input,
  Popover,
  theme,
  Drawer,
  Modal,
  Switch,
  FloatButton,
  message,
} from 'antd'
import React, { useState, useRef, useEffect } from 'react'
import { Outlet, history, useLocation, SelectLang, setLocale } from 'umi'

import {
  setLocalStorageInfo,
  setSessionStorageInfo,
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'

import MenuComp from './menu/index'
import AppCenter from './AppCenter'
import SearchInput from './SearchInput'

import confluxFluent from '@/utils/confluxFluent'
import defaultProps from './_defaultProps'
import logoImg from '../assets/logo.png'

import './index.less'

import { getAppByUserId, getAppListByUserId } from '@/services/getAppByUserId'
import { getPublishApps } from '@/services/getPublishApps'
import { getUserMenuTreeByAppCode } from '@/services/appMenu'

import customMenuDate from './customMenu'
import BsinChat from './bsinChat'

import {
  chatWithCopilot,
  getAppAgent,
  getChatHistoryList,
} from './service'

let serviceData: any[] = customMenuDate

export type AppMenu = {
  title: string
  menuName: string
  icon: string
  to: string
  children?: AppMenu[]
}[]

export default () => {
  let defaultMerchantNo = process.env.defaultMerchantNo

  const [isModalOpen, setIsModalOpen] = useState(false)
  const [defaultCopilot, setDefaultCopilot] = useState({})
  const [customerInfo, setCustomerInfo] = useState({})

  // let defaultCopilot = {}

  const [isLogin, setIsLogin] = useState(false)

  const handleOk = () => {
    setIsModalOpen(false)
    setIsLogin(true)
  }

  const handleCancel = () => {
    setIsModalOpen(false)
  }

  // 国际化语言切换
  const HandleLocale = (params) => {
    history.push('/home')
    setLocale(params.key);
  }

  const [settings, setSetting] = useState<Partial<ProSettings> | undefined>({
    fixSiderbar: true,
    layout: 'side', // 默认菜单布局模式 side top mix
    // splitMenus: true, // 打开有bug
  })

  const [layoutMode, setLayoutMode] = useState('side')

  const actionRef = useRef<{
    reload: () => void
  }>()

  const [pathname, setPathname] = useState('/home')

  const handleMenuHeaderClick = async () => {
    console.log(`Clicked menu item`)
    setPathname('/home')
    history.push('/home')
    // 设置菜单数据为空
    setAppMenus({})
    // 查询用户子应用
    await getLayoutMenus()
  }

  const [showSideMenu, setShowSideMenu] = useState(false)

  const [defaultApp, setDefaultApp] = useState({
    appCode: '',
  })

  // 刷新的时候获取url的路径
  const location = useLocation()

  window.addEventListener('load', function () {
    console.log('页面可能被刷新了');
  });


  let chatData = {
    chats: {
      ZGxiX2p4: {
        content: '历史聊天记录 user',
        createAt: 1697862242452,
        id: 'ZGxiX2p4',
        role: 'user',
        updateAt: 1697862243540,
      },
      Sb5pAzLL: {
        content: '历史聊天记录 assistant',
        createAt: 1697862247302,
        id: 'Sb5pAzLL',
        parentId: 'ZGxiX2p4',
        role: 'assistant',
        updateAt: 1697862249387,
        model: 'gpt-3.5-turbo',
      },
    },
  }

  useEffect(() => {
    // TODO 浏览器刷新之后重新注册子应用
    let params = {
      merchantNo: defaultMerchantNo,
      type: '1',
    }
    const token = getSessionStorageInfo("token");
    if (token) {
      //TODO: 获取租户对应的appAgent
      getAppAgent(params).then((res) => {
        if (res?.code == 0) {
          setDefaultCopilot(res?.data)
          setLocalStorageInfo('defaultCopilot', res.data)
          let customerInfo = getLocalStorageInfo('customerInfo')
          setCustomerInfo(customerInfo)
          console.log('defaultCopilot:')
          console.log(res?.data)
          let params = {
            receiver: res.data.serialNo,
            sender: customerInfo?.customerNo,
            chatType: 'chat',
          }
          getChatHistoryList(params).then((res) => {
            if (res?.code == 0) {
              let i = 0
              res?.data.map((historyChat) => {
                let historyChatTmp = {
                  content: historyChat.message,
                  createAt: historyChat.timestamp,
                  // id: historyChat.sender + customerInfo.customerNo,
                  id: 'ZGxiX2p4',
                  role:
                    historyChat.sender == customerInfo.customerNo
                      ? 'user'
                      : 'assistant',
                  updateAt: 1697862243540,
                }
                let id = historyChat.sender + customerInfo.customerNo + i
                i++
                chatData.chats.ZGxiX2p4 = historyChatTmp
              })
            }
          })
          console.log(chatData)
        } else {
          message.error('查询默认copilot失败！！！')
        }
      })
    }

  }, [])

  useEffect(() => {
    // 根据settings?.layout重新渲染菜单数据
    setLayoutMode(settings?.layout)
    actionRef.current?.reload()
  }, [settings?.layout])

  const [num, setNum] = useState(40)
  if (typeof document === 'undefined') {
    return <div />
  }

  const logo = () => {
    return (
      <>
        <img src={logoImg} />
      </>
    )
  }

  const enterWallet = async () => {
    await confluxFluent.enable()
    // 获取钱包地址
    console.log(confluxFluent.getAccount())
  }

  // 使用状态来追踪当前活动的Tab
  const [activeTab, setActiveTab] = useState(1)

  // 切换Tab的事件处理函数
  const handleTabClick = (tabNumber: any) => {
    setActiveTab(tabNumber)
  }

  const [userApps, setUserApps] = useState([])

  const [appMenus, setAppMenus] = useState({})

  // 获取用户子应用菜单
  const getUserApplicationMenu = async (appCode: string) => {
    if (appCode === '') {
      setAppMenus({})
      return
    }
    let res = await getUserMenuTreeByAppCode({ appCode })
    if (res && res.code == 0) {
      setAppMenus(res.data[0])
    }
  }

  // const [layoutMenudata, setLayoutMenudata] = useState([]);

  const getTopMenuIon = (icon: any) => {
    let menuIon = <UserOutlined />
    // console.log(icon == null);
    if (icon == 'UserOutlined') {
      menuIon = <UserOutlined />
    } else if (icon == 'AreaChartOutlined') {
      menuIon = <AreaChartOutlined />
    } else if (icon == 'NodeExpandOutlined') {
      menuIon = <NodeExpandOutlined />
    } else if (icon == 'DotChartOutlined') {
      menuIon = <DotChartOutlined />
    } else if (icon == 'UserSwitchOutlined') {
      menuIon = <UserSwitchOutlined />
    } else if (icon == 'ScheduleOutlined') {
      menuIon = <ScheduleOutlined />
    } else if (icon == 'ShareAltOutlined') {
      menuIon = <ShareAltOutlined />
    } else if (icon == 'PrinterOutlined') {
      menuIon = <PrinterOutlined />
    } else if (icon == 'IdcardOutlined') {
      menuIon = <IdcardOutlined />
    } else if (icon == 'DollarOutlined') {
      menuIon = <DollarOutlined />
    } else if (icon == 'BgColorsOutlined') {
      menuIon = <BgColorsOutlined />
    } else if (icon == 'CustomerServiceOutlined') {
      menuIon = <CustomerServiceOutlined />
    } else if (icon == 'SubnodeOutlined') {
      menuIon = <SubnodeOutlined />
    } else if (icon == 'AndroidOutlined') {
      menuIon = <AndroidOutlined />
    } else if (icon == 'BellOutlined') {
      menuIon = <BellOutlined />
    }

    // else {
    //   menuIon = <span className={` ${icon} iconfont `} style={{ marginRight: '0.5rem' }}></span>;
    // }
    return menuIon
  }

  // 根据布局生成最左侧子应用数据
  const getLayoutMenus = async () => {
    let layoutMenudata: any[] = []
    const token = getSessionStorageInfo("token");
    if (token) {
      const appRes = await getAppListByUserId({})
      if (appRes.code === 0) {
        console.log(appRes.data)
        let apps = appRes.data?.apps.map((item: any) => {
          return {
            name: item.appName,
            path: item.appCode,
            icon: getTopMenuIon(item.logo),
            description: item.remark,
          }
        })
        setUserApps(apps)
        let appMenus = []
        let appCode = location.pathname.split('/')[1]
        if (location.pathname.split('/')[1] == 'home') {
          appCode = appRes.data?.defaultApp.appCode
        }
        setDefaultApp(appRes.data?.defaultApp)
        // 根据默认应用查询应用菜单及默认选中home导航
        const menuRes = await getUserMenuTreeByAppCode({ appCode: appCode })
        if (menuRes.code === 0) {
          // 跳转默认子应用
          // setPathname('/' + appCode)
          // history.push('/' + appCode)
          // setAppMenus(menuRes.data[0]);
          appMenus = menuRes.data[0]?.children?.map((item: any) => {
            return {
              name: item.menuName,
              path: item.path,
              icon: getTopMenuIon(item.icon),
              description: item.remark,
            }
          })
        }

        // 如果是side布局 返回应用列表，side布局(top和mix)返回选中应用菜单列表
        console.log(settings?.layout == 'side')
        if (settings?.layout == 'side') {
          layoutMenudata = apps
        } else {
          layoutMenudata = appMenus
        }
      }
    }

    return layoutMenudata
  }

  // 切换Tab的事件处理函数
  const collapsedButtonRender = (layout) => {
    if (layout == 'side') {
      return false
    }
  }

  // 切换Tab的事件处理函数
  const collapsed = (layout) => {
    if (layout == 'side') {
      return true
    }
  }

  return (
    <div
      id="test-pro-layout"
      style={{
        height: '100vh',
        overflow: 'auto',
      }}
    >
      {/* <ProConfigProvider hashed={false} dark={true}> */}
      <ProConfigProvider hashed={false}>
        <ConfigProvider
          getTargetContainer={() => {
            return document.getElementById('test-pro-layout') || document.body
          }}
        >
          <ProLayout
            breakpoint={false} //  关闭后defaultCollapsed生效
            defaultCollapsed={true} // 默认的菜单的收起
            // collapsed={true}
            collapsed={collapsed(settings?.layout)} // 左侧导航是否展开
            // 是否显示展开收缩按钮
            collapsedButtonRender={collapsedButtonRender(settings?.layout)}
            token={{
              colorBgAppListIconHover: 'rgba(0,0,0,0.06)',
              colorTextAppListIconHover: 'rgba(255,255,255,0.95)',
              colorTextAppListIcon: 'rgba(255,255,255,0.85)',
              sider: {
                colorBgCollapsedButton: '#fff',
                colorTextCollapsedButtonHover: 'rgba(0,0,0,0.65)',
                colorTextCollapsedButton: 'rgba(0,0,0,0.45)',
                colorMenuBackground: '#004FD9', // 菜单背景色 #8943ff
                colorBgMenuItemCollapsedElevated: 'rgba(0,0,0,0.85)',
                colorMenuItemDivider: 'rgba(255,255,255,0.15)',
                colorBgMenuItemHover: 'rgba(0,0,0,0.06)',
                colorBgMenuItemSelected: '#8943ff', //选择菜单背景色 #8943ff
                colorTextMenuSelected: '#fff',
                colorTextMenuItemHover: 'rgba(255,255,255,0.75)',
                colorTextMenu: 'rgba(255,255,255,0.75)',
                colorTextMenuSecondary: 'rgba(255,255,255,0.65)',
                colorTextMenuTitle: 'rgba(255,255,255,0.95)',
                colorTextMenuActive: 'rgba(255,255,255,0.95)',
                colorTextSubMenuSelected: '#fff',
              },
              header: {
                colorBgHeader: '#004FD9',
                colorBgRightActionsItemHover: 'rgba(0,0,0,0.06)',
                colorTextRightActionsItem: 'rgba(255,255,255,0.65)',
                colorHeaderTitle: '#fff',
                colorBgMenuItemHover: 'rgba(0,0,0,0.06)',
                colorBgMenuItemSelected: 'rgba(0,0,0,0.15)',
                colorTextMenuSelected: '#fff',
                colorTextMenu: 'rgba(255,255,255,0.75)',
                colorTextMenuSecondary: 'rgba(255,255,255,0.65)',
                colorTextMenuActive: 'rgba(255,255,255,0.95)',
              },
            }}
            prefixCls="bsin" // className前缀 防止样式冲突
            actionRef={actionRef} // 菜单导航刷新
            title="bsin-paas"
            logo={logo()}
            // 展开的宽度
            siderWidth={218}
            bgLayoutImgList={[
              {
                src:
                  'https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png',
                left: 85,
                bottom: 100,
                height: '303px',
              },
              {
                src:
                  'https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png',
                bottom: -68,
                right: -45,
                height: '303px',
              },
              {
                src:
                  'https://img.alicdn.com/imgextra/i3/O1CN018NxReL1shX85Yz6Cx_!!6000000005798-2-tps-884-496.png',
                bottom: 0,
                left: 0,
                width: '331px',
              },
            ]}
            //   {...defaultProps}
            //   菜单路由，从后端获取数组组装
            // siderMenuType="group"
            menu={{
              // 图标加文字组合
              collapsedShowGroupTitle: true,
              // type: 'group',
              type: 'sub', // mix左侧菜单分类变为收缩
              onLoadingChange: (loading) => {
                console.log(loading)
                setShowSideMenu(false)
                if (!loading) {
                  setTimeout(() => {
                    setShowSideMenu(true)
                  }, 300)
                }
              },
              collapsedShowTitle: false, //收缩显示文字
              request: async () => {
                // layout 菜单数据
                return await getLayoutMenus()
              },
            }}
            location={{
              pathname,
            }}
            avatarProps={{
              src:
                getLocalStorageInfo('customerInfo')?.avatar == null
                  ? 'https://gw.alipayobjects.com/zos/antfincdn/efFD%24IOql2/weixintupian_20170331104822.jpg'
                  : getLocalStorageInfo('customerInfo')?.avatar,
              size: 'small',
              title: getLocalStorageInfo('userInformation')?.username,
              render: (props, dom) => {
                return (
                  <Dropdown
                    menu={{
                      items: [
                        {
                          key: 'userCenter',
                          icon: <UserOutlined />,
                          label: (
                            <span
                              onClick={() => {
                                // 根据当前登录的用户类型判断跳转不同的中心：租户个人中心，商户个人中心
                                history.push(
                                  `/${defaultApp.appCode}/userCenter`,
                                )
                              }}
                            >
                              个人中心
                            </span>
                          ),
                        },
                        {
                          key: 'logout',
                          icon: <LogoutOutlined />,
                          label: (
                            <span
                              onClick={() => {
                                console.log('点击图标')
                                setIsLogin(false)
                                localStorage.clear()
                                sessionStorage.clear()
                                history.push('/login')
                              }}
                            >
                              退出登录
                            </span>
                          ),
                        },
                      ],
                    }}
                  >
                    {dom}
                  </Dropdown>
                )
              },
            }}
            // 左侧底部图标
            actionsRender={(props) => {
              if (props.isMobile) return []
              if (typeof window === 'undefined') return []
              return [
                props.layout !== 'side' && document.body.clientWidth > 1400 ? (
                  <SearchInput />
                ) : undefined,
                // <QuestionCircleFilled key="QuestionCircleFilled" />,
                // 国际化支持
                <SelectLang onItemClick= {(params) => {
                  HandleLocale(params)
                }} />,
                <GithubFilled
                  key="GithubFilled"
                  onClick={() => {
                    console.log('开源地址')
                    window.open(
                      'https://gitee.com/s11e-DAO/bsin-paas-all-in-one',
                    )
                  }}
                />,
              ]
            }}
            // 标题和logo
            headerTitleRender={(logo, title, _) => {
              const defaultDom = (
                <a>
                  {logo}
                  {title}
                </a>
              )
              if (typeof window === 'undefined') return defaultDom
              if (document.body.clientWidth < 1000) {
                return defaultDom
              }
              if (_.isMobile) return defaultDom
              return (
                <>
                  {defaultDom}
                  <AppCenter userApps={userApps} />
                </>
              )
            }}
            menuFooterRender={(props) => {
              if (props?.collapsed) return undefined
              return (
                <div
                  style={{
                    textAlign: 'center',
                    paddingBlockStart: 12,
                  }}
                >
                  <div>© {new Date().getFullYear()} Made with love</div>
                  <div>by s11e dao</div>
                </div>
              )
            }}
            // 点击logo名称事件
            onMenuHeaderClick={handleMenuHeaderClick}
            // 左侧应用点击事件
            menuItemRender={(item, dom) => {
              // console.log(item)
              return (
                <div
                  onClick={() => {
                    // 初始化子应用开始状态
                    window.localStorage.setItem('bsin-microAppMountStatus', '1');
                    // 查询应用菜单
                    getUserApplicationMenu(item?.path?.split('/')[1] || '')
                    // 点击应用默认跳转到首页
                    history.push(item.path)
                    setPathname(item.path || '/home')
                  }}
                >
                  {dom}
                </div>
              )
            }}
            // 设置主题
            {...settings}
          >
            <PageContainer
              token={{
                paddingInlinePageContainerContent: num,
              }}
              header={{
                title: '',
              }}
            >
              {/* 内容渲染 */}
              <div style={{ display: 'flex' }}>
                {layoutMode == 'side' && showSideMenu && appMenus && (
                  <MenuComp appMenus={appMenus} />
                )}
                <div
                  className="bsin-layout-content"
                  style={{
                    margin: '15px',
                    flex: 1,
                    overflowY: 'auto',
                    overflowX: 'hidden',
                    height: '100vh',
                  }}
                >
                  <Outlet />
                </div>
              </div>
              <BsinChat
                defaultCopilot={defaultCopilot}
                customerInfo={customerInfo}
              // chatData={chatData}
              />
              <Modal
                title="登录"
                open={isModalOpen}
                onOk={handleOk}
                onCancel={handleCancel}
                footer={[]}
              >
                <div className="loginNav">
                  <p
                    onClick={() => handleTabClick(1)}
                    className={
                      activeTab === 1 ? 'loginNavItemA' : 'loginNavItem'
                    }
                  >
                    钱包
                  </p>
                  <p
                    onClick={() => handleTabClick(2)}
                    className={
                      activeTab === 2 ? 'loginNavItemA' : 'loginNavItem'
                    }
                  >
                    账号
                  </p>
                </div>
                <div className="loginForm">
                  {activeTab === 1 && (
                    <div>
                      <Button type="primary" onClick={() => enterWallet()}>
                        连接钱包
                      </Button>
                    </div>
                  )}
                  {activeTab === 2 && <div>账户登录</div>}
                </div>
              </Modal>
            </PageContainer>

            <SettingDrawer
              pathname={pathname}
              enableDarkTheme
              getContainer={(e: any) => {
                if (typeof window === 'undefined') return e
                return document.getElementById('test-pro-layout')
              }}
              settings={settings}
              onSettingChange={(changeSetting) => {
                setSetting(changeSetting)
              }}
              disableUrlParams={false}
            />
          </ProLayout>
        </ConfigProvider>
      </ProConfigProvider>
    </div>
  )
}
