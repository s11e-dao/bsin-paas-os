package me.flyray.bsin.job.config;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import org.springframework.stereotype.Component;

@Component
@JobExecutor(name = "singleAnnotationJob")
public class SingleAnnotationJobTest {


    /**
     * <p>
     *     由于JobExecutor注解默认的方法名为jobExecute
     *     如果想自定义方法名则：  @JobExecutor(name = "singleAnnotationJob",method=自定义方法名)
     *
     * </p>
     * @param jobArgs
     * @return
     */
    public ExecuteResult jobExecute(JobArgs jobArgs){
        SnailJobLog.REMOTE.info("执行定时任务，{}",jobArgs);
        return ExecuteResult.success("执行成功~~~");
    }
}
