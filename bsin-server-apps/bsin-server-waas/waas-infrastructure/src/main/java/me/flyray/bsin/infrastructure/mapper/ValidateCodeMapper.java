package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.ValidateCode;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author Admin
* @description 针对表【validate_code】的数据库操作Mapper
*/

@Repository
@Mapper
public interface ValidateCodeMapper extends BaseMapper<ValidateCode> {

}




