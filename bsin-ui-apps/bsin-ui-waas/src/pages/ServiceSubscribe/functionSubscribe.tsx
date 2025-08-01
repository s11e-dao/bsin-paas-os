import React, { useState, useRef, useEffect } from 'react';
import type { ProFormInstance } from '@ant-design/pro-form';
import { EditableProTable } from '@ant-design/pro-table';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
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

const waitTime = (time: number = 100) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(true);
        }, time);
    });
};

export default ({ serviceAppRecord, setCurrentContent }) => {

    useEffect(() => {
        // 查询合约模板协议
        let params = {
          current: '1',
          pageSize: '99',
        };
        console.log(serviceAppRecord)
        
      }, []);

    return (
        <>
            <Card title="功能订阅" extra={<Button type='primary' onClick={() => {
                setCurrentContent("subscribeList");
              }} >返回</Button>}
            >
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
                    submitter={{
                        render: (props) => {
                            if (props.step === 0) {
                                return (
                                    <Button type="primary" onClick={() => props.onSubmit?.()}>
                                        去第二步 {'>'}
                                    </Button>
                                );
                            }

                            if (props.step === 1) {
                                return [
                                    <Button key="pre" onClick={() => props.onPre?.()}>
                                        返回第一步
                                    </Button>,
                                    <Button
                                        type="primary"
                                        key="goToTree"
                                        onClick={() => props.onSubmit?.()}
                                    >
                                        去第三步 {'>'}
                                    </Button>,
                                ];
                            }

                            return [
                                <Button key="gotoTwo" onClick={() => props.onPre?.()}>
                                    {'<'} 返回第二步
                                </Button>,
                                <Button
                                    type="primary"
                                    key="goToTree"
                                    onClick={() => props.onSubmit?.()}
                                >
                                    提交 √
                                </Button>,
                            ];
                        },
                    }}
                >
                    <StepsForm.StepForm<{
                        name: string;
                    }>
                        name="base"
                        title="创建实验"
                        onFinish={async ({ name }) => {
                            console.log(name);
                            await waitTime(2000);
                            return true;
                        }}
                    >
                        <ProFormText
                            name="name"
                            label="实验名称"
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
                        title="设置参数"
                    >
                        <ProFormCheckbox.Group
                            name="checkbox"
                            label="迁移类型"
                            width="lg"
                            options={['结构迁移', '全量迁移', '增量迁移', '全量校验']}
                        />
                        <ProForm.Group>
                            <ProFormText name="dbname" label="业务 DB 用户名" />
                            <ProFormCheckbox.Group
                                name="checkbox"
                                label="迁移类型"
                                options={['完整 LOB', '不同步 LOB', '受限制 LOB']}
                            />
                        </ProForm.Group>
                    </StepsForm.StepForm>
                    <StepsForm.StepForm name="time" title="发布实验">
                        <ProFormCheckbox.Group
                            name="checkbox"
                            label="部署单元"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                            options={['部署单元1', '部署单元2', '部署单元3']}
                        />
                        <ProFormSelect
                            label="部署分组策略"
                            name="remark"
                            rules={[
                                {
                                    required: true,
                                },
                            ]}
                            initialValue="1"
                            options={[
                                {
                                    value: '1',
                                    label: '策略一',
                                },
                                { value: '2', label: '策略二' },
                            ]}
                        />
                        <ProFormSelect
                            label="Pod 调度策略"
                            name="remark2"
                            initialValue="2"
                            options={[
                                {
                                    value: '1',
                                    label: '策略一',
                                },
                                { value: '2', label: '策略二' },
                            ]}
                        />
                    </StepsForm.StepForm>
                </StepsForm>
            </Card>
        </>
    );
};
