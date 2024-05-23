package me.flyray.bsin.server.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/19 /11/56
 */
public class ObjectToOther {
  public static <T> List<T> castList(Object obj, Class<T> clazz) {
    List<T> result = new ArrayList<>();
    if (obj instanceof List<?>) {
      for (Object o : (List<?>) obj) {
        result.add(clazz.cast(o));
      }
      return result;
    }
    return null;
  }
}
