import React, { useState } from 'react';
import { Col, Row, Tree, Input } from 'antd';
import type { GetProps, TreeDataNode } from 'antd';
import { CarryOutOutlined, CheckOutlined, FormOutlined } from '@ant-design/icons';

const { DirectoryTree } = Tree;

const { Search } = Input;

const treeData: TreeDataNode[] = [
    {
        title: '身份类数据',
        key: '0-0',
    },
    {
        title: '消费类数据',
        key: '0-1',

    },
    {
        title: '行为类数据',
        key: '0-2', 
    },
    {
        title: '设备类数据',
        key: '0-3',
    },
    {
        title: '节点类数据',
        key: '0-4',
    },
    {
        title: '渠道类数据',
        key: '0-5',
    },
];

const DataCategoryTree: React.FC = () => {
    const [showLine, setShowLine] = useState<boolean>(true);
    const [showIcon, setShowIcon] = useState<boolean>(false);
    const [showLeafIcon, setShowLeafIcon] = useState<React.ReactNode>(true);

    const onSelect = (selectedKeys: React.Key[], info: any) => {
        console.log('selected', selectedKeys, info);
    };

    const handleLeafIconChange = (value: 'true' | 'false' | 'custom') => {
        if (value === 'custom') {
            return setShowLeafIcon(<CarryOutOutlined />);
        }

        if (value === 'true') {
            return setShowLeafIcon(true);
        }

        return setShowLeafIcon(false);
    };

    return (

        <Row style={{ backgroundColor: '#fff', padding: 24, lineHeight: 0 }}>
            <Col span={24}>
                <Search style={{ marginBottom: 24 }} placeholder="搜索数据分类" />
            </Col>
            <Col span={24}>
                <Tree
                    showLine={showLine ? { showLeafIcon } : false}
                    showIcon={showIcon}
                    defaultExpandedKeys={['0-0-0']}
                    onSelect={onSelect}
                    treeData={treeData}
                />
            </Col>
        </Row>
    );
};

export default DataCategoryTree;