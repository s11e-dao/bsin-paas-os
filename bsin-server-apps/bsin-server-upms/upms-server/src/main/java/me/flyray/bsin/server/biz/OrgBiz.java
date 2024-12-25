package me.flyray.bsin.server.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import me.flyray.bsin.domain.entity.SysOrg;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.response.OrgTree;
import me.flyray.bsin.infrastructure.mapper.UserMapper;

@Service
public class OrgBiz {

    @Autowired
    private UserMapper userMapper;

    public Integer getUserByOrgId(String orgId) {
        List<SysUser> sysUsers = userMapper.selectUserByPostIdAndOrgId(null, orgId);
        return sysUsers.size();
    }

    /**
     * 递归
     * @param root
     * @param all
     * @return
     */
    public List<OrgTree> getOrgTree(SysOrg root, List<SysOrg> all) {
        List<OrgTree> children = all.stream().filter(m -> {
            return m.getParentId().equals(root.getOrgId());
        }).map(m -> {
            OrgTree childMenu = new OrgTree(m.getOrgId(), m.getOrgCode(), m.getOrgName(), m.getSort(),
                    m.getParentId(), m.getLevel(), m.getType(), m.getLeader(), m.getPhone(), m.getAddress(),
                    m.getEmail(), m.getTenantId(), m.getCreateTime(), m.getRemark(), getUserByOrgId(m.getOrgId()),
                    getOrgTree(m, all)); //递归
            return childMenu;
        }).collect(Collectors.toList());

        return children;
    }
}
