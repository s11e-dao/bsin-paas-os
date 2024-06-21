import React, { useEffect, useState } from 'react'
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Upload,
  Checkbox,
  Card,
} from 'antd'
import type { CheckboxValueType } from 'antd/es/checkbox/Group'
import type { UploadProps } from 'antd/es/upload/interface'
import type { ProColumns, ActionType } from '@ant-design/pro-table'
import ProTable from '@ant-design/pro-table'
import { PlusOutlined, InboxOutlined } from '@ant-design/icons'
import columnsFunctionSubscribeData, {
  columnsFunctionSubscribeDataType,
} from './functionSubscribeData'
import {
  getMerchantAppList,
  getMerchantAuthorizableAppList,
  subscribeApps,
  getSubscribeDetail,
  getServiceSubscribePageList,
  getAiFunctionSubscribePageList,
} from './service'

import TableTitle from '../../../../components/TableTitle'
import { hex_md5 } from '@/utils/md5'
import {
  getLocalStorageInfo,
  getSessionStorageInfo,
} from '@/utils/localStorageInfo'

export default ({ subscribeFunction, setCurrentContent }) => {
  // 控制新增模态框
  const [registerMerchantModal, setRegisterMerchantModal] = useState(false)
  // 查看模态框
  const [isViewSubscribeModal, setIsViewSubscribeModal] = useState(false)
  // 查看
  const [viewRecord, setViewRecord] = useState({})

  const [orgServiceList, setOrgServiceList] = useState([])
  // 订阅服务查询
  const [suscribedServiceList, setSuscribedServiceList] = useState([])
  // 订阅功能查询
  const [suscribedFunctionList, setSuscribedFunctionList] = useState([])

  // 获取表单
  const [FormRef] = Form.useForm()

  useEffect(() => {
    // 查询协议
    let params = {
      current: '1',
      pageSize: '99',
      orgCode: getLocalStorageInfo('merchantInfo')?.merchantName, // 商户名称
    }
    getMerchantAppList(params).then((res) => {
      if (res.code == '000000') {
        setOrgServiceList(res?.data)
      } else {
        message.error(res.message)
      }
    })

    // 查询商户可授权应用
    let params1 = {
      customerNo: getLocalStorageInfo('merchantInfo')?.customerNo,
    }
    getServiceSubscribePageList(params).then((res) => {
      console.log(res?.data)
      if (res?.code == '000000') {
        setSuscribedServiceList(res?.data)
      }
    })
  }, [])

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<
    columnsFunctionSubscribeDataType
  >[] = columnsFunctionSubscribeData

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewSubscribe(record)
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <a
          onClick={() => {
            subscribeFunction(record)
          }}
        >
          订阅功能
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      {/* <li>
        <Popconfirm
          title="确定取消服务订阅？"
          okText="是"
          cancelText="否"
          onConfirm={() => {
            toCancelSubscribe(record)
          }}
          // onCancel={cancel}
        >
          <a>取消</a>
        </Popconfirm>
      </li> */}
    </ul>
  )

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'option' ? (item.render = actionRender) : undefined
  })

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>()

  /**
   * 取消添加模板
   */
  const onCancel = () => {
    // 重置输入的表单
    FormRef.resetFields()
    setRegisterMerchantModal(false)
  }

  /**
   * 取消订阅
   */
  const toCancelSubscribe = async (record) => {
    console.log('record', record)
    // let { serialNo } = record
    // let delRes = await delSubscribe({ serialNo })
    // console.log('delRes', delRes)
    // if (delRes.code === '000000') {
    //   // 删除成功刷新表单
    //   actionRef.current?.reload()
    // }
  }

  /**
   * 查看订阅详情
   */
  const toViewSubscribe = async (record) => {
    // let { serialNo } = record
    // let viewRes = await getSubscribeDetail({ serialNo })
    // setIsViewSubscribeModal(true)
    // console.log('viewRes', viewRes)
    setViewRecord(record)
    setIsViewSubscribeModal(true)
  }

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = viewRecord
    // 类型： 1、应用(服务) 2、功能
    if (type == '1') {
      return '应用'
    } else if (type == '2') {
      return '功能'
    } else {
      return type
    }
  }

  const handleViewRecordOfStatus = () => {
    let { status } = viewRecord
    if (status == '0') {
      return '待缴费'
    } else if (status == '1') {
      return '待审核'
    } else if (status == '2') {
      return '正常'
    } else if (status == '3') {
      return '欠费停止'
    } else if (status == '4') {
      return '冻结'
    } else {
      return status
    }
  }

  // 认证状态   1: 待认证  2：认证成功  3：认证失败
  const handleViewRecordOfSauthenticationStatus = () => {
    let { authenticationStatus } = viewRecord
    if (authenticationStatus == '1') {
      return '待认证'
    } else if (authenticationStatus == '2') {
      return '认证成功'
    } else if (authenticationStatus == '3') {
      return '认证失败'
    } else {
      return authenticationStatus
    }
  }

  return (
    <div>
      {/* Pro表格 */}
      <Card>
        <ProTable<columnsFunctionSubscribeDataType>
          headerTitle={<TableTitle title="订阅服务列表" />}
          scroll={{ x: 900 }}
          bordered
          // 表头
          columns={columns}
          actionRef={actionRef}
          // 请求获取的数据
          request={async (params) => {
            console.log(params)
            // let res = await getMerchantAppList({
            //   orgCode: getLocalStorageInfo('merchantInfo')?.merchantName, // 商户名称
            // })
            // console.log('😒', res)
            // setOrgServiceList(res.data)
            // }
            // let res = await getServiceSubscribePageList({
            //   current: '1',
            //   pageSize: '99',
            //   customerNo: getLocalStorageInfo('merchantInfo')?.customerNo,
            // })
            // console.log(res?.data)
            // setSuscribedServiceList(res?.data)
            let { status } = params
            let res = await getAiFunctionSubscribePageList({
              current: '1',
              pageSize: '99',
              status: status,
              // type: '2',
              customerNo: getLocalStorageInfo('merchantInfo')?.customerNo,
            })
            console.log(res?.data)
            setSuscribedFunctionList(res?.data)
            const result = {
              data: res.data,
            }
            return result
          }}
          rowKey="serialNo"
          // 搜索框配置
          search={{
            labelWidth: 'auto',
          }}
          // 搜索表单的配置
          form={{
            ignoreRules: false,
          }}
          pagination={false}
          toolBarRender={() => [
            <Button
              onClick={() => {
                message.info('暂未开放服务订阅，敬请期待！')
                // setCurrentContent('serviceSubscribe')
              }}
              key="button"
              icon={<PlusOutlined />}
              type="primary"
            >
              订阅服务
            </Button>,
          ]}
        />

        {/* 查看详情模态框 */}
        <Modal
          title="功能订阅"
          width={800}
          centered
          open={isViewSubscribeModal}
          onOk={() => setIsViewSubscribeModal(false)}
          onCancel={() => setIsViewSubscribeModal(false)}
        >
          {/* 详情信息 */}
          <Descriptions title="服务功能详情">
            <Descriptions.Item label="ID">
              {viewRecord?.serialNo}
            </Descriptions.Item>
            <Descriptions.Item label="租户号">
              {viewRecord?.tenantId}
            </Descriptions.Item>
            <Descriptions.Item label="商户户号">
              {viewRecord?.merchantNo}
            </Descriptions.Item>
            <Descriptions.Item label="客户号">
              {viewRecord?.customerNo}
            </Descriptions.Item>
            <Descriptions.Item label="服务类型">
              {handleViewRecordOfType()}
            </Descriptions.Item>
            <Descriptions.Item label="服务状态">
              {handleViewRecordOfStatus()}
            </Descriptions.Item>
            <Descriptions.Item label="开始时间">
              {viewRecord?.startTime}
            </Descriptions.Item>
            <Descriptions.Item label="结束时间">
              {viewRecord?.endTime}
            </Descriptions.Item>
            <Descriptions.Item label="智能体数量">
              {viewRecord?.copilotNum}
            </Descriptions.Item>
            <Descriptions.Item label="知识库数量">
              {viewRecord?.knowledgeBaseNum}
            </Descriptions.Item>
            <Descriptions.Item label="知识库文件数量">
              {viewRecord?.knowledgeBaseFileNum}
            </Descriptions.Item>
            <Descriptions.Item label="公众号数量">
              {viewRecord?.mpNum}
            </Descriptions.Item>
            <Descriptions.Item label="企业微信数量">
              {viewRecord?.cpNum}
            </Descriptions.Item>
            <Descriptions.Item label="个人微信数量">
              {viewRecord?.wechatNum}
            </Descriptions.Item>
            <Descriptions.Item label="小程序数量">
              {viewRecord?.miniappNum}
            </Descriptions.Item>
            <Descriptions.Item label="菜单模版数量">
              {viewRecord?.menuTemplateNum}
            </Descriptions.Item>
            <Descriptions.Item label="敏感词数量">
              {viewRecord?.sensitiveWordsNum}
            </Descriptions.Item>            
            <Descriptions.Item label="订阅价格">
              {viewRecord?.price}
            </Descriptions.Item>
            <Descriptions.Item label="服务时间">
              {viewRecord?.serviceDuration}
            </Descriptions.Item>
          </Descriptions>
        </Modal>
      </Card>
    </div>
  )
}
