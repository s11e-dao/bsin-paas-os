import React, { useState, useEffect } from 'react';
import {
  Form,
  Input,
  InputNumber,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Row,
  Col,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
  getConditionPageList,
  addCondition,
  deleteCondition,
  getConditionDetail,
  getDigitalAssetsItemList,
  getBondingCurveTokenList,
} from './service';


import { getGradeList } from '../../Grade/service';

import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  // 控制新增模态框
  const [isTemplateModal, setIsTemplateModal] = useState(false);
  // 查看模态框
  const [isViewTemplateModal, setIsViewTemplateModal] = useState(false);
  // 查看
  const [isViewRecord, setIsViewRecord] = useState<any>({});
  const [typeNoList, setTypeNoList] = useState<any[]>([]);
  const [gradeList, setGradeList] = useState<any[]>([]);
  const [digitalAssetsType, setDigitalAssetsType] = useState('');
  // 获取表单
  const [FormRef] = Form.useForm();

  /**
   * 以下内容为表格相关
   */

  // 表头数据
  const columns: ProColumns<columnsDataType>[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <ul className="ant-list-item-action" style={{ margin: 0 }}>
      <li>
        <a
          onClick={() => {
            toViewContractTemplate(record);
          }}
        >
          查看
        </a>
        <em className="ant-list-item-action-split"></em>
      </li>
      <li>
        <Popconfirm
          title="确定删除此条模板？"
          okText="是"
          cancelText="否"
          onConfirm={() => {
            toDelContractTemplate(record);
          }}
          // onCancel={cancel}
        >
          <a>删除</a>
        </Popconfirm>
      </li>
    </ul>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  useEffect(() => {
    let params = {};
    // 查询等级
    getGradeList(params).then((res) => {
      setGradeList(res?.data);
    });
  }, []);

  /**
   * 以下内容为操作相关
   */

  // 新增模板
  const increaseTemplate = () => {
    setIsTemplateModal(true);
  };

  /**
   * 确认添加模板
   */
  const confirmTemplate = () => {
    FormRef.validateFields()
      .then(async () => {
        const formData = FormRef.getFieldsValue();
        console.log('表单数据:', formData);

        // 构建请求数据，确保包含所有必要字段
        const requestData: any = {
          name: formData.name,                    // 条件名称
          value: formData.type === '9' ? formData.gradeName : formData.value,  // 比较值：会员等级用gradeName，其他用value
          operator: formData.operator,            // 操作符
          remark: formData.remark,                // 备注
          typeNo: formData.typeNo,                // 资产类型ID
          typeProtocol: formData.typeProtocol,    // 协议类型
          typeTokenId: formData.typeTokenId,      // Token ID
          type: formData.type                     // 条件类型
        };

        // 如果是会员等级类型，添加gradeName字段
        if (formData.type === '9') {
          requestData.gradeName = formData.gradeName;
        }

        console.log('请求数据:', requestData);

        addCondition(requestData).then((res) => {
          console.log('add', res);
          if (res.code === 0) {
            FormRef.resetFields();
            actionRef.current?.reload();
            setIsTemplateModal(false);
            message.success('条件添加成功！');
          } else {
            message.error(`失败： ${res?.message}`);
          }
        }).catch((error) => {
          console.error('添加条件失败:', error);
          message.error('添加条件失败，请重试！');
        });
      })
      .catch((error) => {
        console.error('表单验证失败:', error);
        message.error('请检查表单填写是否正确！');
      });
  };

  /**
   * 取消添加模板
   */
  const onCancelTemplate = () => {
    // 重置输入的表单
    FormRef.resetFields();
    setIsTemplateModal(false);
  };

  /**
   * 删除模板
   */
  const toDelContractTemplate = async (record: any) => {
    console.log('record', record);
    let { serialNo } = record;
    let delRes = await deleteCondition({ serialNo });
    console.log('delRes', delRes);
    if (delRes.code === 0) {
      // 删除成功刷新表单
      actionRef.current?.reload();
    }
  };

  /**
   * 查看详情
   */
  const toViewContractTemplate = async (record: any) => {
    let { serialNo } = record;
    let viewRes = await getConditionDetail({ serialNo });
    setIsViewTemplateModal(true);
    console.log('viewRes', viewRes);
    setIsViewRecord(viewRes.data);
  };

  /**
   * 详情，模板类型对应
   */
  const handleViewRecordOfType = () => {
    let { type } = isViewRecord;
    if (type == `1`) {
      return '数字资产';
    } else if (type == `2`) {
      return 'PFP';
    } else if (type == `3`) {
      return '账户-DP';
    } else if (type == `4`) {
      return '数字门票';
    } else if (type == `5`) {
      return 'Pass卡';
    } else if (type == `6`) {
      return '账户-BC';
    } else if (type == `7`) {
      return '满减';
    } else if (type == `8`) {
      return '权限';
    } else if (type == `9`) {
      return '会员等级';
    } else {
      return type;
    }
  };

  // 自动生成条件名称和备注的函数
  const generateConditionInfo = () => {
    const formValues = FormRef.getFieldsValue();
    const { type, operator, amount, gradeName } = formValues;
    
    if (!type || !operator || (!amount && !gradeName)) {
      return { name: '', remark: '' };
    }

    // 获取条件类型名称
    const getTypeName = (typeValue: string) => {
      const typeMap: Record<string, string> = {
        '1': '数字徽章',
        '2': 'PFP',
        '3': '账户-DP',
        '4': '数字门票',
        '5': 'Pass卡',
        '6': '账户-BC',
        '7': '满减',
        '8': '权限',
        '9': '会员等级'
      };
      return typeMap[typeValue] || '未知类型';
    };

    // 获取操作符名称
    const getOperatorName = (operatorValue: string) => {
      const operatorMap: Record<string, string> = {
        'eq': '等于',
        'ne': '不等于',
        'gt': '大于',
        'gte': '大于等于',
        'lt': '小于',
        'lte': '小于等于',
        'in': '包含',
        'like': '模糊匹配'
      };
      return operatorMap[operatorValue] || operatorValue;
    };

    const typeName = getTypeName(type);
    const operatorName = getOperatorName(operator);
    const value = type === '9' ? gradeName : amount;

    // 生成条件名称
    const name = `${typeName}${operatorName}${value}`;
    
    // 生成备注
    const remark = `当${typeName}${operatorName}${value}时触发此条件`;

    return { name, remark };
  };

  // 监听表单值变化，自动生成名称和备注
  const handleFormChange = () => {
    const { name, remark } = generateConditionInfo();
    if (name && remark) {
      FormRef.setFieldsValue({
        name: name,
        remark: remark
      });
    }
  };

  // 根据条件类型获取对应的数字资产列表
  const typeOnChange = (value: string) => {
    console.log(value);
    setDigitalAssetsType(value);
    
    // 清空资产类型选择，避免验证错误
    FormRef.setFieldsValue({ typeNo: undefined });
    
    // 触发自动生成
    setTimeout(() => {
      handleFormChange();
    }, 100);
    
    // 定义资产类型映射
    const assetTypeMapping: { [key: string]: { service: Function | null; params: any } } = {
      '1': { service: getDigitalAssetsItemList, params: { assetsTypes: ['1'] } },      // 数字徽章
      '2': { service: getDigitalAssetsItemList, params: { assetsTypes: ['2'] } },      // PFP
      '3': { service: getDigitalAssetsItemList, params: { assetsTypes: ['3'] } },      // 账户-DP
      '4': { service: getDigitalAssetsItemList, params: { assetsTypes: ['4'] } },      // 数字门票
      '5': { service: getDigitalAssetsItemList, params: { assetsTypes: ['5'] } },      // PASS卡
      '6': { service: getBondingCurveTokenList, params: { assetsTypes: [] } },         // 账户-BC
      '7': { service: null, params: null },                                             // 满减
      '8': { service: null, params: null },                                             // 权限
      '9': { service: getGradeList, params: { assetsTypes: [] } }                      // 会员等级
    };

    const mapping = assetTypeMapping[value];
    
    if (!mapping || !mapping.service) {
      // 不需要获取数据的类型（满减、权限）
      setTypeNoList([]);
      return;
    }

    // 调用对应的服务获取数据
    mapping.service!(mapping.params).then((res: any) => {
      console.log(res);
      let typeNoListTemp: any[] = [];
      
      if (res?.code == 0 && res?.data) {
        res.data.map((item: any) => {
          let typeNoJson;
          
          if (value === '6') {
            // 联合曲线的数据结构
            typeNoJson = {
              typeNo: item?.serialNo,
              name: item?.name,
            };
          } else if (value === '9') {
            // 等级的数据结构
            typeNoJson = {
              typeNo: item?.serialNo,
              name: item?.name,
              gradeNum: item?.gradeNum,
            };
          } else {
            // 数字资产的数据结构
            typeNoJson = {
              typeNo: item.serialNo,
              name: item.assetsName,
              tokenId: item.tokenId,
            };
          }
          
          typeNoListTemp.push(typeNoJson);
        });
      }
      
      setTypeNoList(typeNoListTemp);
    }).catch((error: any) => {
      console.error('获取数据失败:', error);
      setTypeNoList([]);
    });
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="条件列表" />}
        scroll={{ x: 900 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 请求获取的数据
        request={async (params) => {
          // console.log(params);
          let res = await getConditionPageList({
            ...params,
            // pageNum: params.current,
          });
          console.log('😒', res);
          const result = {
            data: res.data,
            total: res.pagination.totalSize,
          };
          return result;
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
        pagination={{
          pageSize: 10,
        }}
        toolBarRender={() => [
          <Button
            onClick={() => {
              increaseTemplate();
            }}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            添加条件
          </Button>,
        ]}
      />
      {/* 新增合约模板模态框 */}
      <Modal
        title="添加条件"
        centered
        width={800}
        open={isTemplateModal}
        onOk={confirmTemplate}
        onCancel={onCancelTemplate}
      >
        <Form
          name="basic"
          form={FormRef}
          layout="vertical"
          // 表单默认值
          initialValues={{ 
            type: '0', 
            operator: '>=', 
            typeProtocol: 'ERC20',
            value: 0,
            typeNo: '',
            typeTokenId: null
          }}
        >
          <Row gutter={16} style={{ marginTop: '16px' }}>
            <Col span={8}>
              <Form.Item
                label="条件类型"
                name="type"
                rules={[{ required: true, message: '请选择条件类型!' }]}
              >
                <Select
                  style={{ width: '100%' }}
                  onChange={(value) => typeOnChange(value)}
                >
                  <Option value="0">请选择条件类型</Option>
                  <Option value="1">数字徽章</Option>
                  <Option value="2">PFP</Option>
                  <Option value="3">账户-DP</Option>
                  <Option value="4">数字门票</Option>
                  <Option value="5">Pass卡</Option>
                  <Option value="6">账户-BC</Option>
                  <Option value="7">满减</Option>
                  <Option value="8">权限</Option>
                  <Option value="9">会员等级</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="操作符"
                name="operator"
                rules={[{ required: true, message: '请选择操作符!' }]}
              >
                <Select 
                  style={{ width: '100%' }}
                  onChange={() => {
                    setTimeout(() => {
                      handleFormChange();
                    }, 100);
                  }}
                >
                  <Option value="=">等于</Option>
                  <Option value="!=">不等于</Option>
                  <Option value=">">大于</Option>
                  <Option value=">=">大于等于</Option>
                  <Option value="<">小于</Option>
                  <Option value="<=">小于等于</Option>
                  <Option value="in">包含</Option>
                  <Option value="like">模糊匹配</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              {digitalAssetsType == '9' ? (
                <Form.Item
                  label="等级名称"
                  name="gradeName"
                  rules={[{ required: false, message: '请输入等级名称!' }]}
                >
                  <Input 
                    placeholder="请输入等级名称"
                    onChange={() => {
                      setTimeout(() => {
                        handleFormChange();
                      }, 100);
                    }}
                  />
                </Form.Item>
              ) : (
                <Form.Item
                  label="比较值"
                  name="value"
                  rules={[{ required: true, message: '请输入比较值!' }]}
                >
                  <InputNumber 
                    style={{ width: '100%' }}
                    placeholder="请输入比较值"
                    min={0}
                    precision={2}
                    step={1}
                    onChange={() => {
                      setTimeout(() => {
                        handleFormChange();
                      }, 100);
                    }}
                  />
                </Form.Item>
              )}
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="资产类型"
                name="typeNo"
                rules={[{ required: true, message: '请选择资产类型!' }]}
              >
                <Select 
                  style={{ width: '100%' }}
                  placeholder="请选择资产类型"
                  onChange={() => {
                    setTimeout(() => {
                      handleFormChange();
                    }, 100);
                  }}
                >
                  {typeNoList.map((item: any) => (
                    <Option key={item.typeNo} value={item.typeNo}>
                      {item.name}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="协议类型"
                name="typeProtocol"
                rules={[{ required: true, message: '请选择协议类型!' }]}
              >
                <Select 
                  style={{ width: '100%' }}
                  placeholder="请选择协议类型"
                >
                  <Option value="ERC20">ERC20</Option>
                  <Option value="ERC721">ERC721</Option>
                  <Option value="ERC1155">ERC1155</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="条件名称"
            name="name"
            rules={[{ required: true, message: '请输入条件名称!' }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            label="备注"
            name="remark"
            rules={[{ required: true, message: '请输入备注描述!' }]}
          >
            <Input.TextArea rows={3} placeholder="请输入备注描述" />
          </Form.Item>

          {/* 隐藏字段 */}
          <Form.Item name="typeTokenId" style={{ display: 'none' }}>
            <Input />
          </Form.Item>
        </Form>
      </Modal>
      {/* 查看详情模态框 */}
      <Modal
        title="查看条件"
        width={800}
        centered
        visible={isViewTemplateModal}
        onOk={() => setIsViewTemplateModal(false)}
        onCancel={() => setIsViewTemplateModal(false)}
      >
        {/* 详情信息 */}
        <Descriptions title="条件信息">
          <Descriptions.Item label="租户ID">
            {isViewRecord?.tenantId}
          </Descriptions.Item>
          <Descriptions.Item label="商户ID">
            {isViewRecord?.merchantId}
          </Descriptions.Item>
          <Descriptions.Item label="条件编号">
            {isViewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="条件类型">
            {handleViewRecordOfType()}
          </Descriptions.Item>
          <Descriptions.Item label="条件名称">
            {isViewRecord?.name}
          </Descriptions.Item>
          <Descriptions.Item label="条件类型编号">
            {isViewRecord?.typeNo}
          </Descriptions.Item>
          <Descriptions.Item label="条件资产类型协议">
            {isViewRecord?.typeProtocol}
          </Descriptions.Item>
          <Descriptions.Item label="条件资产tokenId">
            {isViewRecord?.typeTokenId}
          </Descriptions.Item>
          <Descriptions.Item label="会员等级">
            {isViewRecord?.grade}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {isViewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {isViewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="条件备注描述">
            {isViewRecord?.remark}
          </Descriptions.Item>
        </Descriptions>
      </Modal>
    </div>
  );
};
