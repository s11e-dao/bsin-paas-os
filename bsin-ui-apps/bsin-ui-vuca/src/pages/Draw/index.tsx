import React, { useEffect, useState, useRef } from 'react';
import { UploadOutlined, UserOutlined, VideoCameraOutlined } from '@ant-design/icons';
import { Layout, Menu, theme, Avatar, List, message, Input, Card } from 'antd';
import VirtualList from 'rc-virtual-list';
import { SocketServer } from '../../utils/socket';

const { Header, Content, Footer, Sider } = Layout;

import { ProChat } from '@ant-design/pro-chat';

import { useTheme } from 'antd-style';

import { MockResponse } from './mocks/streamResponse';

import "./global.less"
import styles from "./index.less"

const { Search } = Input;

const items = [UserOutlined, VideoCameraOutlined, UploadOutlined, UserOutlined].map(
  (icon, index) => ({
    key: String(index + 1),
    icon: React.createElement(icon),
    label: `nav ${index + 1}`,
  }),
);

const data = [
  {
    title: 'Ant Design Title 1',
  },
  {
    title: 'Ant Design Title 2',
  },
  {
    title: 'Ant Design Title 3',
  },
  {
    title: 'Ant Design Title 4',
  },
];

interface UserItem {
  email: string;
  gender: string;
  name: {
    first: string;
    last: string;
    title: string;
  };
  nat: string;
  picture: {
    large: string;
    medium: string;
    thumbnail: string;
  };
}

const fakeDataUrl =
  'https://randomuser.me/api/?results=20&inc=name,gender,email,nat,picture&noinfo';
const ContainerHeight = "90vh";

const initialChats = new Array(10)
  .fill(0)
  .map((_, index) => {
    return {
      id: 'chat-' + index,
      content: '这是一段模拟的流式字符串数据。' + index,
      role: index % 2 === 1 ? 'user' : 'assistant',
      updateAt: Date.now(),
      createAt: Date.now(),
    };
  })
  .reduce(
    (acc, cur) => {
      acc[cur.id] = cur;
      return acc;
    },
    {} as Record<string, any>,
  );


const App: React.FC = () => {

  const chatTheme = useTheme();

  const [data, setData] = useState<UserItem[]>([]);

  const appendData = () => {
    fetch(fakeDataUrl)
      .then((res) => res.json())
      .then((body) => {
        setData(data.concat(body.results));
        message.success(`${body.results.length} more items loaded!`);
      });
  };

  useEffect(() => {
    appendData();
  }, []);

  const onScroll = (e: React.UIEvent<HTMLElement, UIEvent>) => {
    if (e.currentTarget.scrollHeight - e.currentTarget.scrollTop === ContainerHeight) {
      appendData();
    }
  };

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

  return (
    <div className={styles.wrap}>
      <div className={styles.side}>
        <Header style={{ padding: 0, background: colorBgContainer }} >
          <div className={styles.subTop}>描绘美好</div>
        </Header>
        <Content className={styles.leftContent}>
          <div
            className={styles.leftContent}
            style={{
              paddingLeft: 24,
              minHeight: 360,
              borderRadius: borderRadiusLG,
            }}
          >
            <div className={styles.conentTabs}>
              <p className={styles.conentTabsItemA}>全部</p>
              <p className={styles.conentTabsItem}>推荐</p>
              <p className={styles.conentTabsItem}>收藏</p>
            </div>

            <Search placeholder="input search text" onSearch={onSearch} style={{ width: 240 }} />

            <List>
              <VirtualList
                data={data}
                height={ContainerHeight}
                itemHeight={47}
                itemKey="email"
                onScroll={onScroll}
              >
                {(item: UserItem) => (
                  <List.Item key={item.email}>
                    <List.Item.Meta
                      avatar={<Avatar src={item.picture.large} />}
                      title={<a href="https://ant.design">{item.name.last}</a>}
                      description={item.email}
                    />
                  </List.Item>
                )}
              </VirtualList>
            </List>

          </div>
        </Content>
      </div>
      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer }} >
          <div className={styles.nav}>
            <p onClick={() => handleTabClick(1)} className={activeTab === 1 ? 'navItemA' : 'navItem'}>绘画</p>
            <p onClick={() => handleTabClick(2)} className={activeTab === 2 ? 'navItemA' : 'navItem'}>视频</p>
          </div>
        </Header>
        <Content className={styles.content}>
          <Card>
            <div style={{ background: chatTheme.colorBgLayout }}>
              <ProChat
                style={{ height: "82vh" }}
                initialChats={initialChats}
                chatItemRenderConfig={{
                  titleRender: (item, dom) => {
                    if (item.placement === 'right') return dom;
                    return (
                      <div
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          padding: '8px 0',
                          gap: 8,
                        }}
                      >
                        <span
                          style={{
                            textAlign: 'center',
                            display: 'inline-flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            height: 24,
                            padding: 4,
                            borderRadius: chatTheme.borderRadius,
                            border: '1px solid ' + chatTheme.colorSplit,
                            backgroundColor: chatTheme.colorPrimaryBg,
                          }}
                        >
                          🦾 使用 mock 生成
                        </span>
                      </div>
                    );
                  },
                  actionsRender: false,
                }}
                request={async (messages) => {
                  const mockedData: string = `这是一段模拟的流式字符串数据。本次会话传入了${messages.length}条消息`;

                  const mockResponse = new MockResponse(mockedData, 50);

                  return mockResponse.getResponse();
                }}
              />
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
