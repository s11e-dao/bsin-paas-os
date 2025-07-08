import { 
  calculateBondingCurveSimulation, 
  calculateFirstLevelReward,
  calculateTotalTarget,
  SimulationParams 
} from './simulationUtils';

// 测试函数
export const testOptimizationLogic = () => {
    console.log('=== 测试优化逻辑 ===');
    
    // 测试参数
    const testParams: SimulationParams = {
        totalTargetToken: 21000000,
        levelWidth: 2100000,
        decayFactor: 0.9975,
        estimatedLaborValue: 105000000,
    };
    
    console.log('原始参数:', testParams);
    
    // 计算当前结果
    const currentResult = calculateBondingCurveSimulation(testParams);
    console.log('当前结果:', {
        targetAchievement: currentResult.summary.targetAchievement,
        totalReward: currentResult.summary.totalReward,
        firstLevelReward: currentResult.summary.firstLevelReward
    });
    
    // 测试理论公式
    const currentFirstLevelReward = calculateFirstLevelReward(testParams.totalTargetToken, testParams.decayFactor);
    console.log('当前首档奖励:', currentFirstLevelReward);
    
    // 理论衰减系数计算
    const theoreticalDecayFactor = 1 - (currentFirstLevelReward / testParams.totalTargetToken);
    console.log('理论衰减系数:', theoreticalDecayFactor);
    
    // 验证理论公式
    const theoreticalTotal = calculateTotalTarget(currentFirstLevelReward, theoreticalDecayFactor);
    console.log('理论总积分:', theoreticalTotal);
    console.log('理论达成率:', (theoreticalTotal / testParams.totalTargetToken) * 100);
    
    // 测试理论衰减系数的实际效果
    const testParamsWithTheoretical = {
        ...testParams,
        decayFactor: theoreticalDecayFactor,
        firstLevelReward: currentFirstLevelReward
    };
    
    const theoreticalResult = calculateBondingCurveSimulation(testParamsWithTheoretical);
    console.log('理论衰减系数结果:', {
        targetAchievement: theoreticalResult.summary.targetAchievement,
        totalReward: theoreticalResult.summary.totalReward,
        firstLevelReward: theoreticalResult.summary.firstLevelReward
    });
    
    // 分析问题
    console.log('\n=== 问题分析 ===');
    console.log('问题1: 理论公式可能不正确');
    console.log('问题2: 有限档期 vs 无限档期的差异');
    console.log('问题3: 档位宽度和档位总数的影响');
    
    // 测试不同衰减系数的效果
    console.log('\n=== 衰减系数测试 ===');
    for (let k = 0.99; k <= 0.9999; k += 0.001) {
        const testParamsK = {
            ...testParams,
            decayFactor: k,
            firstLevelReward: calculateFirstLevelReward(testParams.totalTargetToken, k)
        };
        const resultK = calculateBondingCurveSimulation(testParamsK);
        console.log(`衰减系数 ${k.toFixed(4)}: 达成率 ${resultK.summary.targetAchievement.toFixed(2)}%, 档位数 ${resultK.summary.totalLevels}`);
        
        if (Math.abs(resultK.summary.targetAchievement - 100) < 1) {
            console.log(`找到接近100%的衰减系数: ${k.toFixed(4)}`);
            break;
        }
    }
    
    // 测试档位数量的影响
    console.log('\n=== 档位数量测试 ===');
    const testDecayFactor = 0.99; // 使用一个较小的衰减系数
    for (let levels = 10; levels <= 200; levels += 10) {
        const testParamsLevels = {
            ...testParams,
            decayFactor: testDecayFactor,
            firstLevelReward: calculateFirstLevelReward(testParams.totalTargetToken, testDecayFactor),
            totalLevels: levels
        };
        const resultLevels = calculateBondingCurveSimulation(testParamsLevels);
        console.log(`档位数 ${levels}: 达成率 ${resultLevels.summary.targetAchievement.toFixed(2)}%`);
        
        if (resultLevels.summary.targetAchievement >= 99) {
            console.log(`找到达到99%的档位数: ${levels}`);
            break;
        }
    }
};

// 导出测试函数
export default testOptimizationLogic; 