import React, { useState, useEffect } from 'react';
import { withdrawService, withdrawAccountService } from './service';
import { 
  Button, 
  Card, 
  Typography, 
  Space, 
  InputNumber, 
  Select, 
  Alert, 
  Divider,
  Row,
  Col,
  Statistic,
  message,
  Modal,
  Form,
  Descriptions,
  Tag,
  Progress,
  Spin
} from 'antd';
import { 
  ArrowLeftOutlined, 
  WalletOutlined, 
  BankOutlined,
  AlipayCircleOutlined,
  WechatOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  LoadingOutlined
} from '@ant-design/icons';

const { Title, Text } = Typography;
const { Option } = Select;

interface WithdrawProps {
  setCurrentContent: (content: string) => void;
}

interface WithdrawAccount {
  id: string;
  name: string;
  accountNumber: string;
  accountType: 'bank' | 'alipay' | 'wechat';
  bankName?: string;
  isDefault: boolean;
  status: 'active' | 'inactive';
}

export default ({ setCurrentContent }: WithdrawProps) => {
  const [form] = Form.useForm();
  const [availableAmount, setAvailableAmount] = useState(12580.50);
  const [withdrawAmount, setWithdrawAmount] = useState<number | null>(null);
  const [selectedAccount, setSelectedAccount] = useState<string>('');
  const [withdrawAccounts, setWithdrawAccounts] = useState<WithdrawAccount[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [withdrawOrder, setWithdrawOrder] = useState<any>(null);

  // 获取提现账户数据和可提现金额
  useEffect(() => {
    const fetchData = async () => {
      try {
        // 获取提现账户列表
        const accountsResponse = await withdrawAccountService.getWithdrawAccountList();
        if (accountsResponse.data) {
          setWithdrawAccounts(accountsResponse.data);
          const defaultAccount = accountsResponse.data.find((acc: any) => acc.isDefault);
          if (defaultAccount) {
            setSelectedAccount(defaultAccount.id);
            form.setFieldsValue({ account: defaultAccount.id });
          }
        }

        // 获取可提现金额
        const amountResponse = await withdrawService.getAvailableWithdrawAmount();
        if (amountResponse.data) {
          setAvailableAmount(amountResponse.data.availableAmount || 0);
        }
      } catch (error) {
        console.error('获取数据失败:', error);
        message.error('获取数据失败，请重试');
      }
    };

    fetchData();
  }, []);

  // 获取账户图标
  const getAccountIcon = (accountType: string) => {
    switch (accountType) {
      case 'bank':
        return <BankOutlined style={{ color: '#1890ff' }} />;
      case 'alipay':
        return <AlipayCircleOutlined style={{ color: '#1677ff' }} />;
      case 'wechat':
        return <WechatOutlined style={{ color: '#07c160' }} />;
      default:
        return <BankOutlined />;
    }
  };

  // 获取账户类型名称
  const getAccountTypeName = (accountType: string) => {
    switch (accountType) {
      case 'bank':
        return '银行卡';
      case 'alipay':
        return '支付宝';
      case 'wechat':
        return '微信';
      default:
        return '未知';
    }
  };

  // 计算手续费（示例：提现金额的1%，最低2元）
  const calculateFee = (amount: number) => {
    const fee = Math.max(amount * 0.01, 2);
    return Math.min(fee, 50); // 最高50元
  };

  // 计算实际到账金额
  const actualAmount = withdrawAmount ? withdrawAmount - calculateFee(withdrawAmount) : 0;

  // 快速金额选择
  const quickAmounts = [100, 500, 1000, 2000, 5000];

  // 提交提现申请
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      if (!withdrawAmount || withdrawAmount <= 0) {
        message.error('请输入有效的提现金额');
        return;
      }

      if (withdrawAmount > availableAmount) {
        message.error('提现金额不能超过可提现金额');
        return;
      }

      if (!selectedAccount) {
        message.error('请选择提现账户');
        return;
      }

      setIsSubmitting(true);

      try {
        // 调用提现API
        const response = await withdrawService.createWithdrawApplication({
          amount: withdrawAmount,
          accountId: selectedAccount,
          remark: values.remark || ''
        });

        if (response.data) {
          const order = {
            id: response.data.id || `WD${Date.now()}`,
            amount: withdrawAmount,
            fee: calculateFee(withdrawAmount),
            actualAmount,
            account: withdrawAccounts.find(acc => acc.id === selectedAccount),
            status: 'pending',
            createTime: new Date().toLocaleString()
          };

          setWithdrawOrder(order);
          setShowSuccessModal(true);
          message.success('提现申请提交成功');

          // 重置表单
          form.resetFields();
          setWithdrawAmount(null);
        }
      } catch (error) {
        console.error('提现申请失败:', error);
        message.error('提现申请失败，请重试');
      } finally {
        setIsSubmitting(false);
      }

    } catch (error) {
      console.error('提现申请失败:', error);
      message.error('提现申请失败，请重试');
      setIsSubmitting(false);
    }
  };

  return (
    <>
      <Card style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          icon={<ArrowLeftOutlined />} 
          onClick={() => setCurrentContent('overview')}
          style={{ float: 'right' }}
        >
          返回
        </Button>
        <Descriptions title="提现"></Descriptions>

        <div style={{ maxWidth: 600, margin: '0 auto' }}>
          {/* 可提现金额展示 */}
          <Card style={{ marginBottom: 24, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
            <div style={{ textAlign: 'center' }}>
              <Statistic
                title="可提现金额"
                value={availableAmount}
                precision={2}
                valueStyle={{ 
                  color: 'white', 
                  fontSize: 36,
                  fontWeight: 'bold'
                }}
                prefix="¥"
              />
            </div>
          </Card>

          {/* 提现表单 */}
          <Card title="提现申请">
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
            >
              <Space direction="vertical" style={{ width: '100%' }} size="large">
                {/* 提现金额 */}
                <Form.Item
                  label="提现金额"
                  name="amount"
                  rules={[
                    { required: true, message: '请输入提现金额' },
                    { 
                      validator: (_, value) => {
                        if (!value || value <= 0) {
                          return Promise.reject(new Error('提现金额必须大于0'));
                        }
                        if (value > availableAmount) {
                          return Promise.reject(new Error('提现金额不能超过可提现金额'));
                        }
                        return Promise.resolve();
                      }
                    }
                  ]}
                >
                  <Space direction="vertical" style={{ width: '100%' }} size="middle">
                    <InputNumber
                      size="large"
                      placeholder="请输入提现金额"
                      value={withdrawAmount}
                      onChange={(value) => {
                        setWithdrawAmount(value);
                        form.setFieldsValue({ amount: value });
                      }}
                      min={1}
                      max={availableAmount}
                      step={1}
                      precision={2}
                      style={{ width: '100%' }}
                      prefix="¥"
                    />
                    
                    {/* 快速金额选择 */}
                    <div>
                      <Space wrap>
                        {quickAmounts.map(amount => (
                          <Button
                            key={amount}
                            size="small"
                            type={withdrawAmount === amount ? 'primary' : 'default'}
                            onClick={() => {
                              setWithdrawAmount(amount);
                              form.setFieldsValue({ amount: amount });
                            }}
                          >
                            ¥{amount}
                          </Button>
                        ))}
                        <Button
                          size="small"
                          type={withdrawAmount === availableAmount ? 'primary' : 'default'}
                          onClick={() => {
                            setWithdrawAmount(availableAmount);
                            form.setFieldsValue({ amount: availableAmount });
                          }}
                        >
                          全部提现
                        </Button>
                      </Space>
                    </div>
                  </Space>
                </Form.Item>

                {/* 提现账户选择 */}
                <Form.Item
                  label="提现账户"
                  name="account"
                  rules={[{ required: true, message: '请选择提现账户' }]}
                >
                  <Select
                    size="large"
                    placeholder="请选择提现账户"
                    value={selectedAccount}
                    onChange={(value) => {
                      setSelectedAccount(value);
                      form.setFieldsValue({ account: value });
                    }}
                    style={{ width: '100%' }}
                  >
                    {withdrawAccounts.map(account => (
                      <Option key={account.id} value={account.id}>
                        <Space>
                          {getAccountIcon(account.accountType)}
                          <div>
                            <div style={{ fontSize: 12, color: '#666' }}>
                              {account.accountType && ` - ${account.accountNumber}`}
                              {account.isDefault && <Tag color="blue">默认</Tag>}
                            </div>
                          </div>
                        </Space>
                      </Option>
                    ))}
                  </Select>
                </Form.Item>

                {/* 费用明细 */}
                {withdrawAmount && withdrawAmount > 0 && (
                  <Card size="small" style={{ background: '#f6ffed', border: '1px solid #b7eb8f' }}>
                    <Row justify="space-between" align="middle">
                      <Col>
                        <Text>提现金额：¥{withdrawAmount.toFixed(2)}</Text>
                        <br />
                        <Text type="secondary" style={{ fontSize: 12 }}>
                          手续费：¥{calculateFee(withdrawAmount).toFixed(2)}
                        </Text>
                      </Col>
                      <Col>
                        <Text strong style={{ color: '#52c41a', fontSize: 18 }}>
                          实际到账：¥{actualAmount.toFixed(2)}
                        </Text>
                      </Col>
                    </Row>
                  </Card>
                )}
              </Space>

              {/* 提交按钮 */}
              <Form.Item style={{ marginTop: 32, textAlign: 'center' }}>
                <Space size="large">
                  <Button 
                    size="middle"
                    onClick={() => setCurrentContent('overview')}
                  >
                    取消
                  </Button>
                  <Button
                    type="primary"
                    size="middle"
                    htmlType="submit"
                    loading={isSubmitting}
                    disabled={!withdrawAmount || withdrawAmount <= 0 || !selectedAccount}
                    icon={<WalletOutlined />}
                    style={{ minWidth: 140 }}
                  >
                    提交申请
                  </Button>
                </Space>
              </Form.Item>
            </Form>
          </Card>
        </div>
      </Card>

      {/* 提现成功弹窗 */}
      <Modal
        title="提现申请提交成功"
        open={showSuccessModal}
        onCancel={() => setShowSuccessModal(false)}
        footer={[
          <Button key="back" onClick={() => setShowSuccessModal(false)}>
            关闭
          </Button>,
          <Button 
            key="submit" 
            type="primary" 
            onClick={() => setCurrentContent('overview')}
          >
            返回钱包
          </Button>
        ]}
        width={500}
      >
        {withdrawOrder && (
          <div style={{ textAlign: 'center', padding: '20px 0' }}>
            <CheckCircleOutlined 
              style={{ 
                fontSize: 48, 
                color: '#52c41a',
                marginBottom: 16
              }} 
            />
            <Title level={4} style={{ color: '#52c41a', marginBottom: 24 }}>
              申请已提交
            </Title>
            
            <Card size="small" style={{ background: '#f6ffed', marginBottom: 16 }}>
              <Row justify="space-between" align="middle">
                <Col>
                  <Text>提现金额：¥{withdrawOrder.amount.toFixed(2)}</Text>
                  <br />
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    手续费：¥{withdrawOrder.fee.toFixed(2)}
                  </Text>
                </Col>
                <Col>
                  <Text strong style={{ color: '#52c41a', fontSize: 16 }}>
                    实际到账：¥{withdrawOrder.actualAmount.toFixed(2)}
                  </Text>
                </Col>
              </Row>
            </Card>
            
            <Text type="secondary" style={{ fontSize: 12 }}>
              订单号：{withdrawOrder.id} | 处理时间：1-3个工作日
            </Text>
          </div>
        )}
      </Modal>
    </>
  );
};
