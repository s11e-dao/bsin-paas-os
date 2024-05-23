import { LikeOutlined, LoadingOutlined, MessageOutlined, StarOutlined } from '@ant-design/icons';
import { Button, Card, Input, Form, List, Row, Select, Tag } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import type { FC } from 'react';
import React from 'react';
import ArticleListContent from './components/ArticleListContent';
import StandardFormRow from './components/StandardFormRow';
import TagSelect from './components/TagSelect';
import type { ListItemDataType } from './data';
import styles from './style.less';

import './index.less';

const { Option } = Select;
const FormItem = Form.Item;

const pageSize = 5;

const Articles: FC = () => {
  const [form] = Form.useForm();

  const tabList = [
    {
      key: 'articles',
      tab: '进行中',
    },
    {
      key: 'projects',
      tab: '已结束',
    },
  ];

  const data = {
    list: [{
      id: `fake-list-${Math.random().toString(36).slice(2, 6)}`,
      owner: "1",
      title: "AIGC",
      // avatar: avatars[i % 8],
      cover: "",
      status: ['active', 'exception', 'normal'][1 % 3] as
        | 'normal'
        | 'exception'
        | 'active'
        | 'success',
      percent: Math.ceil(Math.random() * 50) + 50,
      logo: "",
      href: 'https://ant.design',
      updatedAt: new Date(new Date().getTime() - 1000 * 60 * 60 * 2 * 1).getTime(),
      createdAt: new Date(new Date().getTime() - 1000 * 60 * 60 * 2 * 1).getTime(),
      subDescription: "desc[i % 5]",
      description:
        '在中台产品的研发过程中，会出现不同的设计规范和实现方式，但其中往往存在很多类似的页面和组件，这些类似的组件会被抽离成一套标准规范。',
      activeUser: Math.ceil(Math.random() * 100000) + 100000,
      newUser: Math.ceil(Math.random() * 1000) + 1000,
      star: Math.ceil(Math.random() * 100) + 100,
      like: Math.ceil(Math.random() * 100) + 100,
      message: Math.ceil(Math.random() * 10) + 10,
      content:
        '段落示意：蚂蚁金服设计平台 ant.design，用最小的工作量，无缝接入蚂蚁金服生态，提供跨越设计与开发的体验解决方案。蚂蚁金服设计平台 ant.design，用最小的工作量，无缝接入蚂蚁金服生态，提供跨越设计与开发的体验解决方案。',
      members: [
        {
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/ZiESqWwCXBRQoaPONSJe.png',
          name: '曲丽丽',
          id: 'member1',
        },
        {
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/tBOxZPlITHqwlGjsJWaF.png',
          name: '王昭君',
          id: 'member2',
        },
        {
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/sBxjgqiuHMGRkIjqlQCd.png',
          name: '董娜娜',
          id: 'member3',
        },
      ],
    },
    {
      id: `fake-list-${Math.random().toString(36).slice(2, 6)}`,
      owner: "1",
      title: "AIGC",
      // avatar: avatars[i % 8],
      cover: "",
      status: ['active', 'exception', 'normal'][1 % 3] as
        | 'normal'
        | 'exception'
        | 'active'
        | 'success',
      percent: Math.ceil(Math.random() * 50) + 50,
      logo: "",
      href: 'https://ant.design',
      updatedAt: new Date(new Date().getTime() - 1000 * 60 * 60 * 2 * 1).getTime(),
      createdAt: new Date(new Date().getTime() - 1000 * 60 * 60 * 2 * 1).getTime(),
      subDescription: "desc[i % 5]",
      description:
        '在中台产品的研发过程中，会出现不同的设计规范和实现方式，但其中往往存在很多类似的页面和组件，这些类似的组件会被抽离成一套标准规范。',
      activeUser: Math.ceil(Math.random() * 100000) + 100000,
      newUser: Math.ceil(Math.random() * 1000) + 1000,
      star: Math.ceil(Math.random() * 100) + 100,
      like: Math.ceil(Math.random() * 100) + 100,
      message: Math.ceil(Math.random() * 10) + 10,
      content:
        '段落示意：蚂蚁金服设计平台 ant.design，用最小的工作量，无缝接入蚂蚁金服生态，提供跨越设计与开发的体验解决方案。蚂蚁金服设计平台 ant.design，用最小的工作量，无缝接入蚂蚁金服生态，提供跨越设计与开发的体验解决方案。',
      members: [
        {
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/ZiESqWwCXBRQoaPONSJe.png',
          name: '曲丽丽',
          id: 'member1',
        },
        {
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/tBOxZPlITHqwlGjsJWaF.png',
          name: '王昭君',
          id: 'member2',
        },
        {
          avatar: 'https://gw.alipayobjects.com/zos/rmsportal/sBxjgqiuHMGRkIjqlQCd.png',
          name: '董娜娜',
          id: 'member3',
        },
      ],
    }]
  }

  const list = data?.list || [];

  const setOwner = () => {
    form.setFieldsValue({
      owner: ['wzj'],
    });
  };

  const owners = [
    {
      id: 'wzj',
      name: '我自己',
    },
    {
      id: 'wjh',
      name: '吴家豪',
    },
    {
      id: 'zxx',
      name: '周星星',
    },
    {
      id: 'zly',
      name: '赵丽颖',
    },
    {
      id: 'ym',
      name: '姚明',
    },
  ];

  const IconText: React.FC<{
    type: string;
    text: React.ReactNode;
  }> = ({ type, text }) => {
    switch (type) {
      case 'star-o':
        return (
          <span>
            <StarOutlined style={{ marginRight: 8 }} />
            {text}
          </span>
        );
      case 'like-o':
        return (
          <span>
            <LikeOutlined style={{ marginRight: 8 }} />
            {text}
          </span>
        );
      case 'message':
        return (
          <span>
            <Button style={{height: "23px", lineHeight: "23px", fontSize: "12px",padding: "0px 11px"}} type="primary" ghost>领取</Button>
          </span>
        );
      default:
        return null;
    }
  };

  const formItemLayout = {
    wrapperCol: {
      xs: { span: 24 },
      sm: { span: 24 },
      md: { span: 12 },
    },
  };

  const loadMoreDom = list.length > 0 && (
    <div style={{ textAlign: 'center', marginTop: 16 }}>
      <Button style={{ paddingLeft: 48, paddingRight: 48 }}>

      </Button>
    </div>
  );

  const handleFormSubmit = (value: string) => {
    // eslint-disable-next-line no-console
    console.log(value);
  };

  return (
    <>
      <PageContainer
        title="广场·新文明叙事"
        content={
          <div style={{ textAlign: 'center' }}>
            <Input.Search
              placeholder="请输入"
              enterButton="搜索"
              size="large"
              onSearch={handleFormSubmit}
              style={{ maxWidth: 522, width: '100%' }}
            />
          </div>
        }
        tabList={tabList}
      >

        <Card bordered={false}>
          <Form
            layout="inline"
            form={form}
            initialValues={{
              owner: ['wjh', 'zxx'],
            }}
          >
            <StandardFormRow title="所属类目" block style={{ paddingBottom: 11 }}>
              <FormItem name="category">
                <TagSelect expandable>
                  <TagSelect.Option value="cat1">个体的价值</TagSelect.Option>
                  <TagSelect.Option value="cat2">环保生活</TagSelect.Option>
                  <TagSelect.Option value="cat3">社区营造</TagSelect.Option>
                  <TagSelect.Option value="cat4">社会构建</TagSelect.Option>
                  <TagSelect.Option value="cat5">数字未来</TagSelect.Option>
                  <TagSelect.Option value="cat6">公益事业</TagSelect.Option>
                  <TagSelect.Option value="cat7">创造力</TagSelect.Option>
                  <TagSelect.Option value="cat8">批判与进步</TagSelect.Option>
                  <TagSelect.Option value="cat9">未来文明</TagSelect.Option>
                  <TagSelect.Option value="cat10">DAO组织</TagSelect.Option>
                  <TagSelect.Option value="cat11">社区治理</TagSelect.Option>
                  <TagSelect.Option value="cat12">协作连接</TagSelect.Option>
                </TagSelect>
              </FormItem>
            </StandardFormRow>

          </Form>
        </Card>
        <Card
          style={{ marginTop: 24 }}
          bordered={false}
          bodyStyle={{ padding: '8px 32px 32px 32px' }}
        >
          <List<ListItemDataType>
            size="large"
            rowKey="id"
            itemLayout="vertical"
            loadMore={loadMoreDom}
            dataSource={list}
            renderItem={(item) => (
              <List.Item
                key={item.id}
                actions={[
                  <IconText key="star" type="star-o" text={item.star} />,
                  <IconText key="like" type="like-o" text={item.like} />,
                  <IconText key="message" type="message" text={item.message} />,
                ]}
                extra={<div className={styles.listItemExtra} />}
              >
                <List.Item.Meta
                  title={
                    <a className={styles.listItemMetaTitle} href={item.href}>
                      {item.title}
                    </a>
                  }
                  description={
                    <span>
                      <Tag>Ant Design</Tag>
                      <Tag>设计语言</Tag>
                      <Tag>蚂蚁金服</Tag>
                    </span>
                  }
                />
                <ArticleListContent data={item} />
              </List.Item>
            )}
          />
        </Card>

      </PageContainer>


    </>
  );
};

export default Articles;
