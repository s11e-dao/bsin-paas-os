package me.flyray.bsin.domain.response;

import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysOrg;
import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.entity.SysUser;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Data;

/**
 * @author bolei
 * @date 2023/9/22
 * @desc
 */

@Data
public class SysUserVO implements Serializable {

    private SysTenant sysTenant;

    private SysUser sysUser;

    private SysOrg sysOrg;

    private List<SysPost> sysPost;

    private List<SysRole> sysRoleList;

    private Set<SysApp> sysAppSet;

    private String token;

}
