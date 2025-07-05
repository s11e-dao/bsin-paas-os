# 分段联合曲线仿真系统设计文档

## 1. 系统概述

### 1.1 设计目标
分段联合曲线仿真系统是一个基于分段函数的联合曲线积分铸造模型，将价格曲线分为多个段，每个分段使用不同的价格函数和分布策略。该系统旨在为不同的项目提供灵活、可配置的联合曲线设计工具。

### 1.2 核心原理
- **分段函数**：将连续的价格曲线分解为多个独立的分段
- **价格函数**：每个分段使用不同的数学函数计算价格
- **分布策略**：控制分段点在供应量轴上的分布方式
- **波动控制**：通过波动因子和平滑因子控制曲线的随机性和连续性

### 1.3 技术架构
- **前端框架**：React + TypeScript + Ant Design
- **图表组件**：基于 @ant-design/plots 的 Line 组件
- **状态管理**：React Hooks (useState, useEffect)
- **表单处理**：Ant Design Form 组件
- **数据可视化**：自定义 LindeChartSimple 组件

## 2. 参数设计

### 2.1 基础参数

#### 2.1.1 总供应量 (totalSupply)
- **类型**：number
- **范围**：> 0
- **默认值**：21000000
- **说明**：代币的最大供应量上限，影响价格曲线的总长度
- **单位**：个

#### 2.1.2 分段数量 (segmentCount)
- **类型**：number
- **范围**：2-20
- **默认值**：5
- **说明**：价格曲线的分段数量，影响曲线的复杂度
- **单位**：段

#### 2.1.3 初始价格 (initialPrice)
- **类型**：number
- **范围**：> 0.001
- **默认值**：0.01
- **说明**：代币的起始价格，曲线的起始价格点
- **单位**：元

#### 2.1.4 最终价格 (finalPrice)
- **类型**：number
- **范围**：> 0.001
- **默认值**：1.0
- **说明**：代币的目标价格，曲线的结束价格点
- **单位**：元

#### 2.1.5 当前供应量 (currentSupply)
- **类型**：number
- **范围**：>= 0
- **默认值**：0
- **说明**：当前流通的代币数量，影响价格曲线的当前位置
- **单位**：个

### 2.2 分段分布策略

#### 2.2.1 均匀分布 (uniform)
- **公式**：`segmentPoint[i] = (totalSupply * i) / segmentCount`
- **特点**：分段点均匀分布
- **适用场景**：价格变化相对均匀的项目
- **优势**：简单直观，易于理解和预测

#### 2.2.2 渐进分布 (progressive)
- **公式**：`segmentPoint[i] = totalSupply * (i / segmentCount)^0.7`
- **特点**：前段较密，后段较疏
- **适用场景**：早期价格快速上涨，后期稳定的项目
- **优势**：早期投资者获得更高收益，后期价格趋于稳定

#### 2.2.3 回归分布 (regressive)
- **公式**：`segmentPoint[i] = totalSupply * (i / segmentCount)^1.3`
- **特点**：前段较疏，后段较密
- **适用场景**：早期价格稳定，后期快速上涨的项目
- **优势**：早期价格相对稳定，后期价格加速上涨

#### 2.2.4 自定义分布 (custom)
- **特点**：支持用户自定义分段点分布
- **适用场景**：特殊需求的项目
- **优势**：最大灵活性，可适应复杂需求

### 2.3 价格函数类型

#### 2.3.1 线性函数 (linear)
- **公式**：`price = slope × supply + intercept`
- **特点**：价格随供应量线性变化
- **适用场景**：价格增长相对稳定的项目
- **优势**：简单直观，易于计算

#### 2.3.2 指数函数 (exponential)
- **公式**：`price = startPrice × exp(growthRate × supply)`
- **特点**：价格随供应量指数增长
- **适用场景**：价格增长加速的项目
- **优势**：支持快速价格增长

#### 2.3.3 多项式函数 (polynomial)
- **公式**：`price = a × supply² + slope × supply + intercept`
- **特点**：价格随供应量二次变化
- **适用场景**：复杂价格变化模式的项目
- **优势**：支持更复杂的价格变化曲线

### 2.4 高级参数

