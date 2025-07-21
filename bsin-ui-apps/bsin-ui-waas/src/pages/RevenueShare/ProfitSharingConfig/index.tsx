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
  message,
  Modal,
  Descriptions
} from 'antd';
import {
  SaveOutlined,
  CalculatorOutlined,
  TableOutlined,
  GiftOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import {
  configProfitSharingConfig,
  getProfitSharingConfigDetail,
  getProfitSharingConfigPageList
} from './service';
import { getLocalStorageInfo } from '../../../utils/localStorageInfo';
import {
  ProfitSharingConfigDataType,
  createProfitSharingConfigColumnsData,
  ConfigItemData,
  FormValues,
  defaultFormValues,
  configItemsData
} from './profitSharingConfigData';


const { Title, Text } = Typography;

export default () => {
  const userInfo = getLocalStorageInfo('userInfo');
  const [formRef] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'config' | 'list'>('list');

  // 让利配置相关状态
  const [profitSharingViewModal, setProfitSharingViewModal] = useState(false);
  const [profitSharingViewRecord, setProfitSharingViewRecord] = useState<ProfitSharingConfigDataType>({} as ProfitSharingConfigDataType);
  const [profitSharingFormValues, setProfitSharingFormValues] = useState<FormValues>(defaultFormValues);

  // 让利配置相关函数

  /**
   * 查看让利配置详情
   */
  const toViewProfitSharingConfig = async (record: ProfitSharingConfigDataType) => {
    let { serialNo } = record;
    let viewRes = await getProfitSharingConfigDetail({ serialNo });
    setProfitSharingViewModal(true);
    console.log('profitSharingViewRes', viewRes);
    setProfitSharingViewRecord(viewRes.data || record);
  };

  // 创建让利配置表格列配置
  const profitSharingColumnsData = createProfitSharingConfigColumnsData();
  const profitSharingColumns = [
    ...profitSharingColumnsData.slice(0, -1), // 除了操作列的所有列
    {
      ...profitSharingColumnsData[profitSharingColumnsData.length - 1], // 操作列
      render: (text: any, record: any) => (
        <ul className="ant-list-item-action" style={{ margin: 0 }}>
          <li>
            <a
              onClick={() => {
                toViewProfitSharingConfig(record);
              }}
            >
              查看
            </a>
          </li>
        </ul>
      ),
    }
  ];

  // Table action 的引用，便于自定义触发
  const profitSharingActionRef = React.useRef<ActionType>();

  /**
   * 保存让利配置
   */
  const confirmProfitSharingActivity = () => {
    formRef.validateFields()
      .then(() => {
        const formData = formRef.getFieldsValue();

        // 显示确认对话框
        Modal.confirm({
          title: '确认保存让利配置',
          icon: <SaveOutlined />,
          content: (
            <div style={{ marginTop: 16 }}>
              <Alert
                message="请确认以下让利比例配置："
                type="info"
                style={{ marginBottom: 16 }}
              />
              <div style={{ background: '#f9f9f9', padding: 16, borderRadius: 6 }}>
                <Space direction="vertical" style={{ width: '100%' }} size="small">
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <Text><GiftOutlined style={{ color: '#1890ff', marginRight: 8 }} />商户让利比例：</Text>
                    <Text strong>{formData.profitSharingRate || 0}%</Text>
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
                  message.success("让利配置保存成功！");
                  // 刷新列表
                  profitSharingActionRef.current?.reload();
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

  const handleProfitSharingValuesChange = (changedValues: any, allValues: FormValues) => {
    setProfitSharingFormValues(allValues);
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
        setProfitSharingFormValues(res?.data);
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

  React.useEffect(() => {
    loadProfitSharingConfig();
  }, []);

    return (
    <div style={{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }}>
      <Row gutter={[24, 24]}>
        {/* 让利配置 */}
        <Col span={24}>
          <Card 
            title={
              <Row justify="space-between" align="middle">
                <Col>
                  <Space>
                    <GiftOutlined />
                    商户让利配置
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
                <Alert
                  message="设置规则"
                  description="商户让利比例应在0-100%之间，请合理设置让利比例"
                  type="info"
                  showIcon
                  style={{ marginBottom: 16 }}
                />

                <Form
                  form={formRef}
                  layout="vertical"
                  onValuesChange={handleProfitSharingValuesChange}
                >
                  <Row gutter={[16, 16]}>
                    {configItemsData.map((item: ConfigItemData, index: number) => {
                      // 根据iconName动态获取图标组件
                      const getIcon = (iconName: string) => {
                        switch (iconName) {
                          case 'GiftOutlined': return <GiftOutlined />;
                          default: return <GiftOutlined />;
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
                        onClick={confirmProfitSharingActivity}
                        icon={<SaveOutlined />}
                        style={{
                          height: 48,
                          paddingLeft: 32,
                          paddingRight: 32,
                          fontSize: 16,
                        }}
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
                columns={profitSharingColumns}
                actionRef={profitSharingActionRef}
                request={async (params) => {
                  let res = await getProfitSharingConfigPageList({
                    ...params,
                    pagination: {
                      pageNum: params.current,
                      pageSize: params.pageSize,
                    },
                  });
                  console.log('让利配置列表', res);
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

      {/* 让利配置查看详情模态框 */}
      <Modal
        title="查看让利配置详情"
        width={800}
        centered
        open={profitSharingViewModal}
        onOk={() => setProfitSharingViewModal(false)}
        onCancel={() => setProfitSharingViewModal(false)}
      >
        <Descriptions title="让利配置信息" bordered>
          <Descriptions.Item label="配置ID" span={2}>
            {profitSharingViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="租户ID" span={2}>
            {profitSharingViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户编号" span={2}>
            {profitSharingViewRecord?.merchantNo}
          </Descriptions.Item>
          <Descriptions.Item label="会员模型">
            {profitSharingViewRecord?.memberModel === '1' ? '平台会员' : 
             profitSharingViewRecord?.memberModel === '2' ? '商户会员' : '店铺会员'}
          </Descriptions.Item>
          <Descriptions.Item label="让利比例">
            {profitSharingViewRecord?.profitSharingRate}%
          </Descriptions.Item>
          <Descriptions.Item label="回调地址" span={2}>
            {profitSharingViewRecord?.callbackAddress}
          </Descriptions.Item>
          <Descriptions.Item label="创建人" span={2}>
            {profitSharingViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间" span={2}>
            {profitSharingViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新人" span={2}>
            {profitSharingViewRecord?.updateBy}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间" span={2}>
            {profitSharingViewRecord?.updateTime}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
