import React from 'react';
import { Card, Row, Col } from 'antd';
import LindeChart from './LineChart';
import { Radio } from 'antd';
import { lssueToken } from './service';

export type ExcRateChangeList = {
  date: string;
  excRate: number | string;
};

type AllData = {
  ccyPair: string;
  curExcRate: number;
  excRateChangeList: ExcRateChangeList[];
};

const HomeBottom = () => {
  // 表头数据
  const [allData, setAllData] = React.useState<AllData>();

  React.useEffect(() => {
    getDate(7);
  }, []);

  // 获取报表数据
  const getDate = async (amount: number) => {};

  // 改变时间时调用
  const changeTime = (event: any) => {
    if (event.target.value === '7') {
      getDate(7);
    }
    if (event.target.value === '30') {
      getDate(30);
    }
  };

  return (
    <Card
      title={
        <div>
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12} md={12} lg={6} xl={6}>
              汇率变化
            </Col>
            <Col xs={24} sm={12} md={12} lg={6} xl={6}>
              当前汇率: {allData?.curExcRate}
            </Col>
            <Col xs={24} sm={12} md={12} lg={6} xl={6}>
              货币对: {allData?.ccyPair}{' '}
            </Col>
            <Col xs={24} sm={12} md={12} lg={6} xl={6}>
              <Radio.Group
                defaultValue="7"
                buttonStyle="solid"
                onChange={changeTime}
              >
                <Radio.Button value="7">近七天</Radio.Button>
                <Radio.Button value="30">近一个月</Radio.Button>
              </Radio.Group>
            </Col>
          </Row>
        </div>
      }
    >
      <LindeChart data={allData ? allData.excRateChangeList : []} />
    </Card>
  );
};

export default HomeBottom;
