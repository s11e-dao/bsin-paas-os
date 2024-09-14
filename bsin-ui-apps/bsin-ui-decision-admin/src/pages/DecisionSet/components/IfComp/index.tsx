import React, { useState } from 'react';
import { Select } from 'antd';
import CanvasEditComp from '../CanvasEditComp';
import './index.less';
const IfComp = ({
  ifList,
  setIfList,
  handleChange,
  selectedValue,
}: {
  ifList: any;
  setIfList: any;
  handleChange: any;
  selectedValue: string;
}) => {
  // 编辑弹窗
  const [isModalOpen, setIsModalOpen] = useState(false);

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
    const nextList = ifList.map((x) => ({
      ...x,
      title: id === x.id ? value : x.title,
    }));
    setIfList(nextList);
    console.log('同样是保存数据---', formData); // 请求接口提交数据
    setIsModalOpen(false);
  };

  return (
    <div className="rules-design-if">
      <div className="rules-design-if-title">如果</div>
      <div className="rules-design-if-choose">
        <Select
          defaultValue="and"
          style={{ width: 120 }}
          onChange={handleChange}
          options={[
            { value: 'and', label: '并且' },
            { value: 'or', label: '或' },
          ]}
        />
        <CanvasEditComp
          handleDel={handleDel}
          list={ifList}
          isModalOpen={isModalOpen}
          handleOpenModal={handleOpenModal}
          handleCloseModal={handleCloseModal}
          handleOkModal={handleOkModal}
          bgColor
          selectedValue={selectedValue}
          name="if"
        />
      </div>
    </div>
  );
};

export default IfComp;
