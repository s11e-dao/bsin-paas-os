import React, { useState } from 'react';
import {
  Form,
  Input,
  Modal,
  Divider,
  message,
  Button,
  Select,
  Card,
  Popconfirm,
  Descriptions,
  DatePicker,
  InputNumber,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './equityData';
import {
  getEquityList,
  getListByCategoryNo,
  getEquityDetail,
  deleteEquityConfig,
  configEquity,
} from './service';
import TableTitle from '../../../components/TableTitle';
// import styles from './index.css';

const { RangePicker } = DatePicker;

interface Props {
  record: any;
  setCurrentContent?: (content: string) => void;
}

export default ({ record, setCurrentContent }: Props) => {
  const { TextArea } = Input; 
  const { Option } = Select;
  // 控制新增模态框
  const [isEquityModal, setIsEquityModal] = useState(false);
  // 查看模态框
  const [isViewEquityModal, setIsViewEquityModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState<any>({});
  // 任务起止时间
  const [equityList, setEquityList] = useState<any[]>([]);

  const [basedOnModel, setbasedOnModel] = useState('0');

  const [equityCategory, setEquityCategory] = useState('0');

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
      <a onClick={() => toViewEquity(record)}>查看</a>
      <Divider type="vertical" />
      <Popconfirm
        title="确定删除此条数据？?"
        onConfirm={() => toDelEquity(record.id)}
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
  const increaseEquity = () => {
    // 先获取权益列表
    const params = {
      // 可以根据需要添加查询参数
    };
    
    getEquityList(params).then((res) => {
      console.log('获取权益列表:', res);
      let equityListTemp: any[] = [];
      if (res?.data && res.data.length > 0) {
        res.data.map((item: any) => {
          console.log(item);
          let equityJson = {
            serialNo: item.serialNo,
            name: item.name,
          };
          equityListTemp.push(equityJson);
        });
      }
      setEquityList(equityListTemp);
      
      // 获取权益列表后再打开模态框
      setIsEquityModal(true);
    }).catch((error) => {
      console.error('获取权益列表失败:', error);
      message.error('获取权益列表失败，请重试！');
    });
  };

  /**
   * 确认添加任务
   */
  const confirmEquity = () => {
    // 获取输入的表单值
    FormRef.validateFields()
      .then(async () => {
        // 获取表单结果
        let response = FormRef.getFieldsValue();
        console.log(response);
        response.categoryNo = record.serialNo;
        response.category = equityCategory;
        configEquity(response).then((res) => {
          console.log('add', res);
          if (res.code === 0 ) {
            // 重置输入的表单
            FormRef.resetFields();
            // 刷新proTable
            actionRef.current?.reload();
            setIsEquityModal(false);
          } else {
            message.error(`失败： ${res?.message}`);
          }
        });
      })
      .catch(() => {});
  };

  /**
   * 取消添加任务
   */
  const onCancelEquity = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsEquityModal(false);
  };

  /**
   * 删除
   */
  const toDelEquity = async (record: any) => {
    console.log('record', record);
    let { equityRelationshipNo } = record;
    let delRes = await deleteEquityConfig({ serialNo: equityRelationshipNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewEquity = async (record: any) => {
    let { serialNo } = record;
    let viewRes = await getEquityDetail({ serialNo });
    setIsViewEquityModal(true);
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
    // 请求后台获取商户上架的资产
    getEquityList(params).then((res) => {
      console.log(res);
      let equityListTemp: any[] = [];
      res?.data.map((item: any) => {
        console.log(item);
        let equityJson = {
          serialNo: item.serialNo,
          name: item.name,
        };
        equityListTemp.push(equityJson);
      });
      setEquityList(equityListTemp);
    });
  };

  return (
    <>
      <Card style={{ marginBottom: 16 }}>
        <Descriptions title="权益配置"></Descriptions>
        {/* Pro表格 */}
        <ProTable<columnsDataType>
          headerTitle={<TableTitle title="权益列表" />}
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
            setEquityCategory(record.category)
            let res = await getListByCategoryNo({
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
                increaseEquity();
              }}
              key="button"
              icon={<PlusOutlined />}
              type="primary"
            >
              配置权益
            </Button>,
          ]}
        />
        {/* 新增激励模板模态框 */}
        <Modal
          title="配置权益"
          centered
          open={isEquityModal}
          onOk={confirmEquity}
          onCancel={onCancelEquity}
          width={600}
          okText="保存"
          cancelText="取消"
        >
          <Form
            name="basic"
            form={FormRef}
            layout="vertical"
            // 表单默认值
            initialValues={{ equityNo: '0' }}
          >
            {/* 权益表达式预览 */}
            <div style={{ 
              padding: '12px 16px', 
              backgroundColor: '#f5f5f5', 
              borderRadius: '6px', 
              marginBottom: '16px',
              border: '1px solid #d9d9d9'
            }}>
              <div style={{ fontSize: '14px', color: '#666', marginBottom: '4px' }}>权益表达式预览：</div>
              <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#1890ff' }}>
                {(() => {
                  const equityNo = FormRef.getFieldValue('equityNo');
                  const selectedEquity = equityList.find(e => e?.serialNo === equityNo);
                  
                  if (selectedEquity) {
                    return `关联权益: ${selectedEquity.name}`;
                  } else {
                    return '请选择关联权益';
                  }
                })()}
              </div>
            </div>

            <Form.Item
              label="关联权益"
              name="equityNo"
              rules={[{ required: true, message: '请选择关联权益!' }]}
            >
              <Select 
                style={{ width: '100%' }}
                placeholder="请选择要关联的权益"
                showSearch
                filterOption={(input, option) =>
                  (option?.children as unknown as string)?.toLowerCase().includes(input.toLowerCase())
                }
              >
                <Option value="0">请选择关联权益</Option>
                {equityList.map((equity) => {
                  return (
                    <Option key={equity?.serialNo} value={equity?.serialNo}>
                      {equity?.name}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>

            <Form.Item
              label="权益描述"
              name="description"
              rules={[{ required: true, message: '请输入权益描述!' }]}
            >
              <Input.TextArea 
                rows={3}
                placeholder="详细描述此权益的含义和作用"
              />
            </Form.Item>
          </Form>
        </Modal>
        {/* 查看详情模态框 */}
        <Modal
          title="查看条件详情"
          width={800}
          centered
          visible={isViewEquityModal}
          onOk={() => setIsViewEquityModal(false)}
          onCancel={() => setIsViewEquityModal(false)}
        >
          {/* 详情信息 */}
          <Descriptions title="任务详细信息">
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
