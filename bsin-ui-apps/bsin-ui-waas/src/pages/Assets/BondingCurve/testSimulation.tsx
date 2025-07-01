import React from 'react';
import { Card, Button, message } from 'antd';
import {
    SimulationParams,
    calculateBondingCurveSimulation,
    validateSimulationParams,
    getRecommendedParams,
    getCompleteRecommendedParams,
    optimizeSimulationParams,
    recalculateParamsBasedOnChange,
    validateParamChange,
} from './simulationUtils';

const TestSimulation: React.FC = () => {
    const testParams: SimulationParams = {
        totalTarget: 21000000,
        levelWidth: 2100000,
        decayFactor: 0.9975,
        estimatedLaborValue: 105000000,
    };

    const handleTest = () => {
        try {
            // 测试参数验证
            const validation = validateSimulationParams(testParams);
            console.log('参数验证结果:', validation);

            // 测试基础推荐参数计算（包含衰减系数）
            const basicRecommendations = getRecommendedParams({
                totalTarget: 21000000,
                estimatedLaborValue: 105000000,
                // 不提供衰减系数，测试自动计算
            });
            console.log('基础推荐参数（包含衰减系数）:', basicRecommendations);

            // 测试完整推荐参数计算（包含优化）
            const completeRecommendations = getCompleteRecommendedParams({
                totalTarget: 21000000,
                estimatedLaborValue: 105000000,
                // 不提供衰减系数，测试自动计算和优化
            });
            console.log('完整推荐参数（包含衰减系数优化）:', completeRecommendations);

            // 测试参数优化
            const optimizations = optimizeSimulationParams(testParams);
            console.log('参数优化结果（包含衰减系数）:', optimizations);

            // 测试动态重新计算功能
            console.log('=== 测试动态重新计算功能 ===');
            
            // 测试修改总积分目标
            const recalcForTotalTarget = recalculateParamsBasedOnChange('totalTarget', 25000000, testParams);
            console.log('修改总积分目标后的重新计算:', recalcForTotalTarget);
            
            // 测试修改预估劳动价值
            const recalcForLaborValue = recalculateParamsBasedOnChange('estimatedLaborValue', 120000000, testParams);
            console.log('修改预估劳动价值后的重新计算:', recalcForLaborValue);
            
            // 测试修改衰减系数
            const recalcForDecayFactor = recalculateParamsBasedOnChange('decayFactor', 0.998, testParams);
            console.log('修改衰减系数后的重新计算:', recalcForDecayFactor);

            // 测试参数修改验证
            const changeValidation = validateParamChange('decayFactor', 0.998, testParams);
            console.log('参数修改验证结果:', changeValidation);

            // 测试仿真计算
            const result = calculateBondingCurveSimulation(testParams);
            console.log('仿真结果:', result);
            console.log('积分释放达成率:', result.summary.targetAchievement);
            console.log('劳动价值达成率:', result.summary.laborValueAchievement);

            message.success('测试完成，请查看控制台输出');
        } catch (error) {
            console.error('测试失败:', error);
            message.error('测试失败');
        }
    };

    return (
        <Card title="仿真功能测试" style={{ margin: '20px' }}>
            <Button type="primary" onClick={handleTest}>
                运行测试
            </Button>
            <p style={{ marginTop: '10px', color: '#666' }}>
                点击按钮测试仿真功能，包括参数验证、推荐计算、优化功能、动态重新计算和仿真计算，结果将输出到控制台
            </p>
        </Card>
    );
};

export default TestSimulation; 