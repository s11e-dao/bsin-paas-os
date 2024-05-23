package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.flyray.bsin.validate.AddGroup;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author ：bolei
 * @date ：Created in 2022/3/28 21:32
 * @description：数据字典
 * @modified By：
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict")
public class SysDict implements Serializable {

    /**
     * 编号
     */
    @TableId
    private String id;

    /**
     * 类型
     */
    @NotBlank(message = "类型不能为空", groups = AddGroup.class)
    private String dictType;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 是否是系统内置
     */
    private String systemFlag;

    /**
     * 备注信息
     */
    @TableField(exist = false)
    private String remark;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 删除标记
     */
    private String delFlag;


}
