import { DataPoint } from './lindeChart';

// 测试数据 - 确保数据格式正确
export const testCurveJournalData: DataPoint[] = [
  { supply: 0, price: 100, series: 'curveJournal' },
  { supply: 100, price: 95, series: 'curveJournal' },
  { supply: 200, price: 90, series: 'curveJournal' },
  { supply: 300, price: 85, series: 'curveJournal' },
  { supply: 400, price: 80, series: 'curveJournal' },
  { supply: 500, price: 75, series: 'curveJournal' },
];

export const testCurveTrendData: DataPoint[] = [
  { supply: 0, price: 120, series: 'curveTrend' },
  { supply: 100, price: 115, series: 'curveTrend' },
  { supply: 200, price: 110, series: 'curveTrend' },
  { supply: 300, price: 105, series: 'curveTrend' },
  { supply: 400, price: 100, series: 'curveTrend' },
  { supply: 500, price: 95, series: 'curveTrend' },
];

// 合并后的测试数据
export const testAllData: DataPoint[] = [
  ...testCurveJournalData,
  ...testCurveTrendData,
]; 