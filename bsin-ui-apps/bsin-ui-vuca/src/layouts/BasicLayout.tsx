import {
  CaretDownFilled,
  CloudUploadOutlined,
  GithubFilled,
  LoginOutlined,
  InfoCircleFilled,
  LogoutOutlined,
} from '@ant-design/icons';
import type { ProSettings } from '@ant-design/pro-components';
import {
  PageContainer,
  ProConfigProvider,
  ProLayout,
  SettingDrawer,
} from '@ant-design/pro-components';
import { css } from '@emotion/css';
import {
  Button,
  ConfigProvider,
  Dropdown,
  Form, Input,
  message,
  Modal,
} from 'antd';
import React, { useState, useRef, useEffect } from 'react';
import { Outlet, history, useLocation, SelectLang } from 'umi'

import PricingComponent from '@/components/PricingComp'

import MenuComp from "./menu/index"
import MenuCard from "./MenuCard";
import SearchInput from "./SearchInput";

import confluxFluent from "@/utils/confluxFluent"
import defaultProps from './_defaultProps';
import logoImg from "../assets/logo.png";
import bsinBot from "../assets/image/bsin-bot.svg";

import "./index.less"

import { getAppByUserId, getAppListByUserId } from '@/services/getAppByUserId';
import { getPublishApps } from '@/services/getPublishApps';
import { getUserMenuTreeByAppCode } from '@/services/appMenu';
import { userLogin } from '@/services/login';


import customMenuDate from './customMenu';

let serviceData: any[] = customMenuDate;

export type AppMenu = {
  title: string;
  menuName: string;
  icon: string;
  to: string;
  children?: AppMenu[];
}[];

