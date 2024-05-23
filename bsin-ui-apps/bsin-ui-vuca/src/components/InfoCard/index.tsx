import React, { useState, useMemo } from "react";
import styles from "./index.less";
import { IdcardOutlined, RightOutlined } from "@ant-design/icons";
export default function InfoCard(params: any) {
  const { info,options } = params;
  const {
    name,
    desc,
    vChat,
    phone,
    email,
    backgroundUrl,
    character,
    knowledgeList,
    
  } = info;
  const doubledCount = useMemo(() => {
    return knowledgeList.map((item:any) => {
      
      const option = options.find((option:any) => option.value === item);
      return option;
    });

  }, [knowledgeList, options]);
  console.log(doubledCount)
  return (
    <div>
      <div
        className={styles.preview}
        id="previewWarpRef"
        style={{ height: "630px" }}
      >
        <div className={styles.preview_wrapper}>
          <div className={styles.iframe_wrapper}>
            {/* 顶部展示栏 */}
            <div className={styles.phone_header_bar}>
              <img
                className={styles.phone_header_bar_image}
                src="https://muselink.cc/static/phoneHeaderBarIcon.855d306a.svg"
              />
            </div>
            <div className={styles.content_wrapper}>
              {/* 信息展示区 */}
              <div
                className={styles.content_background_wrapper}
                style={{ backgroundImage: `url(${backgroundUrl})` }}
              >
                {/* 添加人物展示 */}
                <img className={styles.character_image} src={character} />
                {/* 信息栏 */}
                <div className={styles.content_info_wrapper}>
                  {/* 展示名称和介绍 */}
                  <div className={styles.content_info_name}>{name}</div>
                  <div className={styles.content_info_desc}>{desc}</div>
                </div>
              </div>
              {/* 列表展示区 */}
              <div className={styles.content_list_wrapper}>
                {doubledCount.map((item: any) => {
                  return (
                    <div key={item.value} className={styles.content_list_item}>
                      <a href={item.url}>
                        <IdcardOutlined
                          style={{ fontSize: "22px", marginRight: 10 }}
                        />
                        <div style={{flex:1}}>
                          <div className={styles.content_list_item_title}>
                            {item.title}
                          </div>
                          <div className={styles.content_list_item_desc}>
                            {item.description}
                          </div>
                        </div>
                        <RightOutlined style={{color: 'rgba(128, 128, 128, 1)'}}/>
                      </a>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
