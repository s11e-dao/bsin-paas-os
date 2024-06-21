package me.flyray.bsin.facade.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author bolei
 * @date 2023/7/19 19:07
 * @desc gpt节点 数据爬取节点
 *
 */

@Data
public abstract class FlowNode extends FlowElement {

    protected List<SequenceFlow> incomingFlows = new ArrayList<>();

    protected List<SequenceFlow> outgoingFlows = new ArrayList<>();

}
