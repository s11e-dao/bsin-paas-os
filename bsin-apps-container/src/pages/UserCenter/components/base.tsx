import React, { useEffect, useState } from 'react';
import { UploadOutlined } from '@ant-design/icons';
import { Button, Input, Upload, Form, message } from 'antd';
import ProForm, {
  ProFormDependency,
  ProFormFieldSet,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-form';
import { useRequest } from 'umi';
import {
  getSessionStorageInfo,
  getLocalStorageInfo,
} from '@/utils/localStorageInfo';
import { queryCurrent } from '../service';
import { queryProvince, queryCity, eidtCustomerService } from '../service';
import AvatarView from './AvatarView';

import styles from './BaseView.less';

const validatorPhone = (
  rule: any,
  value: string[],
  callback: (message?: string) => void,
) => {
  if (!value[0]) {
    callback('Please input your area code!');
  }
  if (!value[1]) {
    callback('Please input your phone number!');
  }
  callback();
};

const BaseView: React.FC = () => {
  // const [customerInfo, setCustomerInfo] = useState({});
  const [avatarUrl, setAvatarUrl] = useState<string>();
  // 获取表单
  const [editFormRef] = Form.useForm();

  const setUploadAvatarUrl = (
    avatarUrl: React.SetStateAction<string | undefined>,
  ) => {
    setAvatarUrl(avatarUrl);
  };

  // 查询用户信息
  useEffect(() => {
    const userInformation = getLocalStorageInfo('userInfo');
    console.log(userInformation);
    const merchantInfo = getLocalStorageInfo('merchantInfo');
    console.log(merchantInfo);
  }, []);

  const currentUser = getLocalStorageInfo('customerInfo');
  console.log(currentUser);

  const handleFinish = async () => {
    // 获取输入的表单值
    editFormRef
      .validateFields()
      .then(async () => {
        // 获取表单结果
        let response = editFormRef.getFieldsValue();
        response.avatar = avatarUrl;
        editFormRef.setFieldValue('avatar', avatarUrl);
        console.log(response);
        eidtCustomerService(response).then((res) => {
          console.log('eidtCustomerService', res);
          if (res.code == '000000') {
            // setLocalStorageInfo('customerInfo', response.data)
            message.success('更新基本信息成功');
          }
        });
        // 重置输入的表单
        // editFormRef.resetFields()
        // 刷新proTable
      })
      .catch(() => {});
  };
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
            ...currentUser,
            phone: currentUser?.phone?.split('-'),
          }}
          hideRequiredMark
        >
          <ProFormText width="md" name="tenantId" label="租户号" disabled />
          <ProFormText width="md" name="customerNo" label="客户号" disabled />
          <ProFormText
            width="md"
            name="username"
            label="登录名"
            rules={[
              {
                required: true,
                message: '请输入登录名!',
              },
            ]}
          />
          <ProFormText width="xl" name="avatar" label="头像地址" disabled />
          <ProFormText
            width="md"
            name="email"
            label="邮箱"
            rules={[
              {
                required: true,
                message: '请输入您的邮箱!',
              },
            ]}
          />
          <ProFormText
            width="md"
            name="walletAddress"
            label="conflux地址"
            disabled
          />
          <ProFormText
            width="md"
            name="evmWalletAddress"
            label="evm地址"
            disabled
          />
          <ProFormTextArea
            name="profile"
            label="个人简介"
            rules={[
              {
                required: true,
                message: '请输入个人简介!',
              },
            ]}
            placeholder="个人简介"
          />
          {/* <ProFormSelect
                width="sm"
                name="country"
                label="国家/地区"
                rules={[
                  {
                    required: true,
                    message: '请输入您的国家或地区!',
                  },
                ]}
                options={[
                  {
                    label: '中国',
                    value: 'China',
                  },
                ]}
              /> */}

          <ProForm.Group title="所在省市" size={8}>
            <ProFormSelect
              rules={[
                {
                  required: true,
                  message: '请输入您的所在省!',
                },
              ]}
              width="sm"
              fieldProps={{
                labelInValue: true,
              }}
              name="province"
              className={styles.item}
              request={async () => {
                return queryProvince().then(({ data }) => {
                  return data.map((item) => {
                    return {
                      label: item.name,
                      value: item.id,
                    };
                  });
                });
              }}
            />
            <ProFormDependency name={['province']}>
              {({ province }) => {
                return (
                  <ProFormSelect
                    params={{
                      key: province?.value,
                    }}
                    name="city"
                    width="sm"
                    rules={[
                      {
                        required: true,
                        message: '请输入您的所在城市!',
                      },
                    ]}
                    disabled={!province}
                    className={styles.item}
                    request={async () => {
                      if (!province?.key) {
                        return [];
                      }
                      return queryCity(province.key || '').then(({ data }) => {
                        return data.map((item) => {
                          return {
                            label: item.name,
                            value: item.id,
                          };
                        });
                      });
                    }}
                  />
                );
              }}
            </ProFormDependency>
          </ProForm.Group>
          <ProFormText
            width="md"
            name="address"
            label="街道地址"
            rules={[
              {
                required: true,
                message: '请输入您的街道地址!',
              },
            ]}
          />
          <ProFormText
            width="md"
            name="phone"
            label="联系电话"
            rules={[
              {
                required: true,
                message: '请输入联系电话!',
              },
            ]}
          />
        </ProForm>
      </div>
      <div className={styles.right}>
        <AvatarView
          avatar={
            currentUser?.avatar == null
              ? 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png'
              : currentUser?.avatar
          }
          setUploadAvatarUrl={setUploadAvatarUrl}
        />
      </div>
    </div>
  );
};

export default BaseView;
