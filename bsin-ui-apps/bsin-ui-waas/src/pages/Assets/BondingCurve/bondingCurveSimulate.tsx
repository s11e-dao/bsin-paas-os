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
import { CalculatorOutlined, ReloadOutlined, InfoCircleOutlined, BulbOutlined } from '@ant-design/icons';
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
    totalTarget: 21000000,
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

    // 使用完整的推荐参数计算（包含优化）
    const recommendations = getCompleteRecommendedParams(currentValues);

    if (Object.keys(recommendations).length > 0) {
      const newValues = { ...currentValues, ...recommendations };
      form.setFieldsValue(newValues);

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

      if (recommendationMessages.length > 0) {
        message.info(`已计算推荐值: ${recommendationMessages.join(', ')}`);
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
      totalTarget: '总积分目标',
      estimatedLaborValue: '预估劳动价值',
      decayFactor: '衰减系数',
      levelWidth: '档位宽度',
      totalLevels: '档位总数',
      firstLevelReward: '首档奖励'
    };
    return displayNames[paramName] || paramName;
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
      title: '档位序号',
      dataIndex: 'level',
      key: 'level',
      width: 100,
      align: 'center' as const,
    },
    {
      title: '档期奖励',
      dataIndex: 'reward',
      key: 'reward',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
    },
    {
      title: '累计奖励',
      dataIndex: 'cumulativeReward',
      key: 'cumulativeReward',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(Math.round(value)),
    },
    {
      title: '劳动价值',
      dataIndex: 'laborValue',
      key: 'laborValue',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(value),
    },
    {
      title: '累计劳动价值',
      dataIndex: 'cumulativeLaborValue',
      key: 'cumulativeLaborValue',
      width: 150,
      align: 'right' as const,
      render: (value: number) => formatNumber(value),
    },
  ];

  return (
    <div style={{ padding: '20px' }}>
      <Title level={2}>联合曲线仿真系统</Title>

      <Alert
        message="仿真原理"
        description={
          <div>
            <Paragraph style={{ marginBottom: '8px' }}>
              基于衰减系数的积分发放模型，确保总积分发放数量等于目标值。每个档期的奖励按指数衰减，劳动价值与档位宽度成正比。
            </Paragraph>
            <Paragraph style={{ marginBottom: '8px' }}>
              <strong>计算公式：</strong>
            </Paragraph>
            <ul style={{ margin: 0, paddingLeft: '20px' }}>
              <li>首档奖励：R₀ = T × (1 - k)</li>
              <li>档期奖励：Rₙ = R₀ × k^(n-1)</li>
              <li>劳动价值：C = δC × n</li>
              <li>理论总积分：ΣR = T / (1 - k)</li>
              <li>档位宽度：δC = Tv / N (N为档位总数)</li>
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
                label="总积分目标 (T)"
                name="totalTarget"
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
                label="预估劳动价值 (Tv)"
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
                    <Tooltip title="留空可自动计算">
                      <BulbOutlined style={{ color: '#1890ff' }} />
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
                    <Tooltip title="留空可自动计算">
                      <BulbOutlined style={{ color: '#1890ff' }} />
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
                    <Tooltip title="留空可自动计算">
                      <BulbOutlined style={{ color: '#1890ff' }} />
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
                    <Tooltip title="留空可自动计算">
                      <BulbOutlined style={{ color: '#1890ff' }} />
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
                  onClick={handleAutoCalculate}
                  disabled={!autoCalculate}
                >
                  计算推荐值
                </Button>
                <Switch
                  checked={autoCalculate}
                  onChange={setAutoCalculate}
                  checkedChildren="自动计算"
                  unCheckedChildren="手动计算"
                />
                <Text type="secondary">
                  {autoCalculate ? '参数变化时自动计算推荐值' : '手动点击计算推荐值'}
                </Text>
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
                <Statistic
                  title="推导档位宽度"
                  value={calculatedParams.calculatedLevelWidth}
                  precision={0}
                  formatter={(value) => formatNumber(Number(value))}
                />
              </Col>
            )}
            {calculatedParams.calculatedTotalLevels && (
              <Col span={6}>
                <Statistic
                  title="推导档位总数"
                  value={calculatedParams.calculatedTotalLevels}
                  suffix="档"
                />
              </Col>
            )}
            {calculatedParams.calculatedFirstLevelReward && (
              <Col span={6}>
                <Statistic
                  title="推导首档奖励"
                  value={calculatedParams.calculatedFirstLevelReward}
                  precision={0}
                  formatter={(value) => formatNumber(Number(value))}
                />
              </Col>
            )}
            {calculatedParams.calculatedDecayFactor && (
              <Col span={6}>
                <Statistic
                  title="推导衰减系数"
                  value={calculatedParams.calculatedDecayFactor}
                  precision={4}
                />
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
              <Statistic
                title="首档奖励 (R₀)"
                value={summary.firstLevelReward}
                precision={0}
                formatter={(value) => formatNumber(Number(value))}
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="档位总数"
                value={summary.totalLevels}
                suffix="档"
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="累计发放积分"
                value={summary.totalReward}
                precision={0}
                formatter={(value) => formatNumber(Number(value))}
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="积分释放达成率"
                value={summary.targetAchievement}
                precision={2}
                suffix="%"
              />
            </Col>
          </Row>
          <Row gutter={16} style={{ marginTop: '16px' }}>
            <Col span={6}>
              <Statistic
                title="累计劳动价值"
                value={summary.totalLaborValue}
                precision={0}
                formatter={(value) => formatNumber(Number(value))}
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="劳动价值达成率"
                value={summary.laborValueAchievement}
                precision={2}
                suffix="%"
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="理论总积分（无限档期）"
                value={calculateTheoreticalTotal(form.getFieldsValue())}
                precision={0}
                formatter={(value) => formatNumber(Number(value))}
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="实际/理论比率"
                value={(summary.totalReward / calculateTheoreticalTotal(form.getFieldsValue())) * 100}
                precision={2}
                suffix="%"
              />
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
