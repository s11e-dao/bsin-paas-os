package me.flyray.bsin.oss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TokenResponse implements Serializable {

    private String encodedPolicy;

    private String aliyunAccessKeyId;

    private String signature;

    private String bucketName;

    private String securityToken;

    private String endpoint;

    private String domain;

}
