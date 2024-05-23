package me.flyray.bsin.domain.request;

import me.flyray.bsin.domain.entity.SysPost;

import javax.validation.constraints.NotNull;

import lombok.Data;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.validate.QueryGroup;

import java.io.Serializable;

/**
 * @author bolei
 * @date 2023/9/26
 * @desc
 */

@Data
public class SysPostDTO extends SysPost implements Serializable {

    private String orgId;

    /**
     * 是否查询所有,默认不查
     */
    private Boolean selectAll = false;

    @NotNull(message = "分页不能为空！", groups = QueryGroup.class)
    private Pagination pagination;

}
