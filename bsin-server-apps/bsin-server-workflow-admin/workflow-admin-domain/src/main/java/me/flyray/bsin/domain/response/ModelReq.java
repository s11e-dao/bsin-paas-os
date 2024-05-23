package me.flyray.bsin.domain.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huangzh
 * @ClassName ModelReq
 * @DATE 2020/8/4 17:04
 */

@Data
public class ModelReq extends ModelRepresentation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 对应Bpmn模型信息
     */
    private String bpmnModelXml;

    /**
     * 新版本标志位
     */
    private boolean isNewVersion;

    /**
     * 页数
     */
    private int pageNum = 1;

    /**
     * 分页大小
     */
    private int pageSize = 5;

    /**
     * 是否包含新版本标志
     */
    private boolean includeNewVersion = false;

    /**
     * 历史版本编号
     */
    private String modelHistoryId;

    /**
     * 模型类型
     */
    private String type;

    /**
     * 最后更新人
     */
    private String updatedBy;

    /**
     * 导入流程模型文件名
     */
    private String fileName;

    /**
     * 导入的model信息
     */
    private ModelInfo modelInfo;


}
