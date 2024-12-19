package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.MemberConfig;
import me.flyray.bsin.domain.response.MemberConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;

/**
 * 会员配置：会员模型(CrmMemberConfig)表数据库访问层
 *
 * @author zth
 * @since 2024-12-18 13:34:22
 */
@Mapper
@Repository
public interface MemberConfigMapper extends BaseMapper<MemberConfig> {


    /**
     * 分页查询
     *
     * @param page
     * @param wrapper
     * @return
     */
    Page<MemberConfigVo> queryPageList(@Param("page") Page<MemberConfig> page, @Param("ew") Wrapper<MemberConfig> wrapper);

}

