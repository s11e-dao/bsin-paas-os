import React, { useState } from 'react';
import { DownCircleOutlined, CloseOutlined } from '@ant-design/icons';
import EditNodeModal from '../EditNodeModal';
import classnames from 'classnames';
import './index.less';

const CanvasEditComp = (props: any) => {
  const {
    list,
    handleDel,
    isModalOpen,
    handleOpenModal,
    handleOkModal,
    handleCloseModal,
    bgColor,
    name,
    selectedValue,
  } = props;

  const [title, setTitle] = useState('');
  const [id, setId] = useState('');

  if (!list) {
    return null;
  }

  return (
    <div className="canvasEditComp-contanier">
      {list.map(
        (
          item: {
            id:
              | boolean
              | React.ReactChild
              | React.ReactFragment
              | React.ReactPortal
              | null
              | undefined;
            title:
              | boolean
              | React.ReactChild
              | React.ReactFragment
              | React.ReactPortal
              | null
              | undefined;
            value:
              | boolean
              | React.ReactChild
              | React.ReactFragment
              | React.ReactPortal
              | null
              | undefined;
          },
          index: React.Key | null | undefined,
        ) => (
          <div
            key={index}
            className={classnames('canvasEditComp-wrapper', {
              'canvasEditComp-wrapper-green': !bgColor,
              'canvasEditComp-wrapper-orange': bgColor,
            })}
          >
            <div
              className={classnames('canvasEditComp-icon', {
                'canvasEditComp-icon-green': !bgColor,
                'canvasEditComp-icon-orange': bgColor,
              })}
            >
              <DownCircleOutlined />
            </div>

            <div
              className="canvasEditComp-content"
              onClick={() => {
                setTitle(item?.title);
                setId(item?.id);
                handleOpenModal();
              }}
            >
              <p className="canvasEditComp-title">{item.title}</p>
              <p className="canvasEditComp-value">{item.value}</p>
            </div>
            {/* 删除需传唯一标识 */}
            <div
              className="canvasEditComp-close"
              onClick={() => {
                handleDel(item?.value);
              }}
            >
              <CloseOutlined />
            </div>
          </div>
        ),
      )}
      <EditNodeModal
        isModalOpen={isModalOpen}
        handleOkModal={handleOkModal}
        handleCloseModal={handleCloseModal}
        title={title}
        setTitle={setTitle}
        id={id}
        name={name}
        ifSelectedValue={selectedValue}
      />
    </div>
  );
};

export default CanvasEditComp;
