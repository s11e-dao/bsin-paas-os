import React, { ReactNode, useState } from 'react';
import { Button, Modal, Tabs, message } from 'antd';
import Icon, { createFromIconfontCN } from "@ant-design/icons";
import './Icon.less';
import useSelectIconHooks from './useSelectIconHooks';

interface ChildComponentProps {
  isModalVisible: boolean;
  handleOk: (choosedIcon: string) => void;
  handleCancel: () => void;
}
const IconFont = createFromIconfontCN({
  scriptUrl: [
    "//at.alicdn.com/t/c/font_2902756_vaobogm4v4.js",
  ],
});

const App: React.FC<ChildComponentProps> = ({
  isModalVisible,
  handleOk,
  handleCancel,
}) => {
  const [activeIndex, setActiveIndex] = useState<string>('');
  const [choosedIcon, setChoosedIcon] = useState<string>('');

  const [messageApi, contextHolder] = message.useMessage();
  const chooseIcon = (icon: any) => {
    setActiveIndex(icon);
    setChoosedIcon(icon);
    handleOk(icon);
    reset();
    // messageApi.info(`选中 ${icon}`);
  };

  //引入选择icon的hooks
  const items=useSelectIconHooks(chooseIcon)

  const reset = () => {
    setActiveIndex('');
    setChoosedIcon('');

  }

  //有确定和取消时的按钮
  // const ok = () => {
  //    if (choosedIcon == '') {
  //      messageApi.warning('尚未选择任何图标');
  //      return;
  //   }
  //   handleOk(choosedIcon);
  //   reset();
  // }
  const cancel = () => {
    reset()
    handleCancel()
  }

  return (
    <>
      {contextHolder}
      <Modal
        open={isModalVisible}
        // onOk={ok}
        onCancel={cancel}
        width={900}
        maskClosable={false}
        mask={false}
        keyboard={false}
        centered={true}
        // okText="确认"
        // cancelText="取消"
        footer={null}
      >
        <Tabs defaultActiveKey="1" items={items} />
      </Modal>
    </>
  );
};

export default App;
