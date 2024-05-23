import React, { useState, useEffect } from 'react';
import { Pie, measureTextWidth } from '@ant-design/plots';
import DataDisplayCard from '../DataDisplayCard';
import { lssueToken } from '../service';
const AnnularFigure = () => {
  const [data, setData] = useState([]);

  function renderStatistic(containerWidth, text, style) {
    const { width: textWidth, height: textHeight } = measureTextWidth(
      text,
      style,
    );
    const R = containerWidth / 2;
    let scale = 1;
    if (containerWidth < textWidth) {
      scale = Math.min(
        Math.sqrt(
          Math.abs(
            Math.pow(R, 2) /
              (Math.pow(textWidth / 2, 2) + Math.pow(textHeight, 2)),
          ),
        ),
        1,
      );
    }
    const textStyleStr = `width:${containerWidth}px;`;
    return `<div style="${textStyleStr};font-size:${scale}em;line-height:${
      scale < 1 ? 1 : 'inherit'
    };">${text}</div>`;
  }

  useEffect(() => {
    getData();
  }, []);
  // 获取图表信息
  const getData = async () => {
    //let res = await lssueToken({});
    //console.log(res);

    let data = [
      {
        type: '平台回收持有量',
        value: Number(20),
      },
      {
        type: '平台收益持有量',
        value: Number(20),
      },
      {
        type: '平台持有量',
        value: Number(40),
      },
      {
        type: '用户持有量',
        value: Number(10),
      },
      {
        type: '未流通量',
        value: Number(10),
      },
    ];

    setData(data);
  };

  const config = {
    appendPadding: 10,
    height: 300,
    data,
    angleField: 'value',
    colorField: 'type',
    radius: 1,
    innerRadius: 0.64,
    meta: {
      value: {
        formatter: (v) => `${v} SE`,
      },
    },
    legend: {
      itemHeight: 30,
      itemWidth: 500,
    },
    label: {
      type: 'inner',
      offset: '-50%',
      style: {
        textAlign: 'center',
      },
      autoRotate: false,
      content: '{value}',
    },
    statistic: {
      title: {
        offsetY: -4,
        customHtml: (container, view, datum) => {
          const { width, height } = container.getBoundingClientRect();
          const d = Math.sqrt(Math.pow(width / 2, 2) + Math.pow(height / 2, 2));
          const text = datum ? datum.type : '总发行量';
          return renderStatistic(d, text, {
            fontSize: 28,
          });
        },
      },
      content: {
        offsetY: 4,
        style: {
          fontSize: '20px',
        },
        customHtml: (container, view, datum, data) => {
          const { width } = container.getBoundingClientRect();
          const text = datum
            ? `${datum.value}<div style="font-size: 14px; display:inline-block">SE</div>`
            : ` ${data.reduce(
                (r, d) => r + d.value,
                0,
              )}<div style="font-size: 14px; display:inline-block">SE</div>`;
          return renderStatistic(width, text, {
            fontSize: 12,
          });
        },
      },
    },
    // 添加 中心统计文本 交互
    interactions: [
      {
        type: 'element-selected',
      },
      {
        type: 'element-active',
      },
      {
        type: 'pie-statistic-active',
      },
    ],
  };
  return (
    <DataDisplayCard
      title="今日资金流动"
      data={
        <div style={{ width: 400, margin: 'auto' }}>
          <Pie {...config} />
        </div>
      }
    />
  );
};

export default AnnularFigure;
