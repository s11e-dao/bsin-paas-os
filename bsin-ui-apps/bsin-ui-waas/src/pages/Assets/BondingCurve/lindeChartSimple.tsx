import React from 'react';
import { Line } from '@ant-design/plots';

// 单条数据点类型
export type DataPoint = {
  supply: number;
  price: number;
  series: string;
};

// 组件属性类型
interface LindeChartProps {
  data: DataPoint[];
  height?: number;
  width?: number;
}

const LindeChartSimple: React.FC<LindeChartProps> = ({
  data,
  height = 400,
  width = 600,
}) => {
  const config = {
    data,
    height,
    width,
    xField: 'supply',
    yField: 'price',
    seriesField: 'series',
    smooth: true,
    yAxis: {
      title: {
        text: '价格',
      },
    },
    xAxis: {
      title: {
        text: '供应量',
      },
    },
    legend: {
      position: 'top-right' as const,
    },
    tooltip: {
      showCrosshairs: true,
    },
  };

  return <Line {...config} />;
};

export default LindeChartSimple; 