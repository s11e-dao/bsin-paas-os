import {
  DownOutlined,
  PlusOutlined,
  UserOutlined,
  AreaChartOutlined,
  NodeExpandOutlined,
  DotChartOutlined,
  UserSwitchOutlined,
  SubnodeOutlined,
  ShareAltOutlined,
  ScheduleOutlined,
  PrinterOutlined,
  IdcardOutlined,
  DollarOutlined,
  BgColorsOutlined,
} from '@ant-design/icons';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import TableTitle from '@/components/TableTitle';
import { useRequest } from '@umijs/max';
import {
  message,
  Button,
  Popconfirm,
  Card,
  Col,
  Dropdown,
  Input,
  List,
  Divider,
  Form,
  Modal,
  Select,
  Radio,
  Row,
  Avatar
} from 'antd';
import dayjs from 'dayjs';
import type { FC } from 'react';
import React, { useState } from 'react';
import type { BasicListItemDataType } from './data.d';

import {
  getAppList, delAppInfo, addAppList, editAppList,
  getAppFunctionPageList,
  addAppFunction, editAppFunction, delAppFunction
} from './service';

import styles from './index.css';
const RadioButton = Radio.Button;
const RadioGroup = Radio.Group;
const { Search } = Input;

import functionColumnsData, { FunctionColumnsItem } from './functionData';

const ListContent = ({
  data: { appId, createTime, appCode, url },
}: {
  data: BasicListItemDataType;
}) => {
  return (
    <div style={{ width: "650px" }}>
      <div className={styles.listContentItem}>
        <span>应用ID</span>
        <p>{appId}</p>
      </div>
      <div className={styles.listContentItem}>
        <span>应用编号</span>
        <p>{appCode}</p>
      </div>
      <div className={styles.listContentItem}>
        <span>应用路径</span>
        <p>{url}</p>
      </div>
    </div>
  );
};

