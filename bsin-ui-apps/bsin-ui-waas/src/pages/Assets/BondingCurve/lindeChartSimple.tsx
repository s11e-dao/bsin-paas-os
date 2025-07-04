import React from 'react';
import { Line } from '@ant-design/plots';

// 单条数据点类型
export type DataPoint = {
  supply: number;
  price: number;
  series: string;
  group?: string; // 添加数据组标识
};

// 组件属性类型
interface LindeChartProps {
  data: DataPoint[];
  height?: number;
  width?: number;
  showLegend?: boolean;
  colors?: string[];
}

const LindeChartSimple: React.FC<LindeChartProps> = ({
  data,
  height = 400,
  width = 600,
  showLegend = true,
  colors,
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
      visible: showLegend,
    },
    tooltip: {
      showCrosshairs: true,
      shared: true,
    },
    color: colors,
    animation: {
      appear: {
        animation: 'path-in',
        duration: 1000,
      },
    },
  };

  return <Line {...config} />;
};

export default LindeChartSimple;