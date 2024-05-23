import React from 'react';
import { Row, Col } from 'antd';
import DataDisplayCard from './DataDisplayCard';
import { TinyArea, TinyColumn } from '@ant-design/plots';

const HomeTop = ({ data }) => {
  const visitData = [
    4000,
    2000,
    7000,
    2000,
    4000,
    1000,
    6000,
    5000,
    2000,
    5000,
    7000,
    3000,
  ];
  return (
    <div>
      <Row gutter={[16, 16]}>
        <Col xl={6} lg={12} md={12} sm={24} xs={24}>
          <DataDisplayCard
            explain="指标说明"
            title="累计交易金额"
            data={data?.curTxAmtStats?.totalTxAmt}
            growthRate={{
              week: data?.curTxAmtStats?.comparePreWeek,
              day: data?.curTxAmtStats?.comparePreDay,
            }}
          />
        </Col>
        <Col xl={6} lg={12} md={12} sm={24} xs={24}>
          <DataDisplayCard
            explain="指标说明"
            title="累计交易笔数"
            data={data?.curIncomeStats?.curTotalIncome}
            growthRate={{
              week: data?.curIncomeStats?.comparePreWeek,
              day: data?.curIncomeStats?.comparePreDay,
            }}
          />
        </Col>
        <Col xl={6} lg={12} md={12} sm={24} xs={24}>
          <DataDisplayCard
            explain="指标说明"
            title="累计回收金额"
            data={data?.curPayStats?.curTotalPay}
            chart={<TinyArea color="#975FE4" smooth data={visitData} />}
          />
        </Col>
        <Col xl={6} lg={12} md={12} sm={24} xs={24}>
          <DataDisplayCard
            explain="指标说明"
            title="累计支出金额"
            data={data?.curTxTotalStats?.curTxTotal}
            chart={<TinyColumn data={visitData} />}
          />
        </Col>
      </Row>
    </div>
  );
};

export default HomeTop;
