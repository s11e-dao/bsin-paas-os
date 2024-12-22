import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  Divider,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Upload,
  Drawer,
  Card,
  Alert,
  Radio,
  Space
} from 'antd';

import {
  addAppPayChannel,
} from '../pay/PayChannelInterface/service';

export default ({ currenBizRoleApp, currentPayChannel }) => {

  const { TextArea } = Input;

  const [open, setOpen] = useState(false);

  const [payChannelConfigFormRef] = Form.useForm();

  /**
     * 保存支付通道配置
     */
  const addAppPayChannelConfig = () => {
    // 获取输入的表单值
    payChannelConfigFormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = payChannelConfigFormRef.getFieldsValue();
        console.log(response);
        let reqParam = {
          ...response,
        };
        console.log(reqParam);
        reqParam.bizRoleAppId = currenBizRoleApp.appId;
        reqParam.payChannelCode = currentPayChannel.payChannelCode;
        addAppPayChannel(reqParam).then((res) => {
          console.log('add', res);
          if (res.code === 0) {
            message.success('修改成功');
            // 重置输入的表单
            payChannelConfigFormRef.resetFields();
            // 刷新proTable
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });

      })
      .catch(() => { });
  };

  return (
    <div>
      {currentPayChannel?.payChannelCode == "alipay" ? (
        <Form name="trigger" form={payChannelConfigFormRef} style={{ maxWidth: 600 }} layout="vertical" autoComplete="off">
          {/* <Alert message="Use 'max' rule, continue type chars to see it" /> */}

          <Form.Item
            hasFeedback
            label="支付参数"
            name="params"
            validateTrigger="onBlur"
            rules={[{ required: true, message: '支付参数不能为空!' }]}
          >
            <TextArea
              placeholder="Controlled autosize"
              autoSize={{ minRows: 3, maxRows: 5 }}
            />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="状态"
            name="field_a"
            validateTrigger="onBlur"
          >
            <Radio.Group options={[{ label: '启用', value: '1' }, { label: '停用', value: '0' },]} defaultValue="0" />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="AppID"
            name="field_a"
            validateTrigger="onBlur"
          >
            <Input placeholder="Validate required onBlur" />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="支付宝商户号"
            name="field_b"
            validateDebounce={1000}
          >
            <Input placeholder="Validate required debounce after 1s" />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="支付宝API版本"
            name="field_a"
            validateTrigger="onBlur"
          >
            <Radio.Group options={[{ label: 'V2', value: '1' }, { label: 'V3', value: '0' },]} defaultValue="0" />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="APIv2密钥"
            name="field_a"
            validateTrigger="onBlur"
            rules={[{ max: 3 }]}
          >
            <TextArea
              placeholder="Controlled autosize"
              autoSize={{ minRows: 3, maxRows: 5 }}
            />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="APIv3密钥"
            name="field_a"
            validateTrigger="onBlur"
          >
            <TextArea
              placeholder="Controlled autosize"
              autoSize={{ minRows: 3, maxRows: 5 }}
            />
          </Form.Item>
        </Form>
      ) : (
        <Form name="trigger" form={payChannelConfigFormRef} style={{ maxWidth: 600 }} layout="vertical" autoComplete="off">
          {/* <Alert message="Use 'max' rule, continue type chars to see it" /> */}

          <Form.Item
            hasFeedback
            label="支付参数"
            name="params"
            validateTrigger="onBlur"
            rules={[{ required: true, message: '支付参数不能为空!' }]}
          >
            <TextArea
              placeholder="Controlled autosize"
              autoSize={{ minRows: 3, maxRows: 5 }}
            />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="状态"
            name="field_a"
            validateTrigger="onBlur"
          >
            <Radio.Group options={[{ label: '启用', value: '1' }, { label: '停用', value: '0' },]} defaultValue="0" />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="AppID"
            name="field_a"
            validateTrigger="onBlur"
          >
            <Input placeholder="Validate required onBlur" />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="微信支付商户号"
            name="field_b"
            validateDebounce={1000}
          >
            <Input placeholder="Validate required debounce after 1s" />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="微信支付API版本"
            name="field_a"
            validateTrigger="onBlur"
          >
            <Radio.Group options={[{ label: 'V2', value: '1' }, { label: 'V3', value: '0' },]} defaultValue="0" />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="APIv2密钥"
            name="field_a"
            validateTrigger="onBlur"
          >
            <TextArea
              placeholder="Controlled autosize"
              autoSize={{ minRows: 3, maxRows: 5 }}
            />
          </Form.Item>

          <Form.Item
            hasFeedback
            label="APIv3密钥"
            name="field_a"
            validateTrigger="onBlur"
          >
            <TextArea
              placeholder="Controlled autosize"
              autoSize={{ minRows: 3, maxRows: 5 }}
            />
          </Form.Item>
        </Form>
      )}

      <Button type="primary" onClick={addAppPayChannelConfig} >保存</Button>
    </div>
  );
};
