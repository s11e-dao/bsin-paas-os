package me.flyray.bsin.domain.response;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ModelTypeTree {

    /**
     * 模型类型id
     */
    private String id;

    /**
     * 模型类型code
     */
    private String typeCode;

    /**
     * 模型类型name
     */
    private String typeName;

    /**
     * 父类模型id
     */
    private String parentId;

    /**
     * 描述
     */
    private String description;

    /**
     * 删除标志
     */
    private String delFlag;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;
    /**
     * 机构子集
     */
    List<ModelTypeTree> children;

    public ModelTypeTree(String id, String typeCode, String typeName, String parentId,
                         String description, String delFlag, Date createTime, Date updateTime, List<ModelTypeTree> children) {
        this.id = id;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.parentId = parentId;
        this.description = description;
        this.delFlag = delFlag;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.children = children;
    }
}
