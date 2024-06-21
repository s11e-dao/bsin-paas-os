package me.flyray.bsin.facade.enums;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/8/9
 * @desc 会员账户类别
 */
public enum AccountCategory {

    /**
     * 余额账户
     */
    BALANCE(1, "余额账户"),
    /**
     * 支出账户
     */
    MINAPP(2, "支出账户"),

    TREASURY(3,"国库账户");

    private Integer code;


    private String desc;

    AccountCategory(Integer code, String desc) {
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
    public static AccountCategory getInstanceById(Integer id) {
        if (id == null) {
            return null;
        }
        for (AccountCategory status : values()) {
            if (id.equals(status.getCode())) {
                return status;
            }
        }
        return null;
    }


    public static List<Map<String, Object>> getMemberAccountEnum() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AccountCategory value : AccountCategory.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("key", value.getCode());
            map.put("value", value.getDesc());
            list.add(map);
        }
        return list;
    }
}
