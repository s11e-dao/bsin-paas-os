import React, { useEffect, useState } from 'react'
import { UploadOutlined } from '@ant-design/icons'
import { Button, Input, Upload, message, Form } from 'antd'
import ProForm, {
  ProFormDependency,
  ProFormFieldSet,
  ProFormSelect,
  ProFormText,
  ProFormSwitch,
  ProFormTextArea,
} from '@ant-design/pro-form'
import { useRequest } from 'umi'
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'
import { editKnowledgeBase, getKnowledgeBaseList } from '../service'
import type { UploadChangeParam } from 'antd/es/upload'
import type { RcFile, UploadFile, UploadProps } from 'antd/es/upload/interface'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons'
import styles from './BaseView.less'
import cstyles from '../style.less'
import ImageViewAndUpload from '../../../components/ImageViewAndUpload'

const BaseView: React.FC = (knowledgeBaseInfo) => {
  const [knowledgeBaseForm] = Form.useForm()

  const [coverImageUrl, setCoverImageUrl] = useState<string>()

  const setUploadAvatarUrl = (
    avatarUrl: React.SetStateAction<string | undefined>,
  ) => {
    setCoverImageUrl(avatarUrl)
  }

  // 查询知识库信息
  useEffect(() => {
    console.log('copilotInfo\n\n')
    console.log(knowledgeBaseInfo)

    setCoverImageUrl(
      knowledgeBaseInfo?.record?.coverImage != null
        ? knowledgeBaseInfo?.record?.coverImage
        : 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png',
    )
  }, [])

  const refreshKnowledgeBaseForm = (nodeInfo: object) => {
    knowledgeBaseForm.resetFields()
    knowledgeBaseForm.setFieldsValue(nodeInfo)
  }

  const handleFinish = async () => {
    knowledgeBaseForm
      .validateFields()
      .then(async () => {
        var response = knowledgeBaseForm.getFieldsValue()
        if (response.editable == false) {
          message.warning('该配置不支持编辑！！')
          return
        }
        console.log('coverImageUrl\n')
        console.log(coverImageUrl)
        if (coverImageUrl != null && coverImageUrl != '') {
          response.coverImage = coverImageUrl
          knowledgeBaseForm.setFieldValue('coverImage', coverImageUrl)
        }
        console.log(response)

        let res = await editKnowledgeBase({ ...response })
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
              ...knowledgeBaseInfo.record,
            }}
            hideRequiredMark
            form={knowledgeBaseForm}
          >
            <ProFormText width="md" name="serialNo" label="知识库ID" disabled />
            <ProFormText width="md" name="tenantId" label="租户号" disabled />
            <ProFormText width="md" name="merchantNo" label="商户号" disabled />
            <ProFormText width="md" name="customerNo" label="客户号" disabled />
            <ProFormText
              width="md"
              name="coverImage"
              label="头像url"
              // disabled
            />
            <ProFormSwitch
              width="md"
              name="editable"
              label="是否可编辑"
              disabled
              tooltip="若为系统默认配置则不支持编辑"
            />
            <ProFormText
              width="md"
              name="name"
              label="知识库名称"
              rules={[
                {
                  required: true,
                  message: '请输入您的名称!',
                },
              ]}
            />
            <ProFormTextArea
              name="description"
              label="知识库简介"
              rules={[
                {
                  required: true,
                  message: '请输入知识库简介!',
                },
              ]}
              placeholder="知识库简介"
            />
            <ProFormText
              width="md"
              name="tokenLimit"
              label="单条数据上限"
              rules={[
                {
                  required: true,
                  message: '请输入单条数据上限!',
                },
              ]}
            />
          </ProForm>
        </div>
        <div className={styles.right}>
          <ImageViewAndUpload
            describe={'更换头像'}
            avatar={
              knowledgeBaseInfo?.record?.coverImage != null
                ? knowledgeBaseInfo?.record?.coverImage
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
