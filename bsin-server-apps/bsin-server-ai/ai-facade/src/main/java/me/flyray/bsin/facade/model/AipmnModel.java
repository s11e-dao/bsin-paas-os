package me.flyray.bsin.facade.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author bolei
 * @date 2023/7/19 19:03
 * @desc 一个模型可能存在多个流程，子流程
 */

@Data
public class AipmnModel {

    /**
     * 模型名称
     */

    /**
     * 属性
     */

    /**
     * process
     */
    private List<AiProcess> aiProcesses;

    /**
     * 连线
     */
    private List<SequenceFlow> sequenceFlows;

}
