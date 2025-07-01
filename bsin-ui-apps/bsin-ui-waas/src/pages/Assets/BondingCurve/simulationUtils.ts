import { DataPoint } from './lindeChart';

// 仿真参数接口
export interface SimulationParams {
    totalTarget: number;      // 总积分目标 T
    levelWidth: number;       // 档位宽度 δC
    decayFactor: number;      // 衰减系数 k
    estimatedLaborValue: number; // 预估捕获劳动价值 Tv
    firstLevelReward?: number;   // 首档奖励 R₀ (可选，可自动计算)
    totalLevels?: number;        // 档位总数 (可选，可自动计算)
}

// 档位数据接口
export interface LevelData {
    level: number;            // 档位序号
    reward: number;           // 档期奖励
    cumulativeReward: number; // 累计奖励
    laborValue: number;       // 劳动价值（档位宽度 * 档位序号）
    cumulativeLaborValue: number; // 累计劳动价值
}

// 仿真结果接口
export interface SimulationResult {
    levels: LevelData[];
    chartData: DataPoint[];
    summary: {
        firstLevelReward: number;
        totalLevels: number;
        totalReward: number;
        targetAchievement: number;
        totalLaborValue: number;
        laborValueAchievement: number;
    };
    calculatedParams: {
        calculatedLevelWidth?: number;
        calculatedTotalLevels?: number;
        calculatedFirstLevelReward?: number;
        calculatedDecayFactor?: number;
    };
}

/**
 * 根据劳动价值和档位总数计算档位宽度
 * @param estimatedLaborValue 预估劳动价值
 * @param totalLevels 档位总数
 * @returns 档位宽度
 */
export const calculateLevelWidth = (estimatedLaborValue: number, totalLevels: number): number => {
    return estimatedLaborValue / totalLevels;
};

/**
 * 根据劳动价值和档位宽度计算档位总数
 * @param estimatedLaborValue 预估劳动价值
 * @param levelWidth 档位宽度
 * @returns 档位总数
 */
export const calculateTotalLevels = (estimatedLaborValue: number, levelWidth: number): number => {
    return Math.ceil(estimatedLaborValue / levelWidth);
};

/**
 * 根据总积分目标和衰减系数计算首档奖励
 * @param totalTarget 总积分目标
 * @param decayFactor 衰减系数
 * @returns 首档奖励
 */
export const calculateFirstLevelReward = (totalTarget: number, decayFactor: number): number => {
    return totalTarget * (1 - decayFactor);
};

/**
 * 根据首档奖励和衰减系数计算总积分目标
 * @param firstLevelReward 首档奖励
 * @param decayFactor 衰减系数
 * @returns 总积分目标
 */
export const calculateTotalTarget = (firstLevelReward: number, decayFactor: number): number => {
    return firstLevelReward / (1 - decayFactor);
};

/**
 * 计算联合曲线仿真
 * @param params 仿真参数
 * @returns 仿真结果
 */
