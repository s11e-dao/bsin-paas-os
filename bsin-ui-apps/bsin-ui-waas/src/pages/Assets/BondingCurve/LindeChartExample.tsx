import React, { useState } from 'react';
import LindeChart from './lindeChart';
import LindeChartSimple from './lindeChartSimple';

// 示例数据 - 多条折线图
const sampleData = [
  // 第一条线：产品A的价格曲线
  { supply: 0, price: 100, series: '产品A' },
  { supply: 100, price: 95, series: '产品A' },
  { supply: 200, price: 90, series: '产品A' },
  { supply: 300, price: 85, series: '产品A' },
  { supply: 400, price: 80, series: '产品A' },
  { supply: 500, price: 75, series: '产品A' },
  
  // 第二条线：产品B的价格曲线
  { supply: 0, price: 120, series: '产品B' },
  { supply: 100, price: 115, series: '产品B' },
  { supply: 200, price: 110, series: '产品B' },
  { supply: 300, price: 105, series: '产品B' },
  { supply: 400, price: 100, series: '产品B' },
  { supply: 500, price: 95, series: '产品B' },
  
  // 第三条线：产品C的价格曲线
  { supply: 0, price: 80, series: '产品C' },
  { supply: 100, price: 78, series: '产品C' },
  { supply: 200, price: 76, series: '产品C' },
  { supply: 300, price: 74, series: '产品C' },
  { supply: 400, price: 72, series: '产品C' },
  { supply: 500, price: 70, series: '产品C' },
];

const LindeChartExample: React.FC = () => {
  const [useSimpleVersion, setUseSimpleVersion] = useState(false);

  return (
    <div style={{ padding: '20px' }}>
      <h2>多条折线图示例</h2>
      <p>展示不同产品随供应量变化的价格曲线</p>
      
      <div style={{ marginBottom: '20px' }}>
        <label>
          <input
            type="checkbox"
            checked={useSimpleVersion}
            onChange={(e) => setUseSimpleVersion(e.target.checked)}
            style={{ marginRight: '8px' }}
          />
          使用简化版本（如果遇到 getTotalLength 错误）
        </label>
      </div>
      
      {useSimpleVersion ? (
        <LindeChartSimple 
          data={sampleData}
          height={500}
          width={800}
        />
      ) : (
        <LindeChart 
          data={sampleData}
          height={500}
          width={800}
        />
      )}
      
      <div style={{ marginTop: '20px' }}>
        <h3>使用说明：</h3>
        <ul>
          <li>横坐标：供应量</li>
          <li>纵坐标：价格（人民币）</li>
          <li>不同颜色的线条代表不同的产品</li>
          <li>鼠标悬停可查看具体数值</li>
          <li>支持图例显示和隐藏</li>
          <li>如果遇到 "getTotalLength is not a function" 错误，请勾选使用简化版本</li>
        </ul>
      </div>
    </div>
  );
};

export default LindeChartExample; 