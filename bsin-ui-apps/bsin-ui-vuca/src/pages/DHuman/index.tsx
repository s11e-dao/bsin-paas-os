import React, { useEffect, useState, useRef } from 'react';
import { UserOutlined, EllipsisOutlined, VideoCameraOutlined, UploadOutlined } from '@ant-design/icons';
import {
  Layout,
  Typography,
  theme,
  Col,
  Row,
  List,
  Avatar,
  message,
  Input,
  Button,
  Dropdown,
  Card,
  UploadProps,
  Form,
} from "antd";
import { runes } from 'runes2';
import { ProCard, CheckCard } from '@ant-design/pro-components';
import RcResizeObserver from 'rc-resize-observer';
import { ProChat } from '@ant-design/pro-chat';
import { useTheme } from 'antd-style';
import VirtualList from 'rc-virtual-list';

import { chats } from './mocks/threebody';
const { Header, Content, Footer, Sider } = Layout;

import {
  PlusOutlined,
  LoadingOutlined,
  MinusCircleOutlined,
} from "@ant-design/icons";
import { Modal, Upload } from 'antd';

import shenfen from "@/assets/image/fenshen.png"

import styles from "./index.less"
import "./home.less"
import InfoCard from '@/components/InfoCard';
import { textAlign } from '@mui/system';
type FileType = Parameters<GetProp<UploadProps, "beforeUpload">>[0];

import avatarImg from "@/assets/image/vuca.jpg"

const { TextArea } = Input;

const items = [UserOutlined, VideoCameraOutlined, UploadOutlined, UserOutlined].map(
  (icon, index) => ({
    key: String(index + 1),
    icon: React.createElement(icon),
    label: `nav ${index + 1}`,
  }),
);
// 知识库列表，需要接口提供提供
const knowledg_base_options = [
  {
    title: "他擅长的领域",
    value: "1",
    description: "不仅有写诗的热情，还有写诗的才华",
    url: "https://www.baidu.com",
  },
  {
    title: "他的故事",
    value: "2",
    description: "一杯咖啡一壶酒，故事无限有",
    url: "https://procomponents.ant.design/components/check-card",
  },
  {
    title: "他的观点",
    value: "3",
    description: "听君一席话，胜读十年书",
    url: "https://procomponents.ant.design/components/check-card",
  },
  {
    title: "他的数据资产",
    value: "4",
    description: "自我掌控数据主权",
    url: "https://procomponents.ant.design/components/check-card",
  },
  {
    title: "他的社交圈",
    value: "5",
    description: "链接，链接，链接无限可能",
    url: "https://procomponents.ant.design/components/check-card",
  },
  {
    title: "他的合作伙伴",
    value: "6",
    description: "协作创新，创造无限可能",
    url: "https://procomponents.ant.design/components/check-card",
  },
  {
    title: "朋友印象",
    value: "7",
    description: "协作创新，创造无限可能",
    url: "https://procomponents.ant.design/components/check-card",
  },
];
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