#### 2.4.1 波动因子 (volatilityFactor)
- **类型**：number
- **范围**：0-1
- **默认值**：0.1
- **说明**：控制价格曲线的随机波动程度
- **效果**：0为无波动，1为最大波动

#### 2.4.2 平滑因子 (smoothingFactor)
- **类型**：number
- **范围**：0-1
- **默认值**：0.5
- **说明**：控制分段间的平滑过渡程度
- **效果**：0为无平滑，1为完全平滑

#### 2.4.3 自动优化 (autoOptimize)
- **类型**：boolean
- **默认值**：true
- **说明**：是否启用自动优化算法来调整参数
- **功能**：智能推荐最优参数组合

## 3. 功能特性

### 3.1 智能推荐系统

#### 3.1.1 分段数量推荐
- **算法**：基于总供应量计算最优分段数量
- **规则**：确保每个分段宽度不小于100万
- **公式**：`recommendedSegmentCount = Math.ceil(totalSupply / 1000000)`

#### 3.1.2 分段分布推荐
- **价格区间 > 0.5**：推荐渐进分布
- **价格区间 < 0.2**：推荐均匀分布
- **其他情况**：保持当前分布

#### 3.1.3 价格函数推荐
- **线性增长**：推荐线性函数
- **加速增长**：推荐指数函数
- **复杂变化**：推荐多项式函数

### 3.2 深度分析功能

#### 3.2.1 供应量利用率
- **公式**：`(currentSupply / totalSupply) × 100`
- **说明**：当前供应量占总供应量的百分比
- **用途**：反映代币的流通程度和价格阶段

#### 3.2.2 价格进度
- **公式**：`((currentPrice - initialPrice) / (finalPrice - initialPrice)) × 100`
- **说明**：当前价格在价格区间中的进度百分比
- **用途**：反映价格从初始值到最终值的完成度

#### 3.2.3 最优铸造点
- **算法**：计算价格变化率最大的供应量点
- **公式**：`maxChangeRate = max(Δprice/Δsupply)`
- **用途**：帮助用户判断最佳铸造时机

#### 3.2.4 最大变化率
- **说明**：价格对供应量的最大敏感度
- **单位**：元/积分
- **用途**：评估价格曲线的陡峭程度

### 3.3 分段配置详情

#### 3.3.1 分段信息
- **分段索引**：分段的编号
- **起始供应量**：分段的起始点
- **结束供应量**：分段的结束点
- **价格区间**：分段的价格范围
- **价格函数**：分段使用的数学函数
- **波动率**：分段的随机波动程度

#### 3.3.2 函数公式
- **线性函数**：`price = slope × supply + intercept`
- **指数函数**：`price = startPrice × exp(growthRate × supply)`
- **多项式函数**：`price = a × supply² + slope × supply + intercept`

### 3.4 交互式测试

#### 3.4.1 铸造测试
- **测试金额**：100元、500元、1000元、5000元、10000元
- **计算方式**：`mintAmount = laborValue / currentPrice`
- **输出结果**：获得积分数量、平均价格

#### 3.4.2 销毁测试
- **测试数量**：1000个、5000个、10000个、50000个、100000个
- **计算方式**：`burnValue = burnAmount × currentPrice`
- **输出结果**：获得法币金额、平均价格

## 4. 应用场景

### 4.1 渐进分布 + 指数函数
**适用项目**：早期价格快速上涨，后期稳定的项目
**特点**：
- 早期投资者获得更高收益
- 后期价格趋于稳定
- 适合新项目的快速成长阶段

**参数建议**：
- 分段分布：progressive
- 价格函数：exponential
- 波动因子：0.05-0.15
- 分段数量：5-8段

### 4.2 回归分布 + 线性函数
**适用项目**：早期价格稳定，后期快速上涨的项目
**特点**：
- 早期价格相对稳定
- 后期价格加速上涨
- 适合成熟项目的价值释放阶段

**参数建议**：
- 分段分布：regressive
- 价格函数：linear
- 波动因子：0.1-0.2
- 分段数量：6-10段

### 4.3 均匀分布 + 多项式函数
**适用项目**：复杂价格变化模式的项目
**特点**：
- 支持更复杂的价格变化曲线
- 适合有特殊需求的项目
- 提供最大的灵活性

