package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysApp;
import me.flyray.bsin.domain.entity.SysProductApp;


/**
* @author bolei
* @description 针对表【sys_product_app】的数据库操作Mapper
* @createDate 2023-11-05 18:00:01
* @Entity generator.domain.SysProductApp
*/

@Repository
@Mapper
public interface ProductAppMapper {

    void insert(SysProductApp appFunction);

    public void deleteById(@Param("productId") String productId, @Param("appId") String appId);

    List<SysApp> selectListByProductId(@Param("productId") String productId);
    
    //List<SysApp> selectPageList(@Param("productId") String productId);

    IPage<SysApp> selectPageList(@Param("page") IPage<?> page, @Param("productId") String productId);

    List<String> selectListByProductCode(@Param("productCode") String tenantProductCode);

    SysProductApp selectByProductIdAndAppId(@Param("productId") String productId, @Param("appId") String appId);

}





