import React, { useState } from 'react';
import { rechargeService } from './service';
import { 
  Button, 
  Card, 
  Typography, 
  Space, 
  InputNumber, 
  Radio, 
  Steps, 
  QRCode, 
  Alert, 
  Divider,
  Row,
  Col,
  Statistic,
  message,
  Spin,
  Descriptions
} from 'antd';
import { 
  ArrowLeftOutlined, 
  WalletOutlined, 
  WechatOutlined, 
  AlipayCircleOutlined,
  CreditCardOutlined,
  CheckCircleOutlined,
  LoadingOutlined
} from '@ant-design/icons';

const { Title, Text } = Typography;

interface RechargeProps {
  setCurrentContent: (content: string) => void;
}

export default ({ setCurrentContent }: RechargeProps) => {
  const [currentStep, setCurrentStep] = useState(0);
  const [amount, setAmount] = useState<number | null>(null);
  const [paymentMethod, setPaymentMethod] = useState('wechat');
  const [orderId, setOrderId] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isPaid, setIsPaid] = useState(false);

  // 支付方式选项
  const paymentMethods = [
    { value: 'wechat', label: '微信支付', icon: <WechatOutlined style={{ color: '#07c160' }} /> },
    { value: 'alipay', label: '支付宝', icon: <AlipayCircleOutlined style={{ color: '#1677ff' }} /> },
    { value: 'card', label: '银行卡', icon: <CreditCardOutlined style={{ color: '#722ed1' }} /> }
  ];

  // 预设金额选项
  const presetAmounts = [50, 100, 200, 500, 1000, 2000];

  // 第一步：输入金额和选择支付方式
  const renderStep1 = () => (
    <div style={{ maxWidth: 600, margin: '0 auto' }}>
      <Title level={4} style={{ textAlign: 'center', marginBottom: 32 }}>
        <WalletOutlined style={{ marginRight: 8 }} />
        选择充值金额
      </Title>

      <Card>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          {/* 预设金额 */}
          <div>
            <Text strong>选择金额：</Text>
            <div style={{ marginTop: 16 }}>
              <Row gutter={[12, 12]}>
                {presetAmounts.map(preset => (
                  <Col span={8} key={preset}>
                    <Button
                      size="large"
                      block
                      type={amount === preset ? 'primary' : 'default'}
                      onClick={() => setAmount(preset)}
                      style={{ height: 50, fontSize: 16 }}
                    >
                      ¥{preset}
                    </Button>
                  </Col>
                ))}
              </Row>
            </div>
          </div>

          <Divider>或</Divider>

          {/* 自定义金额 */}
          <div>
            <Text strong>自定义金额：</Text>
            <div style={{ marginTop: 16 }}>
              <InputNumber
                size="large"
                placeholder="请输入充值金额"
                value={amount}
                onChange={setAmount}
                min={1}
                max={50000}
                step={1}
                precision={0}
                style={{ width: '100%' }}
                prefix="¥"
              />
            </div>
          </div>

          <Divider />

          {/* 支付方式选择 */}
          <div>
            <Text strong>选择支付方式：</Text>
            <div style={{ marginTop: 16 }}>
              <Radio.Group 
                value={paymentMethod} 
                onChange={(e) => setPaymentMethod(e.target.value)}
                style={{ width: '100%' }}
              >
                <Space direction="vertical" style={{ width: '100%' }}>
                  {paymentMethods.map(method => (
                    <Radio.Button 
                      key={method.value} 
                      value={method.value}
                      style={{ 
                        width: '100%', 
                        height: 50, 
                        display: 'flex', 
                        alignItems: 'center',
                        justifyContent: 'center'
                      }}
                    >
                      <Space>
                        {method.icon}
                        {method.label}
                      </Space>
                    </Radio.Button>
                  ))}
                </Space>
              </Radio.Group>
            </div>
          </div>

          <Button
            type="primary"
            size="large"
            block
            disabled={!amount || amount <= 0}
            loading={isLoading}
            onClick={async () => {
              if (!amount || amount <= 0) return;
              
              setIsLoading(true);
              try {
                const result = await rechargeService.createRechargeOrder({
                  amount,
                  paymentMethod
                });
                setOrderId(result.orderId || `ORDER${Date.now()}`);
                setCurrentStep(1);
                message.success('订单创建成功');
              } catch (error) {
                message.error('创建订单失败，请重试');
                console.error('创建充值订单失败:', error);
              } finally {
                setIsLoading(false);
              }
            }}
            style={{ height: 50, fontSize: 16 }}
          >
            下一步
          </Button>
        </Space>
      </Card>
    </div>
  );

  // 第二步：支付二维码
  const renderStep2 = () => (
    <div style={{ maxWidth: 500, margin: '0 auto' }}>
      <Title level={4} style={{ textAlign: 'center', marginBottom: 32 }}>
        扫码支付
      </Title>

      <Card>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          {/* 订单信息 */}
          <Alert
            message="订单信息"
            description={
              <div>
                <div>订单号：{orderId}</div>
                <div>充值金额：¥{amount}</div>
                <div>支付方式：{paymentMethods.find(m => m.value === paymentMethod)?.label}</div>
              </div>
            }
            type="info"
            showIcon
          />

          {/* 二维码 */}
          <div style={{ textAlign: 'center', padding: '20px 0' }}>
            <div style={{ 
              display: 'inline-block', 
              padding: '20px', 
              border: '1px solid #d9d9d9', 
              borderRadius: '8px',
              background: '#fafafa'
            }}>
              <QRCode
                value={`https://pay.example.com/qr/${orderId}?amount=${amount}&method=${paymentMethod}`}
                size={200}
                icon={paymentMethod === 'wechat' ? '/wechat-icon.png' : '/alipay-icon.png'}
              />
            </div>
          </div>

          <Alert
            message="支付说明"
            description="请使用对应的支付APP扫描二维码完成支付，支付成功后页面将自动跳转。"
            type="warning"
            showIcon
          />

          {/* 模拟支付状态 */}
          <div style={{ textAlign: 'center' }}>
            <Space direction="vertical">
              <Spin 
                indicator={<LoadingOutlined style={{ fontSize: 24 }} spin />} 
                spinning={!isPaid}
              />
              <Text type="secondary">
                {isPaid ? '支付成功' : '等待支付中...'}
              </Text>
            </Space>
          </div>

          <Space style={{ width: '100%', justifyContent: 'center' }}>
            <Button onClick={() => setCurrentStep(0)}>
              上一步
            </Button>
            <Button 
              type="primary" 
              onClick={() => {
                setIsPaid(true);
                setTimeout(() => setCurrentStep(2), 1000);
              }}
            >
              模拟支付完成
            </Button>
          </Space>
        </Space>
      </Card>
    </div>
  );

  // 第三步：完成页面
  const renderStep3 = () => (
    <div style={{ maxWidth: 500, margin: '0 auto' }}>
      <Title level={4} style={{ textAlign: 'center', marginBottom: 32 }}>
        充值完成
      </Title>

      <Card>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          {/* 成功图标 */}
          <div style={{ textAlign: 'center', padding: '20px 0' }}>
            <CheckCircleOutlined 
              style={{ 
                fontSize: 64, 
                color: '#52c41a',
                marginBottom: 16
              }} 
            />
            <Title level={3} style={{ color: '#52c41a', margin: 0 }}>
              充值成功！
            </Title>
          </div>

          {/* 充值详情 */}
          <Card size="small" style={{ background: '#f6ffed' }}>
            <Row gutter={16}>
              <Col span={12}>
                <Statistic
                  title="充值金额"
                  value={amount || 0}
                  precision={0}
                  valueStyle={{ color: '#52c41a', fontSize: 24 }}
                  prefix="¥"
                />
              </Col>
              <Col span={12}>
                <Statistic
                  title="支付方式"
                  value={paymentMethods.find(m => m.value === paymentMethod)?.label}
                  valueStyle={{ fontSize: 16 }}
                />
              </Col>
            </Row>
            <Divider style={{ margin: '16px 0' }} />
            <div style={{ fontSize: 12, color: '#666' }}>
              <div>订单号：{orderId}</div>
              <div>完成时间：{new Date().toLocaleString()}</div>
            </div>
          </Card>

          <Alert
            message="充值到账"
            description="充值金额已实时到账，您可以在钱包余额中查看。"
            type="success"
            showIcon
          />

          <Space style={{ width: '100%', justifyContent: 'center' }}>
            <Button 
              type="primary" 
              size="middle"
              onClick={() => setCurrentContent('overview')}
            >
              返回钱包
            </Button>
            <Button 
              size="middle"
              onClick={() => {
                setCurrentStep(0);
                setAmount(null);
                setPaymentMethod('wechat');
                setOrderId('');
                setIsPaid(false);
              }}
            >
              继续充值
            </Button>
          </Space>
        </Space>
      </Card>
    </div>
  );

  const steps = [
    { title: '选择金额', description: '输入充值金额' },
    { title: '扫码支付', description: '完成支付' },
    { title: '充值完成', description: '到账成功' }
  ];

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
        <Descriptions title="充值"></Descriptions>

        {/* 步骤条 */}
        <div style={{ 
          marginBottom: 32, 
          marginTop: 32,
          maxWidth: 600,
          margin: '32px auto',
          padding: '0 20px'
        }}>
          <Steps current={currentStep} items={steps} />
        </div>

        {/* 步骤内容 */}
        {currentStep === 0 && renderStep1()}
        {currentStep === 1 && renderStep2()}
        {currentStep === 2 && renderStep3()}
      </Card>
    </>
  );
};