export const calculateBondingCurveSimulation = (params: SimulationParams): SimulationResult => {
    const {
        totalTarget,    // 总积分目标 T
        levelWidth,     // 档位宽度 δC
        decayFactor,    // 衰减系数 k
        estimatedLaborValue,  // 预估捕获劳动价值 Tv
        firstLevelReward: userFirstLevelReward,   // 首档奖励 R₀ (可选，可自动计算)
        totalLevels: userTotalLevels             // 档位总数 (可选，可自动计算)
    } = params;

    // 计算或使用用户提供的首档奖励
    const actualFirstLevelReward = userFirstLevelReward || calculateFirstLevelReward(totalTarget, decayFactor);

    // 计算或使用用户提供的档位总数
    const actualTotalLevels = userTotalLevels || calculateTotalLevels(estimatedLaborValue, levelWidth);

    // 计算或使用用户提供的档位宽度
    const actualLevelWidth = levelWidth || calculateLevelWidth(estimatedLaborValue, actualTotalLevels);

    const levels: LevelData[] = [];
    const chartData: DataPoint[] = [];

    let cumulativeReward = 0;   // 累计积分释放奖励
    let cumulativeLaborValue = 0;  // 累计劳动价值
    let level = 1;  // 档位序号

    // 计算档位数据
    while (level <= actualTotalLevels && cumulativeReward < totalTarget * 0.999) {
        // 档期奖励：Rn = R₀ * k^(n-1)
        const reward = actualFirstLevelReward * Math.pow(decayFactor, level - 1);
        cumulativeReward += reward;

        // 劳动价值 = 档位宽度 * 档位序号
        const laborValue = actualLevelWidth * level;
        cumulativeLaborValue = laborValue;

        const levelInfo: LevelData = {
            level,
            reward,
            cumulativeReward,
            laborValue,
            cumulativeLaborValue,
        };

        levels.push(levelInfo);

        // 为图表添加数据点
        chartData.push({
            supply: laborValue,
            price: reward,
            series: '档期奖励',
        });

        chartData.push({
            supply: laborValue,
            price: cumulativeReward,
            series: '累计奖励',
        });

        chartData.push({
            supply: laborValue,
            price: cumulativeLaborValue,
            series: '累计劳动价值',
        });

        level++;
    }

    // 计算汇总信息
    const summary = {
        firstLevelReward: actualFirstLevelReward,
        totalLevels: levels.length,
        totalReward: cumulativeReward,
        targetAchievement: (cumulativeReward / totalTarget) * 100,  // 积分释放达成率
        totalLaborValue: cumulativeLaborValue,
        laborValueAchievement: (cumulativeLaborValue / estimatedLaborValue) * 100,  // 劳动价值达成率
    };

    // 计算推导的参数
    const calculatedParams = {
        calculatedLevelWidth: !levelWidth ? actualLevelWidth : undefined,
        calculatedTotalLevels: !userTotalLevels ? actualTotalLevels : undefined,
        calculatedFirstLevelReward: !userFirstLevelReward ? actualFirstLevelReward : undefined,
        calculatedDecayFactor: undefined, // 衰减系数通常由用户提供，暂不自动计算
    };

    return {
        levels,
        chartData,
        summary,
        calculatedParams,
    };
};

/**
 * 验证仿真参数
 * @param params 仿真参数
 * @returns 验证结果
 */
export const validateSimulationParams = (params: SimulationParams): { valid: boolean; errors: string[] } => {
    const errors: string[] = [];

    if (params.totalTarget <= 0) {
        errors.push('总积分目标必须大于0');
    }

    if (params.estimatedLaborValue <= 0) {
        errors.push('预估劳动价值必须大于0');
    }

    if (params.decayFactor <= 0 || params.decayFactor >= 1) {
        errors.push('衰减系数必须在0到1之间');
    }

    // 验证档位宽度
    if (params.levelWidth && params.levelWidth <= 0) {
        errors.push('档位宽度必须大于0');
    }

    // 验证首档奖励
    if (params.firstLevelReward && params.firstLevelReward <= 0) {
        errors.push('首档奖励必须大于0');
    }

    // 验证档位总数
    if (params.totalLevels && params.totalLevels <= 0) {
        errors.push('档位总数必须大于0');
    }

    // 验证参数组合的合理性
    if (params.levelWidth && params.totalLevels) {
        const calculatedLaborValue = params.levelWidth * params.totalLevels;
        if (Math.abs(calculatedLaborValue - params.estimatedLaborValue) / params.estimatedLaborValue > 0.1) {
            errors.push('档位宽度与档位总数的乘积应与预估劳动价值相近');
        }
    }

    return {
        valid: errors.length === 0,
        errors,
    };
};

/**
 * 格式化数字显示
 * @param value 数值
 * @returns 格式化后的字符串
 */
export const formatNumber = (value: number): string => {
    return value.toLocaleString();
};

/**
 * 计算理论总积分（无限档期）
 * @param params 仿真参数
 * @returns 理论釋放总积分
 */
export const calculateTheoreticalTotal = (params: SimulationParams): number => {
    const { totalTarget, decayFactor } = params;
    // 理论釋放总积分 = T / (1 - k)
    return totalTarget / (1 - decayFactor);
};

