import React, { useState, useEffect } from 'react';
import { useLocation } from '@umijs/max';
import {
  Form,
  Input,
  Modal,
  message,
  Button,
  Select,
  Popconfirm,
  Descriptions,
  Divider,
  QRCode,
  Tag,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import type { FormInstance } from 'antd';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined } from '@ant-design/icons';
import columnsData from './data';
import type { columnsDataType } from './data';
import {
  getWalletAccountPageList,
  addWalletAccount,
  createWalletAccount,
  editWalletAccount,
  deleteWalletAccount,
  getWalletAccountDetail,
  getChainCoinList,
  getWalletPageList,
} from './service';
import TableTitle from '../../../components/TableTitle';

export default () => {
  const { TextArea } = Input;
  const { Option } = Select;
  const location = useLocation();

  // 控制新增/编辑模态框
  const [isModalVisible, setIsModalVisible] = useState(false);
  // 查看模态框
  const [isViewModalVisible, setIsViewModalVisible] = useState(false);
  // 查看详情数据
  const [viewRecord, setViewRecord] = useState<any>({});
  // 编辑模式标识
  const [isEditMode, setIsEditMode] = useState(false);
  // 操作模式：'import'（导入）、'create'（创建）、'edit'（编辑）
  const [operationMode, setOperationMode] = useState<'import' | 'create' | 'edit'>('import');
  // 当前编辑的记录ID
  const [currentSerialNo, setCurrentSerialNo] = useState('');
  // 充值模态框
  const [isRechargeModal, setIsRechargeModal] = useState(false);
  // 充值信息
  const [rechargeInfo, setRechargeInfo] = useState({
    address: '',
    chainName: '',
    coin: ''
  });
  // 币种列表
  const [chainCoinList, setChainCoinList] = useState<any[]>([]);
  // 钱包列表
  const [walletList, setWalletList] = useState<any[]>([]);
  // 从URL获取的钱包编号
  const [urlWalletNo, setUrlWalletNo] = useState<string>('');
  // 获取表单
  const [form] = Form.useForm();
  // 搜索表单
  const searchFormRef = React.useRef<FormInstance>();

  // 获取币种列表和钱包列表
  useEffect(() => {
    loadChainCoinList();
    loadWalletList();
  }, []);

  // 加载币种列表
  const loadChainCoinList = async () => {
    try {
      const res = await getChainCoinList();
      if (res.code === 0) {
        setChainCoinList(res.data || []);
      }
    } catch (error) {
      console.error('加载币种列表失败', error);
    }
  };

  // 加载钱包列表
  const loadWalletList = async () => {
    try {
      const res = await getWalletPageList({
        pagination: {
          pageNum: 1,
          pageSize: 9999, // 获取所有钱包
        }
      });
      if (res.code === 0) {
        setWalletList(res.data || []);
      }
    } catch (error) {
      console.error('加载钱包列表失败', error);
    }
  };

  // 获取URL参数并填充钱包ID作为查询条件
  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const walletSerialNo = urlParams.get('walletSerialNo');
    console.log('walletSerialNo', walletSerialNo);
    console.log('searchFormRef.current', searchFormRef.current);
    if (walletSerialNo) {
      setUrlWalletNo(walletSerialNo);
      if (searchFormRef.current) {
        searchFormRef.current.setFieldsValue({
          walletNo: walletSerialNo
        });
      }
      // 触发表格重新加载
      setTimeout(() => {
        actionRef.current?.reload();
      }, 100);
    }
  }, [location.search]);

  /**
   * 以下内容为表格相关
   */

  // Table action 的引用，便于自定义触发
  const actionRef = React.useRef<ActionType>();

  // 表头数据
  const columns: any[] = columnsData;

  // 操作行数据 自定义操作行
  const actionRender: any = (text: any, record: any, index: number) => (
    <div key={record.serialNo}>
      <a onClick={() => handleView(record)}>查看</a>
      <Divider type="vertical" />
      <a onClick={() => handleEdit(record)}>编辑</a>
      <Divider type="vertical" />
      <a onClick={() => handleRecharge(record)}>充值</a>
      <Divider type="vertical" />
      <Popconfirm
        title="确认删除此链账户吗?"
        onConfirm={() => handleDelete(record.serialNo)}
        onCancel={() => {
          message.warning('取消删除');
        }}
        okText="确认"
        cancelText="取消"
      >
        <a style={{ color: 'red' }}>删除</a>
      </Popconfirm>
    </div>
  );

  // 自定义数据的表格头部数据
  columns.forEach((item: any) => {
    item.dataIndex === 'action' ? (item.render = actionRender) : undefined;
  });

  /**
   * 导入链账户
   */
  const handleAdd = () => {
    setIsEditMode(false);
    setOperationMode('import');
    setCurrentSerialNo('');
    form.resetFields();
    setIsModalVisible(true);
  };

  /**
   * 创建链账户
   */
  const handleCreate = () => {
    setIsEditMode(false);
    setOperationMode('create');
    setCurrentSerialNo('');
    form.resetFields();
    setIsModalVisible(true);
  };

  /**
   * 编辑链账户
   */
  const handleEdit = async (record: any) => {
    setIsEditMode(true);
    setOperationMode('edit');
    setCurrentSerialNo(record.serialNo);
    const { serialNo } = record;
    const viewRes = await getWalletAccountDetail({ serialNo });
    if (viewRes.code === 0) {
      form.setFieldsValue({
        ...viewRes.data,
        status: viewRes.data.status?.toString(),
      });
      setIsModalVisible(true);
    } else {
      message.error(`获取详情失败：${viewRes?.message}`);
    }
  };

  /**
   * 确认添加、创建或编辑
   */
  const handleSubmit = () => {
    form.validateFields()
      .then(async () => {
        const values = form.getFieldsValue();
        const reqParam = {
          ...values,
          serialNo: isEditMode ? currentSerialNo : undefined,
        };
        
        let apiCall: any;
        let successMsg = '';
        
        if (isEditMode) {
          apiCall = editWalletAccount;
          successMsg = '编辑成功';
        } else if (operationMode === 'import') {
          apiCall = addWalletAccount;
          successMsg = '导入成功';
        } else if (operationMode === 'create') {
          apiCall = createWalletAccount;
          successMsg = '创建成功';
        }
        
        if (!apiCall) {
          message.error('操作类型错误');
          return;
        }
        
        const res = await apiCall(reqParam);
        
        if (res.code === 0) {
          message.success(successMsg);
          form.resetFields();
          setIsModalVisible(false);
          actionRef.current?.reload();
        } else {
          message.error(`操作失败：${res?.message}`);
        }
      })
      .catch(() => {});
  };

  /**
   * 取消添加或编辑
   */
  const handleCancel = () => {
    form.resetFields();
    setIsModalVisible(false);
  };

  /**
   * 删除链账户
   */
  const handleDelete = async (serialNo: string) => {
    const delRes = await deleteWalletAccount({ serialNo });
    if (delRes.code === 0) {
      message.success('删除成功');
      actionRef.current?.reload();
    } else {
      message.error(`删除失败：${delRes?.message}`);
    }
  };

  /**
   * 查看详情
   */
  const handleView = async (record: any) => {
    const { serialNo } = record;
    const viewRes = await getWalletAccountDetail({ serialNo });
    if (viewRes.code === 0) {
      setViewRecord(viewRes.data);
      setIsViewModalVisible(true);
    } else {
      message.error(`获取详情失败：${viewRes?.message}`);
    }
  };

  /**
   * 充值
   */
  const handleRecharge = (record: any) => {
    const { address, chainName, coin } = record;
    setRechargeInfo({
      address,
      chainName,
      coin
    });
    setIsRechargeModal(true);
  };

  return (
    <div>
      {/* Pro表格 */}
      <ProTable<columnsDataType>
        headerTitle={<TableTitle title="链账户管理" />}
        scroll={{ x: 1800 }}
        bordered
        // 表头
        columns={columns}
        actionRef={actionRef}
        // 传递额外参数
        params={{ urlWalletNo }}
        // 请求获取的数据
        request={async (params) => {
          console.log('ProTable request params:', params);
          // 优先使用搜索表单中的 walletNo，如果没有则使用 URL 中的
          const requestParams = {
            ...params,
            walletNo: params.walletNo || urlWalletNo || undefined,
          };
          console.log('Final request params:', requestParams);
          const res = await getWalletAccountPageList(requestParams);
          const result = {
            data: res.data || [],
            total: res.pagination?.totalSize || 0,
            success: res.code === 0,
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
        // 关联搜索表单
        formRef={searchFormRef}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
        }}
        toolBarRender={() => [
          <Button
            onClick={handleAdd}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            导入链账户
          </Button>,
          <Button
            onClick={handleCreate}
            key="button"
            icon={<PlusOutlined />}
            type="primary"
          >
            创建链账户
          </Button>,
        ]}
      />
      
      {/* 新增/编辑链账户模态框 */}
      <Modal
        title={
          isEditMode 
            ? '编辑链账户' 
            : operationMode === 'import' 
              ? '导入链账户' 
              : '创建链账户'
        }
        width={800}
        centered
        open={isModalVisible}
        onOk={handleSubmit}
        onCancel={handleCancel}
        okText="确认"
        cancelText="取消"
      >
        <Form
          name="walletAccountForm"
          form={form}
          labelCol={{ span: 6 }}
          wrapperCol={{ span: 16 }}
          initialValues={{ status: '1' }}
        >
          {/* 导入模式：显示地址、公钥、私钥字段 */}
          {(operationMode === 'import' || isEditMode) && (
            <>
              <Form.Item
                label="链地址"
                name="address"
                rules={[{ required: operationMode === 'import', message: '请输入链地址!' }]}
              >
                <Input placeholder="请输入链地址" />
              </Form.Item>
              
              <Form.Item
                label="签名公钥"
                name="pubKey"
                rules={[{ required: operationMode === 'import', message: '请输入签名公钥!' }]}
              >
                <Input placeholder="请输入签名公钥" />
              </Form.Item>
              
              <Form.Item
                label="签名私钥"
                name="privateKey"
                rules={[{ required: operationMode === 'import', message: '请输入签名私钥!' }]}
              >
                <Input.Password placeholder="请输入签名私钥" />
              </Form.Item>
            </>
          )}
          
          {/* 共同字段：链上货币 */}
          <Form.Item
            label="链上货币"
            name="chainCoinNo"
            rules={[{ required: true, message: '请选择链上货币!' }]}
          >
            <Select placeholder="请选择链上货币" disabled={isEditMode}>
              {chainCoinList.map((item: any) => (
                <Option key={item.serialNo} value={item.serialNo}>
                  {item.chainCoinName} ({item.coin} - {item.chainName})
                </Option>
              ))}
            </Select>
          </Form.Item>
          
          {/* 共同字段：钱包ID */}
          <Form.Item
            label="钱包ID"
            name="walletNo"
            rules={[{ required: true, message: '请选择钱包!' }]}
          >
            <Select placeholder="请选择钱包" disabled={isEditMode} showSearch optionFilterProp="children">
              {walletList.map((item: any) => (
                <Option key={item.serialNo} value={item.serialNo}>
                  {item.walletName} ({item.serialNo})
                </Option>
              ))}
            </Select>
          </Form.Item>
          
          {/* 编辑模式：显示账户状态和备注 */}
          {isEditMode && (
            <>
              <Form.Item
                label="账户状态"
                name="status"
                rules={[{ required: true, message: '请选择账户状态!' }]}
              >
                <Select placeholder="请选择账户状态">
                  <Option value="1">正常</Option>
                  <Option value="2">冻结</Option>
                </Select>
              </Form.Item>
              
              <Form.Item
                label="备注"
                name="remark"
              >
                <TextArea rows={3} placeholder="请输入备注信息" maxLength={500} />
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
      
      {/* 查看详情模态框 */}
      <Modal
        title="链账户详情"
        width={900}
        centered
        open={isViewModalVisible}
        onOk={() => setIsViewModalVisible(false)}
        onCancel={() => setIsViewModalVisible(false)}
        footer={[
          <Button key="close" type="primary" onClick={() => setIsViewModalVisible(false)}>
            关闭
          </Button>
        ]}
      >
        <Descriptions bordered column={2}>
          <Descriptions.Item label="账户ID" span={2}>
            {viewRecord?.serialNo}
          </Descriptions.Item>
          <Descriptions.Item label="钱包名称">
            {viewRecord?.walletName || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="钱包ID">
            {viewRecord?.walletNo}
          </Descriptions.Item>
          <Descriptions.Item label="链名">
            {viewRecord?.chainName}
          </Descriptions.Item>
          <Descriptions.Item label="币种">
            {viewRecord?.coin}
          </Descriptions.Item>
          <Descriptions.Item label="币种名称">
            {viewRecord?.chainCoinName || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="链上币Key">
            {viewRecord?.chainCoinKey || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="链地址" span={2}>
            {viewRecord?.address}
          </Descriptions.Item>
          <Descriptions.Item label="签名公钥" span={2}>
            {viewRecord?.pubKey}
          </Descriptions.Item>
          <Descriptions.Item label="签名私钥" span={2}>
            {viewRecord?.privateKey}
          </Descriptions.Item>
          <Descriptions.Item label="余额">
            {viewRecord?.balance || '0'}
          </Descriptions.Item>
          <Descriptions.Item label="冻结余额">
            {viewRecord?.freezeBalance || '0'}
          </Descriptions.Item>
          <Descriptions.Item label="账户状态">
            <Tag color={viewRecord?.status === '1' ? 'green' : 'red'}>
              {viewRecord?.status === '1' ? '正常' : '冻结'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="钱包类型">
            {viewRecord?.walletType === '1' ? '普通钱包' : viewRecord?.walletType === '2' ? '多签钱包' : 'EOA钱包'}
          </Descriptions.Item>
          <Descriptions.Item label="钱包状态">
            {viewRecord?.walletStatus === '1' ? '正常' : viewRecord?.walletStatus === '2' ? '冻结' : '注销'}
          </Descriptions.Item>
          <Descriptions.Item label="账户标签">
            {viewRecord?.walletTag === 'NONE' ? '无' : viewRecord?.walletTag === 'DEPOSIT' ? '寄存' : viewRecord?.walletTag || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="备注" span={2}>
            {viewRecord?.remark || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="创建者">
            {viewRecord?.createBy}
          </Descriptions.Item>
          <Descriptions.Item label="创建时间">
            {viewRecord?.createTime}
          </Descriptions.Item>
          <Descriptions.Item label="更新者">
            {viewRecord?.updateBy || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {viewRecord?.updateTime || '-'}
          </Descriptions.Item>
        </Descriptions>
      </Modal>

      {/* 充值二维码模态框 */}
      <Modal
        title="充值"
        centered
        open={isRechargeModal}
        onOk={() => setIsRechargeModal(false)}
        onCancel={() => setIsRechargeModal(false)}
        footer={[
          <Button key="ok" type="primary" onClick={() => setIsRechargeModal(false)}>
            确定
          </Button>,
        ]}
      >
        <div style={{ textAlign: 'center', padding: '20px' }}>
          <div style={{ marginBottom: '16px', fontSize: '14px', fontWeight: 'bold' }}>
            充值地址
          </div>
          <div style={{ marginBottom: '20px', wordBreak: 'break-all', fontSize: '14px', color: '#666' }}>
            {rechargeInfo.address}
          </div>
          <QRCode 
            value={rechargeInfo.address} 
            size={200}
            style={{ margin: '0 auto 20px' }}
          />
          <div style={{ 
            display: 'flex', 
            justifyContent: 'center', 
            alignItems: 'center',
            gap: '20px',
            fontSize: '14px'
          }}>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <span style={{ color: '#666', marginRight: '6px' }}>链名:</span>
              <span style={{ fontWeight: 'bold', color: '#1890ff' }}>{rechargeInfo.chainName}</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <span style={{ color: '#666', marginRight: '6px' }}>币种:</span>
              <span style={{ fontWeight: 'bold', color: '#52c41a' }}>{rechargeInfo.coin}</span>
            </div>
          </div>
        </div>
      </Modal>
    </div>
  );
};
