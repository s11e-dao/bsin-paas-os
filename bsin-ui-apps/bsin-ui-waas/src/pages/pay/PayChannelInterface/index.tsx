import React, { useState } from 'react';
import {
    Form,
    Input,
    Modal,
    message,
    Button,
    Select,
    Popconfirm,
    Divider,
    Descriptions,
    Switch,
    Tooltip,
    Space,
    Upload,
} from 'antd';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import type { UploadProps } from 'antd/es/upload/interface';
import ProTable from '@ant-design/pro-table';
import { PlusOutlined, EditOutlined, EyeOutlined, DeleteOutlined, ReloadOutlined, UploadOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
    getPayInterfacePageList,
    addPayInterface,
    editPayInterface,
    deletePayInterface,
    getPayInterfaceDetail,
} from './service';
import TableTitle from '../../../components/TableTitle';
import { getSessionStorageInfo } from '../../../utils/localStorageInfo';
const PayChannelInterface: React.FC = () => {
    const { TextArea } = Input;
    const { Option } = Select;

    // 文件上传相关配置
    const bsinFileUploadUrl = process.env.bsinFileUploadUrl;
    const tenantAppType = process.env.tenantAppType;

    // 控制模态框状态
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [isViewModalVisible, setIsViewModalVisible] = useState(false);
    const [modalTitle, setModalTitle] = useState('添加支付接口');
    const [isEdit, setIsEdit] = useState(false);
    const [loading, setLoading] = useState(false);

    // 数据状态
    const [viewRecord, setViewRecord] = useState<columnsDataType>({} as columnsDataType);
    const [currentRecord, setCurrentRecord] = useState<columnsDataType>({} as columnsDataType);
    const [iconUrl, setIconUrl] = useState<string>('');

    // 获取表单
    const [formRef] = Form.useForm();
    // 图片上传配置
    const uploadProps: UploadProps = {
        name: 'file',
        headers: {
            Authorization: getSessionStorageInfo('token')?.token,
        },
        action: bsinFileUploadUrl,
        data: {
            tenantAppType: tenantAppType,
            thumbnailSize: '100,100', // 缩略图尺寸
        },
        maxCount: 1,
        accept: 'image/*',
        onChange(info) {
            const { file } = info;
            if (file?.status === 'done') {
                console.log('file.response:', file.response);
                message.success(`${file.name} 图片上传成功`);
                const uploadedUrl = file?.response?.data?.url;
                setIconUrl(uploadedUrl);
                formRef.setFieldValue('icon', uploadedUrl);
            } else if (file?.status === 'error') {
                message.error(`${file.name} 图片上传失败`);
            }
        },
        onRemove() {
            setIconUrl('');
            formRef.setFieldValue('icon', '');
        },
        beforeUpload(file) {
            const isImage = file.type.startsWith('image/');
            if (!isImage) {
                message.error('只能上传图片文件!');
                return false;
            }
            const isLt2M = file.size / 1024 / 1024 < 2;
            if (!isLt2M) {
                message.error('图片大小不能超过2MB!');
                return false;
            }
            return true;
        },
    };
    /**
     * 以下内容为表格相关
     */
    // 表头数据
    const columns: ProColumns<columnsDataType>[] = columnsData;
    // 操作行数据 自定义操作行
    const actionRender = (text: any, record: columnsDataType, index: number) => (
        <Space key={record.payChannelCode}>
            <Tooltip title="查看详情">
                <a onClick={() => handleView(record)}>
                    <EyeOutlined />
                </a>
            </Tooltip>
            <Divider type="vertical" />
            <Tooltip title="编辑">
                <a onClick={() => handleEdit(record)}>
                    <EditOutlined />
                </a>
            </Tooltip>
            <Divider type="vertical" />
            <Tooltip title="启用/停用">
                <Switch
                    checked={record.status === 1}
                    onChange={(checked) => handleStatusChange(record, checked)}
                    size="small"
                />
            </Tooltip>
            <Divider type="vertical" />
            <Popconfirm
                title="确定要删除这个支付接口吗？"
                onConfirm={() => handleDelete(record)}
                okText="确定"
                cancelText="取消"
            >
                <Tooltip title="删除">
                    <a style={{ color: '#ff4d4f' }}>
                        <DeleteOutlined />
                    </a>
                </Tooltip>
            </Popconfirm>
        </Space>
    );
    // 自定义数据的表格头部数据
    columns.forEach((item: any) => {
        if (item.dataIndex === 'action') {
            item.render = actionRender;
        }
        if (item.dataIndex === 'icon') {
            item.render = (text: string) =>
                text ? (
                    <img src={text} alt="icon" style={{ width: 96, height: 96, objectFit: 'contain', borderRadius: '4px' }} />
                ) : (
                    <span>-</span>
                );
        }
    });
    // Table action 的引用，便于自定义触发
    const actionRef = React.useRef<ActionType>();
    /**
 * 打开新增模态框
 */
    const handleAdd = () => {
        setModalTitle('添加支付接口');
        setIsEdit(false);
        setCurrentRecord({} as columnsDataType);
        setIconUrl('');
        formRef.resetFields();
        setIsModalVisible(true);
    };
    /**
 * 打开编辑模态框
 */
    const handleEdit = (record: columnsDataType) => {
        setModalTitle('编辑支付接口');
        setIsEdit(true);
        setCurrentRecord(record);
        setIconUrl(record.icon || '');
        formRef.setFieldsValue(record);
        setIsModalVisible(true);
    };
    /**
     * 查看详情
     */
    const handleView = async (record: columnsDataType) => {
        try {
            const res = await getPayInterfaceDetail({ payChannelCode: record.payChannelCode });
            if (res?.data) {
                setViewRecord(res.data);
                setIsViewModalVisible(true);
            }
        } catch (error) {
            message.error('获取详情失败');
        }
    };
    /**
     * 删除接口
     */
    const handleDelete = async (record: columnsDataType) => {
        try {
            const res = await deletePayInterface({ payChannelCode: record.payChannelCode });
            if (res.code === 0 || res.code === '000000') {
                message.success('删除成功');
                actionRef.current?.reload();
            } else {
                message.error(res.message || '删除失败');
            }
        } catch (error) {
            message.error('删除失败');
        }
    };
    /**
     * 状态变更
     */
    const handleStatusChange = async (record: columnsDataType, checked: boolean) => {
        try {
            const res = await editPayInterface({
                payChannelCode: record.payChannelCode,
                status: checked ? 1 : 0,
            });
            if (res.code === 0 || res.code === '000000') {
                message.success(checked ? '启用成功' : '停用成功');
                actionRef.current?.reload();
            } else {
                message.error(res.message || '操作失败');
            }
        } catch (error) {
            message.error('操作失败');
        }
    };
    /**
 * 确认保存
 */
    const handleSave = async () => {
        try {
            await formRef.validateFields();
            setLoading(true);

            const values = formRef.getFieldsValue();
            const requestData = {
                ...values,
                icon: iconUrl, // 使用上传的图标URL
                wayCode: Array.isArray(values.wayCode) ? values.wayCode.join(',') : values.wayCode,
            };
            const res = isEdit
                ? await editPayInterface({ ...requestData, payChannelCode: currentRecord.payChannelCode })
                : await addPayInterface(requestData);
            if (res.code === 0 || res.code === '000000') {
                message.success(isEdit ? '更新成功' : '添加成功');
                setIsModalVisible(false);
                actionRef.current?.reload();
            } else {
                message.error(res.message || '操作失败');
            }
        } catch (error) {
            console.error('保存失败:', error);
        } finally {
            setLoading(false);
        }
    };
    /**
 * 取消操作
 */
    const handleCancel = () => {
        setIsModalVisible(false);
        setIconUrl('');
        formRef.resetFields();
    };
    return (
        <div>
            {/* Pro表格 */}
            <ProTable<columnsDataType>
                headerTitle={<TableTitle title="支付接口管理" />}
                scroll={{ x: 1400 }}
                bordered
                columns={columns}
                actionRef={actionRef}
                request={async (params) => {
                    try {
                        const res = await getPayInterfacePageList({
                            ...params,
                            pagination: {
                                pageNum: params.current,
                                pageSize: params.pageSize,
                            },
                        });
                        return {
                            data: res.data?.records || res.data || [],
                            total: res.data?.total || res.pagination?.totalSize || 0,
                        };
                    } catch (error) {
                        console.error('获取列表失败:', error);
                        return { data: [], total: 0 };
                    }
                }}
                rowKey="payChannelCode"
                search={{
                    labelWidth: 'auto',
                    collapsed: false,
                    collapseRender: (collapsed) => (collapsed ? '展开' : '收起'),
                }}
                form={{
                    ignoreRules: false,
                }}
                pagination={{
                    pageSize: 10,
                }}
                toolBarRender={() => [
                    <Button
                        key="refresh"
                        icon={<ReloadOutlined />}
                        onClick={() => actionRef.current?.reload()}
                    >
                        刷新
                    </Button>,
                    <Button
                        key="add"
                        type="primary"
                        icon={<PlusOutlined />}
                        onClick={handleAdd}
                    >
                        添加接口
                    </Button>,
                ]}
            />
            {/* 新增/编辑模态框 */}
            <Modal
                title={modalTitle}
                open={isModalVisible}
                onOk={handleSave}
                onCancel={handleCancel}
                confirmLoading={loading}
                width={600}
                destroyOnClose
            >
                <Form
                    form={formRef}
                    labelCol={{ span: 6 }}
                    wrapperCol={{ span: 16 }}
                    layout="horizontal"
                >
                    <Form.Item
                        label="接口代码"
                        name="payChannelCode"
                        rules={[
                            { required: true, message: '请输入接口代码!' },
                            { max: 32, message: '接口代码不能超过32个字符!' },
                        ]}
                        tooltip="支付接口的唯一标识代码，如：wxpay、alipay"
                    >
                        <Input
                            placeholder="请输入接口代码"
                            disabled={isEdit}
                        />
                    </Form.Item>
                    <Form.Item
                        label="接口名称"
                        name="payChannelName"
                        rules={[
                            { required: true, message: '请输入接口名称!' },
                            { max: 50, message: '接口名称不能超过50个字符!' },
                        ]}
                        tooltip="支付接口的显示名称"
                    >
                        <Input placeholder="请输入接口名称" />
                    </Form.Item>
                    <Form.Item
                        label="配置页面类型"
                        name="configPageType"
                        rules={[{ required: true, message: '请选择配置页面类型!' }]}
                        tooltip="支付参数配置页面的渲染方式"
                    >
                        <Select placeholder="请选择配置页面类型">
                            <Option value={1}>JSON渲染</Option>
                            <Option value={2}>自定义</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item
                        label="支付参数"
                        name="params"
                        tooltip="支付接口的参数配置定义，JSON格式"
                    >
                        <TextArea
                            rows={4}
                            placeholder="请输入JSON格式的支付参数配置"
                        />
                    </Form.Item>
                    <Form.Item
                        label="支付方式"
                        name="wayCode"
                        rules={[{ required: true, message: '请选择支付方式!' }]}
                        tooltip="支持的支付方式列表"
                    >
                        <Select
                            mode="multiple"
                            placeholder="请选择支付方式"
                            style={{ width: '100%' }}
                        >
                            <Option value="wxpay_jsapi">微信JSAPI支付</Option>
                            <Option value="wxpay_h5">微信H5支付</Option>
                            <Option value="wxpay_app">微信APP支付</Option>
                            <Option value="wxpay_bar">微信条码支付</Option>
                            <Option value="alipay_jsapi">支付宝JSAPI支付</Option>
                            <Option value="alipay_h5">支付宝H5支付</Option>
                            <Option value="alipay_app">支付宝APP支付</Option>
                            <Option value="alipay_bar">支付宝条码支付</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item
                        label="图标"
                        name="icon"
                        tooltip="页面展示的卡片图标"
                    >
                        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                            <Upload {...uploadProps} listType="picture-card" showUploadList={false}>
                                {iconUrl ? (
                                    <div style={{ position: 'relative' }}>
                                        <img
                                            src={iconUrl}
                                            alt="图标"
                                            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                        />
                                        <div style={{
                                            position: 'absolute',
                                            top: 0,
                                            left: 0,
                                            right: 0,
                                            bottom: 0,
                                            background: 'rgba(0,0,0,0.5)',
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            opacity: 0,
                                            transition: 'opacity 0.3s'
                                        }} className="upload-hover">
                                            <UploadOutlined style={{ color: 'white', fontSize: 20 }} />
                                        </div>
                                    </div>
                                ) : (
                                    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
                                        <UploadOutlined style={{ fontSize: 24, color: '#999' }} />
                                        <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>上传图标</div>
                                    </div>
                                )}
                            </Upload>
                            {iconUrl && (
                                <Button
                                    type="link"
                                    danger
                                    onClick={() => {
                                        setIconUrl('');
                                        formRef.setFieldValue('icon', '');
                                    }}
                                >
                                    删除
                                </Button>
                            )}
                        </div>
                    </Form.Item>
                    <Form.Item
                        label="状态"
                        name="status"
                        initialValue={1}
                        tooltip="接口的启用状态"
                    >
                        <Select>
                            <Option value={1}>启用</Option>
                            <Option value={0}>停用</Option>
                        </Select>
                    </Form.Item>
                    <Form.Item
                        label="备注"
                        name="remark"
                        tooltip="接口的备注信息"
                    >
                        <TextArea
                            rows={3}
                            placeholder="请输入备注信息"
                        />
                    </Form.Item>
                </Form>
            </Modal>
            {/* 查看详情模态框 */}
            <Modal
                title="支付接口详情"
                open={isViewModalVisible}
                onCancel={() => setIsViewModalVisible(false)}
                footer={[
                    <Button key="close" onClick={() => setIsViewModalVisible(false)}>
                        关闭
                    </Button>,
                ]}
                width={800}
            >
                <Descriptions bordered column={2}>
                    <Descriptions.Item label="接口代码" span={1}>
                        {viewRecord.payChannelCode}
                    </Descriptions.Item>
                    <Descriptions.Item label="接口名称" span={1}>
                        {viewRecord.payChannelName}
                    </Descriptions.Item>
                    <Descriptions.Item label="配置页面类型" span={1}>
                        {viewRecord.configPageType === 1 ? 'JSON渲染' : viewRecord.configPageType === 2 ? '自定义' : '-'}
                    </Descriptions.Item>
                    <Descriptions.Item label="状态" span={1}>
                        {viewRecord.status === 1 ? '启用' : '停用'}
                    </Descriptions.Item>
                    <Descriptions.Item label="支付参数" span={2}>
                        <div style={{ maxHeight: 100, overflow: 'auto' }}>
                            {viewRecord.params || '-'}
                        </div>
                    </Descriptions.Item>
                    <Descriptions.Item label="支付方式" span={2}>
                        {viewRecord.wayCode || '-'}
                    </Descriptions.Item>
                    <Descriptions.Item label="图标" span={1}>
                        {viewRecord.icon ? (
                            <img 
                                src={viewRecord.icon} 
                                alt="图标" 
                                style={{ width: 64, height: 64, objectFit: 'contain', borderRadius: '4px' }} 
                            />
                        ) : (
                            '-'
                        )}
                    </Descriptions.Item>
                    <Descriptions.Item label="租户ID" span={1}>
                        {viewRecord.tenantId || '-'}
                    </Descriptions.Item>
                    <Descriptions.Item label="备注" span={2}>
                        {viewRecord.remark || '-'}
                    </Descriptions.Item>
                    <Descriptions.Item label="创建时间" span={1}>
                        {viewRecord.createTime || '-'}
                    </Descriptions.Item>
                    <Descriptions.Item label="更新时间" span={1}>
                        {viewRecord.updateTime || '-'}
                    </Descriptions.Item>
                </Descriptions>
            </Modal>
        </div>
    );
};

export default PayChannelInterface;
