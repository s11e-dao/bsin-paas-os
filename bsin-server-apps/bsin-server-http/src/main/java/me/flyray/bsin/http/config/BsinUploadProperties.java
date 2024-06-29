package me.flyray.bsin.http.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bsin.upload")
public class BsinUploadProperties {

    private long fileSize;  //压缩大小


    private double scaleRatio; //压缩比例


    private String uploadPath; //保存路径


    private String imageType; //图片类型

    private String preImgUrl; //图片回显地址

}
