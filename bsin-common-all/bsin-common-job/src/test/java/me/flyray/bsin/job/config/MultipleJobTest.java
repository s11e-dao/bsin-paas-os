package me.flyray.bsin.job.config;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import org.springframework.stereotype.Component;

@Component
public class MultipleJobTest {



    @JobExecutor(name = "testA",method = "testA")
    public ExecuteResult testA(JobArgs jobArgs){
        //控制台日志
        SnailJobLog.LOCAL.info("执行定时任务A，参数：{}",jobArgs);
        //上报日志到服务端
        SnailJobLog.REMOTE.info("执行定时任务A，参数：{}",jobArgs);
        return ExecuteResult.success("执行成功");
    }


    @JobExecutor(name = "testB",method = "testB")
    public ExecuteResult testB(JobArgs jobArgs){
        //控制台日志
        SnailJobLog.LOCAL.info("执行定时任务B，参数：{}",jobArgs);
        //上报日志到服务端
        SnailJobLog.REMOTE.info("执行定时任务B，参数：{}",jobArgs);
        return ExecuteResult.success("执行成功");
    }

}
