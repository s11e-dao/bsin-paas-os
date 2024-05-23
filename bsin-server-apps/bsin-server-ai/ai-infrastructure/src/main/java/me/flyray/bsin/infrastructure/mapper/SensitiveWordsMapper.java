package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.domain.SensitiveWords;
import me.flyray.bsin.domain.domain.WxPlatform;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author leonard
 * @description 针对表【ai_sensitive_words】的数据库操作Mapper
 * @createDate 2023-12-09 01:40:35 @Entity generator.domain.SensitiveWords
 */
@Mapper
@Repository
public interface SensitiveWordsMapper extends BaseMapper<SensitiveWords> {
  SensitiveWords selectSysSensitiveWords(Boolean defaultFlag);
}
