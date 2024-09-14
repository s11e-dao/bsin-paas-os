import React, { useState, useEffect } from 'react'
import { Button, message, Upload } from 'antd'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons'
import type { UploadChangeParam } from 'antd/es/upload'
import type { RcFile, UploadFile, UploadProps } from 'antd/es/upload/interface'

import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo'

export default ({ avatar, setUploadAvatarUrl }) => {
  let bsinFileUploadUrl = process.env.bsinFileUploadUrl
  let tenantAppType = process.env.tenantAppType

  const [avatarUrl, setAvatarUrl] = useState<string>()

  // 查询Copilot信息
  useEffect(() => {
    setAvatarUrl(
      avatar != null
        ? avatar
        : 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png',
    )
  }, [])

  const handleChange: UploadProps['onChange'] = (
    info: UploadChangeParam<UploadFile>,
  ) => {
    console.log(info)
    if (info.file.status === 'uploading') {
      console.log('uploading')
      return
    }
    if (info.file.status === 'done') {
      console.log('file.response:\n')
      console.log(info.file.response)
      message.success(`${info.file.name} file uploaded successfully.`)
      setAvatarUrl(info.file?.response.data.url)
      setUploadAvatarUrl(info.file?.response.data.url)
    } else if (info.file?.status === 'error') {
      message.error(`${info.file.name} file upload failed.`)
    }
  }

  const uploadButton = (
    <button style={{ border: 0, background: 'none' }} type="button">
      <PlusOutlined />
      <div style={{ marginTop: 8 }}>更换头像</div>
    </button>
  )
  const beforeUpload = (file: RcFile) => {
    const isJpgOrPng = true
    // const isJpgOrPng = file.type === 'image/jpeg/' || file.type === 'image/png'
    // if (!isJpgOrPng) {
    //   message.error('You can only upload JPG/PNG file!')
    // }
    const isLt2M = file.size / 1024 / 1024 < 2
    if (!isLt2M) {
      message.error('Image must smaller than 2MB!')
    }
    return isJpgOrPng && isLt2M
  }

  return (
    <>
      <Upload
        listType="picture-circle"
        className="avatar-uploader"
        showUploadList={false}
        headers={{ Authorization: getSessionStorageInfo('token')?.token }}
        data={{
          tenantAppType: tenantAppType,
        }}
        maxCount={1}
        action={bsinFileUploadUrl}
        beforeUpload={beforeUpload}
        onChange={handleChange}
      >
        {avatarUrl ? (
          <div style={{ display: 'flex', flexDirection: 'column-reverse' }}>
            <Button style={{ marginTop: 8 }} icon={<PlusOutlined />}>
              更换头像
            </Button>
            <img src={avatarUrl} alt="avatar" style={{ width: '100%' }} />
          </div>
        ) : (
          uploadButton
        )}
      </Upload>
    </>
  )
}
