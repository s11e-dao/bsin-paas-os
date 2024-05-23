package me.flyray.bsin.domain.domain;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName ai_wx_platform_user_tag
 */
@Data
@TableName("ai_wx_platform_user_tag")
public class WxPlatformUserTag {

    /**
     * 
     */
    private String serialNo;


    private String tenantId;

    /**
     * 用户openId
     */
    private String openId;

    /**
     * 标签
     */
    private String tag;

    /**
     * 应用ID
     */
    private String appId;

}