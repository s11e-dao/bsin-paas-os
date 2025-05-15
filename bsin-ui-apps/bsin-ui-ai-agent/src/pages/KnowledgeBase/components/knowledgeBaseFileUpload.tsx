import React, { useState } from 'react'
import { Button, message, Steps, theme, Upload, Table, Card, Radio, Input, Divider, Space } from 'antd';
import type { UploadProps, UploadFile } from 'antd/es/upload';
import { BackwardOutlined, InboxOutlined, SettingOutlined, EditOutlined } from '@ant-design/icons'

const { Dragger } = Upload;

const steps = [
    {
        title: '选择文件',
    },
    {
        title: '文本分段与清洗',
    },
    {
        title: '确认上传',
    },
];

const props: UploadProps = {
    name: 'file',
    multiple: true,
    action: 'https://660d2bd96ddfa2943b33731c.mockapi.io/api/upload',
    onChange(info) {
        const { status } = info.file;
        if (status !== 'uploading') {
            console.log(info.file, info.fileList);
        }
        if (status === 'done') {
            message.success(`${info.file.name} file uploaded successfully.`);
        } else if (status === 'error') {
            message.error(`${info.file.name} file upload failed.`);
        }
    },
    onDrop(e) {
        console.log('Dropped files', e.dataTransfer.files);
    },
};

