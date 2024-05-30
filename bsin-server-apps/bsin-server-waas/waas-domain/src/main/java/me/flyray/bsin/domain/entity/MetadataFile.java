package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import lombok.Data;
import me.flyray.bsin.domain.enums.FileType;

/**
 * 
 * @TableName da_metadata_file
 */
@TableName(value ="da_metadata_file")
@Data
public class MetadataFile implements Serializable {
    /**
     * 文件ID
     */
    @TableId
    private String serialNo;

    /**
     * 所属租户
     */
    private String tenantId;

    /**
     * 所属商户
     */
    private String merchantNo;

    /**
     * 父级ID
     */
    private String parentNo;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件编号
     */
    private String fileCode;

    /**
     * 描述
     */
    private String fileDescription;

    /**
     * 文件类型： 1 图片  2 gif 3 视频 4 音频 5 json 6 文件夹
     * @see FileType
     */
    private String fileType;

    /**
     * 是否是目录 0 否 1是
     */
    private String dirFlag;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * ips文件地址
     */
    private String ipfsUrl;


//    /**
//     * 图片uri
//     */
//    private String imageUrl;

    /**
     * 上传用户号
     */
    private String createBy;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private BigInteger tokenId;

    /**
     * metadata json内容
     */
    private String metadataContent;

}