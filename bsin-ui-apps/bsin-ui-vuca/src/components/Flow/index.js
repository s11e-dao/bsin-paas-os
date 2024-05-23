import React, { useState, useRef, useCallback } from 'react';

import ReactFlow, {
  addEdge,
  MiniMap,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
  ReactFlowProvider
} from 'reactflow';

import { nodes as initialNodes, edges as initialEdges } from './initial-elements';


// 注册自定义组件
import nodeTypes from '../nodeComponents';

import 'reactflow/dist/style.css';

import Sidebar from '../Sidebar';
import ChatBox from '../ChatBox';

import 'reactflow/dist/style.css';
import './overviewReactflow.css';


const minimapStyle = {
  height: 120,
};

let id = 0;
const getId = () => `dndnode_${id++}`;

const AiWorkflow = () => {
  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
  const onConnect = useCallback((params) => setEdges((eds) => addEdge(params, eds)), []);


  const reactFlowWrapper = useRef(null);
  const [reactFlowInstance, setReactFlowInstance] = useState(null);


  const onDragOver = useCallback((event) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback(
    (event) => {
      event.preventDefault();

      const reactFlowBounds = reactFlowWrapper.current.getBoundingClientRect();
      const type = event.dataTransfer.getData('application/reactflow');

      // check if the dropped element is valid
      if (typeof type === 'undefined' || !type) {
        return;
      }

      const position = reactFlowInstance.project({
        x: event.clientX - reactFlowBounds.left,
        y: event.clientY - reactFlowBounds.top,
      });
      const newNode = {
        id: getId(),
        type,
        position,
        data: { label: `${type} node` },
      };

      console.log(newNode)
      console.log(reactFlowInstance)
      // 获取画布对象
      console.log(reactFlowInstance.getNodes())
      setNodes((nds) => nds.concat(newNode));
    },
    [reactFlowInstance]
  );


  // we are using a bit of a shortcut here to adjust the edge type
  // this could also be done with a custom edge for example
  const edgesWithUpdatedTypes = edges.map((edge) => {
    if (edge.sourceHandle) {
      const edgeType = nodes.find((node) => node.type === 'custom').data.selects[edge.sourceHandle];
      edge.type = edgeType;
    }

    return edge;
  });

  return (
    <div className="dndflow">
      <ReactFlowProvider>
        <Sidebar />
        <div className="reactflow-wrapper" ref={reactFlowWrapper}>
          <ReactFlow
            nodes={nodes}
            edges={edgesWithUpdatedTypes}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onConnect={onConnect}
            onInit={setReactFlowInstance}
            onDrop={onDrop}
            onDragOver={onDragOver}
            fitView
            // attributionPosition="top-right"
            nodeTypes={nodeTypes}
          >
            {/* <MiniMap style={minimapStyle} zoomable pannable /> */}
            <Controls />
            <Background color="#aaa" gap={16} />
          </ReactFlow>
        </div>
        <ChatBox />
      </ReactFlowProvider >
    </div >
  );
};

export default AiWorkflow;
