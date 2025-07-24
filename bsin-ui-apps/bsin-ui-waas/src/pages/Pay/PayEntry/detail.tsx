import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  Button,
  Card,
  Steps,
  Upload,
  Select,
  Radio,
  message,
  Space,
  Row,
  Col,
  Alert,
  Modal,
  Descriptions
} from 'antd';
import {
  ArrowLeftOutlined,
  InboxOutlined,
  SaveOutlined,
  SendOutlined
} from '@ant-design/icons';

const { Step } = Steps;
const { Option } = Select;
const { TextArea } = Input;
const { Dragger } = Upload;

import {
  getMerchantAuthDetail,
  payEntryApply,
} from './service';

export default ({ currentRecord, setIsLoadMerchantAuditDetail }: { currentRecord: any, setIsLoadMerchantAuditDetail: (isLoadMerchantAuditDetail: boolean) => void }) => {

  console.log('currentRecord',currentRecord);

  const [form] = Form.useForm();
  const [currentStep, setCurrentStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState('');
  const [fileList, setFileList] = useState<any>({
    businessLicenseImg: [],
    legalPersonIdFront: [], 
    legalPersonIdBack: [],
    bankAccountImg: [],
    storeImg: []
  });
  const [isLoadingData, setIsLoadingData] = useState(false);
  const [authStatus, setAuthStatus] = useState<any>({});

  // 步骤配置
  const steps = [
    { title: '基本信息', description: '企业基础资料' },
    { title: '经营信息', description: '经营范围与地址' },
    { title: '法人信息', description: '法定代表人信息' },
    { title: '结算信息', description: '银行账户信息' },
    { title: '资质材料', description: '上传证件照片' },
    { title: '确认提交', description: '核对信息并提交' }
  ];

  // 获取商户认证详情数据
  const fetchMerchantAuthDetail = async (params:any) => {
    if (!params?.merchantNo) return;
    try {
      setIsLoadingData(true);
      const response = await getMerchantAuthDetail({ merchantNo: params.merchantNo });
      console.log('response',response);
      if (response?.code === 0 && response?.data) {
        const data = response.data;
        
        // 回显表单数据
        form.setFieldsValue({
          // 基本信息 (baseInfo)
          merchantName: data.baseInfo?.merchantName,
          businessType: data.businessInfo?.businessType,
          organizationCode: data.businessInfo?.businessNo,
          businessLicense: data.businessInfo?.businessNo,
          
          // 经营信息 (businessInfo)
          businessScope: data.businessInfo?.businessScope,
          businessDescription: data.baseInfo?.description,
          businessAddress: data.baseInfo?.merchantAddress,
          servicePhone: data.baseInfo?.contactPhone,
          
          // 法人信息 (legalInfo)
          legalPersonName: data.legalInfo?.legalPersonName,
          legalPersonIdCard: data.legalInfo?.legalPersonCredNo,
          legalPersonPhone: data.baseInfo?.contactPhone,
          
          // 结算信息 (settlementInfo)
          accountType: data.settlementInfo?.accountType || 'corporate',
          accountName: data.settlementInfo?.accountName,
          accountNumber: data.settlementInfo?.accountNum,
          bankName: data.settlementInfo?.bankName,
          bankCode: data.settlementInfo?.swiftCode,
        });

        // 保存审核状态
        setAuthStatus({
          baseInfo: data.baseInfo?.authStatus,
          legalInfo: data.legalInfo?.authStatus,
          businessInfo: data.businessInfo?.authStatus,
          overall: data.overallAuthStatus,
          status: data.overallStatus
        });

        // 打印调试信息
        console.log('商户认证详情数据:', data);
        console.log('基础信息:', data.baseInfo);
        console.log('法人信息:', data.legalInfo);
        console.log('营业信息:', data.businessInfo);
        console.log('结算信息:', data.settlementInfo);
        console.log('整体审核状态:', data.overallAuthStatus);
        console.log('整体状态:', data.overallStatus);

        // 回显文件列表
        if (data.businessInfo?.businessLicenceImg) {
          setFileList((prev: any) => ({
            ...prev,
            businessLicenseImg: [{
              uid: '-1',
              name: '营业执照.jpg',
              status: 'done',
              url: data.businessInfo.businessLicenceImg,
              thumbUrl: data.businessInfo.businessLicenceImg
            }]
          }));
        }
        
        // 注意：接口中没有单独的身份证正反面和开户许可证图片字段
        // 这里可以根据实际业务需求调整，或者从其他字段获取
        // 暂时保留原有的逻辑，但设置为空
        
        // 如果有其他图片字段，可以在这里添加
        // 例如：data.baseInfo?.logoUrl 等

        message.success('数据加载成功');
      }
    } catch (error: any) {
      console.error('获取商户认证详情失败:', error);
      message.error('获取数据失败，请稍后重试');
    } finally {
      setIsLoadingData(false);
    }
  };

  // 组件加载时获取数据
  useEffect(() => {
    console.log('currentRecord',currentRecord);
    if (currentRecord?.merchantNo) {
      fetchMerchantAuthDetail(currentRecord);
    }
  }, [currentRecord?.merchantNo]);

  // 上传配置
  const uploadProps = (fileType: any) => ({
    name: 'file',
    multiple: false,
    fileList: fileList[fileType] || [],
    beforeUpload: (file: any) => {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJpgOrPng) {
        message.error('只能上传 JPG/PNG 格式的图片!');
        return false;
      }
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isLt5M) {
        message.error('图片大小不能超过 5MB!');
        return false;
      }
      return false; // 阻止自动上传
    },
    onChange: (info: any) => {
      let newFileList = [...info.fileList];
      newFileList = newFileList.slice(-1); // 只保留最新的一个文件
      setFileList((prev: any) => ({
        ...prev,
        [fileType]: newFileList
      }));
    },
    onPreview: (file: any) => {
      setPreviewImage(file.url || file.thumbUrl);
      setPreviewVisible(true);
    },
    onRemove: () => {
      setFileList((prev: any) => ({
        ...prev,
        [fileType]: []
      }));
    }
  });

  // 下一步
  const handleNext = async () => {
    try {
      await form.validateFields();
      if (currentStep < steps.length - 1) {
        setCurrentStep(currentStep + 1);
      }
    } catch (error) {
      message.error('请完善当前步骤的必填信息');
    }
  };

  // 上一步
  const handlePrev = () => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1);
    }
  };

  // 保存草稿
  const handleSaveDraft = async () => {
    try {
      const values = form.getFieldsValue();
      // 这里调用保存草稿的API
      console.log('保存草稿:', values);
      message.success('草稿保存成功');
    } catch (error) {
      message.error('保存失败');
    }
  };

  // 提交申请
  const handleSubmit = async () => {
    try {
      setLoading(true);
      const values = form.getFieldsValue();

      // 检查必需的文件
      const requiredFiles = ['businessLicenseImg', 'legalPersonIdFront', 'legalPersonIdBack', 'bankAccountImg'];
      const missingFiles = requiredFiles.filter((fileType: any) => !fileList[fileType] || fileList[fileType].length === 0);

      if (missingFiles.length > 0) {
        message.error('请上传所有必需的证件照片');
        setCurrentStep(4); // 跳转到资质材料步骤
        return;
      }

      // 构建提交数据
      const submitData = {
        merchantNo: currentRecord?.merchantNo,
        // 基础信息
        baseInfo: {
          merchantName: values.merchantName,
          businessType: values.businessType,
          organizationCode: values.organizationCode,
          businessLicense: values.businessLicense,
        },
        // 经营信息
        businessInfo: {
          businessScope: values.businessScope,
          businessDescription: values.businessDescription,
          businessAddress: values.businessAddress,
          servicePhone: values.servicePhone,
        },
        // 法人信息
        legalInfo: {
          legalPersonName: values.legalPersonName,
          legalPersonIdCard: values.legalPersonIdCard,
          legalPersonPhone: values.legalPersonPhone,
        },
        // 结算信息
        settlementInfo: {
          accountType: values.accountType,
          accountName: values.accountName,
          accountNumber: values.accountNumber,
          bankName: values.bankName,
          bankCode: values.bankCode,
        },
        // 文件信息
        files: {
          businessLicenseImg: fileList.businessLicenseImg?.[0]?.url || fileList.businessLicenseImg?.[0]?.response?.url,
          legalPersonIdFront: fileList.legalPersonIdFront?.[0]?.url || fileList.legalPersonIdFront?.[0]?.response?.url,
          legalPersonIdBack: fileList.legalPersonIdBack?.[0]?.url || fileList.legalPersonIdBack?.[0]?.response?.url,
          bankAccountImg: fileList.bankAccountImg?.[0]?.url || fileList.bankAccountImg?.[0]?.response?.url,
          storeImg: fileList.storeImg?.[0]?.url || fileList.storeImg?.[0]?.response?.url,
        }
      };

      console.log('提交申请数据:', submitData);

      // 调用提交申请的API
      const response = await payEntryApply(submitData);
      
      if (response?.code === 0) {
        Modal.success({
          title: '申请提交成功',
          content: '您的商户进件申请已成功提交，我们将在1-3个工作日内完成审核，请关注审核进度。',
          onOk: () => {
            // 跳转到申请列表或其他页面
            window.history.back();
          }
        });
      } else {
        message.error(response?.message || '提交失败，请稍后重试');
      }

    } catch (error: any) {
      console.error('提交失败:', error);
      message.error(error?.message || '提交失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  // 渲染基本信息步骤
  const renderBasicInfo = () => (
    <Card 
      title={
        <span>
          企业基本信息
          {authStatus.baseInfo && (
            <span style={{ 
              marginLeft: 8, 
              fontSize: '12px',
              color: authStatus.baseInfo === '1' ? '#52c41a' : 
                     authStatus.baseInfo === '2' ? '#1890ff' : 
                     authStatus.baseInfo === '3' ? '#ff4d4f' : '#999'
            }}>
              ({authStatus.baseInfo === '1' ? '已通过' : 
                authStatus.baseInfo === '2' ? '审核中' : 
                authStatus.baseInfo === '3' ? '已拒绝' : '未审核'})
            </span>
          )}
        </span>
      } 
      size="small"
    >
      <Row gutter={24}>
        <Col span={12}>
          <Form.Item
            name="merchantName"
            label="商户名称"
            rules={[{ required: true, message: '请输入商户名称' }]}
          >
            <Input placeholder="请输入商户全称" maxLength={50} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="businessType"
            label="主营业务"
            rules={[{ required: true, message: '请选择主营业务' }]}
          >
            <Select placeholder="请选择主营业务类型">
              <Option value="retail">零售</Option>
              <Option value="catering">餐饮</Option>
              <Option value="entertainment">娱乐</Option>
              <Option value="education">教育培训</Option>
              <Option value="medical">医疗</Option>
              <Option value="transport">交通运输</Option>
              <Option value="other">其他</Option>
            </Select>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="organizationCode"
            label="统一社会信用代码"
            rules={[
              { required: true, message: '请输入统一社会信用代码' },
              { pattern: /^[0-9A-HJ-NPQRTUWXY]{2}\d{6}[0-9A-HJ-NPQRTUWXY]{10}$/, message: '请输入正确的统一社会信用代码' }
            ]}
          >
            <Input placeholder="请输入18位统一社会信用代码" maxLength={18} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="businessLicense"
            label="营业执照注册号"
            rules={[{ required: true, message: '请输入营业执照注册号' }]}
          >
            <Input placeholder="请输入营业执照注册号" />
          </Form.Item>
        </Col>
      </Row>
    </Card>
  );

  // 渲染经营信息步骤
  const renderBusinessInfo = () => (
    <Card 
      title={
        <span>
          经营信息
          {authStatus.businessInfo && (
            <span style={{ 
              marginLeft: 8, 
              fontSize: '12px',
              color: authStatus.businessInfo === '1' ? '#52c41a' : 
                     authStatus.businessInfo === '2' ? '#1890ff' : 
                     authStatus.businessInfo === '3' ? '#ff4d4f' : '#999'
            }}>
              ({authStatus.businessInfo === '1' ? '已通过' : 
                authStatus.businessInfo === '2' ? '审核中' : 
                authStatus.businessInfo === '3' ? '已拒绝' : '未审核'})
            </span>
          )}
        </span>
      } 
      size="small"
    >
      <Row gutter={24}>
        <Col span={24}>
          <Form.Item
            name="businessScope"
            label="经营范围"
            rules={[{ required: true, message: '请输入经营范围' }]}
          >
            <TextArea rows={4} placeholder="请详细描述经营范围" maxLength={200} />
          </Form.Item>
        </Col>
        <Col span={24}>
          <Form.Item
            name="businessDescription"
            label="经营描述"
            rules={[{ required: true, message: '请输入经营描述' }]}
          >
            <TextArea rows={3} placeholder="请简要描述主要经营内容" maxLength={100} />
          </Form.Item>
        </Col>
        <Col span={24}>
          <Form.Item
            name="businessAddress"
            label="经营地址"
            rules={[{ required: true, message: '请输入经营地址' }]}
          >
            <TextArea rows={2} placeholder="请输入详细的经营地址" maxLength={100} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="servicePhone"
            label="客服电话"
            rules={[
              { required: true, message: '请输入客服电话' },
              { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: '请输入正确的电话号码' }
            ]}
          >
            <Input placeholder="请输入客服电话" />
          </Form.Item>
        </Col>
      </Row>
    </Card>
  );

  // 渲染法人信息步骤
  const renderLegalInfo = () => (
    <Card 
      title={
        <span>
          法定代表人信息
          {authStatus.legalInfo && (
            <span style={{ 
              marginLeft: 8, 
              fontSize: '12px',
              color: authStatus.legalInfo === '1' ? '#52c41a' : 
                     authStatus.legalInfo === '2' ? '#1890ff' : 
                     authStatus.legalInfo === '3' ? '#ff4d4f' : '#999'
            }}>
              ({authStatus.legalInfo === '1' ? '已通过' : 
                authStatus.legalInfo === '2' ? '审核中' : 
                authStatus.legalInfo === '3' ? '已拒绝' : '未审核'})
            </span>
          )}
        </span>
      } 
      size="small"
    >
      <Row gutter={24}>
        <Col span={12}>
          <Form.Item
            name="legalPersonName"
            label="法人姓名"
            rules={[{ required: true, message: '请输入法人姓名' }]}
          >
            <Input placeholder="请输入法人真实姓名" maxLength={20} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="legalPersonIdCard"
            label="法人身份证号"
            rules={[
              { required: true, message: '请输入身份证号' },
              { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号' }
            ]}
          >
            <Input placeholder="请输入18位身份证号" maxLength={18} />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="legalPersonPhone"
            label="法人手机号"
            rules={[
              { required: true, message: '请输入手机号' },
              { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
            ]}
          >
            <Input placeholder="请输入法人手机号" maxLength={11} />
          </Form.Item>
        </Col>
      </Row>
    </Card>
  );

  // 渲染结算信息步骤
  const renderSettlementInfo = () => (
    <Card title="结算银行账户信息" size="small">
      <Row gutter={24}>
        <Col span={12}>
          <Form.Item
            name="accountType"
            label="账户类型"
            rules={[{ required: true, message: '请选择账户类型' }]}
          >
            <Radio.Group>
              <Radio value="corporate">对公账户</Radio>
              <Radio value="personal">个人账户</Radio>
            </Radio.Group>
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="accountName"
            label="开户名称"
            rules={[{ required: true, message: '请输入开户名称' }]}
          >
            <Input placeholder="请输入银行开户名称" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="accountNumber"
            label="银行账号"
            rules={[
              { required: true, message: '请输入银行账号' },
              { pattern: /^\d{16,25}$/, message: '请输入正确的银行账号' }
            ]}
          >
            <Input placeholder="请输入银行账号" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="bankName"
            label="开户银行"
            rules={[{ required: true, message: '请输入开户银行' }]}
          >
            <Input placeholder="请输入开户银行全称" />
          </Form.Item>
        </Col>
        <Col span={12}>
          <Form.Item
            name="bankCode"
            label="银行联行号"
            rules={[{ required: true, message: '请输入银行联行号' }]}
          >
            <Input placeholder="请输入12位银行联行号" maxLength={12} />
          </Form.Item>
        </Col>
      </Row>
    </Card>
  );

  // 渲染资质材料步骤
  const renderQualificationMaterials = () => (
    <div>
      <Alert
        message="上传提醒"
        description="请确保上传的图片清晰可见，文件大小不超过5MB，支持JPG、PNG格式。"
        type="info"
        style={{ marginBottom: 24 }}
      />

      <Row gutter={24}>
        <Col span={12}>
          <Card title="营业执照 *" size="small" style={{ height: '300px' }}>
            <Dragger {...uploadProps('businessLicenseImg')} style={{ height: '200px' }}>
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽上传营业执照</p>
              <p className="ant-upload-hint">请上传营业执照正本照片</p>
            </Dragger>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="法人身份证正面 *" size="small" style={{ height: '300px' }}>
            <Dragger {...uploadProps('legalPersonIdFront')} style={{ height: '200px' }}>
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽上传身份证正面</p>
              <p className="ant-upload-hint">请上传法人身份证正面照片</p>
            </Dragger>
          </Card>
        </Col>

        <Col span={12} style={{ marginTop: 16 }}>
          <Card title="法人身份证背面 *" size="small" style={{ height: '300px' }}>
            <Dragger {...uploadProps('legalPersonIdBack')} style={{ height: '200px' }}>
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽上传身份证背面</p>
              <p className="ant-upload-hint">请上传法人身份证背面照片</p>
            </Dragger>
          </Card>
        </Col>

        <Col span={12} style={{ marginTop: 16 }}>
          <Card title="银行开户许可证 *" size="small" style={{ height: '300px' }}>
            <Dragger {...uploadProps('bankAccountImg')} style={{ height: '200px' }}>
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽上传开户许可证</p>
              <p className="ant-upload-hint">请上传银行开户许可证照片</p>
            </Dragger>
          </Card>
        </Col>

        <Col span={12} style={{ marginTop: 16 }}>
          <Card title="门店照片(可选)" size="small" style={{ height: '300px' }}>
            <Dragger {...uploadProps('storeImg')} style={{ height: '200px' }}>
              <p className="ant-upload-drag-icon">
                <InboxOutlined />
              </p>
              <p className="ant-upload-text">点击或拖拽上传门店照片</p>
              <p className="ant-upload-hint">可上传门店外观或内景照片</p>
            </Dragger>
          </Card>
        </Col>
      </Row>
    </div>
  );

  // 渲染确认提交步骤
  const renderConfirmSubmit = () => {
    const formData = form.getFieldsValue();

    return (
      <div>
        <Alert
          message="请仔细核对以下信息，确认无误后提交申请"
          type="warning"
          style={{ marginBottom: 24 }}
        />

        <Card title="基本信息" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col span={12}>商户名称: {formData.merchantName || '-'}</Col>
            <Col span={12}>主营业务: {formData.businessType || '-'}</Col>
            <Col span={12}>统一社会信用代码: {formData.organizationCode || '-'}</Col>
            <Col span={12}>营业执照注册号: {formData.businessLicense || '-'}</Col>
          </Row>
        </Card>

        <Card title="经营信息" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col span={24}>经营范围: {formData.businessScope || '-'}</Col>
            <Col span={24}>经营地址: {formData.businessAddress || '-'}</Col>
            <Col span={12}>客服电话: {formData.servicePhone || '-'}</Col>
          </Row>
        </Card>

        <Card title="法人信息" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col span={8}>法人姓名: {formData.legalPersonName || '-'}</Col>
            <Col span={8}>身份证号: {formData.legalPersonIdCard || '-'}</Col>
            <Col span={8}>手机号: {formData.legalPersonPhone || '-'}</Col>
          </Row>
        </Card>

        <Card title="结算信息" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col span={8}>账户类型: {formData.accountType === 'corporate' ? '对公账户' : '个人账户'}</Col>
            <Col span={8}>开户名称: {formData.accountName || '-'}</Col>
            <Col span={8}>银行账号: {formData.accountNumber || '-'}</Col>
            <Col span={12}>开户银行: {formData.bankName || '-'}</Col>
            <Col span={12}>银行联行号: {formData.bankCode || '-'}</Col>
          </Row>
        </Card>

        <Card title="上传材料">
          <Row gutter={16}>
            <Col span={6}>营业执照: {fileList.businessLicenseImg?.length > 0 ? '✅ 已上传' : '❌ 未上传'}</Col>
            <Col span={6}>身份证正面: {fileList.legalPersonIdFront?.length > 0 ? '✅ 已上传' : '❌ 未上传'}</Col>
            <Col span={6}>身份证背面: {fileList.legalPersonIdBack?.length > 0 ? '✅ 已上传' : '❌ 未上传'}</Col>
            <Col span={6}>开户许可证: {fileList.bankAccountImg?.length > 0 ? '✅ 已上传' : '❌ 未上传'}</Col>
          </Row>
        </Card>
      </div>
    );
  };

  // 根据当前步骤渲染内容
  const renderStepContent = () => {
    switch (currentStep) {
      case 0: return renderBasicInfo();
      case 1: return renderBusinessInfo();
      case 2: return renderLegalInfo();
      case 3: return renderSettlementInfo();
      case 4: return renderQualificationMaterials();
      case 5: return renderConfirmSubmit();
      default: return null;
    }
  };

  return (
    <>
      <Card style={{ marginBottom: 16 }}>
        <Space style={{ float: 'right' }}>
          <Button
            onClick={() => fetchMerchantAuthDetail(currentRecord)}
            loading={isLoadingData}
            disabled={!currentRecord?.merchantNo}
          >
            刷新数据
          </Button>
          <Button
            type="primary"
            icon={<ArrowLeftOutlined />}
            onClick={() => setIsLoadMerchantAuditDetail(false)}
          >
            返回
          </Button>
        </Space>
        <Descriptions title="微信支付商户进件申请">
          <Descriptions.Item label="商户编号">{currentRecord?.merchantNo || '-'}</Descriptions.Item>
          <Descriptions.Item label="整体审核状态">
            {authStatus.overall === '1' ? '已通过' : 
             authStatus.overall === '2' ? '审核中' : 
             authStatus.overall === '3' ? '已拒绝' : '未审核'}
          </Descriptions.Item>
          <Descriptions.Item label="整体状态">
            {authStatus.status === '0' ? '正常' : 
             authStatus.status === '1' ? '禁用' : '未知'}
          </Descriptions.Item>
        </Descriptions>


        {/* 页面标题 */}
        <Card style={{ marginBottom: 24 }}>
          <div style={{ textAlign: 'center' }}>
            <h1 style={{ margin: 0, color: '#1890ff' }}>微信支付商户进件申请</h1>
            <p style={{ color: '#666', marginTop: 8 }}>
              {isLoadingData ? '正在加载数据...' : '请按步骤填写完整的商户资料，我们将在1-3个工作日内完成审核'}
            </p>
          </div>
        </Card>

        {/* 步骤条 */}
        <Card style={{ marginBottom: 24 }}>
          <Steps current={currentStep} size="small">
            {steps.map((step, index) => (
              <Step
                key={index}
                title={step.title}
                description={step.description}
              />
            ))}
          </Steps>
        </Card>

        {/* 表单内容 */}
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            accountType: 'corporate'
          }}
          disabled={isLoadingData}
        >
          {renderStepContent()}
        </Form>

        {/* 操作按钮 */}
        <Card style={{ marginTop: 24 }}>
          <div style={{ textAlign: 'center' }}>
            <Space size="large">
              {currentStep > 0 && (
                <Button onClick={handlePrev} disabled={isLoadingData}>
                  上一步
                </Button>
              )}

              <Button
                icon={<SaveOutlined />}
                onClick={handleSaveDraft}
                disabled={isLoadingData}
              >
                保存草稿
              </Button>

              {currentStep < steps.length - 1 ? (
                <Button type="primary" onClick={handleNext} disabled={isLoadingData}>
                  下一步
                </Button>
              ) : (
                <Button
                  type="primary"
                  icon={<SendOutlined />}
                  onClick={handleSubmit}
                  loading={loading}
                  disabled={isLoadingData}
                >
                  提交申请
                </Button>
              )}
            </Space>
          </div>
        </Card>

        {/* 图片预览模态框 */}
        <Modal
          open={previewVisible}
          title="图片预览"
          footer={null}
          onCancel={() => setPreviewVisible(false)}
        >
          <img alt="预览" style={{ width: '100%' }} src={previewImage} />
        </Modal>
      </Card>
    </>
  );

}