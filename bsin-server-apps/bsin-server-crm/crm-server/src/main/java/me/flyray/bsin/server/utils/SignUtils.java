package me.flyray.bsin.server.utils;

import cn.hutool.core.date.DateUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SignUtils {


  /**
   * 签到
   *
   * @param customerNo 用户id
   * @param date 日期
   * @return
   */
  public String sign(String customerNo, Date date) {
    String key = buildSignKey(customerNo, date);
    int dayOfMonth = DateUtil.dayOfMonth(date);
//    BsinCacheProvider.setBit(key, dayOfMonth - 1, true);
    return "签到成功";
  }

  /**
   * 获取连续签到次数
   *
   * @param customerNo 用户id
   * @param date 日期
   * @return
   */
  public Integer getContinuousSignCount(String customerNo, Date date) {
    String key = buildSignKey(customerNo, date);
    int dayOfMonth = DateUtil.dayOfMonth(date);
    // 获取用户从当前日期开始到1号的签到状态
    List<Long> list = null;
//            BsinCacheProvider.bitField(
//            key,
//            BitFieldSubCommands.create()
//                .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
//                .valueAt(0));
    if (list == null || list.isEmpty()) {
      return 0;
    }
    // 连续签到计数器
    int signCount = 0;
    long v = list.get(0) == null ? 0 : list.get(0);
    // 位移运算连续签到次数
    for (int i = dayOfMonth; i > 0; i--) {
      // i表示位移操作的次数，右移再左移如果等于自己说明最低位是0，表示未签到
      if (v >> 1 << 1 == v) {
        // 用户可能还未签到，所以要排除是否是当天的可能性
        if (i != dayOfMonth) break;
      } else {
        // 右移再左移，如果不等于自己说明最低位是1，表示签到
        signCount++;
      }
      v >>= 1;
    }
    return signCount;
  }

  /**
   * 获取本月累计签到数
   *
   * @param customerNo
   * @param date
   * @return
   */
  public long getSumSignCount(String customerNo, Date date) {
    String key = buildSignKey(customerNo, date);
    int dayOfMonth = DateUtil.dayOfMonth(date);
    return 0 ;// bsinCacheProvider.execute(
        //(RedisCallback<Long>) connection -> connection.bitCount(key.getBytes()));
  }

  /**
   * 查询当天是否有签到
   *
   * @param customerNo 用户id
   * @param date 日期
   * @return
   */
  public boolean checkSign(String customerNo, Date date) {
    String key = buildSignKey(customerNo, date);
    int dayOfMonth = DateUtil.dayOfMonth(date);
    return false; //bsinCacheProvider.getBit(key, dayOfMonth - 1);
  }

  /**
   * 获取本月签到信息
   *
   * @param customerNo 用户id
   * @param date 日期
   * @return
   */
  public Map<String, String> getSignInfo(String customerNo, Date date) {
    String key = buildSignKey(customerNo, date);
    int dayOfMonth = DateUtil.dayOfMonth(date);
    Map<String, String> signMap = new LinkedHashMap<>(dayOfMonth);
    // 获取BitMap中的bit数组，并以十进制返回
    List<Long> bitFieldList = null;
//        (List<Long>)
//            bsinCacheProvider.execute(
//                (RedisCallback<List<Long>>)
//                    cbk ->
//                        cbk.bitField(
//                            key.getBytes(),
//                            BitFieldSubCommands.create()
//                                .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
//                                .valueAt(0)));
    if (bitFieldList != null && bitFieldList.size() > 0) {
      Long valueDec = bitFieldList.get(0) != null ? bitFieldList.get(0) : 0;
      // 使用i--,从最低位开始处理
      for (int i = dayOfMonth; i > 0; i--) {
        LocalDate tempDayOfMonth = LocalDate.now().withDayOfMonth(i);
        // valueDec先右移一位再左移以为得到一个新值，这个新值最低位的二进制为0，再与valueDec做比较，如果相等valueDec的最低位是0，否则是1
        if (valueDec >> 1 << 1 != valueDec) {
          signMap.put(tempDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "1");
        } else {
          signMap.put(tempDayOfMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "0");
        }
        // 每次处理完右移一位
        valueDec >>= 1;
      }
    }
    return signMap;
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
