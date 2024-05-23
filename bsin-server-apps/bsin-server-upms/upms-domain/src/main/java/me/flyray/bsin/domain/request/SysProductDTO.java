package me.flyray.bsin.domain.request;

    import me.flyray.bsin.domain.entity.SysProduct;

import javax.validation.constraints.NotNull;

import lombok.Data;
    import me.flyray.bsin.mybatis.utils.Pagination;

    import java.io.Serializable;

/**
 * @author bolei
 * @date 2024/1/18
 * @desc
 */

@Data
public class SysProductDTO extends SysProduct implements Serializable {

    @NotNull(message = "分页不能为空！")
    private Pagination pagination;

}
