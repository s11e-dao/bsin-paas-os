package me.flyray.bsin.oss;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HLW
 */
@Data
public class StsResponse implements Serializable {


    /**
     * token
     */
    private String securityToken;

    /**
     * 秘钥
     */
    private String accessKeySecret;

    /**
     * accessKey
     */
    private String accessKeyId;

    /**
     * 过期时间
     */
    private String expiration;


}
