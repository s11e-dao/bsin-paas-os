import {
  Tabs, Card, Button, Popconfirm,
  Row,
  Col,
  Avatar,
  message,
  List,
  Descriptions,
  Table,
  Divider
} from 'antd';
import React from 'react';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { Link, history } from 'umi';
import {
  ScheduleTwoTone,
  AppstoreTwoTone,
  ProfileTwoTone,
} from '@ant-design/icons';
import moment from 'moment';


import TableTitle from '../../../components/TableTitle';

import columnsData, { columnsDataType } from './data';

type ExcRateChangeList = {
  supply: number | string;
  excRate: number | string;
};

type AllData = {
  ccyPair: string;
  curExcRate: number;
  excRateChangeList: ExcRateChangeList[];
};

interface DataType {
  key: string;
  name: string;
  age: number;
  address: string;
  tags: string[];
}

const data: DataType[] = [
  {
    key: '1',
    name: 'John Brown',
    age: 32,
    address: 'New York No. 1 Lake Park',
    tags: ['nice', 'developer'],
  },
  {
    key: '2',
    name: 'Jim Green',
    age: 42,
    address: 'London No. 1 Lake Park',
    tags: ['loser'],
  },
  {
    key: '3',
    name: 'Joe Black',
    age: 32,
    address: 'Sidney No. 1 Lake Park',
    tags: ['cool', 'teacher'],
  },
];

export default ({ setIsLoadAuthentication }) => {

  // 表头数据
  const [allData, setAllData] = React.useState<AllData>();

  /**
   * 监听页面路径设置布局
   */
  React.useEffect(() => {
    // getDate(7)
    //getBondingCurveTokenJournal();
  }, []);

  const getBondingCurveTokenJournal = async () => {
    const reqParams = {
      merchantNo: '',
      pageSize: '100',
      current: '1',
      limit: '100',
    };
  };

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.dictType}>
      <a onClick={() => toViewContractTemplate(record)}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="确定撤销此处认证？?"
        onConfirm={() => toDelContractTemplate(record.id)}
        onCancel={() => {
          message.warning(`取消认证`);
        }}
        okText="是"
        cancelText="否"
      >
        <a>撤销</a>
      </Popconfirm>
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 获取报表数据
  const getDate = async (amount: number) => {
    let res = {
      body: {
        excRateChangeList: [
          {
            supply: 7472785520000000000000,
            excRate: 0.1336922435,
          },
          {
            supply: 167522907060000000000000,
            excRate: 0.138059241,
          },
          {
            supply: 325581590830000000000000,
            excRate: 0.1429301828,
          },
          {
            supply: 481672335450000000000000,
            excRate: 0.1483468416,
          },
          {
            supply: 635821714090000000000000,
            excRate: 0.1543682634,
          },
          {
            supply: 788059644590000000000000,
            excRate: 0.1610512487,
          },
          {
            supply: 938418917840000000000000,
            excRate: 0.1684565665,
          },
          {
            supply: 1486935206350000000000000,
            excRate: 0.2039348265,
          },
          {
            supply: 2028892000640000000000000,
            excRate: 0.2569915906,
          },
          {
            supply: 2564746571040000000000000,
            excRate: 0.3359547999,
          },
          {
            supply: 3095075715900000000000000,
            excRate: 0.453027127,
          },
          {
            supply: 3620521275890000000000000,
            excRate: 0.6259082209,
          },
          {
            supply: 4141735312230000000000000,
            excRate: 0.8803038931,
          },
          {
            supply: 4659337743340000000000000,
            excRate: 1.2532208731,
          },
        ],
        ccyPair: '',
        curExcRate: 1,
      },
    };

    let excRateChangeList = res.body.excRateChangeList.map(
      (item: ExcRateChangeList) => {
        return {
          supply: item.supply,
          excRate: Number(item.excRate),
        };
      },
    );
    let data = { ...res.body, excRateChangeList };
    setAllData(data);
  };

  return (
    <div>
      <Card>
        <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab="商户认证信息" key="1">
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
                          src=""
                        />
                      }
                      title={
                        <div style={{ fontSize: 24 }}>
                          你好
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
                      style={{ color: "#ddd" }}
                      onClick={() => {
                        // setQiankunGlobalState(4);
                        // console.log(globalState);
                      }}
                    >
                      <Row gutter={[5, 8]} >
                        <Col className="gutter-row" span={24}>
                          <ProfileTwoTone
                            style={{ fontSize: 40 }}
                            twoToneColor={"#ddd"}
                          />
                        </Col>
                        <Col className="gutter-row" span={24}>
                          待办事项
                        </Col>
                      </Row>
                    </a>
                  </Col>
                  <Col className="gutter-row" xl={6} lg={6} md={6} sm={12} xs={24}>
                    <Link to="/apps" style={{ color: "#ddd" }}>
                      <Row gutter={[5, 8]} >
                        <Col className="gutter-row" span={24}>
                          <ScheduleTwoTone
                            style={{ fontSize: 40 }}
                            twoToneColor={"#ddd"}
                          />
                        </Col>
                        <Col className="gutter-row" span={24}>
                          日程
                        </Col>
                      </Row>
                    </Link>
                  </Col>
                  <Col className="gutter-row" xl={6} lg={6} md={6} sm={12} xs={24}>
                    <Link to="/apps" style={{ color: "#ddd" }}>
                      <Row gutter={[5, 8]} >
                        <Col className="gutter-row" span={24}>
                          <AppstoreTwoTone
                            style={{ fontSize: 40 }}
                            twoToneColor={"#ddd"}
                          />
                        </Col>
                        <Col className="gutter-row" span={24}>
                          打开多个应用
                        </Col>
                      </Row>
                    </Link>
                  </Col>
                </Row>
              </Col>
            </Row>

            <Card
              title="商户实名认证"
              extra={<a onClick={() => {
                setIsLoadAuthentication(true);
              }} >修改认证主体</a>}
              bordered={false}
            >
              <Descriptions >
                <Descriptions.Item label="账号类型">企业账户</Descriptions.Item>
                <Descriptions.Item label="法人姓名">1810000000</Descriptions.Item>
                <Descriptions.Item label="认证状态">Hangzhou, Zhejiang</Descriptions.Item>
                <Descriptions.Item label="企业名称">empty</Descriptions.Item>
                <Descriptions.Item label="法人证证类型">No. 18, Wantang Road, Xihu District</Descriptions.Item>
                <Descriptions.Item label="认证时间">No. 18, Wantang Road, Xihu District</Descriptions.Item>
                <Descriptions.Item label="组织机构代码">empty</Descriptions.Item>
                <Descriptions.Item label="法人证证号">No. 18, Wantang Road, Xihu District</Descriptions.Item>
                <Descriptions.Item label="法人手机号">No. 18, Wantang Road, Xihu District</Descriptions.Item>
              </Descriptions>
            </Card>

            <Card
              title="认证记录"
              bordered={false}
            >
              <Table columns={columns} dataSource={data} />
            </Card>

          </Tabs.TabPane>
          {/* <Tabs.TabPane tab="店铺社群" key="2">
            设置
          </Tabs.TabPane> */}
        </Tabs>
      </Card>
    </div>
  );
};
