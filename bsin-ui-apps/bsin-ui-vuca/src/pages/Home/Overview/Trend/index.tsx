import React from 'react';
import { Row, Col } from 'antd';
import { CaretUpOutlined, CaretDownOutlined } from '@ant-design/icons';
import { ReactNode } from 'react-router/node_modules/@types/react';

export type TrendProps = {
  flag: string;
  children: ReactNode;
  growthRate: string;
  style?: {};
};

const Trend: React.FC<TrendProps> = ({ flag, children, growthRate, style }) => {
  return (
    <div style={{ ...style, display: 'inline-block' }}>
      <span>{children}</span>
      <span>
        {growthRate}{' '}
        {Number(flag?.split('%')[0]) >= 0 ? (
          <CaretUpOutlined style={{ color: '#52C41A' }} />
        ) : (
          <CaretDownOutlined style={{ color: '#F5222D' }} />
        )}
      </span>
    </div>
  );
};

export default Trend;
