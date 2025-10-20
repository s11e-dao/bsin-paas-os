package me.flyray.bsin.facade.service;

import java.text.ParseException;
import java.util.Map;

public interface SignUpService {

    /**
     * 本月连续签到次数
     * @param requestMap
     * @return
     */
    public Map<String, Object> getContinuousSignCount(Map<String, Object> requestMap)
            throws ParseException;

    /**
     * 获取累计签到数
     * @param requestMap
     * @return
     */
    public Map<String, Object> getSumSignCount(Map<String, Object> requestMap) throws ParseException;

    /**
     * 签到
     * @param requestMap
     * @return
     */
    public String sign(Map<String, Object> requestMap) throws ParseException;

    /**
     * 签到结果
     * @param requestMap
     * @return
     */
    public boolean getSignResult(Map<String, Object> requestMap) throws ParseException;

    /**
     * 签到信息
     * @param requestMap
     * @return
     */
    public Map<String, String> getSignInfo(Map<String, Object> requestMap) throws ParseException;

}
