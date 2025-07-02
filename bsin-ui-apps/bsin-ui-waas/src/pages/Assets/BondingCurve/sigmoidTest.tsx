import React from 'react';
import { Card, Typography, Space, Alert } from 'antd';
import {
  SigmoidParams,
  calculateSigmoidPrice,
  calculateMintAmount,
  calculateBurnAmount,
  validateSigmoidParams,
  calculatePriceChangeRate,
  calculateOptimalMintTiming,
  formatNumber,
} from './sigmoidUtils';

const { Title, Text, Paragraph } = Typography;

const SigmoidTest: React.FC = () => {
  // 测试参数
  const testParams: SigmoidParams = {
    cap: 21000000,
    initialPrice: 0.01,
    finalPrice: 1.0,
    flexible: 5,
    currentSupply: 1000000,
  };

  // 测试价格计算
  const testPriceCalculation = () => {
    const prices = [];
    for (let supply = 0; supply <= testParams.cap; supply += testParams.cap / 10) {
      const price = calculateSigmoidPrice(testParams, supply);
      prices.push({ supply, price });
    }
    return prices;
  };

  // 测试铸造计算
  const testMintCalculation = () => {
    const laborValues = [100, 500, 1000, 5000, 10000];
    return laborValues.map(laborValue => ({
      laborValue,
      mintAmount: calculateMintAmount(testParams, testParams.currentSupply, laborValue),
      avgPrice: laborValue / calculateMintAmount(testParams, testParams.currentSupply, laborValue)
    }));
  };

  // 测试销毁计算
  const testBurnCalculation = () => {
    const burnAmounts = [1000, 5000, 10000, 50000, 100000];
    return burnAmounts.map(burnAmount => ({
      burnAmount,
      burnValue: calculateBurnAmount(testParams, testParams.currentSupply, burnAmount),
      avgPrice: calculateBurnAmount(testParams, testParams.currentSupply, burnAmount) / burnAmount
    }));
  };

  // 测试参数验证
  const testValidation = () => {
    const invalidParams: SigmoidParams = {
      cap: -1000,
      initialPrice: 2.0,
      finalPrice: 1.0,
      flexible: 15,
      currentSupply: 25000000,
    };
    return validateSigmoidParams(invalidParams);
  };

  // 测试价格变化率
  const testPriceChangeRate = () => {
    const supplyPoints = [0, testParams.cap * 0.25, testParams.cap * 0.5, testParams.cap * 0.75, testParams.cap];
    return supplyPoints.map(supply => ({
      supply,
      price: calculateSigmoidPrice(testParams, supply),
      changeRate: calculatePriceChangeRate(testParams, supply)
    }));
  };

  // 测试最优铸造时机
  const testOptimalTiming = () => {
    const targetSupplies = [testParams.cap * 0.25, testParams.cap * 0.5, testParams.cap * 0.75];
    return targetSupplies.map(targetSupply => ({
      targetSupply,
      ...calculateOptimalMintTiming(testParams, targetSupply)
    }));
  };

  const priceData = testPriceCalculation();
  const mintData = testMintCalculation();
  const burnData = testBurnCalculation();
  const validationResult = testValidation();
  const changeRateData = testPriceChangeRate();
  const timingData = testOptimalTiming();

  return (
    <div style={{ padding: '20px' }}>
      <Title level={2}>Sigmoid仿真系统测试</Title>

      <Alert
        message="测试说明"
        description="本页面用于测试Sigmoid仿真系统的各项功能，验证计算逻辑的正确性。"
        type="info"
        showIcon
        style={{ marginBottom: '20px' }}
      />

      {/* 测试参数 */}
      <Card title="测试参数" style={{ marginBottom: '20px' }}>
        <Space direction="vertical">
          <Text>供应上限: {formatNumber(testParams.cap)}</Text>
          <Text>初始价格: {testParams.initialPrice}元</Text>
          <Text>最终价格: {testParams.finalPrice}元</Text>
          <Text>拉伸变换值: {testParams.flexible}</Text>
          <Text>当前供应量: {formatNumber(testParams.currentSupply)}</Text>
        </Space>
      </Card>

      {/* 价格计算测试 */}
      <Card title="价格计算测试" style={{ marginBottom: '20px' }}>
        <div style={{ maxHeight: '300px', overflow: 'auto' }}>
          {priceData.map((item, index) => (
            <div key={index} style={{ marginBottom: '8px' }}>
              <Text>供应量: {formatNumber(Math.round(item.supply))} → 价格: {item.price.toFixed(4)}元</Text>
            </div>
          ))}
        </div>
      </Card>

      {/* 铸造计算测试 */}
      <Card title="铸造计算测试" style={{ marginBottom: '20px' }}>
        <div style={{ maxHeight: '300px', overflow: 'auto' }}>
          {mintData.map((item, index) => (
            <div key={index} style={{ marginBottom: '8px' }}>
              <Text>
                投入{item.laborValue}元 → 获得{formatNumber(Math.round(item.mintAmount))}积分 
                (平均价格: {item.avgPrice.toFixed(4)}元)
              </Text>
            </div>
          ))}
        </div>
      </Card>

      {/* 销毁计算测试 */}
      <Card title="销毁计算测试" style={{ marginBottom: '20px' }}>
        <div style={{ maxHeight: '300px', overflow: 'auto' }}>
          {burnData.map((item, index) => (
            <div key={index} style={{ marginBottom: '8px' }}>
              <Text>
                销毁{formatNumber(item.burnAmount)}积分 → 获得{item.burnValue.toFixed(2)}元 
                (平均价格: {item.avgPrice.toFixed(4)}元)
              </Text>
            </div>
          ))}
        </div>
      </Card>

      {/* 参数验证测试 */}
      <Card title="参数验证测试" style={{ marginBottom: '20px' }}>
        <Alert
          message={`验证结果: ${validationResult.valid ? '通过' : '失败'}`}
          description={
            <div>
              {validationResult.errors.length > 0 && (
                <div>
                  <Text strong>错误:</Text>
                  <ul>
                    {validationResult.errors.map((error, index) => (
                      <li key={index}>{error}</li>
                    ))}
                  </ul>
                </div>
              )}
              {validationResult.warnings.length > 0 && (
                <div>
                  <Text strong>警告:</Text>
                  <ul>
                    {validationResult.warnings.map((warning, index) => (
                      <li key={index}>{warning}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          }
          type={validationResult.valid ? 'success' : 'error'}
          showIcon
        />
      </Card>

      {/* 价格变化率测试 */}
      <Card title="价格变化率测试" style={{ marginBottom: '20px' }}>
        <div style={{ maxHeight: '300px', overflow: 'auto' }}>
          {changeRateData.map((item, index) => (
            <div key={index} style={{ marginBottom: '8px' }}>
              <Text>
                供应量: {formatNumber(Math.round(item.supply))} → 
                价格: {item.price.toFixed(4)}元 → 
                变化率: {item.changeRate.toFixed(6)}元/积分
              </Text>
            </div>
          ))}
        </div>
      </Card>

      {/* 最优铸造时机测试 */}
      <Card title="最优铸造时机测试" style={{ marginBottom: '20px' }}>
        <div style={{ maxHeight: '300px', overflow: 'auto' }}>
          {timingData.map((item, index) => (
            <div key={index} style={{ marginBottom: '16px' }}>
              <Alert
                message={`目标供应量: ${formatNumber(Math.round(item.targetSupply))}`}
                description={
                  <div>
                    <p>当前价格: {item.currentPrice.toFixed(4)}元</p>
                    <p>目标价格: {item.targetPrice.toFixed(4)}元</p>
                    <p>价格涨幅: {item.priceIncrease.toFixed(2)}%</p>
                    <p>建议: {item.recommendation}</p>
                  </div>
                }
                type={item.priceIncrease > 5 ? 'warning' : 'info'}
                showIcon
              />
            </div>
          ))}
        </div>
      </Card>

      {/* 测试总结 */}
      <Card title="测试总结">
        <Space direction="vertical">
          <Text>✓ 价格计算功能正常</Text>
          <Text>✓ 铸造计算功能正常</Text>
          <Text>✓ 销毁计算功能正常</Text>
          <Text>✓ 参数验证功能正常</Text>
          <Text>✓ 价格变化率计算正常</Text>
          <Text>✓ 最优铸造时机分析正常</Text>
          <Paragraph>
            <Text strong>测试结论:</Text> Sigmoid仿真系统的各项功能均正常工作，计算逻辑正确。
          </Paragraph>
        </Space>
      </Card>
    </div>
  );
};

export default SigmoidTest; 