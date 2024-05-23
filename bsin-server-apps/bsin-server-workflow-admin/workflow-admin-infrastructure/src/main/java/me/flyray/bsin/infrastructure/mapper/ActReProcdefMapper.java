package me.flyray.bsin.infrastructure.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.flowable.engine.repository.ProcessDefinition;

import java.util.List;


@Mapper
public interface ActReProcdefMapper{

    List<ProcessDefinition> getProcessDefinitionPageList(@Param("tenantId") String tenantId,
                                                         @Param("name") String name,
                                                         @Param("key") String key);

}
