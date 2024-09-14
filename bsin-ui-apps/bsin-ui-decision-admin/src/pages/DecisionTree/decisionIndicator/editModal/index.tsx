import React from 'react';
import { Modal } from 'antd';
import { Input } from 'antd';

const { TextArea } = Input;
export const EditModal = ({
  visible,
  setVisible,
  viewJsonData,
  setViewJsonData,
}: {
  visible: boolean;
  setVisible: any;
  viewJsonData: string;
  setViewJsonData: any;
}) => {
  const handleOk = () => {
    setVisible(false);
    // TODO, 提交数据给接口
  };

  const handleCancel = () => {
    setVisible(false);
  };

  return (
    <Modal
      title="编辑节点json"
      open={visible}
      onOk={handleOk}
      onCancel={handleCancel}
    >
      <TextArea
        placeholder="请输入。。。"
        value={JSON.stringify(viewJsonData)}
        onChange={(e) => {
          setViewJsonData(JSON.parse(e.target.value));
        }}
      />
    </Modal>
  );
};
