import React, { useState, useEffect } from 'react';
import {
  Card,
  Button,
  Modal,
  Form,
  Input,
  Select,
  Space,
  Tag,
  Row,
  Col,
  Typography,
  Divider,
  message,
  Popconfirm,
  Avatar,
  Switch,
  Alert,
  Empty,
  Tooltip,
  Badge,
  Spin,
  notification,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  BankOutlined,
  CreditCardOutlined,
  UserOutlined,
  PhoneOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SettingOutlined,
  EyeOutlined,
  StarOutlined,
  StarFilled,
  ReloadOutlined,
} from '@ant-design/icons';
import type { WithdrawAccountType } from './types';
import { withdrawAccountService, withdrawSettingService } from './service';

const { Title, Text, Paragraph } = Typography;
const { Option } = Select;

// 账号类型配置
const accountTypeConfig = {
  bank: {
    name: '银行卡',
    icon: <BankOutlined style={{ color: '#1890ff' }} />,
    color: '#1890ff',
    bgColor: '#f0f8ff',
  },
  alipay: {
    name: '支付宝',
    icon: <CreditCardOutlined style={{ color: '#1677ff' }} />,
    color: '#1677ff',
    bgColor: '#f0f8ff',
  },
  wechat: {
    name: '微信支付',
    icon: <CreditCardOutlined style={{ color: '#52c41a' }} />,
    color: '#52c41a',
    bgColor: '#f6ffed',
  },
};

