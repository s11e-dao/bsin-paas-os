package me.flyray.bsin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * solidity 变量类型
 */
public enum SolidityVariableType {

    /**
     * string
     */
    STRING("string", "字符串"),
    /**
     * string[]
     */
    STRING_ARRAY("string[]", "字符串"),
    /**
     * address
     */
    ADDRESS("address", "地址"),
    /**
     * address[]
     */
    ADDRES_ARRAY("address[]", "地址数组"),
    /**
     * bool
     */
    BOOL("bool", "bool类型"),
    /**
     * bool[]
     */
    BOOL_ARRAY("bool[]", "bool数组"),
    /**
     * uint8
     */
    UINT8("uint8", "8位无符号"),
    /**
     * uint8[]
     */
    UINT8_ARRAY("uint8[]", "8位无符号数组"),
    /**
     * uint32
     */
    UINT32("uint32", "32位无符号"),
    /**
     * uint32[]
     */
    UINT32_ARRAY("uint32[]", "32位无符号数组"),
    /**
     * uint128
     */
    UINT128("uint128", "128位无符号"),
    /**
     * uint128[]
     */
    UINT128_ARRAY("uint128[]", "128位无符号数组"),
    /**
     * uint256
     */
    UINT256("uint256", "256位无符号"),
    /**
     * uint256[]
     */
    UINT256_ARRAY("uint256[]", "256位无符号数组"),
    /**
     * byte32
     */
    BYTE32("byte32", "固定大小字节"),
    /**
     * byte32[]
     */
    BYTE32_ARRAY("byte32[]", "固定大小字节数组"),
    /**
     * bytes
     */
    BYTES("bytes", "固定大小字节"),
    /**
     * bytes
     */
    BYTES_ARRAY("bytes[]", "固定大小字节数组");

    private String type;
    private String desc;

    SolidityVariableType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * Json 枚举序列化
     */
    @JsonCreator
    public static SolidityVariableType getInstanceByType(String type) {
        if (type == null) {
            return null;
        }
        for (SolidityVariableType status : values()) {
            if (type.equals(status.getType())) {
                return status;
            }
        }
        return null;
    }

}
