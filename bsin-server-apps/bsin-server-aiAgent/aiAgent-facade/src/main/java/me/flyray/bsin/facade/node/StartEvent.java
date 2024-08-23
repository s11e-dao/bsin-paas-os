package me.flyray.bsin.facade.node;

import lombok.Data;
import me.flyray.bsin.facade.model.FlowNode;

/**
 * @author bolei
 * @date 2023/7/28 9:19
 * @desc
 */

@Data
public class StartEvent extends FlowNode {

    private String prologue;

}
