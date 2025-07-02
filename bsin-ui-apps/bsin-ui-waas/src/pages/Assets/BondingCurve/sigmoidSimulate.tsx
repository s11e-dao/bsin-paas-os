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
} from './sigmoidUtils';

const { Title, Text, Paragraph } = Typography;

interface SigmoidSimulateProps {
  refreshTrigger?: number;
}

export default ({ refreshTrigger }: SigmoidSimulateProps) => {
  const [form] = Form.useForm();
  const [simulationData, setSimulationData] = useState<DataPoint[]>([]);
  const [isCalculating, setIsCalculating] = useState(false);
  const [summary, setSummary] = useState<any>(null);
  const [dataPoints, setDataPoints] = useState<any[]>([]);
  const [autoCalculate, setAutoCalculate] = useState(true);
  const [testResults, setTestResults] = useState<any>(null);
  const [analysisData, setAnalysisData] = useState<any>(null);

  // 默认参数
  const defaultParams: SigmoidParams = {
    cap: 21000000,           // 2100万供应上限
    initialPrice: 0.01,      // 初始价格0.01
    finalPrice: 1.0,         // 最终价格1.0
    flexible: 5,             // 理想S曲线值
    currentSupply: 0,        // 当前供应量
  };

  // 计算sigmoid仿真
  const calculateSimulation = (params: SigmoidParams) => {
    setIsCalculating(true);

    try {
      // 验证参数
      const validation = validateSigmoidParams(params);
      if (!validation.valid) {
        message.error(`参数验证失败: ${validation.errors.join(', ')}`);
        setIsCalculating(false);
        return;
      }

      if (validation.warnings.length > 0) {
        message.warning(`参数警告: ${validation.warnings.join(', ')}`);
      }

      // 执行仿真计算
      const result = calculateSigmoidSimulation(params);

      setSimulationData(result.chartData);
      setSummary(result.summary);
      setDataPoints(result.dataPoints);

      // 计算分析数据
      calculateAnalysisData(params, result);

      message.success('Sigmoid仿真计算完成');
    } catch (error) {
      console.error('Sigmoid仿真计算失败:', error);
      message.error('Sigmoid仿真计算失败，请检查参数');
    } finally {
      setIsCalculating(false);
    }
  };

  // 计算分析数据
  const calculateAnalysisData = (params: SigmoidParams, result: SigmoidSimulationResult) => {
    const currentPrice = calculateSigmoidPrice(params, params.currentSupply);
    const midPointPrice = calculateSigmoidPrice(params, params.cap / 2);
    const priceChangeRate = calculatePriceChangeRate(params, params.currentSupply);
    
    // 计算不同供应量下的价格变化
    const supplyPoints = [0, params.cap * 0.25, params.cap * 0.5, params.cap * 0.75, params.cap];
    const pricePoints = supplyPoints.map(supply => ({
      supply,
      price: calculateSigmoidPrice(params, supply),
      changeRate: calculatePriceChangeRate(params, supply)
    }));

    // 计算最优铸造时机
    const optimalTiming = calculateOptimalMintTiming(params, params.cap * 0.5);

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
    calculateSimulation(values);
  };

  // 重置为默认值
  const handleReset = () => {
    form.setFieldsValue(defaultParams);
    calculateSimulation(defaultParams);
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
          calculateSimulation(newValues);
        }, 100);
      }
    }
  };

  // 处理表单值变化
  const handleFormValuesChange = (changedValues: any, allValues: any) => {
    if (autoCalculate) {
      // 延迟执行，避免频繁计算
      setTimeout(() => {
        calculateSimulation(allValues);
      }, 500);
    }
  };

  // 交互式测试功能
  const handleInteractiveTest = () => {
    const values = form.getFieldsValue();
    const testLaborValues = [100, 500, 1000, 5000, 10000];
    const testBurnAmounts = [1000, 5000, 10000, 50000, 100000];
    
    const mintResults = testLaborValues.map(laborValue => ({
      laborValue,
      mintAmount: calculateMintAmount(values, values.currentSupply, laborValue),
      avgPrice: laborValue / calculateMintAmount(values, values.currentSupply, laborValue)
    }));

    const burnResults = testBurnAmounts.map(burnAmount => ({
      burnAmount,
      burnValue: calculateBurnAmount(values, values.currentSupply, burnAmount),
      avgPrice: calculateBurnAmount(values, values.currentSupply, burnAmount) / burnAmount
    }));

    setTestResults({ mintResults, burnResults });
    message.success('交互式测试完成');
  };

  // 组件初始化时计算默认参数
  useEffect(() => {
    form.setFieldsValue(defaultParams);
    calculateSimulation(defaultParams);
  }, []);

  // 监听refreshTrigger变化
  useEffect(() => {
    if (refreshTrigger && refreshTrigger > 0) {
      const values = form.getFieldsValue();
      calculateSimulation(values);
    }
  }, [refreshTrigger]);

  // 表格列定义
  const columns = [
    {
      title: '供应量',
      dataIndex: 'supply',
      key: 'supply',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      width: 120,
      align: 'right' as const,
      render: (value: number) => value.toFixed(4),
    },
    {
      title: '铸造收益',
      dataIndex: 'mintAmount',
      key: 'mintAmount',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
      tooltip: '投入1000元劳动价值获得的积分数量',
    },
    {
      title: '销毁收益',
      dataIndex: 'burnAmount',
      key: 'burnAmount',
      width: 150,
      align: 'right' as const,
      render: (value: number) => value.toFixed(2),
      tooltip: '销毁10000积分获得的法币金额',
    },
  ];

  // 测试结果表格列定义
  const testColumns = [
    {
      title: '劳动价值',
      dataIndex: 'laborValue',
      key: 'laborValue',
      width: 120,
      align: 'right' as const,
      render: (value: number) => `${value}元`,
    },
    {
      title: '获得积分',
      dataIndex: 'mintAmount',
      key: 'mintAmount',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
    },
    {
      title: '平均价格',
      dataIndex: 'avgPrice',
      key: 'avgPrice',
      width: 120,
      align: 'right' as const,
      render: (value: number) => value.toFixed(4),
    },
  ];

  const burnTestColumns = [
    {
      title: '销毁积分',
      dataIndex: 'burnAmount',
      key: 'burnAmount',
      width: 120,
      align: 'right' as const,
      render: (value: number) => formatNumber(value),
    },
    {
      title: '获得法币',
      dataIndex: 'burnValue',
      key: 'burnValue',
      width: 150,
      align: 'right' as const,
      render: (value: number) => `${value.toFixed(2)}元`,
    },
    {
      title: '平均价格',
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
                label="供应上限 (cap)"
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
                label="初始价格 (initialPrice)"
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
                label="最终价格 (finalPrice)"
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
                    <span>拉伸变换值 (flexible)</span>
                    <Tooltip title="4-6为理想S曲线值，越大曲线越陡峭">
                      <BulbOutlined style={{ color: '#1890ff' }} />
                    </Tooltip>
                  </Space>
                }
                name="flexible"
                rules={[{ required: true, message: '请输入拉伸变换值' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  max={10}
                  step={0.1}
                  precision={1}
                  placeholder="5.0"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="当前供应量 (currentSupply)"
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
                label="自动计算"
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
                <Button
                  type="primary"
                  icon={<CalculatorOutlined />}
                  onClick={() => form.submit()}
                  loading={isCalculating}
                >
                  开始仿真
                </Button>
                <Button
                  icon={<ReloadOutlined />}
                  onClick={handleReset}
                >
                  重置参数
                </Button>
                <Button
                  icon={<BulbOutlined />}
                  onClick={handleCalculateRecommendations}
                  disabled={autoCalculate}
                >
                  计算推荐值
                </Button>
                <Button
                  icon={<PlayCircleOutlined />}
                  onClick={handleInteractiveTest}
                >
                  交互式测试
                </Button>
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
              <Statistic
                title="当前价格"
                value={summary.currentPrice}
                precision={4}
                suffix="元"
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="供应上限"
                value={summary.totalSupply}
                precision={0}
                formatter={(value) => formatNumber(Number(value))}
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="价格区间"
                value={summary.priceRange}
                precision={2}
                suffix="元"
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="中点价格"
                value={summary.midPointPrice}
                precision={4}
                suffix="元"
              />
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
            </Col>
            <Col span={8}>
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
            </Col>
            <Col span={8}>
              <Statistic
                title="价格变化率"
                value={analysisData.priceChangeRate}
                precision={6}
                suffix="元/积分"
              />
            </Col>
          </Row>
          
          <Divider />
          
          <Row gutter={16}>
            <Col span={12}>
              <Title level={5}>价格分布</Title>
              <Table
                columns={[
                  { title: '供应量', dataIndex: 'supply', key: 'supply', render: (v) => formatNumber(Math.round(v)) },
                  { title: '价格', dataIndex: 'price', key: 'price', render: (v) => v.toFixed(4) },
                  { title: '变化率', dataIndex: 'changeRate', key: 'changeRate', render: (v) => v.toFixed(6) }
                ]}
                dataSource={analysisData.pricePoints}
                pagination={false}
                size="small"
              />
            </Col>
            <Col span={12}>
              <Title level={5}>最优铸造建议</Title>
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

      {/* 仿真图表 */}
      {simulationData.length > 0 && (
        <Card title="Sigmoid仿真结果图表" style={{ marginBottom: '20px' }}>
          <LindeChartSimple
            data={simulationData}
            height={500}
            width={800}
          />
          <div style={{ marginTop: '10px' }}>
            <Text type="secondary">
              横轴：代币供应量，纵轴：价格/收益
            </Text>
          </div>
        </Card>
      )}

      {/* 交互式测试结果 */}
      {testResults && (
        <Card title="交互式测试结果" style={{ marginBottom: '20px' }}>
          <Row gutter={16}>
            <Col span={12}>
              <Title level={5}>铸造测试</Title>
              <Table
                columns={testColumns}
                dataSource={testResults.mintResults}
                pagination={false}
                size="small"
              />
            </Col>
            <Col span={12}>
              <Title level={5}>销毁测试</Title>
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