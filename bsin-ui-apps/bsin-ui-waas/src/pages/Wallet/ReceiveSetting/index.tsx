import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Card,
  Space,
  Tag,
  Typography,
  Tooltip,
  Empty,
  Spin,
  Row,
  Col,
  Avatar,
  Switch,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  WalletOutlined,
  CreditCardOutlined,
  SettingOutlined,
  StarOutlined,
  StarFilled,
  ReloadOutlined,
  EyeOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
} from '@ant-design/icons';
import { ReceiveAccountType } from './data';
import {
  getSettlementAccountList,
  addSettlementAccount,
  updateSettlementAccount,
  deleteSettlementAccount,
  setDefaultSettlementAccount,
  getSettlementAccountDetail,
  getChainCoinList,
} from './service';

const { Title, Text } = Typography;
const { Option } = Select;

// 链类型配置
const chainTypeConfig = {
  ETH: {
    name: '以太坊',
    color: '#627EEA',
    bgColor: '#f0f4ff',
  },
  BSC: {
    name: '币安智能链',
    color: '#F3BA2F',
    bgColor: '#fffbf0',
  },
  POLYGON: {
    name: 'Polygon',
    color: '#8247E5',
    bgColor: '#f9f0ff',
  },
  ARBITRUM: {
    name: 'Arbitrum',
    color: '#28A0F0',
    bgColor: '#f0f9ff',
  },
};

