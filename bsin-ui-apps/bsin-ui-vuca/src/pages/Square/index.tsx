import { HeartOutlined, CommentOutlined, LinkOutlined, StarOutlined } from '@ant-design/icons';
import { Button, Card, Input, Form, List, Row, Select, Tag, Avatar, Space } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import type { FC } from 'react';
import React from 'react';
import { history } from 'umi'
import ArticleListContent from './components/ArticleListContent';
import StandardFormRow from './components/StandardFormRow';
import TagSelect from './components/TagSelect';
import type { ListItemDataType } from './data.d';
import styles from './style.less';

import './index.less';

const { Meta } = Card;
const { Option } = Select;
const FormItem = Form.Item;

const pageSize = 5;

const Articles: FC = () => {
  const [form] = Form.useForm();

  const handleFormSubmit = (value: string) => {
    // eslint-disable-next-line no-console
    console.log(value);
  };

  const goChat = (value: string) => {
    history.push("/flow")
  };


  const actionComp = () => {
    const action: any = [];
    const actionEle = (
      <div style={{ display: "flex", justifyContent: "space-around" }}>
        <StarOutlined key="setting" />
        <CommentOutlined onClick={() => {
          goChat("1")
        }} key="ellipsis" />
      </div>)

    action.push(actionEle)
    return action
  }

  return (
    <>
      <PageContainer
        title="广场·您的先进生产力"
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
      >
        <Card
          style={{ margin: "10px" }}
          bordered={false}>
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
        <Card style={{ margin: "10px" }}>
          <Space
            className={styles.content}
            style={{ display: "flex" }}
            size={"middle"}>
            <Card
              style={{
                width: 300,
              }}
              cover={
                <img
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
              }
              actions={actionComp()}
            >
              <Meta
                title="公众号助手"
                description="技能"
              />
            </Card>
            <Card
              style={{
                width: 300,
              }}
              cover={
                <img
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
              }
              actions={actionComp()}
            >
              <Meta
                avatar={<Avatar src="https://api.dicebear.com/7.x/miniavs/svg?seed=8" />}
                title="小红书助手"
                description="This is the description"
              />
            </Card>

            <Card
              style={{
                width: 300,
              }}
              cover={
                <img
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
              }
              actions={actionComp()}
            >
              <Meta
                avatar={<Avatar src="https://api.dicebear.com/7.x/miniavs/svg?seed=8" />}
                title="jiujiu管家"
                description="This is the description"
              />
            </Card>

            <Card
              style={{
                width: 300,
              }}
              cover={
                <img
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
              }
              actions={actionComp()}
            >
              <Meta
                avatar={<Avatar src="https://api.dicebear.com/7.x/miniavs/svg?seed=8" />}
                title="社区财神"
                description="This is the description"
              />
            </Card>

            <Card
              style={{
                width: 300,
              }}
              cover={
                <img
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
              }
              actions={actionComp()}
            >
              <Meta
                avatar={<Avatar src="https://api.dicebear.com/7.x/miniavs/svg?seed=8" />}
                title="社区守护神"
                description="This is the description"
              />
            </Card>

            <Card
              style={{
                width: 300,
              }}
              cover={
                <img
                  alt="example"
                  src="https://gw.alipayobjects.com/zos/rmsportal/JiqGstEfoWAOHiTxclqi.png"
                />
              }
              actions={actionComp()}
            >
              <Meta
                avatar={<Avatar src="https://api.dicebear.com/7.x/miniavs/svg?seed=8" />}
                title="火源兽"
                description="This is the description"
              />
            </Card>
          </Space>
        </Card>
      </PageContainer>
    </>
  );
};

export default Articles;
