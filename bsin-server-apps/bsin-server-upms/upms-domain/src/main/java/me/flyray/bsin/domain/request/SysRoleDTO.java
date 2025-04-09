package me.flyray.bsin.domain.request;

import jakarta.validation.constraints.NotNull;
import me.flyray.bsin.domain.entity.SysRole;


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
public class SysRoleDTO extends SysRole implements Serializable {

    @NotNull(message = "分页不能为空！", groups = QueryGroup.class)
    private Pagination pagination;

}