export default () => {
  const { TextArea } = Input;
  // 账户列表
  const [accounts, setAccounts] = useState<ReceiveAccountType[]>([]);
  const [loading, setLoading] = useState(false);
  // 模态框控制
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isViewModalVisible, setIsViewModalVisible] = useState(false);
  // 编辑模式
  const [editingAccount, setEditingAccount] = useState<ReceiveAccountType | null>(null);
  // 查看详情
  const [viewRecord, setViewRecord] = useState<ReceiveAccountType>({} as ReceiveAccountType);
  // 表单
  const [form] = Form.useForm();
  
  // 币种相关
  const [chainCoins, setChainCoins] = useState<any[]>([]);
  const [loadingCoins, setLoadingCoins] = useState(false);

  // 获取账户列表
  const fetchAccounts = async () => {
    setLoading(true);
    try {
      const response = await getSettlementAccountList({
        pagination: {
          pageNum: 1,
          pageSize: 100,
        },
      });
      if (response.code === 0) {
        // 处理分页数据，后端返回的是 IPage 对象
        let accountData = [];
        if (response.data) {
          if (response.data.records) {
            // 分页数据格式
            accountData = response.data.records;
          } else if (Array.isArray(response.data)) {
            // 直接数组格式
            accountData = response.data;
          } else {
            // 其他格式
            accountData = [];
          }
        }
        setAccounts(accountData);
      } else {
        message.error(response.message || '获取结算账户列表失败');
      }
    } catch (error) {
      console.error('获取结算账户列表失败:', error);
      message.error('获取结算账户列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 获取币种列表
  const fetchChainCoins = async () => {
    setLoadingCoins(true);
    try {
      const response = await getChainCoinList({});
      if (response.code === 0 && response.data && Array.isArray(response.data)) {
        setChainCoins(response.data);
      } else {
        message.error('获取币种列表失败');
      }
    } catch (error) {
      console.error('获取币种列表失败:', error);
      message.error('获取币种列表失败');
    } finally {
      setLoadingCoins(false);
    }
  };

  // 组件挂载时获取数据
  useEffect(() => {
    fetchAccounts();
    fetchChainCoins();
  }, []);

  // 获取状态信息
  const getStatusInfo = (status: string) => {
    switch (status) {
      case '1':
        return {
          text: '待审核',
          color: 'processing',
          icon: <SettingOutlined />,
        };
      case '2':
        return {
          text: '审核通过',
          color: 'success',
          icon: <CheckCircleOutlined />,
        };
      default:
        return {
          text: '未知',
          color: 'default',
          icon: <CloseCircleOutlined />,
        };
    }
  };

  // 获取账户类型信息
  const getAccountTypeInfo = (accountType: number) => {
    const typeMap = {
      1: { name: '多签账户', icon: <WalletOutlined />, color: '#1890ff', bgColor: '#f0f8ff' },
      2: { name: '普通账户', icon: <CreditCardOutlined />, color: '#52c41a', bgColor: '#f6ffed' },
    };
    return typeMap[accountType as keyof typeof typeMap] || typeMap[1];
  };

  // 根据币种ID获取币种名称
  const getCoinName = (chainCoinNo: string) => {
    if (!chainCoinNo) return '未设置';
    const coin = chainCoins.find(c => c.serialNo === chainCoinNo);
    return coin ? `${coin.chainCoinName} (${coin.chainIdentifier}/${coin.coin})` : chainCoinNo;
  };

  /**
   * 查看账户详情
   */
  const handleViewAccount = async (record: ReceiveAccountType) => {
    try {
      const response = await getSettlementAccountDetail({ serialNo: record.serialNo });
      if (response.code === 0 && response.data) {
        setViewRecord(response.data);
        setIsViewModalVisible(true);
      } else {
        message.error(response.message || '获取账户详情失败');
      }
    } catch (error) {
      console.error('获取账户详情失败:', error);
      message.error('获取账户详情失败');
    }
  };

  /**
   * 显示添加/编辑模态框
   */
  const showModal = (account?: ReceiveAccountType) => {
    if (account) {
      setEditingAccount(account);
      form.setFieldsValue({
        accountName: account.accountName,
        accountNum: account.accountNum,
        accountType: account.accountType,
        category: account.category,
        chainCoinNo: account.chainCoinNo,
        remark: account.remark,
      });
    } else {
      setEditingAccount(null);
      form.resetFields();
      // 设置默认值
      form.setFieldsValue({
        category: '1', // 默认支付结算
      });
    }
    setIsModalVisible(true);
  };

  /**
   * 隐藏模态框
   */
  const handleCancel = () => {
    setIsModalVisible(false);
    setIsViewModalVisible(false);
    form.resetFields();
  };

  /**
   * 提交表单 - 添加或编辑账户
   */
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingAccount) {
        // 编辑账户
        const response = await updateSettlementAccount({
          serialNo: editingAccount.serialNo,
          ...values,
        });
        
        if (response.code === 0) {
          message.success('结算账户更新成功');
          fetchAccounts(); // 重新获取列表
        } else {
          message.error(response.message || '结算账户更新失败');
        }
      } else {
        // 添加新账户 - 后端会自动处理默认账户设置
        const response = await addSettlementAccount({
          ...values,
        });
        
        if (response.code === 0) {
          message.success('结算账户添加成功');
          fetchAccounts(); // 重新获取列表
        } else {
          message.error(response.message || '结算账户添加失败');
        }
      }
      
      setIsModalVisible(false);
      form.resetFields();
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  /**
   * 删除账户
   */
  const handleDelete = async (serialNo: string) => {
    try {
      const response = await deleteSettlementAccount({ serialNo });
      
      if (response.code === 0) {
        message.success('结算账户删除成功');
        fetchAccounts(); // 重新获取列表
      } else {
        message.error(response.message || '结算账户删除失败');
      }
    } catch (error) {
      console.error('删除结算账户失败:', error);
      message.error('结算账户删除失败');
    }
  };

  /**
   * 设置默认账户
   */
  const handleSetDefault = async (serialNo: string) => {
    try {
      const response = await setDefaultSettlementAccount({ serialNo });
      
      if (response.code === 0) {
        message.success('默认结算账户设置成功');
        fetchAccounts(); // 重新获取列表
      } else {
        message.error(response.message || '设置默认结算账户失败');
      }
    } catch (error) {
      console.error('设置默认结算账户失败:', error);
      message.error('设置默认结算账户失败');
    }
  };

  /**
   * 切换账户状态
   */
  const handleToggleStatus = async (serialNo: string) => {
    const account = accounts.find(acc => acc.serialNo === serialNo);
    if (account?.defaultFlag === '1' && account.status === '2') {
      message.error('不能禁用默认账户');
      return;
    }
    
    // 这里应该调用切换状态的API，暂时只是提示
    message.info('状态切换功能待实现');
  };


  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      <Card>
        {/* 页面标题 */}
        <div style={{ marginBottom: '24px' }}>
          <Title level={4} style={{ margin: 0 }}>
            链结算账户设置
          </Title>
          <Text type="secondary">
            管理链上资金结算账户，支持IBAN账户、电子账户、微信、支付宝等多种账户类型
          </Text>
        </div>

        {/* 操作按钮 */}
        <div style={{ marginBottom: '24px', textAlign: 'right' }}>
          <Space>
            <Button
              icon={<ReloadOutlined />}
              onClick={fetchAccounts}
              loading={loading}
            >
              刷新
            </Button>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => showModal()}
              size="middle"
            >
              添加结算账户
            </Button>
          </Space>
        </div>

        {/* 账户卡片视图 */}
        <Spin spinning={loading}>
          {(accounts?.length || 0) === 0 ? (
            <Empty
              description="暂无结算账户"
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            >
              <Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>
                添加第一个结算账户
              </Button>
            </Empty>
          ) : (
            <Row gutter={[16, 16]}>
              {accounts.map((account) => {
                const statusInfo = getStatusInfo(account.status);
                const typeInfo = getAccountTypeInfo(account.accountType);
                
                return (
                  <Col xs={24} sm={12} lg={8} xl={6} key={account.serialNo}>
                    <Card
                      hoverable
                      style={{
                        border: account.defaultFlag === '1' ? '2px solid #1890ff' : undefined,
                        background: account.defaultFlag === '1' ? '#f0f8ff' : undefined,
                        height: '220px',
                        minWidth: '300px',
                        display: 'flex',
                        flexDirection: 'column',
                      }}
                      bodyStyle={{ 
                        padding: '16px',
                        flex: 1,
                        display: 'flex',
                        flexDirection: 'column',
                      }}
                    >
                      {/* 账户头部 */}
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                        <div style={{ display: 'flex', alignItems: 'center' }}>
                          <Avatar
                            size={40}
                            style={{ backgroundColor: typeInfo.bgColor, color: typeInfo.color }}
                          >
                            {typeInfo.icon}
                          </Avatar>
                          <div style={{ marginLeft: '12px' }}>
                            <div style={{ fontWeight: 600, fontSize: '16px' }}>
                              {account.accountName}
                            </div>
                            <div style={{ color: '#666', fontSize: '12px' }}>
                              {typeInfo.name}
                            </div>
                          </div>
                        </div>
                        
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '4px' }}>
                          {account.defaultFlag === '1' && (
                            <Tag color="blue" icon={<StarFilled />}>
                              默认
                            </Tag>
                          )}
                          <Tag
                            color={statusInfo.color}
                            icon={statusInfo.icon}
                          >
                            {statusInfo.text}
                          </Tag>
                        </div>
                      </div>

                      {/* 账户信息 */}
                      <div style={{ marginBottom: '16px', flex: 1 }}>
                        <div style={{ marginBottom: '8px' }}>
                          <Text type="secondary" style={{ fontSize: '12px' }}>链地址：</Text>
                          <Text copyable={{ text: account.accountNum }} ellipsis={{ tooltip: account.accountNum }}>
                            {account.accountNum}
                          </Text>
                        </div>
                        
                        <div style={{ marginBottom: '8px' }}>
                          <Text type="secondary" style={{ fontSize: '12px' }}>账户类型：</Text>
                          <Text>{typeInfo.name}</Text>
                        </div>
                        
                        {account.category && (
                          <div style={{ marginBottom: '8px' }}>
                            <Text type="secondary" style={{ fontSize: '12px' }}>分类：</Text>
                            <Text>{account.category === '1' ? '支付结算' : '提现结算'}</Text>
                          </div>
                        )}
                        
                        {account.chainCoinNo && (
                          <div style={{ marginBottom: '8px' }}>
                            <Text type="secondary" style={{ fontSize: '12px' }}>币种：</Text>
                            <Text>{getCoinName(account.chainCoinNo)}</Text>
                          </div>
                        )}
                        
                        <div style={{ marginBottom: '8px' }}>
                          <Text type="secondary" style={{ fontSize: '12px' }}>创建时间：</Text>
                          <Text>{account.createTime}</Text>
                        </div>
                      </div>

                      {/* 操作按钮 */}
                      <div style={{ 
                        display: 'flex', 
                        justifyContent: 'space-between', 
                        alignItems: 'center',
                        marginTop: 'auto',
                      }}>
                        <Space size="small">
                          {account.defaultFlag !== '1' && (
                            <Button
                              type="link"
                              size="small"
                              icon={<StarOutlined />}
                              onClick={() => handleSetDefault(account.serialNo)}
                            >
                              设为默认
                            </Button>
                          )}
                          
                          <Switch
                            size="small"
                            checked={account.status === '2'}
                            onChange={() => handleToggleStatus(account.serialNo)}
                            checkedChildren="启用"
                            unCheckedChildren="禁用"
                          />
                        </Space>
                        
                        <Space size="small">
                          <Tooltip title="查看详情">
                            <Button
                              type="text"
                              size="small"
                              icon={<EyeOutlined />}
                              onClick={() => handleViewAccount(account)}
                            />
                          </Tooltip>
                          <Tooltip title="编辑">
                            <Button
                              type="text"
                              size="small"
                              icon={<EditOutlined />}
                              onClick={() => showModal(account)}
                            />
                          </Tooltip>
                          <Popconfirm
                            title="确定要删除这个结算账户吗？"
                            description="删除后无法恢复，请谨慎操作"
                            onConfirm={() => handleDelete(account.serialNo)}
                            okText="确定"
                            cancelText="取消"
                          >
                            <Tooltip title={account.defaultFlag === '1' ? '默认账户不可删除' : '删除'}>
                              <Button
                                type="text"
                                size="small"
                                danger
                                icon={<DeleteOutlined />}
                                disabled={account.defaultFlag === '1'}
                              />
                            </Tooltip>
                          </Popconfirm>
                        </Space>
                      </div>
                    </Card>
                  </Col>
                );
              })}
            </Row>
          )}
        </Spin>
      </Card>

      {/* 添加/编辑模态框 */}
      <Modal
        title={editingAccount ? '编辑结算账户' : '添加结算账户'}
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={handleCancel}
        width={600}
        okText={editingAccount ? '更新' : '添加'}
        cancelText="取消"
        destroyOnClose
      >
        <Form
          form={form}
          layout="vertical"
        >
          <Form.Item
            name="accountName"
            label="账户名称"
            rules={[{ required: true, message: '请输入账户名称' }]}
          >
            <Input placeholder="请输入结算账户名称" />
          </Form.Item>

          <Form.Item
            name="accountType"
            label="账户类型"
            rules={[{ required: true, message: '请选择账户类型' }]}
          >
            <Select placeholder="请选择账户类型">
              <Option value={1}>
                <Space>
                  <WalletOutlined style={{ color: '#1890ff' }} />
                  多签账户
                </Space>
              </Option>
              <Option value={2}>
                <Space>
                  <CreditCardOutlined style={{ color: '#52c41a' }} />
                  普通账户
                </Space>
              </Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="accountNum"
            label="链地址"
            rules={[{ required: true, message: '请输入链地址' }]}
          >
            <Input.TextArea 
              placeholder="请输入链地址" 
              rows={2}
            />
          </Form.Item>

          <Form.Item
            name="category"
            label="分类"
            rules={[{ required: true, message: '请选择分类' }]}
          >
            <Select placeholder="请选择分类">
              <Option value="1">支付结算</Option>
              <Option value="2">提现结算</Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="chainCoinNo"
            label="币种"
          >
            <Select 
              placeholder="请选择币种" 
              allowClear
              loading={loadingCoins}
              showSearch
              optionFilterProp="children"
              filterOption={(input, option) =>
                (option?.children as unknown as string)?.toLowerCase().includes(input.toLowerCase())
              }
            >
              {chainCoins.map((coin) => (
                <Option key={coin.serialNo} value={coin.serialNo}>
                  {coin.chainCoinName} ({coin.chainIdentifier}/{coin.coin})
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="remark"
            label="备注"
          >
            <Input.TextArea 
              placeholder="请输入备注信息（选填）" 
              rows={3}
              maxLength={200}
              showCount
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* 查看详情模态框 */}
      <Modal
        title="查看结算账户详情"
        width={800}
        centered
        open={isViewModalVisible}
        onOk={() => setIsViewModalVisible(false)}
        onCancel={() => setIsViewModalVisible(false)}
        footer={[
          <Button key="ok" type="primary" onClick={() => setIsViewModalVisible(false)}>
            确定
          </Button>
        ]}
      >
        <Descriptions title="账户详情" column={2}>
          <Descriptions.Item label="账户ID">
            {viewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="账户名称">
            {viewRecord?.accountName}
          </Descriptions.Item>
          <Descriptions.Item label="链地址" span={2}>
            {viewRecord?.accountNum}
          </Descriptions.Item>
          <Descriptions.Item label="账户类型">
            {viewRecord?.accountType === 1 ? '多签账户' : 
             viewRecord?.accountType === 2 ? '普通账户' : '未知'}
          </Descriptions.Item>
          <Descriptions.Item label="分类">
            {viewRecord?.category === '1' ? '支付结算' : '提现结算'}
          </Descriptions.Item>
          <Descriptions.Item label="币种">
            {getCoinName(viewRecord?.chainCoinNo || '')}
          </Descriptions.Item>
          <Descriptions.Item label="是否默认">
            {viewRecord?.defaultFlag === '1' ? '是' : '否'}
          </Descriptions.Item>
          <Descriptions.Item label="状态">
            {viewRecord?.status === '1' ? '待审核' : '审核通过'}
          </Descriptions.Item>
          <Descriptions.Item label="角色类型">
            {viewRecord?.bizRoleType === '1' ? '运营平台' :
             viewRecord?.bizRoleType === '2' ? '租户平台' :
             viewRecord?.bizRoleType === '4' ? '代理商' :
             viewRecord?.bizRoleType === '5' ? '租户客户' :
             viewRecord?.bizRoleType === '6' ? '门店' : '无'}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {viewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {viewRecord?.updateTime}
          </Descriptions.Item>
          <Descriptions.Item label="备注" span={2}>
            {viewRecord?.remark || '无'}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
