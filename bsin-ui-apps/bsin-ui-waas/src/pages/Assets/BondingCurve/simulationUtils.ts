import { DataPoint } from './lindeChart';

// 仿真参数接口
export interface SimulationParams {
  totalTarget: number;      // 总积分目标 T
  levelWidth: number;       // 档位宽度 δC
  decayFactor: number;      // 衰减系数 k
}

// 档位数据接口
export interface LevelData {
  level: number;            // 档位序号
  reward: number;           // 档期奖励
  cumulativeReward: number; // 累计奖励
  laborValue: number;       // 劳动价值（档位宽度 * 档位序号）
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
  };
}

/**
 * 计算联合曲线仿真
 * @param params 仿真参数
 * @returns 仿真结果
 */
export const calculateBondingCurveSimulation = (params: SimulationParams): SimulationResult => {
  const { totalTarget, levelWidth, decayFactor } = params;
  
  // 计算首档奖励：R₀ = T * (1 - k)
  const firstLevelReward = totalTarget * (1 - decayFactor);
  
  const levels: LevelData[] = [];
  const chartData: DataPoint[] = [];
  
  let cumulativeReward = 0;
  let level = 1;
  
  // 计算档位数据，直到达到目标值的99.9%或超过50个档位
  while (level <= 50 && cumulativeReward < totalTarget * 0.999) {
    // 档期奖励：Rn = R₀ * k^(n-1)
    const reward = firstLevelReward * Math.pow(decayFactor, level - 1);
    cumulativeReward += reward;
    
    // 劳动价值 = 档位宽度 * 档位序号
    const laborValue = levelWidth * level;
    
    const levelInfo: LevelData = {
      level,
      reward,
      cumulativeReward,
      laborValue,
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
    
    level++;
  }
  
  // 计算汇总信息
  const summary = {
    firstLevelReward,
    totalLevels: levels.length,
    totalReward: cumulativeReward,
    targetAchievement: (cumulativeReward / totalTarget) * 100,
  };
  
  return {
    levels,
    chartData,
    summary,
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
  
  if (params.levelWidth <= 0) {
    errors.push('档位宽度必须大于0');
  }
  
  if (params.decayFactor <= 0 || params.decayFactor >= 1) {
    errors.push('衰减系数必须在0到1之间');
  }
  
  if (params.levelWidth > params.totalTarget) {
    errors.push('档位宽度不能大于总积分目标');
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
 * @returns 理论总积分
 */
export const calculateTheoreticalTotal = (params: SimulationParams): number => {
  const { totalTarget, decayFactor } = params;
  // 理论总积分 = T / (1 - k)
  return totalTarget / (1 - decayFactor);
}; 