package me.flyray.bsin.utils;

import cn.hutool.core.date.DateUtil;
import java.time.LocalDateTime;
import java.util.Date;

public class DateUtils {
  public static boolean belongDateFlag(Date startTime, Date endTime, Date middleTime) {
    LocalDateTime now = DateUtil.toLocalDateTime(middleTime);
    LocalDateTime start = DateUtil.toLocalDateTime(startTime);
    LocalDateTime end = DateUtil.toLocalDateTime(endTime);
    if (now.isAfter(start) && now.isBefore(end)) {
      return true;
    } else {
      return false;
    }
  }
}
