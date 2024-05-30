import React, { useState, useEffect } from 'react';
import {
    Card,
    Button,
    Descriptions,
} from 'antd';
import type { UploadProps } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';


import styles from './index.css';

export default ({ contactProtocolRouter, currentRecord }) => {

    const [docmentContent, setDocmentContent] = useState('')
    const ctx = "# Hello, *world*!"
    useEffect(() => {
        setDocmentContent(ctx)
    }, [])
    return (

        <PageContainer>
            <Card style={{ marginBottom: 16 }}>
                <Button
                    type="primary"
                    onClick={() => {
                        contactProtocolRouter('list');
                    }}
                    className={styles.btn}
                >
                    返回
                </Button>
                <Descriptions title="帮助中心"></Descriptions>
                
            </Card>
        </PageContainer>

    );
};
