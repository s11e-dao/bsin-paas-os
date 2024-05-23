package me.flyray.bsin.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author leonard
 * @description
 * @createDate 2024/01/2024/1/24 /00/28
 */
public class VerficationCodeUtil {

  private static final String SYMBOLS = "0123456789";
  private static final Random RANDOM = new SecureRandom();

  /**
   * 生成指定位数的数字验证码
   *
   * @return
   */
  public static String getVerficationCode(int length) {
    char[] nonceChars = new char[length];

    for (int index = 0; index < nonceChars.length; ++index) {
      nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
    }
    return new String(nonceChars);
  }
}
