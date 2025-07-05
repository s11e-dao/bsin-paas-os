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
  Switch,
  Tooltip,
  Divider,
  Progress,
  Tag,
} from 'antd';
import React, { useState, useEffect } from 'react';
import { CalculatorOutlined, ReloadOutlined, InfoCircleOutlined, BulbOutlined, PlayCircleOutlined } from '@ant-design/icons';
import LindeChartSimple, { DataPoint } from './lindeChartSimple';

const { Title, Text, Paragraph } = Typography;

// 劳动价值释放模型参数接口
interface LaborValueReleaseParams {
  totalLaborValue: number;        // 总劳动价值
  initialReleaseRate: number;     // 初始释放率
  decayFactor: number;           // 衰减系数
  releasePeriods: number;        // 释放周期数
  currentPeriod: number;         // 当前周期
  releaseInterval: number;       // 释放间隔（天）
  autoOptimize: boolean;         // 自动优化
  volatilityFactor: number;      // 波动因子
}

// 释放周期数据接口
interface ReleasePeriodData {
  period: number;
  releaseRate: number;
  releasedValue: number;
  cumulativeReleased: number;
  remainingValue: number;
  periodProgress: number;
  theoreticalValue: number;
  actualValue: number;
}

// 仿真结果接口
interface LaborValueReleaseResult {
  chartData: DataPoint[];
  summary: any;
  periodData: ReleasePeriodData[];
  analysis: any;
}

interface LaborValueReleaseSimulateProps {
  refreshTrigger?: number;
}

