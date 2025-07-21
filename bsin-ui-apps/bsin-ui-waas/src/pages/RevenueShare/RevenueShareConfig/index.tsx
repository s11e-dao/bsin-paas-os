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
  Modal,
  Descriptions
} from 'antd';
import {
  SaveOutlined,
  CalculatorOutlined,
  TableOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { getLocalStorageInfo } from '../../../utils/localStorageInfo';
import { configProfitSharingConfig, getProfitSharingConfigDetail, getProfitSharingConfigPageList } from './service';
import { 
  ProfitSharingConfigDataType, 
  createProfitSharingConfigColumnsData, 
  ConfigItemData, 
  FormValues, 
  defaultFormValues,
  configItemsData
} from './revenueShareConfigData';
import {
  BankOutlined,
  ShopOutlined,
  TeamOutlined,
  UserOutlined,
  GiftOutlined,
  DollarOutlined
} from '@ant-design/icons';

const { Title, Text } = Typography;

export default () => {
  const userInfo = getLocalStorageInfo('userInfo');
  const [formRef] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'config' | 'list'>('config');
  
  // 查看模态框
  const [isViewModal, setIsViewModal] = useState(false);
  // 查看记录
  const [viewRecord, setViewRecord] = useState<ProfitSharingConfigDataType>({} as ProfitSharingConfigDataType);

  const [formValues, setFormValues] = useState<FormValues>(defaultFormValues);

  // 计算总比例
  const totalRate = Object.values(formValues).reduce((sum: number, rate: any) => sum + (rate || 0), 0);
  const isValidTotal = totalRate === 100;

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 查看详情
   */
  const toViewConfig = async (record: ProfitSharingConfigDataType) => {
    let { serialNo } = record;
    let viewRes = await getProfitSharingConfigDetail({ serialNo });
    setIsViewModal(true);
    console.log('viewRes', viewRes);
    setViewRecord(viewRes.data || record);
  };

  // 创建表格列配置
  const columnsData = createProfitSharingConfigColumnsData();
  const columns = [
    ...columnsData.slice(0, -1), // 除了操作列的所有列
    {
      ...columnsData[columnsData.length - 1], // 操作列
      render: (text: any, record: any) => (
        <ul className="ant-list-item-action" style={{ margin: 0 }}>
          <li>
            <a
              onClick={() => {
                toViewConfig(record);
              }}
            >
              查看
            </a>
          </li>
        </ul>
      ),
    }
  ];

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
                    <Text><TeamOutlined style={{ color: '#faad14', marginRight: 8 }} />合伙人：</Text>
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
                  // 刷新列表
                  actionRef.current?.reload();
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

  /**
   * 读取配置
   */
  const loadProfitSharingConfig = async () => {
    setLoading(true);
    try {
      const res = await getProfitSharingConfigDetail({});
      if (res?.data) {
        formRef.setFieldsValue(res?.data);
        setFormValues(res?.data);
        message.success('配置读取成功！');
      } else {
        message.info('暂无配置数据');
      }
    } catch (error) {
      message.error('读取配置失败，请重试');
      console.error('读取配置失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      <Row gutter={[24, 24]}>
        <Col span={24}>
          <Card 
            title={
              <Row justify="space-between" align="middle">
                <Col>
                  <Space>
                    <BankOutlined />
                    分账比例设置
                  </Space>
                </Col>
                <Col>
                  <Space>
                    <Button
                      type={activeTab === 'config' ? 'primary' : 'default'}
                      icon={<CalculatorOutlined />}
                      onClick={() => setActiveTab('config')}
                    >
                      配置设置
                    </Button>
                    <Button
                      type={activeTab === 'list' ? 'primary' : 'default'}
                      icon={<TableOutlined />}
                      onClick={() => setActiveTab('list')}
                    >
                      配置列表
                    </Button>
                  </Space>
                </Col>
              </Row>
            }
          >
            {activeTab === 'config' && (
              <>
                <Row justify="space-between" align="middle" style={{ marginBottom: 16 }}>
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
                    {configItemsData.map((item: ConfigItemData, index: number) => {
                      // 根据iconName动态获取图标组件
                      const getIcon = (iconName: string) => {
                        switch (iconName) {
                          case 'BankOutlined': return <BankOutlined />;
                          case 'ShopOutlined': return <ShopOutlined />;
                          case 'TeamOutlined': return <TeamOutlined />;
                          case 'UserOutlined': return <UserOutlined />;
                          case 'GiftOutlined': return <GiftOutlined />;
                          case 'DollarOutlined': return <DollarOutlined />;
                          default: return <BankOutlined />;
                        }
                      };
                      
                      return (
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
                                  {getIcon(item.iconName)}
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
                      );
                    })}
                  </Row>

                  <Divider />

                  <Row justify="center" gutter={16}>
                    <Col>
                      <Button
                        type="default"
                        size="large"
                        loading={loading}
                        onClick={loadProfitSharingConfig}
                        icon={<ReloadOutlined />}
                        style={{
                          height: 48,
                          paddingLeft: 32,
                          paddingRight: 32,
                          fontSize: 16,
                        }}
                      >
                        读取配置
                      </Button>
                    </Col>
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
              </>
            )}

            {activeTab === 'list' && (
              <ProTable<ProfitSharingConfigDataType>
                scroll={{ x: 1200 }}
                bordered
                columns={columns}
                actionRef={actionRef}
                request={async (params) => {
                  let res = await getProfitSharingConfigPageList({
                    ...params,
                    pagination: {
                      pageNum: params.current,
                      pageSize: params.pageSize,
                    },
                  });
                  console.log('分账配置列表', res);
                  const result = {
                    data: res.data?.records || res.data || [],
                    total: res.data?.total || res.pagination?.totalSize || 0,
                  };
                  return result;
                }}
                rowKey="serialNo"
                search={{
                  labelWidth: 'auto',
                  collapsed: false,
                  collapseRender: (collapsed) => (collapsed ? '展开' : '收起'),
                }}
                form={{
                  ignoreRules: false,
                }}
                pagination={{
                  pageSize: 10,
                }}
                toolBarRender={() => []}
              />
            )}
          </Card>
        </Col>
      </Row>

      {/* 查看详情模态框 */}
      <Modal
        title="查看分账配置详情"
        width={800}
        centered
        open={isViewModal}
        onOk={() => setIsViewModal(false)}
        onCancel={() => setIsViewModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="分账配置信息" bordered>
          <Descriptions.Item label="配置ID" span={2}>
            {viewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID" span={2}>
            {viewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="运营平台分佣比例">
            {viewRecord?.superTenantRate}%
          </Descriptions.Item>
          <Descriptions.Item label="租户平台分佣比例">
            {viewRecord?.tenantRate}%
          </Descriptions.Item>
          <Descriptions.Item label="合伙人分佣比例">
            {viewRecord?.sysAgentRate}%
          </Descriptions.Item>
          <Descriptions.Item label="消费者返利比例">
            {viewRecord?.customerRate}%
          </Descriptions.Item>
          <Descriptions.Item label="分销者分佣比例">
            {viewRecord?.distributorRate}%
          </Descriptions.Item>
          <Descriptions.Item label="数字积分兑换比例">
            {viewRecord?.exchangeDigitalPointsRate}%
          </Descriptions.Item>
          <Descriptions.Item label="创建时间" span={2}>
            {viewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间" span={2}>
            {viewRecord?.updateTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};