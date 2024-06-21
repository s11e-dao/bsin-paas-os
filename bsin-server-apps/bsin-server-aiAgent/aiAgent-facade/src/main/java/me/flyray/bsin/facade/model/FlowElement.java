package me.flyray.bsin.facade.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author bolei
 * @date 2023/7/19 19:04
 * @desc
 */

@Data
public abstract class FlowElement {

    protected String id;
    protected String name;
    protected String documentation;

}
