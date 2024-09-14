import React, { useState } from 'react';
import { Modal, Form, Input, ConfigProvider, Select } from 'antd';

import './index.less';

type EditNodeModalProps = {
  isModalOpen: boolean;
  handleOkModal: (title: string, id: number, formData: any) => void;
  handleCloseModal: () => void;
  title: string;
  setTitle: (title: string) => void;
  id: string;
  name: string;
  ifSelectedValue: string;
};

type IfElectValCommand = {
  and: string;
  or: string;
};

const IFELECTVAL: IfElectValCommand = {
  and: '且',
  or: '或',
};

const EditNodeModal = (props: EditNodeModalProps) => {
  const {
    isModalOpen,
    handleOkModal,
    handleCloseModal,
    title,
    setTitle,
    id,
    name,
    ifSelectedValue,
  } = props;

  const layout = {
    labelCol: { span: 5 },
    wrapperCol: { span: 15 },
  };
  // 下拉框 条件语句 1
  const [selectedValue, setSelectedValue] = useState('');
  // 下拉框运算符 1
  const [operatorValue, setOperatorValue] = useState('');
  // 值 1
  const [value, setValue] = useState('');

  // 下拉框 条件语句 2
  const [selectedValueTwo, setSelectedValueTwo] = useState('');
  // 下拉框运算符 2
  const [operatorValueTwo, setOperatorValueTwo] = useState('');
  // 值 2
  const [valueTwo, setValueTwo] = useState('');

  const handleChange = (value: string) => {
    console.log('if选择器--value', value);
    setSelectedValue(value);
  };

  const handleOperator = (value: string) => {
    setOperatorValue(value);
  };

  const handleChangeSecond = (value: string) => {
    setSelectedValueTwo(value);
  };

  const handleOperatorTwo = (value: string) => {
    setOperatorValueTwo(value);
  };

  const formData = {
    selectedValue,
    operatorValue,
    value,

    selectedValueTwo,
    operatorValueTwo,
    valueTwo,
  };

  const when = `serviceName: ${selectedValue} ${operatorValue} ${value} && methodName:  ${selectedValueTwo}`; // 需要替换为相应值
  const then = 'System.out.println("test2 rule");'; //那么语句
  const ruleName = '随便改个名';

  let board =
    'package rules\n\n import java.util.Map;\n rule "Postcode 6 numbers"\n\n  when\n  then\n System.out.println("规则2中打印日志：校验通过!");\n end';
  board = board.replace('when', `when\n  ${when}`);
  board = board.replace('then', `then\n  ${then}`);
  board = board.replace('rule ', `rule  \"${ruleName}\"`);
  console.log('board', board);

  return (
    <div>
      <Modal
        width={700}
        open={isModalOpen}
        onOk={() => handleOkModal(title, id, formData)}
        onCancel={handleCloseModal}
      >
        <ConfigProvider>
          {name === 'if' ? (
            <Form {...layout}>
              <Form.Item>
                <p className="editNodeModal-title">
                  关系式（
                  <span className="editNodeModal-title-span">条件一</span>
                  {IFELECTVAL[ifSelectedValue as keyof typeof IFELECTVAL]}
                  <span className="editNodeModal-title-span">条件二</span>
                  ）成立时通过，否则直接拒绝
                </p>
              </Form.Item>
              <Form.Item label="条件一">
                <Select
                  style={{ width: 120, marginRight: 20 }}
                  defaultValue="选择变量"
                  onChange={handleChange}
                  options={[
                    { value: 'age', label: '年龄>12' },
                    { value: '性别', label: '女' },
                  ]}
                />
                <Select
                  defaultValue="运算符"
                  style={{ width: 120, marginRight: 20 }}
                  onChange={handleOperator}
                  options={[
                    { value: 'add', label: '加' },
                    { value: 'subtract', label: '减' },
                  ]}
                />
                <Input
                  value={value}
                  style={{ width: 100 }}
                  placeholder="请输入值"
                  onChange={(e) => {
                    setValue(e.target.value);
                    console.log(e.target.value);
                  }}
                />
              </Form.Item>
              <Form.Item label="条件二">
                <Select
                  defaultValue="选择变量"
                  style={{ width: 120, marginRight: 20 }}
                  onChange={handleChangeSecond}
                  options={[
                    { value: 'ifAge', label: '年龄' },
                    { value: 'ifSex', label: '女' },
                  ]}
                />
                <Select
                  defaultValue="运算符"
                  style={{ width: 120, marginRight: 20 }}
                  onChange={handleOperatorTwo}
                  options={[
                    { value: 'add', label: '加' },
                    { value: 'subtract', label: '减' },
                  ]}
                />
                <Input
                  style={{ width: 100 }}
                  placeholder="请输入值"
                  value={valueTwo}
                  onChange={(e) => {
                    setValueTwo(e.target.value);
                    console.log(e.target.value);
                  }}
                />
              </Form.Item>
            </Form>
          ) : null}
          {name === 'so' && <div>so</div>}
          {name === 'else' && <div>else</div>}
        </ConfigProvider>
      </Modal>
    </div>
  );
};

export default EditNodeModal;
