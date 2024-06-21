import React, { memo } from 'react';
import { Handle, useReactFlow, useStoreApi, Position } from 'reactflow';
import { Form, Input, InputNumber, Typography } from 'antd';

const onConnect = (params) => console.log('handle onConnect', params);

const labelStyle = {
  display: 'flex',
  alignItems: 'center',
  padding: '0px 10px'
};

const node = {
  backgroundColor: "#fff",
  border: "1px solid #ddd"
};

function RoleDefineNode({ id, data }) {

  const [form] = Form.useForm();
  const nameValue = Form.useWatch('name', form);

  return (
    <>
      <div style={node}>
        <div className="custom-node__header">
          <strong>角色</strong>
        </div>
        <Handle type="target" position={Position.Left} onConnect={onConnect} />
        <div style={labelStyle}>
          <Form form={form} layout="vertical" autoComplete="off">
            <Form.Item name="name" label="角色定义">
              <Input />
            </Form.Item>
          </Form>

        </div>
        <Handle type="source" position={Position.Right} />
      </div>
    </>
  );
}

export default memo(RoleDefineNode);