/**
 * 获取参数推荐值 - 优化积分释放达成率和劳动价值达成率
 * @param params 当前参数
 * @returns 推荐参数
 */
export const getRecommendedParams = (params: Partial<SimulationParams>): Partial<SimulationParams> => {
    const recommendations: Partial<SimulationParams> = {};

    // 如果缺少档位宽度，根据劳动价值和档位总数计算
    if (!params.levelWidth && params.estimatedLaborValue && params.totalLevels) {
        recommendations.levelWidth = calculateLevelWidth(params.estimatedLaborValue, params.totalLevels);
    }

    // 如果缺少档位总数，根据劳动价值和档位宽度计算
    if (!params.totalLevels && params.estimatedLaborValue && params.levelWidth) {
        recommendations.totalLevels = calculateTotalLevels(params.estimatedLaborValue, params.levelWidth);
    }

    // 如果缺少首档奖励，根据总积分目标和衰减系数计算
    if (!params.firstLevelReward && params.totalTarget && params.decayFactor) {
        recommendations.firstLevelReward = calculateFirstLevelReward(params.totalTarget, params.decayFactor);
    }

    // 如果缺少衰减系数，根据总积分目标和首档奖励计算
    if (!params.decayFactor && params.totalTarget && params.firstLevelReward) {
        recommendations.decayFactor = 1 - (params.firstLevelReward / params.totalTarget);
    }

    // 智能优化：如果同时缺少档位宽度和档位总数，计算最优组合
    if (!params.levelWidth && !params.totalLevels && params.estimatedLaborValue) {
        // 推荐档位总数在20-100之间，优先选择50
        const recommendedTotalLevels = 50;
        recommendations.totalLevels = recommendedTotalLevels;
        recommendations.levelWidth = calculateLevelWidth(params.estimatedLaborValue, recommendedTotalLevels);
    }

    // 智能优化：如果同时缺少衰减系数和首档奖励，计算最优组合
    if (!params.decayFactor && !params.firstLevelReward && params.totalTarget) {
        // 推荐衰减系数在0.99-0.999之间，优先选择0.9975
        const recommendedDecayFactor = 0.9975;
        recommendations.decayFactor = recommendedDecayFactor;
        recommendations.firstLevelReward = calculateFirstLevelReward(params.totalTarget, recommendedDecayFactor);
    }

    // 如果只缺少衰减系数，根据总积分目标计算推荐值
    if (!params.decayFactor && params.totalTarget) {
        // 基于积分释放达成率优化的衰减系数推荐
        const recommendedDecayFactor = 0.9975; // 默认推荐值
        recommendations.decayFactor = recommendedDecayFactor;
    }

    return recommendations;
};

/**
 * 智能优化参数 - 使积分释放达成率和劳动价值达成率都接近100%
 * @param params 当前参数
 * @returns 优化后的参数
 */
