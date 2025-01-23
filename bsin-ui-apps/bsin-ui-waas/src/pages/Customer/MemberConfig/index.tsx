import React, { useState } from 'react';
import { Form, InputNumber, Button, Card, Alert, Radio } from 'antd';
import type { RadioChangeEvent } from 'antd';

import { getLocalStorageInfo } from '../../../utils/localStorageInfo';
import { configDisBrokerageConfig, getDisBrokerageConfigDetail } from './service';

export default () => {

  const userInfo = getLocalStorageInfo('userInfo');
  const [formRef] = Form.useForm();

  const [value, setValue] = useState(1);

  const onChange = (e: RadioChangeEvent) => {
    console.log('radio checked', e.target.value);
    setValue(e.target.value);
  };

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
      .catch(() => { });
  };

  const renderOperatorForm = () => (
    <>
      <Form.Item
        label="会员模型"
        name="superTenantRate"
        rules={[{ required: true, message: '请输入给运营平台分佣比例!' }]}
      >
        <Radio.Group onChange={onChange} value={value}>
          <Radio value={1}>平台会员</Radio>
          <Radio value={2}>商户会员</Radio>
          <Radio value={3}>门店会员</Radio>
        </Radio.Group>
      </Form.Item>
    </>
  );

  const renderMerchantForm = () => (
    <>
      <Form.Item
        label="会员模型"
        name="merchantSharingRate"
        rules={[{ required: true, message: '请输入商户让利比例!' }]}
      >
        <Radio.Group onChange={onChange} value={value}>
          <Radio value={1}>平台会员</Radio>
          <Radio value={2}>商户会员</Radio>
          <Radio value={3}>门店会员</Radio>
        </Radio.Group>
      </Form.Item>
    </>
  );

  const renderForm = () => {
    const isOperator = userInfo?.bizRoleType === "2";
    const isMerchant = userInfo?.bizRoleType === "3";

    return (
      <>
        {isOperator && renderOperatorForm()}
        {isMerchant && renderMerchantForm()}

        {(isOperator || isMerchant) && (
          <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
            <Button type="primary" onClick={confirmActivity}>
              保存
            </Button>
          </Form.Item>
        )}
      </>
    );
  };

  return (
    <Card title="会员配置">
      <Alert
        message="会员配置"
        description="平台和商户可以配置自己的会员模型：平台会员、商户会员、门店会员"
        type="warning"
      />
      <br />
      <Form
        form={formRef}
        labelCol={{ span: 8 }}
        wrapperCol={{ span: 16 }}
        style={{ maxWidth: 600 }}
      >
        {renderForm()}
      </Form>
    </Card>
  );
};