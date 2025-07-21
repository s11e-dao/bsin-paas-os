import React, { useState, useEffect } from 'react';
import {
  ProTable,
  ProForm,
  ProFormText,
  ProFormSelect,
  ProFormTextArea,
  ProFormDatePicker,
  ProFormDigit,
  ProFormList,
  ProFormGroup,
} from '@ant-design/pro-components';
import {
  Card,
  Tabs,
  Button,
  Space,
  Modal,
  message,
  Descriptions,
  Tag,
  Alert,
  Divider,
  Row,
  Col,
  Statistic,
} from 'antd';
import {
  PlusOutlined,
  ReloadOutlined,
  SettingOutlined,
  ApiOutlined,
  HistoryOutlined,
} from '@ant-design/icons';
import type { ActionType } from '@ant-design/pro-components';
import { useRef } from 'react';

import {
  getBizRoleAppList,
  getBizRoleAppPayChannelConfig,
  requestProfitShare,
  queryProfitShareResult,
  requestProfitShareReturn,
  queryProfitShareReturnResult,
  unfreezeRemainingFunds,
  queryRemainingAmount,
  addProfitShareReceiver,
  deleteProfitShareReceiver,
  applyProfitShareBill,
  downloadProfitShareBill,
} from './service';
import {
  bizRoleAppColumns,
  wxProfitShareActions,
  receiverFormFields,
  profitShareFormFields,
  returnFormFields,
  billFormFields,
  statusTagConfig,
  operationResultColumns,
} from './data';

const { TabPane } = Tabs;

