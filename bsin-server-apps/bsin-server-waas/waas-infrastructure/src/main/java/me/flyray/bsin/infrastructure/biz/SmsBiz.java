package me.flyray.bsin.infrastructure.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import me.flyray.bsin.domain.entity.SmsLog;
import me.flyray.bsin.domain.entity.ValidateCode;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.infrastructure.mapper.SmsLogMapper;
import me.flyray.bsin.infrastructure.mapper.ValidateCodeMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import me.flyray.bsin.sms.infobip.InfobipSmsUtil;
import me.flyray.bsin.utils.BsinSnowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SmsBiz {
    @Autowired
    public SmsLogMapper smsLogMapper;
    @Autowired
    public ValidateCodeMapper validateCodeMapper;

    /**
     * 判断某手机号今天发送短信是否超限
     * @param mobile 手机号
     */
    public void checkUpperLimit(String mobile, String ip)throws BusinessException{
        QueryWrapper<SmsLog> queryWrapper = new QueryWrapper<>();
        Date date = new Date();
        queryWrapper.eq("mobile", mobile);
        queryWrapper.ge("create_time", getStartOfDay(date));
        queryWrapper.le("create_time", getEndOfDay(date));
        Long count = smsLogMapper.selectCount(queryWrapper);
        if(count > 50){
            throw new BusinessException("手机号发送短信超限");
        }
    }

    /**
     * 发送验证码
     * @param mobile
     * @param sendType
     * @return
     */
    public Map<String,Object> sendValidateCode(String mobile, int sendType) {
        Map<String,Object> map = new HashMap<>();
        try{
            if(mobile == null || "".equals(mobile)){
                throw new BusinessException("MOBILE_NOT_EMPTY");
            }
            LoginUser user = LoginInfoContextHelper.getLoginUser();
            // 检查手机号是否超限
            checkUpperLimit(mobile,null);
            // 生成验证码
            String validCode = getValidCode();
            String msg = String.format("验证码：%s。您正在身份验证，需要进行验证码校验（5分钟内有效），请勿向任何人提供此验证码",validCode);
            // 发送短信
            sendMsg(mobile,sendType,msg);
            // 保存验证码
            ValidateCode validateCode = new ValidateCode();
            String serialNo = BsinSnowflake.getId();
            validateCode.setSerialNo(serialNo);
            validateCode.setValidateType(sendType);
            validateCode.setMobile(mobile);
            validateCode.setValidateCode(validCode);
            validateCode.setMsg(msg);
            validateCode.setCreateTime(new Date());
            validateCode.setCreateBy(user.getUserId());
            validateCode.setStatus(1);
            validateCodeMapper.insert(validateCode);
            map.put("uniqueKey",serialNo);
            return map;
        }catch (BusinessException be){
            throw be;
        }catch (Exception e){
            throw new BusinessException("SYSTEM_ERROR");
        }
    }

    public void verifyCode(String uniqueKey, String code){
        if(uniqueKey == null || "".equals(uniqueKey)){
            throw new BusinessException("UNIQUE_KEY_NOT_EMPTY");
        }
        QueryWrapper<ValidateCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("serial_no", uniqueKey);
        queryWrapper.notIn("status", 2,3);
        ValidateCode validateCode = validateCodeMapper.selectOne(queryWrapper);
        if(validateCode == null){
            throw new BusinessException("VALIDATE_CODE_NOT_EXIST");
        }
        // 校验验证码
        if(!validateCode.getValidateCode().equals(code)){
            validateCode.setStatus(2);
            validateCodeMapper.updateById(validateCode);
            throw new BusinessException("VERIFY_FAIL");
        }
        // 时间暂定5分钟内有效
        if(new Date().getTime() - validateCode.getCreateTime().getTime() > 300000){
            validateCode.setStatus(2);
            validateCodeMapper.updateById(validateCode);
            throw new BusinessException("VERIFY_TIMEOUT");
        }
        validateCode.setStatus(3);
        validateCodeMapper.updateById(validateCode);
    }

    /**
     * 发送短信
     * 1、记录短信发送日志
     */
    public void sendMsg(String mobile,Integer validateType, String msg){
        SmsLog smsLog = new SmsLog();
        try{
            LoginUser user = LoginInfoContextHelper.getLoginUser();
            // 短信日志记录
            smsLog.setSerialNo(BsinSnowflake.getId());
            smsLog.setMobile(mobile);
            smsLog.setChannel(1);  // 1、InfoBip
            smsLog.setLogType(validateType);
            smsLog.setContent(msg);
            smsLog.setCreateTime(new Date());
            smsLog.setCreateBy(user.getUserId());
            // 发送短信
            String rspMsg = InfobipSmsUtil.sendCode(mobile, msg);
            smsLog.setRspMsg(rspMsg);
            smsLogMapper.insert(smsLog);
        }catch (Exception e){
            smsLog.setStatus(2);
            smsLogMapper.insert(smsLog);
            throw new BusinessException("SMS_SEND_FAIL");
        }
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 返回6位随机数
     */
    public String getValidCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
