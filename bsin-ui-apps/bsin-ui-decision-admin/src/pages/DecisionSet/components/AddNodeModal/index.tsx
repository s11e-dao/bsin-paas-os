import React, { useState } from 'react';
import { Modal, Form, Input, ConfigProvider } from 'antd';

import './index.less';

const AddNodeModal = (props: any) => {
  const { addNodeVisible, onOkAddNodeModal, closeAddNodeModal } = props;

  const [title, setTitle] = useState('');
  const [value, setValue] = useState(undefined);
  const layout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 15 },
  };

  return (
    <div>
      <Modal
        title="添加动作"
        open={addNodeVisible}
        onOk={() => onOkAddNodeModal({ title, value })}
        onCancel={closeAddNodeModal}
        okText="确认"
        cancelText="取消"
      >
        <ConfigProvider>
          <Form {...layout}>
            <Form.Item name="newNodeName" label="节点名">
              <Input
                value={title}
                onChange={(e) => {
                  console.log('e.target.value', e.target.value);

                  setTitle(e.target.value);
                }}
              />
            </Form.Item>

            <Form.Item name="newNodeValue" label="值">
              <Input
                value={value}
                onChange={(e) => {
                  console.log('e.target.value', e.target.value);

                  setValue(e.target.value);
                }}
              />
            </Form.Item>
          </Form>
        </ConfigProvider>
      </Modal>
    </div>
  );
};

export default AddNodeModal;
