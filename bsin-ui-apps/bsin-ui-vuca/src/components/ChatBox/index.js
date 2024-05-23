import React from 'react';
import { Card } from 'antd';
import { ProChat } from '@ant-design/pro-chat';
import { useTheme } from 'antd-style';

import { SendOutlined } from '@ant-design/icons';
import { Input, Space } from 'antd';

import logo from '../../assets/setting.png';
import sendOutlined from '../../assets/SendOutlined.png';

import { chats } from './mocks/threebody';

const { Search } = Input;
const suffix = (
    <img src={sendOutlined} width={30} height={30} />
);
const onSearch = (value) => console.log(value);

export default () => {
    const chatTheme = useTheme();

    return (

        <Card title="Chat" extra={<img src={logo} width={30} height={30} />} style={{ width: 340 }}
        >
            <div style={{ background: chatTheme.colorBgLayout }}>
                <ProChat
                    style={{ minHeight: "72vh" }}
                    showTitle
                    userMeta={{
                        avatar: 'https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg',
                        title: 'Ant Design',
                    }}
                    assistantMeta={{ avatar: '🛸', title: '三体世界', backgroundColor: '#67dedd' }}
                    initialChats={chats.chats}
                />
            </div>
        </Card>

    );
};