const WxProfitShare: React.FC = () => {
  const [selectedApp, setSelectedApp] = useState<any>(null);
  const [channelConfig, setChannelConfig] = useState<any>(null);
  const [operationHistory, setOperationHistory] = useState<any[]>([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [modalTitle, setModalTitle] = useState('');
  const [modalForm, setModalForm] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const actionRef = useRef<ActionType>();

  // 获取支付通道配置
  const fetchChannelConfig = async (app: any) => {
    try {
      const response = await getBizRoleAppPayChannelConfig({
        bizRoleAppId: app.serialNo,
        payChannelCode: 'wxPay',
      });
      if (response.code === 0 && response.data) {
        setChannelConfig(response.data);
        message.success('获取支付通道配置成功');
      } else {
        message.warning('该应用未配置微信支付通道');
      }
    } catch (error) {
      message.error('获取支付通道配置失败');
    }
  };

  // 处理配置分账点击
  const handleConfigClick = (record: any) => {
    setSelectedApp(record);
    fetchChannelConfig(record);
  };

  // 处理API操作
  const handleApiOperation = async (actionKey: string, params: any) => {
    if (!selectedApp) {
      message.error('请先选择应用');
      return;
    }

    setLoading(true);
    try {
      const baseParams = {
        bizRoleAppId: selectedApp.serialNo,
        payChannelCode: 'wxPay',
        ...params,
      };

      let response;
      switch (actionKey) {
        case 'request':
          response = await requestProfitShare(baseParams);
          break;
        case 'query':
          response = await queryProfitShareResult(baseParams);
          break;
        case 'return':
          response = await requestProfitShareReturn(baseParams);
          break;
        case 'returnQuery':
          response = await queryProfitShareReturnResult(baseParams);
          break;
        case 'unfreeze':
          response = await unfreezeRemainingFunds(baseParams);
          break;
        case 'remaining':
          response = await queryRemainingAmount(baseParams);
          break;
        case 'addReceiver':
          response = await addProfitShareReceiver(baseParams);
          break;
        case 'deleteReceiver':
          response = await deleteProfitShareReceiver(baseParams);
          break;
        case 'applyBill':
          response = await applyProfitShareBill(baseParams);
          break;
        case 'downloadBill':
          response = await downloadProfitShareBill(baseParams);
          break;
        default:
          message.error('未知操作类型');
          return;
      }

      // 记录操作历史
      const historyItem = {
        operationType: actionKey,
        requestParams: JSON.stringify(baseParams),
        responseResult: JSON.stringify(response),
        status: response.code === 0 ? 'SUCCESS' : 'FAILED',
        operationTime: new Date().toISOString(),
      };
      setOperationHistory([historyItem, ...operationHistory]);

      if (response.code === 0) {
        message.success('操作成功');
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      message.error('操作失败');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  // 打开操作模态框
  const openOperationModal = (action: any) => {
    setModalTitle(action.label);
    setModalVisible(true);
    
    let formFields = [];
    switch (action.key) {
      case 'request':
        formFields = profitShareFormFields;
        break;
      case 'return':
        formFields = returnFormFields;
        break;
      case 'addReceiver':
        formFields = receiverFormFields;
        break;
      case 'applyBill':
        formFields = billFormFields;
        break;
      default:
        formFields = [
          {
            name: 'transactionId',
            label: '微信订单号',
            type: 'input',
            required: true,
            placeholder: '请输入微信支付订单号',
          },
        ];
    }
    
    setModalForm({ actionKey: action.key, fields: formFields });
  };

  // 处理模态框提交
  const handleModalSubmit = async (values: any) => {
    await handleApiOperation(modalForm.actionKey, values);
    setModalVisible(false);
  };

  // 渲染操作按钮
  const renderActionButtons = () => {
    return (
      <Row gutter={[16, 16]}>
        {wxProfitShareActions.map((action) => (
          <Col key={action.key} xs={24} sm={12} md={8} lg={6}>
            <Card
              hoverable
              size="small"
              onClick={() => openOperationModal(action)}
              style={{ cursor: 'pointer' }}
            >
              <div style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '24px', marginBottom: '8px' }}>
                  {action.icon}
                </div>
                <div style={{ fontWeight: 'bold', marginBottom: '4px' }}>
                  {action.label}
                </div>
                <div style={{ fontSize: '12px', color: '#666' }}>
                  {action.description}
                </div>
              </div>
            </Card>
          </Col>
        ))}
      </Row>
    );
  };

  // 渲染应用配置信息
  const renderAppConfig = () => {
    if (!selectedApp) {
      return (
        <Alert
          message="请选择应用"
          description="在左侧应用列表中选择一个应用来配置微信分账功能"
          type="info"
          showIcon
        />
      );
    }

    return (
      <Card title="应用配置信息" size="small">
        <Descriptions column={2}>
          <Descriptions.Item label="应用名称">{selectedApp.appName}</Descriptions.Item>
          <Descriptions.Item label="应用ID">{selectedApp.appId}</Descriptions.Item>
          <Descriptions.Item label="应用描述">{selectedApp.appDescription}</Descriptions.Item>
          <Descriptions.Item label="状态">
            <Tag color={selectedApp.status === '1' ? 'success' : 'error'}>
              {selectedApp.status === '1' ? '启用' : '停用'}
            </Tag>
          </Descriptions.Item>
        </Descriptions>
        
        {channelConfig && (
          <>
            <Divider />
            <Descriptions title="支付通道配置" column={2}>
              <Descriptions.Item label="通道代码">{channelConfig.payChannelCode}</Descriptions.Item>
              <Descriptions.Item label="费率">{channelConfig.feeRatio}%</Descriptions.Item>
              <Descriptions.Item label="普通商户模式">
                <Tag color={channelConfig.isNormalMerchanMode ? 'success' : 'default'}>
                  {channelConfig.isNormalMerchanMode ? '支持' : '不支持'}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="服务商子商户模式">
                <Tag color={channelConfig.isServiceSubMerchantMode ? 'success' : 'default'}>
                  {channelConfig.isServiceSubMerchantMode ? '支持' : '不支持'}
                </Tag>
              </Descriptions.Item>
            </Descriptions>
          </>
        )}
      </Card>
    );
  };

  return (
    <div style={{ padding: '24px' }}>
      <Tabs defaultActiveKey="apps" >
        <TabPane tab="平台应用" key="apps">
          <ProTable
            headerTitle="平台应用列表"
            actionRef={actionRef}
            rowKey="serialNo"
            search={{
              labelWidth: 120,
            }}
            toolBarRender={() => [
              <Button
                key="refresh"
                icon={<ReloadOutlined />}
                onClick={() => actionRef.current?.reload()}
              >
                刷新
              </Button>,
            ]}
            request={async (params) => {
              const response = await getBizRoleAppList({
                ...params,
                pagination: {
                  pageNum: params.current || 1,
                  pageSize: params.pageSize || 10,
                },
              });
              return {
                data: response.data || [],
                success: response.code === 0,
                total: response.pagination?.totalSize || 0,
              };
            }}
            columns={bizRoleAppColumns.map(col => ({
              ...col,
              render: col.render ? (text, record) => col.render!(text, { ...record, onConfigClick: handleConfigClick }) : undefined,
            }))}
            pagination={{
              defaultPageSize: 10,
              showSizeChanger: true,
            }}
          />
        </TabPane>

        <TabPane tab="分账配置" key="config" disabled={!selectedApp}>
          <Space direction="vertical" style={{ width: '100%' }} size="large">
            {renderAppConfig()}
            
            {selectedApp && (
              <Card title="微信分账API操作" size="small">
                <Alert
                  message="微信分账功能说明"
                  description="根据微信支付分账文档，支持分账请求、查询、回退、解冻等操作。分账资金会先冻结在商户账户中，可根据业务需要实时或延时分账。"
                  type="info"
                  showIcon
                  style={{ marginBottom: '16px' }}
                />
                {renderActionButtons()}
              </Card>
            )}
          </Space>
        </TabPane>

        <TabPane tab="操作历史" key="history">
          <ProTable
            headerTitle="操作历史记录"
            rowKey="operationTime"
            search={false}
            dataSource={operationHistory}
            columns={operationResultColumns}
            pagination={{
              defaultPageSize: 10,
              showSizeChanger: true,
            }}
            toolBarRender={() => [
              <Button
                key="clear"
                onClick={() => setOperationHistory([])}
                danger
              >
                清空历史
              </Button>,
            ]}
          />
        </TabPane>
      </Tabs>

      {/* 操作模态框 */}
      <Modal
        title={modalTitle}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={600}
        destroyOnClose
      >
        {modalForm && (
          <ProForm
            onFinish={handleModalSubmit}
            submitter={{
              render: (props, doms) => {
                return (
                  <div style={{ textAlign: 'right' }}>
                    <Space>
                      <Button onClick={() => setModalVisible(false)}>取消</Button>
                      <Button type="primary" loading={loading} onClick={() => props.submit()}>
                        确定
                      </Button>
                    </Space>
                  </div>
                );
              },
            }}
          >
            {modalForm.fields.map((field: any, index: number) => {
              const { type, ...rest } = field;
              switch (type) {
                case 'input':
                  return <ProFormText key={index} {...rest} />;
                case 'textarea':
                  return <ProFormTextArea key={index} {...rest} />;
                case 'select':
                  return <ProFormSelect key={index} {...rest} />;
                case 'number':
                  return <ProFormDigit key={index} {...rest} />;
                case 'datePicker':
                  return <ProFormDatePicker key={index} {...rest} />;
                case 'array':
                  return (
                    <ProFormList
                      key={index}
                      name={rest.name}
                      label={rest.label}
                      creatorButtonProps={{
                        creatorButtonText: '添加接收方',
                      }}
                    >
                      <ProFormGroup>
                        {rest.itemFields.map((itemField: any, itemIndex: number) => {
                          const { type: itemType, ...itemRest } = itemField;
                          switch (itemType) {
                            case 'input':
                              return <ProFormText key={itemIndex} {...itemRest} />;
                            case 'select':
                              return <ProFormSelect key={itemIndex} {...itemRest} />;
                            case 'number':
                              return <ProFormDigit key={itemIndex} {...itemRest} />;
                            default:
                              return <ProFormText key={itemIndex} {...itemRest} />;
                          }
                        })}
                      </ProFormGroup>
                    </ProFormList>
                  );
                default:
                  return <ProFormText key={index} {...rest} />;
              }
            })}
          </ProForm>
        )}
      </Modal>
    </div>
  );
};

export default WxProfitShare;
 