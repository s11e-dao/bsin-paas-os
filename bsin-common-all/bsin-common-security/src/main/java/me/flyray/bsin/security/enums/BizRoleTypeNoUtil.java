package me.flyray.bsin.security.enums;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class BizRoleTypeNoUtil {

    // 使用单例模式，避免重复创建Snowflake实例
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    /**
     * 根据业务角色类型生成对应的规则的编号
     * @param bizRoleType 业务角色类型
     * @return 生成的编号
     */
    public static String getBizRoleTypeNo(BizRoleType bizRoleType) {
        if (bizRoleType == null) {
            throw new IllegalArgumentException("业务角色类型不能为空");
        }
        
        // 生成雪花算法ID
        long id = SNOWFLAKE.nextId();
        
        // 根据业务角色类型添加前缀，便于识别和管理
        String prefix = getBizRoleTypePrefix(bizRoleType);
        
        return prefix + id;
    }
    
    /**
     * 获取业务角色类型的前缀
     * @param bizRoleType 业务角色类型
     * @return 前缀字符串
     */
    private static String getBizRoleTypePrefix(BizRoleType bizRoleType) {
        switch (bizRoleType) {
            case SYS:
                return "SYS";
            case TENANT:
                return "T";
            case MERCHANT:
                return "M";
            case SYS_AGENT:
                return "A";
            case CUSTOMER:
                return "C";
            case STORE:
                return "ST";
            case NONE_PLATFOR:
                return "N";
            default:
                return "U"; // Unknown
        }
    }
    
    /**
     * 生成纯数字编号（兼容原有逻辑）
     * @return 纯数字编号
     */
    public static String generateNumericId() {
        return String.valueOf(SNOWFLAKE.nextId());
    }

}
