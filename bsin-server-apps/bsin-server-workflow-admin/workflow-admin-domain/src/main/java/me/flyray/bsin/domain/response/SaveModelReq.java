package me.flyray.bsin.domain.response;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author huangzh
 * @ClassName SaveModelReq
 * @DATE 2021/1/12 9:45
 */

@Data
public class SaveModelReq implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     *  模型名称
     */
    @NotNull(message = "name不能为空")
    private String name;

    /**
     *  模型key
     */
    @NotNull(message = "key不能为空")
    private String key;

    /**
     *  模型描述
     */
    private String description;

    /**
     *  模型创建人
     */
    private String createdBy;

    /**
     *  最后更新人
     */
    private String lastUpdatedBy;

    /**
     *  最后更新日期
     */
    private Date lastUpdated;

    /**
     *  更新标志
     */
    private boolean latestVersion;

    /**
     *  版本号
     */
    private int version;

    /**
     *  评论
     */
    private String comment;

    /**
     *  原生模型类型(0、流程模型 2、表单模型 3、应用模型 4、决策表模型 5、案例模型)
     */
    private Integer modelType;

    /**
     *  租户id
     */
    private String tenantId;

    /**
     * 流程模型信息
     */
//    @NotNull(message = "bpmnXML不能为空")
    private String bpmnModelXML;

    /**
     *  模型类型
     */
//    @NotNull(message = "Type不能为空")
    private String type;


}
