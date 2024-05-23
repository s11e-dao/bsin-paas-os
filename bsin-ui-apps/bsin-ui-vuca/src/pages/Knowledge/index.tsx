import React, { useEffect, useState, useRef } from 'react';
import { PlusOutlined, EllipsisOutlined, VideoCameraOutlined } from '@ant-design/icons';
import { Layout, Button, theme, Avatar, Dropdown, message, Space, Input, Card } from 'antd';

import { useTheme } from 'antd-style';

import {
  ProFormRadio,
  CheckCard,
  ProList,
} from '@ant-design/pro-components';
import { Progress, Tag } from 'antd';

const { Header, Content, Footer, Sider } = Layout;

import BaseInfo from './BaseInfo';
import Document from './Document';
import RagTest from './RagTest';

import styles from "./index.less"

import "./home.less"

const { Search } = Input;


const App: React.FC = () => {

  const chatTheme = useTheme();

  const [ghost, setGhost] = useState<boolean>(true);

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const onSearch = (value, _e, info) => console.log(info?.source, value);

  // 使用状态来追踪当前活动的Tab
  const [activeTab, setActiveTab] = useState(1);

  // 切换Tab的事件处理函数
  const handleTabClick = (tabNumber: any) => {
    setActiveTab(tabNumber);
  };

  // 新增知识库
  const addKnowledgeBase = () => {
    console.log("点击新增知识库")
  };

  const Conent = () => {
    let conentComp = (null);
    if (activeTab == 1) {
      conentComp = <BaseInfo />
    } else if (activeTab == 2) {
      conentComp = <Document />
    } else if (activeTab == 3) {
      conentComp = <RagTest /> 
    }
    return <>{conentComp}</>;
  };

  return (
    <div className={styles.wrap}>
      <div className={styles.side}>
        <Header style={{ padding: 0, background: colorBgContainer }} >
          <div className={styles.subTop}>知识库</div>
        </Header>
        <Content className={styles.leftContent}>
          <div
            className={styles.leftContent}
            style={{
              paddingLeft: 14,
              paddingRight: 14,
              minHeight: 360,
              borderRadius: borderRadiusLG,
            }}
          >
            <div className={styles.conentNav}>
              <div className={styles.conentTabs}>
                <p className={styles.conentTabsItemA}>常用</p>
                <p className={styles.conentTabsItem}>分身</p>
              </div>
              <div>
                <Button className={styles.conentTabsItemAdd} icon={<PlusOutlined />}
                  onClick={addKnowledgeBase}>
                  新增
                </Button>
              </div>
            </div>

            <Search placeholder="input search text" onSearch={onSearch} style={{ width: 320, marginBottom: "15px" }} />

            <CheckCard.Group style={{ width: '100%' }}>
                <CheckCard
                  title="Spring Boot"
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
                  onChange={()=>{

                  }}
                />
                <CheckCard
                  title="SOFA Boot"
                  avatar={
                    <Avatar
                      src="https://gw.alipayobjects.com/zos/bmw-prod/6935b98e-96f6-464f-9d4f-215b917c6548.svg"
                      size="large"
                    />
                  }
                  description="使用 SOFAStack 中间件来快速构建分布式后端应用"
                  value="SOFABoot"
                  onChange={()=>{

                  }}
                />
                <CheckCard
                  title="Node JS"
                  avatar={
                    <Avatar
                      src="https://gw.alipayobjects.com/zos/bmw-prod/d12c3392-61fa-489e-a82c-71de0f888a8e.svg"
                      size="large"
                    />
                  }
                  description="使用前后端统一的语言方案快速构建后端应用"
                  value="NodeJS"
                  onChange={()=>{

                  }}
                />
            </CheckCard.Group>
          </div>
        </Content>
      </div>
      <Layout>
          <Header style={{ padding: 0, background: colorBgContainer }} >
            <div className={styles.knav}>
              <p onClick={() => handleTabClick(1)} className={activeTab === 1 ? 'navItemA' : 'navItem'}>基础信息</p>
              <p onClick={() => handleTabClick(2)} className={activeTab === 2 ? 'navItemA' : 'navItem'}>文档</p>
              <p onClick={() => handleTabClick(3)} className={activeTab === 3 ? 'navItemA' : 'navItem'}>命中测试</p>
            </div>
          </Header>
          <Content className={styles.content}>
            
            <Card style={{ margin: "10px" }}>
              <Conent />
              <div style={{ background: chatTheme.colorBgLayout }}>
                知识库设置
              </div>
            </Card>
          </Content>
          <Footer style={{ textAlign: 'center' }}>
            vuca ©{new Date().getFullYear()} Created by s11eDao
          </Footer>
        </Layout>

    </div>
  );
};

export default App;
