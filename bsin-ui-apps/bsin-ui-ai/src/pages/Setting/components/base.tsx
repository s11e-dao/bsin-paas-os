import React, { useEffect, useState } from 'react'
import { UploadOutlined } from '@ant-design/icons'
import { Button, Input, Upload, Form, message } from 'antd'
import ProForm, {
  ProFormDependency,
  ProFormFieldSet,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-form'
import { setLocalStorageInfo } from '@/utils/localStorageInfo'

import styles from './baseView.less'
import ImageViewAndUpload from '../../../components/ImageViewAndUpload'
import { eidtCustomerService } from '../service'

const validatorPhone = (
  rule: any,
  value: string[],
  callback: (message?: string) => void,
) => {
  if (!value[0]) {
    callback('Please input your area code!')
  }
  if (!value[1]) {
    callback('Please input your phone number!')
  }
  callback()
}

const BaseView: React.FC = ({ customerInfo }) => {
  const [avatarUrl, setAvatarUrl] = useState<string>()
  // 获取表单
  const [editFormRef] = Form.useForm()

  const setUploadAvatarUrl = (
    avatarUrl: React.SetStateAction<string | undefined>,
  ) => {
    setAvatarUrl(avatarUrl)
  }

  const handleFinish = async () => {
    // 获取输入的表单值
    editFormRef
      .validateFields()
      .then(async () => {
        // 获取表单结果
        let response = editFormRef.getFieldsValue()
        response.avatar = avatarUrl
        editFormRef.setFieldValue('avatar', avatarUrl)
        console.log(response)
        eidtCustomerService(response).then((res) => {
          console.log('eidtCustomerService', res)
          if (res.code == '000000') {
            // setLocalStorageInfo('customerInfo', response.data)
            message.success('更新基本信息成功')
          }
        })
        // 重置输入的表单
        // editFormRef.resetFields()
        // 刷新proTable
      })
      .catch(() => {})
  }
  return (
    <div className={styles.baseView}>
      <div className={styles.left}>
        <ProForm
          layout="vertical"
          form={editFormRef}
          onFinish={handleFinish}
          submitter={{
            searchConfig: {
              submitText: '更新基本信息',
            },
            render: (_, dom) => dom[1],
          }}
          initialValues={{
            ...customerInfo,
            phone: customerInfo?.phone?.split('-'),
          }}
          hideRequiredMark
        >
          <ProFormText width="md" name="tenantId" label="租户号" disabled />
          <ProFormText width="md" name="customerNo" label="客户号" disabled />
          <ProFormText
            width="md"
            name="username"
            label="登录名"
            // rules={[
            //   {
            //     required: true,
            //     message: '请输入登录名!',
            //   },
            // ]}
            disabled
          />
          <ProFormText width="xl" name="avatar" label="头像地址" disabled />

          <ProFormText
            width="lg"
            name="walletAddress"
            label="conflux地址"
            disabled
          />
          <ProFormText
            width="lg"
            name="evmWalletAddress"
            label="evm地址"
            disabled
          />
          <ProFormTextArea
            name="info"
            label="个人简介"
            rules={[
              {
                required: true,
                message: '请输入个人简介!',
              },
            ]}
            placeholder="个人简介"
          />
          <ProFormText
            width="md"
            name="phone"
            label="联系电话"
            // rules={[
            //   {
            //     // required: false,
            //     message: '请输入联系电话!',
            //   },
            // ]}
            disabled
          />
          <ProFormText
            width="md"
            name="email"
            label="邮箱"
            // rules={[
            //   {
            //     // required: false,
            //     message: '请输入您的邮箱!',
            //   },
            // ]}
            disabled
          />
        </ProForm>
      </div>
      <div className={styles.right}>
        <ImageViewAndUpload
          describe={'更换头像'}
          avatar={
            customerInfo.avatar == null
              ? 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png'
              : customerInfo.avatar
          }
          setUploadAvatarUrl={setUploadAvatarUrl}
        />
      </div>
    </div>
  )
}

export default BaseView
