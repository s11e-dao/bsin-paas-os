import { DataPoint } from './lindeChartSimple';

// Sigmoid联合曲线参数接口
export interface SigmoidParams {
    cap: number;              // token理论上限值
    initialPrice: number;     // 初始定价
    finalPrice: number;       // 稳定定价
    flexible: number;         // 曲线的拉伸变换
    currentSupply: number;    // 当前代币供应量
}

// Sigmoid仿真结果接口
export interface SigmoidSimulationResult {
    chartData: DataPoint[];
    summary: {
        currentPrice: number;
        totalSupply: number;
        priceRange: number;
        flexibleEffect: string;
        midPointPrice: number;
    };
    dataPoints: Array<{
        supply: number;
        price: number;
        mintAmount: number;
        burnAmount: number;
    }>;
}

/**
 * 基于sigmoid曲线公式的联合曲线积分铸造价格计算
 * @param params sigmoid参数
 * @param currentSupply 当前代币供应量
 * @returns 当前mint价格
 */
export const calculateSigmoidPrice = (params: SigmoidParams, currentSupply: number): number => {
    const { cap, initialPrice, finalPrice, flexible } = params;
    
    // 确保供应量在有效范围内
    const supply = Math.max(0, Math.min(currentSupply, cap));
    
    // 计算中点
    const midPoint = cap / 2;
    
    // 计算sigmoid变换
    const melo = flexible * (supply - midPoint) / midPoint;
    const deno = 1.0 / (1 + Math.exp(-melo));
    
    // 计算当前价格
    const currentPrice = initialPrice - (initialPrice - finalPrice) * deno;
    
    // // 添加调试信息
    // console.log('calculateSigmoidPrice debug:', {
    //     supply,
    //     cap,
    //     initialPrice,
    //     finalPrice,
    //     flexible,
    //     midPoint,
    //     melo,
    //     deno,
    //     currentPrice
    // });
    
    return currentPrice;
};

/**
 * 计算铸造指定劳动价值获得的积分数量
 * @param params sigmoid参数
 * @param currentSupply 当前供应量
 * @param laborValue 劳动价值（法币金额）
 * @returns 获得的积分数量
 */
export const calculateMintAmount = (params: SigmoidParams, currentSupply: number, laborValue: number): number => {
    let totalMintAmount = 0;
    let remainingLaborValue = laborValue;
    let currentSupplyTemp = currentSupply;
    
    // 分批次计算，每次100元
    const batchSize = 100;
    
    while (remainingLaborValue > 0) {
        const batchAmount = Math.min(batchSize, remainingLaborValue);
        const currentPrice = calculateSigmoidPrice(params, currentSupplyTemp);
        
        // 计算本次获得的积分
        const mintAmount = batchAmount / currentPrice;
        totalMintAmount += mintAmount;
        
        // 更新供应量和剩余劳动价值
        currentSupplyTemp += mintAmount;
        remainingLaborValue -= batchAmount;
    }
    
    return totalMintAmount;
};

/**
 * 计算销毁指定积分获得的劳动价值
 * @param params sigmoid参数
 * @param currentSupply 当前供应量
 * @param burnAmount 销毁的积分数量
 * @returns 获得的劳动价值
 */
export const calculateBurnAmount = (params: SigmoidParams, currentSupply: number, burnAmount: number): number => {
    const newSupply = currentSupply - burnAmount;
    const price = calculateSigmoidPrice(params, newSupply);
    return burnAmount * price;
};

/**
 * 生成sigmoid仿真数据
 * @param params sigmoid参数
 * @returns 仿真结果
 */
export const calculateSigmoidSimulation = (params: SigmoidParams): SigmoidSimulationResult => {
    const { cap, initialPrice, finalPrice, flexible } = params;
    
    const chartData: DataPoint[] = [];
    const dataPoints: Array<{
        supply: number;
        price: number;
        mintAmount: number;
        burnAmount: number;
    }> = [];
    
    // 生成数据点，从0到cap
    const stepSize = cap / 100; // 100个数据点
    
    for (let i = 0; i <= 100; i++) {
        const supply = i * stepSize;
        const price = calculateSigmoidPrice(params, supply);
        
        // 计算示例铸造和销毁
        const exampleMintValue = 1000; // 1000元劳动价值
        const exampleBurnAmount = 10000; // 10000积分
        
        const mintAmount = calculateMintAmount(params, supply, exampleMintValue);
        const burnAmount = calculateBurnAmount(params, supply, exampleBurnAmount);
        
        dataPoints.push({
            supply,
            price,
            mintAmount,
            burnAmount
        });
        
        // 为图表添加数据点
        chartData.push({
            supply,
            price,
            series: '价格曲线',
        });
        
        chartData.push({
            supply,
            price: mintAmount,
            series: '铸造收益',
        });
        
        chartData.push({
            supply,
            price: burnAmount,
            series: '销毁收益',
        });
    }
    
    // 计算汇总信息
    const currentPrice = calculateSigmoidPrice(params, params.currentSupply);
    const midPointPrice = calculateSigmoidPrice(params, cap / 2);
    const priceRange = finalPrice - initialPrice;
    
    let flexibleEffect = '';
    if (flexible < 4) {
        flexibleEffect = '曲线较平缓，接近线性';
    } else if (flexible >= 4 && flexible <= 6) {
        flexibleEffect = '理想S曲线，中间加速明显';
    } else {
        flexibleEffect = '曲线较陡峭，中间压缩严重';
    }
    
    const summary = {
        currentPrice,
        totalSupply: cap,
        priceRange,
        flexibleEffect,
        midPointPrice,
    };
    
    return {
        chartData,
        summary,
        dataPoints,
    };
};

