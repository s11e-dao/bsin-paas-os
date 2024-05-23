import React, { useEffect, useState } from 'react'
import { UploadOutlined } from '@ant-design/icons'
import { Button, Input, Upload, message, Form, Flex } from 'antd'
import ProForm, {
  ProFormDependency,
  ProFormFieldSet,
  ProFormSelect,
  ProFormText,
  ProFormSwitch,
  ProFormTextArea,
} from '@ant-design/pro-form'
import { getCopilotList, editCopilot } from '../service'
import ImageViewAndUpload from '../../../components/ImageViewAndUpload'

import styles from './BaseView.less'
import cstyles from '../style.less'

const BaseView: React.FC = (copilotInfo) => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl
  let tenantAppType = process.env.tenantAppType
  const [llmForm] = Form.useForm()
  const [embeddingModelForm] = Form.useForm()
  const [promptTemplateForm] = Form.useForm()
  const [knowledgeBaseForm] = Form.useForm()
  const [copilotForm] = Form.useForm()

  const [avatarUrl, setAvatarUrl] = useState<string>()
  const setUploadAvatarUrl = (
    avatarUrl: React.SetStateAction<string | undefined>,
  ) => {
    setAvatarUrl(avatarUrl)
  }

  // 查询Copilot信息
  useEffect(() => {
    console.log('copilotInfo\n\n')
    console.log(copilotInfo)
    console.log(copilotInfo?.copilotInfo?.avatar)
    setAvatarUrl(
      copilotInfo?.copilotInfo?.avatar != null
        ? copilotInfo?.copilotInfo?.avatar
        : 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png',
    )
  }, [])

  const handleFinish = async () => {
    copilotForm
      .validateFields()
      .then(async () => {
        var response = copilotForm.getFieldsValue()
        console.log(response)
        if (response.editable == false) {
          message.warning('该配置不支持编辑！！')
          return
        }

        console.log('avatarUrl\n')
        console.log(avatarUrl)
        if (avatarUrl != null && avatarUrl != '') {
          response.avatar = avatarUrl
          copilotForm.setFieldValue('avatar', avatarUrl)
        }
        let res = await editCopilot({ ...response })
        res
          ? message.success('更新基本信息成功')
          : message.error('更新基本信息失败！')
      })
      .catch(() => {})
  }
  return (
    <>
      <div className={cstyles.title}>基本信息</div>
      <div className={styles.baseView}>
        <div className={styles.left}>
          <ProForm
            layout="vertical"
            onFinish={handleFinish}
            submitter={{
              searchConfig: {
                submitText: '更新基本信息',
              },
              render: (_, dom) => dom[1],
            }}
            initialValues={{
              ...copilotInfo.copilotInfo,
            }}
            hideRequiredMark
            form={copilotForm}
          >
            <ProFormText
              width="md"
              name="serialNo"
              label="CopilotID"
              disabled
            />
            <ProFormText width="md" name="tenantId" label="租户号" disabled />
            <ProFormText width="md" name="merchantNo" label="商户号" disabled />
            <ProFormText width="md" name="customerNo" label="客户号" disabled />
            <ProFormText width="md" name="avatar" label="头像url" />
            <ProFormText
              width="md"
              name="name"
              label="Copilot名称"
              rules={[
                {
                  required: true,
                  message: '请输入您的名称!',
                },
              ]}
            />
            <ProFormSwitch
              width="md"
              name="streaming"
              label="流式回复"
              disabled
              tooltip="和绑定的llm相关,不可编辑"
            />
            <ProFormSwitch
              width="md"
              name="editable"
              label="是否可编辑"
              disabled
              tooltip="若为系统默认配置则不支持编辑"
            />
            <ProFormSelect
              width="md"
              name="type"
              label="Copilot类型"
              rules={[
                {
                  required: true,
                  message: '请选择Copilot类型!',
                },
              ]}
              options={[
                {
                  label: '品牌官',
                  value: '1',
                },
                {
                  label: '数字分身',
                  value: '2',
                },
                {
                  label: '通用copilot',
                  value: '3',
                },
              ]}
            />

            <ProFormTextArea
              name="description"
              label="Copilot简介"
              rules={[
                {
                  required: true,
                  message: '请输入Copilot简介!',
                },
              ]}
              placeholder="Copilot简介"
            />
          </ProForm>
        </div>
        <div className={styles.right}>
          <ImageViewAndUpload
            describe={'更换头像'}
            avatar={
              copilotInfo?.copilotInfo?.avatar != null
                ? copilotInfo?.copilotInfo?.avatar
                : 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png'
            }
            setUploadAvatarUrl={setUploadAvatarUrl}
          />
        </div>
      </div>
    </>
  )
}

export default BaseView
