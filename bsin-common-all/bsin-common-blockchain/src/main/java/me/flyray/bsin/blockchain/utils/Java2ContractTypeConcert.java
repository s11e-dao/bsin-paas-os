package me.flyray.bsin.blockchain.utils;

import java.util.Map;

/**
 * java中都是用string类型，即 java string-->contract type
 */
public class Java2ContractTypeConcert {

//    /**
//     * 1、string
//     */
//    STRING("string",null,"字符串类型"),
//
//    /**
//     * 2、address
//     */
//    ADDRESS("address",null,"地址类型"),
//
//    /**
//     * 3、array
//     */
//    ARRAY("array",null,"数组类型"),
//
//    /**
//     * 4、uint8
//     */
//    UINT8("uint8",null,"8位无符号整型型"),
//
//
//    /**
//     * 5、uint128
//     */
//    UINT128("uint128",null,"128位无符号整型型"),
//
//
//    /**
//     * 6、uint256
//     */
//    UINT256("uint256",null,"256位无符号整型型"),
//
//    /**
//     * 7、bytes32
//     */
//    BYTES32("bytes32",null,"32byte定长数组"),
//
//    /**
//     * 7、struct
//     */
//    STRUCT("struct",null,"结构体类型");


    private String type;
    private Map<String, Object> value;
    private String desc;


    public Java2ContractTypeConcert() {
    }

    public Java2ContractTypeConcert(String type, Map<String, Object> value, String desc) {
        this.type = type;
        this.value = value;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(Map<String, Object> value) {
        this.value = value;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
