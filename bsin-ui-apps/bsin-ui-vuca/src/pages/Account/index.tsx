import React, { useState, useRef, useEffect } from 'react';
import { PlusOutlined, HomeOutlined, ContactsOutlined, ClusterOutlined } from '@ant-design/icons';
import { Modal, Card, Col, Input, Row, Tag, Layout, Button } from 'antd';
import type { RouteChildrenProps } from 'react-router';
import { StatisticCard } from '@ant-design/pro-components';
import RcResizeObserver from 'rc-resize-observer';
import { Chart } from '@antv/g2';
import confluxFluent from "@/utils/confluxFluent"

const { Footer } = Layout;

import type { CurrentUser, TagType, tabKeyType } from './data.d';
import styles from './Center.less';

const operationTabList = [
  {
    key: 'articles',
    tab: (
      <span>
        收益 <span style={{ fontSize: 14 }}></span>
      </span>
    ),
  },
  {
    key: 'applications',
    tab: (
      <span>
        任务 <span style={{ fontSize: 14 }}>(8)</span>
      </span>
    ),
  }
];

const TagList: React.FC<{ tags: CurrentUser['tags'] }> = ({ tags }) => {
  const ref = useRef<Input | null>(null);
  const [newTags, setNewTags] = useState<TagType[]>([]);
  const [inputVisible, setInputVisible] = useState<boolean>(false);
  const [inputValue, setInputValue] = useState<string>('');

  const showInput = () => {
    setInputVisible(true);
    if (ref.current) {
      // eslint-disable-next-line no-unused-expressions
      ref.current?.focus();
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const handleInputConfirm = () => {
    let tempsTags = [...newTags];
    if (inputValue && tempsTags.filter((tag) => tag.label === inputValue).length === 0) {
      tempsTags = [...tempsTags, { key: `new-${tempsTags.length}`, label: inputValue }];
    }
    setNewTags(tempsTags);
    setInputVisible(false);
    setInputValue('');
  };

  return (
    <div className={styles.tags}>
      <div className={styles.tagsTitle}>标签</div>
      {(tags || []).concat(newTags).map((item) => (
        <Tag key={item.key}>{item.label}</Tag>
      ))}
      {inputVisible && (
        <Input
          ref={ref}
          type="text"
          size="small"
          style={{ width: 78 }}
          value={inputValue}
          onChange={handleInputChange}
          onBlur={handleInputConfirm}
          onPressEnter={handleInputConfirm}
        />
      )}
      {!inputVisible && (
        <Tag onClick={showInput} style={{ borderStyle: 'dashed' }}>
          <PlusOutlined />
        </Tag>
      )}
    </div>
  );
};

const { Statistic, Divider } = StatisticCard;

const Center: React.FC<RouteChildrenProps> = () => {

  const [responsive, setResponsive] = useState(false);

  const [walletAddress, setWalletAddress] = useState("");

  const [tabKey, setTabKey] = useState<tabKeyType>('articles');

  //  获取用户信息
  const currentUser = {
    name: 'Serati Ma',
    avatar: 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png',
    userid: '00000001',
    email: 'antdesign@alipay.com',
    signature: '博羸',
    title: '区块链专家',
    group: 's11eDao－社区核心建设者',
    tags: [
      {
        key: '0',
        label: '有想法',
      },
      {
        key: '0',
        label: '很有想法',
      },
      {
        key: '0',
        label: '很有想法',
      }, {
        key: '0',
        label: '很有想法',
      }, {
        key: '0',
        label: '很有想法',
      }, {
        key: '0',
        label: '很有想法',
      }, {
        key: '0',
        label: '很有想法',
      }, {
        key: '0',
        label: '很有想法',
      },
      {
        key: '1',
        label: '专注设计',
      },
      {
        key: '2',
        label: '辣~',
      },
      {
        key: '3',
        label: '喜欢大长腿',
      },
      {
        key: '4',
        label: '程序员',
      },
      {
        key: '5',
        label: '海纳百川',
      },
    ],
    notice: [
      {
        id: 'xxx1',
        title: 'xxx1',
        logo: 'xxx1',
        description: '那是一种内在的东西，他们到达不了，也无法触及的',
        updatedAt: new Date(),
        member: '科学搬砖组',
        href: '',
        memberLink: '',
      },
      {
        id: 'xxx2',
        title: 'xxx1',
        logo: 'xxx1',
        description: '希望是一个好东西，也许是最好的，好东西是不会消亡的',
        updatedAt: new Date('2017-07-24'),
        member: '全组都是吴彦祖',
        href: '',
        memberLink: '',
      },
      {
        id: 'xxx3',
        title: 'xxx1',
        logo: 'xxx1',
        description: '城镇中有那么多的酒馆，她却偏偏走进了我的酒馆',
        updatedAt: new Date(),
        member: '中二少女团',
        href: '',
        memberLink: '',
      },
    ],
    notifyCount: 12,
    unreadCount: 11,
    country: 'China',
    geographic: {
      province: {
        label: '云南',
        key: '330000',
      },
      city: {
        label: '大理',
        key: '330100',
      },
    },
    address: '西湖区工专路 77 号',
    phone: '0752-268888888',
  };

  //  渲染用户信息
  const renderUserInfo = ({ title, group, geographic }: Partial<CurrentUser>) => {
    return (
      <div className={styles.detail}>
        <p>
          <ContactsOutlined
            style={{
              marginRight: 8,
            }}
          />
          {title}
        </p>
        <p>
          <ClusterOutlined
            style={{
              marginRight: 8,
            }}
          />
          {group}
        </p>
        <p>
          <HomeOutlined
            style={{
              marginRight: 8,
            }}
          />
          {(geographic || { province: { label: '' } }).province.label}
          {
            (
              geographic || {
                city: {
                  label: '',
                },
              }
            ).city.label
          }
        </p>
      </div>
    );
  };

  // 渲染tab切换
  const renderChildrenByTabKey = (tabValue: tabKeyType) => {
    return null;
  };

  useEffect(() => {
    // 在组件挂载后执行的代码
    const chart = new Chart({
      container: 'container',
      autoFit: true,
    });
    chart.data({
      type: 'fetch',
      value:
        'https://gw.alipayobjects.com/os/bmw-prod/f38a8ad0-6e1f-4bb3-894c-7db50781fdec.json',
    });

    chart
      .area()
      .transform({ type: 'stackY', orderBy: 'maxIndex', reverse: true })
      .encode('x', (d) => new Date(d.year))
      .encode('y', 'revenue')
      .encode('series', 'format')
      .encode('color', 'group')
      .encode('shape', 'smooth')
      .axis('y', { labelFormatter: '~s' })
      .tooltip({ channel: 'y', valueFormatter: '.2f' });

    chart
      .line()
      .transform({ type: 'stackY', orderBy: 'maxIndex', reverse: true, y: 'y1' })
      .encode('x', (d) => new Date(d.year))
      .encode('y', 'revenue')
      .encode('series', 'format')
      .encode('shape', 'smooth')
      .encode('color', 'group') // For legendFilter.
      .style('stroke', 'white')
      .tooltip(false);
    chart.interaction('tooltip', { filter: (d) => parseInt(d.value) > 0 });
    chart.render();

  }, []);

  const enterWallet = async () => {
    await confluxFluent.enable();
    // 获取钱包地址
    console.log(confluxFluent.getAccount())
    setWalletAddress(confluxFluent.getAccount())
  }

  return (
    <Layout>
      <Card style={{ height: "92vh" }}>
        <Row gutter={24}>
          <Col lg={6} md={24}>
            <Card bordered={false} style={{ marginBottom: 24, minHeight: "620px" }} >
              {currentUser && (
                <div>
                  <div className={styles.avatarHolder}>
                    <img alt="" src={currentUser.avatar} />
                    <div className={styles.name}>{currentUser.name}</div>
                    <div>{currentUser?.signature}</div>
                  </div>
                  {renderUserInfo(currentUser)}
                  <div className={styles.accountInfo}>
                    <p style={{ width: "90px" }}>钱包地址: </p><p className={styles.accountAddress}>{walletAddress}</p>
                    {walletAddress ? (null) : (
                      <Button type="primary" ghost
                        onClick={() => enterWallet()}
                      >链接钱包</Button>
                    )}

                  </div>
                  <div className={styles.accountInfo}>
                    <p>余额： <strong>900</strong></p>
                  </div>
                  {/* <Divider dashed /> */}
                  <TagList tags={currentUser.tags || []} />
                  {/* <div id="radarCContainer"></div> */}
                </div>
              )}
            </Card>
          </Col>
          <Col lg={18} md={24}>
            <Card
              className={styles.tabsCard}
              bordered={false}
              tabList={operationTabList}
              activeTabKey={tabKey}
              onTabChange={(_tabKey: string) => {
                setTabKey(_tabKey as tabKeyType);
              }}
            >
              {renderChildrenByTabKey(tabKey)}
              {/* 统计信息 */}
              <RcResizeObserver
                key="resize-observer"
                onResize={(offset) => {
                  setResponsive(offset.width < 596);
                }}
              >
                <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
                  <StatisticCard
                    statistic={{
                      title: '火源总流通量',
                      value: 3701928,
                      description: "发行量：21000W",
                    }}
                    chart={
                      <img
                        src="https://gw.alipayobjects.com/zos/alicdn/ShNDpDTik/huan.svg"
                        alt="百分比"
                        width="100%"
                      />
                    }
                    chartPlacement="left"
                  />
                  <Divider type={responsive ? 'horizontal' : 'vertical'} />
                  <StatisticCard
                    statistic={{
                      title: '持有火源',
                      value: 1806062,
                      description: "占比1.5%",
                    }}
                    chart={
                      <img
                        src="https://gw.alipayobjects.com/zos/alicdn/6YR18tCxJ/huanlv.svg"
                        alt="百分比"
                        width="100%"
                      />
                    }
                    chartPlacement="left"
                  />
                  <StatisticCard
                    statistic={{
                      title: '持有原力',
                      value: 3701928,
                      description: "占比1.5%",
                    }}
                    chart={
                      <img
                        src="https://gw.alipayobjects.com/zos/alicdn/ShNDpDTik/huan.svg"
                        alt="百分比"
                        width="100%"
                      />
                    }
                    chartPlacement="left"
                  />
                </StatisticCard.Group>
                <div style={{ height: "400px" }} id="container"></div>
              </RcResizeObserver>
            </Card>
          </Col>
        </Row>
      </Card>
      <Footer style={{ textAlign: 'center' }}>
        vuca ©{new Date().getFullYear()} Created by s11eDao
      </Footer>
    </Layout>
  );
};
export default Center;
