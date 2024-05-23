package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 存储短信
 * @TableName validate_code
 */
@Data
public class ValidateCode implements Serializable {
    /**
     * 序号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String serialNo;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 验证码
     */
    private String validateCode;

    /**
     * 1、平台验证 2、币种验证 3、客户币种验证 4、秘钥验证 5、结算账户验证 6、黑白名单地址验证 7、资金转出验证 8、钱包验证
     */
    private Integer validateType;

    /**
     * 消息
     */
    private String msg;

    /**
     * 1:发送成功 2:发送失败 3:已验证 4:短信验证码失效
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}