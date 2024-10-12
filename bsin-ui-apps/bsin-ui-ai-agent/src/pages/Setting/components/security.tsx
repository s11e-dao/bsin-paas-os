import React, { useState, useEffect } from 'react'
import { List, Modal, Form, Input, message } from 'antd'

type Unpacked<T> = T extends (infer U)[] ? U : T

import {
  settingWallet,
  settingEmail,
  settingPhone,
  getCustomerDetail,
  getMpVerifyCode,
  verifyMpCode,
} from '../service'

const passwordStrength = {
  strong: <span className="strong">强</span>,
  medium: <span className="medium">中</span>,
  weak: <span className="weak">弱 Weak</span>,
}

const SecurityView: React.FC = () => {
  const [settingWalletModal, setSettingWalletModal] = useState(false)
  const [settingPhoneModal, setSettingPhoneModal] = useState(false)
  const [settingEmailModal, setSettingEmailModal] = useState(false)
  const [getMpVerifyCodeModel, setGetMpVerifyCodeModel] = useState(false)

  const [customerInfo, setCustomerInfo] = useState({})
  // 获取表单
  const [FormRef] = Form.useForm()

  // 查询用户信息
  useEffect(() => {
    getCustomerDetail({}).then((res) => {
      if (res.code == '000000') {
        FormRef.setFieldsValue(res.data)
        setCustomerInfo(res.data)
      }
    })
  }, [])

  const getData = () => [
    {
      title: '区块链密钥',
      description: (
        <>
          是否设置密钥：
          {customerInfo?.walletAddress ? '是' : '否'}
        </>
      ),
      actions: [
        <a
          key="Modify"
          onClick={() => {
            setSettingWalletModal(true)
          }}
        >
          设置
        </a>,
      ],
    },
    {
      title: '手机',
      description: (
        <>
          {customerInfo?.phone != null ? (
            <>
              已绑定手机:
              {customerInfo?.phone}
            </>
          ) : (
            <>还未绑定手机</>
          )}
        </>
      ),
      actions: [
        <a
          key="Modify"
          onClick={() => {
            setSettingPhoneModal(true)
          }}
        >
          修改
        </a>,
      ],
    },
    {
      title: '密保问题',
      description: '未设置密保问题，密保问题可有效保护账户安全',
      actions: [<a key="Set">设置</a>],
    },
    {
      title: '邮箱',
      description: (
        <>
          {customerInfo?.email != null ? (
            <>
              已绑定邮箱:
              {customerInfo?.email}
            </>
          ) : (
            <>还未绑定邮箱</>
          )}
        </>
      ),
      actions: [
        <a
          key="Modify"
          onClick={() => {
            setSettingEmailModal(true)
          }}
        >
          修改
        </a>,
      ],
    },
    {
      title: '获取验证码测试',
      description: '从公众号获取验证码',
      actions: [
        <a
          key="Modify"
          onClick={() => {
            toGetMpVerifyCode()
          }}
        >
          获取验证码
        </a>,
      ],
    },
  ]

  // 设置钱包
  const settingWalletHandle = () => {
    setSettingWalletModal(false)
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue()
        console.log(response)
        settingWallet(response).then((res) => {
          console.log('settingWallet', res)
          if (res.code == '000000') {
            message.info('设置成功')
          }
        })
        // 重置输入的表单
        FormRef.resetFields()
        // 刷新proTable
      })
      .catch(() => {})
  }

  // 设置手机
  const settingPhoneHandle = () => {
    setSettingPhoneModal(false)
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue()
        console.log(response)
        settingPhone(response).then((res) => {
          console.log('settingPhone', res)
          if (res.code == '000000') {
            message.info('设置成功')
          }
        })
        // 重置输入的表单
        FormRef.resetFields()
      })
      .catch(() => {})
  }

  // 设置邮箱
  const settingEmailHandle = () => {
    setSettingEmailModal(false)
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue()
        console.log(response)
        settingEmail(response).then((res) => {
          console.log('settingEmail', res)
          if (res.code == '000000') {
            message.info('设置成功')
          }
        })
        // 重置输入的表单
        FormRef.resetFields()
      })
      .catch(() => {})
  }

  const toGetMpVerifyCode = () => {
    message.info('验证码已经发送，请查收...')
    setGetMpVerifyCodeModel(true)
    let parmas = {
      // customerNo: '1',
    }
    getMpVerifyCode(parmas).then((res) => {
      console.log('getMpVerifyCode', res)
      if (res.code == '000000') {
        message.info('验证码发送成功，请在火源社区公众号获取！！！')
      }
    })
  }

  // 设置邮箱
  const mpVerifyCodeCodeHandle = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue()
        console.log(response)
        verifyMpCode(response).then((res) => {
          console.log('verifyMpCode', res)
          if (res.code == '000000') {
            message.info('验证成功')
            setGetMpVerifyCodeModel(false)
          }else(
            message.error(res.message)
          )
        })
        // 重置输入的表单
        FormRef.resetFields()
      })
      .catch(() => {})
  }

  const data = getData()
  return (
    <>
      <List<Unpacked<typeof data>>
        itemLayout="horizontal"
        dataSource={data}
        renderItem={(item) => (
          <List.Item actions={item.actions}>
            <List.Item.Meta title={item.title} description={item.description} />
          </List.Item>
        )}
      />

      <Modal
        closable={false}
        open={settingWalletModal}
        onOk={settingWalletHandle}
        onCancel={() => {
          setSettingWalletModal(false)
        }}
        title={'设置钱包地址'}
        width={800}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{}}
        >
          <Form.Item
            label="conflux钱包地址"
            name="walletAddress"
            rules={[{ required: true, message: '请输入conflux钱包地址!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="conflux钱包私钥" name="walletPrivateKey">
            <Input.Password />
          </Form.Item>
          <Form.Item
            label="evm钱包地址"
            name="evmWalletAddress"
            rules={[{ required: true, message: '请输入evm钱包地址!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="evm钱包私钥" name="evmWalletPrivateKey">
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        closable={false}
        open={settingPhoneModal}
        onOk={settingPhoneHandle}
        onCancel={() => {
          setSettingPhoneModal(false)
        }}
        title={'修改手机'}
        width={800}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{}}
        >
          <Form.Item
            label="手机号"
            name="phone"
            rules={[{ required: true, message: '请输入手机号!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        closable={false}
        open={settingEmailModal}
        onOk={settingEmailHandle}
        onCancel={() => {
          setSettingEmailModal(false)
        }}
        title={'设置钱包地址'}
        width={800}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{}}
        >
          <Form.Item
            label="邮箱地址"
            name="email"
            rules={[{ required: true, message: '请输入邮箱地址!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        closable={false}
        open={getMpVerifyCodeModel}
        onOk={mpVerifyCodeCodeHandle}
        onCancel={() => {
          setGetMpVerifyCodeModel(false)
        }}
        title={'获取验证码'}
        width={800}
      >
        <Form
          name="basic"
          form={FormRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          // 表单默认值
          initialValues={{}}
        >
          <Form.Item
            label="6位验证码"
            name="mpVerifyCode"
            rules={[{ required: true, message: '请输入6位验证码!' }]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default SecurityView
