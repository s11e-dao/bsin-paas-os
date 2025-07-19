import React, { useState } from 'react';
import {
  Form,
  InputNumber,
  Button,
  Card,
  Alert,
  Row,
  Col,
  Typography,
  Space,
  Divider,
  Progress,
  Statistic,
  message,
  Modal
} from 'antd';
import {
  BankOutlined,
  ShopOutlined,
  TeamOutlined,
  UserOutlined,
  SaveOutlined,
  CalculatorOutlined,
  GiftOutlined,
  DollarOutlined
} from '@ant-design/icons';
import { getLocalStorageInfo } from '../../../utils/localStorageInfo';
import { configProfitSharingConfig, getProfitSharingConfigDetail } from './service';

const { Title, Text } = Typography;

export default () => {
  const userInfo = getLocalStorageInfo('userInfo');
  const [formRef] = Form.useForm();
  const [loading, setLoading] = useState(false);
  interface FormValues {
    superTenantRate: number;
    tenantRate: number;
    sysAgentRate: number;
    customerRate: number;
    distributorRate: number;
    exchangeDigitalPointsRate: number;
  }

  const [formValues, setFormValues] = useState<FormValues>({
    superTenantRate: 0,
    tenantRate: 0,
    sysAgentRate: 0,
    customerRate: 0,
    distributorRate: 0,
    exchangeDigitalPointsRate: 0,
  });

  // 计算总比例
  const totalRate = Object.values(formValues).reduce((sum, rate) => sum + (rate || 0), 0);
  const isValidTotal = totalRate === 100;

  React.useEffect(() => {
    getProfitSharingConfigDetail({}).then((res) => {
      if (res?.data) {
        formRef.setFieldsValue(res?.data);
        setFormValues(res?.data);
      }
    });
  }, []);

  const confirmActivity = () => {
    if (!isValidTotal) {
      message.error('所有比例总和必须等于100%');
      return;
    }

    formRef.validateFields()
      .then(() => {
        const formData = formRef.getFieldsValue();

        // 显示确认对话框
        Modal.confirm({
          title: '确认保存分账配置',
          icon: <SaveOutlined />,
          content: (
            <div style={{ marginTop: 16 }}>
              <Alert
                message="请确认以下分账比例配置："
                type="info"
                style={{ marginBottom: 16 }}
              />
              <div style={{ background: '#f9f9f9', padding: 16, borderRadius: 6 }}>
                <Space direction="vertical" style={{ width: '100%' }} size="small">
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Text><BankOutlined style={{ color: '#1890ff', marginRight: 8 }} />运营平台：</Text>
                    <Text strong>{formData.superTenantRate || 0}%</Text>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Text><ShopOutlined style={{ color: '#52c41a', marginRight: 8 }} />租户平台：</Text>
                    <Text strong>{formData.tenantRate || 0}%</Text>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Text><TeamOutlined style={{ color: '#faad14', marginRight: 8 }} />代理商：</Text>
                    <Text strong>{formData.sysAgentRate || 0}%</Text>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Text><UserOutlined style={{ color: '#722ed1', marginRight: 8 }} />消费者：</Text>
                    <Text strong>{formData.customerRate || 0}%</Text>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Text><GiftOutlined style={{ color: '#eb2f96', marginRight: 8 }} />分销者：</Text>
                    <Text strong>{formData.distributorRate || 0}%</Text>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Text><DollarOutlined style={{ color: '#13c2c2', marginRight: 8 }} />数字积分：</Text>
                    <Text strong>{formData.exchangeDigitalPointsRate || 0}%</Text>
                  </div>
                  <Divider style={{ margin: '8px 0' }} />
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Text strong>总计：</Text>
                    <Text strong style={{ color: '#52c41a' }}>{totalRate}%</Text>
                  </div>
                </Space>
              </div>
              <Alert
                message="提醒"
                description="配置保存后将立即生效，请确认无误后点击确定。"
                type="warning"
                style={{ marginTop: 16 }}
                showIcon
              />
            </div>
          ),
          width: 500,
          okText: '确认保存',
          cancelText: '取消',
          okType: 'primary',
          onOk() {
            return new Promise((resolve, reject) => {
              setLoading(true);
              configProfitSharingConfig(formData).then((res) => {
                if (res.code === 0) {
                  message.success("分账配置保存成功！");
                  resolve(res);
                } else {
                  message.error(`保存失败： ${res?.message}`);
                  reject(res);
                }
              }).catch((error) => {
                message.error('保存失败，请重试');
                reject(error);
              }).finally(() => {
                setLoading(false);
              });
            });
          },
          onCancel() {
            console.log('用户取消保存');
          },
        });
      })
      .catch(() => {
        message.error('请检查表单输入');
      });
  };

  const handleValuesChange = (changedValues: any, allValues: FormValues) => {
    setFormValues(allValues);
  };

  const configItems = [
    {
      name: 'superTenantRate',
      label: '运营平台分佣比例',
      icon: <BankOutlined />,
      color: '#1890ff',
      description: '平台运营方获得的分佣比例'
    },
    {
      name: 'tenantRate',
      label: '租户平台分佣比例',
      icon: <ShopOutlined />,
      color: '#52c41a',
      description: '租户平台获得的分佣比例'
    },
    {
      name: 'sysAgentRate',
      label: '代理商分佣比例',
      icon: <TeamOutlined />,
      color: '#faad14',
      description: '代理商获得的分佣比例'
    },
    {
      name: 'customerRate',
      label: '消费者返利比例',
      icon: <UserOutlined />,
      color: '#722ed1',
      description: '消费者获得的返利比例'
    },
    {
      name: 'distributorRate',
      label: '分销者分佣比例',
      icon: <GiftOutlined />,
      color: '#eb2f96',
      description: '分销者获得的分佣比例'
    },
    {
      name: 'exchangeDigitalPointsRate',
      label: '数字积分兑换比例',
      icon: <DollarOutlined />,
      color: '#13c2c2',
      description: '佣金兑换数字积分的比例'
    }
  ];

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      <Row gutter={[24, 24]}>

        <Col span={24}>
          <Card title={
            <Row justify="space-between" align="middle">
              <Col>
                <Space>
                  <BankOutlined />
                  分账比例设置
                </Space>
              </Col>
              <Col>
                <Space align="center">
                  <Text type="secondary">总比例：</Text>
                  <Text
                    strong
                    style={{
                      color: isValidTotal ? '#52c41a' : '#ff4d4f',
                      fontSize: 18,
                    }}
                  >
                    {totalRate}%
                  </Text>
                  {isValidTotal ? (
                    <Text type="success">✓</Text>
                  ) : totalRate > 100 ? (
                    <Text type="danger">⚠</Text>
                  ) : (
                    <Text type="warning">⚠</Text>
                  )}
                </Space>
              </Col>
            </Row>
          }>
            <Alert
              message="设置规则"
              description="所有角色的分账比例总和必须等于100%，请合理分配各角色比例"
              type="info"
              showIcon
              style={{ marginBottom: 16 }}
            />

            <div style={{ marginBottom: 24 }}>
              <Row justify="space-between" align="middle" style={{ marginBottom: 8 }}>
                <Col>
                  <Text strong>当前配置进度</Text>
                </Col>
                <Col>
                  {isValidTotal ? (
                    <Text type="success">✓ 比例配置正确</Text>
                  ) : totalRate > 100 ? (
                    <Text type="danger">⚠ 总比例超过100%</Text>
                  ) : (
                    <Text type="warning">⚠ 总比例不足100%</Text>
                  )}
                </Col>
              </Row>
              <Progress
                percent={totalRate}
                status={totalRate > 100 ? 'exception' : totalRate === 100 ? 'success' : 'active'}
                strokeColor={totalRate > 100 ? '#ff4d4f' : totalRate === 100 ? '#52c41a' : '#1890ff'}
                strokeWidth={8}
              />
            </div>

            <Form
              form={formRef}
              layout="vertical"
              onValuesChange={handleValuesChange}
            >
              <Row gutter={[16, 16]}>
                {configItems.map((item, index) => (
                  <Col xs={24} sm={12} md={8} key={item.name}>
                    <Card
                      size="small"
                      style={{
                        height: '100%',
                        borderLeft: `4px solid ${item.color}`,
                        transition: 'all 0.3s ease',
                      }}
                      hoverable
                    >
                      <Space direction="vertical" style={{ width: '100%' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                          <span style={{ color: item.color, fontSize: 18 }}>
                            {item.icon}
                          </span>
                          <Text strong>{item.label}</Text>
                        </div>

                        <Text type="secondary" style={{ fontSize: 12 }}>
                          {item.description}
                        </Text>

                        <Form.Item
                          name={item.name}
                          rules={[
                            { required: true, message: `请输入${item.label}!` },
                            { type: 'number', min: 0, max: 100, message: '比例应在0-100之间' }
                          ]}
                          style={{ margin: 0 }}
                        >
                          <InputNumber
                            addonAfter="%"
                            defaultValue={0}
                            style={{ width: '100%' }}
                            size="large"
                            placeholder="输入比例"
                          />
                        </Form.Item>
                      </Space>
                    </Card>
                  </Col>
                ))}
              </Row>

              <Divider />

              <Row justify="center">
                <Col>
                  <Button
                    type="primary"
                    size="large"
                    loading={loading}
                    onClick={confirmActivity}
                    icon={<SaveOutlined />}
                    style={{
                      height: 48,
                      paddingLeft: 32,
                      paddingRight: 32,
                      fontSize: 16,
                    }}
                    disabled={!isValidTotal}
                  >
                    保存配置
                  </Button>
                </Col>
              </Row>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>
  );
};