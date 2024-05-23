import React, { useState, useEffect, FC } from 'react';
import ReactDOM from 'react-dom';
import { Line } from '@ant-design/plots';
import { ExcRateChangeList } from '../HomeBottom';

const LindeChart = ({ data }: { data: ExcRateChangeList[] }) => {
  const config = {
    data,
    xField: 'date',
    yField: 'excRate',
    yAxis: {
      title: {
        text: '汇率',
        spacing: 6,
      },
    },
    xAxis: {
      range: [0, 1],
    },
    meta: {
      excRate: {
        alias: '汇率',
      },
    },
    areaStyle: () => {
      return {
        fill: 'l(270) 0:#ffffff 0.5:#7ec2f3 1:#1890ff',
      };
    },
  };
  return <Line {...config} />;
};

export default LindeChart;