export const optimizeSimulationParams = (params: SimulationParams): Partial<SimulationParams> => {
    const optimizations: Partial<SimulationParams> = {};

    // 计算当前参数下的仿真结果
    const currentResult = calculateBondingCurveSimulation(params);
    const currentTargetAchievement = currentResult.summary.targetAchievement;
    const currentLaborValueAchievement = currentResult.summary.laborValueAchievement;

    // 优化积分释放达成率
    if (currentTargetAchievement < 99.5) {
        // 如果积分释放达成率过低，调整衰减系数
        // 衰减系数越小，积分释放达成率越高
        const targetDecayFactor = Math.max(0.99, params.decayFactor - 0.001);
        optimizations.decayFactor = targetDecayFactor;

        // 重新计算首档奖励
        optimizations.firstLevelReward = calculateFirstLevelReward(params.totalTarget, targetDecayFactor);
    }

    // 优化劳动价值达成率
    if (currentLaborValueAchievement < 99.5) {
        // 如果劳动价值达成率过低，调整档位宽度
        const currentLevelWidth = params.levelWidth || calculateLevelWidth(params.estimatedLaborValue, params.totalLevels || 50);
        const targetLevelWidth = currentLevelWidth * (100 / currentLaborValueAchievement);
        optimizations.levelWidth = targetLevelWidth;

        // 重新计算档位总数
        optimizations.totalLevels = calculateTotalLevels(params.estimatedLaborValue, targetLevelWidth);
    }

    // 平衡优化：确保两个达成率都接近100%
    if (currentTargetAchievement < 99.5 || currentLaborValueAchievement < 99.5) {
        // 综合优化策略
        const currentLevelWidth = params.levelWidth || calculateLevelWidth(params.estimatedLaborValue, params.totalLevels || 50);
        
        // 基于积分释放达成率优化衰减系数
        let targetDecayFactor = params.decayFactor;
        if (currentTargetAchievement < 99.5) {
            // 积分释放达成率过低时，减小衰减系数
            targetDecayFactor = Math.max(0.99, Math.min(0.999, params.decayFactor * (currentTargetAchievement / 100)));
        }
        
        // 基于劳动价值达成率优化档位宽度
        const targetLevelWidth = currentLevelWidth * (100 / currentLaborValueAchievement);

        optimizations.decayFactor = targetDecayFactor;
        optimizations.levelWidth = targetLevelWidth;
        optimizations.firstLevelReward = calculateFirstLevelReward(params.totalTarget, targetDecayFactor);
        optimizations.totalLevels = calculateTotalLevels(params.estimatedLaborValue, targetLevelWidth);
    }

    return optimizations;
};

/**
 * 获取完整的推荐参数（包含优化）
 * @param params 当前参数
 * @returns 完整的推荐参数
 */
export const getCompleteRecommendedParams = (params: Partial<SimulationParams>): Partial<SimulationParams> => {
    // 首先获取基础推荐参数
    const basicRecommendations = getRecommendedParams(params);

    // 合并当前参数和基础推荐
    const completeParams = { ...params, ...basicRecommendations };

    // 如果所有必要参数都已具备，进行优化
    if (completeParams.totalTarget && completeParams.estimatedLaborValue &&
        completeParams.decayFactor && completeParams.levelWidth &&
        completeParams.totalLevels && completeParams.firstLevelReward) {

        const optimizations = optimizeSimulationParams(completeParams as SimulationParams);
        return { ...completeParams, ...optimizations };
    }

    return basicRecommendations;
};

/**
 * 根据用户修改的参数重新计算其他参数的推荐值
 * @param changedParam 用户修改的参数名
 * @param changedValue 用户修改的参数值
 * @param currentParams 当前所有参数
 * @returns 重新计算的推荐参数
 */
