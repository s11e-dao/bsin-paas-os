import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  message,
  Button,
  Descriptions,
  Tag,
  Card,
  Space,
  Popconfirm,
  Steps,
  Result,
  Tabs,
  Modal,
  Select,
  Upload,
} from 'antd';
import {
  ArrowLeftOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  EditOutlined,
  UploadOutlined,
  PictureOutlined
} from '@ant-design/icons';
import {
  getCustomerEnterpriseInfo,
  auditCustomerEnterprise,
} from './service';

const { Step } = Steps;
const { TabPane } = Tabs;
const { Option } = Select;

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
  // 审核步骤状态
  basicInfoStatus?: string; // 基础信息审核状态 0:待审核 1:通过 2:拒绝
  businessInfoStatus?: string; // 营业信息审核状态
  legalPersonStatus?: string; // 法人信息审核状态
  [key: string]: any;
}

interface MerchantAuditDetailProps {
  currentRecord: EnterpriseRecord;
  setIsLoadMerchantAuditDetail: (isLoad: boolean) => void;
}

export default ({ currentRecord, setIsLoadMerchantAuditDetail }: MerchantAuditDetailProps) => {
  const [detailRecord, setDetailRecord] = useState<EnterpriseRecord>({} as EnterpriseRecord);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('1');

  // 编辑模态框状态
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editType, setEditType] = useState<'basic' | 'business' | 'legal'>('basic');
  const [editForm] = Form.useForm();

  /**
   * 组件加载时获取详细信息
   */
  useEffect(() => {
    if (currentRecord?.customerNo) {
      loadEnterpriseDetail();
    }
  }, [currentRecord]);

  /**
   * 加载企业详情
   */
  const loadEnterpriseDetail = async () => {
    try {
      setLoading(true);
      const res = await getCustomerEnterpriseInfo({ customerNo: currentRecord.customerNo });

      if (res.code === 0 && res.data) {
        // 模拟审核步骤状态，实际应该从后端获取
        const mockData = {
          ...res.data,
          basicInfoStatus: res.data.basicInfoStatus || '0',
          businessInfoStatus: res.data.businessInfoStatus || '0',
          legalPersonStatus: res.data.legalPersonStatus || '0',
        };
        setDetailRecord(mockData);
      } else {
        message.error('获取企业详情失败');
      }
    } catch (error) {
      console.error('获取企业详情失败:', error);
      message.error('获取详情失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  /**
   * 处理分步审核操作
   */
  const handleStepAudit = async (stepType: 'basic' | 'business' | 'legal', auditFlag: string) => {
    try {
      setLoading(true);

      const res = await auditCustomerEnterprise({
        merchantNo: detailRecord.serialNo,
        customerNo: detailRecord.customerNo,
        auditFlag,
        stepType,
      });

      if (res.code === 0) {
        const stepNames = {
          basic: '基础信息',
          business: '营业信息',
          legal: '法人信息'
        };

        message.success(`${stepNames[stepType]}${auditFlag === '1' ? '审核通过' : '审核拒绝'}成功`);

        // 更新本地状态
        const statusField = stepType === 'basic' ? 'basicInfoStatus' :
          stepType === 'business' ? 'businessInfoStatus' : 'legalPersonStatus';

        setDetailRecord(prev => ({
          ...prev,
          [statusField]: auditFlag
        }));

      } else {
        message.error(`操作失败：${res?.message}`);
      }
    } catch (error) {
      console.error('审核操作失败:', error);
      message.error('操作失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  /**
   * 打开编辑模态框
   */
  const openEditModal = (type: 'basic' | 'business' | 'legal') => {
    setEditType(type);
    setEditModalVisible(true);

    // 根据编辑类型填充表单
    if (type === 'basic') {
      editForm.setFieldsValue({
        enterpriseName: detailRecord.enterpriseName,
        businessNo: detailRecord.businessNo,
        phone: detailRecord.phone,
        netAddress: detailRecord.netAddress,
        enterpriseAddress: detailRecord.enterpriseAddress,
      });
    } else if (type === 'business') {
      editForm.setFieldsValue({
        businessScope: detailRecord.businessScope,
        businessLicenceImg: detailRecord.businessLicenceImg,
      });
    } else if (type === 'legal') {
      editForm.setFieldsValue({
        legalPersonName: detailRecord.legalPersonName,
        legalPersonCredType: detailRecord.legalPersonCredType,
        legalPersonCredNo: detailRecord.legalPersonCredNo,
      });
    }
  };

  /**
   * 保存编辑信息
   */
  const saveEditInfo = async () => {
    try {
      const values = await editForm.validateFields();

      // 这里应该调用更新商户信息的API
      // await updateMerchantInfo({ ...values, customerNo: detailRecord.customerNo });

      // 更新本地数据
      setDetailRecord(prev => ({
        ...prev,
        ...values
      }));

      message.success('信息更新成功');
      setEditModalVisible(false);
      editForm.resetFields();
    } catch (error) {
      console.error('保存失败:', error);
    }
  };

  /**
   * 返回列表
   */
  const goBack = () => {
    setIsLoadMerchantAuditDetail(false);
  };

  /**
   * 渲染审核状态标签
   */
  const renderAuditStatus = (status: string) => {
    const statusMap = {
      '0': { text: '待审核', color: 'default' },
      '1': { text: '已通过', color: 'success' },
      '2': { text: '已拒绝', color: 'error' },
    };
    const config = statusMap[status as keyof typeof statusMap] || statusMap['0'];
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  /**
   * 渲染步骤状态
   */
  const getStepStatus = (stepIndex: number) => {
    const statusFields = ['basicInfoStatus', 'businessInfoStatus', 'legalPersonStatus'];
    const status = detailRecord[statusFields[stepIndex]];

    if (status === '1') return 'finish';
    if (status === '2') return 'error';
    return 'process';
  };

  /**
   * 渲染基础信息审核
   */
  const renderBasicInfoAudit = () => (
    <Card
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span>基础信息审核 {renderAuditStatus(detailRecord.basicInfoStatus || '0')}</span>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => openEditModal('basic')}
          >
            编辑信息
          </Button>
        </div>
      }
    >
      <Descriptions bordered column={2} size="middle">
        <Descriptions.Item label="企业名称" span={2}>
          {detailRecord?.enterpriseName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label="企业工商号" span={1}>
          {detailRecord?.businessNo || '-'}
        </Descriptions.Item>
        <Descriptions.Item label="联系电话" span={1}>
          {detailRecord?.phone || '-'}
        </Descriptions.Item>
        <Descriptions.Item label="公司网址" span={2}>
          {detailRecord?.netAddress ? (
            <a href={detailRecord.netAddress} target="_blank" rel="noopener noreferrer">
              {detailRecord.netAddress}
            </a>
          ) : '-'}
        </Descriptions.Item>
        <Descriptions.Item label="企业地址" span={2}>
          {detailRecord?.enterpriseAddress || '-'}
        </Descriptions.Item>
      </Descriptions>

      <div style={{ marginTop: 24, textAlign: 'center' }}>
        <Space size="large">
          <Popconfirm
            title="确认通过基础信息审核？"
            onConfirm={() => handleStepAudit('basic', '1')}
            okText="确认通过"
            cancelText="取消"
          >
            <Button type="primary" icon={<CheckCircleOutlined />} loading={loading}>
              通过审核
            </Button>
          </Popconfirm>

          <Popconfirm
            title="确认拒绝基础信息审核？"
            onConfirm={() => handleStepAudit('basic', '2')}
            okText="确认拒绝"
            cancelText="取消"
          >
            <Button danger icon={<CloseCircleOutlined />} loading={loading}>
              拒绝审核
            </Button>
          </Popconfirm>
        </Space>
      </div>
    </Card>
  );

  /**
   * 渲染营业信息审核
   */
  const renderBusinessInfoAudit = () => (
    <Card
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span>营业信息审核 {renderAuditStatus(detailRecord.businessInfoStatus || '0')}</span>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => openEditModal('business')}
          >
            编辑信息
          </Button>
        </div>
      }
    >
      <Descriptions bordered column={2} size="middle">
        <Descriptions.Item label="经营范围" span={2}>
          {detailRecord?.businessScope || '-'}
        </Descriptions.Item>
        <Descriptions.Item label="营业执照" span={2}>
          {detailRecord?.businessLicenceImg ? (
            <div style={{ textAlign: 'center' }}>
              <img
                style={{
                  maxWidth: '400px',
                  maxHeight: '300px',
                  border: '1px solid #d9d9d9',
                  borderRadius: '6px',
                  cursor: 'pointer'
                }}
                src={detailRecord.businessLicenceImg}
                alt="营业执照"
                onClick={() => window.open(detailRecord.businessLicenceImg, '_blank')}
                onError={(e) => {
                  e.currentTarget.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjE2IiBmaWxsPSIjOTk5IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5Zu+54mH5Yqg6L295aSx6LSlPC90ZXh0Pjwvc3ZnPg==';
                }}
              />
              <div style={{ marginTop: '8px', color: '#666', fontSize: '12px' }}>
                点击图片查看大图
              </div>
            </div>
          ) : (
            <div style={{ textAlign: 'center', color: '#999', padding: '40px' }}>
              暂无营业执照图片
            </div>
          )}
        </Descriptions.Item>
      </Descriptions>

      <div style={{ marginTop: 24, textAlign: 'center' }}>
        <Space size="large">
          <Popconfirm
            title="确认通过营业信息审核？"
            onConfirm={() => handleStepAudit('business', '1')}
            okText="确认通过"
            cancelText="取消"
          >
            <Button type="primary" icon={<CheckCircleOutlined />} loading={loading}>
              通过审核
            </Button>
          </Popconfirm>

          <Popconfirm
            title="确认拒绝营业信息审核？"
            onConfirm={() => handleStepAudit('business', '2')}
            okText="确认拒绝"
            cancelText="取消"
          >
            <Button danger icon={<CloseCircleOutlined />} loading={loading}>
              拒绝审核
            </Button>
          </Popconfirm>
        </Space>
      </div>
    </Card>
  );

  /**
   * 渲染法人信息审核
   */
  const renderLegalPersonAudit = () => (
    <Card
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span>法人信息审核 {renderAuditStatus(detailRecord.legalPersonStatus || '0')}</span>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => openEditModal('legal')}
          >
            编辑信息
          </Button>
        </div>
      }
    >
      <Descriptions bordered column={2} size="middle">
        <Descriptions.Item label="法人姓名" span={1}>
          {detailRecord?.legalPersonName || '-'}
        </Descriptions.Item>
        <Descriptions.Item label="法人证件类型" span={1}>
          {detailRecord?.legalPersonCredType === '0' ? '大陆居民身份证' : '军官证'}
        </Descriptions.Item>
        <Descriptions.Item label="法人证件号" span={2}>
          {detailRecord?.legalPersonCredNo || '-'}
        </Descriptions.Item>
      </Descriptions>

      <div style={{ marginTop: 24, textAlign: 'center' }}>
        <Space size="large">
          <Popconfirm
            title="确认通过法人信息审核？"
            onConfirm={() => handleStepAudit('legal', '1')}
            okText="确认通过"
            cancelText="取消"
          >
            <Button type="primary" icon={<CheckCircleOutlined />} loading={loading}>
              通过审核
            </Button>
          </Popconfirm>

          <Popconfirm
            title="确认拒绝法人信息审核？"
            onConfirm={() => handleStepAudit('legal', '2')}
            okText="确认拒绝"
            cancelText="取消"
          >
            <Button danger icon={<CloseCircleOutlined />} loading={loading}>
              拒绝审核
            </Button>
          </Popconfirm>
        </Space>
      </div>
    </Card>
  );

  /**
   * 渲染编辑模态框
   */
  const renderEditModal = () => {
    const getModalTitle = () => {
      const titles = {
        basic: '编辑基础信息',
        business: '编辑营业信息',
        legal: '编辑法人信息'
      };
      return titles[editType];
    };

    const renderFormItems = () => {
      if (editType === 'basic') {
        return (
          <>
            <Form.Item name="enterpriseName" label="企业名称" rules={[{ required: true }]}>
              <Input placeholder="请输入企业名称" />
            </Form.Item>
            <Form.Item name="businessNo" label="企业工商号" rules={[{ required: true }]}>
              <Input placeholder="请输入企业工商号" />
            </Form.Item>
            <Form.Item name="phone" label="联系电话" rules={[{ required: true }]}>
              <Input placeholder="请输入联系电话" />
            </Form.Item>
            <Form.Item name="netAddress" label="公司网址">
              <Input placeholder="请输入公司网址" />
            </Form.Item>
            <Form.Item name="enterpriseAddress" label="企业地址" rules={[{ required: true }]}>
              <Input.TextArea rows={3} placeholder="请输入企业地址" />
            </Form.Item>
          </>
        );
      } else if (editType === 'business') {
        return (
          <>
            <Form.Item name="businessScope" label="经营范围" rules={[{ required: true }]}>
              <Input.TextArea rows={4} placeholder="请输入经营范围" />
            </Form.Item>
            <Form.Item name="businessLicenceImg" label="营业执照">
              <Input placeholder="请输入营业执照图片URL" />
            </Form.Item>
          </>
        );
      } else if (editType === 'legal') {
        return (
          <>
            <Form.Item name="legalPersonName" label="法人姓名" rules={[{ required: true }]}>
              <Input placeholder="请输入法人姓名" />
            </Form.Item>
            <Form.Item name="legalPersonCredType" label="法人证件类型" rules={[{ required: true }]}>
              <Select placeholder="请选择证件类型">
                <Option value="0">大陆居民身份证</Option>
                <Option value="1">军官证</Option>
              </Select>
            </Form.Item>
            <Form.Item name="legalPersonCredNo" label="法人证件号" rules={[{ required: true }]}>
              <Input placeholder="请输入法人证件号" />
            </Form.Item>
          </>
        );
      }
    };

    return (
      <Modal
        title={getModalTitle()}
        open={editModalVisible}
        onOk={saveEditInfo}
        onCancel={() => {
          setEditModalVisible(false);
          editForm.resetFields();
        }}
        width={600}
        okText="保存"
        cancelText="取消"
      >
        <Form form={editForm} layout="vertical">
          {renderFormItems()}
        </Form>
      </Modal>
    );
  };

  /**
   * 检查是否全部审核完成
   */
  const isAllAuditCompleted = () => {
    return detailRecord.basicInfoStatus === '1' &&
      detailRecord.businessInfoStatus === '1' &&
      detailRecord.legalPersonStatus === '1';
  };

  return (
    <div>
      {/* 页面头部 */}
      <Card>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2 style={{ margin: 0 }}>商户认证分步审核</h2>

          <Space>
            <Tag color="blue">商户号: {detailRecord?.serialNo}</Tag>
            <Button
              icon={<ArrowLeftOutlined />}
              onClick={goBack}
              type="primary"
            >
              返回
            </Button>
          </Space>
        </div>
      </Card>

      {/* 审核步骤概览 */}
      <Card style={{ marginTop: 16 }}>
        <Steps size="small">
          <Step
            title="基础信息"
            description="企业基本信息"
            status={getStepStatus(0)}
            icon={detailRecord.basicInfoStatus === '1' ? <CheckCircleOutlined /> :
              detailRecord.basicInfoStatus === '2' ? <CloseCircleOutlined /> : undefined}
          />
          <Step
            title="营业信息"
            description="营业执照及范围"
            status={getStepStatus(1)}
            icon={detailRecord.businessInfoStatus === '1' ? <CheckCircleOutlined /> :
              detailRecord.businessInfoStatus === '2' ? <CloseCircleOutlined /> : undefined}
          />
          <Step
            title="法人信息"
            description="法人身份验证"
            status={getStepStatus(2)}
            icon={detailRecord.legalPersonStatus === '1' ? <CheckCircleOutlined /> :
              detailRecord.legalPersonStatus === '2' ? <CloseCircleOutlined /> : undefined}
          />
        </Steps>
      </Card>

      {/* 审核内容 - 使用 Tabs 显示所有步骤 */}
      {isAllAuditCompleted() ? (
        <Card style={{ marginTop: 16 }}>
          <Result
            status="success"
            title="商户认证审核已全部完成！"
            subTitle="该商户的所有认证信息均已通过审核，可以正常使用平台服务。"
            extra={[
              <Button type="primary" key="back" onClick={goBack}>
                返回审核列表
              </Button>
            ]}
          />
        </Card>
      ) : (
        <Card style={{ marginTop: 16 }}>
          <Tabs activeKey={activeTab} onChange={setActiveTab}>
            <TabPane tab="基础信息" key="1">
              {renderBasicInfoAudit()}
            </TabPane>
            <TabPane tab="营业信息" key="2">
              {renderBusinessInfoAudit()}
            </TabPane>
            <TabPane tab="法人信息" key="3">
              {renderLegalPersonAudit()}
            </TabPane>
          </Tabs>
        </Card>
      )}

      {/* 编辑模态框 */}
      {renderEditModal()}
    </div>
  );
};
