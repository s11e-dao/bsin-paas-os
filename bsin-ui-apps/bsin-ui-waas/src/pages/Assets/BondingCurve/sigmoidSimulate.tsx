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
} from 'antd';
import React, { useState, useEffect } from 'react';
import { CalculatorOutlined, ReloadOutlined, InfoCircleOutlined, BulbOutlined, PlayCircleOutlined, LineChartOutlined } from '@ant-design/icons';
import LindeChartSimple, { DataPoint } from './lindeChartSimple';
import {
  SigmoidParams,
  SigmoidSimulationResult,
  calculateSigmoidSimulation,
  validateSigmoidParams,
  formatNumber,
  getRecommendedSigmoidParams,
  calculateSigmoidPrice,
  calculateMintAmount,
  calculateBurnAmount,
  calculateOptimalMintTiming,
  calculatePriceChangeRate,
  calculatePriceChangeRateAnalytical,
  calculateMultiSigmoidSimulation,
} from './sigmoidUtils';

const { Title, Text, Paragraph } = Typography;

interface SigmoidSimulateProps {
  refreshTrigger?: number;
}

export default ({ refreshTrigger }: SigmoidSimulateProps) => {
  const [form] = Form.useForm();
  const [isCalculating, setIsCalculating] = useState(false);
  const [summary, setSummary] = useState<any>(null);
  const [dataPoints, setDataPoints] = useState<any[]>([]);
  const [autoCalculate, setAutoCalculate] = useState(true);
  const [testResults, setTestResults] = useState<any>(null);
  const [analysisData, setAnalysisData] = useState<any>(null);
  const [flexibleList, setFlexibleList] = useState<number[]>([5]);
  const [multiChartData, setMultiChartData] = useState<DataPoint[]>([]);
  const [hiddenSeries, setHiddenSeries] = useState<string[]>([]);
  const [prevFlexibleList, setPrevFlexibleList] = useState<number[]>([5]);

  // 默认参数
  const defaultParams: SigmoidParams = {
    cap: 21000000,           // 2100万供应上限
    initialPrice: 0.01,      // 初始价格0.01
    finalPrice: 1.0,         // 最终价格1.0
    flexible: 5,             // 理想S曲线值
    currentSupply: 0,        // 当前供应量
  };

  // 多组仿真计算
  const handleMultiSimulate = () => {
    const baseParams = form.getFieldsValue();
    const paramArray = flexibleList.map(flex => ({
      params: { ...baseParams, flexible: flex },
      name: `flexible=${flex}`,
    }));
    const { chartData, summaries, dataPoints } = calculateMultiSigmoidSimulation(paramArray);
    
    // 检查是否有新的flexible值（新增曲线）
    const hasNewFlexible = flexibleList.some(flex => !prevFlexibleList.includes(flex));
    
    // 如果有新的flexible值，重置隐藏状态
    if (hasNewFlexible) {
      setHiddenSeries([]);
    }
    
    setMultiChartData(chartData);
    setPrevFlexibleList([...flexibleList]);
    
    // 使用第一组数据进行分析
    if (summaries.length > 0 && dataPoints.length > 0) {
      const firstGroupParams = paramArray[0].params;
      const firstGroupSummary = summaries[0].summary;
      const firstGroupDataPoints = dataPoints[0].data;
      
      setSummary(firstGroupSummary);
      setDataPoints(firstGroupDataPoints);
      
      // 计算分析数据
      calculateAnalysisData(firstGroupParams, {
        chartData: chartData.filter(d => (d as any).group === paramArray[0].name),
        summary: firstGroupSummary,
        dataPoints: firstGroupDataPoints
      });
    }
    message.success('多组flexible仿真完成');
  };

  // 计算分析数据
  const calculateAnalysisData = (params: SigmoidParams, result: SigmoidSimulationResult) => {
    const currentPrice = calculateSigmoidPrice(params, params.currentSupply);
    const midPointPrice = calculateSigmoidPrice(params, params.cap / 2);
    const priceChangeRate = calculatePriceChangeRateAnalytical(params, params.currentSupply);

    // 计算不同供应量下的价格变化
    const supplyPoints = [0, params.cap * 0.25, params.cap * 0.5, params.cap * 0.75, params.cap];
    const pricePoints = supplyPoints.map(supply => ({
      supply,
      price: calculateSigmoidPrice(params, supply),
      changeRate: calculatePriceChangeRateAnalytical(params, supply)
    }));

    // 计算最优铸造时机
    // const optimalTiming = calculateOptimalMintTiming(params, params.cap * 0.5);
    const optimalTiming = calculateOptimalMintTiming(params, params.cap);

    setAnalysisData({
      currentPrice,
      midPointPrice,
      priceChangeRate,
      pricePoints,
      optimalTiming,
      supplyUtilization: (params.currentSupply / params.cap) * 100,
      priceProgress: ((currentPrice - params.initialPrice) / (params.finalPrice - params.initialPrice)) * 100
    });
  };

  // 处理表单提交
  const handleSubmit = (values: SigmoidParams) => {
    handleMultiSimulate();
  };

  // 重置为默认值
  const handleReset = () => {
    form.setFieldsValue(defaultParams);
    handleMultiSimulate();
  };

  // 计算推荐参数
  const handleCalculateRecommendations = () => {
    const currentValues = form.getFieldsValue();
    const recommendations = getRecommendedSigmoidParams(currentValues);

    if (Object.keys(recommendations).length > 0) {
      const newValues = { ...currentValues, ...recommendations };
      form.setFieldsValue(newValues);

      // 显示推荐值提示
      const recommendationMessages = [];
      if (recommendations.flexible) {
        recommendationMessages.push(`推荐拉伸变换值: ${recommendations.flexible}`);
      }
      if (recommendations.initialPrice) {
        recommendationMessages.push(`推荐初始价格: ${recommendations.initialPrice}`);
      }
      if (recommendations.finalPrice) {
        recommendationMessages.push(`推荐最终价格: ${recommendations.finalPrice}`);
      }

      if (recommendationMessages.length > 0) {
        message.info(`已计算推荐值: ${recommendationMessages.join(', ')}`);
      }

      // 如果自动计算开启，立即执行仿真
      if (autoCalculate) {
        setTimeout(() => {
          handleMultiSimulate();
        }, 100);
      }
    }
  };

  // 处理表单值变化
  const handleFormValuesChange = (changedValues: any, allValues: any) => {
    if (autoCalculate) {
      // 延迟执行，避免频繁计算
      setTimeout(() => {
        handleMultiSimulate();
      }, 500);
    }
  };
  
  // 交互式测试功能
  const handleInteractiveTest = () => {
    // 使用第一组flexible参数进行测试
    const baseParams = form.getFieldsValue();
    const firstFlexible = flexibleList[0] || 5;
    const testParams = { ...baseParams, flexible: firstFlexible };
    
    const testLaborValues = [100, 500, 1000, 5000, 10000];
    const testBurnAmounts = [1000, 5000, 10000, 50000, 100000];

    const mintResults = testLaborValues.map(laborValue => ({
      laborValue,
      mintAmount: calculateMintAmount(testParams, testParams.currentSupply, laborValue),
      avgPrice: laborValue / calculateMintAmount(testParams, testParams.currentSupply, laborValue)
    }));

    const burnResults = testBurnAmounts.map(burnAmount => ({
      burnAmount,
      burnValue: calculateBurnAmount(testParams, testParams.currentSupply, burnAmount),
      avgPrice: calculateBurnAmount(testParams, testParams.currentSupply, burnAmount) / burnAmount
    }));

    setTestResults({ mintResults, burnResults });
    message.success(`交互式测试完成 (使用flexible=${firstFlexible})`);
  };

  // 组件初始化时计算默认参数
  useEffect(() => {
    form.setFieldsValue(defaultParams);
    handleMultiSimulate();
  }, []);

  // 监听refreshTrigger变化
  useEffect(() => {
    if (refreshTrigger && refreshTrigger > 0) {
      handleMultiSimulate();
    }
  }, [refreshTrigger]);

  // 表格列定义
  const columns = [
    {
      title: (
        <Tooltip title="代币的当前供应量，是sigmoid函数计算价格的基础变量，影响价格曲线的位置">
          <span>供应量</span>
        </Tooltip>
      ),
      dataIndex: 'supply',
      key: 'supply',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
    },
    {
      title: (
        <Tooltip title="基于sigmoid函数计算的代币价格：price = initialPrice - (initialPrice - finalPrice) × sigmoid(supply)">
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
        <Tooltip title="投入1000元劳动价值获得的积分数量，基于当前价格计算：mintAmount = laborValue / currentPrice">
          <span>铸造收益</span>
        </Tooltip>
      ),
      dataIndex: 'mintAmount',
      key: 'mintAmount',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
    },
    {
      title: (
        <Tooltip title="销毁10000积分获得的法币金额，基于销毁后的新价格计算：burnValue = burnAmount × newPrice">
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
        <Tooltip title="投入的劳动价值金额（元），用于计算铸造收益">
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
        <Tooltip title="投入劳动价值后获得的积分数量，计算公式：mintAmount = laborValue / currentPrice">
          <span>获得积分</span>
        </Tooltip>
      ),
      dataIndex: 'mintAmount',
      key: 'mintAmount',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
    },
    {
      title: (
        <Tooltip title="劳动价值与获得积分的比值，即平均价格：avgPrice = laborValue / mintAmount">
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
        <Tooltip title="要销毁的积分数量，销毁后供应量会减少，影响价格计算">
          <span>销毁积分</span>
        </Tooltip>
      ),
      dataIndex: 'burnAmount',
      key: 'burnAmount',
      width: 120,
      align: 'right' as const,
      render: (value: number) => formatNumber(value),
    },
    {
      title: (
        <Tooltip title="销毁积分后获得的法币金额，基于销毁后的新价格计算：burnValue = burnAmount × newPrice">
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
        <Tooltip title="获得法币与销毁积分的比值，即平均价格：avgPrice = burnValue / burnAmount">
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
      <Title level={2}>Sigmoid联合曲线仿真系统</Title>

      <Alert
        message="Sigmoid曲线原理"
        description={
          <div>
            <Paragraph style={{ marginBottom: '8px' }}>
              基于sigmoid函数的联合曲线积分铸造模型，实现价格从初始值到最终值的平滑过渡。曲线在中间点附近变化最快，两端变化较慢。
            </Paragraph>
            <Paragraph style={{ marginBottom: '8px' }}>
              <strong>计算公式：</strong>
            </Paragraph>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li>中点：midPoint = cap / 2</li>
              <li>sigmoid变换：melo = flexible × (supply - midPoint) / midPoint</li>
              <li>sigmoid函数：deno = 1 / (1 + exp(-melo))</li>
              <li>当前价格：price = initialPrice - (initialPrice - finalPrice) × deno</li>
            </ul>
            <Paragraph style={{ marginTop: '8px', marginBottom: '8px' }}>
              <strong>价格变化率计算：</strong>
            </Paragraph>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li>使用数值微分方法：Δsupply = supply × 1%</li>
              <li>价格变化率 = (price(supply + Δsupply) - price(supply - Δsupply)) / (2 × Δsupply)</li>
              <li>单位：元/积分，正值表示价格上涨，负值表示价格下跌</li>
            </ul>
          </div>
        }
        type="info"
        showIcon
        icon={<InfoCircleOutlined />}
        style={{ marginBottom: '20px' }}
      />

      {/* 参数输入区域 */}
      <Card title="Sigmoid参数设置" style={{ marginBottom: '20px' }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={defaultParams}
          onValuesChange={handleFormValuesChange}
        >
          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                label={
                  <Tooltip title="代币的最大供应量上限，影响价格曲线的总长度和最终价格点">
                    <span>供应上限 (cap)</span>
                  </Tooltip>
                }
                name="cap"
                rules={[{ required: true, message: '请输入供应上限' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="21000000"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label={
                  <Tooltip title="代币的初始价格，sigmoid曲线的起始价格点">
                    <span>初始价格 (initialPrice)</span>
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
            <Col span={8}>
              <Form.Item
                label={
                  <Tooltip title="代币的最终价格，sigmoid曲线的结束价格点">
                    <span>最终价格 (finalPrice)</span>
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
            <Col span={8}>
              <Form.Item
                label={
                  <Space>
                    <Tooltip title="sigmoid函数的拉伸变换值，控制曲线的陡峭程度：4-6为理想S曲线，越大曲线越陡峭">
                      <span>拉伸变换值组 (flexible)</span>
                    </Tooltip>
                    <Tooltip title="可输入多个flexible值进行对比分析">
                      <BulbOutlined style={{ color: '#1890ff' }} />
                    </Tooltip>
                  </Space>
                }
              >
                <Space>
                  {flexibleList.map((f, idx) => (
                    <InputNumber
                      key={idx}
                      min={1}
                      max={10}
                      step={0.1}
                      value={f}
                      onChange={val => {
                        const arr = [...flexibleList];
                        arr[idx] = val || 1;
                        setFlexibleList(arr);
                      }}
                    />
                  ))}
                  <Button onClick={() => {
                    setFlexibleList([...flexibleList, 5]);
                  }}>添加</Button>
                  {flexibleList.length > 1 && (
                    <Button danger onClick={() => {
                      const newList = flexibleList.slice(0, -1);
                      setFlexibleList(newList);
                      // 如果删除的flexible值在隐藏列表中，也需要更新隐藏状态
                      const removedFlexible = flexibleList[flexibleList.length - 1];
                      const removedSeries = multiChartData
                        .filter(d => (d as any).group?.includes(`flexible=${removedFlexible}`))
                        .map(d => d.series);
                      setHiddenSeries(prev => prev.filter(series => !removedSeries.includes(series)));
                    }}>删除</Button>
                  )}
                </Space>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label={
                  <Tooltip title="当前代币的供应量，是价格计算的基础变量，影响价格曲线的当前位置">
                    <span>当前供应量 (currentSupply)</span>
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
            <Col span={8}>
              <Form.Item
                label={
                  <Tooltip title="开启后参数变化时自动重新计算仿真结果，关闭后需要手动点击仿真按钮">
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
                <Text type="secondary" style={{ marginLeft: '8px' }}>
                  {autoCalculate ? '参数变化时自动计算' : '手动点击计算'}
                </Text>
              </Form.Item>
            </Col>
          </Row>

          <Row>
            <Col span={24}>
              <Space>
                <Tooltip title="执行多组flexible参数的仿真计算，生成对比图表和分析数据">
                  <Button
                    type="primary"
                    icon={<CalculatorOutlined />}
                    onClick={handleMultiSimulate}
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
                <Tooltip title="根据当前参数计算推荐的参数值，仅在自动计算关闭时可用">
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
            <Col span={6}>
              <Tooltip title="基于当前供应量计算的代币价格，使用sigmoid函数：price = initialPrice - (initialPrice - finalPrice) × (1 / (1 + exp(-flexible × (supply - cap/2) / (cap/2))))">
                <Statistic
                  title="当前价格"
                  value={summary.currentPrice}
                  precision={4}
                  suffix="元"
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="代币的最大供应量上限，决定了价格曲线的总长度和最终价格点">
                <Statistic
                  title="供应上限"
                  value={summary.totalSupply}
                  precision={0}
                  formatter={(value) => formatNumber(Number(value))}
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="最终价格与初始价格的差值，决定了价格变化的幅度范围">
                <Statistic
                  title="价格区间"
                  value={summary.priceRange}
                  precision={2}
                  suffix="元"
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="供应量达到50%时的价格，是sigmoid曲线的中点，价格变化最快的位置">
                <Statistic
                  title="中点价格"
                  value={summary.midPointPrice}
                  precision={4}
                  suffix="元"
                />
              </Tooltip>
            </Col>
          </Row>
          <Row gutter={16} style={{ marginTop: '16px' }}>
            <Col span={24}>
              <Alert
                message="曲线特性"
                description={summary.flexibleEffect}
                type="info"
                showIcon
              />
            </Col>
          </Row>
        </Card>
      )}

      {/* 分析数据展示 */}
      {analysisData && (
        <Card title="深度分析" style={{ marginBottom: '20px' }}>
          <Row gutter={16}>
            <Col span={8}>
              <Tooltip title="当前供应量占总供应量的百分比，反映代币的流通程度和价格阶段">
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
            <Col span={8}>
              <Tooltip title="当前价格在价格区间中的进度百分比，反映价格从初始值到最终值的完成度">
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
            <Col span={8}>
              <Tooltip title="使用数值微分计算的价格变化率：当供应量变化1%时，价格的变化量（元/积分）。正值表示价格上涨，负值表示价格下跌">
                <Statistic
                  title="价格变化率"
                  value={analysisData.priceChangeRate}
                  precision={12}
                  suffix="元/积分"
                />
              </Tooltip>
            </Col>
          </Row>

          <Divider />

          <Row gutter={16}>
            <Col span={12}>
              <Tooltip title="不同供应量下的价格分布情况，展示价格随供应量变化的规律">
                <Title level={5}>价格分布</Title>
              </Tooltip>
              <Table
                columns={[
                  { 
                    title: (
                      <Tooltip title="代币的当前供应量，是价格计算的基础变量">
                        <span>供应量</span>
                      </Tooltip>
                    ), 
                    dataIndex: 'supply', 
                    key: 'supply', 
                    render: (v) => formatNumber(Math.round(v)) 
                  },
                  { 
                    title: (
                      <Tooltip title="对应供应量下的代币价格，基于sigmoid函数计算得出">
                        <span>价格</span>
                      </Tooltip>
                    ), 
                    dataIndex: 'price', 
                    key: 'price', 
                    render: (v) => v.toFixed(4) 
                  },
                  { 
                    title: (
                      <Tooltip title="使用数值微分计算的价格变化率：Δprice/Δsupply，反映价格对供应量的敏感度">
                        <span>变化率</span>
                      </Tooltip>
                    ), 
                    dataIndex: 'changeRate', 
                    key: 'changeRate', 
                    render: (v) => v.toFixed(12) 
                  }
                ]}
                dataSource={analysisData.pricePoints}
                pagination={false}
                size="small"
              />
            </Col>
            <Col span={12}>
              <Tooltip title="基于当前价格和供应量的铸造时机建议，帮助用户判断最佳铸造时间">
                <Title level={5}>最优铸造建议</Title>
              </Tooltip>
              <Alert
                message={analysisData.optimalTiming.recommendation}
                description={
                  <div>
                    <p>当前价格: {analysisData.optimalTiming.currentPrice.toFixed(4)}元</p>
                    <p>目标价格: {analysisData.optimalTiming.targetPrice.toFixed(4)}元</p>
                    <p>价格涨幅: <Tag color={analysisData.optimalTiming.priceIncrease > 5 ? 'red' : 'green'}>
                      {analysisData.optimalTiming.priceIncrease.toFixed(2)}%
                    </Tag></p>
                  </div>
                }
                type={analysisData.optimalTiming.priceIncrease > 5 ? 'warning' : 'info'}
                showIcon
              />
            </Col>
          </Row>
        </Card>
      )}
      {/* 多组仿真图表 */}
      {multiChartData.length > 0 && (
        <Card 
          title={
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Tooltip title="不同flexible参数下的价格曲线对比，展示拉伸变换值对价格曲线形状的影响">
                <span>多组flexible仿真结果对比图表</span>
              </Tooltip>
              {hiddenSeries.length > 0 && (
                <Button 
                  size="small" 
                  onClick={() => setHiddenSeries([])}
                  style={{ marginLeft: '8px' }}
                >
                  显示所有曲线
                </Button>
              )}
            </div>
          } 
          style={{ marginBottom: '20px' }}
        >
          <LindeChartSimple
            data={multiChartData}
            height={500}
            width={800}
          />
          <div style={{ marginTop: '10px' }}>
            <Text type="secondary">
              横轴：代币供应量，纵轴：价格/收益（多组flexible参数对比）
            </Text>
            {hiddenSeries.length > 0 && (
              <div style={{ marginTop: '8px' }}>
                <Text type="secondary" style={{ fontSize: '12px' }}>
                  已隐藏: {hiddenSeries.join(', ')}
                </Text>
              </div>
            )}
          </div>
        </Card>
      )}
      {/* 交互式测试结果 */}
      {testResults && (
        <Card title="交互式测试结果" style={{ marginBottom: '20px' }}>
          <Row gutter={16}>
            <Col span={12}>
              <Tooltip title="不同劳动价值投入下的铸造收益测试，展示投入金额与获得积分的对应关系">
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
              <Tooltip title="不同积分销毁量下的法币收益测试，展示销毁积分与获得法币的对应关系">
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
          <Tooltip title="不同供应量下的价格、铸造收益和销毁收益数据，展示完整的sigmoid曲线数据">
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