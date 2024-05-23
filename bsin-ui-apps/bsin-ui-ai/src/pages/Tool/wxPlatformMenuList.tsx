import columnsData, { WxPlatformMenuColumnsItem } from './wxPlatformMenuData'

import React, { useState, useEffect } from 'react'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import {
  getWxPlatformList,
  getWxPlatformMenuPageList,
  delWxPlatformMenu,
  addWxPlatformMenu,
  editWxPlatformMenu,
  getWxPlatformMenuDetail,
  getWxPlatformMenuTemplate,
} from './service'
import TableTitle from '@/components/TableTitle'
import {
  Button,
  Modal,
  Table,
  Popconfirm,
  message,
  Form,
  Input,
  Divider,
  Switch,
  Badge,
  InputNumber,
  Tag,
  Select,
  Card,
  Radio,
  Tooltip,
  Drawer,
} from 'antd'
import {
  PlusOutlined,
  QuestionCircleOutlined,
  InboxOutlined,
} from '@ant-design/icons'
export default ({ currentRecord, routerChange }) => {
  const { TextArea } = Input
  const { Option } = Select

  // 保存请求的数据 用于渲染table
  const [wxPlatformMenuTemplateVo, setWxPlatformMenuTemplateVo] = useState({})

  // 配置提示信息
  const menuSortText = (
    <>
      <div>主菜单：大菜单</div>
      <div>菜单：大菜单内的子菜单</div>
      <div>按钮：配置按钮</div>
    </>
  )

  // 添加模态框 --修改为抽屉模式
  const [isAddFromModal, setIsAddFromModal] = useState(false)
  //
  // 获取表单
  const [formRef] = Form.useForm()
  // 点击添加保存每行的数据
  const [menuRecordInfo, setMenuRecordInfo] = useState({})
  // 存储select value的值用于后面input的显示 菜单路径和按钮权限的转换
  const [selectValue, setSelectValue] = useState(0)
  // 存储当前是什么操作
  const [isOption, setIsOption] = useState('')
  // 触发选择图标弹窗
  const [isModalVisible, setIsModalVisible] = useState(false)

  // 查询Copilot文件列表
  useEffect(() => {
    let { serialNo } = currentRecord
    // 查询协议菜单模版列表
    getWxPlatformMenuTemplate({ serialNo }).then((res) => {
      if (res.code == '000000') {
        setWxPlatformMenuTemplateVo(res.data)
        console.log(res.data)
      } else {
        message.error(res.message)
      }
    })
  }, [])

  // 定义操作行
  const columnsActionRender: any = {
    option: (text: any, record: any, index: number) => (
      <div>
        <a
          onClick={() => {
            handleAddMenu(record)
          }}
        >
          添加
        </a>
        <Divider type="vertical" />
        <a
          onClick={() => {
            handleEdit(record)
          }}
        >
          编辑
        </a>
        <Divider type="vertical" />
        <Popconfirm
          title="是否删除此条数据？"
          onConfirm={() => {
            confirmDel(record)
          }}
          onCancel={() => {
            message.warning(`取消删除！`)
          }}
          okText="是"
          cancelText="否"
        >
          <a>删除</a>
        </Popconfirm>
      </div>
    ),
  }

  // 定义标签行
  const columnsTagsRender: any = {
    type: (text: any, record: any, index: number) => (
      <div>
        {record.type === 'click' ? (
          <Tag color="#f50">click</Tag>
        ) : record.type === 'view' ? (
          <Tag color="#87d068">view</Tag>
        ) : record.type === 'miniprogram' ? (
          <Tag color="#2db7f5">miniprogram</Tag>
        ) : (
          ''
        )}
      </div>
    ),
  }

  // 定义状态行
  const columnsStatusRender: any = {
    status: (text: any, record: any, index: number) => (
      <div>
        {record.status === '1' ? (
          <Badge status="processing" text="启用" />
        ) : record.status === '0' ? (
          <Badge status="error" text="禁用" />
        ) : (
          ''
        )}
      </div>
    ),
  }

  // 新增父级菜单
  const increaseParentMenu = () => {
    setIsOption('increaseParentMenu')
    setIsAddFromModal(true)
  }

  // 添加子菜单
  const handleAddMenu = (record: object) => {
    // 保存每行数据
    setMenuRecordInfo(record)
    // add标识
    setIsOption('addMenu')
    setIsAddFromModal(true)
  }

  const formOk = () => {
    // 表单验证
    formRef
      .validateFields()
      .then(async () => {
        // 拿到表单的值
        // 必传但是不显示的字段 应用ID 父级ID
        let response = formRef.getFieldsValue()
        if (isOption === 'addMenu') {
          let { serialNo, level } = menuRecordInfo
          if (level == '一级菜单') {
            level = '二级菜单'
          } else if (level == '二级菜单') {
            level = '三级菜单'
          }
          let params = {
            ...response,
            parentId: serialNo,
            level: level,
            wxPlatformMenuTemplateNo: currentRecord.serialNo,
          }
          let res = await addWxPlatformMenu(params)
          res
            ? message.success('添加子菜单成功')
            : message.error(`添子菜单失败`)
        } else if (isOption === 'increaseParentMenu') {
          let params = {
            ...response,
            parentId: '0',
            level: '一级菜单',
            wxPlatformMenuTemplateNo: currentRecord.serialNo,
          }
          let res = await addWxPlatformMenu(params)
          res
            ? message.success('新增父级菜单成功')
            : message.error(`新增父级菜单失败`)
        } else {
          let { serialNo } = menuRecordInfo
          let params = { ...response, serialNo }
          let res = await editWxPlatformMenu(params)
          res ? message.success('修改菜单成功') : message.error(`修改菜单失败`)
        }
        // 重置表单数据
        formRef.resetFields()
        // 重新请求数据页面渲染
        let { serialNo } = currentRecord
        let res = await getWxPlatformMenuTemplate({ serialNo })
        if (res.code == '000000') {
          setWxPlatformMenuTemplateVo(res?.data)
          console.log(res.data)
        } else {
          message.error(res.message)
        }
        setIsAddFromModal(false)
      })
      .catch(() => {})
  }

  // 取消确认
  const formCancel = () => {
    setIsAddFromModal(false)
    // 重置表单数据
    formRef.resetFields()
  }

  // edit
  const handleEdit = (record: object) => {
    // 保存每行数据
    setMenuRecordInfo(record)
    // 数据回显
    formRef.setFieldsValue(record)
    // edit标识
    setIsOption('edit')
    setIsAddFromModal(true)
  }

  // 删除
  const confirmDel = async (record: object) => {
    let { serialNo } = record
    let res = await delWxPlatformMenu({ serialNo })
    if ((res.data = '000000')) {
      message.success('删除数据成功')
      serialNo = currentRecord.serialNo
      res = await getWxPlatformMenuTemplate({ serialNo })
      if (res.code == '000000') {
        setWxPlatformMenuTemplateVo(res.data)
        console.log(res.data)
      } else {
        message.error(res.message)
      }
    } else {
      message.error(res.message)
    }
  }
  // 渲染操作行
  columnsData.forEach((item: any) => {
    if (item.dataIndex === 'option')
      item.render = columnsActionRender[item.dataIndex]
    if (item.dataIndex === 'type')
      item.render = columnsTagsRender[item.dataIndex]
    if (item.dataIndex === 'status')
      item.render = columnsStatusRender[item.dataIndex]
  })

  return (
    <>
      <Card
        title={<TableTitle title="菜单模版管理" />}
        bordered={false}
        extra={[
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={increaseParentMenu}
          >
            新增
          </Button>,
        ]}
      >
        <Table
          bordered
          columns={columnsData}
          dataSource={wxPlatformMenuTemplateVo?.children}
          rowKey={(record) => record.serialNo}
          scroll={{ x: 900 }}
          pagination={false}
        />

        <Button
          type="primary"
          // icon={<InboxOutlined />}
          onClick={({ key }) => {
            routerChange(null, 'toolList')
          }}
        >
          返回
        </Button>
      </Card>
      {/* 添加模态框 */}
      <Drawer
        title="添加"
        placement="right"
        onClose={formCancel}
        open={isAddFromModal}
        width="600"
      >
        <Form
          form={formRef}
          labelCol={{ span: 7 }}
          wrapperCol={{ span: 14 }}
          initialValues={{
            Type: 'click',
            status: '1',
            type: 'click',
            sort: '0',
            level: '一级菜单',
          }}
        >
          <Form.Item
            label="名称"
            name="name"
            tooltip="菜单标题，不超过16个字节，子菜单不超过60个字节"
            rules={[{ required: true, message: '请输入菜单名称!' }]}
          >
            <Input placeholder="给菜单起个名字吧！" />
          </Form.Item>
          {/* <Form.Item label="菜单层级" name="level">
            <Select>
              <Option value="0">请选择菜单层级！</Option>.
              <Option value="一级菜单">一级菜单</Option>
              <Option value="二级菜单">二级菜单</Option>
              <Option value="三级菜单">三级菜单</Option>
            </Select>
          </Form.Item> */}
          <Form.Item
            label="菜单类型"
            name="type"
            tooltip="菜单的响应动作类型，
            view表示网页类型，
            click表示点击类型，
            miniprogram表示小程序类型
            article为公众号文章跳转"
          >
            <Select>
              <Option value="0">请选择菜单类型！</Option>.
              <Option value="click">click</Option>
              <Option value="view">view</Option>
              <Option value="miniprogram">miniprogram</Option>
              <Option value="article">article</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="跳转链接"
            name="url"
            rules={[{ required: false, message: '请输入跳转链接!' }]}
            tooltip="网页链接，用户点击菜单可打开链接，不超过1024字节 
            type为miniprogram时，不支持小程序的老版本客户端将打开本url 
            view|miniprogram菜单为必填字段"
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="菜单key"
            name="menuKey"
            rules={[{ required: false, message: '请输入菜单key!' }]}
            tooltip="用于消息接口推送，不超过128字节  
            click等点击类型必须"
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="小程序的appid"
            name="appid"
            rules={[{ required: false, message: '请输入appid!' }]}
            tooltip="miniprogram类型必须(仅认证公众号可配置）"
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="小程序的页面路径"
            name="pagepath"
            rules={[{ required: false, message: '请输入菜单url!' }]}
            tooltip="miniprogram类型必须"
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="发布文章id"
            name="articleId"
            rules={[{ required: false, message: '请输入articleId!' }]}
            tooltip="article类型和articleViewLimited类型必须"
          >
            <Input />
          </Form.Item>

          <Form.Item label="菜单序列" name="sort">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select>
              <Option value="1">启用</Option>
              <Option value="0">禁用</Option>
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
    </>
  )
}
