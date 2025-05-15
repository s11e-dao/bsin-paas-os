import React, { useState } from 'react'
import { Button, message, Steps, theme } from 'antd';
import { BackwardOutlined } from '@ant-design/icons'


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

const KnowledgeBaseFileUpload: React.FC = ({
    routeChange,
    knowledgeBaseFileRecord,
}) => {

    const { token } = theme.useToken();
    const [current, setCurrent] = useState(0);

    const next = () => {
        setCurrent(current + 1);
    };

    const prev = () => {
        setCurrent(current - 1);
    };

    const items = steps.map((item) => ({ key: item.title, title: item.title }));

    const contentStyle: React.CSSProperties = {
        lineHeight: '260px',
        textAlign: 'center',
        color: token.colorTextTertiary,
        backgroundColor: token.colorFillAlter,
        borderRadius: token.borderRadiusLG,
        border: `1px dashed ${token.colorBorder}`,
        marginTop: 16,
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
                <Steps current={current} items={items} />
            </div>
            <div style={contentStyle}>
                内容区
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
                    <Button type="primary" onClick={() => message.success('处理完成!')}>
                        完成
                    </Button>
                )}
            </div>

        </>
    )
}

export default KnowledgeBaseFileUpload