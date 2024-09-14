import React, { useState } from 'react';
import CanvasEditComp from '../CanvasEditComp';
import AddNodeModal from '../AddNodeModal';
import './index.less';
const ElseComp = ({
  elseList,
  setElseList,
}: {
  elseList: any;
  setElseList: any;
}) => {
  // 编辑弹窗
  const [isModalOpen, setIsModalOpen] = useState(false);

  // 新增节点弹窗
  const [addNodeVisible, setAddNodeVisible] = useState(false);

  // 确认新增
  const onOkAddNodeModal = ({
    title,
    value,
  }: {
    title: string;
    value: number;
  }) => {
    console.log('title, value', title, value);
    setAddNodeVisible(false);
    const nextList = [
      ...elseList,
      {
        id: elseList.length + 1,
        title,
        value,
      },
    ];

    setElseList(nextList);
  };

  // 打开新增节点弹窗
  const openAddNodeModal = () => {
    setAddNodeVisible(true);
  };

  // 关闭节点弹窗
  const closeAddNodeModal = () => {
    setAddNodeVisible(false);
  };

  const handleDel = (value: any) => {
    console.log('删除value', value);
  };

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    console.log('触发关闭弹窗');
    setIsModalOpen(false);
  };

  // 保存数据
  const handleOkModal = (value: string, id: string, formData: string) => {
    const nextList = elseList.map((x) => ({
      ...x,
      title: id === x.id ? value : x.title,
    }));

    setElseList(nextList);
    setIsModalOpen(false);
  };

  return (
    <div>
      <div className="rules-design-else">
        <div className="rules-design-else-title">否则</div>
        <CanvasEditComp
          handleDel={handleDel}
          list={elseList}
          isModalOpen={isModalOpen}
          handleOpenModal={handleOpenModal}
          handleCloseModal={handleCloseModal}
          handleOkModal={handleOkModal}
          name="else"
        />
      </div>
      <div
        className="rules-design-else-btn"
        onClick={() => {
          openAddNodeModal();
        }}
      >
        添加动作
      </div>
      <AddNodeModal
        addNodeVisible={addNodeVisible}
        onOkAddNodeModal={onOkAddNodeModal}
        closeAddNodeModal={closeAddNodeModal}
      />
    </div>
  );
};

export default ElseComp;
