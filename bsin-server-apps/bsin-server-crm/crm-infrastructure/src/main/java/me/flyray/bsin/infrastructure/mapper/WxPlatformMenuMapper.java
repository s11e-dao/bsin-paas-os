package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import me.flyray.bsin.domain.entity.WxPlatformMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author bolei
 * @description 针对表【ai_wx_platform_menu】的数据库操作Mapper
 * @createDate 2024-01-26 11:16:19
 */
@Mapper
@Repository
public interface WxPlatformMenuMapper extends BaseMapper<WxPlatformMenu> {

  List<WxPlatformMenu> selectByWxPlatformMenuTemplateNo(String wxPlatformMenuTemplateNo);
}