const App: React.FC = () => {

  const chatTheme = useTheme();

  const [isChat, setIsChat] = useState<boolean>(false);

  const [responsive, setResponsive] = useState(false);

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

  // 使用状态来追踪当前活动的Tab
  const [activeTab, setActiveTab] = useState(1);

  // 切换Tab的事件处理函数
  const handleTabClick = (tabNumber: any) => {
    setActiveTab(tabNumber);
    if (tabNumber === 2) {
      setIsChat(true)
    }else{
      setIsChat(false)
    }
  };

  const onScroll = (e: React.UIEvent<HTMLElement, UIEvent>) => {
    if (e.currentTarget.scrollHeight - e.currentTarget.scrollTop === ContainerHeight) {
      appendData();
    }
  };

  const enterLoading = (index: number) => {

  }

  const getBase64 = (img: FileType, callback: (url: string) => void) => {
    const reader = new FileReader();
    reader.addEventListener("load", () => callback(reader.result as string));
    reader.readAsDataURL(img);
  };
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const [selfInfo, setSelfInfo] = useState({
    name: "数字分身", //名称
    desc: "s11eDao的守护神和AI助理", //介绍
    vChat: "", //微信号
    phone: "", //电话
    email: "", //邮箱
    backgroundUrl:
      "https://ebany-front-resource.oss-cn-beijing.aliyuncs.com/cache/ai.png", //背景图
    character: avatarImg, //人物形象图
    knowledgeList: ['1'],
  });
  console.log("selfInfo", selfInfo);
  const [loading, setLoading] = useState(false);


  const beforeUpload = (file: FileType) => {
    const isJpgOrPng = file.type === "image/jpeg" || file.type === "image/png";
    if (!isJpgOrPng) {
      message.error("You can only upload JPG/PNG file!");
    }
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
      message.error("Image must smaller than 2MB!");
    }
    return isJpgOrPng && isLt2M;
  };
  const handleChange: UploadProps['onChange'] = (info) => {
    if (info.file.status === 'uploading') {
      setLoading(true);
      return;
    }
    if (info.file.status === 'done') {
      // Get this url from response in real world.
      getBase64(info.file.originFileObj as FileType, (url) => {
        setLoading(false);
        setSelfInfo({ ...selfInfo, character: url })
      });
    }
  };
  const backgroundhandleChange: UploadProps["onChange"] = (info) => {
    if (info.file.status === "uploading") {
      setLoading(true);
      return;
    }
    if (info.file.status === "done") {
      // Get this url from response in real world.
      getBase64(info.file.originFileObj as FileType, (url) => {
        setLoading(false);
        setSelfInfo({ ...selfInfo, backgroundUrl: url });
      });
    }
  };
  const uploadButton = (
    <button style={{ border: 0, background: "none" }} type="button">
      {loading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>Upload</div>
    </button>
  );
  const onFinish = (values: any) => {
    console.log("Received values of form:", values);
  };
  // 保存分身按钮触发事件
  const saveSeparation = () => {
    console.log("saveSeparation", selfInfo);
  }

  return (
    <div className={styles.wrap}>
      <div className={styles.side}>
        <Header style={{ padding: 0, background: colorBgContainer }}>
          <div className={styles.subTop}></div>
        </Header>
        <Content className={styles.leftContent}>
          <div
            className={styles.leftContent}
            style={{
              minHeight: 360,
              borderRadius: borderRadiusLG,
            }}
          >
            <div style={{ textAlign: "center" }}>
              <Button
                type="primary"
                style={{ marginTop: 30 }}
                onClick={saveSeparation}
              >
                保存分身
              </Button>
            </div>

            <InfoCard info={selfInfo} options={knowledg_base_options} />
          </div>
        </Content>
      </div>

      <Layout>
        <Header style={{ padding: 0, background: colorBgContainer }}>
          <div className={styles.nav}>
            <p
              onClick={() => handleTabClick(1)}
              className={activeTab === 1 ? "navItemA" : "navItem"}
            >
              分身设置
            </p>
            <p
              onClick={() => handleTabClick(2)}
              className={activeTab === 2 ? "navItemA" : "navItem"}
            >
              chat测试
            </p>
          </div>
        </Header>

        <Content className={styles.content}>

          {!isChat ? (
            <div style={{ margin: "0 20px" }}>
              <RcResizeObserver
                key="resize-observer"
                onResize={(offset) => {
                  setResponsive(offset.width < 596);
                }}
              >
                <ProCard
                  title="基础信息"
                  split={responsive ? "horizontal" : "vertical"}
                  style={{ marginTop: "15px" }}
                >
                  <ProCard colSpan="20%" title="AI智能">
                    <div style={{ height: 180 }}>
                      <Upload
                        name="avatar"
                        listType="picture-card"
                        className="avatar-uploader"
                        showUploadList={false}
                        action="https://run.mocky.io/v3/435e224c-44fb-4773-9faf-380c5e6a2188"
                        beforeUpload={beforeUpload}
                        onChange={handleChange}
                      >
                        {selfInfo.character ? (
                          <img
                            src={selfInfo.character}
                            alt="avatar"
                            style={{ width: "100%" }}
                          />
                        ) : (
                          uploadButton
                        )}
                      </Upload>
                    </div>
                  </ProCard>
                  <ProCard colSpan="20%" title="背景图">
                    <div style={{ height: 180 }}>
                      <Upload
                        name="avatar"
                        listType="picture-card"
                        className="avatar-uploader"
                        showUploadList={false}
                        action="https://run.mocky.io/v3/435e224c-44fb-4773-9faf-380c5e6a2188"
                        beforeUpload={beforeUpload}
                        onChange={backgroundhandleChange}
                      >
                        {selfInfo.backgroundUrl ? (
                          <img
                            src={selfInfo.backgroundUrl}
                            alt="avatar"
                            style={{ width: "100%" }}
                          />
                        ) : (
                          uploadButton
                        )}
                      </Upload>
                    </div>
                  </ProCard>
                  <ProCard>
                    <div style={{ height: 70 }}>
                      <Typography.Title level={5}>名称</Typography.Title>
                      <Input
                        count={{
                          show: true,
                          max: 6,
                          strategy: (txt) => runes(txt).length,
                          exceedFormatter: (txt, { max }) =>
                            runes(txt).slice(0, max).join(""),
                        }}
                        value={selfInfo.name}
                        onChange={(e) =>
                          setSelfInfo({ ...selfInfo, name: e.target.value })
                        }
                      />
                    </div>
                    <div style={{ height: 70 }}>
                      <Typography.Title level={5}>介绍</Typography.Title>
                      <TextArea
                        count={{
                          show: true,
                          max: 20,
                          strategy: (txt) => runes(txt).length,
                          exceedFormatter: (txt, { max }) =>
                            runes(txt).slice(0, max).join(""),
                        }}
                        value={selfInfo.desc}
                        onChange={(e) =>
                          setSelfInfo({ ...selfInfo, desc: e.target.value })
                        }
                      />
                    </div>
                  </ProCard>
                </ProCard>
              </RcResizeObserver>

              <RcResizeObserver
                key="resize-observer"
                onResize={(offset) => {
                  setResponsive(offset.width < 596);
                }}
              >
                <ProCard
                  title="联系方式"
                  split={responsive ? "horizontal" : "vertical"}
                  gutter={8}
                  style={{ marginTop: "15px" }}
                >
                  <Row style={{ margin: "20px 20px" }} gutter={24}>
                    <Col className="gutter-row" span={8}>
                      <Card>
                        微信
                        <Input
                          value={selfInfo.vChat}
                          onChange={(e) =>
                            setSelfInfo({ ...selfInfo, vChat: e.target.value })
                          }
                        />
                      </Card>
                    </Col>
                    <Col className="gutter-row" span={8}>
                      <Card>
                        手机号
                        <Input
                          value={selfInfo.phone}
                          onChange={(e) =>
                            setSelfInfo({ ...selfInfo, phone: e.target.value })
                          }
                        />
                      </Card>
                    </Col>
                    <Col className="gutter-row" span={8}>
                      <Card>
                        邮箱
                        <Input
                          value={selfInfo.email}
                          onChange={(e) =>
                            setSelfInfo({ ...selfInfo, email: e.target.value })
                          }
                        />
                      </Card>
                    </Col>
                  </Row>
                </ProCard>
              </RcResizeObserver>

              <RcResizeObserver
                key="resize-observer"
                onResize={(offset) => {
                  setResponsive(offset.width < 596);
                }}
              >
                <ProCard
                  title="分身知识库"
                  split={responsive ? "horizontal" : "vertical"}
                  gutter={8}
                  style={{ marginTop: "15px", width: "100%" }}
                >

                  <CheckCard.Group
                    multiple
                    style={{ padding: 20, width: "100%" }}
                    onChange={(value) => {
                      setSelfInfo({ ...selfInfo, knowledgeList: value });
                      console.log("value", value);
                    }}
                    defaultValue="1"
                  >
                    <CheckCard
                      style={{ width: "100%" }}
                      title="他擅长的领域"
                      description="不仅有写诗的热情，还有写诗的才华"
                      value="1"
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
                    />

                    <CheckCard
                      style={{ width: "100%" }}
                      title="他擅长的领域"
                      description="不仅有写诗的热情，还有写诗的才华"
                      value="2"
                    />

                    <CheckCard
                      style={{ width: "100%" }}
                      title="他擅长的领域"
                      description="不仅有写诗的热情，还有写诗的才华"
                      value="3"
                    />

                  </CheckCard.Group>

                  <CheckCard.Group
                    multiple
                    onChange={(value) => {
                      setSelfInfo({ ...selfInfo, knowledgeList: value });
                      console.log("value", value);
                    }}
                    value={selfInfo.knowledgeList}
                    style={{ padding: 20, width: "100%" }}
                    options={knowledg_base_options}
                    className={styles.CheckCard_Group}
                  ></CheckCard.Group>
                </ProCard>
              </RcResizeObserver>
            </div>
          ) : (
            <Card style={{ margin: "10px" }}>
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
          )}

        </Content>
        <Footer style={{ textAlign: "center" }}>
          vuca ©{new Date().getFullYear()} Created by s11eDao
        </Footer>
      </Layout>

    </div>
  );
};

export default App;
