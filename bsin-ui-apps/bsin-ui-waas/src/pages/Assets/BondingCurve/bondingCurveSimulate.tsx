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
} from 'antd';
import React, { useState, useEffect } from 'react';
import { CalculatorOutlined, ReloadOutlined, InfoCircleOutlined } from '@ant-design/icons';
import LindeChartSimple, { DataPoint } from './lindeChartSimple';
import {
  SimulationParams,
  LevelData,
  calculateBondingCurveSimulation,
  validateSimulationParams,
  formatNumber,
  calculateTheoreticalTotal,
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

  // 默认参数
  const defaultParams: SimulationParams = {
    totalTarget: 21000000,
    levelWidth: 2100000,
    decayFactor: 0.9975,
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
                label="档位宽度 (δC)"
                name="levelWidth"
                rules={[{ required: true, message: '请输入档位宽度' }]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={1}
                  placeholder="2100000"
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="衰减系数 (k)"
                name="decayFactor"
                rules={[
                  { required: true, message: '请输入衰减系数' },
                  { type: 'number', min: 0, max: 1, message: '衰减系数应在0-1之间' }
                ]}
              >
                <InputNumber
                  style={{ width: '100%' }}
                  min={0}
                  max={1}
                  step={0.0001}
                  precision={4}
                  placeholder="0.9975"
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
                title="目标达成率"
                value={summary.targetAchievement}
                precision={2}
                suffix="%"
              />
            </Col>
          </Row>
          <Row gutter={16} style={{ marginTop: '16px' }}>
            <Col span={12}>
              <Statistic
                title="理论总积分（无限档期）"
                value={calculateTheoreticalTotal(form.getFieldsValue())}
                precision={0}
                formatter={(value) => formatNumber(Number(value))}
              />
            </Col>
            <Col span={12}>
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
              横轴：劳动价值（档位宽度 × 档位序号），纵轴：积分奖励
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
            scroll={{ x: 600 }}
            size="small"
          />
        </Card>
      )}
    </div>
  );
};
