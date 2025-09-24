import React, { useState } from 'react';
import { Plus, Trash2, Copy, Save, Eye, EyeOff, ChevronDown, ChevronUp } from 'lucide-react';

const EventRuleConfigUI = () => {
    const [showJsonPreview, setShowJsonPreview] = useState(false);
    const [expandedSections, setExpandedSections] = useState({
        basic: true,
        rewards: true,
        conditions: true,
        limits: true
    });

    const [formData, setFormData] = useState({
        eventCode: 'USER_LOGIN',
        modelName: '登录奖励规则',
        modelType: '3',
        priority: 1,
        status: 1,
        rewards: [
            {
                assetType: 'POINTS',
                amountType: 'FIXED',
                amount: 10,
                description: '登录积分奖励'
            }
        ],
        conditions: [],
        limits: {
            dailyLimit: 1,
            totalLimit: 0,
            hourlyLimit: 0,
            monthlyLimit: 0
        },
        effectTime: {
            startTime: '',
            endTime: ''
        }
    });

    const eventOptions = [
        { value: 'USER_LOGIN', label: '用户登录' },
        { value: 'ORDER_COMPLETE', label: '完成订单' },
        { value: 'USER_SHARE', label: '用户分享' },
        { value: 'DAILY_CHECKIN', label: '每日签到' },
        { value: 'FIRST_ORDER', label: '首次下单' },
        { value: 'INVITE_FRIEND', label: '邀请好友' }
    ];

    const assetTypes = [
        { value: 'POINTS', label: '积分' },
        { value: 'COIN', label: '金币' },
        { value: 'EXP', label: '经验值' },
        { value: 'COUPON', label: '优惠券' }
    ];

    const amountTypes = [
        { value: 'FIXED', label: '固定数量' },
        { value: 'RANDOM', label: '随机区间' },
        { value: 'FORMULA', label: '公式计算' },
        { value: 'PERCENT', label: '按比例' }
    ];

    const operators = [
        { value: 'eq', label: '等于(=)' },
        { value: 'ne', label: '不等于(≠)' },
        { value: 'gt', label: '大于(>)' },
        { value: 'gte', label: '大于等于(≥)' },
        { value: 'lt', label: '小于(<)' },
        { value: 'lte', label: '小于等于(≤)' },
        { value: 'in', label: '包含' },
        { value: 'like', label: '模糊匹配' }
    ];

    const toggleSection = (section) => {
        setExpandedSections(prev => ({
            ...prev,
            [section]: !prev[section]
        }));
    };

    const addReward = () => {
        setFormData(prev => ({
            ...prev,
            rewards: [...prev.rewards, {
                assetType: 'POINTS',
                amountType: 'FIXED',
                amount: 0,
                description: ''
            }]
        }));
    };

    const removeReward = (index) => {
        setFormData(prev => ({
            ...prev,
            rewards: prev.rewards.filter((_, i) => i !== index)
        }));
    };

    const updateReward = (index, field, value) => {
        setFormData(prev => ({
            ...prev,
            rewards: prev.rewards.map((reward, i) =>
                i === index ? { ...reward, [field]: value } : reward
            )
        }));
    };

    const addCondition = () => {
        setFormData(prev => ({
            ...prev,
            conditions: [...prev.conditions, {
                field: '',
                operator: 'eq',
                value: '',
                description: ''
            }]
        }));
    };

    const removeCondition = (index) => {
        setFormData(prev => ({
            ...prev,
            conditions: prev.conditions.filter((_, i) => i !== index)
        }));
    };

    const updateCondition = (index, field, value) => {
        setFormData(prev => ({
            ...prev,
            conditions: prev.conditions.map((condition, i) =>
                i === index ? { ...condition, [field]: value } : condition
            )
        }));
    };

    const updateLimits = (field, value) => {
        setFormData(prev => ({
            ...prev,
            limits: { ...prev.limits, [field]: parseInt(value) || 0 }
        }));
    };

    const previewJSON = () => {
        const config = {
            rewards: formData.rewards,
            conditions: formData.conditions,
            limits: formData.limits,
            effectTime: formData.effectTime
        };
        return JSON.stringify(config, null, 2);
    };

    const SectionHeader = ({ title, section, children }) => (
        <div className="mb-6">
        <div
            className="flex items-center justify-between cursor-pointer bg-gray-50 p-4 rounded-lg hover:bg-gray-100 transition-colors"
    onClick={() => toggleSection(section)}
>
    <h2 className="text-xl font-semibold text-gray-800">{title}</h2>
    {expandedSections[section] ? <ChevronUp size={20} /> : <ChevronDown size={20} />}
    </div>
    {expandedSections[section] && (
        <div className="mt-4">
            {children}
            </div>
    )}
    </div>
);

    const renderRewardConfig = (reward, index) => (
        <div key={index} className="border border-gray-200 rounded-lg p-4 bg-white shadow-sm">
    <div className="flex justify-between items-center mb-4">
    <h4 className="font-medium text-gray-800">奖励 #{index + 1}</h4>
        <button
    onClick={() => removeReward(index)}
    className="text-red-500 hover:text-red-700 hover:bg-red-50 p-2 rounded-full transition-colors"
    >
    <Trash2 size={16} />
    </button>
    </div>

    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
    <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">资产类型</label>
        <select
    value={reward.assetType}
    onChange={(e) => updateReward(index, 'assetType', e.target.value)}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        >
        {assetTypes.map(type => (
                <option key={type.value} value={type.value}>{type.label}</option>
))}
    </select>
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">奖励类型</label>
        <select
    value={reward.amountType}
    onChange={(e) => updateReward(index, 'amountType', e.target.value)}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        >
        {amountTypes.map(type => (
                <option key={type.value} value={type.value}>{type.label}</option>
))}
    </select>
    </div>
    </div>

    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
        {reward.amountType === 'FIXED' && (
                <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">奖励数量</label>
                    <input
            type="number"
            value={reward.amount || ''}
            onChange={(e) => updateReward(index, 'amount', e.target.value)}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="请输入固定数量"
        />
        </div>
)}

    {reward.amountType === 'RANDOM' && (
        <>
            <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">最小值</label>
            <input
        type="number"
        value={reward.minAmount || ''}
        onChange={(e) => updateReward(index, 'minAmount', e.target.value)}
        className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
        </div>
        <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">最大值</label>
            <input
        type="number"
        value={reward.maxAmount || ''}
        onChange={(e) => updateReward(index, 'maxAmount', e.target.value)}
        className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
            </div>
            </>
    )}

    {reward.amountType === 'FORMULA' && (
        <div className="md:col-span-2">
        <label className="block text-sm font-medium text-gray-700 mb-2">计算公式</label>
            <input
        type="text"
        value={reward.formula || ''}
        onChange={(e) => updateReward(index, 'formula', e.target.value)}
        className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        placeholder="例：days * 5"
        />
        <p className="text-xs text-gray-500 mt-1">支持变量：days(天数)、amount(金额)等</p>
    </div>
    )}

    {reward.amountType === 'PERCENT' && (
        <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">比例(%)</label>
    <input
        type="number"
        step="0.01"
        value={reward.percent || ''}
        onChange={(e) => updateReward(index, 'percent', e.target.value)}
        className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        placeholder="例：1.5"
            />
            </div>
    )}

    <div className={reward.amountType === 'FORMULA' ? 'md:col-span-2' : ''}>
    <label className="block text-sm font-medium text-gray-700 mb-2">奖励描述</label>
        <input
    type="text"
    value={reward.description || ''}
    onChange={(e) => updateReward(index, 'description', e.target.value)}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="奖励说明"
        />
        </div>
        </div>
        </div>
);

    const renderConditionConfig = (condition, index) => (
        <div key={index} className="border border-gray-200 rounded-lg p-4 bg-white shadow-sm">
    <div className="flex justify-between items-center mb-4">
    <h4 className="font-medium text-gray-800">条件 #{index + 1}</h4>
        <button
    onClick={() => removeCondition(index)}
    className="text-red-500 hover:text-red-700 hover:bg-red-50 p-2 rounded-full transition-colors"
    >
    <Trash2 size={16} />
    </button>
    </div>

    <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
    <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">字段名</label>
        <input
    type="text"
    value={condition.field}
    onChange={(e) => updateCondition(index, 'field', e.target.value)}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="如：orderAmount"
    />
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">操作符</label>
        <select
    value={condition.operator}
    onChange={(e) => updateCondition(index, 'operator', e.target.value)}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        >
        {operators.map(op => (
                <option key={op.value} value={op.value}>{op.label}</option>
))}
    </select>
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">比较值</label>
        <input
    type="text"
    value={condition.value}
    onChange={(e) => updateCondition(index, 'value', e.target.value)}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="如：100"
    />
    </div>
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">条件描述</label>
        <input
    type="text"
    value={condition.description}
    onChange={(e) => updateCondition(index, 'description', e.target.value)}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="条件说明"
        />
        </div>
        </div>
);

    return (
        <div className="max-w-5xl mx-auto p-6 bg-gray-50 min-h-screen">
            {/* 页面标题 */}
            <div className="mb-8">
    <h1 className="text-3xl font-bold text-gray-900 mb-2">事件奖励规则配置</h1>
        <p className="text-gray-600">配置事件触发的奖励规则，支持多种奖励类型和触发条件</p>
    </div>

    {/* JSON预览切换按钮 */}
    <div className="mb-6 flex justify-end">
    <button
        onClick={() => setShowJsonPreview(!showJsonPreview)}
    className={`flex items-center gap-2 px-4 py-2 rounded-lg transition-colors ${
        showJsonPreview
            ? 'bg-blue-500 text-white hover:bg-blue-600'
            : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
    }`}
>
    {showJsonPreview ? <EyeOff size={16} /> : <Eye size={16} />}
    {showJsonPreview ? '隐藏' : '显示'} JSON预览
    </button>
    </div>

    {/* JSON预览面板 */}
    {showJsonPreview && (
        <div className="mb-6 bg-white rounded-lg shadow-sm border">
        <div className="flex justify-between items-center p-4 border-b">
        <h3 className="font-medium text-gray-800">JSON配置预览</h3>
            <button
        onClick={() => navigator.clipboard.writeText(previewJSON())}
        className="flex items-center gap-2 px-3 py-1 text-sm bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
        >
        <Copy size={14} />
        复制
        </button>
        </div>
        <div className="p-4">
    <div className="bg-gray-900 text-green-400 p-4 rounded-lg font-mono text-sm overflow-auto max-h-96">
        <pre>{previewJSON()}</pre>
        </div>
        </div>
        </div>
    )}

    {/* 基础配置 */}
    <SectionHeader title="基础配置" section="basic">
    <div className="bg-white p-6 rounded-lg shadow-sm border">
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
    <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">关联事件</label>
        <select
    value={formData.eventCode}
    onChange={(e) => setFormData(prev => ({ ...prev, eventCode: e.target.value }))}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        >
        {eventOptions.map(event => (
                <option key={event.value} value={event.value}>{event.label}</option>
))}
    </select>
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">模型名称</label>
        <input
    type="text"
    value={formData.modelName}
    onChange={(e) => setFormData(prev => ({ ...prev, modelName: e.target.value }))}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="请输入模型名称"
    />
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">优先级</label>
        <input
    type="number"
    value={formData.priority}
    onChange={(e) => setFormData(prev => ({ ...prev, priority: parseInt(e.target.value) }))}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="数字越大优先级越高"
    />
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">状态</label>
        <select
    value={formData.status}
    onChange={(e) => setFormData(prev => ({ ...prev, status: parseInt(e.target.value) }))}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    >
    <option value={1}>启用</option>
        <option value={0}>禁用</option>
    </select>
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">生效开始时间</label>
        <input
    type="datetime-local"
    value={formData.effectTime.startTime}
    onChange={(e) => setFormData(prev => ({
        ...prev,
        effectTime: { ...prev.effectTime, startTime: e.target.value }
    }))}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    />
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">生效结束时间</label>
        <input
    type="datetime-local"
    value={formData.effectTime.endTime}
    onChange={(e) => setFormData(prev => ({
        ...prev,
        effectTime: { ...prev.effectTime, endTime: e.target.value }
    }))}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
        </div>
        </div>
        </div>
        </SectionHeader>

    {/* 奖励配置 */}
    <SectionHeader title="奖励配置" section="rewards">
    <div className="space-y-4">
    <div className="flex justify-between items-center">
    <p className="text-gray-600">配置事件触发时给予用户的奖励</p>
        <button
    onClick={addReward}
    className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 flex items-center gap-2 transition-colors"
    >
    <Plus size={16} />
    添加奖励
    </button>
    </div>

    <div className="space-y-4">
        {formData.rewards.map((reward, index) => renderRewardConfig(reward, index))}
        </div>

    {formData.rewards.length === 0 && (
        <div className="text-center py-12 text-gray-500 bg-white rounded-lg border border-dashed border-gray-300">
        <p className="mb-4">暂未配置奖励规则</p>
            <button
        onClick={addReward}
        className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600"
            >
            添加第一个奖励
            </button>
            </div>
    )}
    </div>
    </SectionHeader>

    {/* 触发条件 */}
    <SectionHeader title="触发条件" section="conditions">
    <div className="space-y-4">
    <div className="bg-blue-50 p-4 rounded-lg border border-blue-200">
    <p className="text-sm text-blue-700">
        <strong>说明：</strong>多个条件之间为AND关系，即所有条件都必须满足才会触发奖励。如果不设置条件，则事件触发时直接发放奖励。
    </p>
    </div>

    <div className="flex justify-between items-center">
    <p className="text-gray-600">设置事件奖励的触发条件</p>
        <button
    onClick={addCondition}
    className="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 flex items-center gap-2 transition-colors"
    >
    <Plus size={16} />
    添加条件
    </button>
    </div>

    <div className="space-y-4">
        {formData.conditions.map((condition, index) => renderConditionConfig(condition, index))}
        </div>

    {formData.conditions.length === 0 && (
        <div className="text-center py-12 text-gray-500 bg-white rounded-lg border border-dashed border-gray-300">
        <p className="mb-2">暂未配置触发条件</p>
            <p className="text-sm mb-4">事件触发时将直接发放奖励</p>
        <button
        onClick={addCondition}
        className="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600"
            >
            添加触发条件
            </button>
            </div>
    )}
    </div>
    </SectionHeader>

    {/* 执行限制 */}
    <SectionHeader title="执行限制" section="limits">
    <div className="bg-white p-6 rounded-lg shadow-sm border">
    <div className="mb-4">
    <p className="text-gray-600">设置用户获得奖励的频次限制，防止过度获取</p>
    </div>

    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
    <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">每日限制次数</label>
        <input
    type="number"
    value={formData.limits.dailyLimit}
    onChange={(e) => updateLimits('dailyLimit', e.target.value)}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="0表示不限制"
    />
    <p className="text-xs text-gray-500 mt-1">用户每天最多获得奖励的次数</p>
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">每小时限制次数</label>
        <input
    type="number"
    value={formData.limits.hourlyLimit}
    onChange={(e) => updateLimits('hourlyLimit', e.target.value)}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="0表示不限制"
    />
    <p className="text-xs text-gray-500 mt-1">用户每小时最多获得奖励的次数</p>
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">每月限制次数</label>
        <input
    type="number"
    value={formData.limits.monthlyLimit}
    onChange={(e) => updateLimits('monthlyLimit', e.target.value)}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="0表示不限制"
    />
    <p className="text-xs text-gray-500 mt-1">用户每月最多获得奖励的次数</p>
    </div>

    <div>
    <label className="block text-sm font-medium text-gray-700 mb-2">总限制次数</label>
        <input
    type="number"
    value={formData.limits.totalLimit}
    onChange={(e) => updateLimits('totalLimit', e.target.value)}
    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
    placeholder="0表示不限制"
    />
    <p className="text-xs text-gray-500 mt-1">用户最多获得奖励的总次数</p>
        </div>
        </div>

        <div className="mt-6 bg-yellow-50 p-4 rounded-lg border border-yellow-200">
    <p className="text-sm text-yellow-700">
        <strong>提示：</strong>执行限制按优先级依次检查：小时限制 > 日限制 > 月限制 > 总限制。设置为0表示不限制该项。
    </p>
    </div>
    </div>
    </SectionHeader>

    {/* 底部操作按钮 */}
    <div className="flex justify-end space-x-4 mt-8 pt-6 border-t border-gray-200 bg-white -mx-6 px-6 py-4 rounded-lg">
    <button className="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors">
        取消
        </button>
        <button className="bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-600 flex items-center gap-2 transition-colors">
    <Save size={16} />
    保存配置
    </button>
    </div>
    </div>
);
};

export default EventRuleConfigUI;