export default () => {

  const [isModalOpen, setIsModalOpen] = useState(false);

  const [isLogin, setIsLogin] = useState(false);

  const showModal = () => {
    setIsModalOpen(true);
  };

  // 获取编辑表单信息
  const [loginFormRef] = Form.useForm()

  // 登录处理
  const handleLoginOk = async () => {

    setIsModalOpen(false);
    setIsLogin(true);

    // loginFormRef
    //   .validateFields()
    //   .then(async () => {
    //     var formInfo = loginFormRef.getFieldsValue()
    //     const res = await userLogin({});
    //     if (res.code == '000000') {
    //       setIsModalOpen(false);
    //       console.log(res)
    //       setIsLogin(true);
    //     } else {
    //       message.error(res.message)
    //     }
    //   })
    //   .catch(() => { })
  };

  const handleCancel = () => {
    setIsModalOpen(false);
  };

  const [settings, setSetting] = useState<Partial<ProSettings> | undefined>({
    fixSiderbar: true,
    layout: 'side', // 默认菜单布局模式 side top mix
    // splitMenus: true, // 打开有bug
  });

  const [toggle, setToggle] = useState(false);

  const [layoutMode, setLayoutMode] = useState("side");

  const actionRef = useRef<{
    reload: () => void;
  }>();

  const handleMenuHeaderClick = () => {
    console.log(`Clicked menu item`);
  };

  const [pathname, setPathname] = useState('/square');

  const [showSideMenu, setShowSideMenu] = useState(false);

  // 刷新的时候获取url的路径
  const location = useLocation();
  // const locationUrl = useLocation().pathname.split('/')[1];

  useEffect(() => {
    console.log(location.pathname)
  }, []);

  useEffect(() => {
    console.log(location.pathname)
    setPathname(location.pathname)
    console.log(settings?.layout)
    setLayoutMode(settings?.layout)
    // f5刷新的时候获取location.pathname应用对应的菜单
    let appCode = location.pathname.split('/')[1];
    if (
      appCode !== 'workplace' &&
      appCode !== 'user' &&
      appCode !== 'apps'
    ) {
      if (settings?.layout != "side") {
        actionRef.current?.reload();
      }
      // getUserApplicationMenu(appCode);
      // // 路由跳转
    }
    // 根据settings?.layout处理菜单样式
  }, [location.pathname, settings?.layout]);

  useEffect(() => {
    // 根据settings?.layout重新渲染菜单数据
    actionRef.current?.reload();
  }, [settings?.layout]);

  const [num, setNum] = useState(40);
  if (typeof document === 'undefined') {
    return <div />;
  }

  const logo = () => {
    return (
      <>
        <img src={logoImg} />
      </>
    );
  }

  const enterWallet = async () => {
    await confluxFluent.enable();
    // 获取钱包地址
    console.log(confluxFluent.getAccount())
  }

  // 使用状态来追踪当前活动的Tab
  const [activeTab, setActiveTab] = useState(1);

  // 切换Tab的事件处理函数
  const handleTabClick = (tabNumber: any) => {
    setActiveTab(tabNumber);
  };


  // 切换Tab的事件处理函数
  const collapsedButtonRender = (layout) => {
    if (layout == "side") {
      return false
    }
  };

  // 切换Tab的事件处理函数
  const collapsed = (layout) => {
    if (layout == "side") {
      return true
    }
  };

  const onFinish = (values: any) => {
    console.log('Success:', values);
  };

  const onFinishFailed = (errorInfo: any) => {
    console.log('Failed:', errorInfo);
  };

  type FieldType = {
    username?: string;
    password?: string;
    remember?: string;
  };


  const [upgradeShow, setUpgradeShow] = useState(false);

  const upgradeShowClick = () => {
    setUpgradeShow(true)
  };

  const upgradeClick = () => {
    setUpgradeShow(false)
  };

  const upgradeCancel = () => {
    setUpgradeShow(false)
  };

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
            return document.getElementById('test-pro-layout') || document.body;
          }}
        >
          <ProLayout
            breakpoint={false} //  关闭后defaultCollapsed生效
            defaultCollapsed={true} // 默认的菜单的收起
            // collapsed={true}
            collapsed={collapsed(settings?.layout)} // 左侧导航是否展开
            // 是否显示展开收缩按钮 // 组合使用
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
            prefixCls="bsin" // className前缀
            actionRef={actionRef} // 菜单导航刷新
            title="bsin-paas"
            logo={logo()}
            // 展开的宽度
            siderWidth={218}
            bgLayoutImgList={[
              {
                src: 'https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png',
                left: 85,
                bottom: 100,
                height: '303px',
              },
              {
                src: 'https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png',
                bottom: -68,
                right: -45,
                height: '303px',
              },
              {
                src: 'https://img.alicdn.com/imgextra/i3/O1CN018NxReL1shX85Yz6Cx_!!6000000005798-2-tps-884-496.png',
                bottom: 0,
                left: 0,
                width: '331px',
              },
            ]}
            //   {...defaultProps}
            //   菜单路由，从后端获取数组组装
            menu={{
              // 图标加文字组合
              type: 'group',
              onLoadingChange: (loading) => {
                console.log(loading)
                setShowSideMenu(false)
                if (!loading) {
                  setTimeout(() => {
                    setShowSideMenu(true)
                  }, 300);
                }
              },
              collapsedShowTitle: true, //收缩显示文字
              request: async () => {
                // layout 菜单数据
                return serviceData;
              },
            }}
            location={{
              pathname,
            }}
            siderMenuType="group"
            avatarProps={{
              src: 'https://gw.alipayobjects.com/zos/antfincdn/efFD%24IOql2/weixintupian_20170331104822.jpg',
              size: 'small',
              title: '博羸',
              render: (props, dom) => {

                if (!isLogin) {
                  return (
                    <LoginOutlined key="InfoCircleFilled"
                      onClick={showModal} />
                  );
                } else {
                  return (
                    <Dropdown
                      menu={{
                        items: [
                          {

                            key: 'logout',
                            icon: <LogoutOutlined />,
                            label: <span onClick={() => {
                              console.log("点击图标")
                              setIsLogin(false)
                            }} >退出登录</span>,
                          },
                        ],
                      }}
                    >
                      {dom}
                    </Dropdown>
                  );
                }
              },
            }}
            // 左侧底部图标
            actionsRender={(props) => {
              if (props.isMobile) return [];
              if (typeof window === 'undefined') return [];
              return [
                props.layout !== 'side' && document.body.clientWidth > 1400 ? (
                  <SearchInput />
                ) : undefined,
                // <QuestionCircleFilled key="QuestionCircleFilled" />,
                // 国际化支持
                <SelectLang />,
                <GithubFilled key="GithubFilled"
                  onClick={() => {
                    console.log("开源地址")
                    window.open("https://gitee.com/s11e-DAO/bsin-paas-all-in-one")
                  }} />,
                <CloudUploadOutlined key="upgrade"
                  onClick={upgradeShowClick} />
              ];
            }}
            // 标题和logo
            headerTitleRender={(logo, title, _) => {
              const defaultDom = (
                <a>
                  {logo}
                  {title}
                </a>
              );
              if (typeof window === 'undefined') return defaultDom;
              if (document.body.clientWidth < 1000) {
                return defaultDom;
              }
              if (_.isMobile) return defaultDom;
              return (
                <>
                  {defaultDom}
                </>
              );
            }}
            menuFooterRender={(props) => {
              if (props?.collapsed) return undefined;
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
              );
            }}
            // 点击logo名称事件
            onMenuHeaderClick={handleMenuHeaderClick}
            // 左侧应用点击事件
            menuItemRender={(item, dom) => {
              // console.log(item)
              return (
                <div
                  onClick={() => {
                    console.log(item.path)
                    // 查询应用菜单
                    history.push(item.path)
                    setPathname(item.path || '/square');
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
              <div>
                {/* {layoutMode == "side" && showSideMenu && appMenus && <MenuComp appMenus={appMenus} />} */}
                <div style={{ margin: "0 15px" }}>
                  <Outlet />
                </div>
              </div>

              {/* 登录表单 */}
              <Modal title="登录" open={isModalOpen}
                onOk={handleLoginOk}
                onCancel={handleCancel}
                footer={[]}
              >
                <div className="loginNav">
                  <p onClick={() => handleTabClick(1)} className={activeTab === 1 ? 'loginNavItemA' : 'loginNavItem'}>钱包</p>
                  <p onClick={() => handleTabClick(2)} className={activeTab === 2 ? 'loginNavItemA' : 'loginNavItem'}>账号</p>
                </div>
                <div className='loginForm'>
                  {activeTab === 1 &&
                    <div>
                      <Button type="primary"
                        onClick={() => enterWallet()}
                      >连接钱包</Button>
                    </div>}
                  {activeTab === 2 &&
                    <div style={{ width: "380", marginTop: "40px" }}>
                      <Form
                        name="basic"
                        form={loginFormRef}
                        labelCol={{ span: 8 }}
                        wrapperCol={{ span: 16 }}
                        style={{ maxWidth: 400, textAlign: "center" }}
                        initialValues={{ remember: true }}
                        onFinish={onFinish}
                        onFinishFailed={onFinishFailed}
                        autoComplete="off"
                      >
                        <Form.Item<FieldType>
                          label="用户名"
                          name="username"
                          rules={[{ required: true, message: 'Please input your username!' }]}
                        >
                          <Input />
                        </Form.Item>

                        <Form.Item<FieldType>
                          label="密码"
                          name="password"
                          rules={[{ required: true, message: 'Please input your password!' }]}
                        >
                          <Input.Password />
                        </Form.Item>

                        <Form.Item<FieldType>
                          label="邀请码"
                          name="inviteCode"
                          rules={[{ required: true, message: 'Please input your inviteCode!' }]}
                        >
                          <Input />
                        </Form.Item>

                        {/* <Form.Item<FieldType>
                          name="remember"
                          valuePropName="checked"
                          wrapperCol={{ offset: 8, span: 16 }}
                        >
                          <Checkbox>Remember me</Checkbox>
                        </Form.Item> */}

                        <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
                          <Button onClick={handleLoginOk} type="primary" htmlType="submit">
                            登录
                          </Button>
                        </Form.Item>
                      </Form>

                    </div>}
                </div>
              </Modal>
            </PageContainer>

            <SettingDrawer
              pathname={pathname}
              enableDarkTheme
              getContainer={(e: any) => {
                if (typeof window === 'undefined') return e;
                return document.getElementById('test-pro-layout');
              }}
              settings={settings}
              onSettingChange={(changeSetting) => {
                setSetting(changeSetting);
              }}
              disableUrlParams={false}
            />
          </ProLayout>

          <Modal width={700} title="" open={upgradeShow} onOk={upgradeClick} onCancel={upgradeCancel} footer={[]}>
            <PricingComponent />
          </Modal>
        </ConfigProvider>
      </ProConfigProvider>
    </div>
  );
};