export default ({ refreshTrigger }: LaborValueReleaseSimulateProps) => {
  const [form] = Form.useForm();
  const [isCalculating, setIsCalculating] = useState(false);
  const [summary, setSummary] = useState<any>(null);
  const [periodData, setPeriodData] = useState<ReleasePeriodData[]>([]);
  const [autoCalculate, setAutoCalculate] = useState(true);
  const [analysisData, setAnalysisData] = useState<any>(null);

  // 默认参数
  const defaultParams: LaborValueReleaseParams = {
    totalLaborValue: 100000000,    // 1亿劳动价值
    initialReleaseRate: 0.1,       // 初始释放率10%
    decayFactor: 0.95,            // 衰减系数0.95
    releasePeriods: 20,           // 20个释放周期
    currentPeriod: 1,             // 当前第1周期
    releaseInterval: 30,          // 30天间隔
    autoOptimize: true,           // 自动优化
    volatilityFactor: 0.05,       // 波动因子5%
  };

  // 计算劳动价值释放仿真
  const calculateLaborValueRelease = (params: LaborValueReleaseParams): LaborValueReleaseResult => {
    const { 
      totalLaborValue, 
      initialReleaseRate, 
      decayFactor, 
      releasePeriods, 
      currentPeriod,
      volatilityFactor 
    } = params;
    
    const periodData: ReleasePeriodData[] = [];
    let cumulativeReleased = 0;
    
    // 计算每个周期的释放数据
    for (let period = 1; period <= releasePeriods; period++) {
      // 计算当前周期的释放率（考虑衰减）
      const releaseRate = initialReleaseRate * Math.pow(decayFactor, period - 1);
      
      // 计算当前周期应释放的价值
      const theoreticalValue = totalLaborValue * releaseRate;
      
      // 应用波动因子
      const volatility = (Math.random() - 0.5) * 2 * volatilityFactor;
      const actualValue = theoreticalValue * (1 + volatility);
      
      // 确保不超过剩余价值
      const remainingValue = totalLaborValue - cumulativeReleased;
      const releasedValue = Math.min(actualValue, remainingValue);
      
      cumulativeReleased += releasedValue;
      
      // 计算周期进度
      const periodProgress = (period / releasePeriods) * 100;
      
      periodData.push({
        period,
        releaseRate,
        releasedValue,
        cumulativeReleased,
        remainingValue: totalLaborValue - cumulativeReleased,
        periodProgress,
        theoreticalValue,
        actualValue,
      });
    }
    
    // 计算汇总信息
    const totalReleased = cumulativeReleased;
    const releaseEfficiency = (totalReleased / totalLaborValue) * 100;
    const averageReleaseRate = totalReleased / releasePeriods;
    const currentPeriodData = periodData.find(p => p.period === currentPeriod) || periodData[0];
    
    const summary = {
      totalLaborValue,
      totalReleased,
      releaseEfficiency,
      averageReleaseRate,
      currentPeriod,
      currentReleaseRate: currentPeriodData.releaseRate,
      currentReleasedValue: currentPeriodData.releasedValue,
      remainingValue: currentPeriodData.remainingValue,
      periodsCompleted: currentPeriod,
      periodsRemaining: releasePeriods - currentPeriod,
      decayFactor,
      volatilityFactor,
    };
    
    // 生成图表数据
    const chartData: DataPoint[] = periodData.map(period => ({
      supply: period.period,
      price: period.releasedValue,
      series: '释放价值',
      group: '劳动价值释放模型',
    }));
    
    // 计算分析数据
    const analysis = calculateAnalysisData(params, { summary, periodData });
    
    return {
      chartData,
      summary,
      periodData,
      analysis,
    };
  };

  // 计算分析数据
  const calculateAnalysisData = (params: LaborValueReleaseParams, result: any) => {
    const { summary, periodData } = result;
    
    // 计算释放效率趋势
    const efficiencyTrend = periodData.map((period, index) => ({
      period: period.period,
      efficiency: (period.releasedValue / period.theoreticalValue) * 100,
      cumulativeEfficiency: (period.cumulativeReleased / (params.totalLaborValue * (index + 1) / params.releasePeriods)) * 100,
    }));
    
    // 计算最优释放时机
    const maxReleasePeriod = periodData.reduce((max, current) => 
      current.releasedValue > max.releasedValue ? current : max
    );
    
    // 计算释放稳定性
    const releaseValues = periodData.map(p => p.releasedValue);
    const mean = releaseValues.reduce((sum, val) => sum + val, 0) / releaseValues.length;
    const variance = releaseValues.reduce((sum, val) => sum + Math.pow(val - mean, 2), 0) / releaseValues.length;
    const stability = 1 - (Math.sqrt(variance) / mean);
    
    return {
      efficiencyTrend,
      optimalReleasePeriod: maxReleasePeriod.period,
      maxReleaseValue: maxReleasePeriod.releasedValue,
      releaseStability: Math.max(0, stability),
      averageEfficiency: efficiencyTrend.reduce((sum, e) => sum + e.efficiency, 0) / efficiencyTrend.length,
      totalEfficiency: summary.releaseEfficiency,
    };
  };

  // 处理表单提交
  const handleSubmit = (values: LaborValueReleaseParams) => {
    setIsCalculating(true);
    
    try {
      const result = calculateLaborValueRelease(values);
      setSummary(result.summary);
      setPeriodData(result.periodData);
      setAnalysisData(result.analysis);
      
      message.success('劳动价值释放仿真完成');
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
    const recommendations: Partial<LaborValueReleaseParams> = {};
    
    // 根据总劳动价值推荐释放周期数
    if (currentValues.totalLaborValue > 50000000) {
      recommendations.releasePeriods = Math.ceil(currentValues.totalLaborValue / 5000000);
    }
    
    // 根据释放周期数推荐衰减系数
    if (currentValues.releasePeriods > 15) {
      recommendations.decayFactor = 0.97;
    } else if (currentValues.releasePeriods < 10) {
      recommendations.decayFactor = 0.90;
    }
    
    // 根据总价值推荐初始释放率
    if (currentValues.totalLaborValue > 100000000) {
      recommendations.initialReleaseRate = 0.08;
    } else if (currentValues.totalLaborValue < 50000000) {
      recommendations.initialReleaseRate = 0.15;
    }
    
    if (Object.keys(recommendations).length > 0) {
      const newValues = { ...currentValues, ...recommendations };
      form.setFieldsValue(newValues);
      
      const recommendationMessages = [];
      if (recommendations.releasePeriods) {
        recommendationMessages.push(`推荐释放周期数: ${recommendations.releasePeriods}`);
      }
      if (recommendations.decayFactor) {
        recommendationMessages.push(`推荐衰减系数: ${recommendations.decayFactor.toFixed(3)}`);
      }
      if (recommendations.initialReleaseRate) {
        recommendationMessages.push(`推荐初始释放率: ${(recommendations.initialReleaseRate * 100).toFixed(1)}%`);
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
        <Tooltip title="释放周期的序号，从1开始递增">
          <span>周期序号</span>
        </Tooltip>
      ),
      dataIndex: 'period',
      key: 'period',
      width: 100,
      align: 'center' as const,
    },
    {
      title: (
        <Tooltip title="当前周期的释放率，基于衰减系数计算">
          <span>释放率</span>
        </Tooltip>
      ),
      dataIndex: 'releaseRate',
      key: 'releaseRate',
      width: 120,
      align: 'right' as const,
      render: (value: number) => `${(value * 100).toFixed(2)}%`,
    },
    {
      title: (
        <Tooltip title="当前周期实际释放的劳动价值">
          <span>释放价值</span>
        </Tooltip>
      ),
      dataIndex: 'releasedValue',
      key: 'releasedValue',
      width: 150,
      align: 'right' as const,
      render: (value: number) => value.toLocaleString(),
    },
    {
      title: (
        <Tooltip title="累计释放的劳动价值">
          <span>累计释放</span>
        </Tooltip>
      ),
      dataIndex: 'cumulativeReleased',
      key: 'cumulativeReleased',
      width: 150,
      align: 'right' as const,
      render: (value: number) => value.toLocaleString(),
    },
    {
      title: (
        <Tooltip title="剩余未释放的劳动价值">
          <span>剩余价值</span>
        </Tooltip>
      ),
      dataIndex: 'remainingValue',
      key: 'remainingValue',
      width: 150,
      align: 'right' as const,
      render: (value: number) => value.toLocaleString(),
    },
    {
      title: (
        <Tooltip title="释放进度百分比">
          <span>释放进度</span>
        </Tooltip>
      ),
      dataIndex: 'periodProgress',
      key: 'periodProgress',
      width: 120,
      align: 'right' as const,
      render: (value: number) => `${value.toFixed(1)}%`,
    },
  ];

  return (
    <div style={{ padding: '20px' }}>
      <Title level={2}>劳动价值释放模型</Title>

      <Alert
        message="存量递减释放法原理"
        description={
          <div>
            <Paragraph style={{ marginBottom: '8px' }}>
              基于腾讯元宝的存量递减释放法，采用存量递减释放机制，确保劳动价值的有序释放和价值保护。
            </Paragraph>
            <Paragraph style={{ marginBottom: '8px' }}>
              <strong>核心机制：</strong>
            </Paragraph>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li><strong>存量递减释放</strong>：每个周期释放的劳动价值逐渐递减</li>
              <li><strong>衰减系数控制</strong>：通过衰减系数控制释放速度的递减程度</li>
              <li><strong>波动因子</strong>：模拟市场波动对释放价值的影响</li>
              <li><strong>价值保护</strong>：确保总释放价值不超过预设的劳动价值总量</li>
            </ul>
            <Paragraph style={{ marginTop: '8px', marginBottom: '8px' }}>
              <strong>计算公式：</strong>
            </Paragraph>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li><strong>周期释放率</strong>：releaseRate = initialReleaseRate × decayFactor^(period-1)</li>
              <li><strong>理论释放价值</strong>：theoreticalValue = totalLaborValue × releaseRate</li>
              <li><strong>实际释放价值</strong>：actualValue = theoreticalValue × (1 + volatility)</li>
              <li><strong>累计释放</strong>：cumulativeReleased = Σ releasedValue</li>
              <li><strong>剩余价值</strong>：remainingValue = totalLaborValue - cumulativeReleased</li>
            </ul>
            <Paragraph style={{ marginTop: '8px', marginBottom: '8px' }}>
              <strong>应用场景：</strong>
            </Paragraph>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li><strong>早期高激励</strong>：初期释放率较高，激励早期参与者</li>
              <li><strong>后期稳定</strong>：后期释放率递减，保持价值稳定</li>
              <li><strong>风险控制</strong>：通过衰减系数控制释放风险</li>
              <li><strong>市场适应</strong>：通过波动因子模拟市场变化</li>
            </ul>
          </div>
        }
        type="info"
        showIcon
        icon={<InfoCircleOutlined />}
        style={{ marginBottom: '20px' }}
      />

      {/* 参数输入区域 */}
      <Card title="释放参数设置" style={{ marginBottom: '20px' }}>
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
                  <Tooltip title="总的劳动价值存量，决定释放的总量上限">
                    <span>总劳动价值</span>
                  </Tooltip>
                }
                name="totalLaborValue"
                rules={[{ required: true, message: '请输入总劳动价值' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="100000000"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="第一个周期的释放率，影响初期释放强度">
                    <span>初始释放率</span>
                  </Tooltip>
                }
                name="initialReleaseRate"
                rules={[{ required: true, message: '请输入初始释放率' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0.01}
                  max={1}
                  step={0.01}
                  precision={2}
                  placeholder="0.10"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="衰减系数：控制释放率的递减速度&#10;公式：releaseRate = initialReleaseRate × decayFactor^(period-1)&#10;范围：0-1，越小递减越快">
                    <span>衰减系数</span>
                  </Tooltip>
                }
                name="decayFactor"
                rules={[{ required: true, message: '请输入衰减系数' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0.1}
                  max={1}
                  step={0.01}
                  precision={3}
                  placeholder="0.95"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="释放周期的总数，影响释放的持续时间">
                    <span>释放周期数</span>
                  </Tooltip>
                }
                name="releasePeriods"
                rules={[{ required: true, message: '请输入释放周期数' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  max={100}
                  placeholder="20"
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="当前所处的释放周期，影响计算起点">
                    <span>当前周期</span>
                  </Tooltip>
                }
                name="currentPeriod"
                rules={[{ required: true, message: '请输入当前周期' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="1"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="每个释放周期的时间间隔（天）">
                    <span>释放间隔（天）</span>
                  </Tooltip>
                }
                name="releaseInterval"
                rules={[{ required: true, message: '请输入释放间隔' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="30"
                />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                label={
                  <Tooltip title="波动因子：控制释放价值的随机波动&#10;公式：actualValue = theoreticalValue × (1 + volatility)&#10;范围：0-1，0为无波动，1为最大波动">
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
                  placeholder="0.05"
                />
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

          <Row>
            <Col span={24}>
              <Space>
                <Tooltip title="执行劳动价值释放仿真计算">
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
              <Tooltip title="总的劳动价值存量">
                <Statistic
                  title="总劳动价值"
                  value={summary.totalLaborValue}
                  precision={0}
                  formatter={(value) => Number(value).toLocaleString()}
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="累计释放的劳动价值">
                <Statistic
                  title="累计释放"
                  value={summary.totalReleased}
                  precision={0}
                  formatter={(value) => Number(value).toLocaleString()}
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="释放效率百分比">
                <Statistic
                  title="释放效率"
                  value={summary.releaseEfficiency}
                  precision={2}
                  suffix="%"
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="平均每个周期的释放价值">
                <Statistic
                  title="平均释放"
                  value={summary.averageReleaseRate}
                  precision={0}
                  formatter={(value) => Number(value).toLocaleString()}
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="当前周期的释放率">
                <Statistic
                  title="当前释放率"
                  value={summary.currentReleaseRate}
                  precision={4}
                  formatter={(value) => `${(Number(value) * 100).toFixed(2)}%`}
                />
              </Tooltip>
            </Col>
            <Col span={4}>
              <Tooltip title="剩余未释放的劳动价值">
                <Statistic
                  title="剩余价值"
                  value={summary.remainingValue}
                  precision={0}
                  formatter={(value) => Number(value).toLocaleString()}
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
              <Tooltip title="释放效率最高的周期">
                <Statistic
                  title="最优释放周期"
                  value={analysisData.optimalReleasePeriod}
                  suffix="期"
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="单周期最大释放价值">
                <Statistic
                  title="最大释放价值"
                  value={analysisData.maxReleaseValue}
                  precision={0}
                  formatter={(value) => Number(value).toLocaleString()}
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="释放稳定性指标，越高越稳定">
                <Statistic
                  title="释放稳定性"
                  value={analysisData.releaseStability}
                  precision={3}
                  formatter={(value) => `${(Number(value) * 100).toFixed(1)}%`}
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="平均释放效率">
                <Statistic
                  title="平均效率"
                  value={analysisData.averageEfficiency}
                  precision={2}
                  suffix="%"
                />
              </Tooltip>
            </Col>
          </Row>

          <Divider />

          <Row gutter={16}>
            <Col span={24}>
              <Tooltip title="各周期的释放效率趋势">
                <Title level={5}>释放效率趋势</Title>
              </Tooltip>
              <Table
                columns={[
                  { 
                    title: '周期', 
                    dataIndex: 'period', 
                    key: 'period',
                    width: 80,
                  },
                  { 
                    title: '释放效率', 
                    dataIndex: 'efficiency', 
                    key: 'efficiency',
                    render: (v) => `${v.toFixed(2)}%`,
                  },
                  { 
                    title: '累计效率', 
                    dataIndex: 'cumulativeEfficiency', 
                    key: 'cumulativeEfficiency',
                    render: (v) => `${v.toFixed(2)}%`,
                  }
                ]}
                dataSource={analysisData.efficiencyTrend}
                pagination={false}
                size="small"
              />
            </Col>
          </Row>
        </Card>
      )}

      {/* 仿真图表 */}
      {periodData.length > 0 && (
        <Card title="劳动价值释放仿真结果" style={{ marginBottom: '20px' }}>
          <LindeChartSimple
            data={periodData.map(period => ({
              supply: period.period,
              price: period.releasedValue,
              series: '释放价值',
              group: '劳动价值释放模型',
            }))}
            height={500}
            width={800}
          />
          <div style={{ marginTop: '10px' }}>
            <Text type="secondary">
              横轴：释放周期，纵轴：释放价值（存量递减释放法）
            </Text>
          </div>
        </Card>
      )}

      {/* 释放周期详情表格 */}
      {periodData.length > 0 && (
        <Card title="释放周期详情">
          <Tooltip title="各周期的详细释放数据">
            <span>释放周期详情</span>
          </Tooltip>
          <Table
            columns={columns}
            dataSource={periodData}
            rowKey="period"
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