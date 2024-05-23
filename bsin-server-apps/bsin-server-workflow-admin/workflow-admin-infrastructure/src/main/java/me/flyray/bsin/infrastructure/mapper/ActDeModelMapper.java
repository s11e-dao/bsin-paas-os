package me.flyray.bsin.infrastructure.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.ActDeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ActDeModelMapper extends BaseMapper<ActDeModel> {
    List<ActDeModel> getProcessModelByType(@Param("tenantId") String tenantId, @Param("modelTypeId")  String modelTypeId);

//    List<ActDeModel> getFormModel(@Param("tenantId") String tenantId, @Param("modelType") String modelType,@Param("modelKey") String modelKey);

    int setActDeModelTenantId (@Param("id") String id ,@Param("tenantId") String tenantId);

    ActDeModel getActDeModelKey(String modelKey);

    List<ActDeModel> getFormModel(@Param("actDeModel") ActDeModel actDeModel);
}