**参数建议**：
- 分段分布：uniform
- 价格函数：polynomial
- 波动因子：0.15-0.25
- 分段数量：8-12段

## 5. 界面设计

### 5.1 分层参数设置

#### 5.1.1 基础参数区域
- **布局**：6列网格布局
- **参数**：总供应量、分段数量、初始价格、最终价格、当前供应量
- **控件**：InputNumber、Select
- **验证**：实时参数验证和提示

#### 5.1.2 高级参数区域
- **布局**：可折叠的高级设置面板
- **参数**：波动因子、平滑因子、自动优化
- **控件**：InputNumber、Switch
- **交互**：点击展开/收起

### 5.2 可视化展示

#### 5.2.1 关键指标卡片
- **布局**：4列统计卡片
- **指标**：当前价格、总供应量、价格区间、分段配置
- **样式**：Statistic 组件，带图标和单位

#### 5.2.2 深度分析面板
- **布局**：6列统计卡片 + 分段配置表格
- **指标**：供应量利用率、价格进度、最优铸造点、最大变化率
- **表格**：分段配置详情，包含函数公式

#### 5.2.3 价格曲线图表
- **组件**：LindeChartSimple
- **数据**：1000个数据点的价格曲线
- **交互**：鼠标悬停显示详细信息
- **样式**：平滑曲线，多色主题

### 5.3 智能提示系统

#### 5.3.1 参数验证
- **实时验证**：输入时立即检查参数有效性
- **错误提示**：红色边框和错误信息
- **警告提示**：黄色警告信息

#### 5.3.2 推荐提示
- **自动推荐**：根据当前参数计算推荐值
- **推荐消息**：绿色信息提示框
- **一键应用**：点击应用推荐参数

#### 5.3.3 应用场景说明
- **场景描述**：详细说明每种参数组合的适用场景
- **优势分析**：解释不同配置的优势和特点
- **使用建议**：提供具体的使用建议

## 6. 技术实现

### 6.1 核心算法

#### 6.1.1 分段点生成算法
```typescript
const generateSegmentPoints = (totalSupply: number, segmentCount: number, distribution: string): number[] => {
  const points = [];
  
  switch (distribution) {
    case 'uniform':
      for (let i = 1; i <= segmentCount; i++) {
        points.push((totalSupply * i) / segmentCount);
      }
      break;
    case 'progressive':
      for (let i = 1; i <= segmentCount; i++) {
        const ratio = Math.pow(i / segmentCount, 0.7);
        points.push(totalSupply * ratio);
      }
      break;
    case 'regressive':
      for (let i = 1; i <= segmentCount; i++) {
        const ratio = Math.pow(i / segmentCount, 1.3);
        points.push(totalSupply * ratio);
      }
      break;
  }
  
  return points;
};
```

#### 6.1.2 价格函数计算算法
```typescript
const calculatePriceAtSupply = (supply: number, segmentFunctions: any[]): number => {
  for (let i = 0; i < segmentFunctions.length; i++) {
    const func = segmentFunctions[i];
    if (supply >= func.startSupply && supply <= func.endSupply) {
      let price;
      
      switch (func.functionType) {
        case 'linear':
          price = func.slope * supply + func.intercept;
          break;
        case 'exponential':
          price = func.intercept * Math.exp(func.slope * supply);
          break;
        case 'polynomial':
          const a = (func.endPrice - func.startPrice) / Math.pow(func.endSupply - func.startSupply, 2);
          price = a * Math.pow(supply, 2) + func.slope * supply + func.intercept;
          break;
        default:
          price = func.slope * supply + func.intercept;
      }
      
      // 应用波动因子
      price *= (1 + func.volatility);
      
      return price;
    }
  }
  
  return segmentFunctions[segmentFunctions.length - 1]?.endPrice || 1.0;
};
```

### 6.2 数据结构

#### 6.2.1 参数接口
```typescript
interface SegmentedCurveParams {
  totalSupply: number;           // 总供应量
  segmentCount: number;          // 分段数量
  initialPrice: number;          // 初始价格
  finalPrice: number;            // 最终价格
  currentSupply: number;         // 当前供应量
  segmentType: 'linear' | 'exponential' | 'logarithmic' | 'custom';
  segmentDistribution: 'uniform' | 'progressive' | 'regressive' | 'custom';
  priceFunction: 'linear' | 'exponential' | 'polynomial' | 'custom';
  segmentConfigs: SegmentConfig[];
  autoOptimize: boolean;         // 自动优化
  volatilityFactor: number;      // 波动因子
  smoothingFactor: number;       // 平滑因子
}
```

