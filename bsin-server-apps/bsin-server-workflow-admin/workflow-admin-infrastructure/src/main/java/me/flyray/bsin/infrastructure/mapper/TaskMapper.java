package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;

/**
 * @author huangzh
 * @ClassName NGDBTaskMapper
 * @DATE 2020/11/10 14:39
 */


@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    /**
     * 查询运行中的任务实例
     * @param params
     * @return
     */
    List<Task> selectTasks(Map<String, Object> params);

    /**
     * 查询历史任务实例
     * @param params
     * @return
     */
    List<Task> selectHistoricTasks(Map<String, Object> params);

    List<Task> selectTasksByUser(@Param("tenantId") String tenantId,
                                 @Param("assignee") String assignee,
                                 @Param("owner") String owner);
}
