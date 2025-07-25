import {
  Card,
  Button,
  Table,
  Divider,
  Modal,
  Tag,
  Badge,
  Form,
  Input,
  Select,
  message,
  Popconfirm,
  Tooltip,
  InputNumber,
  Drawer,
} from 'antd';
import {
  PlusOutlined,
  QuestionCircleOutlined,
  SettingOutlined,
  createFromIconfontCN
} from '@ant-design/icons';
import React, { useState, useEffect } from 'react';
import columnsData from './data';
import TableTitle from '@/components/TableTitle';
import IconModel from './icon/icon';
import { getMenuList, addMenu, delMenu, editMenu, getAppFunctionList } from './service';
import { useModel, useLocation } from 'umi';

const MenuManagement = () => {

  const { TextArea } = Input;
  const { Option } = Select;
  const { appId } = useModel('@@qiankunStateFromMaster');
  if(!appId){
    // 根据路径appCode获取appId
    const location = useLocation()
    let appCode = location.pathname.split('/')[1]
    console.log(appCode)
  }
  // 配置提示信息
  const menuSortText = (
    <>
      <div>主菜单：大菜单</div>
      <div>菜单：大菜单内的子菜单</div>
      <div>按钮：配置按钮</div>
    </>
  );

  // 添加模态框 --修改为抽屉模式
  const [isFromModal, setIsFromModal] = useState(false);
  // 
  // 获取表单
  const [formRef] = Form.useForm();
  // 保存请求的数据 用于渲染table
  const [isMenuList, setIsMenuList] = useState([]);
  const [appFunctionList, setAppFunctionList] = useState([]);
  // 点击添加保存每行的数据
  const [menuRecordInfo, setMenuRecordInfo] = useState({});
  // 存储select value的值用于后面input的显示 菜单路径和按钮权限的转换
  const [selectValue, setSelectValue] = useState(0);
  // 存储当前是什么操作
  const [isOption, setIsOption] = useState('');
  // 触发选择图标弹窗
  const [isModalVisible, setIsModalVisible] = useState(false);

  // 页面开始请求接口
  useEffect(() => { 
    doGetMenuList();
  }, []);

  const doGetMenuList = async () => {
    let { data } = await getMenuList({ appId });
    setIsMenuList(data[0]);
  };

  // 定义操作行
  const columnsActionRender: any = {
    action: (text: any, record: any, index: number) => (
      <div>
        <a
          onClick={() => {
            handleAdd(record);
          }}
        >
          添加
        </a>
        <Divider type="vertical" />
        <a
          onClick={() => {
            handleEdit(record);
          }}
        >
          编辑
        </a>
        <Divider type="vertical" />
        <Popconfirm
          title="是否删除此条数据？"
          onConfirm={() => {
            delOk(record);
          }}
          onCancel={() => {
            message.warning(`取消删除！`);
          }}
          okText="是"
          cancelText="否"
        >
          <a>删除</a>
        </Popconfirm>
      </div>
    ),
  };

  // 定义标签行
  const columnsTagsRender: any = {
    type: (text: any, record: any, index: number) => (
      <div>
        {record.type === 0 ? (
          <Tag color="purple" >主菜单</Tag>
        ) : record.type === 1 ? (
          <Tag color="blue" >菜单</Tag>
        ) : record.type === 2 ? (
          <Tag color="cyan" >按钮</Tag>
        ) : (
          ''
        )}
      </div>
    ),
  };

  // 定义状态行
  const columnsStatusRender: any = {
    status: (text: any, record: any, index: number) => (
      <div>
        {record.status === 0 ? (
          <Badge status="processing" text="启用" />
        ) : record.status === 1 ? (
          <Badge status="error" text="禁用" />
        ) : (
          ''
        )}
      </div>
    ),
  };

  // 新增
  const increased = () => {
    // increased标识
    setIsOption('increased');
    setIsFromModal(true);
    // 查询应用下的功能
    getAppFunctionList({appId}).then((res)=>{
      setAppFunctionList(res?.data)
    })

  };

  // add
  const handleAdd = (record: object) => {
    // 保存每行数据
    setMenuRecordInfo(record);
    // add标识
    setIsOption('add');
    setIsFromModal(true);
    // 查询应用下的功能
    getAppFunctionList({appId}).then((res)=>{
      setAppFunctionList(res?.data)
    })
  };

  // edit
  const handleEdit = (record: object) => {
    // 保存每行数据
    setMenuRecordInfo(record);
    // 数据回显
    formRef.setFieldsValue(record);
    // edit标识
    setIsOption('edit');
    setIsFromModal(true);
    getAppFunctionList({appId}).then((res)=>{
      setAppFunctionList(res?.data)
    })
  };

  // 删除
  const delOk = async (record: object) => {
    const { menuId } = record;
    let res = await delMenu({ menuId });
    res ? message.success('删除数据成功') : message.error(`删除数据失败`);
    // 重新请求数据页面渲染
    const { data } = await getMenuList({ appId });
    setIsMenuList(data[0]);
  };

  // 确认add
  const formOk = () => {
    // 取到appId
    const { appId } = isMenuList;
    // 表单验证
    formRef
      .validateFields()
      .then(async () => {
        // 拿到表单的值
        // 必传但是不显示的字段 应用ID 父级ID
        let response = formRef.getFieldsValue();
        if (isOption === 'add') {
          let { menuId } = menuRecordInfo;
          let params = { ...response, appId, parentId: menuId };
          let res = await addMenu(params);
          res ? message.success('添加数据成功') : message.error(`添加数据失败`);
        } else if (isOption === 'increased') {
          let { menuId } = isMenuList;
          console.log(appId);
          let params = { ...response, appId, parentId: menuId };
          let res = await addMenu(params);
          res ? message.success('新增数据成功') : message.error(`新增数据失败`);
        } else {
          let { menuId, parentId } = menuRecordInfo;
          let params = { ...response, appId, parentId, menuId };
          let res = await editMenu(params);
          res ? message.success('修改数据成功') : message.error(`修改数据失败`);
        }
        // 重置表单数据
        formRef.resetFields();
        // 重新请求数据页面渲染
        const { data } = await getMenuList({ appId });
        setIsMenuList(data[0]);
        setIsFromModal(false);
      })
      .catch(() => {});
  };

  // 取消确认
  const formCancel = () => {
    setIsFromModal(false);
    // 重置表单数据
    formRef.resetFields();
  };

  // 菜单图标弹窗
 const showModal = () => {
   setIsModalVisible(true);
 };

  const handleOk = (choosedIcon: string) => {
    const updateformRef = { ...formRef, icon: choosedIcon };
    formRef.setFieldsValue(updateformRef);
   setIsModalVisible(false);
 };

 const handleCancel = () => {
   setIsModalVisible(false);
 };
 
  // 渲染操作行
  columnsData.forEach((item: any) => {
    if (item.dataIndex === 'action')
      item.render = columnsActionRender[item.dataIndex];
    if (item.dataIndex === 'type')
      item.render = columnsTagsRender[item.dataIndex];
    if (item.dataIndex === 'status')
      item.render = columnsStatusRender[item.dataIndex];
  });

  return (
    <>
      <Card
        title={<TableTitle title="菜单管理" />}
        bordered={false}
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={increased}>
            新增
          </Button>
        }
      >
        <Table
          bordered
          columns={columnsData}
          dataSource={isMenuList?.children}
          rowKey={(record) => record.menuCode}
          scroll={{ x: 900 }}
          pagination={false}
        />
      </Card>
      {/* 添加模态框 */}
      <Drawer
        title="添加"
        placement="right"
        onClose={formCancel}
        open={isFromModal}
        width="600"
      >
        <Form
          form={formRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{ Type: 1, status: 0, appFunctionId: "0" }}
        >
          <Form.Item
            label="菜单编码"
            name="menuCode"
            rules={[{ required: true, message: '请输入菜单编码!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="菜单名称"
            name="menuName"
            rules={[{ required: true, message: '请输入菜单名称!' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item label="所属功能" name="appFunctionId">
            {/* todo */}
            <Select>
                <Option value="0">请选择所属功能！</Option>
                .
                {appFunctionList.map((appFunction) => {
                  return (
                    <Option value={appFunction?.appFunctionId}>
                      {appFunction?.functionName}
                    </Option>
                  );
                })}
            </Select>
          </Form.Item>
          <Form.Item label="菜单图标" name="icon">
            <Input
            onClick={showModal}
              addonAfter={ <SettingOutlined onClick={showModal}/>}
            />
          </Form.Item>
          <Tooltip placement="bottom" title={menuSortText}>
            <QuestionCircleOutlined
              style={{
                float: 'right',
                color: '#999',
                marginLeft: '-20px',
                position: 'relative',
                left: '-24px',
                top: '8px',
              }}
            />
          </Tooltip>
          <Form.Item label="菜单类型" name="type">
            {/* 判断是不是新增还是添加还是编辑 */}
            {isOption === 'increased' ? (
              <Select>
                <Option value={0}>主菜单</Option>
                <Option value={1}>菜单</Option>
                <Option value={2}>按钮</Option>
              </Select>
            ) : isOption === 'add' ? (
              <Select
                onSelect={(value) => {
                  setSelectValue(value);
                }}
              >
                <Option value={1}>菜单</Option>
                <Option value={2}>按钮</Option>
              </Select>
            ) : (
              <Select>
                <Option value={0}>主菜单</Option>
                <Option value={1}>菜单</Option>
                <Option value={2}>按钮</Option>
              </Select>
            )}
          </Form.Item>
          <Tooltip placement="bottom" title={menuSortText}>
            <QuestionCircleOutlined
              style={{
                float: 'right',
                color: '#999',
                marginLeft: '-20px',
                position: 'relative',
                left: '-24px',
                top: '8px',
              }}
            />
          </Tooltip>
          <Form.Item label="菜单序列" name="sort">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          {selectValue !== 2 ? (
            <Form.Item
              label="组件路径"
              name="path"
              rules={[{ required: true, message: '请输入组件路径!' }]}
            >
              <Input />
            </Form.Item>
          ) : (
            <Form.Item
              label="权限标识"
              name="permission"
              rules={[{ required: true, message: '请输入权限名称!' }]}
            >
              <Input />
            </Form.Item>
          )}
          <Form.Item label="状态" name="status">
            <Select>
              <Option value={0}>启用</Option>
              <Option value={1}>禁用</Option>
            </Select>
          </Form.Item>
          <Form.Item label="描述" name="remark">
            <TextArea rows={4} />
          </Form.Item>
        </Form>
        <div style={{ textAlign: 'right' }}>
          <Button onClick={formCancel} style={{ marginRight: '10px' }}>
            取消
          </Button>
          <Button type="primary" onClick={formOk}>
            确定
          </Button>
        </div>
      </Drawer>
      <IconModel
        isModalVisible={isModalVisible}
        handleOk={handleOk}
        handleCancel={handleCancel}
      />
    </>
  );
};
export default MenuManagement;
