import React from 'react';
import { Card, Row, Col, Tooltip } from 'antd';
import { InfoCircleOutlined } from '@ant-design/icons';
import Trend from '../Trend';
import { ReactNode } from 'react-router/node_modules/@types/react';

type GrowthRateType = {
  week: string;
  day: string;
};

type DataDisplayCardType = {
  title: string;
  data?: string | ReactNode;
  growthRate?: GrowthRateType;
  chart?: ReactNode;
  explain?: string;
};

const DataDisplayCard: React.FC<DataDisplayCardType> = ({
  title,
  data,
  growthRate,
  chart,
  explain,
}) => {
  return (
    <Card bodyStyle={{ padding: '20px 24px 8px 24px' }}>
      <div style={{ position: 'relative' }}>
        <span style={{ color: '#777' }}>{title}</span>
        {explain ? (
          <span style={{ color: '#777', position: 'absolute', right: 0 }}>
            <Tooltip title={explain}>
              <InfoCircleOutlined />
            </Tooltip>
          </span>
        ) : null}
      </div>
      <Row gutter={[0, 16]} align="bottom" style={{ marginBottom: 22 }}>
        <Col span={24} style={{ fontSize: 30 }}>
          {data ? data : 0}
        </Col>
        {chart ? (
          <Col span={24} style={{ height: 45 }}>
            {chart}
          </Col>
        ) : null}
        {growthRate ? (
          <Col span={24} style={{ height: 45 }}>
            <div style={{ position: 'absolute', bottom: 0, left: 0 }}>
              <Trend
                style={{ marginRight: 16 }}
                flag={growthRate.week}
                growthRate={growthRate.week}
              >
                <span>周同比：</span>
              </Trend>
              <Trend flag={growthRate.day} growthRate={growthRate.day}>
                <span>日同比：</span>
              </Trend>
            </div>
          </Col>
        ) : null}
      </Row>
    </Card>
  );
};

export default DataDisplayCard;
