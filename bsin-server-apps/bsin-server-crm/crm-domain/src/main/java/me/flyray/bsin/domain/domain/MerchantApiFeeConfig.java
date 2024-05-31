package me.flyray.bsin.domain.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value ="crm_merchant_app_api_fee")
public class MerchantApiFeeConfig implements Serializable {

    /**
     * 序号
     */
    @TableId
    private String serialNo;

    /**
     * 租户号
     */
    private String tenantId;

    /**
     * 单次调用费用：单位分
     */
    private String fee;

    /**
     * 可免费调用次数
     */
    private Integer freeTimes;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 配置生效状态：0 待审核 1已生效
     */
    private String status;

    /**
     * 应用ID
     */
    private String productId;
}
