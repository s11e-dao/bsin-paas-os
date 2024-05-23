package me.flyray.bsin.domain.request;

import me.flyray.bsin.domain.entity.SysApp;

import javax.validation.constraints.NotNull;

import lombok.Data;
import me.flyray.bsin.mybatis.utils.Pagination;

import java.io.Serializable;

/**
 * @author bolei
 * @date 2023/9/26
 * @desc
 */

@Data
public class SysAppDTO extends SysApp implements Serializable {

    private String tenantId;

    @NotNull(message = "分页不能为空！")
    private Pagination pagination;

}
