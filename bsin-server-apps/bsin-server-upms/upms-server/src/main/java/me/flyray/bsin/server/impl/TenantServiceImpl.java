package me.flyray.bsin.server.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.MD5;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysMenu;
import me.flyray.bsin.domain.entity.SysOrg;
import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysProduct;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.request.SysTenantDTO;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.enums.UserType;
import me.flyray.bsin.infrastructure.mapper.AppMapper;
import me.flyray.bsin.infrastructure.mapper.MenuMapper;
import me.flyray.bsin.infrastructure.mapper.OrgAppMapper;
import me.flyray.bsin.infrastructure.mapper.OrgMapper;
import me.flyray.bsin.infrastructure.mapper.OrgPostMapper;
import me.flyray.bsin.infrastructure.mapper.PostMapper;
import me.flyray.bsin.infrastructure.mapper.PostRoleMapper;
import me.flyray.bsin.infrastructure.mapper.ProductAppMapper;
import me.flyray.bsin.infrastructure.mapper.ProductMapper;
import me.flyray.bsin.infrastructure.mapper.RoleMapper;
import me.flyray.bsin.infrastructure.mapper.RoleMenuMapper;
import me.flyray.bsin.infrastructure.mapper.TenantAppMapper;
import me.flyray.bsin.infrastructure.mapper.TenantMapper;
import me.flyray.bsin.infrastructure.mapper.UserMapper;
import me.flyray.bsin.infrastructure.mapper.UserPostMapper;
import me.flyray.bsin.infrastructure.config.TenantConfig;
import me.flyray.bsin.facade.enums.TenantOrgAppType;
import me.flyray.bsin.facade.service.TenantService;

import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.enums.BizRoleType;
import me.flyray.bsin.server.biz.AppBiz;
import me.flyray.bsin.utils.BsinSnowflake;

