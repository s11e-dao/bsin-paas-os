import React, { useState } from 'react';
import { Button, Modal, Tabs, message } from 'antd';
import type { TabsProps } from 'antd';
import './Icon.less';
interface ChildComponentProps {
  isModalVisible: boolean;
  handleOk: (choosedIcon:string) => void;
  handleCancel: () => void;
}


const directionIcons = [
  'icon-step-backward',
  'icon-step-forward',
  'icon-fast-backward',
  'icon-fast-forward',
  'icon-shrink',
  'icon-down',
  'icon-up',
  'icon-left',
  'icon-right',
  'icon-caret-up',
  'icon-caret-down',
  'icon-caret-left',
  'icon-caret-right',
  'icon-up-circle',
  'icon-down-circle',
  'icon-left-circle',
  'icon-right-circle',
  'icon-forward',
  'icon-backward',
  'icon-rollback',
  'icon-enter',
  'icon-retweet',
  'icon-swap',
  'icon-swap-left',
  'icon-swap-right',
  'icon-play-circle',
  'icon-up-square',
  'icon-down-square',
  'icon-left-square',
  'icon-right-square',
  'icon-login',
  'icon-logout',
  'icon-border-bottom',
  'icon-border-horizontal',
  'icon-border-inner',
  'icon-border-left',
  'icon-border-right',
  'icon-border-top',
  'icon-border-verticle',
  'icon-pic-center',
  'icon-pic-left',
  'icon-pic-right',
  'icon-radius-bottomleft',
  'icon-radius-bottomright',
  'icon-radius-upleft',
  'icon-radius-upright',
  'icon-fullscreen',
  'icon-fullscreen-exit',
];
const suggestionIcons = [
  'icon-question',
  'icon-question-circle',
  'icon-plus',
  'icon-plus-circle',
  'icon-pause',
  'icon-minus',
  'icon-minus-circle',
  'icon-plus-square',
  'icon-minus-square',
  'icon-info-circle',
  'icon-close',
  'icon-close-circle',
  'icon-close-square',
  'icon-check',
  'icon-check-circle',
  'icon-check-square',
  'icon-stop',
];
const editIcons = [
  'icon-edit',
  'icon-scissor',
  'icon-delete',
  'icon-snippets',
  'icon-diff',
  'icon-highlight',
  'icon-align-center',
  'icon-align-left',
  'icon-align-right',
  'icon-bg-colors',
  'icon-bold',
  'icon-italic',
  'icon-underline',
  'icon-strikethrough',
  'icon-redo',
  'icon-undo',
  'icon-font-colors',
  'icon-font-size',
  'icon-line-height',
  'icon-colum-height',
  'icon-dash',
  'icon-small-dash',
  'icon-sort-ascending',
  'icon-sort-descending',
  'icon-drag',
  'icon-radius-setting',
];
const webIcons = [
  'icon-lock',
  'icon-unlock',
  'icon-book',
  'icon-calendar',
  'icon-cloud',
  'icon-cloud-download',
  'icon-code',
  'icon-delete',
  'icon-desktop',
  'icon-download',
  'icon-ellipsis',
  'icon-file',
  'icon-file-text',
  'icon-file-unknown',
  'icon-file-pdf',
  'icon-file-word',
  'icon-file-excel',
  'icon-file-ppt',
  'icon-file-markdown',
  'icon-file-add',
  'icon-folder',
  'icon-folder-open',
  'icon-folder-add',
  'icon-frown',
  'icon-meh',
  'icon-smile',
  'icon-laptop',
  'icon-appstore',
  'icon-link',
  'icon-mail',
  'icon-mobile',
  'icon-notification',
  'icon-poweroff',
  'icon-reload',
  'icon-search',
  'icon-setting',
  'icon-tablet',
  'icon-tag',
  'icon-tags',
  'icon-upload',
  'icon-user',
  'icon-home',
  'icon-star',
  'icon-heart',
  'icon-eye',
  'icon-camera',
  'icon-save',
  'icon-team',
  'icon-solution',
  'icon-phone',
  'icon-filter',
  'icon-export',
  'icon-qrcode',
  'icon-scan',
  'icon-like',
  'icon-message',
  'icon-calculator',
  'icon-pushpin',
  'icon-bulb',
  'icon-select',
  'icon-rocket',
  'icon-bell',
  'icon-disconnect',
  'icon-database',
  'icon-compass',
  'icon-barcode',
  'icon-hourglass',
  'icon-key',
  'icon-flag',
  'icon-layout',
  'icon-printer',
  'icon-sound',
  'icon-skin',
  'icon-sync',
  'icon-wifi',
  'icon-car',
  'icon-man',
  'icon-woman',
  'icon-shop',
  'icon-gift',
  'icon-idcard',
  'icon-copyright',
  'icon-trademark',
  'icon-wallet',
  'icon-bank',
  'icon-trophy',
  'icon-contacts',
  'icon-shake',
  'icon-api',
  'icon-fork',
  'icon-dashboard',
  'icon-table',
  'icon-alert',
  'icon-audit',
  'icon-branches',
  'icon-build',
  'icon-border',
  'icon-crown',
  'icon-experiment',
  'icon-fire',
  'icon-read',
  'icon-reconciliation',
  'icon-rest',
];

