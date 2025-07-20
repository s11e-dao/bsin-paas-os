import React, { useState, useEffect } from 'react';
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
    Card,
    List,
    Tabs,
    Avatar,
    Checkbox,
    Row,
    Col,
} from 'antd';
import type { UploadProps } from 'antd/es/upload/interface';
import { PlusOutlined, EditOutlined, EyeOutlined, DeleteOutlined, ReloadOutlined, UploadOutlined } from '@ant-design/icons';
import columnsData, { columnsDataType } from './data';
import {
    getPayInterfacePageList,
    addPayInterface,
    editPayInterface,
    deletePayInterface,
    getPayInterfaceDetail,
} from './service';
import { getPayWayList } from '../PayWay/service';
import { getSessionStorageInfo } from '../../../utils/localStorageInfo';

const { Meta } = Card;

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
    const [cardData, setCardData] = useState<columnsDataType[]>([]);
    const [loadingData, setLoadingData] = useState(false);

    // 分类数据
    const [enabledInterfaceList, setEnabledInterfaceList] = useState<columnsDataType[]>([]);
    const [disabledInterfaceList, setDisabledInterfaceList] = useState<columnsDataType[]>([]);
    const [interfaceTypeList, setInterfaceTypeList] = useState<any[]>([]);

    // 支付方式数据
    const [payWayList, setPayWayList] = useState<any[]>([]);
    const [selectedPayWays, setSelectedPayWays] = useState<string[]>([]);

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

    // 获取支付方式列表
    const fetchPayWayList = async () => {
        try {
            const res = await getPayWayList({});
            if (res?.data) {
                setPayWayList(res.data);
            }
        } catch (error) {
            console.error('获取支付方式列表失败:', error);
        }
    };

    // 获取卡片数据
    const fetchCardData = async () => {
        setLoadingData(true);
        try {
            const res = await getPayInterfacePageList({
                pagination: {
                    pageNum: 1,
                    pageSize: 100, // 获取更多数据用于卡片展示
                },
            });
            const data = res.data?.records || res.data || [];
            setCardData(data);

            // 按状态分类数据
            let enabledList: columnsDataType[] = [];
            let disabledList: columnsDataType[] = [];
            
            data.forEach((item: columnsDataType) => {
                if (item.status === 1) {
                    enabledList.push(item);
                } else {
                    disabledList.push(item);
                }
            });

            setEnabledInterfaceList(enabledList);
            setDisabledInterfaceList(disabledList);

            // 设置分类列表
            const typeList = [
                { id: 1, name: '已启用接口', copilotList: enabledList },
                { id: 2, name: '已停用接口', copilotList: disabledList },
            ];
            setInterfaceTypeList(typeList);
        } catch (error) {
            console.error('获取数据失败:', error);
            message.error('获取数据失败');
        } finally {
            setLoadingData(false);
        }
    };

    useEffect(() => {
        fetchCardData();
        fetchPayWayList();
    }, []);

    /**
     * 打开新增模态框
     */
    const handleAdd = () => {
        setModalTitle('添加支付接口');
        setIsEdit(false);
        setCurrentRecord({} as columnsDataType);
        setIconUrl('');
        setSelectedPayWays([]);
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
        // 解析已选择的支付方式
        const selectedWays = record.wayCode ? record.wayCode.split(',') : [];
        setSelectedPayWays(selectedWays);
        formRef.setFieldsValue({
            ...record,
            wayCode: selectedWays,
        });
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
                fetchCardData(); // 重新获取数据
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
                fetchCardData(); // 重新获取数据
            } else {
                message.error(res.message || '操作失败');
            }
        } catch (error) {
            message.error('操作失败');
        }
    };

    /**
     * 支付方式选择变更
     */
    const handlePayWayChange = (checkedValues: string[]) => {
        setSelectedPayWays(checkedValues);
        formRef.setFieldValue('wayCode', checkedValues);
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
                wayCode: selectedPayWays.join(','), // 使用选中的支付方式
            };
            const res = isEdit
                ? await editPayInterface({ ...requestData, payChannelCode: currentRecord.payChannelCode })
                : await addPayInterface(requestData);
            if (res.code === 0 || res.code === '000000') {
                message.success(isEdit ? '更新成功' : '添加成功');
                setIsModalVisible(false);
                fetchCardData(); // 重新获取数据
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
        setSelectedPayWays([]);
        formRef.resetFields();
    };

    return (
        <Card>
            {/* 工具栏 */}
            <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Space>
                    <Button
                        icon={<ReloadOutlined />}
                        onClick={fetchCardData}
                        loading={loadingData}
                    >
                        刷新
                    </Button>
                </Space>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={handleAdd}
                >
                    新建支付接口
                </Button>
            </div>

            {/* 页面标题和描述 */}
            <Descriptions title="支付接口管理">
                <Descriptions.Item>
                    管理支付接口购买渠道，支持多种支付方式的配置和管理
                </Descriptions.Item>
            </Descriptions>

            {/* 分类标签页 */}
            <Tabs defaultActiveKey="1">
                {interfaceTypeList.map((type) => (
                    <Tabs.TabPane tab={type.name} key={type.id}>
                        <Space
                            size="middle"
                            direction={'vertical'}
                            style={{ display: 'flex', flexWrap: 'wrap' }}
                        >
                            <List
                                loading={loadingData}
                                rowKey="payChannelCode"
                                grid={{
                                    gutter: 16,
                                    xs: 1,
                                    sm: 2,
                                    md: 2,
                                    lg: 3,
                                    xl: 4,
                                    xxl: 5,
                                }}
                                dataSource={type.copilotList}
                                renderItem={(item) => {
                                    if (item && item.payChannelCode) {
                                        return (
                                            <List.Item key={item.payChannelCode}>
                                                <Card
                                                    style={{ width: 300 }}
                                                    cover={
                                                        <div style={{ 
                                                            width: '100%', 
                                                            height: '200px',
                                                            display: 'flex',
                                                            alignItems: 'center',
                                                            justifyContent: 'center',
                                                            backgroundColor: '#f5f5f5',
                                                            borderRadius: '8px 8px 0 0'
                                                        }}>
                                                            {item.icon ? (
                                                                <img
                                                                    style={{ 
                                                                        width: '100%', 
                                                                        height: '100%',
                                                                        objectFit: 'contain',
                                                                        borderRadius: '8px 8px 0 0'
                                                                    }}
                                                                    alt={item.payChannelName}
                                                                    src={item.icon}
                                                                />
                                                            ) : (
                                                                <div style={{
                                                                    fontSize: 48,
                                                                    color: '#999'
                                                                }}>
                                                                    💰
                                                                </div>
                                                            )}
                                                        </div>
                                                    }
                                                    actions={[
                                                        <Tooltip title="查看详情">
                                                            <EyeOutlined
                                                                key="view"
                                                                onClick={() => handleView(item)}
                                                            />
                                                        </Tooltip>,
                                                        <Tooltip title="编辑">
                                                            <EditOutlined
                                                                key="edit"
                                                                onClick={() => handleEdit(item)}
                                                            />
                                                        </Tooltip>,
                                                        <Tooltip title={item.status === 1 ? '停用' : '启用'}>
                                                            <Switch
                                                                key="status"
                                                                checked={item.status === 1}
                                                                onChange={(checked) => handleStatusChange(item, checked)}
                                                                size="small"
                                                            />
                                                        </Tooltip>,
                                                        <Popconfirm
                                                            title="确定要删除这个支付接口吗？"
                                                            onConfirm={() => handleDelete(item)}
                                                            okText="确定"
                                                            cancelText="取消"
                                                        >
                                                            <Tooltip title="删除">
                                                                <DeleteOutlined
                                                                    key="delete"
                                                                    style={{ color: '#ff4d4f' }}
                                                                />
                                                            </Tooltip>
                                                        </Popconfirm>,
                                                    ]}
                                                >
                                                    <Meta
                                                        avatar={
                                                            <Avatar
                                                                style={{
                                                                    backgroundColor: item.status === 1 ? '#52c41a' : '#d9d9d9'
                                                                }}
                                                            >
                                                                {item.status === 1 ? '✓' : '✗'}
                                                            </Avatar>
                                                        }
                                                        title={item.payChannelName || '未命名接口'}
                                                        description={
                                                            <div>
                                                                <div>{item.payChannelCode}</div>
                                                                <div style={{ fontSize: 12, color: '#999', marginTop: 4 }}>
                                                                    {item.status === 1 ? '已启用' : '已停用'}
                                                                </div>
                                                            </div>
                                                        }
                                                    />
                                                </Card>
                                            </List.Item>
                                        );
                                    }
                                }}
                            />
                        </Space>
                    </Tabs.TabPane>
                ))}
            </Tabs>

            {/* 新增/编辑模态框 */}
            <Modal
                title={modalTitle}
                open={isModalVisible}
                onOk={handleSave}
                onCancel={handleCancel}
                confirmLoading={loading}
                width={800}
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
                        label="支持的支付方式"
                        name="wayCode"
                        rules={[{ required: true, message: '请选择支付方式!' }]}
                        tooltip="支持的支付方式列表"
                    >
                        <div style={{ maxHeight: 300, overflow: 'auto', border: '1px solid #d9d9d9', borderRadius: '6px', padding: '12px' }}>
                            <Checkbox.Group
                                value={selectedPayWays}
                                onChange={handlePayWayChange}
                                style={{ width: '100%' }}
                            >
                                <Row gutter={[16, 8]}>
                                    {payWayList.map((payWay) => (
                                        <Col span={8} key={payWay.payWayCode}>
                                            <Checkbox value={payWay.payWayCode}>
                                                {payWay.payWayName}
                                            </Checkbox>
                                        </Col>
                                    ))}
                                </Row>
                            </Checkbox.Group>
                        </div>
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
                    <Descriptions.Item label="支持的支付方式" span={2}>
                        <div style={{ maxHeight: 150, overflow: 'auto' }}>
                            {viewRecord.wayCode ? (
                                <Row gutter={[16, 8]}>
                                    {viewRecord.wayCode.split(',').map((wayCode, index) => {
                                        const payWay = payWayList.find(pw => pw.payWayCode === wayCode);
                                        return (
                                            <Col span={8} key={index}>
                                                <div style={{ 
                                                    padding: '4px 8px', 
                                                    backgroundColor: '#f5f5f5', 
                                                    borderRadius: '4px',
                                                    fontSize: '12px'
                                                }}>
                                                    {payWay ? payWay.payWayName : wayCode}
                                                </div>
                                            </Col>
                                        );
                                    })}
                                </Row>
                            ) : '-'}
                        </div>
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
        </Card>
    );
};

export default PayChannelInterface;