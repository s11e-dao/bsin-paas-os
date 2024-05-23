package me.flyray.bsin.domain.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppResp implements Serializable {
    
    public String appId;

    public String appName;

    public String appCode;

    public String logo;

    public String type;


}
