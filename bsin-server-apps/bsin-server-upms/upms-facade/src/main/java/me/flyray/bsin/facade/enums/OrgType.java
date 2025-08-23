package me.flyray.bsin.facade.enums;


import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author ：bolei
 * @date ：Created in 2022/02/05 16:19
 * @description：机构类型  1、公司 2、部门 3、小组 4、商户 5、店铺  99、其他
 * @modified By：
 */
public enum OrgType {

    /**
     * 公司
     */
    COMPANY(1, "公司"),
    
    /**
     * 部门
     */
    DEPARTMENT(2, "部门"),
    
    /**
     * 小组
     */
    GROUP(3, "小组"),
    
    /**
     * 商户
     */
    MERCHANT(4, "商户"),
    
    /**
     * 店铺
     */
    SHOP(5, "店铺"),
    
    /**
     * 其他
     */
    OTHER(99, "其他");

    private Integer code;

    private String desc;

    OrgType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * Json 枚举序列化
     */
    @JsonCreator
    public static OrgType getInstanceById(Integer id) {
        if (id == null) {
            return null;
        }
        for (OrgType status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }
}
