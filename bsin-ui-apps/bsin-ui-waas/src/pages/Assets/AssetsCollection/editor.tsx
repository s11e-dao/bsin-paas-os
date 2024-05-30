import React, { useState, useEffect } from 'react';
import MarkdownIt from 'markdown-it';
import MdEditor from 'react-markdown-editor-lite';
// 导入编辑器的样式，不导入会出现毫无样式情况
import 'react-markdown-editor-lite/lib/index.css';

export default () => {
  // 数据保存
  const [value, setValue] = useState('');
  // markdown-it 利用设置参数，具体查询markdown-it官网
  const mdParser = new MarkdownIt({
    html: true,
    linkify: false,
    typographer: true,
  }).enable('image');

  // 检测markdown数据变化
  function handleEditorChange({ html, text }) {
    setValue(text);
    console.log('handleEditorChange', html, text);
  }
  return (
    <MdEditor
      value={value}
      onChange={handleEditorChange}
      renderHTML={(text) => mdParser.render(text)}
      style={{ height: 400 }}
      onImageUpload={async (file) => {
        const formData = new FormData();
        formData.append('files', file);
        // 自行图片上传功能，利用form文件表单
        return await postUrl(formData).then((res) => {
          if (res.code === 200) {
            return res.data;
          } else {
            message.error('图片过大，请上传小于1mb的图片');
          }
        });
      }}
    ></MdEditor>
  );
};
