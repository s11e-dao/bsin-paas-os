package me.flyray.bsin.job.config;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.job.core.executor.AbstractJobExecutor;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import org.springframework.stereotype.Component;

@Component
public class ExtendJobTest extends AbstractJobExecutor {


    @Override
    protected ExecuteResult doJobExecute(JobArgs jobArgs) {
        SnailJobLog.REMOTE.info("执行定时任务，参数：{}", jobArgs);
        return ExecuteResult.success("执行定时任务成功~");
    }
}
