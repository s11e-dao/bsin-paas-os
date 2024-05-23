import React, { useEffect, useState, useRef } from 'react';
import { PlusOutlined, EllipsisOutlined, MenuFoldOutlined } from '@ant-design/icons';
import { Layout, Card, theme, Button, Avatar, Dropdown, Space, message, Input } from 'antd';

import {
  ProFormRadio,
  CheckCard,
  ProList,
} from '@ant-design/pro-components';
import { Progress, Tag } from 'antd';
import { ProChat } from '@ant-design/pro-chat';
import { useTheme } from 'antd-style';

const { Search } = Input;

const { Header, Content, Footer, Sider } = Layout;

import BaseInfo from './BaseInfo';

// 引入组件

import AiWorkflow from '../../components/Flow';

import styles from "./index.less";

import "./global.less"

import { chats } from './mocks/threebody';


const App: React.FC = () => {

  const chatTheme = useTheme();

  const [isChat, setIsChat] = useState<boolean>(true);

  const [ghost, setGhost] = useState<boolean>(true);

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const onSearch = (value, _e, info) => console.log(info?.source, value);

  const [isOpen, setIsOpen] = useState(true);

  // 使用状态来追踪当前活动的Tab
  const [activeTab, setActiveTab] = useState(1);

  // 切换Tab的事件处理函数
  const handleTabClick = (tabNumber: any) => {
    setActiveTab(tabNumber);
  };

  // 新增AI app
  const addAiApp = () => {
    setIsChat(false)
  };

  const Conent = () => {
    let conentComp = (null);
    if (activeTab == 1) {
      conentComp = <BaseInfo />
    } else if (activeTab == 2) {
      conentComp = <AiWorkflow />
    }
    return <>{conentComp}</>;
  };

  return (
    <div className={styles.wrap}>
      <div className={styles.sideWrap}>
        <div className={`side panel ${isOpen ? 'open' : 'closed'}`}>
          <Header style={{ padding: 0, background: colorBgContainer }} >
            <div className={styles.subTop}>GPTs应用</div>
          </Header>
          <Content>
            <div
              className={styles.content}
              style={{
                paddingLeft: 14,
                paddingRight: 14,
                minHeight: 360,
                borderRadius: borderRadiusLG,
              }}
            >
              <div className={styles.conentNav}>
                <div className={styles.conentTabs}>
                  <p className={styles.conentTabsItemA}>自建</p>
                  <p className={styles.conentTabsItem}>收藏</p>
                </div>
                <div>
                  <Button className={styles.conentTabsItemAdd} icon={<PlusOutlined />}
                  onClick={addAiApp}>
                    新增
                  </Button>
                </div>
              </div>

              <Search placeholder="input search text" onSearch={onSearch} style={{ width: 280, marginBottom: "15px" }} />

              <CheckCard.Group style={{ width: '100%' }}>
                  <CheckCard
                    title="Spring Boot"
                    style={{ width: 280}}
                    avatar={
                      <Avatar
                        src="https://gw.alipayobjects.com/zos/bmw-prod/2dd637c7-5f50-4d89-a819-33b3d6da73b6.svg"
                        size="large"
                      />
                    }
                    extra={
                      <Dropdown
                        placement="topCenter"
                        menu={{
                          onClick: ({ domEvent }) => {
                            domEvent.stopPropagation();
                            message.info('menu click');
                          },
                          items: [
                            {
                              label: '删除',
                              key: '1',
                            },
                            {
                              label: '编辑',
                              key: '2',
                            }
                          ],
                        }}
                      >
                        <EllipsisOutlined
                          style={{ fontSize: 22, color: 'rgba(0,0,0,0.5)' }}
                          onClick={(e) => e.stopPropagation()}
                        />
                      </Dropdown>
                    }
                    description="通过业界流行的技术栈来快速构建 Java 后端应用"
                    value="SpringBoot"
                    onChange={() => {
                      setIsChat(true)
                    }}
                  />
                  <CheckCard
                    title="SOFA Boot"
                    style={{ width: 280}}
                    avatar={
                      <Avatar
                        src="https://gw.alipayobjects.com/zos/bmw-prod/6935b98e-96f6-464f-9d4f-215b917c6548.svg"
                        size="large"
                      />
                    }
                    description="使用 SOFAStack 中间件来快速构建分布式后端应用"
                    value="SOFABoot"
                    onChange={() => {
                      setIsChat(true)
                    }}
                  />
                  <CheckCard
                    title="Node JS"
                    style={{ width: 280}}
                    avatar={
                      <Avatar
                        src="https://gw.alipayobjects.com/zos/bmw-prod/d12c3392-61fa-489e-a82c-71de0f888a8e.svg"
                        size="large"
                      />
                    }
                    description="使用前后端统一的语言方案快速构建后端应用"
                    value="NodeJS"
                    onChange={() => {
                      setIsChat(true)
                    }}
                  />
              </CheckCard.Group>

            </div>
          </Content>
        </div>
        <Button type="text" icon={<MenuFoldOutlined />} className={styles.flowCollapsedButton}
          onClick={() => setIsOpen(!isOpen)} ></Button>
      </div>
      {isChat ? (
        <Layout>
          <Header style={{ padding: 0, background: colorBgContainer }} >
            <div className={styles.nav}>
              <p onClick={() => handleTabClick(1)} className={activeTab === 1 ? 'navItemA' : 'navItem'}>对话</p>
            </div>
          </Header>
          <Content className={styles.content}>
            <Card style={{ margin: "0 10px" }}>
              <div style={{ background: chatTheme.colorBgLayout }}>
                <ProChat
                  style={{ minHeight: "80vh" }}
                  showTitle
                  userMeta={{
                    avatar: 'https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg',
                    title: 'Ant Design',
                  }}
                  assistantMeta={{ avatar: '🛸', title: '三体世界', backgroundColor: '#67dedd' }}
                  initialChats={chats.chats}
                />
              </div>
            </Card>
          </Content>
          <Footer style={{ textAlign: 'center' }}>
            vuca ©{new Date().getFullYear()} Created by s11eDao
          </Footer>
        </Layout>
      ) : (
        <Layout>
        <Header style={{ padding: 0, background: colorBgContainer }} >
          <div className={styles.flowNav}>
            <p onClick={() => handleTabClick(1)} className={activeTab === 1 ? 'navItemA' : 'navItem'}>基础信息</p>
            <p onClick={() => handleTabClick(2)} className={activeTab === 2 ? 'navItemA' : 'navItem'}>任务编排</p>
          </div>
        </Header>
        <Content className={styles.content}>
          <div
            className={styles.content}
            style={{
              marginLeft: 6,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <Conent />
          </div>
        </Content>
        <Footer style={{ textAlign: 'center' }}>
          vuca ©{new Date().getFullYear()} Created by s11eDao
        </Footer>
      </Layout>
      )}
      
    </div>
  );
};

export default App;
