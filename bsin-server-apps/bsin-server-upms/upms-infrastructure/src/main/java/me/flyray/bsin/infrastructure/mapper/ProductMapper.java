package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import me.flyray.bsin.domain.entity.SysProduct;


/**
* @author bolei
* @description 针对表【sys_product】的数据库操作Mapper
* @createDate 2023-11-05 17:59:52
* @Entity generator.domain.SysProduct
*/

@Repository
@Mapper
public interface ProductMapper{

    List<SysProduct> selectList(@Param("productName") String productName);

    SysProduct selectByProductCode(@Param("productCode") String productCode);

    public void insert(SysProduct product);

    public void deleteById(String id);

    public void updateById(SysProduct product);

    IPage<SysProduct> selectPageList(@Param("page") IPage<?> page, @Param("productName") String productName);

}




