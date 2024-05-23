package me.flyray.bsin.domain.request;

import me.flyray.bsin.domain.entity.SysDict;
import lombok.Data;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.validate.QueryGroup;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SysDictDTO extends SysDict implements Serializable {

    @NotNull(message = "分页不能为空！", groups = QueryGroup.class)
    private Pagination pagination;

}
