package me.flyray.bsin.server.impl;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.service.SignUpService;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.server.biz.SignUpBiz;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@DubboService
@ApiModule(value = "signUp")
@ShenyuDubboService("/signUp")
public class SignUpServiceImpl implements SignUpService {

    @Autowired
    private SignUpBiz signUpBiz;

    /**
     * 本月连续签到次数
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getContinuousSignCount")
    @ShenyuDubboClient("/getContinuousSignCount")
    @Override
    public Map<String, Object> getContinuousSignCount(Map<String, Object> requestMap)
            throws ParseException {
        try {
            String customerNo = (String) requestMap.get("customerNo");
            if (customerNo == null) {
                customerNo = LoginInfoContextHelper.getCustomerNo();
            }
            if (customerNo == null) {
                throw new IllegalArgumentException("用户编号不能为空");
            }
            
            // 定义输出格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 将字符串转化为日期
            String dateStr = (String) requestMap.get("date");
            Date date;
            if (dateStr != null && !dateStr.isEmpty()) {
                date = sdf.parse(dateStr);
            } else {
                date = new DateTime();
            }
            
            Map<String, Object> responseMap = new HashMap<String, Object>();
            Integer continuousSignCount = signUpBiz.getContinuousSignCount(customerNo, date);
            responseMap.put("continuousSignCount", continuousSignCount != null ? continuousSignCount : 0);
            return responseMap;
        } catch (Exception e) {
            log.error("获取连续签到次数失败", e);
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("continuousSignCount", 0);
            return errorMap;
        }
    }

    /**
     * 获取累计签到数
     *
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getSumSignCount")
    @ShenyuDubboClient("/getSumSignCount")
    @Override
    public Map<String, Object> getSumSignCount(Map<String, Object> requestMap) throws ParseException {
        try {
            String customerNo = (String) requestMap.get("customerNo");
            if (customerNo == null) {
                customerNo = LoginInfoContextHelper.getCustomerNo();
            }
            if (customerNo == null) {
                throw new IllegalArgumentException("用户编号不能为空");
            }
            
            // 定义输出格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 将字符串转化为日期
            String dateStr = (String) requestMap.get("date");
            Date date;
            if (dateStr != null && !dateStr.isEmpty()) {
                date = sdf.parse(dateStr);
            } else {
                date = new DateTime();
            }
            
            Map<String, Object> responseMap = new HashMap<String, Object>();
            long sumSignCount = signUpBiz.getSumSignCount(customerNo, date);
            responseMap.put("sumSignCount", sumSignCount); // 修正字段名
            return responseMap;
        } catch (Exception e) {
            log.error("获取累计签到数失败", e);
            Map<String, Object> errorMap = new HashMap<String, Object>();
            errorMap.put("sumSignCount", 0);
            return errorMap;
        }
    }

    /**
     * 签到
     *
     * @return
     */
    @ApiDoc(desc = "sign")
    @ShenyuDubboClient("/sign")
    @Override
    public String sign(Map<String, Object> requestMap) throws ParseException {
        try {
            String customerNo = (String) requestMap.get("customerNo");
            if (customerNo == null) {
                customerNo = LoginInfoContextHelper.getCustomerNo();
            }
            if (customerNo == null) {
                throw new IllegalArgumentException("用户编号不能为空");
            }
            
            // 检查今日是否已签到
            String dateStr = (String) requestMap.get("date");
            Date date;
            if (dateStr != null && !dateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = sdf.parse(dateStr);
            } else {
                date = new DateTime();
            }
            
            // 检查是否已经签到
            if (signUpBiz.checkSign(customerNo, date)) {
                return "今日已签到";
            }
            
            // 执行签到
            String result = signUpBiz.sign(customerNo, date);
            log.info("用户 {} 签到成功，日期: {}", customerNo, date);
            return result;
        } catch (Exception e) {
            log.error("签到失败", e);
            throw new RuntimeException("签到失败: " + e.getMessage());
        }
    }

    /**
     * 签到结果
     *
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getSignResult")
    @ShenyuDubboClient("/getSignResult")
    @Override
    public boolean getSignResult(Map<String, Object> requestMap) throws ParseException {
        try {
            String customerNo = (String) requestMap.get("customerNo");
            if (customerNo == null) {
                customerNo = LoginInfoContextHelper.getCustomerNo();
            }
            if (customerNo == null) {
                log.warn("用户编号为空，返回未签到状态");
                return false;
            }
            
            // 定义输出格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 将字符串转化为日期
            String dateStr = (String) requestMap.get("date");
            Date date;
            if (dateStr != null && !dateStr.isEmpty()) {
                date = sdf.parse(dateStr);
            } else {
                date = new DateTime();
            }
            return signUpBiz.checkSign(customerNo, date);
        } catch (Exception e) {
            log.error("获取签到结果失败", e);
            return false;
        }
    }

    /**
     * 签到信息
     *
     * @param requestMap
     * @return
     */
    @ApiDoc(desc = "getSignInfo")
    @ShenyuDubboClient("/getSignInfo")
    @Override
    public Map<String, String> getSignInfo(Map<String, Object> requestMap) throws ParseException {
        try {
            String customerNo = (String) requestMap.get("customerNo");
            if (customerNo == null) {
                customerNo = LoginInfoContextHelper.getCustomerNo();
            }
            if (customerNo == null) {
                throw new IllegalArgumentException("用户编号不能为空");
            }
            
            // 定义输出格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 将字符串转化为日期
            String dateStr = (String) requestMap.get("date");
            Date date;
            if (dateStr != null && !dateStr.isEmpty()) {
                date = sdf.parse(dateStr);
            } else {
                date = new DateTime();
            }
            
            Map<String, String> signInfo = signUpBiz.getSignInfo(customerNo, date);
            
            // 添加积分信息
            if (signInfo == null) {
                signInfo = new HashMap<>();
            }
            
            // 计算积分奖励信息
            Integer continuousDays = signUpBiz.getContinuousSignCount(customerNo, date);
            long totalDays = signUpBiz.getSumSignCount(customerNo, date);
            
            if (continuousDays != null) {
                signInfo.put("continuousDays", String.valueOf(continuousDays));
            }
            signInfo.put("totalDays", String.valueOf(totalDays));
            
            // 计算积分（每日800积分 + 连续7天额外2000积分）
            int basePoints = (int) (totalDays * 800);
            int bonusPoints = 0;
            if (continuousDays != null && continuousDays >= 7) {
                bonusPoints = Math.floorDiv(continuousDays, 7) * 2000;
            }
            signInfo.put("totalPoints", String.valueOf(basePoints + bonusPoints));
            signInfo.put("basePoints", String.valueOf(basePoints));
            signInfo.put("bonusPoints", String.valueOf(bonusPoints));
            
            return signInfo;
        } catch (Exception e) {
            log.error("获取签到信息失败", e);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("continuousDays", "0");
            errorMap.put("totalDays", "0");
            errorMap.put("totalPoints", "0");
            errorMap.put("basePoints", "0");
            errorMap.put("bonusPoints", "0");
            return errorMap;
        }
    }

}
