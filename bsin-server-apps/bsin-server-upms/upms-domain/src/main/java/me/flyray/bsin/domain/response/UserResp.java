package me.flyray.bsin.domain.response;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysOrg;
import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.entity.SysUser;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResp implements Serializable {

    private SysTenant sysTenant;

    private SysUser sysUser;

    private SysOrg sysOrg;

    private List<SysPost> sysPost;

    private List<SysRole> sysRoleList;

    private Set<SysApp> sysAppSet;

}
