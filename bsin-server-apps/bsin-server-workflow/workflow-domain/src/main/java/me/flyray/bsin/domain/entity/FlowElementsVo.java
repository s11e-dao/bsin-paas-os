package me.flyray.bsin.domain.entity;

import lombok.Data;
import org.flowable.bpmn.model.FlowableListener;

import java.util.List;

@Data
public class FlowElementsVo {

    private String id;

    private String name;

    private String documentation;

    private List<FlowableListener> flowableListenerList;
}
