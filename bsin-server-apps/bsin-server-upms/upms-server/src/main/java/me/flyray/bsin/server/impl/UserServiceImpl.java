package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.security.enums.BizRoleType;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysAppFunction;
import me.flyray.bsin.domain.entity.SysOrg;
import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysProduct;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.entity.SysTenant;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.entity.UserPost;
import me.flyray.bsin.domain.entity.UserRole;
import me.flyray.bsin.domain.request.SysUserDTO;
import me.flyray.bsin.domain.response.AppListResp;
import me.flyray.bsin.domain.response.SysUserVO;
import me.flyray.bsin.domain.response.UserResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.enums.TenantOrgAppType;
import me.flyray.bsin.facade.enums.UserStatusEnum;
import me.flyray.bsin.facade.enums.UserType;
import me.flyray.bsin.facade.service.UserService;
import me.flyray.bsin.infrastructure.config.TenantConfig;
import me.flyray.bsin.infrastructure.mapper.AppFunctionMapper;
import me.flyray.bsin.infrastructure.mapper.AppMapper;
import me.flyray.bsin.infrastructure.mapper.MenuMapper;
import me.flyray.bsin.infrastructure.mapper.OrgAppMapper;
import me.flyray.bsin.infrastructure.mapper.OrgMapper;
import me.flyray.bsin.infrastructure.mapper.OrgPostMapper;
import me.flyray.bsin.infrastructure.mapper.PostMapper;
import me.flyray.bsin.infrastructure.mapper.PostRoleMapper;
import me.flyray.bsin.infrastructure.mapper.ProductMapper;
import me.flyray.bsin.infrastructure.mapper.RoleMapper;
import me.flyray.bsin.infrastructure.mapper.RoleMenuMapper;
import me.flyray.bsin.infrastructure.mapper.TenantAppMapper;
import me.flyray.bsin.infrastructure.mapper.TenantMapper;
import me.flyray.bsin.infrastructure.mapper.UserMapper;
import me.flyray.bsin.infrastructure.mapper.UserPostMapper;
import me.flyray.bsin.infrastructure.mapper.UserRoleMapper;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.authentication.AuthenticationProvider;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.utils.BsinSnowflake;
import me.flyray.bsin.utils.EmptyChecker;

/**
 * @author bolei
 * @date 2023/9/22
 * @desc
 */

