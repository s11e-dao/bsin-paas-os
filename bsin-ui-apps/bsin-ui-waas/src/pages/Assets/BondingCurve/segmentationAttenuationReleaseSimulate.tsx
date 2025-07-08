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
} from 'antd';
import React, { useState, useEffect } from 'react';
import { CalculatorOutlined, ReloadOutlined, InfoCircleOutlined, BulbOutlined, ExperimentOutlined } from '@ant-design/icons';
import LindeChartSimple, { DataPoint } from './lindeChartSimple';
import {
  SimulationParams,
  LevelData,
  calculateBondingCurveSimulation,
  validateSimulationParams,
  formatNumber,
  calculateTheoreticalTotal,
  getRecommendedParams,
  getCompleteRecommendedParams,
  optimizeSimulationParams,
  recalculateParamsBasedOnChange,
  validateParamChange,
} from './simulationUtils';
import { testOptimizationLogic } from './testOptimization';

const { Title, Text, Paragraph } = Typography;

interface BondingCurveMintSimulateProps {
  refreshTrigger?: number;
}

export default ({ refreshTrigger }: BondingCurveMintSimulateProps) => {
  const [form] = Form.useForm();
  const [simulationData, setSimulationData] = useState<DataPoint[]>([]);
  const [levelData, setLevelData] = useState<LevelData[]>([]);
  const [isCalculating, setIsCalculating] = useState(false);
  const [summary, setSummary] = useState<any>(null);
  const [calculatedParams, setCalculatedParams] = useState<{
    calculatedLevelWidth?: number;
    calculatedTotalLevels?: number;
    calculatedFirstLevelReward?: number;
    calculatedDecayFactor?: number;
  } | null>(null);
  const [autoCalculate, setAutoCalculate] = useState(true);

  // 默认参数
  const defaultParams: SimulationParams = {
    totalTargetToken: 21000000,
    levelWidth: 2100000,
    decayFactor: 0.9975,
    estimatedLaborValue: 105000000, // 新增：预估劳动价值
  };

  // 计算联合曲线仿真
  const calculateSimulation = (params: SimulationParams) => {
    setIsCalculating(true);

    try {
      // 验证参数
      const validation = validateSimulationParams(params);
      if (!validation.valid) {
        message.error(`参数验证失败: ${validation.errors.join(', ')}`);
        setIsCalculating(false);
        return;
      }

      // 执行仿真计算
      const result = calculateBondingCurveSimulation(params);

      setLevelData(result.levels);
      setSimulationData(result.chartData);
      setSummary(result.summary);
      setCalculatedParams(result.calculatedParams);

      message.success('仿真计算完成');
    } catch (error) {
      console.error('仿真计算失败:', error);
      message.error('仿真计算失败，请检查参数');
    } finally {
      setIsCalculating(false);
    }
  };

  // 处理表单提交
  const handleSubmit = (values: SimulationParams) => {
    calculateSimulation(values);
  };

  // 重置为默认值
  const handleReset = () => {
    form.setFieldsValue(defaultParams);
    calculateSimulation(defaultParams);
  };

  // 自动计算推荐参数
  const handleAutoCalculate = () => {
    const currentValues = form.getFieldsValue();

    // 计算当前参数下的仿真结果（用于对比）
    let currentResult = null;
    try {
      currentResult = calculateBondingCurveSimulation(currentValues);
    } catch (error) {
      // 如果当前参数无法计算，继续执行推荐值计算
    }

    // 使用完整的推荐参数计算（包含优化）
    const recommendations = getCompleteRecommendedParams(currentValues);

    if (Object.keys(recommendations).length > 0) {
      const newValues = { ...currentValues, ...recommendations };
      form.setFieldsValue(newValues);

      // 计算优化后的仿真结果
      let optimizedResult = null;
      try {
        optimizedResult = calculateBondingCurveSimulation(newValues);
      } catch (error) {
        console.error('优化后参数计算失败:', error);
      }

      // 显示推荐值提示
      const recommendationMessages = [];
      if (recommendations.levelWidth) {
        recommendationMessages.push(`推荐档位宽度: ${formatNumber(recommendations.levelWidth)}`);
      }
      if (recommendations.totalLevels) {
        recommendationMessages.push(`推荐档位总数: ${recommendations.totalLevels}`);
      }
      if (recommendations.firstLevelReward) {
        recommendationMessages.push(`推荐首档奖励: ${formatNumber(recommendations.firstLevelReward)}`);
      }
      if (recommendations.decayFactor) {
        recommendationMessages.push(`推荐衰减系数: ${recommendations.decayFactor.toFixed(4)}`);
      }

      // 显示优化效果对比
      let optimizationMessage = '';
      if (currentResult && optimizedResult) {
        const currentTargetAchievement = currentResult.summary.targetAchievement;
        const currentLaborValueAchievement = currentResult.summary.laborValueAchievement;
        const optimizedTargetAchievement = optimizedResult.summary.targetAchievement;
        const optimizedLaborValueAchievement = optimizedResult.summary.laborValueAchievement;

        optimizationMessage = `优化效果: 积分释放达成率 ${currentTargetAchievement.toFixed(2)}% → ${optimizedTargetAchievement.toFixed(2)}%, 劳动价值达成率 ${currentLaborValueAchievement.toFixed(2)}% → ${optimizedLaborValueAchievement.toFixed(2)}%`;
      }

      if (recommendationMessages.length > 0) {
        const fullMessage = optimizationMessage 
          ? `${recommendationMessages.join(', ')} | ${optimizationMessage}`
          : recommendationMessages.join(', ');
        message.success(`已计算推荐值: ${fullMessage}`);
      }

      // 如果自动计算开关打开，立即执行仿真
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
        // 找出用户修改的参数
        const changedParam = Object.keys(changedValues)[0] as keyof SimulationParams;
        const changedValue = changedValues[changedParam];
        
        if (changedParam && changedValue !== undefined && changedValue !== null) {
          // 验证参数修改的合理性
          const validation = validateParamChange(changedParam, changedValue, allValues);
          
          if (!validation.valid) {
            message.error(`参数验证失败: ${validation.errors.join(', ')}`);
            return;
          }
          
          if (validation.warnings.length > 0) {
            message.warning(`参数警告: ${validation.warnings.join(', ')}`);
          }
          
          // 根据用户修改的参数重新计算其他参数的推荐值
          const recommendations = recalculateParamsBasedOnChange(changedParam, changedValue, allValues);
          
          if (Object.keys(recommendations).length > 0) {
            // 更新表单值（排除用户刚修改的参数）
            const newValues = { ...allValues };
            Object.keys(recommendations).forEach(key => {
              if (key !== changedParam) {
                newValues[key] = recommendations[key as keyof SimulationParams];
              }
            });
            
            form.setFieldsValue(newValues);
            
            // 显示推荐值提示
            const recommendationMessages = [];
            if (recommendations.levelWidth && recommendations.levelWidth !== allValues.levelWidth) {
              recommendationMessages.push(`推荐档位宽度: ${formatNumber(recommendations.levelWidth)}`);
            }
            if (recommendations.totalLevels && recommendations.totalLevels !== allValues.totalLevels) {
              recommendationMessages.push(`推荐档位总数: ${recommendations.totalLevels}`);
            }
            if (recommendations.firstLevelReward && recommendations.firstLevelReward !== allValues.firstLevelReward) {
              recommendationMessages.push(`推荐首档奖励: ${formatNumber(recommendations.firstLevelReward)}`);
            }
            if (recommendations.decayFactor && recommendations.decayFactor !== allValues.decayFactor) {
              recommendationMessages.push(`推荐衰减系数: ${recommendations.decayFactor.toFixed(4)}`);
            }
            
            if (recommendationMessages.length > 0) {
              message.info(`已根据${getParamDisplayName(changedParam)}重新计算: ${recommendationMessages.join(', ')}`);
            }
            
            // 执行仿真计算
            setTimeout(() => {
              calculateSimulation(newValues);
            }, 100);
          }
        }
      }, 500);
    }
  };

  // 获取参数显示名称
  const getParamDisplayName = (paramName: keyof SimulationParams): string => {
    const displayNames: Record<keyof SimulationParams, string> = {
      totalTargetToken: '总积分目标',
      estimatedLaborValue: '预估劳动价值',
      decayFactor: '衰减系数',
      levelWidth: '档位宽度',
      totalLevels: '档位总数',
      firstLevelReward: '首档奖励'
    };
    return displayNames[paramName] || paramName;
  };

  // 处理测试优化逻辑
  const handleTestOptimization = () => {
    try {
      console.log('=== 开始测试优化逻辑 ===');
      testOptimizationLogic();
      message.success('测试完成，请查看浏览器控制台输出');
    } catch (error) {
      console.error('测试失败:', error);
      message.error('测试失败，请查看浏览器控制台错误信息');
    }
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
      title: (
        <Tooltip title="档位在联合曲线中的序号，从1开始递增。档位序号决定了该档位的奖励计算，奖励 = 首档奖励 × 衰减系数^(档位序号-1)。档位序号越大，奖励越少，体现了衰减机制。">
          <span>档位序号 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} /></span>
        </Tooltip>
      ),
      dataIndex: 'level',
      key: 'level',
      width: 100,
      align: 'center' as const,
    },
    {
      title: (
        <Tooltip title="当前档位的积分奖励数量。计算公式：当期奖励 = 首档奖励 × 衰减系数^(档位序号-1)。随着档位序号增加，档期奖励逐渐减少，体现了激励衰减机制。">
          <span>档期奖励 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} /></span>
        </Tooltip>
      ),
      dataIndex: 'reward',
      key: 'reward',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
    },
    {
      title: (
        <Tooltip title="从第1档到当前档位的累计积分奖励总和。计算公式：累计奖励 = Σ(档期奖励)。这个指标反映了系统到当前档位为止的总积分发放量，用于监控积分释放进度。">
          <span>累计奖励 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} /></span>
        </Tooltip>
      ),
      dataIndex: 'cumulativeReward',
      key: 'cumulativeReward',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
    },
    {
      title: (
        <Tooltip title="当前档位对应的劳动价值区间。计算公式：劳动价值 = 档位宽度 × 档位序号。这个值表示触发该档位奖励所需的劳动价值阈值，反映了用户贡献与奖励的对应关系。">
          <span>劳动价值 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} /></span>
        </Tooltip>
      ),
      dataIndex: 'laborValue',
      key: 'laborValue',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(value),
    },
    {
      title: (
        <Tooltip title="从第1档到当前档位的累计劳动价值总和。计算公式：累计劳动价值 = Σ(劳动价值)。这个指标反映了系统到当前档位为止的总劳动价值捕获量，用于评估用户贡献的总体水平。">
          <span>累计劳动价值 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} /></span>
        </Tooltip>
      ),
      dataIndex: 'cumulativeLaborValue',
      key: 'cumulativeLaborValue',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(value),
    },
  ];

  return (
    <div style={{ padding: '20px' }}>
      <Title level={2}>RWA 联合曲线价值模型</Title>

      <Alert
        message="仿真原理"
        description={
          <div>
            <Paragraph style={{ marginBottom: '8px' }}>
            <li>本模型基于 Token Bonding Curve（联合曲线） 理论，将 RWA 产业中用户的劳动与数据贡献映射为曲线价值 C，按固定档位触发积分 rₙ 发放，并采用可调指数衰减系数确保初期高激励、后期平滑递减，从而实现总发放积分 T = 2.1 × 10^8 的精准控制</li> 
            <li>基于衰减系数的劳动价值与数据贡献转换：参与者的工作量、数据上链、社区治理等行为，经算法或 Oracles 转换为联合曲线价值:C（单位：贡献分）。</li>
            <li>价值累计：随着平台运行，所有贡献累计的曲线价值 C 不断增长。</li>
            <li>保证早期激励最强，后期自然递减,同时，引入生态利润回购与销毁机制，以项目收益为积分价值提供稳定托底，保障通缩与价值可持续增长。</li>
            </Paragraph>
            <Paragraph style={{ marginBottom: '8px' }}>
              <strong>计算公式：</strong>
            </Paragraph>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li>预估捕获劳动价值：Tv = 105000000 (法币价值)</li> 
              <li>总积分发放目标：T = 2.1 × 10^8</li>
              <li>档位宽度：ΔC = 2.1 × 10^7 = Tv / N </li>
              <li>档位索引：n = ⌈C/ΔC⌉</li>
              <li>首档奖励：R₀ = T × (1 - k) = 2.1 × 10^8 × (1 - 0.9975) = 5250000</li>
              <li>衰减系数：k = 0.9975 （每档递减 0.25% 可调整：激励衰减：平滑指数）</li>
              <li>当期奖励：rₙ = R₀ × k^(n-1)</li>
              <li>累计发放：Sₙ = Σᵢ₌₁ⁿ rᵢ = R₀ × (1 - k^n) / (1 - k)</li>
            </ul>
          </div>
        }
        type="info"
        showIcon
        icon={<InfoCircleOutlined />}
        style={{ marginBottom: '20px' }}
      />

      {/* 参数输入区域 */}
      <Card title="仿真参数设置" style={{ marginBottom: '20px' }}>
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
                  <Space>
                    <span>总积分目标 (T)</span>
                    <Tooltip title="系统设计的总积分发放目标，用于控制整个生态系统的积分总量。计算公式：T = 2.1 × 10^8。这个参数决定了整个联合曲线模型的积分上限，影响所有档位的奖励计算。建议根据项目规模和预期用户数量合理设定。">
                      <InfoCircleOutlined style={{ color: '#1890ff' }} />
                    </Tooltip>
                  </Space>
                }
                name="totalTargetToken"
                rules={[{ required: true, message: '请输入总积分目标' }]}
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
                  <Space>
                    <span>预估劳动价值 (Tv)</span>
                    <Tooltip title="预估的整个生态系统中用户劳动价值总量，用于计算档位宽度。计算公式：Tv = 105000000（法币价值）。这个参数反映了平台预期的用户贡献总量，包括工作量、数据上链、社区治理等行为的价值转换。档位宽度 = Tv / 档位总数。">
                      <InfoCircleOutlined style={{ color: '#1890ff' }} />
                    </Tooltip>
                  </Space>
                }
                name="estimatedLaborValue"
                rules={[{ required: true, message: '请输入预估劳动价值' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="105000000"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label={
                  <Space>
                    <span>衰减系数 (k)</span>
                    <Tooltip title="控制每档奖励递减的系数，范围0-1。计算公式：k = 0.9975（每档递减0.25%）。当期奖励 = 首档奖励 × k^(档位序号-1)。较小的k值导致奖励快速衰减，较大的k值使奖励更平滑。建议值0.995-0.999，可根据激励策略调整。留空可自动计算最优值。">
                      <InfoCircleOutlined style={{ color: '#1890ff' }} />
                    </Tooltip>
                  </Space>
                }
                name="decayFactor"
                rules={[
                  // { required: true, message: '请输入衰减系数' },
                  { type: 'number', min: 0, max: 1, message: '衰减系数应在0-1之间' }
                ]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  max={1}
                  step={0.0001}
                  precision={4}
                  placeholder="自动计算"
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                label={
                  <Space>
                    <span>档位宽度 (δC)</span>
                    <Tooltip title="每个档位对应的劳动价值区间宽度。计算公式：δC = Tv / N = 预估劳动价值 / 档位总数。档位索引 = ⌈当前劳动价值 / 档位宽度⌉。较大的档位宽度意味着需要更多劳动价值才能触发下一档奖励，较小的档位宽度使奖励更频繁。留空可自动计算最优值。">
                      <InfoCircleOutlined style={{ color: '#1890ff' }} />
                    </Tooltip>
                  </Space>
                }
                name="levelWidth"
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="自动计算"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label={
                  <Space>
                    <span>档位总数 (N)</span>
                    <Tooltip title="整个联合曲线模型的总档位数。计算公式：N = Tv / δC = 预估劳动价值 / 档位宽度。档位总数决定了奖励发放的精细度，较多的档位使奖励更平滑，较少的档位使奖励更集中。建议根据预期用户活跃度和劳动价值分布合理设定。留空可自动计算最优值。">
                      <InfoCircleOutlined style={{ color: '#1890ff' }} />
                    </Tooltip>
                  </Space>
                }
                name="totalLevels"
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="自动计算"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label={
                  <Space>
                    <span>首档奖励 (R₀)</span>
                    <Tooltip title="第一个档位的积分奖励数量。计算公式：R₀ = T × (1 - k) = 总积分目标 × (1 - 衰减系数)。首档奖励是所有后续档位奖励的基准，后续档位奖励 = 首档奖励 × k^(档位序号-1)。较大的首档奖励提供更强的初期激励，但可能导致后期奖励不足。留空可自动计算最优值。">
                      <InfoCircleOutlined style={{ color: '#1890ff' }} />
                    </Tooltip>
                  </Space>
                }
                name="firstLevelReward"
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="自动计算"
                />
              </Form.Item>
            </Col>
          </Row>

          <Row>
            <Col span={24}>
              <Space>
                <Tooltip title="根据当前设置的参数执行联合曲线仿真计算，生成档位详情、图表数据和关键指标。计算过程包括档位奖励计算、累计值统计、达成率分析等。">
                  <Button
                    type="primary"
                    icon={<CalculatorOutlined />}
                    onClick={() => form.submit()}
                    loading={isCalculating}
                  >
                    开始仿真
                  </Button>
                </Tooltip>
                <Tooltip title="将所有参数重置为默认值并重新执行仿真计算。默认参数经过优化设计，适合大多数场景使用。">
                  <Button
                    icon={<ReloadOutlined />}
                    onClick={handleReset}
                  >
                    重置参数
                  </Button>
                </Tooltip>
                <Tooltip title="优先保证积分释放达成率=100%，其次保证累计劳动价值=100%。系统会自动调整首档奖励、档位总数、档位宽度、衰减系数这四个参数，使用迭代优化算法精确计算最优参数组合，确保两个达成率都尽可能接近100%。">
                  <Button
                    icon={<BulbOutlined />}
                    onClick={handleAutoCalculate}
                    disabled={!autoCalculate}
                  >
                    计算推荐值
                  </Button>
                </Tooltip>
                <Tooltip title="开启后，当您修改任何参数时，系统会自动计算其他参数的推荐值并更新表单。关闭后需要手动点击'计算推荐值'按钮。">
                  <Switch
                    checked={autoCalculate}
                    onChange={setAutoCalculate}
                    checkedChildren="自动计算"
                    unCheckedChildren="手动计算"
                  />
                </Tooltip>
                <Text type="secondary">
                  {autoCalculate ? '参数变化时自动计算推荐值' : '手动点击计算推荐值'}
                </Text>
                <Tooltip title="运行优化逻辑测试，验证算法正确性。测试结果将输出到浏览器控制台，用于诊断优化问题。">
                  <Button
                    icon={<ExperimentOutlined />}
                    onClick={handleTestOptimization}
                    type="dashed"
                  >
                    测试优化逻辑
                  </Button>
                </Tooltip>
              </Space>
            </Col>
          </Row>
        </Form>
      </Card>

      {/* 计算推导的参数展示 */}
      {calculatedParams && Object.keys(calculatedParams).length > 0 && (
        <Card title="计算推导的参数" style={{ marginBottom: '20px' }}>
          <Row gutter={16}>
            {calculatedParams.calculatedLevelWidth && (
              <Col span={6}>
                <Tooltip title="根据预估劳动价值和档位总数自动计算的档位宽度。计算公式：推导档位宽度 = 预估劳动价值 / 档位总数。这个值确保档位设置能够覆盖预期的劳动价值范围，使奖励发放更加合理。">
                  <Statistic
                    title={
                      <span>
                        推导档位宽度 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                      </span>
                    }
                    value={calculatedParams.calculatedLevelWidth}
                    precision={0}
                    formatter={(value) => formatNumber(Number(value))}
                  />
                </Tooltip>
              </Col>
            )}
            {calculatedParams.calculatedTotalLevels && (
              <Col span={6}>
                <Tooltip title="根据预估劳动价值和档位宽度自动计算的档位总数。计算公式：推导档位总数 = 预估劳动价值 / 档位宽度。这个值确保档位数量能够合理分配劳动价值区间，使奖励发放更加精细。">
                  <Statistic
                    title={
                      <span>
                        推导档位总数 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                      </span>
                    }
                    value={calculatedParams.calculatedTotalLevels}
                    suffix="档"
                  />
                </Tooltip>
              </Col>
            )}
            {calculatedParams.calculatedFirstLevelReward && (
              <Col span={6}>
                <Tooltip title="根据总积分目标和衰减系数自动计算的首档奖励。计算公式：推导首档奖励 = 总积分目标 × (1 - 衰减系数)。这个值确保首档奖励能够为后续档位提供合适的基准，使整个激励体系更加平衡。">
                  <Statistic
                    title={
                      <span>
                        推导首档奖励 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                      </span>
                    }
                    value={calculatedParams.calculatedFirstLevelReward}
                    precision={0}
                    formatter={(value) => formatNumber(Number(value))}
                  />
                </Tooltip>
              </Col>
            )}
            {calculatedParams.calculatedDecayFactor && (
              <Col span={6}>
                <Tooltip title="根据总积分目标和首档奖励自动计算的衰减系数。计算公式：推导衰减系数 = 1 - (首档奖励 / 总积分目标)。这个值确保衰减机制能够合理分配积分，使激励效果更加可持续。">
                  <Statistic
                    title={
                      <span>
                        推导衰减系数 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                      </span>
                    }
                    value={calculatedParams.calculatedDecayFactor}
                    precision={4}
                  />
                </Tooltip>
              </Col>
            )}
          </Row>
        </Card>
      )}

      {/* 关键指标展示 */}
      {summary && (
        <Card title="关键指标" style={{ marginBottom: '20px' }}>
          <Row gutter={16}>
            <Col span={6}>
              <Tooltip title="第一个档位的积分奖励数量，作为所有后续档位奖励的基准。计算公式：R₀ = T × (1 - k) = 总积分目标 × (1 - 衰减系数)。首档奖励决定了整个激励体系的强度，较大的首档奖励提供更强的初期激励。">
                <Statistic
                  title={
                    <span>
                      首档奖励 (R₀) <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                    </span>
                  }
                  value={summary.firstLevelReward}
                  precision={0}
                  formatter={(value) => formatNumber(Number(value))}
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="整个联合曲线模型的总档位数。计算公式：N = Tv / δC = 预估劳动价值 / 档位宽度。档位总数决定了奖励发放的精细度，较多的档位使奖励更平滑，较少的档位使奖励更集中。">
                <Statistic
                  title={
                    <span>
                      档位总数 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                    </span>
                  }
                  value={summary.totalLevels}
                  suffix="档"
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="从第1档到最后一档的累计积分奖励总和。计算公式：累计发放积分 = Σ(档期奖励)。这个指标反映了系统实际发放的总积分量，用于监控积分释放进度和评估激励效果。">
                <Statistic
                  title={
                    <span>
                      累计发放积分 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                    </span>
                  }
                  value={summary.totalReward}
                  precision={0}
                  formatter={(value) => formatNumber(Number(value))}
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="实际发放积分与目标积分的比率。计算公式：积分释放达成率 = (累计发放积分 / 总积分目标) × 100%。这个指标反映了积分释放的完成度，接近100%表示积分释放接近目标。">
                <Statistic
                  title={
                    <span>
                      积分释放达成率 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                    </span>
                  }
                  value={summary.targetAchievement}
                  precision={2}
                  suffix="%"
                />
              </Tooltip>
            </Col>
          </Row>
          <Row gutter={16} style={{ marginTop: '16px' }}>
            <Col span={6}>
              <Tooltip title="从第1档到最后一档的累计劳动价值总和。计算公式：累计劳动价值 = Σ(劳动价值)。这个指标反映了系统捕获的总劳动价值量，用于评估用户贡献的总体水平和生态系统的活跃度。">
                <Statistic
                  title={
                    <span>
                      累计劳动价值 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                    </span>
                  }
                  value={summary.totalLaborValue}
                  precision={0}
                  formatter={(value) => formatNumber(Number(value))}
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="实际捕获劳动价值与预估劳动价值的比率。计算公式：劳动价值达成率 = (累计劳动价值 / 预估劳动价值) × 100%。这个指标反映了劳动价值捕获的完成度，用于评估用户参与度和贡献水平。">
                <Statistic
                  title={
                    <span>
                      劳动价值达成率 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                    </span>
                  }
                  value={summary.laborValueAchievement}
                  precision={2}
                  suffix="%"
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="在无限档期情况下的理论积分总量。计算公式：理论总积分 = 首档奖励 / (1 - 衰减系数)。这个指标反映了衰减机制下的理论积分上限，用于评估模型的长期可持续性。">
                <Statistic
                  title={
                    <span>
                      理论总积分（无限档期） <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                    </span>
                  }
                  value={calculateTheoreticalTotal(form.getFieldsValue())}
                  precision={0}
                  formatter={(value) => formatNumber(Number(value))}
                />
              </Tooltip>
            </Col>
            <Col span={6}>
              <Tooltip title="实际发放积分与理论总积分的比率。计算公式：实际/理论比率 = (累计发放积分 / 理论总积分) × 100%。这个指标反映了有限档期对积分释放的影响，比率越高说明档期设置越合理。">
                <Statistic
                  title={
                    <span>
                      实际/理论比率 <InfoCircleOutlined style={{ fontSize: '12px', color: '#1890ff' }} />
                    </span>
                  }
                  value={(summary.totalReward / calculateTheoreticalTotal(form.getFieldsValue())) * 100}
                  precision={2}
                  suffix="%"
                />
              </Tooltip>
            </Col>
          </Row>
        </Card>
      )}

      {/* 仿真图表 */}
      {simulationData.length > 0 && (
        <Card title="仿真结果图表" style={{ marginBottom: '20px' }}>
          <LindeChartSimple
            data={simulationData}
            height={500}
            width={800}
          />
          <div style={{ marginTop: '10px' }}>
            <Text type="secondary">
              横轴：劳动价值（档位宽度 × 档位序号），纵轴：积分奖励/劳动价值
            </Text>
          </div>
        </Card>
      )}

      {/* 档位详情表格 */}
      {levelData.length > 0 && (
        <Card title="档位详情">
          <Table
            columns={columns}
            dataSource={levelData}
            rowKey="level"
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
