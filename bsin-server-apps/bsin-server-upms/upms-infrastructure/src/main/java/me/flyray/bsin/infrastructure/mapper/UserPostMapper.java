package me.flyray.bsin.infrastructure.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysPost;
import me.flyray.bsin.domain.entity.SysUser;
import me.flyray.bsin.domain.entity.UserPost;

@Repository
@Mapper
public interface UserPostMapper extends BaseMapper<UserPost> {

    void assignPosts(@Param("userId") String userId, @Param("postIds") List<String> postIds);

    void unbindPost(@Param("userId") String userId);

    List<SysPost> getUserByPostId(@Param("postId") String postId);

    SysUser getPostByUserId(@Param("userId") String userId);



    List<UserPost> getAll(@Param("tenantId")String tenantId);
}
