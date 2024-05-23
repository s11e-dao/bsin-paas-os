package me.flyray.bsin.domain.request;

import lombok.Data;
import org.flowable.ui.modeler.model.ModelRepresentation;

@Data
public class ModelRepresentationDTO extends ModelRepresentation {
    private String modelTypeId;
}