export const recalculateParamsBasedOnChange = (
    changedParam: keyof SimulationParams,
    changedValue: number,
    currentParams: Partial<SimulationParams>
): Partial<SimulationParams> => {
    const recommendations: Partial<SimulationParams> = {};
    const updatedParams = { ...currentParams, [changedParam]: changedValue };

    // 根据修改的参数类型，重新计算其他参数
    switch (changedParam) {
        case 'totalTarget':
            // 用户修改了总积分目标，重新计算首档奖励和衰减系数
            if (updatedParams.decayFactor) {
                recommendations.firstLevelReward = calculateFirstLevelReward(changedValue, updatedParams.decayFactor);
            } else if (updatedParams.firstLevelReward) {
                recommendations.decayFactor = 1 - (updatedParams.firstLevelReward / changedValue);
            } else {
                // 如果都没有，推荐默认衰减系数
                const recommendedDecayFactor = 0.9975;
                recommendations.decayFactor = recommendedDecayFactor;
                recommendations.firstLevelReward = calculateFirstLevelReward(changedValue, recommendedDecayFactor);
            }
            break;

        case 'estimatedLaborValue':
            // 用户修改了预估劳动价值，重新计算档位宽度和档位总数
            if (updatedParams.levelWidth) {
                recommendations.totalLevels = calculateTotalLevels(changedValue, updatedParams.levelWidth);
            } else if (updatedParams.totalLevels) {
                recommendations.levelWidth = calculateLevelWidth(changedValue, updatedParams.totalLevels);
            } else {
                // 如果都没有，推荐默认档位总数
                const recommendedTotalLevels = 50;
                recommendations.totalLevels = recommendedTotalLevels;
                recommendations.levelWidth = calculateLevelWidth(changedValue, recommendedTotalLevels);
            }
            break;

        case 'decayFactor':
            // 用户修改了衰减系数，重新计算首档奖励
            if (updatedParams.totalTarget) {
                recommendations.firstLevelReward = calculateFirstLevelReward(updatedParams.totalTarget, changedValue);
            }
            break;

        case 'levelWidth':
            // 用户修改了档位宽度，重新计算档位总数
            if (updatedParams.estimatedLaborValue) {
                recommendations.totalLevels = calculateTotalLevels(updatedParams.estimatedLaborValue, changedValue);
            }
            break;

        case 'totalLevels':
            // 用户修改了档位总数，重新计算档位宽度
            if (updatedParams.estimatedLaborValue) {
                recommendations.levelWidth = calculateLevelWidth(updatedParams.estimatedLaborValue, changedValue);
            }
            break;

        case 'firstLevelReward':
            // 用户修改了首档奖励，重新计算衰减系数
            if (updatedParams.totalTarget) {
                recommendations.decayFactor = 1 - (changedValue / updatedParams.totalTarget);
            }
            break;
    }

    // 如果所有必要参数都已具备，进行优化计算
    if (updatedParams.totalTarget && updatedParams.estimatedLaborValue && 
        updatedParams.decayFactor && updatedParams.levelWidth && 
        updatedParams.totalLevels && updatedParams.firstLevelReward) {
        
        const completeParams = { ...updatedParams, ...recommendations };
        const optimizations = optimizeSimulationParams(completeParams as SimulationParams);
        return { ...recommendations, ...optimizations };
    }

    return recommendations;
};

/**
 * 验证参数修改的合理性
 * @param changedParam 用户修改的参数名
 * @param changedValue 用户修改的参数值
 * @param currentParams 当前所有参数
 * @returns 验证结果
 */
export const validateParamChange = (
    changedParam: keyof SimulationParams,
    changedValue: number,
    currentParams: Partial<SimulationParams>
): { valid: boolean; errors: string[]; warnings: string[] } => {
    const errors: string[] = [];
    const warnings: string[] = [];

    // 基础验证
    if (changedValue <= 0) {
        errors.push(`${getParamDisplayName(changedParam)}必须大于0`);
    }

    // 特定参数验证
    switch (changedParam) {
        case 'decayFactor':
            if (changedValue <= 0 || changedValue >= 1) {
                errors.push('衰减系数必须在0到1之间');
            }
            if (changedValue < 0.95) {
                warnings.push('衰减系数过小可能导致奖励衰减过快');
            }
            if (changedValue > 0.999) {
                warnings.push('衰减系数过大可能导致奖励衰减过慢');
            }
            break;

        case 'totalLevels':
            if (changedValue < 10) {
                warnings.push('档位总数过少可能影响计算精度');
            }
            if (changedValue > 200) {
                warnings.push('档位总数过多可能影响性能');
            }
            break;

        case 'levelWidth':
            if (currentParams.estimatedLaborValue && currentParams.totalLevels) {
                const calculatedLaborValue = changedValue * currentParams.totalLevels;
                const deviation = Math.abs(calculatedLaborValue - currentParams.estimatedLaborValue) / currentParams.estimatedLaborValue;
                if (deviation > 0.1) {
                    warnings.push('档位宽度与预估劳动价值可能不匹配');
                }
            }
            break;
    }

    return {
        valid: errors.length === 0,
        errors,
        warnings
    };
};

/**
 * 获取参数显示名称
 * @param paramName 参数名
 * @returns 显示名称
 */
const getParamDisplayName = (paramName: keyof SimulationParams): string => {
    const displayNames: Record<keyof SimulationParams, string> = {
        totalTarget: '总积分目标',
        estimatedLaborValue: '预估劳动价值',
        decayFactor: '衰减系数',
        levelWidth: '档位宽度',
        totalLevels: '档位总数',
        firstLevelReward: '首档奖励'
    };
    return displayNames[paramName] || paramName;
}; 