export const BasicList: FC = () => {

  const { TextArea } = Input;
  const { Option } = Select;

  // Table action 的引用，便于自定义触发 用于更改数据之后的表单刷新
  const actionRef = React.useRef<ActionType>();
  
  const {
    data: listData,
    loading,
    mutate,
  } = useRequest(() => {
    return getAppList({
      pageNum: 1,
      pageSize: 99
    });
  });

  const { run: postRun } = useRequest(
    (method, params) => {
      if (method === 'delete') {
        return getAppList(params);
      }
      return getAppList(params);
    },
    {
      manual: true,
      onSuccess: (result) => {
        mutate(result);
      },
    },
  );

  // 控制当前操作
  const [currentOption, setCurrentOption] = React.useState<object>({});
  // 存储当前行数据，用于编辑操作
  const [currentRecord, setCurrentRecord] = React.useState({});
  // 控制表单模态框
  const [appFormModal, setAppFormModal] = React.useState(false);
  
  // 控制查看中新增、编辑模态框title
  const [appModalTitle, setAppModalTitle] = React.useState('');

  // 获取编辑表单信息
  const [formRef] = Form.useForm();

  // 确定提交
  const appFormModalOk = () => {
    const { option } = currentOption;
    const { appId } = currentRecord;
    formRef
      .validateFields()
      .then(async () => {
        let response = formRef.getFieldsValue();
        if (option === 'add') {
          let res = await addAppList(response);
          res ? message.success('添加成功') : message.error('添加失败！');
        } else {
          let res = await editAppList({ ...response, appId });
          res ? message.success('编辑成功') : message.error('编辑失败！');
        }
        // 刷新表格
        postRun("edit", {
          pageNum: 1,
          pageSize: 99
        });
        // 重置表单Form
        formRef.resetFields();
        setAppFormModal(false);
      })
      .catch(() => { });
  };

  // edit模态框控制
  const handleEditAppFormModal = (record: object) => {
    setCurrentRecord(record);
    setCurrentOption({ option: 'edit' });
    setAppModalTitle("编辑应用")
    setAppFormModal(true);
    // 进行数据的回显
    formRef.setFieldsValue(record);
  };

  // 取消关闭
  const appFormModalCancel = () => {
    // 重置表单Form
    formRef.resetFields();
    setAppFormModal(false);
  };

  // add模态框控制
  const handleAddAppFormModal = () => {
    setCurrentOption({ option: 'add' });
    setAppModalTitle("添加应用")
    setAppFormModal(true);
  };

  // 表头赋值
  const functionColumns: ProColumns<AppColumnsItem>[] = functionColumnsData;

  const [functionListMoalVisible, setFunctionListMoalVisible] = React.useState(false);
  // 控制查看中新增、编辑模态框title
  const [functionAddModalTitle, setFunctionAddModalTitle] = React.useState('');
  // 控制查看中新增、编辑模态框
  const [functionAddModalVisible, setFunctionAddModalVisible] = React.useState(false);
  // 功能项表 Table action 的引用，便于自定义触发
  const functionRef = React.useRef<ActionType>();
  // 功能新增、编辑表单
  const [functionForm] = Form.useForm();

  const handleEditAppFunctionFormModal = (record: object) => {
    setCurrentRecord(record);
    setCurrentOption({ option: 'edit' });
    setFunctionAddModalTitle("编辑功能")
    setFunctionAddModalVisible(true);
    // 进行数据的回显
    functionForm.setFieldsValue(record);
  };

   // 点击删除
   const confirmDel = async (appId: string) => {
    let res = await delAppInfo({ appId });
    res ? message.success('删除成功') : message.error('删除失败！');
    // 刷新表格
    actionRef.current?.reload();
  };

  // add模态框控制
  const functionAdd = () => {
    setCurrentOption({ option: 'add' });
    setFunctionAddModalTitle("添加功能")
    // 重置表单Form
    functionForm.resetFields();
    setFunctionAddModalVisible(true);
  };

  const handleFunctionListModal = (record: object) => {
    setCurrentRecord(record);
    setFunctionListMoalVisible(true);
    // 根据应用ID查询功能列表

  };

  const handleFunctionModalCancel = () => {
    setFunctionListMoalVisible(false);
  };

  const handlefunctionAddModalOk = () => {
    // 调用功能添加
    const { option } = setCurrentOption;
    const { appId } = currentRecord;
    functionForm
      .validateFields()
      .then(async () => {
        let response = functionForm.getFieldsValue();
        if (option === 'add') {
          let res = await addAppFunction({ ...response, appId });
          res ? message.success('添加成功') : message.error('添加失败！');
        } else {
          let res = await editAppFunction({ ...response, appId });
          res ? message.success('编辑成功') : message.error('编辑失败！');
        }
        // 刷新表格
        functionRef.current?.reload();
        // 重置表单Form
        functionForm.resetFields();
        setFunctionAddModalVisible(false);
      })
      .catch(() => {});
  }

  const handlefunctionAddModalCancel = () => {
    setFunctionAddModalVisible(false)
  }

  // 点击删除
  const confirmDelFunction = async (appFunctionId: string) => {
    let res = await delAppFunction({ appFunctionId });
    res ? message.success('删除成功') : message.error('删除失败！');
    // 刷新表格
    functionForm.current?.reload();
  };

  const appList = listData?.data || [];
  const paginationProps = {
    showSizeChanger: true,
    showQuickJumper: true,
    pageSize: 5,
    total: appList.length,
  };
  
  const deleteItem = (appId: string) => {
    console.log(appId)
    delAppInfo({appId:appId}).then(res=>{
      if(res?.code == 0){
        message.success('删除成功')
        postRun("delete", {
          pageNum: 1,
          pageSize: 99
        });
      }else{
        message.error('删除失败！')
      }
    })
  };

  const editAndDelete = (key: string | number, currentItem: BasicListItemDataType) => {
    console.log(key)
    if (key === 'appFunction') handleFunctionListModal(currentItem);
    else if (key === 'delete') {
      Modal.confirm({
        title: '删除任务',
        content: '确定删除该任务吗？',
        okText: '确认',
        cancelText: '取消',
        onOk: () => deleteItem(currentItem.appId),
      });
    }
  };
  const extraContent = (
    <div>
      <Button
        key="button"
        icon={<PlusOutlined />}
        type="primary"
        onClick={() => {
          handleAddAppFormModal();
        }}
        style={{ marginRight: "16px" }}
      >
        新建
      </Button>
      <RadioGroup defaultValue="all">
        <RadioButton value="all">全部</RadioButton>
        <RadioButton value="waiting">已上线</RadioButton>
      </RadioGroup>
      <Search className={styles.extraContentSearch} placeholder="请输入" onSearch={() => ({})} />
    </div>
  );
  const MoreBtn: React.FC<{
    item: BasicListItemDataType;
  }> = ({ item }) => (
    <Dropdown
      menu={{
        onClick: ({ key }) => editAndDelete(key, item),
        items: [
          {
            key: 'appFunction',
            label: '应用功能',
          },
          {
            key: 'delete',
            label: '删除',
          },
        ],
      }}
    >
      <a>
        更多 <DownOutlined />
      </a>
    </Dropdown>
  );
 
  const handleSubmit = (values: BasicListItemDataType) => {
    const method = values?.appId ? 'update' : 'add';
    postRun(method, values);
  };

  // const [layoutMenudata, setLayoutMenudata] = useState([]);
  const getAppLogo = (appLogo: any) => {
    if(appLogo){
      return <Avatar shape="square" src={appLogo} />
    }
    return <IdcardOutlined className={styles.appIcon} />;
  }

  const getMenuIon = (icon: any) => {
    let menuIon = <UserOutlined className={styles.appIcon} />;
    // console.log(icon == null);
    if (icon == 'UserOutlined') {
      menuIon = <UserOutlined className={styles.appIcon} />;
    } else if (icon == 'AreaChartOutlined') {
      menuIon = <AreaChartOutlined className={styles.appIcon} />;
    } else if (icon == 'NodeExpandOutlined') {
      menuIon = <NodeExpandOutlined className={styles.appIcon} />;
    } else if (icon == 'DotChartOutlined') {
      menuIon = <DotChartOutlined className={styles.appIcon} />;
    } else if (icon == 'UserSwitchOutlined') {
      menuIon = <UserSwitchOutlined className={styles.appIcon} />;
    } else if (icon == 'ScheduleOutlined') {
      menuIon = <ScheduleOutlined className={styles.appIcon} />;
    } else if (icon == 'ShareAltOutlined') {
      menuIon = <ShareAltOutlined className={styles.appIcon} />;
    } else if (icon == 'PrinterOutlined') {
      menuIon = <PrinterOutlined className={styles.appIcon} />;
    } else if (icon == 'IdcardOutlined') {
      menuIon = <IdcardOutlined className={styles.appIcon} />;
    } else if (icon == 'DollarOutlined') {
      menuIon = <DollarOutlined className={styles.appIcon} />;
    } else if (icon == 'BgColorsOutlined') {
      menuIon = <BgColorsOutlined className={styles.appIcon} />;
    }
    // else {
    //   menuIon = <span className={` ${icon} iconfont `} style={{ marginRight: '0.5rem' }}></span>;
    // }
    return menuIon;
  };

  // 操作列渲染
  const functionOptionRender = (text: any, record: any, index: number) => [
    <div key={record.appId}>
      <a onClick={() => handleEditAppFunctionFormModal(record)}>编辑</a>
      <Divider type="vertical" />
      <Popconfirm
        title="是否确认删除？"
        okText="是"
        cancelText="否"
        onConfirm={() => confirmDelFunction(record.appFunctionId)}
        onCancel={() => {
          message.warning('取消删除！');
        }}
      >
        <a>删除</a>
      </Popconfirm>
    </div>,
  ];

  // 渲染操作按钮
  functionColumns.forEach((item: any) => {
    item.dataIndex === 'option' ? (item.render = functionOptionRender) : undefined;
  });

  return (
    <div>
      <div className={styles.standardList}>
        <Card
          className={styles.listCard}
          bordered={false}
          title="应用列表"
          style={{
            marginTop: 0,
          }}
          bodyStyle={{
            padding: '0 32px 40px 32px',
          }}
          extra={extraContent}
        >
          <List
            size="large"
            rowKey="id"
            loading={loading}
            pagination={paginationProps}
            dataSource={appList}
            renderItem={(item) => (
              <List.Item
                actions={[
                  <a
                    key="edit"
                    onClick={(e) => {
                      e.preventDefault();
                      handleEditAppFormModal(item);
                    }}
                  >
                    编辑
                  </a>,
                  <MoreBtn key="more" item={item} />,
                ]}
              >
                <List.Item.Meta
                  avatar={getAppLogo(item?.logo)}
                  title={<a href={item.appCode}>{item.appName}</a>}
                  description={item.remark}
                />
                <ListContent data={item} />
              </List.Item>
            )}
          />
        </Card>
      </div>

      {/* 添加编辑 */}
      <Modal
        title={appModalTitle}
        open={appFormModal}
        onOk={() => appFormModalOk()}
        onCancel={() => appFormModalCancel()}
        centered
        width={700}
      >
        <Form
          name="basic"
          form={formRef}
          labelCol={{ span: 24 }}
          wrapperCol={{ span: 24 }}
          initialValues={{ status: 1, appLanguage: 1 }}
        >
          <Row gutter={20}>
            <Col span={8}>
              <Form.Item
                label="应用编码"
                name="appCode"
                rules={[{ required: true, message: '请填写应用编码' }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="应用名称(中文)"
                name="appName"
                rules={[{ required: true, message: '请填写应用名称' }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label="应用图标" name="logo">
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={20}>
            <Col span={8}>
              <Form.Item
                label="应用访问地址"
                name="url"
                rules={[
                  {
                    required: true,
                    message: '请填写应用访问地址',
                  },
                  {
                    type: 'url',
                    message: '应用访问地址格式不正确',
                  },
                ]}
              >
                <Input placeholder="格式: http://+域名+端口" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label="应用语言" name="appLanguage">
                <Select>
                  <Option value={0}>Vue</Option>
                  <Option value={1}>React</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item label="应用状态" name="status">
                <Radio.Group>
                  <Radio value={1}>启用</Radio>
                  <Radio value={0}>未启用</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item label="应用描述" name="remark">
            <TextArea
              showCount
              maxLength={30}
              style={{ height: 60 }}
              placeholder="请输入应用描述信息..."
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* 添加功能 */}
      <Modal
        bodyStyle={{ margin: 0, padding: 0 }}
        width={1200}
        title="功能管理"
        open={functionListMoalVisible}
        onCancel={handleFunctionModalCancel}
        footer={[]}
      >
        <ProTable<FunctionColumnsItem>
          actionRef={functionRef}
          scroll={{ x: 900 }}
          columns={functionColumns}
          params={{ appId: currentRecord?.appId }}
          // 请求的数据
          request={async (params) => {
            console.log(params);

            let res = await getAppFunctionPageList({
              ...params,
              pageNum: params.current,
            });
            console.log(res);
            return {
              data: res.data,
              total: res.pagination.totalSize,
            };
          }}
          rowKey="id"
          search={false}
          dateFormatter="string"
          headerTitle="功能项列表"
          toolBarRender={() => [
            <Button
              onClick={functionAdd}
              key="button"
              icon={<PlusOutlined />}
              type="primary"
            >
              新增
            </Button>,
          ]}
          // 分页相关配置
          pagination={{
            // 初始页面数据条数
            pageSize: 10,
          }}
        />
      </Modal>

      {/* 功能的的编辑、新增模态框 */}
      <Modal
        title={functionAddModalTitle}
        open={functionAddModalVisible}
        onOk={handlefunctionAddModalOk}
        onCancel={handlefunctionAddModalCancel}
      >
        <Form
          name="basic"
          form={functionForm}
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          initialValues={{ remember: true }}
          autoComplete="off"
        >
          <Form.Item
            label="功能名称"
            name="functionName"
            rules={[{ required: true, message: '请输入功能名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="功能编号"
            name="functionCode"
            rules={[{ required: true, message: '请输入功能编号!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="基础功能"
            name="baseFlag"
            rules={[{ required: true, message: '请选择是否是基础功能!' }]}
          >
            <Select style={{ width: '100%' }} allowClear>
              <Option value="0">否</Option>
              <Option value="1">是</Option>
            </Select>
          </Form.Item>
          <Form.Item label="功能描述" name="remark">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};
export default BasicList;
