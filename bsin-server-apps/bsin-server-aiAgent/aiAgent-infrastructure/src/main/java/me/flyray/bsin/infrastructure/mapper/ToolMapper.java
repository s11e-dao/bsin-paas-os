package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.ToolInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author leonard
 * @description 针对表【ai_tool】的数据库操作Mapper
 * @createDate 2023-12-09 01:40:35 @Entity generator.domain.ToolInfo
 */
@Mapper
@Repository
public interface ToolMapper extends BaseMapper<ToolInfo> {}
