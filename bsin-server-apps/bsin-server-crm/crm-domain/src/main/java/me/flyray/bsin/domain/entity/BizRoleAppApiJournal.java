package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value ="crm_biz_role_app_api_journal")
public class BizRoleAppApiJournal implements Serializable {

    /**
     * 序号
     */
    @TableId
    private String serialNo;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * productId
     */
    private String appId;

    /**
     * 调用接口名称
     */
    private String apiName;

    /**
     * 费用
     */
    private String fee;

    /**
     * 消费客户号
     */
    private String customerNo;

    /**
     * 调用时间
     */
    private Date createTime;
}
