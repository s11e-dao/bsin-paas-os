package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.domain.OutputParsers;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author leonard
 * @description 针对表【ai_output_parsers】的数据库操作Mapper
 * @createDate 2023-12-09 01:40:35 @Entity generator.domain.OutputParsers
 */
@Mapper
@Repository
public interface OutputParsersMapper extends BaseMapper<OutputParsers> {}
