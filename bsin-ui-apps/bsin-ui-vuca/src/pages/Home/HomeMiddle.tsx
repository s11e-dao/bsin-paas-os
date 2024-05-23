import React from 'react';
import { Row, Col } from 'antd';
import AnnularFigure from './AnnularFigure';
import DataDisplayCard from './DataDisplayCard';

const HomeMiddle = ({ data }) => {
  const dataCardColStyle = {
    xs: 24,
    sm: 24,
    md: 12,
    lg: 12,
    xl: 12,
  };
  return (
    <Row gutter={[16, 16]}>
      <Col xl={12} lg={24} md={24} sm={24} xs={24}>
        <AnnularFigure />
      </Col>
      <Col xl={12} lg={24} md={24} sm={24} xs={24}>
        <Row gutter={[8, 8]}>
          <Col {...dataCardColStyle}>
            <DataDisplayCard
              explain="指标说明"
              title="今日支出金额"
              data={data?.curPointIssueStats?.curPointIssueTotal}
              growthRate={{
                week: data?.curPointIssueStats?.comparePreWeek,
                day: data?.curPointIssueStats?.comparePreDay,
              }}
            />
          </Col>
          <Col {...dataCardColStyle}>
            <DataDisplayCard
              explain="指标说明"
              title="今日回收金额"
              data={data?.curPointRcyStats?.curPointRcyTotal}
              growthRate={{
                week: data?.curPointRcyStats?.comparePreWeek,
                day: data?.curPointRcyStats?.comparePreDay,
              }}
            />
          </Col>
          <Col {...dataCardColStyle}>
            <DataDisplayCard
              explain="指标说明"
              title="今日交易金额"
              data={data?.curPointTxTimesStats?.PointTxTimes}
              growthRate={{
                week: data?.curPointTxTimesStats?.comparePreWeek,
                day: data?.curPointTxTimesStats?.comparePreDay,
              }}
            />
          </Col>
          <Col {...dataCardColStyle}>
            <DataDisplayCard
              explain="指标说明"
              title="今日交易笔数"
              data={data?.curOMThrowInStats?.throwInTotal}
              growthRate={{
                week: data?.curOMThrowInStats?.comparePreWeek,
                day: data?.curOMThrowInStats?.comparePreDay,
              }}
            />
          </Col>
        </Row>
      </Col>
    </Row>
  );
};

export default HomeMiddle;