const App: React.FC<ChildComponentProps> = ({
  isModalVisible,
  handleOk,
  handleCancel,
}) => {
  const [activeIndex, setActiveIndex] = useState<string>('');
  const [choosedIcon, setChoosedIcon] = useState<string>('');
  const items: TabsProps['items'] = [
    {
      key: '1',
      label: '方向性图标',
      children: (
        <ul className="icon-list-ul">
          {directionIcons.map((item, index) => (
            <li key={item}>
              <span
                className={
                  activeIndex === item
                    ? `active ${item} iconfont `
                    : `${item} iconfont `
                }
                onClick={() => chooseIcon(item)}
              ></span>
            </li>
          ))}
        </ul>
      ),
    },
    {
      key: '2',
      label: '指示性图标',
      children: (
        <ul className="icon-list-ul">
          {suggestionIcons.map((item, index) => (
            <li key={item}>
              <span
                className={
                  activeIndex === item
                    ? `active ${item} iconfont `
                    : `${item} iconfont `
                }
                onClick={() => chooseIcon(item)}
              ></span>
            </li>
          ))}
        </ul>
      ),
    },
    {
      key: '3',
      label: '编辑类图标',
      children: (
        <ul className="icon-list-ul">
          {editIcons.map((item, index) => (
            <li key={item}>
              <span
                className={
                  activeIndex === item
                    ? `active ${item} iconfont `
                    : `${item} iconfont `
                }
                onClick={() => chooseIcon(item)}
              ></span>
            </li>
          ))}
        </ul>
      ),
    },
    {
      key: '4',
      label: '网站通用图标',
      children: (
        <ul className="icon-list-ul">
          {webIcons.map((item, index) => (
            <li key={item}>
              <span
                className={
                  activeIndex === item
                    ? `active ${item} iconfont `
                    : `${item} iconfont `
                }
                onClick={() => chooseIcon(item)}
              ></span>
            </li>
          ))}
        </ul>
      ),
    },
  ];
  const [messageApi, contextHolder] = message.useMessage();
  const chooseIcon = (icon: string) => {
    setActiveIndex(icon);
    setChoosedIcon(icon);

    messageApi.info(`选中 ${icon}`);
  };
  const reset= ()=> {
    setActiveIndex('');
    setChoosedIcon('');
    
  }
  const ok = () => {
     if (choosedIcon == '') {
       messageApi.warning('尚未选择任何图标');
       return;
    }
        handleOk(choosedIcon);
    reset();


  }
  const cancel = () => {
    reset()
    handleCancel()
  }
  return (
    <>
      {contextHolder}
      <Modal
        open={isModalVisible}
        onOk={ok}
        onCancel={cancel}
        width={900}
        maskClosable={false}
        mask={false}
        keyboard={false}
        centered={true}
        okText="确认"
        cancelText="取消"
      >
        <Tabs defaultActiveKey="1" items={items}  />
      </Modal>
    </>
  );
};

export default App;
