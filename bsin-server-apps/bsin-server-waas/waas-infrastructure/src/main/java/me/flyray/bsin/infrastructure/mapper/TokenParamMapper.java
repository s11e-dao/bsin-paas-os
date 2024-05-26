package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.domain.TokenParam;

/**
* @author bolei
* @description 针对表【da_token】的数据库操作Mapper
* @createDate 2023-08-22 11:57:38
* @Entity me.flyray.bsin.domain.Token
*/

@Repository
@Mapper
public interface TokenParamMapper extends BaseMapper<TokenParam> {

}




