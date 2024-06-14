import React from 'react';
import ProTable from '@ant-design/pro-table';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import { PlusOutlined, SyncOutlined } from '@ant-design/icons';
import {
  Descriptions,
  Modal,
  Tag,
  Button,
  Divider,
  Popconfirm,
  message,
  Form,
  Input,
  Select,
  DatePicker,
} from 'antd';
import productColumnsData, { productAppColumnsData } from './data';
import type { DictColumnsItem, DictItemColumnsItem } from './data.d';
import {
  getPageList,
  addPageList,
  editPageList,
  deletePageList,
  getProductAppPageList,
  addDictItemPageList,
  editDictItemPageList,
  deleteProductApp,
  getAppList
} from './service';

const { Option } = Select;

const DictManagement = () => {
  // 控制查看模态框
  const [isCheckModalVisible, setIsCheckModalVisible] = React.useState(false);
  // 控制新增、编辑模态框
  const [isAddModalVisible, setIsAddModalVisible] = React.useState(false);
  // 控制新增、编辑模态框title
  const [addModalTitle, setAddModalTitle] = React.useState('');
  // 控制查看的字典的 DictType
  const [showDictItem, setShowDictItem] = React.useState<DictColumnsItem>();
  // 控制查看中新增、编辑模态框
  const [isCheckAddModalVisible, setIsCheckAddModalVisible] =
    React.useState(false);
  // 控制查看中新增、编辑模态框title
  const [checkAddModalTitle, setCheckAddModalTitle] = React.useState('');
  // 查看字典项编辑对象
  const [checkItem, setCheckItem] = React.useState<
    DictItemColumnsItem | DictColumnsItem
  >();

  const [appList, setAppList] = React.useState([]);

  const [baseAppFlag, setBaseAppFlag] = React.useState(false);

  // 查看模态框取消
  const handleCheckModalCancel = () => {
    console.log('handleCheckModalCancel');
    setIsCheckModalVisible(false);
  };

  // 点击新增
  const DictAdd = () => {
    console.log('DictAdd');
    setAddModalTitle('新增');
    setIsAddModalVisible(true);
  };

  // 点击编辑
  const handleEditModel = (record: DictColumnsItem) => {
    console.log('handleEditModel', record);
    dictForm.setFieldsValue(record);
    setCheckItem(record);
    setAddModalTitle('编辑');
    setIsAddModalVisible(true);
  };

  // 新增、编辑模态框取消按钮
  const handleAddModalCancel = () => {
    console.log('handleAddModalCancel');
    dictForm.resetFields();
    setIsAddModalVisible(false);
  };

  // 新增、编辑模态框确定按钮
  const handleAddModalOk = () => {
    console.log('handleAddModalOk');
    // 表单验证
    dictForm.validateFields().then(async () => {
      let response = dictForm.getFieldsValue();
      console.log(response);

      if (addModalTitle === '新增') {
        let res = await addPageList(response);
        console.log(res);
        res ? message.success('新增成功') : message.error('添加失败！');
      } else {
        let res = await editPageList({
          ...response,
          id: checkItem?.id,
        });
        res ? message.success('修改成功') : message.error('修改失败！');
      }
      setIsAddModalVisible(false);
      dictForm.resetFields();
      // 刷新
      DictRef?.current?.reload();
    });
  };

  // 点击查看
  const handleCheckBtn = (record: DictColumnsItem) => {
    console.log('handleCheckModel');
    setShowDictItem(record);
    setIsCheckModalVisible(true);

  };

  // 确定删除
  const confirmDel = async (id?: string | number) => {
    console.log('confirmDel');
    let res = await deletePageList({ id });
    console.log(res);
    // 刷新
    DictRef?.current?.reload();
  };

  // 定义操作单元格
  const columnsOptionRender = (text: any, record: DictColumnsItem) => {
    return [
      <div key={record.dictType}>
        <a onClick={() => handleCheckBtn(record)}>产品应用</a>
        <Divider type="vertical" />
        <a onClick={() => handleEditModel(record)}>编辑</a>
        <Divider type="vertical" />
        <Popconfirm
          title="是否删除此条数据?"
          onConfirm={() => confirmDel(record.id)}
          onCancel={() => {
            message.warning(`取消删除`);
          }}
          okText="是"
          cancelText="否"
        >
          <a>删除</a>
        </Popconfirm>
      </div>,
    ];
  };

  // 列渲染样式赋值
  productColumnsData.forEach((item) => {
    item.valueType === 'option'
      ? (item.render = columnsOptionRender)
      : undefined;
  });

  // 查看字典新增
  const DictItemAdd = () => {
    getAppList({}).then((res) => {
      setAppList(res?.data);
    });

    console.log('DictItemAdd');
    checkForm.setFieldsValue({ productId: showDictItem?.productId });
    setCheckAddModalTitle('新增');
    setIsCheckAddModalVisible(true);
  };

  // 查看字典项编辑
  const handleCheckEditModel = (record: DictItemColumnsItem) => {
    console.log('handleCheckEditModel', record);
    setCheckAddModalTitle('编辑');
    checkForm.setFieldsValue(record);
    setCheckItem(record);
    setIsCheckAddModalVisible(true);
  };

  //查看字典新增、编辑模态框确定
  const handleCheckAddModalOk = () => {
    console.log('handleCheckAddModalOk');
    // 表单验证
    checkForm.validateFields().then(async () => {
      let response = checkForm.getFieldsValue();
      console.log(response);

      if (checkAddModalTitle === '新增') {
        let bizRoleType = "99";
        if (response.bizRoleType) {
          bizRoleType = response.bizRoleType
        }
        let res = await addDictItemPageList({
          ...response,
          productId: showDictItem?.productId,
          bizRoleType: bizRoleType
        });
        console.log(res);
        res ? message.success('新增成功') : message.error('添加失败！');
      } else {
        console.log(checkItem);

        let res = await editDictItemPageList({
          ...response,
          id: checkItem?.id,
        });
        res ? message.success('修改成功') : message.error('修改失败！');
      }
      checkForm.resetFields();
      setIsCheckAddModalVisible(false);
      // 刷新
      DictItemRef?.current?.reload();
    });
  };

  //查看字典新增、编辑模态框取消
  const handleCheckAddModalCancel = () => {
    console.log('handleCheckAddModalCancel');
    checkForm.resetFields();
    setIsCheckAddModalVisible(false);
  };

  // 查看字典项删除
  const CheckConfirmDel = async (productApp: any) => {
    console.log('CheckConfirmDel');
    console.log(productApp);
    let res = await deleteProductApp({ productId: showDictItem?.productId, appId: productApp.appId });
    console.log(res);
    // 刷新
    DictItemRef?.current?.reload();
  };

  // 定义字典项查看操作单元格
  const CheckColumnsOptionRender = (text: any, record: DictItemColumnsItem) => {
    return [
      <div key={record.id}>
        <Popconfirm
          title="是否删除此条数据?"
          onConfirm={() => CheckConfirmDel(record)}
          onCancel={() => {
            message.warning(`取消删除`);
          }}
          okText="是"
          cancelText="否"
        >
          <a>删除</a>
        </Popconfirm>
      </div>,
    ];
  };

  // 定义字典项查看操作单元格
  const columnsBaseFlagRender = (text: any, record: DictItemColumnsItem) => {
    return [
      <div>
        {record?.baseFlag === 0 ? (
          <Tag color="blue" >普通应用</Tag>
        ) : record?.baseFlag === 1 ? (
          <Tag icon={<SyncOutlined spin />} color="purple" >基础应用</Tag>
        ) : (
          ''
        )}
      </div>
    ];
  };

  // 定义字典项查看操作单元格
  const columnsBizRoleTypeRender = (text: any, record: DictItemColumnsItem) => {
    return [
      <div>
        {record.bizRoleType === "1" ? (
          <Tag color="volcano" >运营平台</Tag>
        ) : record.bizRoleType === "2" ? (
          <Tag color="green" >租户平台</Tag>
        ) : record.bizRoleType === "3" ? (
          <Tag color="success" >商户</Tag>
        ) : record.bizRoleType === "4" ? (
          <Tag color="geekblue" >客户</Tag>
        ) : record.bizRoleType === "99" ? (
          <Tag color="cyan" >无</Tag>
        ) : (
          ''
        )}
      </div>
    ];
  };

  // 查看字典项表格操作行渲染
  productAppColumnsData.forEach((item) => {
    console.log(item)
    if (item.valueType === 'option')
      item.render = CheckColumnsOptionRender
    if (item.dataIndex === 'baseFlag')
      item.render = columnsBaseFlagRender;
    if (item.dataIndex === 'bizRoleType')
      item.render = columnsBizRoleTypeRender;
  });

  

  // 字典表 Table action 的引用，便于自定义触发
  const DictRef = React.useRef<ActionType>();
  // 字典项表 Table action 的引用，便于自定义触发
  const DictItemRef = React.useRef<ActionType>();
  // 字典新增、编辑表单
  const [dictForm] = Form.useForm();
  // 查看新增、编辑表单
  const [checkForm] = Form.useForm();

  const changeBaseAppFlag = (value: any) => {
    setBaseAppFlag(false)
    if (value == 1) {
      setBaseAppFlag(true)
    }
  };

  return (
    <>
      {/* 列表 */}
      <ProTable<DictColumnsItem>
        scroll={{ x: 900 }}
        bordered
        headerTitle="产品管理"
        columns={productColumnsData}
        actionRef={DictRef}
        // 请求的数据
        request={async (params) => {
          let res = await getPageList({ ...params, pageNum: params.current });
          console.log(res);
          return {
            data: res.data,
            total: res.pagination.totalSize,
          };
        }}
        // 本地储存表格列的显示参数
        columnsState={{
          persistenceKey: 'DictTable',
          persistenceType: 'localStorage',
        }}
        // 每行表格的key
        rowKey="dictType"
        // 搜索表单布局配置
        search={{
          labelWidth: 'auto',
        }}
        toolBarRender={() => [
          <Button
            onClick={DictAdd}
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
        dateFormatter="string"
      />
      {/* 字典编辑、新增模态框 */}
      <Modal
        title={addModalTitle}
        open={isAddModalVisible}
        onOk={handleAddModalOk}
        onCancel={handleAddModalCancel}
      >
        <Form
          name="basic"
          form={dictForm}
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          initialValues={{ remember: true }}
          autoComplete="off"
        >
          <Form.Item
            label="产品名称"
            name="productName"
            rules={[{ required: true, message: '请输入产品名称!' }]}
          >
            <Input disabled={addModalTitle === '新增' ? false : true} />
          </Form.Item>
          <Form.Item
            label="产品编号"
            name="productCode"
            rules={[{ required: true, message: '请输入产品编号!' }]}
          >
            <Input disabled={addModalTitle === '新增' ? false : true} />
          </Form.Item>
          <Form.Item label="产品描述" name="remark">
            <Input />
          </Form.Item>
          {addModalTitle === '编辑' ? (
            <Form.Item label="创建时间" name="createTime">
              <Input disabled />
            </Form.Item>
          ) : null}
        </Form>
      </Modal>
      {/* 查看模态框 */}
      <Modal
        width={1200}
        title="产品应用管理"
        open={isCheckModalVisible}
        onCancel={handleCheckModalCancel}
        footer={[]}
      >
        <ProTable<DictItemColumnsItem>
          actionRef={DictItemRef}
          scroll={{ x: 900 }}
          columns={productAppColumnsData}
          params={{ productId: showDictItem?.productId }}
          // 请求的数据
          request={async (params) => {
            console.log(params);

            let res = await getProductAppPageList({
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
          headerTitle="产品应用列表"
          toolBarRender={() => [
            <Button
              onClick={DictItemAdd}
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

      {/* 查看详情中的编辑、新增模态框 */}
      <Modal
        title={checkAddModalTitle}
        open={isCheckAddModalVisible}
        onOk={handleCheckAddModalOk}
        onCancel={handleCheckAddModalCancel}
      >
        <Form
          name="basic"
          form={checkForm}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 20 }}
          initialValues={{ baseFlag: 0, appId: "0" }}
          autoComplete="off"
        >
          <Form.Item
            label="产品应用"
            name="appId"
            rules={[{ required: true, message: '请选择关联应用!' }]}
          >
            <Select style={{ width: '100%' }} allowClear>
              <Option value="0">请选择关联应用</Option>
              {appList.map((app) => {
                return (
                  <Option value={app?.appId}>
                    {app?.appName}
                  </Option>
                );
              })}
            </Select>
          </Form.Item>
          <Form.Item
            label="基础应用"
            name="baseFlag"
            rules={[{ required: true, message: '请选择是否是基础应用!' }]}
          >
            <Select style={{ width: '100%' }} allowClear onChange={changeBaseAppFlag}>
              <Option value={0}>否</Option>
              <Option value={1}>是</Option>
            </Select>
          </Form.Item>
          {/* {如果是基础应用选择基础应用对应的业务角色} */}
          {baseAppFlag ? (
            <Form.Item
              label="业务角色类型"
              name="bizRoleType"
              rules={[{ required: true, message: '请选择是否是基础应用!' }]}
            >
              <Select style={{ width: '100%' }} allowClear defaultValue='0'>
                <Option value="0">请选择业务角色类型</Option>
                <Option value="1">运营平台</Option>
                <Option value="2">租户平台</Option>
                <Option value="3">商户</Option>
                <Option value="4">客户</Option>
              </Select>
            </Form.Item>) : (
            null
          )}
        </Form>
      </Modal>
    </>
  );
};

export default DictManagement;
