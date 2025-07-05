import {
  Form,
  Card,
  Button,
  InputNumber,
  Row,
  Col,
  Statistic,
  Space,
  Alert,
  Typography,
  message,
  Table,
  Tooltip,
  Switch,
  Divider,
  Progress,
  Tag,
  Select,
  Input,
} from 'antd';
import React, { useState, useEffect } from 'react';
import { CalculatorOutlined, ReloadOutlined, InfoCircleOutlined, BulbOutlined, PlayCircleOutlined, SettingOutlined } from '@ant-design/icons';
import LindeChartSimple, { DataPoint } from './lindeChartSimple';

const { Title, Text, Paragraph } = Typography;
const { Option } = Select;

// 分段联合曲线参数接口
interface SegmentedCurveParams {
  totalSupply: number;           // 总供应量
  segmentCount: number;          // 分段数量
  initialPrice: number;          // 初始价格
  finalPrice: number;            // 最终价格
  currentSupply: number;         // 当前供应量
  segmentType: 'linear' | 'exponential' | 'logarithmic' | 'custom'; // 分段类型
  segmentDistribution: 'uniform' | 'progressive' | 'regressive' | 'custom'; // 分段分布
  priceFunction: 'linear' | 'exponential' | 'polynomial' | 'custom'; // 价格函数类型
  segmentConfigs: SegmentConfig[]; // 分段配置
  autoOptimize: boolean;         // 自动优化
  volatilityFactor: number;      // 波动因子
  smoothingFactor: number;       // 平滑因子
}

// 分段配置接口
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

// 仿真结果接口
interface SegmentedSimulationResult {
  chartData: DataPoint[];
  summary: any;
  dataPoints: any[];
  segments: SegmentConfig[];
  analysis: any;
}

interface SegmentedCurveSimulateProps {
  refreshTrigger?: number;
}

