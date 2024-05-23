package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysAppFunction;


/**
* @author bolei
* @description 针对表【sys_app_function】的数据库操作Mapper
* @createDate 2023-11-07 14:22:44
* @Entity generator.domain.SysAppFunction
*/

@Repository
@Mapper
public interface AppFunctionMapper {

    void insert(SysAppFunction appFunction);

    void deleteById( @Param("appFunctionId") String appFunctionId);

    List<SysAppFunction> selectListByAppId(String appId);

    IPage<SysAppFunction> selectPageListByAppId(@Param("page") IPage<?> page, @Param("appId") String appId);

}




