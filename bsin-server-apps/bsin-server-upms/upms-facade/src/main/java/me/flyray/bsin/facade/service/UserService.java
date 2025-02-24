package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.entity.UserPost;
import me.flyray.bsin.domain.request.SysUserDTO;
import me.flyray.bsin.domain.response.AppListResp;
import me.flyray.bsin.domain.response.SysUserVO;
import me.flyray.bsin.domain.response.UserResp;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.QueryGroup;

/**
 * @author bolei
 * @date 2023/9/22
 * @desc
 */

@Validated
public interface UserService {

    /**
     * 运营人员登录
     *
     * @param sysUser
     * @return
     */
    @Validated(AddGroup.class)
    public SysUserVO login(@Valid SysUser sysUser);

    /**
     * 新增
     */
    @Validated(AddGroup.class)
    public SysUser add(@Valid SysUserDTO sysUser);

    public SysUser addMerchantOrStoreUser(SysUserDTO sysUserReq);

    /**
     * 删除
     *
     * @return
     */
    public void delete(String userId);

    public void deleteById(String userId);

    public SysUser getByUserId(String userId);

    public void updateById(SysUser sysUser);


    /**
     * 冻结、解冻账户
     */
    public SysUser freeAndUnfree(SysUser sysUser);

    /**
     * 更新
     *
     * @return
     */
    public SysUser edit(SysUser sysUser);

    /**
     * 修改用户账户
     *
     * @return
     */
    public SysUser editUserAccount(SysUser sysUser);

    /**
     * 根据条件查询用户
     *
     * @return
     */
    public List<SysUserDTO> getList(SysUser sysUser);

    public List<UserPost> getPostList(String postId, String tenantId);

    /**
     * 根据租户id分页查询
     */
    @Validated(QueryGroup.class)
    IPage<SysUser> getPageList(@Valid SysUserDTO sysUserDTO) throws Exception;

    /**
     * 根据条件查询用户
     *
     * @return
     */
    public List<SysUserDTO> getListByUserIds(List<String> userIds);

    /**
     * 根据条件查询用户(rpc)
     *
     * @return
     */
    SysUser getDetail(String userId);

    /**
     * 根据条件查询用户(rpc)
     *
     * @return
     */
    SysUser getDetailByUsername(String username);

    /**
     * 根据条件查询用户(rpc)
     *
     * @return
     */
    SysUser getDetailByPhone(String phone);

    /**
     * 分配岗位
     *
     * @return
     */
    public void assignPost(String userId, List<String> postIds);

    /**
     * 根据userId查询该用户所拥有权限的应用
     *
     * @return
     */
    public AppListResp getAppListByUserId(SysUserDTO sysUserDTO);

    /**
     * 给用户分配角色
     */
    public void assignRoles(String userId, String appId, List<String> roleIds);

    /**
     * 根据userId查询该用户所拥有的角色
     *
     * @return
     */
    @Validated(QueryGroup.class)
    public List<SysRole> getRoleListByUserId(SysUserDTO sysUserDTO);

    /**
     * 分配岗位
     *
     * @return
     */
    public void assignOrgAndPost(String orgId, List<String> postIds, List<String> customerNos);

    /**
     * 修改user和org的关系
     *
     * @return
     */
    public void editUserOrg(String orgId, List<String> userIds);

    /**
     * 根据userIds和orgIds查询user
     * @param userIds
     * @param orgIds
     * @return
     */
    public List<SysUser> getListByuserIdsAndOrgIds(List<String> userIds,List<String> orgIds);

    List<String> getUserIdByName(String name);

    SysUser getUserByRoleCode(String roleCode,String tenantId);

    SysUserVO phoneLogin(SysUser sysUser);

    List<String> getUserIdByCondition(SysUserDTO sysUserDTO);

    public UserResp getUserInfo(SysUser reqUser);

    public Map<String, Object> authMerchantFunction(Map<String, Object> requestMap);


}
