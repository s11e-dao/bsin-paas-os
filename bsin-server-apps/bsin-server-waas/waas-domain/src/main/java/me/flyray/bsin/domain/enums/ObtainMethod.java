package me.flyray.bsin.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author bolei
 * @date 2023/7/6 9:32
 * @desc 领取方式：1 免费领取/空投 2 购买  3 固定口令领取 4 随机口令 5 盲盒购买
 */
public enum ObtainMethod {

    /**
     * 免费领取/空投
     */
    FREE("1", "免费领取/空投"),
    /**
     * 购买
     */
    BUY("2", "购买"),
    /**
     * 固定口令领取 Fixed password
     */
    FIXED_PASSWORD("3", "固定口令领取"),
    /**
     * 随机口令 Random
     */
    RANDOM_PASSWORD("4", "随机口令"),
    /**
     * 盲盒购买 Blind box
     */
    BLIND_BOX_BUY("5", "盲盒购买"),
    
    /**
     * 活动获取
     */
    ACTIVITY("6", "活动获取");

    private String code;

    private String desc;

    ObtainMethod(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * Json 枚举序列化
     */
    @JsonCreator
    public static ObtainMethod getInstanceById(String id) {
        if (id == null) {
            return null;
        }
        for (ObtainMethod status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }

}
