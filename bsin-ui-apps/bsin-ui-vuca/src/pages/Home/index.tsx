import React, { Component } from 'react';
import { Row, Col } from 'antd';
import HomeTop from './HomeTop';
import HomeMiddle from './HomeMiddle';
import HomeBottom from './HomeBottom';
// import { otherReportStats } from './services';

const HomeAll = () => {
  // 报表数据
  const [data, setData] = React.useState();
  React.useEffect(() => {
    getReportStats();
  }, []);

  // 获取报表数据
  const getReportStats = async () => {
    //let res = await otherReportStats({});
    //console.log('首页其他报表统计', res);
    const topJson = {
      curTxAmtStats: {
        totalTxAmt: 11,
      },
      curIncomeStats: {
        curTotalIncome: 11,
      },
      curPayStats: {
        curTotalPay: 11,
      },
      curTxTotalStats: {
        curTxTotal: 1,
      },
    };
    setData(topJson);
  };

  return (
    <>
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <HomeTop data={data} />
        </Col>
        <Col span={24}>
          <HomeMiddle data={data} />
        </Col>
        <Col span={24}>
          <HomeBottom />
        </Col>
      </Row>
    </>
  );
};

export default HomeAll;
