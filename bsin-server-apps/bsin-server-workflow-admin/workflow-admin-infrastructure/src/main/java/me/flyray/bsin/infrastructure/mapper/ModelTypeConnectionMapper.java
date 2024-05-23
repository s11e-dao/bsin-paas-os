package me.flyray.bsin.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ModelTypeConnectionMapper {

    /**
     * 添加模型type关联数据
     * @param
     * @return
     */
    void insert(@Param("modelTypeId") String modelTypeId,@Param("modelId") String modelId);
}