const WithdrawSetting: React.FC = () => {
  // 状态管理
  const [accounts, setAccounts] = useState<WithdrawAccountType[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingAccount, setEditingAccount] = useState<WithdrawAccountType | null>(null);
  const [form] = Form.useForm();

  // 获取账号列表
  const fetchAccounts = async () => {
    setLoading(true);
    try {
      // Mock 数据
      const mockAccounts: WithdrawAccountType[] = [
        {
          serialNo: '1',
          accountType: 'bank',
          accountName: '中国工商银行',
          accountNumber: '6222 **** **** 1234',
          bankName: '中国工商银行',
          bankBranch: '北京分行',
          status: 'active',
          isDefault: true,
          createTime: '2024-01-15 10:30:00',
          updateTime: '2024-01-15 10:30:00',
        },
        {
          serialNo: '2',
          accountType: 'alipay',
          accountName: '支付宝',
          accountNumber: '138****8888',
          bankName: '支付宝',
          bankBranch: '',
          status: 'active',
          isDefault: false,
          createTime: '2024-01-10 14:20:00',
          updateTime: '2024-01-10 14:20:00',
        },
      ];

      // 模拟网络延迟
      await new Promise(resolve => setTimeout(resolve, 500));
      
      setAccounts(mockAccounts);
      
      // 注释掉真实 API 调用，使用 mock 数据
      /*
      const response = await withdrawAccountService.getWithdrawAccountList({
        pageNum: 1,
        pageSize: 100,
      });
      
      if (response.code === 0 && response.data) {
        setAccounts(response.data);
      } else {
        message.error(response.message || '获取账号列表失败');
      }
      */
    } catch (error) {
      console.error('获取账号列表失败:', error);
      message.error('获取账号列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 组件挂载时获取数据
  useEffect(() => {
    fetchAccounts();
  }, []);

  // 获取状态信息
  const getStatusInfo = (status: string) => {
    switch (status) {
      case 'active':
        return {
          text: '正常',
          color: 'success',
          icon: <CheckCircleOutlined />,
        };
      case 'inactive':
        return {
          text: '禁用',
          color: 'default',
          icon: <CloseCircleOutlined />,
        };
      case 'pending':
        return {
          text: '审核中',
          color: 'processing',
          icon: <SettingOutlined />,
        };
      default:
        return {
          text: '未知',
          color: 'default',
          icon: <CloseCircleOutlined />,
        };
    }
  };

  // 获取账号类型信息
  const getAccountTypeInfo = (type: string) => {
    return accountTypeConfig[type as keyof typeof accountTypeConfig] || accountTypeConfig.bank;
  };

  // 显示添加/编辑模态框
  const showModal = (account?: WithdrawAccountType) => {
    if (account) {
      setEditingAccount(account);
      form.setFieldsValue({
        accountType: account.accountType,
        accountName: account.accountName,
        accountNumber: account.accountNumber.replace(/\*/g, ''),
        bankName: account.bankName,
        bankBranch: account.bankBranch,
      });
    } else {
      setEditingAccount(null);
      form.resetFields();
    }
    setIsModalVisible(true);
  };

  // 隐藏模态框
  const handleCancel = () => {
    setIsModalVisible(false);
    form.resetFields();
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingAccount) {
        // 编辑账号 - Mock 实现
        const updatedAccounts = (accounts || []).map(account =>
          account.serialNo === editingAccount.serialNo
            ? {
                ...account,
                ...values,
                accountNumber: values.accountType === 'bank'
                  ? values.accountNumber.slice(0, 4) + ' **** **** ' + values.accountNumber.slice(-4)
                  : values.accountNumber.slice(0, 3) + '****' + values.accountNumber.slice(-4),
                updateTime: new Date().toLocaleString(),
              }
            : account
        );
        setAccounts(updatedAccounts);
        message.success('账号更新成功');
        
        // 注释掉真实 API 调用
        /*
        try {
          const response = await withdrawAccountService.updateWithdrawAccount({
            serialNo: editingAccount.serialNo,
            ...values,
          });
          
          if (response.code === 0) {
            message.success('账号更新成功');
            fetchAccounts(); // 重新获取列表
          } else {
            message.error(response.message || '账号更新失败');
          }
        } catch (error) {
          console.error('更新账号失败:', error);
          message.error('账号更新失败');
        }
        */
      } else {
        // 添加新账号 - Mock 实现
        const newAccount: WithdrawAccountType = {
          serialNo: Date.now().toString(),
          ...values,
          accountNumber: values.accountType === 'bank'
            ? values.accountNumber.slice(0, 4) + ' **** **** ' + values.accountNumber.slice(-4)
            : values.accountNumber.slice(0, 3) + '****' + values.accountNumber.slice(-4),
          status: 'active',
          isDefault: (accounts?.length || 0) === 0,
          createTime: new Date().toLocaleString(),
          updateTime: new Date().toLocaleString(),
        };
        setAccounts([...(accounts || []), newAccount]);
        message.success('账号添加成功');
        
        // 注释掉真实 API 调用
        /*
        try {
          const response = await withdrawAccountService.createWithdrawAccount({
            ...values,
            status: 'active',
            isDefault: accounts.length === 0,
          });
          
          if (response.code === 0) {
            message.success('账号添加成功');
            fetchAccounts(); // 重新获取列表
          } else {
            message.error(response.message || '账号添加失败');
          }
        } catch (error) {
          console.error('添加账号失败:', error);
          message.error('账号添加失败');
        }
        */
      }
      
      setIsModalVisible(false);
      form.resetFields();
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  // 删除账号
  const handleDelete = async (serialNo: string) => {
    const account = (accounts || []).find(acc => acc.serialNo === serialNo);
    if (account?.isDefault) {
      message.error('不能删除默认账号');
      return;
    }
    
    // Mock 实现
    setAccounts((accounts || []).filter(acc => acc.serialNo !== serialNo));
    message.success('账号删除成功');
    
    // 注释掉真实 API 调用
    /*
    try {
      const response = await withdrawAccountService.deleteWithdrawAccount(serialNo);
      
      if (response.code === 0) {
        message.success('账号删除成功');
        fetchAccounts(); // 重新获取列表
      } else {
        message.error(response.message || '账号删除失败');
      }
    } catch (error) {
      console.error('删除账号失败:', error);
      message.error('账号删除失败');
    }
    */
  };

  // 设置默认账号
  const handleSetDefault = async (serialNo: string) => {
    // Mock 实现
    setAccounts((accounts || []).map(account => ({
      ...account,
      isDefault: account.serialNo === serialNo,
    })));
    message.success('默认账号设置成功');
    
    // 注释掉真实 API 调用
    /*
    try {
      const response = await withdrawAccountService.setDefaultWithdrawAccount(serialNo);
      
      if (response.code === 0) {
        message.success('默认账号设置成功');
        fetchAccounts(); // 重新获取列表
      } else {
        message.error(response.message || '设置默认账号失败');
      }
    } catch (error) {
      console.error('设置默认账号失败:', error);
      message.error('设置默认账号失败');
    }
    */
  };

  // 切换账号状态
  const handleToggleStatus = async (serialNo: string) => {
    const account = (accounts || []).find(acc => acc.serialNo === serialNo);
    if (account?.isDefault && account.status === 'active') {
      message.error('不能禁用默认账号');
      return;
    }
    
    const newStatus = account?.status === 'active' ? 'inactive' : 'active';
    
    // Mock 实现
    setAccounts((accounts || []).map(account =>
      account.serialNo === serialNo
        ? { ...account, status: newStatus }
        : account
    ));
    message.success('账号状态更新成功');
    
    // 注释掉真实 API 调用
    /*
    try {
      const response = await withdrawAccountService.toggleWithdrawAccountStatus(serialNo, newStatus);
      
      if (response.code === 0) {
        message.success('账号状态更新成功');
        fetchAccounts(); // 重新获取列表
      } else {
        message.error(response.message || '账号状态更新失败');
      }
    } catch (error) {
      console.error('更新账号状态失败:', error);
      message.error('账号状态更新失败');
    }
    */
  };

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      <Card>
        {/* 页面标题 */}
        <div style={{ marginBottom: '24px' }}>
          <Title level={4} style={{ margin: 0 }}>
            提现账号管理
          </Title>
          <Text type="secondary">
            管理您的提现账号信息，支持银行卡、支付宝、微信支付等多种方式
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
              添加账号
            </Button>
          </Space>
        </div>

        {/* 账号列表 */}
        <Spin spinning={loading}>
          {(accounts?.length || 0) === 0 ? (
            <Empty
              description="暂无提现账号"
              image={Empty.PRESENTED_IMAGE_SIMPLE}
            >
              <Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>
                添加第一个账号
              </Button>
            </Empty>
          ) : (
          <Row gutter={[16, 16]}>
            {(accounts || []).map((account) => {
              const statusInfo = getStatusInfo(account.status);
              const typeInfo = getAccountTypeInfo(account.accountType);
              
              return (
                <Col xs={24} sm={12} lg={8} xl={6} key={account.serialNo}>
                  <Card
                    hoverable
                    style={{
                      border: account.isDefault ? '2px solid #1890ff' : undefined,
                      background: account.isDefault ? '#f0f8ff' : undefined,
                      height: '220px',
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
                    {/* 账号头部 */}
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
                        {account.isDefault && (
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

                    {/* 账号信息 */}
                    <div style={{ marginBottom: '16px', flex: 1 }}>
                      <div style={{ marginBottom: '8px' }}>
                        <Text type="secondary" style={{ fontSize: '12px' }}>账号：</Text>
                        <Text copyable={{ text: account.accountNumber }}>
                          {account.accountNumber}
                        </Text>
                      </div>
                      
                      {account.bankBranch && (
                        <div style={{ marginBottom: '8px' }}>
                          <Text type="secondary" style={{ fontSize: '12px' }}>开户行：</Text>
                          <Text>{account.bankBranch}</Text>
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
                        {!account.isDefault && (
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
                          checked={account.status === 'active'}
                          onChange={() => handleToggleStatus(account.serialNo)}
                          checkedChildren="启用"
                          unCheckedChildren="禁用"
                        />
                      </Space>
                      
                      <Space size="small">
                        <Tooltip title="编辑">
                          <Button
                            type="text"
                            size="small"
                            icon={<EditOutlined />}
                            onClick={() => showModal(account)}
                          />
                        </Tooltip>
                        
                        <Popconfirm
                          title="确定要删除这个账号吗？"
                          description="删除后无法恢复，请谨慎操作"
                          onConfirm={() => handleDelete(account.serialNo)}
                          okText="确定"
                          cancelText="取消"
                        >
                          <Tooltip title="删除">
                            <Button
                              type="text"
                              size="small"
                              danger
                              icon={<DeleteOutlined />}
                              disabled={account.isDefault}
                            />
                          </Tooltip>
                        </Popconfirm>
                      </Space>
                    </div>
                  </Card>
                </Col>
              );
            }            )}
          </Row>
        )}
        </Spin>
      </Card>

      {/* 添加/编辑模态框 */}
      <Modal
        title={editingAccount ? '编辑提现账号' : '添加提现账号'}
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
          initialValues={{
            accountType: 'bank',
          }}
        >
          <Form.Item
            name="accountType"
            label="账号类型"
            rules={[{ required: true, message: '请选择账号类型' }]}
          >
            <Select placeholder="请选择账号类型">
              <Option value="bank">
                <Space>
                  <BankOutlined style={{ color: '#1890ff' }} />
                  银行卡
                </Space>
              </Option>
              <Option value="alipay">
                <Space>
                  <CreditCardOutlined style={{ color: '#1677ff' }} />
                  支付宝
                </Space>
              </Option>
              <Option value="wechat">
                <Space>
                  <CreditCardOutlined style={{ color: '#52c41a' }} />
                  微信支付
                </Space>
              </Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="accountName"
            label="账号名称"
            rules={[{ required: true, message: '请输入账号名称' }]}
          >
            <Input placeholder="如：中国工商银行" />
          </Form.Item>

          <Form.Item
            name="accountNumber"
            label="账号号码"
            rules={[
              { required: true, message: '请输入账号号码' },
              { min: 4, message: '账号号码至少4位' },
            ]}
          >
            <Input placeholder="请输入账号号码" />
          </Form.Item>

          <Form.Item
            name="bankName"
            label="银行名称"
            rules={[{ required: true, message: '请输入银行名称' }]}
          >
            <Input placeholder="请输入银行名称" />
          </Form.Item>

          <Form.Item
            name="bankBranch"
            label="开户行"
          >
            <Input placeholder="请输入开户行（选填）" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default WithdrawSetting; 