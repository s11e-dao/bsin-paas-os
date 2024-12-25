package me.flyray.bsin.server.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.request.SysPostDTO;
import me.flyray.bsin.domain.response.PostResp;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.mapper.PostMapper;
import me.flyray.bsin.infrastructure.mapper.PostRoleMapper;
import me.flyray.bsin.infrastructure.mapper.RoleMapper;
import me.flyray.bsin.infrastructure.mapper.UserMapper;
import me.flyray.bsin.infrastructure.mapper.UserPostMapper;
import me.flyray.bsin.facade.service.PostService;
import me.flyray.bsin.mybatis.utils.Pagination;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
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


@Transactional(rollbackFor = Exception.class)
@ShenyuDubboService(path = "/post")
@ApiModule(value = "post")
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper sysPostMapper;
    @Autowired
    private PostRoleMapper postRoleMapper;
    @Autowired
    private UserPostMapper userPostMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 添加岗位
     */
    @ApiDoc(desc = "add")
    @ShenyuDubboClient("/add")
    @Override
    public SysPost add(SysPost post) {
        //判断岗位编码是否存在
        SysPost sysPost = sysPostMapper.getPostByPostCode(post.getPostCode());
        if (sysPost != null) {
            throw new BusinessException(ResponseCode.POST_CODE_EXIST);
        }
        String id = BsinSnowflake.getId();
        post.setPostId(id);
        post.setTenantId(LoginInfoContextHelper.getTenantId());
        sysPostMapper.insertPost(post);
        return post;
    }

    /**
     * 删除岗位
     */
    @ApiDoc(desc = "delete")
    @ShenyuDubboClient("/delete")
    @Override
    public SysPost delete(SysPost post) {
        // 查询岗位和角色是否有关联
        List<SysPost> roleByPostId = postRoleMapper.getRoleByPostId(post.getPostId());
        // 查询岗位和用户是否有关联
        List<SysPost> userByPostId = userPostMapper.getUserByPostId(post.getPostId());
        if (roleByPostId.size() > 0) {
            throw new BusinessException(ResponseCode.POSITION_ROLE_IS_RELATED);
        } else if (userByPostId.size() > 0) {
            throw new BusinessException(ResponseCode.POSITION_USER_IS_RELATED);
        }
        sysPostMapper.deleteById(post.getPostId());
        return post;
    }

    /**
     * 更新岗位
     */
    @ApiDoc(desc = "edit")
    @ShenyuDubboClient("/edit")
    @Override
    public SysPost edit(SysPost sysPost) {
        SysPost post = sysPostMapper.getById(sysPost.getPostId());
        post.setPostName(sysPost.getPostName());
        post.setUpdateTime(new Date());
        sysPostMapper.updateById(post);
        return sysPost;
    }

    /**
     * 根据条件查询岗位列表
     */
    @ApiDoc(desc = "getList")
    @ShenyuDubboClient("/getList")
    @Override
    public List<SysPost> getList(SysPost sysPost) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        return sysPostMapper.selectList(sysPost.getPostCode(), sysPost.getPostName(), tenantId);
    }

    /**
     * 根据租户id分页查询所有岗位
     */
    @ApiDoc(desc = "getPageList")
    @ShenyuDubboClient("/getPageList")
    @Override
    public IPage<?> getPageList(SysPostDTO postDTO) {
        String tenantId = LoginInfoContextHelper.getTenantId();
        Pagination pagination = postDTO.getPagination();
        String postId = postDTO.getPostId();
        String postName = postDTO.getPostName();
        String postCode = postDTO.getPostCode();
        Page<SysRole> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<SysPost> pageList = sysPostMapper.selectPageList(page, tenantId, postId, postCode, postName);
        return pageList;
    }

    /**
     * 分配角色
     */
    @ApiDoc(desc = "assignRoles")
    @ShenyuDubboClient("/assignRoles")
    @Override
    public void assignRoles(String postId, String appId, List<String> roleIds) {
        if (postId == null) {
            throw new BusinessException(ResponseCode.ID_NOT_ISNULL);
        }
        // 如果角色列表为零说明解除了所有分配
        postRoleMapper.unAssignRoles(postId, appId);
        if (roleIds.size() < 1) {
            return;
        }
        postRoleMapper.assignRoles(postId, roleIds, appId);
    }

    /**
     * 根据租户id查询岗位列表
     */
    @ApiDoc(desc = "getPostListByTenantId")
    @ShenyuDubboClient("/getPostListByTenantId")
    @Override
    public List<PostResp> getPostListByTenantId() {
        String tenantId = LoginInfoContextHelper.getTenantId();
        return sysPostMapper.selectPostListByTenantId(tenantId);
    }

    /**
     * 根据机构id分页查询岗位
     */
    @ApiDoc(desc = "getPageListByOrgId")
    @ShenyuDubboClient("/getPageListByOrgId")
    @Override
    public IPage<?> getPageListByOrgId(SysPostDTO postDTO) {
        Pagination pagination = postDTO.getPagination();
        String orgId = postDTO.getOrgId();
        String postCode = postDTO.getPostCode();
        String postName = postDTO.getPostName();
        Page<SysRole> page = new Page<>(pagination.getPageNum(), pagination.getPageSize());
        IPage<SysPost> pageList = new Page<>();
        if (postDTO.getSelectAll()) {
            pageList = sysPostMapper.selectPostPageAllListByOrgId(page, orgId, postCode, postName);
        } else {
            pageList = sysPostMapper.selectPostPageListByOrgId(page, orgId, postCode, postName);
        }

        return pageList;
    }

    /**
     * 根据岗位id查询所有角色
     */
    @ApiDoc(desc = "getRolesByPostId")
    @ShenyuDubboClient("/getRolesByPostId")
    @Override
    public List<SysRole> getRolesByPostId(String postId) {
        return roleMapper.getRoleListByPostId(postId);
    }

    /**
     * 根据用户查询已经分配的岗位
     */
    @ApiDoc(desc = "getAssignedPostByUserId")
    @ShenyuDubboClient("/getAssignedPostByUserId")
    @Override
    public List<SysPost> getAssignedPostByUserId(String userId) {
        return sysPostMapper.getAlreadyAssignPostByUserId(userId);
    }

    /**
     * 根据用户查询可以分配的岗位
     */
    @ApiDoc(desc = "getAssignablePostByUserId")
    @ShenyuDubboClient("/getAssignablePostByUserId")
    @Override
    public List<SysPost> getAssignablePostByUserId(String userId) {
        SysUser user = userMapper.selectById(userId);
        return sysPostMapper.selectPostListByOrgId(user.getOrgId(), null, null);
    }

    /**
     * 根据机构id查询岗位
     */
    @ApiDoc(desc = "getPostListByOrgId")
    @ShenyuDubboClient("/getPostListByOrgId")
    @Override
    public List<SysPost> getPostListByOrgId(String orgId) {
        return sysPostMapper.selectPostListByOrgId(orgId, null, null);
    }

}
