import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  Radio,
  message,
  Button,
  Select,
  Card,
  Popconfirm,
  Descriptions,
  DatePicker,
  Divider,
  Row,
  Col,
  Space,
  Collapse,
  InputNumber,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './conditionData';

import {
  getConditionList,
  getConditionListByCategoryNo,
  getConditionDetail,
  deleteConditionConfig,
  configCondition,
} from './service';

import TableTitle from '../../../components/TableTitle';
// import styles from './index.css'; // 暂时注释掉，避免类型错误

const { RangePicker } = DatePicker;
const { Panel } = Collapse;

interface Props {
  record: any;
  setCurrentContent?: (content: string) => void;
}

export default ({ record, setCurrentContent }: Props) => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isConditionModal, setIsConditionModal] = useState(false);
  // 查看模态框
  const [isViewConditionModal, setIsViewConditionModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState<any>({});
  // 任务起止时间
  const [conditionList, setConditionList] = useState<any[]>([]);
  const [basedOnModel, setbasedOnModel] = useState('0');

  const [conditionCategory, setConditionCategory] = useState('0');
  
  // 条件配置相关状态
  const [conditionConfig, setConditionConfig] = useState<{
    operator: string;
    value: string;
    description: string;
  }>({
    operator: 'eq',
    value: '',
    description: ''
  });

  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.dictType}>
      <a onClick={() => toViewCondition(record)}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="确定删除此条数据？?"
        onConfirm={() => toDelCondition(record.id)}
        onCancel={() => {
          message.warning(`取消删除`);
        }}
        okText="是"
        cancelText="否"
      >
        <a>删除</a>
      </Popconfirm>
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  /**
   * 以下内容为操作相关
   */

  const baseModelChange = (e: any) => {
    console.log(e);
    // 根据点击选择展示不同的输入框
    setbasedOnModel(e.target.value);
  };

  // 新增模板
  const increaseCondition = () => {
    // 先获取条件列表
    const params = {
      // 可以根据需要添加查询参数
    };
    
    getConditionList(params).then((res) => {
      console.log('获取条件列表:', res);
      let conditionListTemp: any[] = [];
      if (res?.data && res.data.length > 0) {
        res.data.map((item: any) => {
          console.log(item);
          let conditionJson = {
            serialNo: item.serialNo,
            name: item.name,
          };
          conditionListTemp.push(conditionJson);
        });
      }
      setConditionList(conditionListTemp);
      
      // 获取条件列表后再打开模态框
      setIsConditionModal(true);
    }).catch((error) => {
      console.error('获取条件列表失败:', error);
      message.error('获取条件列表失败，请重试！');
    });
  };

  /**
   * 确认添加任务
   */
  const confirmCondition = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log('表单数据:', response);
        console.log('条件配置:', conditionConfig);
        
        // 合并表单数据和条件配置
        const submitData = {
          ...response,
          categoryNo: record.serialNo,
          category: conditionCategory
        };
        
        console.log('提交数据:', submitData);
        
        configCondition(submitData).then((res) => {
          console.log('add', res);
          if (res.code === 0 ) {
            message.success('条件添加成功');
            // 重置输入的表单
            FormRef.resetFields();
            setConditionConfig({
              operator: 'eq',
              value: '',
              description: ''
            });
            // 刷新proTable
            actionRef.current?.reload();
            setIsConditionModal(false);
          } else {
            message.error(`失败： ${res?.message}`);
          }
        }).catch((error) => {
          console.error('添加条件配置失败:', error);
          message.error('添加条件配置失败，请重试！');
        });
      })
      .catch((error) => {
        console.error('表单验证失败:', error);
        message.error('请检查表单填写是否正确');
      });
  };

  /**
   * 取消添加条件
   */
  const onCancelCondition = () => {
    // 重置输入的表单
    FormRef.resetFields();
            // 重置条件配置状态
            setConditionConfig({
              operator: 'eq',
              value: '',
              description: ''
            });
    // 清空条件列表
    setConditionList([]);
    setIsConditionModal(false);
  };

  /**
   * 删除模板
   */
  const toDelCondition = async (record: any) => {
    console.log('record', record);
    let { conditionRelationshipNo } = record;
    let delRes = await deleteConditionConfig({
      serialNo: conditionRelationshipNo,
    });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewCondition = async (record: any) => {
    let { serialNo } = record;
    let viewRes = await getConditionDetail({ serialNo });
    setIsViewConditionModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    let typeText = type;
    return typeText;
  };

  const typeOnChange = (value: string) => {
    console.log(value);
    // 币种
    let params = {
      pageSize: 99,
      current: 1,
      type: value,
    };
    // 请求后台获取条件
    getConditionList(params).then((res) => {
      console.log(res);
      let conditionListTemp: any[] = [];
      res?.data.map((item: any) => {
        console.log(item);
        let conditionJson = {
          serialNo: item.serialNo,
          name: item.name,
        };
        conditionListTemp.push(conditionJson);
      });
      setConditionList(conditionListTemp);
    });
  };

  return (
    <>
      <Card style={{ marginBottom: 16 }}>
        <Descriptions title="配置条件"></Descriptions>
        {/* Pro表格 */}

        <ProTable<columnsDataType>
          headerTitle={<TableTitle title="任务条件列表" />}
          scroll={{ x: 900 }}
          search={false}
          bordered
          // 表头
          columns={columns}
          actionRef={actionRef}
          // 请求获取的数据
          request={async (params) => {
            // console.log(params);
            params.categoryNo = record.serialNo;
            setConditionCategory(record.category);
            let res = await getConditionListByCategoryNo({
              ...params,
              // pageNum: params.current,
            });
            console.log('😒', res);
            const result = {
              data: res.data,
            };
            return result;
          }}
          rowKey="serialNo"
          // 搜索表单的配置
          form={{
            ignoreRules: false,
          }}
          pagination={false}
          toolBarRender={() => [
            <Button
              onClick={() => {
                increaseCondition();
              }}
              key="button"
              icon={<PlusOutlined />}
              type="primary"
            >
              配置条件
            </Button>,
          ]}
        />
        {/* 新增条件模板模态框 */}
        <Modal
          title="配置条件"
          centered
          open={isConditionModal}
          onOk={confirmCondition}
          onCancel={onCancelCondition}
          width={600}
          okText="保存"
          cancelText="取消"
        >
          <Form
            name="basic"
            form={FormRef}
            layout="vertical"
            // 表单默认值
            initialValues={{ conditionNo: '0' }}
          >
            {/* 条件表达式预览 */}
            <div style={{ 
              padding: '12px 16px', 
              backgroundColor: '#f5f5f5', 
              borderRadius: '6px', 
              marginBottom: '16px',
              border: '1px solid #d9d9d9'
            }}>
              <div style={{ fontSize: '14px', color: '#666', marginBottom: '4px' }}>条件表达式预览：</div>
              <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#1890ff' }}>
                {(() => {
                  const conditionNo = FormRef.getFieldValue('conditionNo');
                  const selectedCondition = conditionList.find(c => c?.serialNo === conditionNo);
                  
                  if (selectedCondition) {
                    return `关联条件: ${selectedCondition.name}`;
                  } else {
                    return '请选择关联条件';
                  }
                })()}
              </div>
            </div>

            <Row gutter={16}>
              <Col span={24}>
                <Form.Item
                  label="关联条件"
                  name="conditionNo"
                  rules={[{ required: true, message: '请选择关联条件!' }]}
                >
                  <Select 
                    style={{ width: '100%' }}
                    placeholder="请选择要关联的条件"
                    showSearch
                    filterOption={(input, option) =>
                      (option?.children as unknown as string)?.toLowerCase().includes(input.toLowerCase())
                    }
                  >
                    <Option value="0">请选择关联条件</Option>
                    {conditionList.map((condition) => {
                      return (
                        <Option key={condition?.serialNo} value={condition?.serialNo}>
                          {condition?.name}
                        </Option>
                      );
                    })}
                  </Select>
                </Form.Item>
              </Col>
            </Row>


            <Form.Item
              label="条件描述"
              name="description"
              rules={[{ required: true, message: '请输入条件描述!' }]}
            >
              <Input.TextArea 
                rows={3}
                placeholder="详细描述此条件的含义和作用"
                value={conditionConfig.description}
                onChange={(e) => setConditionConfig(prev => ({ ...prev, description: e.target.value }))}
              />
            </Form.Item>
          </Form>
        </Modal>

        {/* 查看详情模态框 */}
        <Modal
          title="查看条件详情"
          width={800}
          centered
          visible={isViewConditionModal}
          onOk={() => setIsViewConditionModal(false)}
          onCancel={() => setIsViewConditionModal(false)}
        >
          {/* 详情信息 */}
          <Descriptions>
            <Descriptions.Item label="租户ID">
              {isViewRecord?.tenantId}
            </Descriptions.Item>
            <Descriptions.Item label="商户ID">
              {isViewRecord?.merchantNo}
            </Descriptions.Item>
            <Descriptions.Item label="条件编号">
              {isViewRecord?.serialNo}
            </Descriptions.Item>
            <Descriptions.Item label="条件名称">
              {isViewRecord?.name}
            </Descriptions.Item>
            <Descriptions.Item label="条件备注">
              {isViewRecord?.remark}
            </Descriptions.Item>
            <Descriptions.Item label="类型类型">
              {handleViewRecordOfType()}
            </Descriptions.Item>
            <Descriptions.Item label="数量">
              {isViewRecord?.amount}
            </Descriptions.Item>
            <Descriptions.Item label="条件类型编号">
              {isViewRecord?.typeNo}
            </Descriptions.Item>
            <Descriptions.Item label="更新时间">
              {isViewRecord?.updateTime}
            </Descriptions.Item>
            <Descriptions.Item label="创建者">
              {isViewRecord?.createBy}
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">
              {isViewRecord?.createTime}
            </Descriptions.Item>
            <Descriptions.Item label="条件描述">
              {isViewRecord?.description}
            </Descriptions.Item>
          </Descriptions>
        </Modal>
      </Card>
    </>
  );
};
