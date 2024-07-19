package me.flyray.bsin.domain.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;


/**
 * @author ：bolei
 * @date ：Created in 2023/04/12 16:23
 * @description：规则实体类
 * @modified By：
 */

@Data
public class EventModel {

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
     * kbase的名字
     */
    private String eventCode;


    /**
     * 1、规则模型 2、流程模型
     */
    private String modelType;

    /**
     * 事件模型
     */
    private String modelNo;

}
