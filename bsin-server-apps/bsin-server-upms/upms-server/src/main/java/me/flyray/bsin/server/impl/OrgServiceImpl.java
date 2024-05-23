package me.flyray.bsin.server.impl;

import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.SysOrg;
import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.response.AppResp;
import me.flyray.bsin.domain.response.OrgResp;
import me.flyray.bsin.domain.response.OrgTree;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.enums.TenantOrgAppType;
import me.flyray.bsin.infrastructure.mapper.*;
import me.flyray.bsin.facade.service.OrgService;

import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.biz.AppBiz;
import me.flyray.bsin.server.biz.OrgBiz;
import me.flyray.bsin.utils.BsinSnowflake;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(rollbackFor = Exception.class)
@ShenyuDubboService(path = "/org", timeout = 10000)
@ApiModule(value = "org")
@Service
public class OrgServiceImpl implements OrgService {

    @Autowired
    private OrgMapper orgMapper;
    @Autowired
    private OrgAppMapper orgAppMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrgPostMapper orgPostMapper;
    @Autowired
    private OrgBiz orgBiz;
    @Autowired
    private AppMapper appMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private TenantAppMapper tenantAppMapper;
    @Autowired
    private PostRoleMapper postRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private AppBiz appBiz;

    /**
     * 新增机构
     *
     * @return
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysOrg add(SysOrg sysOrg) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        //判断机构编码是否已存在数据库
        SysOrg org = orgMapper.selectByOrgCode(sysOrg.getOrgCode());
        if (org != null) {
            throw new BusinessException(ResponseCode.ORG_CODE_EXIST);
        }
        String id = BsinSnowflake.getId();
        sysOrg.setOrgId(id);
        sysOrg.setTenantId(tenantId);
        orgMapper.insertOrg(sysOrg);
        return sysOrg;
    }

    /**
     * 删除机构
     * 1、判断该机构是否授权应用，如果有抛出异常
     * 2、判断机构是否被分配岗位，如果有抛出异常
     * 3、遍历机构列表，是否有子机构，如果有抛出异常
     * 4、删除机构
     */
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public SysOrg delete(String orgId) {
        // 要删除的对象
        SysOrg sysOrg = orgMapper.selectInfoById(orgId);
        if (sysOrg == null) {
            throw new BusinessException(ResponseCode.ORG_NOT_EXIST);
        }
        // 根据租户id查询所有机构
        List<SysOrg> orgList = orgMapper.selectOrgListByTenantId(sysOrg.getTenantId());
        // 判断机构应用是否有关联关系
        List<SysOrg> appByOrgId = orgAppMapper.getAppByOrgId(orgId);
        // 判断机构岗位是否有关联关系
        List<String> postIds = orgPostMapper.getPostIdsByOrgId(orgId);
        if (appByOrgId.size() > 0) {
            throw new BusinessException(ResponseCode.ORG_APP_IS_RELATED);
        }
        if (postIds.size() > 0) {
            throw new BusinessException(ResponseCode.ORG_POST_IS_RELATED);
        }
        // 遍历机构列表
        for (SysOrg org : orgList) {
            // 判断是否有子机构
            boolean isChildrenOrg = sysOrg.getOrgId().equals(org.getParentId());
            if (isChildrenOrg) {
                throw new BusinessException(ResponseCode.ORG_HAVE_CHILDREN_ORG);
            }
            // 删除机构
            orgMapper.deleteById(sysOrg.getOrgId());
            //删除机构下的用户
            userMapper.deleteUserById(sysOrg.getOrgId());
        }
        return sysOrg;
    }

