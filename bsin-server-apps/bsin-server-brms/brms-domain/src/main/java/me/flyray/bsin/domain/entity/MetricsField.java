    package me.flyray.bsin.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.EditGroup;

import javax.validation.constraints.NotBlank;

/**
 * 指标字段实体
 */
@Data
public class MetricsField {

    /**
     * id
     */
    private String serialNo;

    /**
     * 指标的ID
     */
    @NotBlank(message = "指标的ID-metricsEntityNo不能为空")
    private String metricsEntityNo;

    /**
     * 指标的Key
     */
    @NotBlank(message = "指标的Key-metricsEntityKey不能为空")
    private String metricsEntityKey;

    /**
     * 字段的Key
     */
    @NotBlank(message = "字段的Key-key不能为空")
    private String key;

    /**
     * 字段的名称
     */
    @NotBlank(message = "字段的名称-name不能为空")
    private String name;

    /**
     * 字段的种类
     * 1-基础类型
     * 2-引用类型
     */
    @NotBlank(message = "字段的种类-category不能为空")
    private String category;

    /**
     * 具体的类型
     */
    @NotBlank(message = "具体的类型-type不能为空")
    private String type;

    /**
     * 出参还是入参
     * 1-入参
     * 2-出参
     */
    @NotBlank(message = "指标是出参还是入参-inOrOut不能为空")
    private String inOrOut;

}
