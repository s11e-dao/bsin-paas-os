package me.flyray.bsin.domain.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * @author ：bolei
 * @date ：Created in 2023/04/12 16:23
 * @description：规则实体类
 * @modified By：
 */

@Data
@TableName(value ="brms_decision_rule")
public class DecisionRule implements Serializable {

    /**
     * 规则ID
     */
    @TableId
    private String serialNo;

    /**
     * kbase的名字
     */
    private String tenantId;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * kbase的名字: 规则编号
     */
    private String kieBaseName;

    /**
     * 比如：kiePackageName=rules/rule01 那么当前规则文件写入路径为： kieFileSystem.write("src/main/resources/rules/rule01/1.drl")
     */
    private String kiePackageName;

    /**
     * 规则内容
     */
    private String content;

    /**
     * 规则内容JSON
     */
    private String contentJson;

    /**
     * 规则创建时间
     */
    private Date createTime;

    private String type;

    /**
     * 规则更新时间
     */
    private Date updateTime;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 版本号
     */
    private String version;

    public void validate() {
        if (this.serialNo == null || isBlank(kieBaseName) || isBlank(kiePackageName) || isBlank(content)) {
            throw new RuntimeException("参数有问题");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }

}
