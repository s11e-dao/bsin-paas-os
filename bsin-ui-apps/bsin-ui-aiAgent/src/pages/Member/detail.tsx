import columnsData, { AppColumnsItem } from './data';
import React, { useState, useEffect } from 'react';
import { getWxmpUserTagDetail } from './service';
import { Button, Space, Tag, Divider } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useModel } from 'umi';

const UserDetail = (props: any) => {
  const { openId } = props;
  const [userTagList, setUserTagList] = useState([]);

  // 获取container对象
  useEffect(() => {
    // 查询数据
    console.log('查询详情{}', openId);
    getWxmpUserTagDetailHandle(openId);
  }, []);

  const getWxmpUserTagDetailHandle = (openId: any) => {
    getWxmpUserTagDetail({ openId }).then((res) => {
      console.log(res);
      setUserTagList(res.data);
    });
  };

  // 获取appId
  const { appId } = useModel('@@qiankunStateFromMaster');

  const color = [
    'magenta',
    'red',
    'volcano',
    'orange',
    'gold',
    'lime',
    'green',
    'cyan',
    'blue',
    'geekblue',
    'purple',
  ];

  const userTagListComponent = userTagList.map((userTag) => {
    const num = Math.floor(Math.random() * 10);
    return (
      <Space size={[8, 16]} wrap style={{ margin: '8px 0' }}>
        <Tag color={color[num]}>{userTag?.tag}</Tag>
      </Space>
    );
  });

  return (
    <div>
      <Space size="middle" style={{ display: 'flex', marginBottom: '16px' }}>
        <Button
          onClick={() => {
            props?.closeHandle();
          }}
        >
          返回列表
        </Button>
      </Space>
      <Divider orientation="left">基础标签</Divider>
      <div>{userTagListComponent}</div>
      <Divider orientation="left">属性标签</Divider>
      <div>{userTagListComponent}</div>
      <Divider orientation="left">行为标签</Divider>
      <div>{userTagListComponent}</div>
    </div>
  );
};

export default UserDetail;
