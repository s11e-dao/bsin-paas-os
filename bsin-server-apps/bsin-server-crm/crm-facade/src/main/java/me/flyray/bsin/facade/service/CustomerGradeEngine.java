package me.flyray.bsin.facade.service;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/28 15:40
 * @desc 客户等级引擎
 */
public interface CustomerGradeEngine {

    /**
     * 1、入参客户号和完成事件的编号(动作)
     * 2、完成事件激励发放，没有激励则跳过
     * 3、客户等级升级
     */
    public Map<String, Object> execute(Map<String, Object> requestMap);


    /**
     * 1、验证客户等级
     */
    public Map<String, Object>  verifyGrade(Map<String, Object> requestMap);

}
