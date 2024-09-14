import React, { useState } from 'react';
import CanvasEditComp from '../CanvasEditComp';
import AddNodeModal from '../AddNodeModal';
import './index.less';
const SoComp = ({ soList, setSoList }: { soList: any; setSoList: any }) => {
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
      ...soList,
      {
        id: soList.length + 1,
        title,
        value,
      },
    ];

    setSoList(nextList);
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

  const handleOkModal = (value: string, id: string, formData: string) => {
    const nextList = soList.map((x) => ({
      ...x,
      title: id === x.id ? value : x.title,
    }));
    setSoList(nextList);
    setIsModalOpen(false);
  };

  return (
    <div className="rules-design-so">
      <div className="rules-design-so-content">
        <div className="rules-design-so-title">那么</div>
        <CanvasEditComp
          handleDel={handleDel}
          list={soList}
          isModalOpen={isModalOpen}
          handleOpenModal={handleOpenModal}
          handleCloseModal={handleCloseModal}
          handleOkModal={handleOkModal}
          name="so"
        />
      </div>
      <div
        className="rules-design-so-btn"
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

export default SoComp;
