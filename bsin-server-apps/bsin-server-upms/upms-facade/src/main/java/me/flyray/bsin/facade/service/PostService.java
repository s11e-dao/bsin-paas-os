package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;


import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.request.SysPostDTO;
import me.flyray.bsin.domain.response.PostResp;
import me.flyray.bsin.validate.AddGroup;
import me.flyray.bsin.validate.QueryGroup;


@Validated
public interface PostService {

    /**
     * 新增
     */
    @Validated(AddGroup.class)
    public SysPost add(@Valid SysPost post);

    /**
     * 删除
     */
    public SysPost delete(SysPost post);

    /**
     * 更新
     */
    public SysPost edit(SysPost sysPost);

    /**
     * 根据租户id分页查询岗位
     */
    IPage<?> getPageList(SysPostDTO postDTO);

    /**
     * 根据条件查询岗位列表
     */
    public List<SysPost> getList(SysPost sysPost);

    /**
     * 分配角色
     *
     * @return
     */
    public void assignRoles(String postId, String appId, List<String> roleIds);

    /**
     * 根据租户id查询岗位列表
     */
    public List<PostResp> getPostListByTenantId();

    /**
     * 根据机构id分页查询岗位
     *
     * @return
     */
    @Validated(QueryGroup.class)
    public IPage<?> getPageListByOrgId(@Valid SysPostDTO postDTO);

    /**
     * 根据岗位id查询所有角色
     *
     * @return
     */
    public List<SysRole> getRolesByPostId(String postId);

    /**
     * 根据用户查询已分配的岗位
     *
     * @return
     */
    public List<SysPost> getAssignedPostByUserId(String userId);

    /**
     * 根据用户查询可以分配的岗位
     *
     * @return
     */
    public List<SysPost> getAssignablePostByUserId(String bizUserId);

    /**
     * 根据用户查询可以分配的岗位
     *
     * @return
     */
    public List<SysPost> getPostListByOrgId(String orgId);
}