import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ShenyuDubboService(path = "/tenant", timeout = 60000)
@ApiModule(value = "tenant")
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantConfig tenantConfig;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private TenantAppMapper tenantAppMapper;
    @Autowired
    private OrgMapper orgMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private OrgPostMapper orgPostMapper;
    @Autowired
    private UserPostMapper userPostMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private PostRoleMapper postRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private OrgAppMapper orgAppMapper;
    @Autowired
    private AppMapper appMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductAppMapper productAppMapper;
    @Autowired
    private AppBiz appBiz;

    /**
     * 添加租户
     * 1、添加租户
     * 2、初始化租户对应的机构
     * 3、初始租户的超级管理员
     * 4、初始化一个岗位
     * 5、建立机构与岗位的关系、用户与岗位的关系
     * 6、给租户及租户机构授权默认可访问应用（权限管理应用）
     * 7、获取默认普通租户权限管理角色
     * 8、给角色授予菜单权限
     * 9、移除租户管理菜单（租户管理只有超级管理员才具备，普通租户没有权限）
     * 10、建立岗位与角色的关系
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Transactional
    @Override
    public SysTenant add(SysTenantDTO sysTenantReq) {

        SysTenant sysTenant = new SysTenant();
        BeanUtil.copyProperties(sysTenantReq, sysTenant);
        SysTenant tenantInfo = tenantMapper.getTenantByTenantCode(null, sysTenantReq.getTenantCode());
        if (tenantInfo != null) {
            throw new BusinessException(ResponseCode.TENANT_CODE_EXISTS);
        }

        // 1、添加租户
        String tenantId = BsinSnowflake.getId();
        sysTenant.setTenantId(tenantId);
        tenantMapper.insert(sysTenant);

        // 2、初始化租户对应的机构
        String orgId = BsinSnowflake.getId();
        SysOrg sysOrg = new SysOrg(orgId, sysTenant.getTenantCode(), sysTenant.getTenantName(), tenantId);
        orgMapper.insertOrg(sysOrg);

        // 3、用户：初始租户的超级管理员
        String password = sysTenantReq.getPassword();
        SysUser adminUser = addTenantAdminUser(tenantId, sysTenant.getUsername(), password, orgId);
        String userId = adminUser.getUserId();

        // 4、初始化一个岗位
        String postId = BsinSnowflake.getId();
        SysPost sysPost = new SysPost(postId, postId, tenantConfig.getPostName(), tenantId);
        postMapper.insertPost(sysPost);

        // 5、建立机构与岗位的关系、用户与岗位的关系
        List<String> posts = new ArrayList<>();
        posts.add(postId);
        orgPostMapper.assignPosts(orgId, posts);
        userPostMapper.assignPosts(userId, posts);

        // 6、给租户及租户机构授权默认可访问应用（权限管理应用）
        String appId = tenantConfig.getAppId();
        List<String> appIds = new ArrayList<>();
        appIds.add(appId);

        // 默认给租户和机构授权应用管理
        tenantAppMapper.authorizeApps(tenantId, appIds, TenantOrgAppType.DEF_AUTH.getCode());
        orgAppMapper.authorizeApp(orgId, appId, TenantOrgAppType.DEF_AUTH.getCode());

        // 7、获取默认普通租户权限管理角色
        String roleId = BsinSnowflake.getId();
        SysRole sysRole = new SysRole(roleId, tenantConfig.getRoleName(), sysTenant.getTenantCode(), appId, tenantId, tenantConfig.getRoleType(), null);
        roleMapper.insert(sysRole);

        // 8、给角色授予菜单权限
        List<SysMenu> sysMenus = menuMapper.selectListByAppId(appId);
        List<String> authMenuIds = new ArrayList<>();
        List<String> roleIds = new ArrayList<>();
        roleIds.add(roleId);
        // 9、移除租户管理菜单（租户管理只有超级管理员才具备，普通租户没有权限）
        for (SysMenu sysMenu : sysMenus) {
            if (sysMenu.getMenuName().equals("租户管理")) {
                continue;
            }
            authMenuIds.add(sysMenu.getMenuId());
        }
        roleMenuMapper.authorizeMenus(appId, roleId, authMenuIds);

        // 10、建立岗位与角色的关系
        postRoleMapper.assignRoles(postId, roleIds, appId);
        // 给dao治理服务角色授权dao治理服务应用菜单和分配流程管理应用角色

        // TODO 添加租户用户业务应用、应用角色、角色权限、岗位角色
        // 重复6、7、8、9、10动作
        String productCode = sysTenantReq.getProductCode();
        if(productCode != null) {
            addTenantBizAppsPermission(tenantId, orgId, postId, sysTenant.getTenantCode(), productCode);
        }
        return sysTenant;
    }

    private void validSaveBefore(SysTenant sysTenant) {
        //判断租户编号是否已经存在
        long codeCount = tenantMapper.getCountByTenantCode(sysTenant.getTenantCode(), sysTenant.getTenantId());
        if (codeCount > 0) {
            throw new BusinessException(ResponseCode.TENANT_CODE_EXISTS);
        }
    }

    /**
     * 删除租户
     * 删除租户对应应用的授权信息
     * 暂时先直接逻辑删除租户信息
     */
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public void delete(String tenantId) {
        tenantMapper.deleteById(tenantId);
    }

    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public SysTenant edit(SysTenant sysTenantReq) {
        validSaveBefore(sysTenantReq);
        SysTenant sysTenant = tenantMapper.selectById(sysTenantReq.getTenantId());
        if (StringUtils.isNotBlank(sysTenantReq.getLogo())) {
            sysTenant.setLogo(sysTenantReq.getLogo());
        }
        if (StringUtils.isNotBlank(sysTenantReq.getTenantName())) {
            sysTenant.setTenantName(sysTenantReq.getTenantName());
        }
        sysTenant.setLogo(sysTenantReq.getLogo());
        tenantMapper.updateById(sysTenant);
        return sysTenant;
    }

    @Override
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    public SysTenant getDetail(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            tenantId = LoginInfoContextHelper.getTenantId();
        }
        SysTenant sysTenant = tenantMapper.selectById(tenantId);
        return sysTenant;
    }


    /**
     * 分页查询展示租户列表
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<SysTenant> getPageList(SysTenantDTO tenantDTO) {
        String tenantCode = tenantDTO.getTenantCode();
        String tenantName = tenantDTO.getTenantName();
        String tenantId = tenantDTO.getTenantId();
        Pagination pagination = tenantDTO.getPagination();
        Page<SysTenant> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        LambdaQueryWrapper<SysTenant> warapper = new LambdaQueryWrapper<>();
        warapper.orderByDesc(SysTenant::getCreateTime);
        IPage<SysTenant> pageList = tenantMapper.selectPageList(page, tenantName, tenantCode, tenantId);
        return pageList;
    }

    /**
     * 给租户授权应用，使用场景：超级租户管理员给其他租户授权默认应用
     * 1、先解除给租户的”授权应用“，租户自己添加的应用保持不变 type:1租户新增，2其他授权
     * 2、解除租户授权给机构的”授权应用“
     * 3、解除租户给机构岗位分配的”授权应用“的角色
     * 4、给租户添加授权应用关系
     * 5、添加授权应用的默认角色
     * 6、给租户用户对应的默认岗位授权默认角色
     */
    @ApiDoc(desc = "authorizeApps")
    @ShenyuDubboClient("/authorizeApps")
    @Override
    public void authorizeApps(String tenantId, List<String> appIds) {
        final SysTenant sysTenant = tenantMapper.selectTenantInfoByTenantId(tenantId);
        if (sysTenant.getType() == 0) {
            throw new BusinessException(ResponseCode.SUB_TENANT_NOT_AUTH);
        }
        // 获取已经授权的应用ID
        List<String> authedAppIds = appMapper.selectAppIdsByTenantIdAndAppName(tenantId, null);
        List<SysOrg> tenantOrgs = orgMapper.selectOrgListByTenantId(tenantId);
        // 查询租户已授权的应用，不包括租户添加的应用
        // a、全部解除授权逻辑
        if (appIds.size() < 1) {
            // 1、解除给租户的”授权应用“
            tenantAppMapper.unAuthorizeAppByTenantId(tenantId, TenantOrgAppType.AUTH.getCode());
            // 2、解绑租户对应机构下的所有”授权应用“
            for (SysOrg tenantOrg : tenantOrgs) {
                orgAppMapper.unAuthorizeAppsByOrgId(tenantOrg.getOrgId(), TenantOrgAppType.AUTH.getCode());
                // 3、解除机构对应岗位的应用角色授权
                for (String authedAppId : authedAppIds) {
                    List<String> postIds = orgPostMapper.getPostIdsByOrgId(tenantOrg.getOrgId());
                    for (String postId : postIds) {
                        postRoleMapper.unAssignRoles(postId, authedAppId);
                    }
                }
            }
            return;
        }
        // b、重新授权逻辑 authedAppIds中存在一部分授权不变，一部分需要解除授权 appIds存在一部分不变，一部分添加授权
        // 获取需要解除授权的应用ID
        // authedAppIds:[1,2,5]，sysApps:[1,2,3,4]
        List<String> unAuthAppIds = appBiz.getDifferentAppIds(authedAppIds, appIds);
        // 剔除需集合中默认应用id ，租户下的默认应用不能解绑
        for (String unAuthAppId : unAuthAppIds) {
            // 1、解除给租户的”授权应用“
            tenantAppMapper.unAuthorizeApp(tenantId, unAuthAppId);
            // 2、解绑租户对应机构下的需要解除的”授权应用“
            for (SysOrg tenantOrg : tenantOrgs) {
                orgAppMapper.unAuthorizeAppsByOrgIdAndAppId(tenantOrg.getOrgId(), unAuthAppId);
                // 3、解除机构对应岗位的应用角色授权
                List<String> postIds = orgPostMapper.getPostIdsByOrgId(tenantOrg.getOrgId());
                for (String postId : postIds) {
                    postRoleMapper.unAssignRoles(postId, unAuthAppId);
                }
            }
        }
        // 需要授权的应用ID
        List<String> dealAuthAppIds = appBiz.getDifferentAppIds(appIds, authedAppIds);
        if (dealAuthAppIds.size() < 1) {
            return;
        }
        tenantAppMapper.authorizeApps(tenantId, dealAuthAppIds, TenantOrgAppType.AUTH.getCode());
        // 添加授权应用的默认角色
        String roleId;
        for (String appId : dealAuthAppIds) {
            roleId = BsinSnowflake.getId();
            SysRole sysRole = new SysRole(roleId, tenantConfig.getRoleName(), roleId, appId, tenantId, 2, null);
            roleMapper.insert(sysRole);
            List<SysMenu> sysMenus = menuMapper.selectListByAppId(appId);
            List<String> menusIds = sysMenus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
            roleMenuMapper.authorizeMenus(appId, roleId, menusIds);
        }
        // TODO 缺少6操作
    }

    /**
     * 获取所有租户列表
     */
    @ApiDoc(desc = "getAllTenantList")
    @ShenyuDubboClient("/getAllTenantList")
    @Override
    public List<SysTenant> getAllTenantList() {
        return tenantMapper.selectAllList();
    }

    private SysUser addTenantAdminUser(String tenantId, String username, String password, String orgId) {
        String userId = BsinSnowflake.getId();
        if (StringUtils.isEmpty(password)) {
            password = tenantConfig.getPassword();
        }
        SysUser sysUser = new SysUser(userId, username, password, orgId, tenantId);
        sysUser.setBizRoleType(BizRoleType.TENANT.getCode());
        sysUser.setType(UserType.TENANT.getCode());
        userMapper.insertUser(sysUser);
        return sysUser;
    }

    /**
     * 6、给租户及租户机构授权业务应用
     * 7、添加业务应用角色
     * 8、给角色授予菜单权限
     * 9、移除租户管理菜单（租户管理只有超级管理员才具备，普通租户没有权限）
     * 10、建立岗位与角色的关系
     */
    private void addTenantBizAppsPermission(String tenantId, String orgId, String postId, String tenantCode, String tenantProductCode) {

        // 根据产品tenantProductCode查询产品对应的应用ID
        List<String> bizAppIds = productAppMapper.selectListByProductCode(tenantProductCode);
        if (bizAppIds.size() == 0) {
            return;
        }
        // 6、给租户及租户机构授权业务应用
        tenantAppMapper.authorizeApps(tenantId, bizAppIds, TenantOrgAppType.DEF_AUTH.getCode());
        // 租户机构授权业务应用
        orgAppMapper.authorizeApps(orgId, bizAppIds, TenantOrgAppType.DEF_AUTH.getCode());
        for (String bizAppId : bizAppIds) {
            // 7、新建业务应用角色
            String bizAppRoleId = BsinSnowflake.getId();
            SysRole bizRole = new SysRole(bizAppRoleId, tenantConfig.getBizRoleName(), tenantCode, bizAppId, tenantId, tenantConfig.getRoleType(), null);
            roleMapper.insert(bizRole);

            // 8、给角色授予菜单权限
            List<SysMenu> bizAppMenus = menuMapper.selectListByAppId(bizAppId);
            List<String> bizAuthMenuIds = new ArrayList<>();
            // TODO 9、移除租户管理菜单
            for (SysMenu sysMenu : bizAppMenus) {
                tenantConfig.getPlatformMenus();
                if (tenantConfig.getPlatformMenus().contains(sysMenu.getMenuName())) {
                    continue;
                }
                bizAuthMenuIds.add(sysMenu.getMenuId());
            }
            roleMenuMapper.authorizeMenus(bizAppId, bizAppRoleId, bizAuthMenuIds);
            // 该方法是给一个岗位分配一个应用下的多个角色
            // 10、建立岗位与角色的关系
            // 给租户默认岗位分配产品对应的应用下的默认角
            postRoleMapper.assignRole(postId, bizAppRoleId, bizAppId);
        }
    }

    @ApiDoc(desc = "getTenantBaseApp")
    @ShenyuDubboClient("/getTenantBaseApp")
    @Override
    public SysApp getTenantBaseApp() {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String bizRoleType = LoginInfoContextHelper.getBizRoleType();
        // 查询商户对应租户代理的产品的基础应用 一个租户对应一个产品
        SysTenant sysTenant = tenantMapper.selectTenantInfoByTenantId(tenantId);
        SysProduct sysProduct = productMapper.selectByProductCode(sysTenant.getProductCode());
        SysApp baseApp = tenantAppMapper.selectTenantBaseApp(tenantId, sysProduct.getProductId(), bizRoleType);
        return baseApp;
    }

    public static void main(String[] args) {
        String pad = MD5.create().digestHex("123456");
        System.out.println(pad);
    }

}