const KnowledgeBaseFileUpload: React.FC = ({
    routeChange,
    knowledgeBaseFileRecord,
}) => {

    const { token } = theme.useToken();
    const [current, setCurrent] = useState(0);
    const [fileList, setFileList] = useState<UploadFile[]>([]);
    const [segmentMethod, setSegmentMethod] = useState<string>('auto');
    const [segmentSize, setSegmentSize] = useState<number>(500);
    const [cleaningOptions, setCleaningOptions] = useState({
        removeHeaders: true,
        removeFooters: true,
        removeEmptyLines: true,
        trimWhitespace: true
    });

    const next = () => {
        setCurrent(current + 1);
    };

    const prev = () => {
        setCurrent(current - 1);
    };

    const steps = [
        {
            title: '选择文件',
            content: 'upload',
        },
        {
            title: '文本分段与清洗',
            content: 'process',
        },
        {
            title: '确认上传',
            content: 'confirm',
        },
    ];

    const uploadProps: UploadProps = {
        name: 'file',
        multiple: true,
        action: '/api/knowledge-base/upload',
        onChange(info) {
            const { status } = info.file;
            if (status !== 'uploading') {
                console.log(info.file, info.fileList);
            }
            if (status === 'done') {
                message.success(`${info.file.name} 文件上传成功`);
            } else if (status === 'error') {
                message.error(`${info.file.name} 文件上传失败`);
            }
            setFileList(info.fileList);
        },
        onDrop(e) {
            console.log('Dropped files', e.dataTransfer.files);
        },
    };

    const columns = [
        {
            title: '文件名',
            dataIndex: 'name',
            key: 'name',
            render: (text: string, record: any) => record.name,
        },
        {
            title: '大小',
            dataIndex: 'size',
            key: 'size',
            render: (text: number, record: any) => {
                const size = record.size / 1024;
                return size < 1024
                    ? `${size.toFixed(2)} KB`
                    : `${(size / 1024).toFixed(2)} MB`;
            },
        },
        {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            render: (text: string, record: any) => record.type || '未知',
        },
        {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            render: (text: string) => {
                if (text === 'done') return '上传成功';
                if (text === 'uploading') return '上传中';
                if (text === 'error') return '上传失败';
                return '等待上传';
            },
        },
    ];

    const handleSegmentMethodChange = (e: any) => {
        setSegmentMethod(e.target.value);
    };

    const handleSegmentSizeChange = (e: any) => {
        setSegmentSize(e.target.value);
    };

    const handleCleaningOptionChange = (option: string, checked: boolean) => {
        setCleaningOptions({
            ...cleaningOptions,
            [option]: checked
        });
    };

    const renderStepContent = () => {
        switch (current) {
            case 0:
                return (
                    <div style={{ padding: '20px 0' }}>
                        <Dragger {...uploadProps} fileList={fileList}>
                            <p className="ant-upload-drag-icon">
                                <InboxOutlined />
                            </p>
                            <p className="ant-upload-text">点击或拖拽文件到此区域上传</p>
                            <p className="ant-upload-hint">
                                支持单个或批量上传。支持 PDF、Word、TXT 等文档格式。
                            </p>
                        </Dragger>

                        {fileList.length > 0 && (
                            <div style={{ marginTop: '20px' }}>
                                <h3>已选择的文件</h3>
                                <Table
                                    columns={columns}
                                    dataSource={fileList.map(file => ({
                                        ...file,
                                        key: file.uid,
                                    }))}
                                    pagination={false}
                                    size="small"
                                />
                            </div>
                        )}
                    </div>
                );
            case 1:
                return (
                    <div style={{ padding: '20px 0' }}>
                        <Card title={<><SettingOutlined /> 文本分段设置</>} style={{ marginBottom: '20px' }}>
                            <div style={{ marginBottom: '16px' }}>
                                <div style={{ marginBottom: '8px' }}>分段方法：</div>
                                <Radio.Group onChange={handleSegmentMethodChange} value={segmentMethod}>
                                    <Space direction="vertical">
                                        <Radio value="auto">自动分段（按段落）</Radio>
                                        <Radio value="fixed">固定长度分段</Radio>
                                        <Radio value="semantic">语义分段（按内容主题）</Radio>
                                    </Space>
                                </Radio.Group>
                            </div>

                            {segmentMethod === 'fixed' && (
                                <div style={{ marginBottom: '16px' }}>
                                    <div style={{ marginBottom: '8px' }}>分段大小（字符数）：</div>
                                    <Input
                                        type="number"
                                        value={segmentSize}
                                        onChange={handleSegmentSizeChange}
                                        style={{ width: '200px' }}
                                    />
                                </div>
                            )}
                        </Card>
                    </div>
                );
            case 2:
                return (
                    <div style={{ padding: '20px 0' }}>
                        <div style={{ textAlign: 'center' }}>
                            <p style={{ fontSize: '16px', marginBottom: '16px' }}>
                                确认上述设置无误后，点击"确认上传"按钮开始处理文件。
                            </p>
                            <p style={{ color: '#888' }}>
                                文件处理可能需要一些时间，请耐心等待。
                            </p>
                        </div>
                    </div>
                );
            default:
                return null;
        }
    };

    return (
        <>
            <Button
                key="button"
                icon={<BackwardOutlined />}
                type="primary"
                onClick={() => {
                    routeChange(knowledgeBaseFileRecord, 'knowledgeBaseFileList')
                }}
            >
                返回
            </Button>
            <div style={{ margin: '16px 0' }}>
                <Steps current={current} items={steps.map(item => ({ key: item.title, title: item.title }))} />
            </div>
            <div style={{ marginTop: 24, minHeight: 300, padding: '20px', border: '1px dashed #e9e9e9', borderRadius: '2px' }}>
                {renderStepContent()}
            </div>
            <div style={{ marginTop: 24, display: 'flex', justifyContent: 'flex-end' }}>
                {current > 0 && (
                    <Button style={{ marginRight: 8 }} onClick={() => prev()}>
                        上一步
                    </Button>
                )}
                {current < steps.length - 1 && (
                    <Button type="primary" onClick={() => next()}>
                        下一步
                    </Button>
                )}
                {current === steps.length - 1 && (
                    <Button type="primary" onClick={() => message.success('文件已开始处理!')}>
                        确认上传
                    </Button>
                )}
            </div>
        </>
    )
}

export default KnowledgeBaseFileUpload