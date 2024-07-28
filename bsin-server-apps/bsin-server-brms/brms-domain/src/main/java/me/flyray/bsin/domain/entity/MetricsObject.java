package me.flyray.bsin.domain.entity;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 指标实体
 */
@Data
public class MetricsObject {

    /**
     * id
     */
    private String serialNo;

    /**
     * 指标实体的Key
     */
    @NotBlank(message = "指标实体的Key-key不能为空")
    private String key;

    /**
     * 指标实体的名称
     */
    @NotBlank(message = "指标实体的名称-name不能为空")
    private String name;

    /**
     * 指标实体的种类
     * 参考：me.flyray.bsin.domain.constant.MetricsCategoryConstant
     */
    @NotBlank(message = "指标实体的种类-category不能为空")
    private String category;

    /**
     * 具体的具体类型
     * 参考：me.flyray.bsin.domain.constant.MetricsTypeConstant
     */
    @NotBlank(message = "具体的类型-type不能为空")
    private String type;

}
