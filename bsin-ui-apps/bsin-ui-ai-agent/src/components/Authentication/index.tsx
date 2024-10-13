import React, { useState, useRef, FC, useEffect } from 'react';
import type { ProFormInstance } from '@ant-design/pro-form';
import { EditableProTable } from '@ant-design/pro-table';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import { QRCodeSVG } from 'qrcode.react';

import ProForm, {
    StepsForm,
    ProFormDigit,
    ProFormText,
    ProFormSelect,
    ProFormCheckbox
} from '@ant-design/pro-form';
import { Modal, message, Button, Row, Col, Card, Result } from 'antd';
import {
    getLocalStorageInfo,
    setLocalStorageInfo,
} from '@/utils/localStorageInfo';

import {
    merchantAuth
} from './service';

const waitTime = (time: number = 100) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(true);
        }, time);
    });
};

export default () => {

    const [currentStep, setCurrentStep] = useState(0)

    //需要转为二维码的h5链接
    const [url, setUrl] = useState('')
    //随机数id
    const [randomId, setRandomId] = useState<string>('')
    //生成的二维码图片链接
    const [qrcodeUrl, setQrcodeUrl] = useState('')

    //生成随机数id
    const uuid = () => {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
            const r = (Math.random() * 16) | 0
            const v = c === 'x' ? r : (r & 0x3) | 0x8
            return v.toString(16)
        })
    }

    useEffect(() => {
        setRandomId(uuid())
        setUrl('https://example.com')
        console.log(getLocalStorageInfo('merchantInfo')?.authenticationStatus)
        console.log(getLocalStorageInfo('merchantInfo')?.status == 2)
        if (getLocalStorageInfo('merchantInfo')?.status == 2) {
            setCurrentStep(3)
        }
    }, [])

    useEffect(() => {
        //获取canvas类型的二维码
        const canvasImg = document.getElementById(randomId) as HTMLCanvasElement
        //将canvas对象转换为图片的data url
        setQrcodeUrl(canvasImg?.toDataURL('image/png'))
    }, [randomId])

    const doMerchantAuth = (props) => {
        // 重置输入的表单
        merchantAuth({}).then((res) => {
            message.success('提交成功');
            props.onSubmit?.()
            setCurrentStep(3)
            // 更新商户缓存信息
            let merchantInfo = getLocalStorageInfo('merchantInfo');
            merchantInfo.status = 2
            setLocalStorageInfo("merchantInfo",merchantInfo);
            console.log(res)
        })
    };

    return (
        <>
            <Card title="主体认证">
            <StepsForm<{
                    name: string;
                }>
                    onFinish={async (values) => {
                        console.log(values);
                        await waitTime(1000);
                        message.success('提交成功');
                    }}
                    formProps={{
                        validateMessages: {
                            required: '此项为必填项',
                        },
                    }}
                    current={currentStep}
                    submitter={{
                        render: (props) => {
                            if (props.step === 0) {
                                return (
                                    <Button type="primary" onClick={() => props.onSubmit?.()}>
                                        下一步 {'>'}
                                    </Button>
                                );
                            }

                            if (props.step === 1) {
                                return [
                                    <Button key="pre" onClick={() => props.onPre?.()}>
                                        上一步
                                    </Button>,
                                    <Button
                                        type="primary"
                                        key="goToTree"
                                        onClick={() => props.onSubmit?.()}
                                    >
                                        下一步 {'>'}
                                    </Button>,
                                ];
                            }

                            if (props.step === 2) {
                                return [
                                    <Button key="gotoTwo" onClick={() => props.onPre?.()}>
                                        {'<'} 上一步
                                    </Button>,
                                    <Button
                                        type="primary"
                                        key="goToTree"
                                        onClick={() => doMerchantAuth(props)}
                                    >
                                        确认已支付 √
                                    </Button>,
                                ];
                            }

                            return [
                            ];
                        },
                    }}
                >
                    <StepsForm.StepForm<{
                        name: string;
                    }>
                        name="base"
                        title="主体信息"
                        onFinish={async ({ name }) => {
                            console.log(name);
                            setCurrentStep(1)
                            await waitTime(2000);
                            return true;
                        }}
                    >
                        <ProFormCheckbox.Group
                            name="checkbox"
                            label="账号类型"
                            width="lg"
                            options={['企业', '个体户', '超级个人']}
                        />

                        <ProFormText
                            name="name"
                            label="企业名称"
                            width="md"
                            tooltip="最长为 24 位，用于标定的唯一 id"
                            placeholder="请输入名称"
                            rules={[{ required: true }]}
                        />

                        <ProFormText
                            name="org"
                            label="组织机构代码"
                            width="md"
                            tooltip="最长为 24 位，用于标定的唯一 id"
                            placeholder="请输入名称"
                            rules={[{ required: true }]}
                        />

                    </StepsForm.StepForm>
                    <StepsForm.StepForm<{
                        checkbox: string;
                    }>
                        name="checkbox"
                        title="联系人"
                        onFinish={async ({ name }) => {
                            console.log(name);
                            setCurrentStep(2)
                            await waitTime(2000);
                            return true;
                        }}
                    >

                        <ProFormText
                            name="org"
                            label="法人姓名"
                            width="md"
                            tooltip="最长为 24 位，用于标定的唯一 id"
                            placeholder="请输入名称"
                            rules={[{ required: true }]}
                        />
                        <ProFormText
                            name="type"
                            label="法人证件类型"
                            width="md"
                            tooltip="最长为 24 位，用于标定的唯一 id"
                            placeholder="请输入名称"
                            rules={[{ required: true }]}
                        />
                        <ProFormSelect
                            label="法人证件类型"
                            name="remark2"
                            initialValue="1"
                            options={[
                                { value: '1', label: '身份证' },
                                { value: '2', label: '军官证' },
                            ]}
                        />
                        <ProFormText
                            name="id"
                            label="法人证件号"
                            width="md"
                            tooltip="最长为 24 位，用于标定的唯一 id"
                            placeholder="请输入名称"
                            rules={[{ required: true }]}
                        />

                    </StepsForm.StepForm>
                    <StepsForm.StepForm name="time" title="支付确认">
                        <div style={{ position: 'absolute', opacity: '0', zIndex: '-100' }}>
                            {url && randomId && (
                                <QRCodeSVG value={url} />
                            )}
                        </div>
                        <img style={{ margin: '10px' }} src={qrcodeUrl} />
                    </StepsForm.StepForm>
                    <StepsForm.StepForm name="finish" title="成功">
                        <Result
                            status="success"
                            title="认证信息提交成功，请等待审核结果!"
                            subTitle="s11eDao会尽快审核您的资料."
                            extra={[]}
                        />
                    </StepsForm.StepForm>
                </StepsForm>
            </Card>
        </>
    );
};