/**
 * 验证sigmoid参数
 * @param params sigmoid参数
 * @returns 验证结果
 */
export const validateSigmoidParams = (params: SigmoidParams): { valid: boolean; errors: string[]; warnings: string[] } => {
    const errors: string[] = [];
    const warnings: string[] = [];
    
    if (params.cap <= 0) {
        errors.push('供应上限必须大于0');
    }
    
    if (params.initialPrice <= 0) {
        errors.push('初始价格必须大于0');
    }
    
    if (params.finalPrice <= 0) {
        errors.push('最终价格必须大于0');
    }
    
    if (params.initialPrice >= params.finalPrice) {
        errors.push('初始价格必须小于最终价格');
    }
    
    if (params.flexible <= 0) {
        errors.push('拉伸变换值必须大于0');
    }
    
    if (params.flexible < 2) {
        warnings.push('拉伸变换值过小，曲线可能过于平缓');
    }
    
    if (params.flexible > 8) {
        warnings.push('拉伸变换值过大，曲线可能过于陡峭');
    }
    
    if (params.currentSupply < 0) {
        errors.push('当前供应量不能为负数');
    }
    
    if (params.currentSupply > params.cap) {
        errors.push('当前供应量不能超过供应上限');
    }
    
    return {
        valid: errors.length === 0,
        errors,
        warnings
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
 * 获取参数推荐值
 * @param partialParams 部分参数
 * @returns 推荐参数
 */
export const getRecommendedSigmoidParams = (partialParams: Partial<SigmoidParams>): Partial<SigmoidParams> => {
    const recommendations: Partial<SigmoidParams> = {};
    
    // 如果没有提供flexible，推荐理想值
    if (!partialParams.flexible) {
        recommendations.flexible = 5; // 理想S曲线值
    }
    
    // 如果没有提供initialPrice，推荐默认值
    if (!partialParams.initialPrice) {
        recommendations.initialPrice = 0.01;
    }
    
    // 如果没有提供finalPrice，推荐默认值
    if (!partialParams.finalPrice) {
        recommendations.finalPrice = 1.0;
    }
    
    // 如果没有提供currentSupply，默认为0
    if (partialParams.currentSupply === undefined) {
        recommendations.currentSupply = 0;
    }
    
    return recommendations;
};

/**
 * 计算价格变化率 - 基于sigmoid函数的数学特性
 * @param params sigmoid参数
 * @param supply 供应量
 * @returns 价格变化率
 */
export const calculatePriceChangeRate = (params: SigmoidParams, supply: number): number => {
    const { cap, initialPrice, finalPrice, flexible } = params;
    
    // 使用更合适的delta值，确保数值微分的准确性
    const delta = Math.max(cap * 0.0001, supply * 0.01); // 最小为cap的0.01%，确保有足够的变化
    
    const price1 = calculateSigmoidPrice(params, Math.max(0, supply - delta));
    const price2 = calculateSigmoidPrice(params, supply + delta);
    
    const result = (price2 - price1) / (2 * delta);
    
    // 添加调试信息
    console.log('calculatePriceChangeRate debug:', {
        supply,
        delta,
        price1,
        price2,
        result,
        params
    });
    
    return result;
};

/**
 * 计算价格变化率 - 使用解析方法（更准确）
 * @param params sigmoid参数
 * @param supply 供应量
 * @returns 价格变化率
 */
export const calculatePriceChangeRateAnalytical = (params: SigmoidParams, supply: number): number => {
    const { cap, initialPrice, finalPrice, flexible } = params;
    
    // 确保供应量在有效范围内
    const s = Math.max(0, Math.min(supply, cap));
    
    // 计算中点
    const midPoint = cap / 2;
    
    // 计算sigmoid变换
    const melo = flexible * (s - midPoint) / midPoint;
    const expMelo = Math.exp(melo);
    const deno = 1.0 / (1 + expMelo);
    
    // 计算sigmoid函数的导数
    const sigmoidDerivative = (expMelo * flexible) / (midPoint * Math.pow(1 + expMelo, 2));
    
    // 计算价格变化率
    const priceChangeRate = (finalPrice - initialPrice) * sigmoidDerivative;
    
    console.log('calculatePriceChangeRateAnalytical debug:', {
        supply: s,
        melo,
        expMelo,
        deno,
        sigmoidDerivative,
        priceChangeRate,
        params
    });
    
    return priceChangeRate;
};

/**
 * 计算最优铸造时机
 * @param params sigmoid参数
 * @param targetSupply 目标供应量
 * @returns 最优铸造建议
 */
export const calculateOptimalMintTiming = (params: SigmoidParams, targetSupply: number): {
    currentPrice: number;
    targetPrice: number;
    priceIncrease: number;
    recommendation: string;
} => {
    const currentPrice = calculateSigmoidPrice(params, params.currentSupply);
    const targetPrice = calculateSigmoidPrice(params, targetSupply);
    const priceIncrease = ((targetPrice - currentPrice) / currentPrice) * 100;
    console.log('calculateOptimalMintTiming debug:', {
        currentSupply: params.currentSupply,
        targetSupply,
        currentPrice,
        targetPrice,
        priceIncrease
    });
    
    let recommendation = '';
    if (priceIncrease > 10) {
        recommendation = '建议立即铸造，价格将显著上涨';
    } else if (priceIncrease > 5) {
        recommendation = '建议考虑铸造，价格将适度上涨';
    } else if (priceIncrease > 0) {
        recommendation = '价格将小幅上涨，可根据需求决定';
    } else {
        recommendation = '价格将下降，建议等待或分批铸造';
    }
    
    return {
        currentPrice,
        targetPrice,
        priceIncrease,
        recommendation
    };
}; 

/**
 * 计算价格弹性
 * @param params sigmoid参数
 * @param supply 供应量
 * @returns 价格弹性
 */
export const calculatePriceElasticity = (params: SigmoidParams, supply: number): number => {
    const price = calculateSigmoidPrice(params, supply);
    const changeRate = calculatePriceChangeRate(params, supply);
    
    return (changeRate * supply) / price;
};

/**
 * 计算最优供应量分布
 * @param params sigmoid参数
 * @returns 最优分布建议
 */
export const calculateOptimalSupplyDistribution = (params: SigmoidParams): {
    earlyStage: number;
    midStage: number;
    lateStage: number;
    recommendation: string;
} => {
    const { cap, flexible } = params;
    
    // 根据flexible值调整分布
    let earlyStage, midStage, lateStage;
    
    if (flexible <= 3) {
        // 平缓曲线，均匀分布
        earlyStage = cap * 0.33;
        midStage = cap * 0.34;
        lateStage = cap * 0.33;
    } else if (flexible <= 6) {
        // 标准S曲线，中间加速
        earlyStage = cap * 0.25;
        midStage = cap * 0.5;
        lateStage = cap * 0.25;
    } else {
        // 陡峭曲线，前期集中
        earlyStage = cap * 0.4;
        midStage = cap * 0.4;
        lateStage = cap * 0.2;
    }
    
    let recommendation = '';
    if (flexible <= 3) {
        recommendation = '曲线平缓，建议均匀分布供应量';
    } else if (flexible <= 6) {
        recommendation = '标准S曲线，中期价格变化最快，建议在中期增加供应';
    } else {
        recommendation = '曲线陡峭，建议前期集中供应，后期谨慎';
    }
    
    return {
        earlyStage,
        midStage,
        lateStage,
        recommendation
    };
};

/**
 * 计算风险指标
 * @param params sigmoid参数
 * @returns 风险分析
 */
export const calculateRiskMetrics = (params: SigmoidParams): {
    volatility: number;
    riskLevel: string;
    riskFactors: string[];
} => {
    const { flexible, initialPrice, finalPrice, currentSupply, cap } = params;
    
    // 计算价格波动性
    const priceRange = finalPrice - initialPrice;
    const volatility = (priceRange / initialPrice) * (flexible / 5);
    
    // 计算供应量风险
    const supplyRisk = currentSupply / cap;
    
    const riskFactors: string[] = [];
    let riskLevel = '低';
    
    if (flexible > 7) {
        riskFactors.push('曲线过于陡峭，价格变化剧烈');
        riskLevel = '高';
    } else if (flexible < 3) {
        riskFactors.push('曲线过于平缓，缺乏激励性');
        riskLevel = '中';
    }
    
    if (supplyRisk > 0.8) {
        riskFactors.push('供应量接近上限，价格上涨空间有限');
        riskLevel = '高';
    } else if (supplyRisk < 0.2) {
        riskFactors.push('供应量较低，价格波动较大');
        riskLevel = '中';
    }
    
    if (priceRange / initialPrice > 100) {
        riskFactors.push('价格区间过大，可能影响稳定性');
        riskLevel = '高';
    }
    
    return {
        volatility,
        riskLevel,
        riskFactors
    };
};

/**
 * 生成多组sigmoid仿真数据
 * @param paramsArray 多组sigmoid参数
 * @returns 多组仿真结果
 */
export const calculateMultiSigmoidSimulation = (paramsArray: Array<{ params: SigmoidParams; name: string; color?: string }>): {
    chartData: DataPoint[];
    summaries: Array<{ name: string; summary: any; color?: string }>;
    dataPoints: Array<{ name: string; data: any[] }>;
} => {
    const chartData: DataPoint[] = [];
    const summaries: Array<{ name: string; summary: any; color?: string }> = [];
    const dataPoints: Array<{ name: string; data: any[] }> = [];

    paramsArray.forEach(({ params, name, color }) => {
        const { cap, initialPrice, finalPrice, flexible } = params;
        const groupDataPoints: any[] = [];
        
        // 生成数据点，从0到cap
        const stepSize = cap / 100; // 100个数据点
        
        for (let i = 0; i <= 100; i++) {
            const supply = i * stepSize;
            const price = calculateSigmoidPrice(params, supply);
            
            // 计算示例铸造和销毁
            const exampleMintValue = 1000; // 1000元劳动价值
            const exampleBurnAmount = 10000; // 10000积分
            
            const mintAmount = calculateMintAmount(params, supply, exampleMintValue);
            const burnAmount = calculateBurnAmount(params, supply, exampleBurnAmount);
            
            groupDataPoints.push({
                supply,
                price,
                mintAmount,
                burnAmount
            });
            
            // 为图表添加数据点，添加组标识
            chartData.push({
                supply,
                price,
                series: `${name}-价格曲线`,
                group: name,
            });
            
            chartData.push({
                supply,
                price: mintAmount,
                series: `${name}-铸造收益`,
                group: name,
            });
            
            chartData.push({
                supply,
                price: burnAmount,
                series: `${name}-销毁收益`,
                group: name,
            });
        }
        
        // 计算汇总信息
        const currentPrice = calculateSigmoidPrice(params, params.currentSupply);
        const midPointPrice = calculateSigmoidPrice(params, cap / 2);
        const priceRange = finalPrice - initialPrice;
        
        let flexibleEffect = '';
        if (flexible < 4) {
            flexibleEffect = '曲线较平缓，接近线性';
        } else if (flexible >= 4 && flexible <= 6) {
            flexibleEffect = '理想S曲线，中间加速明显';
        } else {
            flexibleEffect = '曲线较陡峭，中间压缩严重';
        }
        
        const summary = {
            currentPrice,
            totalSupply: cap,
            priceRange,
            flexibleEffect,
            midPointPrice,
        };
        
        summaries.push({ name, summary, color });
        dataPoints.push({ name, data: groupDataPoints });
    });
    
    return {
        chartData,
        summaries,
        dataPoints,
    };
};

/**
 * 比较多组sigmoid参数
 * @param paramsArray 多组sigmoid参数
 * @returns 比较结果
 */
export const compareSigmoidParams = (paramsArray: Array<{ params: SigmoidParams; name: string }>): {
    priceComparison: Array<{ name: string; currentPrice: number; midPointPrice: number; priceRange: number }>;
    supplyComparison: Array<{ name: string; currentSupply: number; totalSupply: number; utilization: number }>;
    flexibleComparison: Array<{ name: string; flexible: number; effect: string }>;
} => {
    const priceComparison = paramsArray.map(({ params, name }) => {
        const currentPrice = calculateSigmoidPrice(params, params.currentSupply);
        const midPointPrice = calculateSigmoidPrice(params, params.cap / 2);
        const priceRange = params.finalPrice - params.initialPrice;
        
        return {
            name,
            currentPrice,
            midPointPrice,
            priceRange,
        };
    });
    
    const supplyComparison = paramsArray.map(({ params, name }) => {
        const utilization = (params.currentSupply / params.cap) * 100;
        
        return {
            name,
            currentSupply: params.currentSupply,
            totalSupply: params.cap,
            utilization,
        };
    });
    
    const flexibleComparison = paramsArray.map(({ params, name }) => {
        let effect = '';
        if (params.flexible < 4) {
            effect = '较平缓';
        } else if (params.flexible >= 4 && params.flexible <= 6) {
            effect = '理想S曲线';
        } else {
            effect = '较陡峭';
        }
        
        return {
            name,
            flexible: params.flexible,
            effect,
        };
    });
    
    return {
        priceComparison,
        supplyComparison,
        flexibleComparison,
    };
};