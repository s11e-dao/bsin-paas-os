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
import { getPromptTemplateList, editPromptTemplate } from '../service'
import ImageViewAndUpload from '../../../components/ImageViewAndUpload'

import styles from './baseView.less'
import cstyles from '../style.less'

const BaseView: React.FC = (promptTemplateInfo) => {
  const [promptTemplateForm] = Form.useForm()

  const [coverImageUrl, setCoverImageUrl] = useState<string>()
  const setUploadCoverImageUrl = (
    coverImageUrl: React.SetStateAction<string | undefined>,
  ) => {
    setCoverImageUrl(coverImageUrl)
  }

  // 查询PromptTemplate信息
  useEffect(() => {
    console.log('promptTemplateInfo\n\n')
    console.log(promptTemplateInfo)
    console.log(promptTemplateInfo?.promptTemplateInfo?.coverImage)
    setCoverImageUrl(
      promptTemplateInfo?.promptTemplateInfo?.coverImage != null
        ? promptTemplateInfo?.promptTemplateInfo?.coverImage
        : 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png',
    )
  }, [])

  const handleFinish = async () => {
    promptTemplateForm
      .validateFields()
      .then(async () => {
        var response = promptTemplateForm.getFieldsValue()
        console.log(response)
        if (response.editable == false) {
          message.warning('该配置不支持编辑！！')
          return
        }
        console.log('coverImageUrl\n')
        console.log(coverImageUrl)
        if (coverImageUrl != null && coverImageUrl != '') {
          response.coverImage = coverImageUrl
          promptTemplateForm.setFieldValue('coverImage', coverImageUrl)
        }
        let res = await editPromptTemplate({ ...response })
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
              ...promptTemplateInfo.promptTemplateInfo,
            }}
            hideRequiredMark
            form={promptTemplateForm}
          >
            <ProFormText width="md" name="serialNo" label="模版ID" disabled />
            <ProFormText width="md" name="tenantId" label="租户号" disabled />
            <ProFormText width="md" name="merchantNo" label="商户号" disabled />
            <ProFormText width="md" name="customerNo" label="客户号" disabled />
            <ProFormText width="md" name="coverImage" label="封面url" />
            <ProFormText
              width="md"
              name="name"
              label="名称"
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
              label="类型"
              rules={[
                {
                  required: true,
                  message: '请选择类型!',
                },
              ]}
              disabled
              options={[
                {
                  label: '个人模版',
                  value: '0',
                },
                {
                  label: '系统模版',
                  value: '1',
                },
              ]}
            />

            <ProFormTextArea
              name="description"
              label="模版简介"
              rules={[
                {
                  required: true,
                  message: '请输入简介!',
                },
              ]}
              placeholder="PromptTemplate简介"
            />
          </ProForm>
        </div>
        <div className={styles.right}>
          <ImageViewAndUpload
            describe={'更换头像'}
            avatar={
              promptTemplateInfo?.promptTemplateInfo?.coverImage != null
                ? promptTemplateInfo?.promptTemplateInfo?.coverImage
                : 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png'
            }
            setUploadAvatarUrl={setUploadCoverImageUrl}
          />
        </div>
      </div>
    </>
  )
}

export default BaseView