@ShenyuDubboService(path = "/user" ,timeout = 15000)
@ApiModule(value = "user")
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrgPostMapper orgPostMapper;
    @Autowired
    private UserPostMapper userPostMapper;
    @Autowired
    private OrgMapper orgMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private AppMapper appMapper;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private OrgAppMapper orgAppMapper;
    @Autowired
    private TenantConfig tenantConfig;

    @Value("${bsin.security.authentication-secretKey}")
    private String authSecretKey;
    @Value("${bsin.security.authentication-expiration}")
    private int authExpiration;
    @Value("${bsin.sysAgent.app-id}")
    private String sysAgentAppId;

    @Autowired
    private TenantAppMapper tenantAppMapper;
    @Autowired
    private AppFunctionMapper appFunctionMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private PostRoleMapper postRoleMapper;
    @Autowired
    private ProductMapper productMapper;


    @ApiDoc(desc = "login")
    @ShenyuDubboClient("/login")
    @Override
    public SysUserVO login(SysUser sysUserReq) {
        String tenantId = sysUserReq.getTenantId();
        String username = sysUserReq.getUsername();
        String password = sysUserReq.getPassword();
        // 判断用户名密码是否为空
        if (EmptyChecker.isEmpty(username) || EmptyChecker.isEmpty(password)) {
            throw new BusinessException(ResponseCode.USERNAME_PASSWORD_ERROR);
        }
        // 用户选择租户 登录返回的用户对象信息
        SysUser sysUser = userMapper.login(tenantId, username, password);
        return this.login(sysUserReq, sysUser);
    }


    /**
     * 校验用户是否已存在
     *
     * @param sysUserDTO
     */
    void verifyIfUserExists(SysUser sysUserDTO) {
        SysUser sysUser = new SysUserDTO();
        sysUser.setUsername(sysUserDTO.getUsername());
        sysUser.setTenantId(sysUserDTO.getTenantId());
        sysUser.setUserId(sysUserDTO.getUserId());
        if (StrUtil.isNotBlank(sysUser.getUsername())) {
            Integer userNameExists = userMapper.verifyIfUserExists(sysUser);
            if (ObjectUtil.isNotEmpty(userNameExists)) {
                throw new BusinessException(ResponseCode.USERNAME_EXIST);
            }
        }
        if (StrUtil.isNotBlank(sysUserDTO.getPhone())) {
            sysUser.setUsername(null);
            sysUser.setPhone(sysUserDTO.getPhone());
            sysUser.setEmail(sysUserDTO.getEmail());
            Integer phoneEmailExists = userMapper.verifyIfUserExists(sysUser);
            if (ObjectUtil.isNotEmpty(phoneEmailExists)) {
                throw new BusinessException(ResponseCode.PHOEN_EMAIL_EXIST);
            }
        }
    }

    /**
     * 新增
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysUser add(SysUserDTO sysUser) {
        String tenantId = sysUser.getTenantId();
        if (tenantId == null) {
            tenantId = LoginInfoContextHelper.getTenantId();
        }
        sysUser.setTenantId(tenantId);
        // 判断用户是否已经存在
        verifyIfUserExists(sysUser);

        String userId = sysUser.getUserId();
        if (userId == null) {
            userId = BsinSnowflake.getId();
        }
        sysUser.setTenantId(tenantId);
        if (StringUtils.isBlank(sysUser.getOrgId())) {
            sysUser.setOrgId("-1");
        }
        if (StringUtils.isNotBlank(sysUser.getRoleId())) {
            //添加角色和user关系
            UserRole userRole = new UserRole();
            userRole.setUserId(sysUser.getUserId());
            userRole.setRoleId(sysUser.getRoleId());
            userRole.setAppId(sysUser.getAppId());
            userRoleMapper.insert(userRole);
        }
        sysUser.setUserId(userId);
        userMapper.insertUser(sysUser);
        return sysUser;
    }

    /**
     * 1、查询租户顶级机构，在租户顶级机构下添加一个部门，如果是门店根据商户的信息查询门店的上级机构
     * 2、添加添加商户部门用户
     * 3、添加商户岗位 建立机构与岗位的关系、用户与岗位的关系
     * 4、给用户分配新岗位
     * 5、查询商户对应租户代理的产品的基础应用 一个租户对应一个产品
     * 6、给商户部门分配可以访问的应用
     * 7、查出基础应用的基础功能
     * 8、新增角色
     * 9、为该角色添加基础功能菜单
     * 10、建立商户岗位与角色的关系
     * @param sysUserReq
     * @return
     */
    @ApiDoc(desc = "addMerchantOrStore")
    @ShenyuDubboClient("/addMerchantOrStore")
    @Transactional
    @Override
    public SysUser addMerchantOrStoreUser(SysUserDTO sysUserReq) {
        log.info("请求 addMerchantUser 参数: {}", sysUserReq);
        SysUser sysUser = new SysUser();
        BeanUtil.copyProperties(sysUserReq, sysUser);

        String merchantNo = sysUserReq.getMerchantId();
        String tenantId = sysUserReq.getTenantId();
        // TODO 1、查询租户顶级机构，在租户顶级机构下添加一个部门，如果是门店根据商户的信息查询门店的上级机构
        SysOrg sysOrg = orgMapper.selectTopOrgByTenantId(sysUser.getTenantId());

        // 在租户下添加商户部门
        SysOrg merchantOrg = new SysOrg();
        String orgId = BsinSnowflake.getId();
        merchantOrg.setTenantId(sysUser.getTenantId());
        merchantOrg.setOrgId(orgId);
        merchantOrg.setParentId(sysOrg.getOrgId());
        merchantOrg.setOrgName(sysUser.getUsername());
        merchantOrg.setOrgCode(merchantNo);
        orgMapper.insertOrg(merchantOrg);

        // 2、添加添加商户部门用户
        sysUser.setType(UserType.MERCHANT.getCode());
        sysUser.setOrgId(orgId);
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        sysUser.setUserId(snowflake.nextIdStr());
        sysUser.setStoreId(sysUserReq.getStoreId());
//        sysUser.setMerchantId(merchantNo);
        //初始化密码 123456
        sysUser.setPassword("e10adc3949ba59abbe56e057f20f883e");
        userMapper.insertUser(sysUser);

        // 3、添加商户默认岗位 建立机构与岗位的关系、用户与岗位的关系
        String postId = BsinSnowflake.getId();
        SysPost sysPost = new SysPost();
        sysPost.setPostId(postId);
        sysPost.setTenantId(sysUser.getTenantId());
        sysPost.setPostCode(merchantNo);
        sysPost.setPostName(sysUser.getUsername() + "岗");
        postMapper.insertPost(sysPost);
        List<String> postIds = new ArrayList<>();
        postIds.add(postId);
        orgPostMapper.assignPosts(orgId, postIds);

        // 4、给用户分配新岗位
        userPostMapper.assignPosts(merchantNo, postIds);

        // TODO 一个租户代理两个产品会存在两个基础应用
        // 5、查询商户对应租户代理的产品的基础应用 一个租户对应一个产品
        SysTenant sysTenant = tenantMapper.selectTenantInfoByTenantId(tenantId);
        SysProduct sysProduct = productMapper.selectByProductCode(sysTenant.getProductCode());
        SysApp baseApp = tenantAppMapper.selectTenantBaseApp(tenantId, sysProduct.getProductId(), BizRoleType.MERCHANT.getCode());
        // 根据业务角色查询默认应用，商户角色默认应用不存在是跟租户相同
        if(baseApp == null){
            baseApp = tenantAppMapper.selectTenantBaseApp(tenantId, sysProduct.getProductId(), null);
        }
        // 6、给商户部门分配可以访问的应用
        orgAppMapper.authorizeApp(orgId, baseApp.getAppId(), TenantOrgAppType.AUTH.getCode());

        // 7、查出基础应用的基础功能
        List<SysAppFunction> appFunctionList = appFunctionMapper.selectListByAppId(baseApp.getAppId());
        // 查询基础功能对应的菜单
        List<String> appFunctionIds = new ArrayList<>();
        for (SysAppFunction sysAppFunction : appFunctionList) {
            appFunctionIds.add(sysAppFunction.getAppFunctionId());
        }
        // 查询基础功能菜单
        List<String> authMenuIds = menuMapper.selectListByAppFunctionIds(appFunctionIds);

        // 方案一： 为基础应用配置一个统一的基础角色 方案二： 为每个商户添加新的角色（选择方案二）
        // 8、为商户新增默认角色，方便为商户自定义
        String roleId = BsinSnowflake.getId();
        // orgId
        SysRole sysRole = new SysRole(roleId, sysUser.getUsername() + "商户"+ baseApp.getAppName() +"基础角色", "0", baseApp.getAppId(), sysUser.getTenantId(), 4, orgId);
        roleMapper.insert(sysRole);

        // 9、为该角色添加基础功能菜单
        roleMenuMapper.authorizeMenus(baseApp.getAppId(), roleId, authMenuIds);
        List<String> roleIds = new ArrayList<>();
        roleIds.add(roleId);
        // 10、建立商户岗位与角色的关系
        postRoleMapper.assignRoles(postId, roleIds, baseApp.getAppId());
        return sysUser;
    }

    /**
     * 删除
     */
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    @Transactional
    public void delete(String userId) {
        if (ObjectUtil.isEmpty(userId)) {
            throw new BusinessException(ResponseCode.CUSTOMER_IS_NOT_NULL);
        }
        //删除user的岗位关系
        userPostMapper.delete(new LambdaQueryWrapper<UserPost>().eq(UserPost::getUserId, userId));
        //删除user
        userMapper.deleteById(userId);
    }

    @Override
    public void deleteById(String userId) {
        userMapper.deleteById(userId);
    }

    @Override
    public SysUser getByUserId(String userId) {
        SysUser sysUser = userMapper.selectByUserId(userId);
        return sysUser;
    }

    @Override
    public void updateById(SysUser sysUser) {
        userMapper.updateById(sysUser);
    }

    @ShenyuDubboClient("/freeAndUnfree")
    @Override
    public SysUser freeAndUnfree(SysUser sysUser) {
        SysUser user = userMapper.selectById(sysUser.getUserId());
        user.setStatus(sysUser.getStatus());
        userMapper.updateById(user);
        return user;
    }


    /**
     * 更新
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    @Transactional
    public SysUser edit(SysUser sysUser) {
        String userId = sysUser.getUserId();
        if (EmptyChecker.isEmpty(userId)) {
            throw new BusinessException(ResponseCode.ID_NOT_ISNULL);
        }
        String tenantId = sysUser.getTenantId();
        if (tenantId == null) {
            tenantId = LoginInfoContextHelper.getTenantId();
        }
        sysUser.setTenantId(tenantId);
        verifyIfUserExists(sysUser);
        userMapper.updateById(sysUser);
        return sysUser;
    }

    /**
     * 修改用户账户
     *
     * @param sysUser
     * @return
     */

    @Override
    @Transactional
    public SysUser editUserAccount(SysUser sysUser) {
        SysUser user = userMapper.selectById(sysUser.getUserId());
        user.setNickname(sysUser.getNickname());
        user.setPassword(sysUser.getPassword());
        user.setUpdateTime(new Date());
        user.setUpdateBy(sysUser.getUpdateBy());
        List<String> roleIds = sysUser.getRoleIds();
        if (roleIds.size() > 0) {
            userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, sysUser.getUserId()));
            for (String roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setAppId(sysUser.getAppId());
                userRole.setRoleId(roleId);
                userRole.setUserId(sysUser.getUserId());
                userRoleMapper.insert(userRole);
            }
        }
        userMapper.updateById(user);
        return user;
    }

    /**
     * 根据用户名查询用户
     */
    @ApiDoc(desc = "getList")
    @ShenyuDubboClient("/getList")
    @Override
    public List<SysUserDTO> getList(SysUser sysUserDto) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        String phone = sysUserDto.getPhone();
        String orgId = sysUserDto.getOrgId();
        String nickname = sysUserDto.getNickname();
        String username = sysUserDto.getUsername();
        List<SysUser> sysUsers = userMapper.selectUserList(tenantId, nickname, username, phone, orgId);
        List<SysUserDTO> sysUserDTOList = new ArrayList<>();
        if (!sysUsers.isEmpty()) {
            for (SysUser sysUser : sysUsers) {
                SysUserDTO sysUserDTO = new SysUserDTO();
                BeanUtil.copyProperties(sysUser, sysUserDTO);
                // 根据orgId查询用户所属机构
                sysUserDTO.setOrg(orgMapper.selectInfoById(sysUser.getOrgId()));
                sysUserDTOList.add(sysUserDTO);
            }
        }
        return sysUserDTOList;
    }

    @Override
    public List<UserPost> getPostList(String postId, String tenantId) {
        List<UserPost> userPostList = new ArrayList<>();
        if (postId != null) {
            userPostList = userPostMapper.selectList(new LambdaQueryWrapper<UserPost>().eq(UserPost::getPostId, postId));
        } else {
            //查询全部
            userPostList = userPostMapper.getAll(tenantId);
        }
        return userPostList;
    }


    /**
     * 多条件分页查询用户集合
     * 查询当前登录租户的用户
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<SysUser> getPageList(SysUserDTO sysUserDTO) throws Exception {
        sysUserDTO.setTenantId(LoginInfoContextHelper.getTenantId());
        Page<SysUser> page = sysUserDTO.getPagination().build();
        if (sysUserDTO.getSelectAll()) {
            userMapper.selectPageAllList(page, sysUserDTO);
        } else {
            userMapper.selectPageList(page, sysUserDTO);
        }
        if (CollUtil.isEmpty(page.getRecords())) {
            return null;
        }
        return page;
    }

    @ApiDoc(desc = "getListByUserIds")
    @ShenyuDubboClient("/getListByUserIds")
    @Override
    public List<SysUserDTO> getListByUserIds(List<String> userIds) {
        List<SysUser> sysUsers = userMapper.selectListByUserIds(userIds);
        List<SysUserDTO> sysUserDTOList = new ArrayList<>();
        List<String> orgIds = sysUsers.stream().map(SysUser::getOrgId).collect(Collectors.toList());
        Map<String, SysOrg> orgList = new HashMap<>(orgIds.size());
        if (ObjectUtil.isNotEmpty(orgIds)) {
            orgList = Optional.ofNullable(orgMapper.selectList(new LambdaQueryWrapper<SysOrg>()
                            .in(SysOrg::getOrgId, orgIds)))
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .collect(Collectors.toMap(SysOrg::getOrgId, org -> org));
        }
        for (SysUser sysUser : sysUsers) {
            SysUserDTO sysUserDTO = new SysUserDTO();
            BeanUtil.copyProperties(sysUser, sysUserDTO);
            // 根据orgId查询用户所属机构
            sysUserDTO.setOrg(orgList.get(sysUser.getOrgId()));
            sysUserDTOList.add(sysUserDTO);
        }
        return sysUserDTOList;
    }


    /**
     * 根据用户名查询用户(rpc)
     */
    @ApiDoc(desc = "getDetail")
    @ShenyuDubboClient("/getDetail")
    @Override
    public SysUser getDetail(String userId) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        if (userId == null) {
            userId = LoginInfoContextHelper.getAdminUserId();
        }
        SysUser sysUser = userMapper.selectUserInfo(tenantId, userId, null);
        if (sysUser != null) {
            sysUser.setOrg(orgMapper.selectInfoById(sysUser.getOrgId()));
            List<SysPost> sysPosts = postMapper.getPostByUserId(userId);
            if (!sysPosts.isEmpty()) {
                sysUser.setPostList(sysPosts);
            }
        }
        return sysUser;
    }

    /**
     * 根据用户名查询用户(rpc)
     */
    @ApiDoc(desc = "getDetailByPhone")
    @ShenyuDubboClient("/getDetailByPhone")
    @Override
    public SysUser getDetailByPhone(String phone) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        SysUser sysUser = userMapper.selectUserInfo(tenantId, null, phone);
        return sysUser;
    }

    /**
     * 根据用户名查询用户(rpc)
     */
    @ApiDoc(desc = "getDetailByUsername")
    @ShenyuDubboClient("/getDetailByUsername")
    @Override
    public SysUser getDetailByUsername(String username) {
        SysUser sysUser = userMapper.selectUserByUsername(username);
        return sysUser;
    }

    /**
     * 分配岗位
     */
    @ApiDoc(desc = "assignPost")
    @ShenyuDubboClient("/assignPost")
    @Override
    public void assignPost(String userId, List<String> postIds) {
        if (EmptyChecker.isEmpty(userId)) {
            throw new BusinessException(ResponseCode.ID_NOT_ISNULL);
        }
        // 岗位id集合为空时，表示解除所有绑定
        userPostMapper.unbindPost(userId);
        if (postIds.size() < 1) {
            return;
        }
        userPostMapper.assignPosts(userId, postIds);
    }

    /**
     * 根据用户id查询用户能看到的应用
     * 1、用户所在部门能访问的应用，部门下的用户则能查看
     * 2、但是用户进入应用具有的菜单权限根据用户分配的岗位及岗位对应的角色决定
     * userId 为空时是商户登录未认证
     */
    @ApiDoc(desc = "getAppListByUserId")
    @ShenyuDubboClient("/getAppListByUserId")
    @Override
    public AppListResp getAppListByUserId(SysUserDTO sysUserDTO) {
        String userId = LoginInfoContextHelper.getAdminUserId();
        String tenantId = LoginInfoContextHelper.getTenantId();
        String bizRoleType = LoginInfoContextHelper.getBizRoleType();
        List<SysApp> sysApps = null;
        SysTenant sysTenant = tenantMapper.selectTenantInfoByTenantId(tenantId);
        SysProduct sysProduct = productMapper.selectByProductCode(sysTenant.getProductCode());
        // 根据业务角色查询默认应用，商户角色默认应用不存在是跟租户相同
        // TODO 如果是代理商角色，则查询配置的代理商应用 sysAgentAppId
        SysApp baseApp = null;
        if(BizRoleType.SYS_AGENT.getCode().equals(bizRoleType)){
            baseApp = appMapper.selectOneByAppId(sysAgentAppId);
            sysApps = new ArrayList<>();
            sysApps.add(baseApp);
        }else {
            baseApp = tenantAppMapper.selectTenantBaseApp(tenantId, sysProduct.getProductId(), bizRoleType);
            if(baseApp == null){
                baseApp = tenantAppMapper.selectTenantBaseApp(tenantId, sysProduct.getProductId(), null);
            }
            if (StringUtils.isEmpty(userId)) {
                sysApps = new ArrayList<>();
                sysApps.add(baseApp);
            } else {
                SysUser sysUser = userMapper.selectById(userId);
                sysApps = appMapper.selectListByOrgId(sysUser.getOrgId());
            }
        }

        AppListResp appListResp = new AppListResp();
        appListResp.setApps(sysApps);
        appListResp.setDefaultApp(baseApp);
        return appListResp;
    }

    @ApiDoc(desc = "assignRoles")
    @ShenyuDubboClient("/assignRoles")
    @Override
    public void assignRoles(String userId, String appId, List<String> roleIds) {
        // 如果角色列表为零说明解除了所有分配
        userRoleMapper.unAssignRoles(userId, appId);
        if (roleIds.size() < 1) {
            return;
        }
        userRoleMapper.assignRoles(userId, roleIds, appId);
    }

    @ApiDoc(desc = "getRoleListByUserId")
    @ShenyuDubboClient("/getRoleListByUserId")
    @Override
    public List<SysRole> getRoleListByUserId(SysUserDTO sysUserDTO) {
        String userId = sysUserDTO.getUserId();
        String appId = sysUserDTO.getAppId();
        if (userId == null) {
            userId = LoginInfoContextHelper.getAdminUserId();
        }
        List<SysRole> roleList = userRoleMapper.selectListByUserId(userId, appId);
        return roleList;
    }


    /**
     * 机构分配岗位
     * 1、判断解绑的岗位是否被授权给其他用户
     * 2、解除所有授权
     * 3、重新分配岗位
     */
    @ApiDoc(desc = "assignOrgAndPost")
    @ShenyuDubboClient("/assignOrgAndPost")
    @Override
    @Transactional
    public void assignOrgAndPost(String orgId, List<String> postIds, List<String> customerNos) {
        if (orgId == null) {
            throw new BusinessException(ResponseCode.ID_NOT_ISNULL);
        }
        if (customerNos.size() < 1) {
            throw new BusinessException(ResponseCode.CUSTOMER_IS_NOT_NULL);
        }
        for (String customerNo : customerNos) {
            //先删除在新增
            userPostMapper.delete(new LambdaQueryWrapper<UserPost>().eq(UserPost::getUserId, customerNo));
            for (String postId : postIds) {
                UserPost userPost = new UserPost();
                userPost.setPostId(postId);
                userPost.setUserId(customerNo);
                userPostMapper.insert(userPost);
            }
            //修改user和org的关系
            SysUser sysUser = userMapper.selectById(customerNo);
            sysUser.setOrgId(orgId);
            sysUser.setUpdateBy(LoginInfoContextHelper.getCustomerNo());
            sysUser.setUpdateTime(new Date());
            userMapper.updateById(sysUser);
        }
        List<SysPost> postList = postMapper.selectPostListByOrgId(orgId, null, null);
        sign:
        for (SysPost post : postList) {
            for (String postId : postIds) {
                if (post.getPostId().equals(postId)) {
                    continue sign;
                }
            }
            List<SysUser> userList = userMapper.selectUserByPostIdAndOrgId(post.getPostId(), orgId);
            if (userList.size() > 0) {
                throw new BusinessException(ResponseCode.USER_POST_IS_RELATED);
            }
        }
        orgPostMapper.unbindPost(orgId);
        if (postIds.size() < 1) {
            return;
        }
        orgPostMapper.assignPosts(orgId, postIds);

    }


    @Override
    @ApiDoc(desc = "editUserOrg")
    @ShenyuDubboClient("/editUserOrg")
    public void editUserOrg(String orgId, List<String> userIds) {
        if (userIds.size() < 1) {
            throw new BusinessException(ResponseCode.CUSTOMER_IS_NOT_NULL);
        }
        for (String userId : userIds) {
            //修改user和org的关系
            SysUser sysUser = userMapper.selectById(userId);
            sysUser.setOrgId(orgId);
            sysUser.setUpdateBy(LoginInfoContextHelper.getCustomerNo());
            sysUser.setUpdateTime(new Date());
            userMapper.updateByUserId(sysUser);
        }
    }


    /**
     * 根据userIds和orgIds查询user
     *
     * @param userIds
     * @param orgIds
     * @return
     */
    @Override
    public List<SysUser> getListByuserIdsAndOrgIds(List<String> userIds, List<String> orgIds) {
        return userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .in(ObjectUtil.isNotEmpty(orgIds), SysUser::getOrgId, orgIds)
                .in(ObjectUtil.isNotEmpty(userIds), SysUser::getUserId, userIds)
                .eq(SysUser::getDelFlag, 0)
                //在职
                .eq(SysUser::getStatus, 0));
    }

    @Override
    public List<String> getUserIdByName(String name) {
        String tenantId = LoginInfoContextHelper.getLoginUser().getTenantId();
        return userMapper.getUserIdByName(name, tenantId);
    }

    @Override
    public SysUser getUserByRoleCode(String roleCode, String tenantId) {
        return userMapper.getUserByRoleCode(roleCode, tenantId);
    }

    @Override
    @ShenyuDubboClient("/phoneLogin")
    public SysUserVO phoneLogin(SysUser sysUserReq) {
        if (StringUtils.isBlank(sysUserReq.getPhone())) {
            throw new BusinessException(ResponseCode.PHONE_IS_NOT_NULL);
        }
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, sysUserReq.getPhone())
                .eq(SysUser::getTenantId, sysUserReq.getTenantId()));
        return login(sysUserReq, user);
    }

    public List<String> getUserIdByCondition(SysUserDTO sysUserDTO) {
        return userMapper.getUserIdsByCondition(sysUserDTO);
    }

    @Override
    public UserResp getUserInfo(SysUser reqUser) {
        LoginUser loginUser = LoginInfoContextHelper.getLoginUser();
        String tenantId = reqUser.getTenantId();
        if(StringUtils.isBlank(tenantId)){
            tenantId = loginUser.getTenantId();
        }
        SysUser sysUser = userMapper.selectUserInfo(tenantId,reqUser.getUserId(),reqUser.getUsername());
        SysTenant sysTenant = tenantMapper.selectTenantInfoByTenantId(tenantId);
        UserResp userResp=new UserResp();
        userResp.setSysTenant(sysTenant);
        userResp.setSysUser(sysUser);
        return userResp;
    }


    @Override
    public List<SysUser> getUserList(SysUserDTO sysUserDTO) {
        return userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .notIn(ObjectUtil.isNotEmpty(sysUserDTO.getExcludeUserIds()), SysUser::getUserId, sysUserDTO.getExcludeUserIds())
                .eq(StrUtil.isNotBlank(sysUserDTO.getTenantId()), SysUser::getTenantId, sysUserDTO.getTenantId())
                .eq(SysUser::getStatus, UserStatusEnum.ON.getStatus())
                .in(ObjectUtil.isNotEmpty(sysUserDTO.getOrgIds()), SysUser::getOrgId, sysUserDTO.getOrgIds())
                .orderByDesc(SysUser::getCreateTime)
        );
    }


    private SysUserVO login(SysUser sysUserReq, SysUser sysUser) {
        log.info("登录用户：{}", sysUser);
        if (sysUser == null) {
            throw new BusinessException(ResponseCode.USERNAME_PASSWORD_ERROR);
        }
        if (!UserStatusEnum.ON.getStatus().equals(sysUser.getStatus())) {
            throw new BusinessException(ResponseCode.USER_STATUS_ERROR);
        }
        Set<SysApp> appSet = new HashSet<>();
        // 登陆返回的用户机构对象信息
        SysOrg sysOrg = orgMapper.selectInfoById(sysUser.getOrgId());
        // 登陆返回的用户岗位对象信息
        List<SysPost> sysPosts = postMapper.getPostByUserId(sysUser.getUserId());
        // 根据岗位查询岗位角色
        List<SysRole> roles = new ArrayList<>();
        for (SysPost post : sysPosts) {
            roles.addAll(roleMapper.getRoleListByPostId(post.getPostId()));
        }
        // 直接查询用户角色
        List<SysRole> userRoles = roleMapper.getRoleListByUserId(sysUser.getUserId());
        roles.addAll(userRoles);

        // 登陆返回的用户角色对象信息
        if (roles.size() > 0 && roles != null) {
            for (SysRole role : roles) {
                // 登录返回的所属用户角色的应用
                SysApp sysApp = appMapper.getAppInfoByAppId(role.getAppId(), sysUser.getTenantId());
                appSet.add(sysApp);
            }
        }
        SysTenant sysTenant = tenantMapper.selectTenantInfoByTenantId(sysUser.getTenantId());
        // 生成token
        LoginUser loginUser = new LoginUser();
        BeanUtil.copyProperties(sysUserReq, loginUser);
        loginUser.setUserId(sysUser.getUserId());
        loginUser.setMerchantNo(sysUser.getOrgId());
        loginUser.setUsername(sysUser.getUsername());
        loginUser.setPhone(sysUser.getPhone());
        loginUser.setBizRoleType(sysUser.getBizRoleType());
        String token = AuthenticationProvider.createToken(loginUser, authSecretKey, authExpiration);
        // 组装返回报文
        SysUserVO sysUserVO = new SysUserVO();
        sysUserVO.setSysTenant(sysTenant);
        sysUserVO.setSysUser(sysUser);
        sysUserVO.setSysOrg(sysOrg);
        sysUserVO.setSysPost(sysPosts);
        sysUserVO.setSysRoleList(roles);
        sysUserVO.setSysAppSet(appSet);
        sysUserVO.setToken(token);
        return sysUserVO;
    }

    /**
     * 只支持订阅具体应用下的功能
     * 1、找到相关功能对应的菜单
     * 2、新建角色
     * 3、删除功能对应菜单
     * 4、给角色添加功能菜单菜单
     * 5、将角色分配给岗位
     * @param requestMap
     * @return
     */
    @Override
    public Map<String, Object> authMerchantFunction(Map<String, Object> requestMap) {
        List<String> appFunctionIds = (List<String>) requestMap.get("appFunctionIds");
        String tenantId = (String) requestMap.get("tenantId");
        //  将商户号转换为org
        String orgCode = (String) requestMap.get("merchantNo");
        SysOrg sysOrg = orgMapper.selectByOrgCode(orgCode);
        String orgId = sysOrg.getOrgId();

        String appId = (String) requestMap.get("appId");

        // 查询订阅功能对应的菜单
        List<String> authMenuIds = menuMapper.selectListByAppFunctionIds(appFunctionIds);
        // 新增角色
        String roleId = BsinSnowflake.getId();
        SysRole sysRole = new SysRole(roleId, "商户订阅角色", "0", appId, tenantId, 4, orgId);
        roleMapper.insert(sysRole);
        // 为该角色添加基础功能菜单
        roleMenuMapper.authorizeMenus(appId, roleId, authMenuIds);

        // 添加商户默认岗位的code是商户号
        SysPost sysPost = postMapper.getPostByPostCode(orgId);
        List<String> roleIds = new ArrayList<>();
        roleIds.add(roleId);
        // 建立商户岗位与角色的关系
        postRoleMapper.assignRoles(sysPost.getPostId(), roleIds, appId);

        return requestMap;
    }

}
