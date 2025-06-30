# 多条折线图组件 (LindeChart)

这是一个基于 @ant-design/plots 的多条折线图组件，支持同时显示多条折线，横坐标为供应量，纵坐标为价格。

## 功能特性

- ✅ 支持多条折线同时显示
- ✅ 自定义颜色配置
- ✅ 平滑曲线显示
- ✅ 交互式图例
- ✅ 网格线显示/隐藏
- ✅ 鼠标悬停提示
- ✅ 动画效果
- ✅ 响应式设计

## 数据结构

```typescript
type DataPoint = {
  supply: number;    // 供应量
  price: number;     // 价格
  series: string;    // 系列名称（用于区分不同折线）
};
```

## 组件属性

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| data | DataPoint[] | - | 图表数据 |
| height | number | 400 | 图表高度 |
| width | number | 600 | 图表宽度 |
| colors | string[] | ['#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1'] | 折线颜色 |
| showLegend | boolean | true | 是否显示图例 |
| showGrid | boolean | true | 是否显示网格线 |
| smooth | boolean | true | 是否使用平滑曲线 |

## 使用示例

### 基础用法

```tsx
import React from 'react';
import LindeChart from './lindeChart';

const data = [
  { supply: 0, price: 100, series: '产品A' },
  { supply: 100, price: 95, series: '产品A' },
  { supply: 200, price: 90, series: '产品A' },
  { supply: 0, price: 120, series: '产品B' },
  { supply: 100, price: 115, series: '产品B' },
  { supply: 200, price: 110, series: '产品B' },
];

const MyComponent = () => {
  return (
    <LindeChart 
      data={data}
      height={500}
      width={800}
    />
  );
};
```

### 自定义配置

```tsx
<LindeChart 
  data={data}
  height={600}
  width={1000}
  colors={['#ff4d4f', '#73d13d', '#40a9ff']}
  showLegend={true}
  showGrid={false}
  smooth={false}
/>
```

## 数据格式示例

```typescript
const sampleData = [
  // 第一条线：产品A
  { supply: 0, price: 100, series: '产品A' },
  { supply: 100, price: 95, series: '产品A' },
  { supply: 200, price: 90, series: '产品A' },
  
  // 第二条线：产品B
  { supply: 0, price: 120, series: '产品B' },
  { supply: 100, price: 115, series: '产品B' },
  { supply: 200, price: 110, series: '产品B' },
  
  // 第三条线：产品C
  { supply: 0, price: 80, series: '产品C' },
  { supply: 100, price: 78, series: '产品C' },
  { supply: 200, price: 76, series: '产品C' },
];
```

## 错误处理

### getTotalLength 错误

如果遇到 `element.getTotalLength is not a function` 错误，这通常是由于 @ant-design/plots 版本兼容性问题导致的。解决方案：

1. **使用简化版本**：
```tsx
import LindeChartSimple from './lindeChartSimple';

<LindeChartSimple 
  data={data}
  height={500}
  width={800}
/>
```

2. **更新依赖**：
```bash
npm update @ant-design/plots
# 或
yarn upgrade @ant-design/plots
```

3. **检查版本兼容性**：
确保 @ant-design/plots 版本与 React 版本兼容。

## 注意事项

1. 确保数据中的 `series` 字段用于区分不同的折线
2. `supply` 和 `price` 字段应为数字类型
3. 建议为不同的系列使用不同的颜色以提高可读性
4. 组件会自动处理图例的显示和隐藏
5. 如果遇到渲染错误，可以尝试使用简化版本

## 依赖

- React
- @ant-design/plots 