    /**
     * 更新
     *
     * @return
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public SysOrg edit(SysOrg sysOrg) {
        sysOrg.setUpdateTime(new Date());
        orgMapper.updateById(sysOrg);
        return sysOrg;
    }

    /**
     * 根据条件查询机构列表
     */
    @ApiDoc(desc = "getList")
    @ShenyuDubboClient("/getList")
    @Override
    public List<OrgResp> getList(String orgCode, String orgName) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        SysOrg sysOrg = new SysOrg();
        sysOrg.setOrgCode(orgCode);
        sysOrg.setOrgName(orgName);
        sysOrg.setTenantId(tenantId);
        return orgMapper.selectOrgList(sysOrg);
    }


    /**
     * 展示机构数
     */
    @ApiDoc(desc = "getOrgTree")
    @ShenyuDubboClient("/getOrgTree")
    @Override
    public List<OrgTree> getOrgTree() {
        String tenantId = LoginInfoContextHelper.getTenantId();
        // 获取所有机构信息
        List<SysOrg> orgList = orgMapper.selectOrgListByTenantId(tenantId);
        // 组装成父子的树形目录结构
        // 查询所有的一级机构(parentId=-1)
        List<OrgTree> orgTreeList = orgList.stream().filter(org -> org.getParentId().equals("-1"))
                .map(m -> {
                    OrgTree levelOrg = new OrgTree(m.getOrgId(), m.getOrgCode(), m.getOrgName(), m.getSort(),
                            m.getParentId(), m.getLevel(), m.getType(), m.getLeader(), m.getPhone(), m.getAddress(),
                            m.getEmail(), m.getTenantId(), m.getCreateTime(), m.getRemark(), orgBiz.getUserByOrgId(m.getOrgId()), orgBiz.getOrgTree(m, orgList));
                    return levelOrg;
                }).collect(Collectors.toList());
        return orgTreeList;
    }


    /**
     * 根据现有id获取其本身和子部门信息
     * @param orgId
     * @return
     */
    @Override
    @ApiDoc(desc = "getOrgListById")
    @ShenyuDubboClient("/getOrgListById")
    public List<SysOrg> getOrgListById(String orgId) {
        return orgMapper.selectOrgListById(orgId);
    }


    /**
     * 根据租户id查询机构列表
     */
    @ApiDoc(desc = "getListByTenantId")
    @ShenyuDubboClient("/getListByTenantId")
    @Override
    public List<SysOrg> getListByTenantId() {
        String tenantId = LoginInfoContextHelper.getTenantId();
        return orgMapper.selectOrgListByTenantId(tenantId);
    }

    /**
     * 授权应用 注：创建租户时，顶级机构下的应用不能解除授权
     * 1、判断是否是顶级机构，顶级机构解绑时需要剔除默认应用id
     * 2、解绑需要解绑的应用，同时解除机构对应岗位的应用角色授权
     * 3、授权需要授权的应用
     *
     * @return
     */
    @ApiDoc(desc = "authorizationApps")
    @ShenyuDubboClient("/authorizationApps")
    @Override
    public void authorizationApps(String orgId, List<String> appIds) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        if (orgId == null) {
            throw new BusinessException(ResponseCode.ID_NOT_ISNULL);
        }
        // 获取机构下的授权应用
        List<String> authedAppIds = appMapper.selectAppIdsByOrgId(orgId);
        // a、全部解绑
        if (appIds.size() < 1) {
            // 判断是否是顶级机构，顶级机构下的默认应用不解绑
            if (orgMapper.selectInfoById(orgId).getParentId().equals("-1")) {
                // 解除新增、其他授权的应用绑定
                orgAppMapper.unAuthorizeAppsByOrgId(orgId, TenantOrgAppType.ADD.getCode());
                orgAppMapper.unAuthorizeAppsByOrgId(orgId, TenantOrgAppType.AUTH.getCode());
                // 去除默认应用
                authedAppIds = authedAppIds.stream().filter(appId -> orgAppMapper.selectOrgAppType(orgId, appId) != 0).collect(Collectors.toList());
                // 解除机构对应岗位的应用角色授权
                for (String authedAppId : authedAppIds) {
                    List<String> postIds = orgPostMapper.getPostIdsByOrgId(orgId);
                    for (String postId : postIds) {
                        postRoleMapper.unAssignRoles(postId, authedAppId);
                    }
                }
            } else {
                // 解除所有应用绑定
                orgAppMapper.unAuthorizeAppsByOrgId(orgId, null);
                // 解除机构对应岗位的应用角色授权
                for (String authedAppId : authedAppIds) {
                    List<String> postIds = orgPostMapper.getPostIdsByOrgId(orgId);
                    for (String postId : postIds) {
                        postRoleMapper.unAssignRoles(postId, authedAppId);
                    }
                }
            }
            return;
        }
        // b、重新授权逻辑
        // 1、需要解绑的应用ID
        List<String> unAuthAppIds = appBiz.getDifferentAppIds(authedAppIds, appIds);
        // 剔除集合中默认应用id ，顶级机构下默认应用不能解绑
        if (orgMapper.selectInfoById(orgId).getParentId().equals("-1")) {
            unAuthAppIds = unAuthAppIds.stream().filter(appId -> orgAppMapper.selectOrgAppType(orgId, appId) != 0).collect(Collectors.toList());
        }
        for (String unAuthAppId : unAuthAppIds) {
            // 解绑应用
            orgAppMapper.unAuthorizeAppsByOrgIdAndAppId(orgId, unAuthAppId);
            // 解除机构对应岗位的应用角色授权
            List<String> postIds = orgPostMapper.getPostIdsByOrgId(orgId);
            for (String postId : postIds) {
                postRoleMapper.unAssignRoles(postId, unAuthAppId);
            }
        }
        // 2、需要授权的应用ID
        List<String> dealAuthAppIds = appBiz.getDifferentAppIds(appIds, authedAppIds);
        if (dealAuthAppIds.size() < 1) {
            return;
        }
        // 重新授权应用
        for (String appId : dealAuthAppIds) {
            String type = tenantAppMapper.selectTenantAppType(tenantId, appId).toString();
            orgAppMapper.authorizeApp(orgId, appId, type);
        }
    }

    @ApiDoc(desc = "authorizeMercahntOrgApps")
    @ShenyuDubboClient("/authorizeMercahntOrgApps")
    @Override
    public void authorizeMercahntOrgApps(String orgCode, List<String> appIds) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        SysOrg sysOrg = orgMapper.selectOrg(tenantId,orgCode);
        for (String appId : appIds) {
            // 1 将应用授权给商户对应的机构
            String type = tenantAppMapper.selectTenantAppType(tenantId, appId);
            orgAppMapper.authorizeApp(sysOrg.getOrgId(), appId, type);
            // TODO 需优化（属于功能订阅能力） 2 将应用的角色授权给商户用户对应的岗位（授权基础功能）
            // 查询商户对应应用的默认角色（需解决默认角色）
            List<SysRole> sysRoles = roleMapper.selectListByAppId(appId, tenantId);
            String roleId = sysRoles.get(0).getRoleId();
            // 查询机构的默认岗位 （需解决默认岗位）
            List<SysPost> sysPosts = postMapper.selectPostListByOrgId(sysOrg.getOrgId(),null,null);
            String postId = sysPosts.get(0).getPostId();
            postRoleMapper.assignRole(postId, roleId, appId);
        }
    }

    @ApiDoc(desc = "assignPost")
    @ShenyuDubboClient("/assignPost")
    @Override
    public void assignPost(String orgId, List<String> postIds) {
        if (orgId == null) {
            throw new BusinessException(ResponseCode.ID_NOT_ISNULL);
        }
        List<SysPost> postList = postMapper.selectPostListByOrgId(orgId,null,null);
        sign:
        for (SysPost post: postList) {
            for (String postId:postIds) {
                if( post.getPostId().equals(postId)){
                    continue sign;
                }
            }
            List<SysUser> userList = userMapper.selectUserByPostIdAndOrgId(post.getPostId(), orgId);
            if(userList.size()>0){
                throw new BusinessException(ResponseCode.USER_POST_IS_RELATED);
            }
        }
        orgPostMapper.unbindPost(orgId);
        if(postIds.size() < 1){
            return;
        }
        orgPostMapper.assignPosts(orgId, postIds);
    }


    /**
     * 根据机构id和岗位id解绑岗位
     * 1、判断岗位是否被授权给其他用户
     * 2、解除绑定
     */
    @ApiDoc(desc = "unbindPost")
    @ShenyuDubboClient("/unbindPost")
    @Override
    public void unbindPost(String orgId, String postId) {
        List<SysUser> userList = userMapper.selectUserByPostIdAndOrgId(postId, orgId);
        if (userList.size() > 0) {
            throw new BusinessException(ResponseCode.USER_POST_IS_RELATED);
        }
        orgPostMapper.unbindPosts(orgId, postId);
    }

    /**
     * 查询机构下的授权应用集合
     */
    @ApiDoc(desc = "getAppListByOrgId")
    @ShenyuDubboClient("/getAppListByOrgId")
    @Override
    public List<AppResp> getAppListByOrgId(String orgId) {
        return appMapper.selectOrgAppTypeListByOrgId(orgId);
    }


    /**
     * 根据机构id获取机构
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public SysOrg getDetail(String orgId) {
        SysOrg sysOrg = orgMapper.selectInfoById(orgId);
        return sysOrg;
    }

    @Override
    public List<SysOrg> getOrgListByIds(List<String> orgIds) {

            return orgMapper.selectListByIds(orgIds);
    }

    @Override
    @ApiDoc(desc = "getOrgListByParentId")
    @ShenyuDubboClient("/getOrgListByParentId")
    public List<SysOrg> getOrgListByParentId(String parentId) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        return orgMapper.selectOrgListByParentId(parentId,tenantId);
    }

    @Override
    public List<SysOrg> getSubOrgList(String tenantId) {
        return orgMapper.getSubOrgList(tenantId);
    }
}
