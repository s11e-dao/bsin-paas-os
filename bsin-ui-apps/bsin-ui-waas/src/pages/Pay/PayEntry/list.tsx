import React from 'react';
import {
  message,
  Button,
  Space,
  Tag,
  Popconfirm,
  Modal,
  Descriptions,
  Spin,
  Card,
  Row,
  Col,
  Statistic,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { EyeOutlined, CheckCircleOutlined, CloseCircleOutlined, ReloadOutlined, ClockCircleOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getCustomerEnterprisePageList,
  auditCustomerEnterprise,
  getMerchantAuthDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';

interface EnterpriseRecord {
  serialNo: string;
  customerNo: string;
  status: string;
  authenticationStatus: string;
  enterpriseName?: string;
  businessNo?: string;
  phone?: string;
  netAddress?: string;
  enterpriseAddress?: string;
  legalPersonName?: string;
  legalPersonCredType?: string;
  legalPersonCredNo?: string;
  businessScope?: string;
  businessLicenceImg?: string;
  [key: string]: any;
}

interface MerchantAuditListProps {
  addCurrentRecord: (record: EnterpriseRecord) => void;
}

export default ({ addCurrentRecord }: MerchantAuditListProps) => {

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 进件状态查询相关状态
  const [statusModalVisible, setStatusModalVisible] = React.useState(false);
  const [currentRecord, setCurrentRecord] = React.useState<EnterpriseRecord | null>(null);
  const [statusData, setStatusData] = React.useState<any>(null);
  const [statusLoading, setStatusLoading] = React.useState(false);

  /**
   * 处理审核操作
   */
  const handleAudit = async (record: EnterpriseRecord, auditFlag: string) => {
    try {
      const res = await auditCustomerEnterprise({
        merchantNo: record.serialNo,
        customerNo: record.customerNo,
        auditFlag,
      });

      if (res.code === 0) {
        message.success(auditFlag === '1' ? '审核通过成功' : '审核拒绝成功');
        // 刷新表格数据
        actionRef.current?.reload();
      } else {
        message.error(`操作失败：${res?.message}`);
      }
    } catch (error) {
      console.error('审核操作失败:', error);
      message.error('操作失败，请稍后重试');
    }
  };

  /**
   * 打开进件状态查询弹框
   */
  const handleStatusQuery = (record: EnterpriseRecord) => {
    setCurrentRecord(record);
    setStatusModalVisible(true);
    fetchMerchantStatus(record);
  };

  /**
   * 获取商户进件状态
   */
  const fetchMerchantStatus = async (record: EnterpriseRecord) => {
    if (!record?.serialNo) return;
    
    setStatusLoading(true);
    try {
      const response = await getMerchantAuthDetail({ serialNo: record.serialNo });
      if (response?.code === 0) {
        setStatusData(response.data);
      } else {
        message.error(`获取状态失败：${response?.message || '未知错误'}`);
      }
    } catch (error) {
      console.error('获取商户状态失败:', error);
      message.error('获取状态失败，请稍后重试');
    } finally {
      setStatusLoading(false);
    }
  };

  /**
   * 刷新进件状态
   */
  const handleRefreshStatus = () => {
    if (currentRecord) {
      fetchMerchantStatus(currentRecord);
    }
  };

  /**
   * 关闭状态弹框
   */
  const handleCloseStatusModal = () => {
    setStatusModalVisible(false);
    setCurrentRecord(null);
    setStatusData(null);
  };

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: EnterpriseRecord, index: number) => (
    <Space key={record.serialNo}>
      {/* 只有待审核状态才显示审核按钮 */}
      {record.status === '0' && (
        <>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => addCurrentRecord(record)}
          >
            微信支付进件
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleStatusQuery(record)}
          >
            进件状态查询
          </Button>
        </>
      )}


    </Space>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    if (item.dataIndex === 'action') {
      item.render = actionRender;
    }
    // 添加状态列的渲染
    if (item.dataIndex === 'authenticationStatus') {
      item.render = (status: string) => {
        const statusMap = {
          '0': { text: '未认证', color: 'default' },
          '1': { text: '待审核', color: 'processing' },
          '2': { text: '认证成功', color: 'success' },
          '3': { text: '认证失败', color: 'error' },
        };
        const config = statusMap[status as keyof typeof statusMap] || { text: '未知', color: 'default' };
        return <Tag color={config.color}>{config.text}</Tag>;
      };
    }
  });

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="商户支付进件列表" />}
        scroll={{ x: 1200 }}
        bordered
        columns={columns}
        actionRef={actionRef}
        request={async (params) => {
          try {
            const res = await getCustomerEnterprisePageList({
              ...params,
              // 只显示待审核的商户
              authenticationStatus: '1',
            });

            return {
              data: res.data || [],
              total: res.pagination?.totalSize || 0,
              success: res.code === 0,
            };
          } catch (error) {
            console.error('获取商户列表失败:', error);
            return {
              data: [],
              total: 0,
              success: false,
            };
          }
        }}
        rowKey="customerNo"
        search={{
          labelWidth: 'auto',
        }}
        form={{
          ignoreRules: false,
        }}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        options={{
          reload: true,
          density: false,
          setting: false,
        }}
      />

      {/* 进件状态查询弹框 */}
      <Modal
        title={
          <div style={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'space-between',
            padding: '8px 0'
          }}>
            <span style={{ 
              fontSize: '18px', 
              fontWeight: '600',
              color: '#1f1f1f'
            }}>
              进件状态查询
            </span>
            <Button
              type="primary"
              icon={<ReloadOutlined />}
              onClick={handleRefreshStatus}
              loading={statusLoading}
              style={{
                borderRadius: '6px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
              }}
            >
              刷新状态
            </Button>
          </div>
        }
        open={statusModalVisible}
        onCancel={handleCloseStatusModal}
        footer={null}
        width={900}
        destroyOnClose
        styles={{
          header: {
            borderBottom: '1px solid #f0f0f0',
            padding: '16px 24px'
          },
          body: {
            padding: '24px'
          }
        }}
      >
        <Spin spinning={statusLoading}>
          {statusData && (
            <div>
              {/* 商户基本信息 */}
              <Card 
                title={
                  <span style={{ 
                    fontSize: '16px', 
                    fontWeight: '600',
                    color: '#262626'
                  }}>
                    商户基本信息
                  </span>
                } 
                style={{ 
                  marginBottom: 20,
                  borderRadius: '8px',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
                  border: '1px solid #f0f0f0'
                }}
                headStyle={{
                  backgroundColor: '#fafafa',
                  borderBottom: '1px solid #f0f0f0',
                  borderRadius: '8px 8px 0 0'
                }}
              >
                <Descriptions 
                  column={2} 
                  bordered 
                  size="small"
                  labelStyle={{
                    backgroundColor: '#fafafa',
                    fontWeight: '500',
                    color: '#595959'
                  }}
                  contentStyle={{
                    backgroundColor: '#ffffff'
                  }}
                >
                  <Descriptions.Item label="商户名称">
                    <span style={{ color: '#262626', fontWeight: '500' }}>
                      {statusData.baseInfo?.merchantName || '-'}
                    </span>
                  </Descriptions.Item>
                  <Descriptions.Item label="商户编号">
                    <span style={{ color: '#1890ff', fontWeight: '500' }}>
                      {currentRecord?.serialNo || '-'}
                    </span>
                  </Descriptions.Item>
                  <Descriptions.Item label="联系电话">
                    <span style={{ color: '#262626' }}>
                      {statusData.baseInfo?.phone || '-'}
                    </span>
                  </Descriptions.Item>
                  <Descriptions.Item label="企业地址">
                    <span style={{ color: '#262626' }}>
                      {statusData.baseInfo?.enterpriseAddress || '-'}
                    </span>
                  </Descriptions.Item>
                </Descriptions>
              </Card>

              {/* 审核状态概览 */}
              <Card 
                title={
                  <span style={{ 
                    fontSize: '16px', 
                    fontWeight: '600',
                    color: '#262626'
                  }}>
                    审核状态概览
                  </span>
                } 
                style={{ 
                  marginBottom: 20,
                  borderRadius: '8px',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
                  border: '1px solid #f0f0f0'
                }}
                headStyle={{
                  backgroundColor: '#fafafa',
                  borderBottom: '1px solid #f0f0f0',
                  borderRadius: '8px 8px 0 0'
                }}
              >
                <Statistic
                    value={statusData.baseInfo?.status === '1' ? '已通过' : '待审核'}
                    valueStyle={{ 
                      color: statusData.baseInfo?.status === '1' ? '#52c41a' : '#faad14',
                      fontSize: '18px',
                      fontWeight: '600'
                    }}
                    prefix={
                      statusData.baseInfo?.status === '1' ? 
                      <CheckCircleOutlined style={{ fontSize: '20px', marginRight: '8px' }} /> : 
                      <ClockCircleOutlined style={{ fontSize: '20px', marginRight: '8px' }} />
                    }
                  />
              </Card>
            </div>
          )}
        </Spin>
      </Modal>
    </div>
  );
}; 