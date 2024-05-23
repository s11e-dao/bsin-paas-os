package me.flyray.bsin.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActDeModel {
    private String id;
    private String name;
    private String modelKey;
    private String description;
    private String modelComment;
    private Date created;
    private String createdBy;
    private Date lastUpdated;
    private String lastUpdatedBy;
    private Integer version;
    private String modelEditorJson;
    private String thumbnail;
    private Integer modelType;
    private String tenantId;
}
