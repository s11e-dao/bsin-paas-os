import React, { useState, useEffect, useRef } from 'react';
import {
  Layout,
  Card,
  Table,
  Button,
  Input,
  Row,
  Col,
  Typography,
  Space,
  Divider,
  message,
  InputNumber,
  Modal,
  Badge,
  Tag,
  Tooltip,
  Alert,
  Progress,
  Statistic,
  Avatar,
  List,
  Empty,
  notification
} from 'antd';
import {
  ShoppingCartOutlined,
  DeleteOutlined,
  ClearOutlined,
  DollarOutlined,
  BarcodeOutlined,
  PrinterOutlined,
  ScanOutlined,
  HistoryOutlined,
  SettingOutlined,
  UserOutlined,
  ClockCircleOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons';

const { Header, Content, Sider } = Layout;
const { Title, Text } = Typography;
const { Search } = Input;

export default () => {
  // 定义商品类型
  interface CartItem {
    key: string;
    barcode: string;
    name: string;
    price: number;
    quantity: number;
    unit: string;
    category: string;
    stock: number;
  }

  interface Order {
    id: number;
    items: CartItem[];
    total: number;
    receivedAmount: number;
    change: number;
    paymentMethod: string;
    timestamp: string;
  }

  const [cart, setCart] = useState<CartItem[]>([]);
  const [barcode, setBarcode] = useState('');
  const [paymentModalVisible, setPaymentModalVisible] = useState(false);
  const [receivedAmount, setReceivedAmount] = useState(0);
  const [paymentMethod, setPaymentMethod] = useState('cash');
  const [orderHistory, setOrderHistory] = useState<Order[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const barcodeInputRef = useRef<any>(null);

  // 示例商品数据
  const products = {
    '1001': { name: '苹果', price: 8.5, unit: '斤', stock: 50, category: '水果' },
    '1002': { name: '香蕉', price: 6.0, unit: '斤', stock: 30, category: '水果' },
    '1003': { name: '牛奶', price: 12.5, unit: '盒', stock: 20, category: '乳制品' },
    '1004': { name: '面包', price: 15.0, unit: '个', stock: 15, category: '烘焙' },
    '1005': { name: '鸡蛋', price: 18.0, unit: '斤', stock: 25, category: '蛋类' },
    '1006': { name: '可乐', price: 3.5, unit: '瓶', stock: 100, category: '饮料' },
    '1007': { name: '薯片', price: 8.0, unit: '包', stock: 40, category: '零食' },
    '1008': { name: '矿泉水', price: 2.0, unit: '瓶', stock: 80, category: '饮料' }
  };

  // 支付方式选项
  const paymentMethods = [
    { value: 'cash', label: '现金', icon: <DollarOutlined /> },
    { value: 'wechat', label: '微信', icon: <UserOutlined /> },
    { value: 'alipay', label: '支付宝', icon: <UserOutlined /> },
    { value: 'card', label: '银行卡', icon: <UserOutlined /> }
  ];

  // 自动聚焦条码输入框
  useEffect(() => {
    if (barcodeInputRef.current) {
      barcodeInputRef.current.focus();
    }
  }, []);

  // 添加商品到购物车
  const addToCart = () => {
    if (!barcode.trim()) {
      message.warning('请输入商品条码');
      return;
    }

    const product = products[barcode];
    if (!product) {
      message.error('商品不存在');
      setBarcode('');
      return;
    }

    const existingItem = cart.find(item => item.barcode === barcode);
    if (existingItem) {
      setCart(cart.map(item =>
        item.barcode === barcode
          ? { ...item, quantity: item.quantity + 1 }
          : item
      ));
    } else {
      setCart([...cart, {
        key: barcode,
        barcode,
        name: product.name,
        price: product.price,
        quantity: 1,
        unit: product.unit,
        category: product.category,
        stock: product.stock
      }]);
    }

    setBarcode('');
    message.success(`${product.name} 已添加`);
    
    // 重新聚焦输入框
    setTimeout(() => {
      if (barcodeInputRef.current) {
        barcodeInputRef.current.focus();
      }
    }, 100);
  };

  // 删除商品
  const removeItem = (barcode) => {
    setCart(cart.filter(item => item.barcode !== barcode));
  };

  // 更新商品数量
  const updateQuantity = (barcode, quantity) => {
    if (quantity <= 0) {
      removeItem(barcode);
      return;
    }
    setCart(cart.map(item =>
      item.barcode === barcode
        ? { ...item, quantity }
        : item
    ));
  };

  // 清空购物车
  const clearCart = () => {
    Modal.confirm({
      title: '确认清空购物车',
      content: '确定要清空所有商品吗？',
      onOk: () => {
        setCart([]);
        message.info('购物车已清空');
      }
    });
  };

  // 计算总额
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const itemCount = cart.reduce((sum, item) => sum + item.quantity, 0);

  // 结算
  const handleCheckout = () => {
    if (cart.length === 0) {
      message.warning('购物车为空');
      return;
    }
    setPaymentModalVisible(true);
    setReceivedAmount(total);
  };

  // 完成支付
  const completePayment = async () => {
    if (receivedAmount < total) {
      message.error('收款金额不足');
      return;
    }
    
    setIsLoading(true);
    
    // 模拟支付处理
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    const change = receivedAmount - total;
    const order = {
      id: Date.now(),
      items: [...cart],
      total,
      receivedAmount,
      change,
      paymentMethod,
      timestamp: new Date().toLocaleString()
    };
    
    setOrderHistory([order, ...orderHistory]);
    setCart([]);
    setReceivedAmount(0);
    setPaymentModalVisible(false);
    setIsLoading(false);
    
    notification.success({
      message: '支付成功',
      description: `找零：¥${change.toFixed(2)}`,
      duration: 3
    });
  };

  // 快速添加商品按钮
  const QuickAddButton = ({ barcode, product }) => (
    <Tooltip title={`${product.name} - ¥${product.price}`}>
      <Button
        size="small"
        onClick={() => {
          setBarcode(barcode);
          addToCart();
        }}
        style={{ margin: 4 }}
      >
        {product.name}
      </Button>
    </Tooltip>
  );

  const columns = [
    {
      title: '商品',
      dataIndex: 'name',
      key: 'name',
      render: (name, record) => (
        <div>
          <div style={{ fontWeight: 'bold' }}>{name}</div>
          <Tag size="small" color="blue">{record.category}</Tag>
        </div>
      ),
    },
    {
      title: '单价',
      dataIndex: 'price',
      key: 'price',
      render: (price) => (
        <Text strong style={{ color: '#1890ff' }}>
          ¥{price.toFixed(2)}
        </Text>
      ),
    },
    {
      title: '数量',
      dataIndex: 'quantity',
      key: 'quantity',
      render: (quantity, record) => (
        <Space>
          <Button
            size="small"
            onClick={() => updateQuantity(record.barcode, quantity - 1)}
            disabled={quantity <= 1}
          >
            -
          </Button>
          <InputNumber
            min={1}
            max={record.stock}
            value={quantity}
            onChange={(value) => updateQuantity(record.barcode, value)}
            size="small"
            style={{ width: 60 }}
          />
          <Button
            size="small"
            onClick={() => updateQuantity(record.barcode, quantity + 1)}
            disabled={quantity >= record.stock}
          >
            +
          </Button>
        </Space>
      ),
    },
    {
      title: '小计',
      key: 'subtotal',
      render: (_, record) => (
        <Text strong style={{ color: '#f5222d' }}>
          ¥{(record.price * record.quantity).toFixed(2)}
        </Text>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Button
          type="text"
          danger
          icon={<DeleteOutlined />}
          onClick={() => removeItem(record.barcode)}
        />
      ),
    },
  ];

  return (
    <Layout style={{ height: '100vh', background: '#f5f5f5' }}>
      <Header style={{ 
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 
        padding: '0 20px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
      }}>
        <Row justify="space-between" align="middle" style={{ height: '100%' }}>
          <Title level={3} style={{ color: 'white', margin: 0 }}>
            <ShoppingCartOutlined style={{ marginRight: 8 }} />
            白圭智慧收银台
          </Title>
          <Space>
            <Text style={{ color: 'white' }}>
              <ClockCircleOutlined style={{ marginRight: 4 }} />
              {new Date().toLocaleString()}
            </Text>
            <Button type="text" icon={<SettingOutlined />} style={{ color: 'white' }} />
          </Space>
        </Row>
      </Header>

      <Layout>
        <Content style={{ padding: '20px' }}>
          <Row gutter={16}>
            {/* 左侧：商品录入和购物车 */}
            <Col span={16}>
              <Card 
                title={
                  <Space>
                    <ScanOutlined />
                    商品录入
                  </Space>
                }
                style={{ marginBottom: 16 }}
                extra={
                  <Badge count={itemCount} showZero>
                    <ShoppingCartOutlined style={{ fontSize: 18 }} />
                  </Badge>
                }
              >
                <Space.Compact style={{ width: '100%' }}>
                  <Input
                    ref={barcodeInputRef}
                    placeholder="请输入或扫描商品条码"
                    value={barcode}
                    onChange={(e) => setBarcode(e.target.value)}
                    onPressEnter={addToCart}
                    prefix={<BarcodeOutlined />}
                    size="large"
                  />
                  <Button type="primary" onClick={addToCart} size="large">
                    添加
                  </Button>
                </Space.Compact>
                
                <div style={{ marginTop: 16 }}>
                  <Text type="secondary">快速添加：</Text>
                  <div style={{ marginTop: 8 }}>
                    {Object.entries(products).map(([code, product]) => (
                      <QuickAddButton key={code} barcode={code} product={product} />
                    ))}
                  </div>
                </div>
              </Card>

              <Card 
                title={
                  <Space>
                    <ShoppingCartOutlined />
                    购物车
                    <Badge count={cart.length} showZero />
                  </Space>
                }
                extra={
                  <Space>
                    <Button 
                      type="text" 
                      danger 
                      icon={<ClearOutlined />}
                      onClick={clearCart}
                      disabled={cart.length === 0}
                    >
                      清空
                    </Button>
                  </Space>
                }
              >
                {cart.length === 0 ? (
                  <Empty 
                    description="购物车为空" 
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                  />
                ) : (
                  <Table
                    columns={columns}
                    dataSource={cart}
                    pagination={false}
                    size="small"
                    rowKey="barcode"
                  />
                )}
              </Card>
            </Col>

            {/* 右侧：结算信息 */}
            <Col span={8}>
              <Card title="结算信息" style={{ marginBottom: 16 }}>
                <Space direction="vertical" style={{ width: '100%' }} size="large">
                  <Row justify="space-between">
                    <Statistic 
                      title="商品数量" 
                      value={itemCount} 
                      suffix="件"
                      valueStyle={{ fontSize: 16 }}
                    />
                    <Statistic 
                      title="商品种类" 
                      value={cart.length} 
                      suffix="种"
                      valueStyle={{ fontSize: 16 }}
                    />
                  </Row>
                  
                  <Divider />
                  
                  <div style={{ textAlign: 'center' }}>
                    <Statistic
                      title="总计金额"
                      value={total}
                      precision={2}
                      valueStyle={{ 
                        fontSize: 32, 
                        color: '#f5222d',
                        fontWeight: 'bold'
                      }}
                      prefix="¥"
                    />
                  </div>

                  <Space direction="vertical" style={{ width: '100%' }}>
                    <Button
                      type="primary"
                      size="large"
                      block
                      icon={<DollarOutlined />}
                      onClick={handleCheckout}
                      disabled={cart.length === 0}
                      style={{ height: 50, fontSize: 16 }}
                    >
                      结算
                    </Button>
                    <Button
                      size="large"
                      block
                      icon={<PrinterOutlined />}
                      disabled={cart.length === 0}
                    >
                      打印小票
                    </Button>
                  </Space>
                </Space>
              </Card>

              {/* 最近订单 */}
              <Card 
                title={
                  <Space>
                    <HistoryOutlined />
                    最近订单
                  </Space>
                }
                size="small"
              >
                <List
                  size="small"
                  dataSource={orderHistory.slice(0, 5)}
                  renderItem={(order) => (
                    <List.Item>
                      <List.Item.Meta
                        avatar={<Avatar icon={<CheckCircleOutlined />} style={{ backgroundColor: '#52c41a' }} />}
                        title={`订单 #${order.id}`}
                        description={
                          <Space direction="vertical" size="small">
                            <Text type="secondary">{order.timestamp}</Text>
                            <Text>¥{order.total.toFixed(2)}</Text>
                          </Space>
                        }
                      />
                    </List.Item>
                  )}
                  locale={{ emptyText: '暂无订单' }}
                />
              </Card>
            </Col>
          </Row>
        </Content>
      </Layout>

      {/* 支付弹窗 */}
      <Modal
        title={
          <Space>
            <DollarOutlined />
            收款
          </Space>
        }
        open={paymentModalVisible}
        onCancel={() => setPaymentModalVisible(false)}
        footer={[
          <Button key="cancel" onClick={() => setPaymentModalVisible(false)}>
            取消
          </Button>,
          <Button 
            key="confirm" 
            type="primary" 
            onClick={completePayment}
            loading={isLoading}
            disabled={receivedAmount < total}
          >
            确认收款
          </Button>
        ]}
        width={500}
      >
        <Space direction="vertical" style={{ width: '100%' }} size="large">
          <Alert
            message="订单信息"
            description={`共 ${itemCount} 件商品，${cart.length} 种商品`}
            type="info"
            showIcon
          />
          
          <div style={{ fontSize: 16 }}>
            <Row justify="space-between" style={{ marginBottom: 16 }}>
              <Text>应收金额：</Text>
              <Text strong style={{ fontSize: 20, color: '#f5222d' }}>
                ¥{total.toFixed(2)}
              </Text>
            </Row>
            
            <div style={{ marginBottom: 16 }}>
              <Text>实收金额：</Text>
              <InputNumber
                style={{ width: '100%', marginTop: 8 }}
                placeholder="请输入实收金额"
                value={receivedAmount}
                onChange={setReceivedAmount}
                min={0}
                step={0.01}
                precision={2}
                size="large"
                prefix="¥"
              />
            </div>
            
            {receivedAmount >= total && (
              <Alert
                message="找零信息"
                description={`找零：¥${(receivedAmount - total).toFixed(2)}`}
                type="success"
                showIcon
              />
            )}
            
            {receivedAmount < total && receivedAmount > 0 && (
              <Alert
                message="金额不足"
                description={`还需：¥${(total - receivedAmount).toFixed(2)}`}
                type="warning"
                showIcon
              />
            )}
          </div>
        </Space>
      </Modal>
    </Layout>
  );
};