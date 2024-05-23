package me.flyray.bsin.infrastructure.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysRole;
import me.flyray.bsin.domain.response.PostResp;

/**
 * SysPost数据访问
 */
@Repository
@Mapper
public interface PostMapper {

    void insertPost(SysPost sysPost);

    void deleteById(@Param("postId") String postId);

    void updateById(SysPost sysPost);

    List<SysPost> selectList(@Param("postCode") String postCode,
                             @Param("postName") String postName,
                             @Param("tenantId") String tenantId);

    List<PostResp> selectPostListByTenantId(@Param("tenantId") String tenantId);

    List<SysPost> getPostByUserId(@Param("userId") String userId);

    SysPost getPostByPostCode(@Param("postCode") String postCode);

    IPage<SysPost> selectPageList(@Param("page") IPage<?> page,
                                  @Param("tenantId") String tenantId,
                                  @Param("postId") String postId,
                                  @Param("postCode") String postCode,
                                  @Param("postName") String postName);

    IPage<SysPost> selectPostPageListByOrgId(@Param("page") IPage<?> page,
                                             @Param("orgId") String orgId,
                                             @Param("postCode") String postCode,
                                             @Param("postName") String postName);

    List<SysPost> selectPostListByOrgId(@Param("orgId") String orgId,
                                        @Param("postCode") String postCode,
                                        @Param("postName") String postName);

    List<SysPost> getAlreadyAssignPostByUserId(@Param("userId") String userId);

    SysPost getById(String postId);

    IPage<SysPost> selectPostPageAllListByOrgId(@Param("page") Page<SysRole> page,
                                                @Param("orgId") String orgId,
                                                @Param("postCode") String postCode,
                                                @Param("postName") String postName);
}
