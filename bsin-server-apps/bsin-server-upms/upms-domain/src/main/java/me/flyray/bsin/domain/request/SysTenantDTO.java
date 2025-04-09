package me.flyray.bsin.domain.request;

import jakarta.validation.constraints.NotNull;
import me.flyray.bsin.domain.entity.SysTenant;

import lombok.Data;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.QueryGroup;

import java.io.Serializable;


/**
 * @author HLW
 **/
@Data
public class SysTenantDTO extends SysTenant implements Serializable {

    /**
     * 备注不能为空
     */
    @NotNull(message = "密码不能为空！", groups = AddGroup.class)
    private String password;

    @NotNull(message = "分页不能为空！", groups = QueryGroup.class)
    private Pagination pagination;

}
