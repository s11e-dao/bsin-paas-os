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
  Avatar,
  Popover,
  theme,
  Drawer,
  Modal,
  Switch,
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

import { getAppListByUserId } from '@/services/getAppByUserId'
import { getUserMenuTreeByAppCode } from '@/services/appMenu'

import BsinChat from './bsinChat'


export type AppMenu = {
  title: string
  menuName: string
  icon: string
  to: string
  children?: AppMenu[]
}[]

// Add this interface to define the app structure
interface UserApp {
  name: string;
  path: string;
  url: string;
  icon: React.ReactNode;
  logo: string;
  description: string;
}

export default () => {

  const [isModalOpen, setIsModalOpen] = useState(false)
  const [customerInfo, setCustomerInfo] = useState({})

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

  //top mix点击自定义菜单应用DO
  //状态判断是否从top mix过来DO
  const [specialMenustatus, setspecialMenustatus] = useState(false)
  const getAppMenu = () => {
    setspecialMenustatus(true)
  }
  //DO监听改变pathname 判断是否是从top mix点击自定义菜单过来需要刷新（不在getAppMenu刷新：刷新之前此方法pathname还是之前的路径，所以不能立即获取改变后的pathname）
  useEffect(() => {
    if (specialMenustatus) {
      actionRef.current?.reload()
    } else {
      // 设置菜单焦点为当前路径
      setPathname(location.pathname);
      // 获取当前应用的菜单
      getUserApplicationMenu(location.pathname?.split('/')[1])
    }
  }, [location.pathname.split('/')[1]])

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

  const [userApps, setUserApps] = useState<UserApp[]>([])

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
  const getAppLogo = (appLogo: any) => {
    let appLogoImg = logoImg;
    if (appLogo) {
      appLogoImg = appLogo
    }
    return <Avatar shape="square" src={appLogoImg} />
  }

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

  const getAppSubMenus = (subMenus: any) => {
    let appSubMenus = subMenus.map((item: any) => {
      return {
        name: item.menuName,
        path: item.path,
        icon: getTopMenuIon(item.icon),
        description: item.remark,
      }
    })
    return appSubMenus;
  }

  // 根据布局生成最左侧子应用数据
  const getLayoutMenus = async () => {
    let layoutMenudata: any[] = []
    const token = getSessionStorageInfo("token");
    if (token) {
      const appRes = await getAppListByUserId({})
      if (appRes.code === 0) {
        let apps = appRes.data?.apps.map((item: any) => {
          return {
            name: item.appName,
            path: item.appCode,
            url: item.url,
            icon: getAppLogo(item.logo),
            logo: item.logo,
            description: item.remark,
          } as UserApp;  // Add type assertion here
        })
        setUserApps(apps)
        let appMenus = []
        let appCode = location.pathname.split('/')[1]
        if (location.pathname.split('/')[1] == 'home') {
          appCode = appRes.data?.defaultApp?.appCode
        }
        setDefaultApp(appRes.data?.defaultApp)
        // 根据默认应用查询应用菜单及默认选中home导航
        let menuRes
        if (appCode) {
          menuRes = await getUserMenuTreeByAppCode({ appCode: appCode })
        }

        console.log(menuRes)
        if (menuRes?.code === 0) {
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
              routes: getAppSubMenus(item?.children)
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

  // 是否展示展开收起图标
  const collapsedButtonRender = (layout) => {
    if (layout == 'side') {
      return false
    }
  }

  // 默认展开还是收起
  const collapsed = (layout) => {
    if (layout == 'side') {
      return true
    }
  }

  const logout = () => {
    setIsLogin(false)
    localStorage.clear()
    sessionStorage.clear()
    history.push('/login')
  }

  // Refactor the checkMicroAppAvailability function to be more robust
  const checkMicroAppAvailability = async (appCode: string) => {
    if (!appCode || appCode === 'home') return true;

    // Find the app in userApps to get its URL
    const app = userApps.find(app => app.path === appCode);
    if (!app?.url) return true; // If app not found or has no URL, assume it's available

    try {
      // Add timeout to the fetch request to prevent long waiting times
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000); // 5 second timeout

      const response = await fetch(app.url, {
        method: 'HEAD', // Use HEAD request for faster checking
        signal: controller.signal
      });

      clearTimeout(timeoutId);
      return response.ok;
    } catch (error) {
      console.error(`Failed to connect to micro-app ${appCode}:`, error);
      return false;
    }
  };

  return (
    <div
      id="bsin-pro-layout"
      style={{
        height: '100vh',
        overflow: 'auto',
      }}
    >
      <ProConfigProvider hashed={false}>
        <ConfigProvider
          getTargetContainer={() => {
            return document.getElementById('bsin-pro-layout') || document.body
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
              title: getLocalStorageInfo('userInfo')?.username || getLocalStorageInfo('merchantInfo')?.username || getLocalStorageInfo('sysAgentInfo')?.username,
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
                                // 处理选中路由
                                setPathname('/userCenter')
                                // 清空菜单数据
                                setAppMenus({})
                                // 根据当前登录的用户类型判断展示不同的中心：租户个人中心，商户个人中心
                                history.push(`/userCenter`)
                              }}
                            >
                              个人中心
                            </span>
                          ),
                        },
                        {
                          key: 'logout',
                          icon: <LogoutOutlined onClick={logout} />,
                          label: (
                            <span onClick={logout}>
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
                <SelectLang onItemClick={(params) => {
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
                  <AppCenter userApps={userApps} getAppMenu={getAppMenu} />
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
              return (
                <div
                  onClick={() => {
                    // Check if we're recovering from an error
                    const isRecovering = window.localStorage.getItem('bsin-microAppErrorRecovery') === '1';
                    if (isRecovering) {
                      // Clear the recovery flag
                      window.localStorage.removeItem('bsin-microAppErrorRecovery');

                      // Force reload the page to clear any lingering state
                      if (item.path !== '/home') {
                        window.location.href = item.path;
                        return;
                      }
                    }

                    // Normal flow - initialize micro-app status
                    window.localStorage.setItem('bsin-microAppMountStatus', '1');

                    // Check if the app is available before navigating
                    const appCode = item?.path?.split('/')[1] || '';
                    checkMicroAppAvailability(appCode).then(isAvailable => {
                      if (isAvailable) {
                        // Query application menu
                        getUserApplicationMenu(appCode);
                        // Navigate to the app
                        history.push(item.path);
                        setPathname(item.path || '/home');
                      } else {
                        message.warning('该应用当前不可用，请稍后再试');
                      }
                    });
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
                {layoutMode == 'side' && showSideMenu && appMenus && Object.keys(appMenus).length > 0 && (
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
                customerInfo={customerInfo}
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
