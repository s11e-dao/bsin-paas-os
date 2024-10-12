import React, { useState, useEffect, FC } from 'react';
import ReactDOM from 'react-dom';
import { Line } from '@ant-design/plots';

type ExcRateChangeList = {
  supply: string;
  excRate: number | string;
};

const LindeChart = ({ data }: { data: ExcRateChangeList[] }) => {
  const config = {
    data,
    xField: 'supply',
    yField: 'excRate',
    yAxis: {
      title: {
        text: 'price',
        spacing: 6,
      },
    },
    xAxis: {
      range: [0, 1],
    },
    meta: {
      excRate: {
        alias: 'price',
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
