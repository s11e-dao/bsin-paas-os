import React from 'react';
import { Form, InputNumber, Button, Card, Alert } from 'antd';
import { getLocalStorageInfo } from '../../../utils/localStorageInfo';
import { configDisBrokerageConfig, getDisBrokerageConfigDetail } from './service';

export default () => {
  const userInfo = getLocalStorageInfo('userInfo');
  const [formRef] = Form.useForm();

  React.useEffect(() => {
    getDisBrokerageConfigDetail({}).then((res) => {
      if (res?.data) {
        formRef.setFieldsValue(res?.data);
      }
    });
  }, []);

  const confirmActivity = () => {
    formRef.validateFields()
      .then(() => {
        const formData = formRef.getFieldsValue();
        configDisBrokerageConfig(formData).then((res) => {
          if (res.code === 0) {
            message.success("设置成功");
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  const renderOperatorForm = () => (
    <>
      <Form.Item
        label="运营平台分佣比例"
        name="superTenantRate"
        rules={[{ required: true, message: '请输入给运营平台分佣比例!' }]}
      >
        <InputNumber addonAfter="%" defaultValue={0} />
      </Form.Item>

      <Form.Item
        label="租户平台分佣比例"
        name="tenantRate"
        rules={[{ required: true, message: '请输入给租户平台分佣比例!' }]}
      >
        <InputNumber addonAfter="%" defaultValue={0} />
      </Form.Item>

      <Form.Item
        label="代理商分佣比例"
        name="sysAgentRate"
        rules={[{ required: true, message: '请输入给代理商分佣比例!' }]}
      >
        <InputNumber addonAfter="%" defaultValue={0} />
      </Form.Item>

      <Form.Item
        label="消费者返利比例"
        name="customerRate"
        rules={[{ required: true, message: '请输入给消费者返利比例!' }]}
      >
        <InputNumber addonAfter="%" defaultValue={0} />
      </Form.Item>
    </>
  );

  const renderMerchantForm = () => (
    <Form.Item
      label="商户让利比例"
      name="merchantSharingRate"
      rules={[{ required: true, message: '请输入商户让利比例!' }]}
    >
      <InputNumber addonAfter="%" defaultValue={0} />
    </Form.Item>
  );

  return (
    <Card title="分账比例设置">
      <Alert
        message="比例设置规则"
        description="运营平台分佣比例、租户平台分佣比例、代理商分佣比例、消费者返利比例、所有比例加起来等于100"
        type="warning"
      />
      <br />
      <Form
        form={formRef}
        labelCol={{ span: 8 }}
        wrapperCol={{ span: 16 }}
        style={{ maxWidth: 600 }}
      >
        {renderOperatorForm()}
        
        <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
            <Button type="primary" onClick={confirmActivity}>
              保存
            </Button>
          </Form.Item>
      </Form>
    </Card>
  );
};