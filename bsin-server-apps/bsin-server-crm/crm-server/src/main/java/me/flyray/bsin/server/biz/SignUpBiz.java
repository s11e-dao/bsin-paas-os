package me.flyray.bsin.server.biz;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.facade.engine.EventServiceEngine;
import me.flyray.bsin.redis.provider.BsinRedisProvider;
import me.flyray.bsin.security.enums.BizRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class SignUpBiz {

  @Autowired
  private EventServiceEngine eventServiceEngine;

  /**
   * 签到
   *
   * @param customerNo 用户id
   * @param date 日期
   * @return
   */
  public String sign(String tenantId, String customerNo, Date date) {
    try {
      if (date == null) {
        throw new IllegalArgumentException("日期不能为空");
      }
      
      String key = buildSignKey(customerNo, date);
      int dayOfMonth = DateUtil.dayOfMonth(date);
      int bitPosition = dayOfMonth - 1;
      
      // 使用Redis位操作（生产环境）
      boolean wasSigned = BsinRedisProvider.getBit(key, bitPosition);
      
      if (wasSigned) {
        log.info("用户 {} 今日已签到，日期: {}", customerNo, DateUtil.format(date, "yyyy-MM-dd"));
        return "今日已签到";
      }
      
      BsinRedisProvider.setBit(key, bitPosition, true);
      
      // 设置过期时间为下个月1号（避免数据积累）
      // 计算到下个月1号的时间（秒）
      Date nextMonth = DateUtil.offsetMonth(date, 1);
      Date nextMonthFirstDay = DateUtil.beginOfMonth(nextMonth);
      long expireTimeSeconds = (nextMonthFirstDay.getTime() - System.currentTimeMillis()) / 1000;
      
      if (expireTimeSeconds > 0) {
        BsinRedisProvider.expire(key, expireTimeSeconds);
        log.debug("设置签到数据过期时间: {}秒", expireTimeSeconds);
      }

      // 调用事件引擎进行签到积分入账
      // 调用事件引擎
      Map<String, Object> executeEvent = new HashMap<>();
      executeEvent.put("eventCode", "signUp");
      executeEvent.put("tenantId", tenantId);
      executeEvent.put("bizRoleType", BizRoleType.CUSTOMER.getCode());
      executeEvent.put("bizRoleTypeNo", customerNo);
      eventServiceEngine.execute(executeEvent);
      
      log.info("用户 {} 签到成功，日期: {}, 位位置: {}", customerNo, DateUtil.format(date, "yyyy-MM-dd"), bitPosition);
      return "签到成功";
    } catch (Exception e) {
      log.error("签到失败，用户: {}, 日期: {}", customerNo, date, e);
      throw new RuntimeException("签到失败: " + e.getMessage());
    }
  }

  /**
   * 获取连续签到次数
   *
   * @param customerNo 用户id
   * @param date 日期
   * @return
   */
  public Integer getContinuousSignCount(String customerNo, Date date) {
    try {
      int dayOfMonth = DateUtil.dayOfMonth(date);
      int signCount = 0;
      
      // 从当前日期开始往前计算连续签到天数
      for (int i = dayOfMonth; i > 0; i--) {
        int bitPosition = i - 1;
        String key = buildSignKey(customerNo, date);
        
        // 检查第bitPosition位是否为1（已签到）
        if (BsinRedisProvider.getBit(key, bitPosition)) {
          signCount++;
        } else {
          // 如果当前不是今天，遇到0就停止计算
          if (i != dayOfMonth) {
            break;
          }
        }
      }
      
      // 如果本月没有连续签到，检查上个月的最后几天
      if (signCount == 0 || (signCount == 1 && dayOfMonth > 1)) {
        // 检查上个月最后几天的签到情况
        Date lastMonth = DateUtil.offsetMonth(date, -1);
        // 使用Java 8时间API获取上个月的天数
        LocalDate lastMonthLocalDate = lastMonth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int lastMonthDays = lastMonthLocalDate.lengthOfMonth();
        String lastMonthKey = buildSignKey(customerNo, lastMonth);
        
        // 从上个月最后一天开始往前检查
        for (int i = lastMonthDays; i > 0; i--) {
          int bitPosition = i - 1;
          if (BsinRedisProvider.getBit(lastMonthKey, bitPosition)) {
            signCount++;
          } else {
            break;
          }
        }
      }
      
      log.debug("用户 {} 连续签到天数: {}", customerNo, signCount);
      return signCount;
    } catch (Exception e) {
      log.error("获取连续签到次数失败，用户: {}, 日期: {}", customerNo, date, e);
      return 0;
    }
  }

  /**
   * 获取本月累计签到数
   *
   * @param customerNo
   * @param date
   * @return
   */
  public long getSumSignCount(String customerNo, Date date) {
    try {
      String key = buildSignKey(customerNo, date);
      
      // 使用Redis位操作统计签到次数
      long signCount = BsinRedisProvider.bitCount(key);
      
      log.debug("用户 {} 本月累计签到数: {}", customerNo, signCount);
      return signCount;
    } catch (Exception e) {
      log.error("获取累计签到数失败，用户: {}, 日期: {}", customerNo, date, e);
      return 0;
    }
  }

  /**
   * 查询当天是否有签到
   *
   * @param customerNo 用户id
   * @param date 日期
   * @return
   */
  public boolean checkSign(String customerNo, Date date) {
    try {
      String key = buildSignKey(customerNo, date);
      int dayOfMonth = DateUtil.dayOfMonth(date);
      int bitPosition = dayOfMonth - 1;
      
      // 使用Redis位操作检查签到状态
      boolean hasSigned = BsinRedisProvider.getBit(key, bitPosition);
      
      log.debug("用户 {} 日期 {} 签到状态: {}", customerNo, DateUtil.format(date, "yyyy-MM-dd"), hasSigned);
      return hasSigned;
    } catch (Exception e) {
      log.error("检查签到状态失败，用户: {}, 日期: {}", customerNo, date, e);
      return false;
    }
  }

  /**
   * 获取本月签到信息
   *
   * @param customerNo 用户id
   * @param date 日期
   * @return
   */
  public Map<String, String> getSignInfo(String customerNo, Date date) {
    try {
      String key = buildSignKey(customerNo, date);
      int dayOfMonth = DateUtil.dayOfMonth(date);
      Map<String, String> signMap = new LinkedHashMap<>(dayOfMonth);
      
      // 优化：批量获取位值，减少Redis调用次数
      long[] bitPositions = new long[dayOfMonth];
      for (int i = 0; i < dayOfMonth; i++) {
        bitPositions[i] = i;
      }
      boolean[] signStatuses = BsinRedisProvider.getBits(key, bitPositions);
      
      // 从第1天到当前天数，构建签到信息
      for (int i = 1; i <= dayOfMonth; i++) {
        LocalDate tempDay = LocalDate.of(
            DateUtil.year(date), 
            DateUtil.month(date), 
            i
        );
        String dateStr = tempDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // 使用批量获取的结果
        boolean hasSigned = signStatuses[i - 1];
        signMap.put(dateStr, hasSigned ? "1" : "0");
      }
      
      log.debug("用户 {} 签到信息: {}", customerNo, signMap);
      return signMap;
    } catch (Exception e) {
      log.error("获取签到信息失败，用户: {}, 日期: {}", customerNo, date, e);
      return new LinkedHashMap<>();
    }
  }

  /**
   * 构建redis Key user:sign:customerNo:yyyyMM
   *
   * @param customerNo 用户id
   * @param date 日期
   * @return
   */
  public String buildSignKey(String customerNo, Date date) {
    return String.format("user:sign:%s:%s", customerNo, DateUtil.format(date, "yyyyMM"));
  }

}
