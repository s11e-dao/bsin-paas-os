package me.flyray.bsin.domain.request;

import me.flyray.bsin.domain.entity.SysMenu;

import lombok.Data;

import java.io.Serializable;

/**
 * @author bolei
 * @date 2023/9/26
 * @desc
 */

@Data
public class SysMenuDTO extends SysMenu implements Serializable {

    private String tenantId;

}
