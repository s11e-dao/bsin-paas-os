package me.flyray.bsin.facade.engine;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/8/21
 * @desc
 */
public interface EventRuleIncentiveEngine {

    /**
     * 1、入参客户号和完成事件的编号(动作) 完成活动、任务
     * 2、完成事件激励发放，没有激励则跳过
     * 3、客户等级升级
     */
    public void excute(Map<String, Object> requestMap);

}
