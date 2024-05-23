package me.flyray.bsin.facade.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author bolei
 * @date 2023/7/19 19:25
 * @desc 一个处理流程
 */

@Data
public class AiProcess {

    /**
     * 节点
     */
    protected List<FlowElement> flowElements;

    /**
     * 开始节点
     */
    protected FlowElement initialFlowElement;
}
