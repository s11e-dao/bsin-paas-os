import React from 'react';
import { MarkerType, Position } from 'reactflow';

export const nodes = [
  {
    id: '1',
    type: 'input',
    data: {
      label: '根据角色定义获取营销热点信息',
    },
    position: { x: 250, y: 0 },
  },
  {
    id: '2',
    data: {
      label: '短信通知',
    },
    position: { x: 100, y: 100 },
  },
  {
    id: '3',
    data: {
      label: '生成营销文案',
    },
    position: { x: 400, y: 100 },
    targetPosition: Position.Top,
    sourcePosition: Position.Bottom,
  },
  {
    id: '4',
    type: 'custom',
    position: { x: 100, y: 200 },
    data: {
      selects: {
        'handle-0': 'smoothstep',
        'handle-1': 'smoothstep',
        'handle-2': 'smoothstep',
      },
    },
  },
  {
    id: '5',
    type: 'output',
    data: {
      label: '发公众号',
    },
    className: 'circle',
    style: {
      background: '#2B6CB0',
      color: 'white',
    },
    position: { x: 450, y: 250 },
    sourcePosition: Position.Right,
    targetPosition: Position.Left,
  },
  {
    id: '6',
    type: 'output',
    style: {
      background: '#63B3ED',
      color: 'white',
      width: 100,
    },
    data: {
      label: '发社区论坛',
    },
    position: { x: 450, y: 353 },
    sourcePosition: Position.Right,
    targetPosition: Position.Left,
  },
  {
    id: '7',
    type: 'default',
    className: 'annotation',
    data: {
      label: (
        <>
          自定义 <strong>AI员工</strong> 提升 <strong>生产力</strong>. 🥳
        </>
      ),
    },
    draggable: false,
    selectable: false,
    position: { x: 150, y: 450 },
  },

  {
    id: '8',
    data: { label: 'Group B' },
    position: { x: 580, y: 120 },
    className: 'light',
    style: { backgroundColor: 'rgba(255, 0, 0, 0.2)', width: 300, height: 300 },
  },
  {
    id: '8a',
    data: { label: 'Node B.1' },
    position: { x: 15, y: 65 },
    className: 'light',
    parentNode: '8',
    extent: 'parent',
  },
  {
    id: '8b',
    data: { label: 'Group B.A' },
    position: { x: 15, y: 120 },
    className: 'light',
    style: { backgroundColor: 'rgba(255, 0, 255, 0.2)', height: 150, width: 270 },
    parentNode: '8',
  },
  {
    id: '8b1',
    data: { label: 'Node B.A.1' },
    position: { x: 20, y: 40 },
    className: 'light',
    parentNode: '8b',
  },
  {
    id: '8b2',
    data: { label: 'Node B.A.2' },
    position: { x: 100, y: 100 },
    className: 'light',
    parentNode: '8b',
  },
];

export const edges = [
  { id: 'e1-2', source: '1', target: '2', label: 'this is an edge label' },
  { id: 'e1-3', source: '1', target: '3', animated: true, sourcePosition: Position.Top},
  {
    id: 'e3-4',
    source: '3',
    target: '4',
    animated: true,
    targetHandle: 'handle-0',
  },
  {
    id: 'e4-5',
    source: '4',
    target: '5',
    type: 'smoothstep',
    sourceHandle: 'handle-1',
    data: {
      selectIndex: 1,
    },
    markerEnd: {
      type: MarkerType.ArrowClosed,
    },
  },
  {
    id: 'e4-6',
    source: '4',
    target: '6',
    type: 'smoothstep',
    sourceHandle: 'handle-2',
    data: {
      selectIndex: 1,
    },
    markerEnd: {
      type: MarkerType.ArrowClosed,
    },
  },
];