#### 6.2.2 分段配置接口
```typescript
interface SegmentConfig {
  segmentIndex: number;
  startSupply: number;
  endSupply: number;
  startPrice: number;
  endPrice: number;
  priceFunction: string;
  slope: number;
  intercept: number;
  volatility: number;
  description: string;
}
```

### 6.3 组件架构

#### 6.3.1 主组件结构
```
SegmentedCurveSimulate
├── 参数设置区域
│   ├── 基础参数表单
│   └── 高级参数面板
├── 关键指标展示
├── 深度分析面板
├── 价格曲线图表
├── 交互式测试结果
└── 数据表格
```

#### 6.3.2 状态管理
```typescript
const [form] = Form.useForm();
const [isCalculating, setIsCalculating] = useState(false);
const [summary, setSummary] = useState<any>(null);
const [dataPoints, setDataPoints] = useState<any[]>([]);
const [autoCalculate, setAutoCalculate] = useState(true);
const [testResults, setTestResults] = useState<any>(null);
const [analysisData, setAnalysisData] = useState<any>(null);
const [segments, setSegments] = useState<SegmentConfig[]>([]);
const [showAdvancedSettings, setShowAdvancedSettings] = useState(false);
```

## 7. 使用指南

### 7.1 快速开始

#### 7.1.1 基础设置
1. 设置总供应量（建议：21000000）
2. 设置分段数量（建议：5-8段）
3. 设置初始价格（建议：0.01）
4. 设置最终价格（建议：1.0）
5. 设置当前供应量（建议：0）

#### 7.1.2 选择分布策略
1. **新项目**：选择渐进分布
2. **成熟项目**：选择回归分布
3. **特殊需求**：选择均匀分布或自定义分布

#### 7.1.3 选择价格函数
1. **稳定增长**：选择线性函数
2. **加速增长**：选择指数函数
3. **复杂变化**：选择多项式函数

### 7.2 高级配置

#### 7.2.1 波动因子设置
- **保守策略**：0.05-0.1
- **平衡策略**：0.1-0.15
- **激进策略**：0.15-0.25

#### 7.2.2 平滑因子设置
- **分段明显**：0.1-0.3
- **平滑过渡**：0.3-0.7
- **完全平滑**：0.7-1.0

### 7.3 最佳实践

#### 7.3.1 参数优化
1. 使用自动优化功能
2. 根据项目阶段调整参数
3. 定期评估和调整

#### 7.3.2 测试验证
1. 运行交互式测试
2. 分析关键指标
3. 验证价格曲线合理性

#### 7.3.3 监控调整
1. 监控实际价格变化
2. 对比预期和实际结果
3. 及时调整参数配置

## 8. 总结

分段联合曲线仿真系统提供了一个完整、灵活的联合曲线设计解决方案。通过丰富的参数配置、智能的推荐系统、深度的分析功能和直观的可视化界面，该系统能够满足不同项目的联合曲线设计需求。

### 8.1 系统优势
- **灵活性**：支持多种分段分布和价格函数
- **智能性**：提供自动推荐和优化功能
- **可视化**：直观的图表和数据分析
- **易用性**：友好的用户界面和详细的使用指南

### 8.2 应用价值
- **项目设计**：帮助项目方设计合适的联合曲线
- **投资分析**：帮助投资者分析投资时机
- **风险管理**：通过参数调整控制价格风险
- **市场教育**：通过可视化帮助理解联合曲线原理

### 8.3 未来扩展
- **更多价格函数**：支持更多数学函数类型
- **机器学习**：集成ML算法进行智能参数优化
- **历史数据分析**：基于历史数据优化参数
- **多币种支持**：支持多种代币的联合曲线设计

---

*本文档描述了分段联合曲线仿真系统的完整设计，包括参数配置、功能特性、应用场景和技术实现。该系统为联合曲线设计提供了一个强大而灵活的工具，能够满足不同项目的需求。* 