export default ({ refreshTrigger }: SegmentedCurveSimulateProps) => {
  const [form] = Form.useForm();
  const [isCalculating, setIsCalculating] = useState(false);
  const [summary, setSummary] = useState<any>(null);
  const [dataPoints, setDataPoints] = useState<any[]>([]);
  const [autoCalculate, setAutoCalculate] = useState(true);
  const [testResults, setTestResults] = useState<any>(null);
  const [analysisData, setAnalysisData] = useState<any>(null);
  const [segments, setSegments] = useState<SegmentConfig[]>([]);
  const [showAdvancedSettings, setShowAdvancedSettings] = useState(false);

  // 默认参数
  const defaultParams: SegmentedCurveParams = {
    totalSupply: 21000000,       // 2100万供应上限
    segmentCount: 5,             // 5个分段
    initialPrice: 0.01,          // 初始价格0.01
    finalPrice: 1.0,             // 最终价格1.0
    currentSupply: 0,            // 当前供应量
    segmentType: 'linear',       // 线性分段
    segmentDistribution: 'uniform', // 均匀分布
    priceFunction: 'linear',     // 线性价格函数
    segmentConfigs: [],          // 分段配置
    autoOptimize: true,          // 自动优化
    volatilityFactor: 0.1,       // 波动因子
    smoothingFactor: 0.5,        // 平滑因子
  };

  // 计算分段联合曲线仿真
  const calculateSegmentedSimulation = (params: SegmentedCurveParams): SegmentedSimulationResult => {
    const { 
      totalSupply, 
      segmentCount, 
      initialPrice, 
      finalPrice, 
      currentSupply, 
      segmentType,
      segmentDistribution,
      priceFunction,
      volatilityFactor,
      smoothingFactor
    } = params;
    
    // 生成分段点
    const segmentPoints = generateSegmentPoints(totalSupply, segmentCount, segmentDistribution);
    
    // 计算每个分段的价格函数
    const segmentFunctions = calculateSegmentFunctions(segmentPoints, initialPrice, finalPrice, priceFunction, volatilityFactor, smoothingFactor);
    
    // 生成数据点
    const dataPoints = [];
    const step = totalSupply / 1000; // 1000个数据点
    
    for (let supply = 0; supply <= totalSupply; supply += step) {
      const price = calculatePriceAtSupply(supply, segmentFunctions);
      const mintAmount = supply > 0 ? 1000 / price : 0;
      const burnAmount = supply > 0 ? 10000 * price : 0;
      
      dataPoints.push({
        supply,
        price,
        mintAmount,
        burnAmount,
      });
    }
    
    // 计算当前价格
    const currentPrice = calculatePriceAtSupply(currentSupply, segmentFunctions);
    
    // 计算汇总信息
    const summary = {
      currentPrice,
      totalSupply,
      priceRange: finalPrice - initialPrice,
      segmentCount,
      segmentType,
      segmentDistribution,
      priceFunction,
      supplyUtilization: (currentSupply / totalSupply) * 100,
      priceProgress: ((currentPrice - initialPrice) / (finalPrice - initialPrice)) * 100,
      volatilityFactor,
      smoothingFactor,
    };
    
    // 生成图表数据
    const chartData: DataPoint[] = dataPoints.map(point => ({
      supply: point.supply,
      price: point.price,
      series: '价格曲线',
      group: '分段联合曲线',
    }));
    
    // 生成分段信息
    const segments = segmentFunctions.map((func, index) => ({
      segmentIndex: index + 1,
      startSupply: func.startSupply,
      endSupply: func.endSupply,
      startPrice: func.startPrice,
      endPrice: func.endPrice,
      priceFunction: func.function,
      slope: func.slope,
      intercept: func.intercept,
      volatility: func.volatility || 0,
      description: `第${index + 1}段: ${func.startPrice.toFixed(4)} - ${func.endPrice.toFixed(4)}`,
    }));
    
    // 计算分析数据
    const analysis = calculateAnalysisData(params, { summary, segments, dataPoints });
    
    return {
      chartData,
      summary,
      dataPoints,
      segments,
      analysis,
    };
  };

  // 生成分段点
  const generateSegmentPoints = (totalSupply: number, segmentCount: number, distribution: string): number[] => {
    const points = [];
    
    switch (distribution) {
      case 'uniform':
        // 均匀分布
        for (let i = 1; i <= segmentCount; i++) {
          points.push((totalSupply * i) / segmentCount);
        }
        break;
      case 'progressive':
        // 渐进分布（前段较密，后段较疏）
        for (let i = 1; i <= segmentCount; i++) {
          const ratio = Math.pow(i / segmentCount, 0.7);
          points.push(totalSupply * ratio);
        }
        break;
      case 'regressive':
        // 回归分布（前段较疏，后段较密）
        for (let i = 1; i <= segmentCount; i++) {
          const ratio = Math.pow(i / segmentCount, 1.3);
          points.push(totalSupply * ratio);
        }
        break;
      case 'custom':
        // 自定义分布（这里使用默认的均匀分布）
        for (let i = 1; i <= segmentCount; i++) {
          points.push((totalSupply * i) / segmentCount);
        }
        break;
    }
    
    return points;
  };

  // 计算分段函数
  const calculateSegmentFunctions = (
    segmentPoints: number[], 
    initialPrice: number, 
    finalPrice: number, 
    priceFunctionType: string,
    volatilityFactor: number,
    smoothingFactor: number
  ) => {
    const functions = [];
    const priceStep = (finalPrice - initialPrice) / (segmentPoints.length - 1);
    
    for (let i = 0; i < segmentPoints.length - 1; i++) {
      const startPrice = initialPrice + i * priceStep;
      const endPrice = initialPrice + (i + 1) * priceStep;
      const startSupply = i === 0 ? 0 : segmentPoints[i - 1];
      const endSupply = segmentPoints[i];
      
      let slope, intercept, functionStr;
      
      switch (priceFunctionType) {
        case 'linear':
          // 线性函数
          slope = (endPrice - startPrice) / (endSupply - startSupply);
          intercept = startPrice - slope * startSupply;
          functionStr = `price = ${slope.toFixed(6)} × supply + ${intercept.toFixed(6)}`;
          break;
        case 'exponential':
          // 指数函数
          const growthRate = Math.log(endPrice / startPrice) / (endSupply - startSupply);
          slope = growthRate;
          intercept = startPrice;
          functionStr = `price = ${startPrice.toFixed(6)} × exp(${growthRate.toFixed(6)} × supply)`;
          break;
        case 'polynomial':
          // 多项式函数（二次）
          const a = (endPrice - startPrice) / Math.pow(endSupply - startSupply, 2);
          slope = 2 * a * startSupply;
          intercept = startPrice - a * Math.pow(startSupply, 2);
          functionStr = `price = ${a.toFixed(6)} × supply² + ${slope.toFixed(6)} × supply + ${intercept.toFixed(6)}`;
          break;
        default:
          // 默认线性函数
          slope = (endPrice - startPrice) / (endSupply - startSupply);
          intercept = startPrice - slope * startSupply;
          functionStr = `price = ${slope.toFixed(6)} × supply + ${intercept.toFixed(6)}`;
      }
      
      // 添加波动因子
      const volatility = volatilityFactor * (Math.random() - 0.5);
      
      functions.push({
        startPrice,
        endPrice,
        startSupply,
        endSupply,
        slope,
        intercept,
        function: functionStr,
        volatility,
        functionType: priceFunctionType,
      });
    }
    
    return functions;
  };

  // 计算指定供应量下的价格
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
    
    // 如果超出范围，返回最终价格
    return segmentFunctions[segmentFunctions.length - 1]?.endPrice || 1.0;
  };

  // 计算分析数据
  const calculateAnalysisData = (params: SegmentedCurveParams, result: any) => {
    const { summary, segments, dataPoints } = result;
    const currentPrice = summary.currentPrice;
    
    // 计算价格变化率
    const priceChangeRates = [];
    for (let i = 1; i < dataPoints.length; i++) {
      const prevPoint = dataPoints[i - 1];
      const currPoint = dataPoints[i];
      const supplyChange = currPoint.supply - prevPoint.supply;
      const priceChange = currPoint.price - prevPoint.price;
      const changeRate = supplyChange > 0 ? priceChange / supplyChange : 0;
      priceChangeRates.push({
        supply: currPoint.supply,
        changeRate,
        price: currPoint.price,
      });
    }
    
    // 计算最优铸造时机
    const maxChangeRate = Math.max(...priceChangeRates.map(r => r.changeRate));
    const optimalSupply = priceChangeRates.find(r => r.changeRate === maxChangeRate)?.supply || 0;
    
    return {
      currentPrice,
      priceChangeRates,
      optimalSupply,
      maxChangeRate,
      supplyUtilization: summary.supplyUtilization,
      priceProgress: summary.priceProgress,
      segmentCount: params.segmentCount,
      segmentType: params.segmentType,
      segmentDistribution: params.segmentDistribution,
      priceFunction: params.priceFunction,
      volatilityFactor: params.volatilityFactor,
      smoothingFactor: params.smoothingFactor,
    };
  };

  // 处理表单提交
  const handleSubmit = (values: SegmentedCurveParams) => {
    setIsCalculating(true);
    
    try {
      const result = calculateSegmentedSimulation(values);
      setSummary(result.summary);
      setDataPoints(result.dataPoints);
      setSegments(result.segments);
      
      // 设置分析数据
      setAnalysisData(result.analysis);
      
      message.success('分段联合曲线仿真完成');
    } catch (error) {
      console.error('仿真计算失败:', error);
      message.error('仿真计算失败，请检查参数');
    } finally {
      setIsCalculating(false);
    }
  };

  // 重置为默认值
  const handleReset = () => {
    form.setFieldsValue(defaultParams);
    handleSubmit(defaultParams);
  };

  // 计算推荐参数
  const handleCalculateRecommendations = () => {
    const currentValues = form.getFieldsValue();
    
    // 根据当前参数计算推荐值
    const recommendations: Partial<SegmentedCurveParams> = {};
    
    if (currentValues.totalSupply && currentValues.segmentCount) {
      const segmentWidth = currentValues.totalSupply / currentValues.segmentCount;
      if (segmentWidth < 1000000) {
        recommendations.segmentCount = Math.ceil(currentValues.totalSupply / 1000000);
      }
    }
    
    // 根据价格区间推荐分段分布
    const priceRange = currentValues.finalPrice - currentValues.initialPrice;
    if (priceRange > 0.5) {
      recommendations.segmentDistribution = 'progressive';
    } else if (priceRange < 0.2) {
      recommendations.segmentDistribution = 'uniform';
    }
    
    if (Object.keys(recommendations).length > 0) {
      const newValues = { ...currentValues, ...recommendations };
      form.setFieldsValue(newValues);
      
      const recommendationMessages = [];
      if (recommendations.segmentCount) {
        recommendationMessages.push(`推荐分段数量: ${recommendations.segmentCount}`);
      }
      if (recommendations.segmentDistribution) {
        recommendationMessages.push(`推荐分段分布: ${recommendations.segmentDistribution}`);
      }
      
      if (recommendationMessages.length > 0) {
        message.info(`已计算推荐值: ${recommendationMessages.join(', ')}`);
      }
      
      if (autoCalculate) {
        setTimeout(() => {
          handleSubmit(newValues);
        }, 100);
      }
    }
  };

  // 处理表单值变化
  const handleFormValuesChange = (changedValues: any, allValues: any) => {
    if (autoCalculate) {
      setTimeout(() => {
        handleSubmit(allValues);
      }, 500);
    }
  };

  // 交互式测试功能
  const handleInteractiveTest = () => {
    const params = form.getFieldsValue();
    const result = calculateSegmentedSimulation(params);
    
    const testLaborValues = [100, 500, 1000, 5000, 10000];
    const testBurnAmounts = [1000, 5000, 10000, 50000, 100000];

    const mintResults = testLaborValues.map(laborValue => ({
      laborValue,
      mintAmount: laborValue / result.summary.currentPrice,
      avgPrice: result.summary.currentPrice,
    }));

    const burnResults = testBurnAmounts.map(burnAmount => ({
      burnAmount,
      burnValue: burnAmount * result.summary.currentPrice,
      avgPrice: result.summary.currentPrice,
    }));

    setTestResults({ mintResults, burnResults });
    message.success('交互式测试完成');
  };

  // 组件初始化
  useEffect(() => {
    form.setFieldsValue(defaultParams);
    handleSubmit(defaultParams);
  }, []);

  // 监听refreshTrigger变化
  useEffect(() => {
    if (refreshTrigger && refreshTrigger > 0) {
      handleSubmit(form.getFieldsValue());
    }
  }, [refreshTrigger]);

  // 表格列定义
  const columns = [
    {
      title: (
        <Tooltip title="代币的当前供应量，是分段函数计算价格的基础变量">
          <span>供应量</span>
        </Tooltip>
      ),
      dataIndex: 'supply',
      key: 'supply',
      width: 150,
      align: 'right' as const,
      render: (value: number) => Math.round(value).toLocaleString(),
    },
    {
      title: (
        <Tooltip title="基于分段函数计算的代币价格">
          <span>价格</span>
        </Tooltip>
      ),
      dataIndex: 'price',
      key: 'price',
      width: 120,
      align: 'right' as const,
      render: (value: number) => value.toFixed(4),
    },
    {
      title: (
        <Tooltip title="投入1000元劳动价值获得的积分数量">
          <span>铸造收益</span>
        </Tooltip>
      ),
      dataIndex: 'mintAmount',
      key: 'mintAmount',
      width: 150,
      align: 'right' as const,
      render: (value: number) => Math.round(value).toLocaleString(),
    },
    {
      title: (
        <Tooltip title="销毁10000积分获得的法币金额">
          <span>销毁收益</span>
        </Tooltip>
      ),
      dataIndex: 'burnAmount',
      key: 'burnAmount',
      width: 150,
      align: 'right' as const,
      render: (value: number) => value.toFixed(2),
    },
  ];

  // 测试结果表格列定义
  const testColumns = [
    {
      title: (
        <Tooltip title="投入的劳动价值金额（元）">
          <span>劳动价值</span>
        </Tooltip>
      ),
      dataIndex: 'laborValue',
      key: 'laborValue',
      width: 120,
      align: 'right' as const,
      render: (value: number) => `${value}元`,
    },
    {
      title: (
        <Tooltip title="投入劳动价值后获得的积分数量">
          <span>获得积分</span>
        </Tooltip>
      ),
      dataIndex: 'mintAmount',
      key: 'mintAmount',
      width: 150,
      align: 'right' as const,
      render: (value: number) => Math.round(value).toLocaleString(),
    },
    {
      title: (
        <Tooltip title="劳动价值与获得积分的比值">
          <span>平均价格</span>
        </Tooltip>
      ),
      dataIndex: 'avgPrice',
      key: 'avgPrice',
      width: 120,
      align: 'right' as const,
      render: (value: number) => value.toFixed(4),
    },
  ];

  const burnTestColumns = [
    {
      title: (
        <Tooltip title="要销毁的积分数量">
          <span>销毁积分</span>
        </Tooltip>
      ),
      dataIndex: 'burnAmount',
      key: 'burnAmount',
      width: 120,
      align: 'right' as const,
      render: (value: number) => value.toLocaleString(),
    },
    {
      title: (
        <Tooltip title="销毁积分后获得的法币金额">
          <span>获得法币</span>
        </Tooltip>
      ),
      dataIndex: 'burnValue',
      key: 'burnValue',
      width: 150,
      align: 'right' as const,
      render: (value: number) => `${value.toFixed(2)}元`,
    },
    {
      title: (
        <Tooltip title="获得法币与销毁积分的比值">
          <span>平均价格</span>
        </Tooltip>
      ),
      dataIndex: 'avgPrice',
      key: 'avgPrice',
      width: 120,
      align: 'right' as const,
      render: (value: number) => value.toFixed(4),
    },
  ];

  return (
    <div style={{ padding: '20px' }}>
      <Title level={2}>分段联合曲线仿真系统</Title>

      <Alert
        message="分段联合曲线原理"
        description={
          <div>
            <Paragraph style={{ marginBottom: '8px' }}>
              基于分段函数的联合曲线积分铸造模型，将价格曲线分为多个段，每个分段使用不同的价格函数和分布策略。
            </Paragraph>
            <Paragraph style={{ marginBottom: '8px' }}>
              <strong>核心参数：</strong>
            </Paragraph>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li><strong>分段类型</strong>：linear（线性）、exponential（指数）、logarithmic（对数）、custom（自定义）</li>
              <li><strong>分段分布</strong>：uniform（均匀）、progressive（渐进）、regressive（回归）、custom（自定义）</li>
              <li><strong>价格函数</strong>：linear（线性）、exponential（指数）、polynomial（多项式）</li>
              <li><strong>波动因子</strong>：控制价格曲线的随机波动程度</li>
              <li><strong>平滑因子</strong>：控制分段间的平滑过渡</li>
            </ul>
            <Paragraph style={{ marginTop: '8px', marginBottom: '8px' }}>
              <strong>应用场景：</strong>
            </Paragraph>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li>渐进分布：适合早期价格快速上涨，后期稳定的项目</li>
              <li>回归分布：适合早期价格稳定，后期快速上涨的项目</li>
              <li>均匀分布：适合价格变化相对均匀的项目</li>
              <li>指数函数：适合价格增长加速的项目</li>
              <li>多项式函数：适合复杂价格变化模式的项目</li>
            </ul>
          </div>
        }
        type="info"
        showIcon
        icon={<InfoCircleOutlined />}
        style={{ marginBottom: '20px' }}
      />

      {/* 基础参数输入区域 */}
      <Card title="基础参数设置" style={{ marginBottom: '20px' }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={defaultParams}
          onValuesChange={handleFormValuesChange}
        >
          <Row gutter={16}>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="代币的最大供应量上限，影响价格曲线的总长度">
                    <span>总供应量</span>
                  </Tooltip>
                }
                name="totalSupply"
                rules={[{ required: true, message: '请输入总供应量' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="21000000"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="价格曲线的分段数量，影响曲线的复杂度">
                    <span>分段数量</span>
                  </Tooltip>
                }
                name="segmentCount"
                rules={[{ required: true, message: '请输入分段数量' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={2}
                  max={20}
                  placeholder="5"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="代币的初始价格，曲线的起始价格点">
                    <span>初始价格</span>
                  </Tooltip>
                }
                name="initialPrice"
                rules={[{ required: true, message: '请输入初始价格' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0.001}
                  step={0.001}
                  precision={3}
                  placeholder="0.01"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="代币的最终价格，曲线的结束价格点">
                    <span>最终价格</span>
                  </Tooltip>
                }
                name="finalPrice"
                rules={[{ required: true, message: '请输入最终价格' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0.001}
                  step={0.1}
                  precision={2}
                  placeholder="1.0"
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="当前代币的供应量，影响价格曲线的当前位置">
                    <span>当前供应量</span>
                  </Tooltip>
                }
                name="currentSupply"
                rules={[{ required: true, message: '请输入当前供应量' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  placeholder="0"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="分段分布策略，影响分段点的分布方式">
                    <span>分段分布</span>
                  </Tooltip>
                }
                name="segmentDistribution"
              >
                <Select placeholder="选择分段分布">
                  <Option value="uniform">
                    <Tooltip title="均匀分布：segmentPoint[i] = (totalSupply × i) / segmentCount&#10;特点：分段点均匀分布&#10;适用：价格变化相对均匀的项目">
                      均匀分布
                    </Tooltip>
                  </Option>
                  <Option value="progressive">
                    <Tooltip title="渐进分布：segmentPoint[i] = totalSupply × (i / segmentCount)^0.7&#10;特点：前段较密，后段较疏&#10;适用：早期价格快速上涨，后期稳定的项目">
                      渐进分布
                    </Tooltip>
                  </Option>
                  <Option value="regressive">
                    <Tooltip title="回归分布：segmentPoint[i] = totalSupply × (i / segmentCount)^1.3&#10;特点：前段较疏，后段较密&#10;适用：早期价格稳定，后期快速上涨的项目">
                      回归分布
                    </Tooltip>
                  </Option>
                  <Option value="custom">
                    <Tooltip title="自定义分布：支持用户自定义分段点分布&#10;特点：最大灵活性&#10;适用：特殊需求的项目">
                      自定义分布
                    </Tooltip>
                  </Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="价格函数类型，影响每个分段内的价格计算方式">
                    <span>价格函数</span>
                  </Tooltip>
                }
                name="priceFunction"
              >
                <Select placeholder="选择价格函数">
                  <Option value="linear">
                    <Tooltip title="线性函数：price = slope × supply + intercept&#10;特点：价格随供应量线性变化&#10;适用：价格增长相对稳定的项目">
                      线性函数
                    </Tooltip>
                  </Option>
                  <Option value="exponential">
                    <Tooltip title="指数函数：price = startPrice × exp(growthRate × supply)&#10;特点：价格随供应量指数增长&#10;适用：价格增长加速的项目">
                      指数函数
                    </Tooltip>
                  </Option>
                  <Option value="polynomial">
                    <Tooltip title="多项式函数：price = a × supply² + slope × supply + intercept&#10;特点：价格随供应量二次变化&#10;适用：复杂价格变化模式的项目">
                      多项式函数
                    </Tooltip>
                  </Option>
                  <Option value="custom">
                    <Tooltip title="自定义函数：支持用户自定义价格计算方式&#10;特点：最大灵活性&#10;适用：特殊需求的项目">
                      自定义函数
                    </Tooltip>
                  </Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="开启后参数变化时自动重新计算仿真结果">
                    <span>自动计算</span>
                  </Tooltip>
                }
              >
                <Switch
                  checked={autoCalculate}
                  onChange={setAutoCalculate}
                  checkedChildren="开启"
                  unCheckedChildren="关闭"
                />
              </Form.Item>
            </Col>
          </Row>

          {/* 高级参数设置 */}
          <Row>
            <Col span={24}>
              <Button
                type="link"
                icon={<SettingOutlined />}
                onClick={() => setShowAdvancedSettings(!showAdvancedSettings)}
              >
                {showAdvancedSettings ? '隐藏' : '显示'}高级参数设置
              </Button>
            </Col>
          </Row>

          {showAdvancedSettings && (
            <Row gutter={16}>
              <Col span={6}>
                <Form.Item
                  label={
                    <Tooltip title="控制价格曲线的随机波动程度，0为无波动，1为最大波动">
                      <span>波动因子</span>
                    </Tooltip>
                  }
                  name="volatilityFactor"
                >
                  <InputNumber
                    style={{ width: '100%' }}
                    min={0}
                    max={1}
                    step={0.01}
                    precision={2}
                    placeholder="0.1"
                  />
                </Form.Item>
              </Col>
              <Col span={6}>
                <Form.Item
                  label={
                    <Tooltip title="控制分段间的平滑过渡程度，0为无平滑，1为完全平滑">
                      <span>平滑因子</span>
                    </Tooltip>
                  }
                  name="smoothingFactor"
                >
                  <InputNumber
                    style={{ width: '100%' }}
                    min={0}
                    max={1}
                    step={0.01}
                    precision={2}
                    placeholder="0.5"
                  />
                </Form.Item>
              </Col>
              <Col span={6}>
                <Form.Item
                  label={
                    <Tooltip title="是否启用自动优化算法来调整参数">
                      <span>自动优化</span>
                    </Tooltip>
                  }
                  name="autoOptimize"
                  valuePropName="checked"
                >
                  <Switch
                    checkedChildren="开启"
                    unCheckedChildren="关闭"
                  />
                </Form.Item>
              </Col>
            </Row>
          )}

          <Row>
            <Col span={24}>
              <Space>
                <Tooltip title="执行分段联合曲线仿真计算">
                  <Button
                    type="primary"
                    icon={<CalculatorOutlined />}
                    onClick={() => handleSubmit(form.getFieldsValue())}
                    loading={isCalculating}
                  >
                    仿真
                  </Button>
                </Tooltip>
                <Tooltip title="将所有参数重置为默认值并重新计算">
                  <Button
                    icon={<ReloadOutlined />}
                    onClick={handleReset}
                  >
                    重置参数
                  </Button>
                </Tooltip>
                <Tooltip title="根据当前参数计算推荐的参数值">
                  <Button
                    icon={<BulbOutlined />}
                    onClick={handleCalculateRecommendations}
                    disabled={autoCalculate}
                  >
                    计算推荐值
                  </Button>
                </Tooltip>
                <Tooltip title="执行交互式测试，展示不同投入下的铸造和销毁收益">
                  <Button
                    icon={<PlayCircleOutlined />}
                    onClick={handleInteractiveTest}
                  >
                    交互式测试
                  </Button>
                </Tooltip>
              </Space>
            </Col>
          </Row>
        </Form>
      </Card>

      {/* 关键指标展示 */}
      {summary && (
        <Card title="关键指标" style={{ marginBottom: '20px' }}>
          <Row gutter={16}>
            <Col span={4}>
              <Tooltip title="基于当前供应量计算的代币价格">
                <Statistic
                  title="当前价格"
                  value={summary.currentPrice}
                  precision={4}
                  suffix="元"
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="代币的最大供应量上限">
                <Statistic
                  title="总供应量"
                  value={summary.totalSupply}
                  precision={0}
                  formatter={(value) => Number(value).toLocaleString()}
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="最终价格与初始价格的差值">
                <Statistic
                  title="价格区间"
                  value={summary.priceRange}
                  precision={2}
                  suffix="元"
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="分段配置信息">
                <Statistic
                  title="分段配置"
                  value={`${summary.segmentCount}段/${summary.segmentDistribution}`}
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="价格函数类型">
                <Statistic
                  title="价格函数"
                  value={summary.priceFunction}
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="波动因子值">
                <Statistic
                  title="波动因子"
                  value={summary.volatilityFactor}
                  precision={2}
                />
              </Tooltip>
            </Col>
          </Row>
        </Card>
      )}

      {/* 分析数据展示 */}
      {analysisData && (
        <Card title="深度分析" style={{ marginBottom: '20px' }}>
          <Row gutter={16}>
            <Col span={6}>
              <Tooltip title="当前供应量占总供应量的百分比">
                <Statistic
                  title="供应量利用率"
                  value={analysisData.supplyUtilization}
                  precision={1}
                  suffix="%"
                  prefix={
                    <Progress
                      type="circle"
                      percent={analysisData.supplyUtilization}
                      width={40}
                      format={() => ''}
                    />
                  }
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="当前价格在价格区间中的进度百分比">
                <Statistic
                  title="价格进度"
                  value={analysisData.priceProgress}
                  precision={1}
                  suffix="%"
                  prefix={
                    <Progress
                      type="circle"
                      percent={analysisData.priceProgress}
                      width={40}
                      format={() => ''}
                    />
                  }
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="价格变化率最大的供应量点">
                <Statistic
                  title="最优铸造点"
                  value={analysisData.optimalSupply}
                  precision={0}
                  formatter={(value) => Number(value).toLocaleString()}
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="最大价格变化率">
                <Statistic
                  title="最大变化率"
                  value={analysisData.maxChangeRate}
                  precision={8}
                  suffix="元/积分"
                />
              </Tooltip>
            </Col>
          </Row>

          <Divider />

          <Row gutter={16}>
            <Col span={24}>
              <Tooltip title="各分段的配置信息和价格函数">
                <Title level={5}>分段配置详情</Title>
              </Tooltip>
              <Table
                columns={[
                  { 
                    title: '分段', 
                    dataIndex: 'segmentIndex', 
                    key: 'segmentIndex',
                    width: 80,
                  },
                  { 
                    title: '起始供应量', 
                    dataIndex: 'startSupply', 
                    key: 'startSupply',
                    render: (v) => v.toLocaleString(),
                  },
                  { 
                    title: '结束供应量', 
                    dataIndex: 'endSupply', 
                    key: 'endSupply',
                    render: (v) => v.toLocaleString(),
                  },
                  { 
                    title: '价格区间', 
                    dataIndex: 'description', 
                    key: 'description',
                  },
                  { 
                    title: '价格函数', 
                    dataIndex: 'priceFunction', 
                    key: 'priceFunction',
                    width: 300,
                    ellipsis: true,
                  },
                  { 
                    title: '波动率', 
                    dataIndex: 'volatility', 
                    key: 'volatility',
                    render: (v) => v.toFixed(4),
                  }
                ]}
                dataSource={segments}
                pagination={false}
                size="small"
              />
            </Col>
          </Row>
        </Card>
      )}

      {/* 仿真图表 */}
      {dataPoints.length > 0 && (
        <Card title="分段联合曲线仿真结果" style={{ marginBottom: '20px' }}>
          <LindeChartSimple
            data={dataPoints.map(point => ({
              supply: point.supply,
              price: point.price,
              series: '价格曲线',
              group: '分段联合曲线',
            }))}
            height={500}
            width={800}
          />
          <div style={{ marginTop: '10px' }}>
            <Text type="secondary">
              横轴：代币供应量，纵轴：价格（分段联合曲线）
            </Text>
          </div>
        </Card>
      )}

      {/* 交互式测试结果 */}
      {testResults && (
        <Card title="交互式测试结果" style={{ marginBottom: '20px' }}>
          <Row gutter={16}>
            <Col span={12}>
              <Tooltip title="不同劳动价值投入下的铸造收益测试">
                <Title level={5}>铸造测试</Title>
              </Tooltip>
              <Table
                columns={testColumns}
                dataSource={testResults.mintResults}
                pagination={false}
                size="small"
              />
            </Col>
            <Col span={12}>
              <Tooltip title="不同积分销毁量下的法币收益测试">
                <Title level={5}>销毁测试</Title>
              </Tooltip>
              <Table
                columns={burnTestColumns}
                dataSource={testResults.burnResults}
                pagination={false}
                size="small"
              />
            </Col>
          </Row>
        </Card>
      )}

      {/* 数据表格 */}
      {dataPoints.length > 0 && (
        <Card title="价格数据表">
          <Tooltip title="不同供应量下的价格、铸造收益和销毁收益数据">
            <span>价格数据表</span>
          </Tooltip>
          <Table
            columns={columns}
            dataSource={dataPoints}
            rowKey="supply"
            pagination={{
              pageSize: 10,
              showSizeChanger: true,
              showQuickJumper: true,
              showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`,
            }}
            scroll={{ x: 800 }}
            size="small"
          />
        </Card>
      )}
    </div>
  );
}; 