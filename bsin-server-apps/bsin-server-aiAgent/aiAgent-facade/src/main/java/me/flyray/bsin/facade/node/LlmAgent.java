package me.flyray.bsin.facade.node;

import lombok.Data;
import me.flyray.bsin.facade.model.FlowNode;

/**
 * @author bolei
 * @date 2023/7/20 16:56
 * @desc 大模型节点
 */

@Data
public class LlmAgent extends FlowNode {

    private String apiKey;

    private String model;

}
