import {
  AlipayOutlined,
  GiftOutlined,
  DingdingOutlined,
  MessageFilled,
  UsergroupAddOutlined,
  TaobaoOutlined,
} from '@ant-design/icons'
import { List } from 'antd'
import React, { Fragment } from 'react'

const BindingView: React.FC = () => {
  const getData = () => [
    {
      title: '智能体创建数量',
      description: 'VIP',
      actions: [<a key="Bind">升级权益</a>],
      avatar: <UsergroupAddOutlined className="usergroup" />,
    },
    {
      title: '微信分数可创建数量',
      description: 'VIP',
      actions: [<a key="Bind">升级权益</a>],
      avatar: <MessageFilled className="message" />,
    },
  ]

  return (
    <Fragment>
      <List
        itemLayout="horizontal"
        dataSource={getData()}
        renderItem={(item) => (
          <List.Item actions={item.actions}>
            <List.Item.Meta
              avatar={item.avatar}
              title={item.title}
              description={item.description}
            />
          </List.Item>
        )}
      />
    </Fragment>
  )
}

export default BindingView
