import React from 'react';
import { Table, Card, Col, Row, Button } from 'antd';
import { PlusOutlined, EllipsisOutlined } from '@ant-design/icons';

const columns = [
    {
        title: '数据分类',
        dataIndex: 'dataCategory',
    },
    {
        title: '数据级别',
        dataIndex: 'dataLevel',
    },
    {
        title: '数据价值',
        dataIndex: 'dataValue',
    },
    {
        title: '数据来源',
        dataIndex: 'dataSource',
    },

];

const DataLevelTable: React.FC = () => {

    const increaseTemplate = () => {
        console.log('添加');
    };
    return (
        <Card title="数据价值配置" extra={
            <Button
                onClick={() => {
                    increaseTemplate();
                }}
                key="button"
                icon={<PlusOutlined />}
                type="primary"
            >
                添加
            </Button>
        }>

            <Row gutter={16}>
                <Col span={8}>
                    <Card title="L1 - 基础数据" style={{ margin: '10px' }} extra={<EllipsisOutlined/>}>
                        <div>基础价值 (T): 10</div>
                        <div>完整性加成 (%): 5</div>
                        <div>时效性加成 (%): 0</div>
                    </Card>
                </Col>
                <Col span={8}>
                    <Card title="L2 - 高级数据"  style={{ margin: '10px' }} extra={<EllipsisOutlined/>}>
                        <div>基础价值 (T): 10</div>
                        <div>完整性加成 (%): 5</div>
                        <div>时效性加成 (%): 0</div>
                    </Card>
                </Col>
                <Col span={8}>
                    <Card title="L3 - 专业数据"  style={{ margin: '10px' }} extra={<EllipsisOutlined/>}>
                        <div>基础价值 (T): 10</div>
                        <div>完整性加成 (%): 5</div>
                        <div>时效性加成 (%): 0</div>
                    </Card>
                </Col>
            </Row>
        </Card>
    );
};

export default DataLevelTable;
