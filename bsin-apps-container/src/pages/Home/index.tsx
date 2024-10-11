import React, { FC } from 'react';
import {
  Row,
  Col,
  Card,
  Avatar,
  Pagination,
  Calendar,
  List,
  message,
} from 'antd';
import { Link, history } from 'umi';
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from "@/utils/localStorageInfo";
import {
  ScheduleTwoTone,
  AppstoreTwoTone,
  ProfileTwoTone,
} from '@ant-design/icons';
import moment from 'moment';
import styles from './index.less';
import logo2 from '@/assets/logo3.png';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');

import { getAppListByUserId } from '@/services/getAppByUserId'

export default function () {
  // 列表数据
  const data = [
    'bsin-paas新增智能决策引擎模块',
    'bsin-paas新增通用人工智能模块，集成chatGPT',
    'bsin-paas是企业数字化的技术中台解决方案.',
    's11eDao开启大社区、小平台战略',
  ];

  const [current, setCurrent] = React.useState(1);

  const [appList, setAppList] = React.useState([]);


  React.useEffect(() => {

    getAppListByUserId({}).then((res) => {

      if (res.code === 0) {
        let apps = res.data?.apps.map((item: any) => {
          return {
            appId: item.appId,
            appName: item.appName,
            path: item.appCode,
            logo: item.logo,
            remark: item.remark,
          }
        })
        setAppList(apps)
      }
    })
  }, []);

  // const dataJson = {};
  // add({...dataJson});

  /**
   * 点击跳转到子应用
   * @param app
   */
  const getMenu = async (app: any) => {
    // debugger
    history.push('/' + app.appCode);
  };

  const style = { padding: '16px 0', height: '100%' };

  return (
    <div className="work-place" style={{ marginTop: 2 }}>
      <Row
        align="middle"
        style={{
          backgroundColor: '#fff',
          marginBottom: 12,
          padding: '0 20px 0 25px',
        }}
      >
        <Col xl={16} lg={12} md={24} sm={24} xs={24}>
          <List itemLayout="horizontal">
            <List.Item>
              <List.Item.Meta
                avatar={
                  <Avatar
                    size={{ xs: 60, sm: 60, md: 60, lg: 60, xl: 60, xxl: 60 }}
                    src={logo2}
                  />
                }
                title={
                  <div style={{ fontSize: 24 }}>
                    你好，{getLocalStorageInfo('userInfo')?.username || getLocalStorageInfo('merchantInfo')?.username || getLocalStorageInfo('sysAgentInfo')?.username}
                  </div>
                }
                description={`欢迎使用${process.env.title}`}
              />
            </List.Item>
          </List>
        </Col>
        <Col xl={8} lg={12} md={24} sm={24} xs={24}>
          <Row align="middle" style={{ textAlign: 'center' }}>
            <Col className="gutter-row" xl={6} lg={6} md={6} sm={12} xs={24}>
              <a
                style={{ color: "#1677ff" }}
                onClick={() => {
                  // setQiankunGlobalState(4);
                  // console.log(globalState);
                }}
              >
                <Row gutter={[5, 8]} style={style}>
                  <Col className="gutter-row" span={24}>
                    <ProfileTwoTone
                      style={{ fontSize: 40 }}
                      twoToneColor={"#1677ff"}
                    />
                  </Col>
                  <Col className="gutter-row" span={24}>
                    待办事项
                  </Col>
                </Row>
              </a>
            </Col>
            <Col className="gutter-row" xl={6} lg={6} md={6} sm={12} xs={24}>
              <Row gutter={[5, 8]} style={style}>
                <Col className="gutter-row" span={24}>
                  <ScheduleTwoTone
                    style={{ fontSize: 40 }}
                    twoToneColor={"#1677ff"}
                  />
                </Col>
                <Col className="gutter-row" span={24}>
                  日程
                </Col>
              </Row>
            </Col>
            <Col className="gutter-row" xl={6} lg={6} md={6} sm={12} xs={24}>
              <Row gutter={[5, 8]} style={style}>
                <Col className="gutter-row" span={24}>
                  <AppstoreTwoTone
                    style={{ fontSize: 40 }}
                    twoToneColor={"#1677ff"}
                  />
                </Col>
                <Col className="gutter-row" span={24}>
                  打开多个应用
                </Col>
              </Row>
            </Col>
            <Col className="gutter-row" xl={6} lg={6} md={6} sm={12} xs={24}>
              <Row style={style}>
                <Col className="gutter-row" span={24}>
                  拥有子应用数
                </Col>
                <Col style={{ fontSize: 40 }} className="gutter-row" span={24}>
                  {appList?.length}
                </Col>
              </Row>
            </Col>
          </Row>
        </Col>
      </Row>
      <Row gutter={12} style={{}}>
        <Col xl={16} lg={24} md={24} sm={24} xs={24}>
          <Card
            style={{ minHeight: 400, padding: '0 0 60px', marginBottom: 12 }}
            className={styles.projectList}
            title="应用快捷访问入口"
            bordered={false}
            // extra={<Link to="/">全部应用</Link>}
            bodyStyle={{ padding: 0 }}
          >
            {appList?.map((item) => (
              <Card.Grid className={styles.projectGrid} key={item.appId}>
                <Card
                  bodyStyle={{
                    padding: 0,
                    height: 100,
                  }}
                  bordered={false}
                >
                  <Card.Meta
                    title={
                      <div
                        className={styles.cardTitle}
                      // onClick={() => getMenu(item)}
                      >
                        <Avatar size="small" src={logo2} />
                        <a>{item.appName}</a>
                      </div>
                    }
                    description={item.remark || ' '}
                  />
                  <div className={styles.projectItemContent}>
                    <a>{item.member || ''}</a>
                    {item.updateTime && (
                      <span className={styles.datetime} title={item.updateTime}>
                        {moment(item.updateTime).fromNow()}
                      </span>
                    )}
                  </div>
                </Card>
              </Card.Grid>
            ))}
          </Card>
          <Row gutter={12}>
            <Col xl={12} lg={12} md={24} sm={24} xs={24}>
              <Card
                style={{ marginBottom: 12 }}
                bodyStyle={{ padding: 10 }}
                bordered={false}
                title="新闻动态"
                extra={<Link to="/">更多</Link>}
              >
                <List
                  size="small"
                  dataSource={data}
                  renderItem={(item, index) => (
                    <List.Item key={index}>
                      <List.Item.Meta
                        title={<a href="https://ant.design">{item}</a>}
                      />
                      <div>2021-11-26</div>
                    </List.Item>
                  )}
                />
              </Card>
            </Col>
            <Col xl={12} lg={12} md={24} sm={24} xs={24}>
              <Card
                style={{ marginBottom: 12 }}
                bodyStyle={{ padding: 10 }}
                bordered={false}
                title="我的待办"
                extra={<Link to="/">更多</Link>}
              >
                <List
                  size="small"
                  dataSource={data}
                  renderItem={(item, index) => (
                    <List.Item key={index}>
                      <List.Item.Meta
                        title={<a href="https://ant.design">{item}</a>}
                      />
                      <div>2021-11-26</div>
                    </List.Item>
                  )}
                />
              </Card>
            </Col>
          </Row>
        </Col>
        <Col xl={8} lg={24} md={24} sm={24} xs={24}>
          <Row gutter={12}>
            <Col xl={24} lg={12} md={24} sm={24} xs={24}>
              <Card
                style={{ marginBottom: 12 }}
                bordered={false}
                bodyStyle={{ padding: 0 }}
                title="Bsin-PaaS"
              >
                <p style={{ paddingLeft: 25, paddingRight: 25, paddingTop: 25 }}>Bsin-PaaS（毕昇） 是一套企业级的低代码、零代码去中心化应用搭建平台，
                  可帮助企业快速搭建有竞争力的业务中台、流程中台、规则中台、通用人工智能中台、业务前台。</p>
                <p style={{ paddingLeft: 25, paddingRight: 25, paddingBottom: 25 }}>开源地址：<a href='https://gitee.com/s11e-DAO/bsin-paas-all-in-one'>https://gitee.com/s11e-DAO/bsin-paas-all-in-one</a></p>

              </Card>
            </Col>
            <Col xl={24} lg={12} md={24} sm={24} xs={24}>
              <Card
                bordered={false}
                title="公告"
                extra={<Link to="/">更多</Link>}
              >
                <List
                  size="small"
                  dataSource={data}
                  renderItem={(item, index) => (
                    <List.Item key={index}>
                      <List.Item.Meta
                        title={<a href="https://ant.design">{item}</a>}
                      />
                      <div>2021-11-26</div>
                    </List.Item>
                  )}
                />
              </Card>
            </Col>
          </Row>
        </Col>
      </Row>
    </div>
  );
};
