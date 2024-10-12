import React from 'react';
import { Card } from 'antd';

import { SendOutlined } from '@ant-design/icons';
import { Input, Space } from 'antd';


import logo from '../../assets/setting.png';
import sendOutlined from '../../assets/SendOutlined.png';

const { Search } = Input;
const suffix = (
    <img src={sendOutlined} width={30} height={30} />
);
const onSearch = (value) => console.log(value);

export default () => {

    return (

        <Card title="Chat" extra={<img src={logo} width={30} height={30} />} style={{ width: 280 }}
        >
            <p>正在执行</p>
            <p>...</p>

            <Input
                placeholder="input search text"
                enterButton="Search"
                size="large"
                suffix={suffix}
            />
        </Card>

    );
};
