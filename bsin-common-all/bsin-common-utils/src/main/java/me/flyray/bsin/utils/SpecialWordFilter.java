package me.flyray.bsin.utils;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author leonard
 * @description
 * @createDate 2024/01/2024/1/24 /01/29
 */
public class SpecialWordFilter {

  /**
   * 原文本是否包含指定字符
   *
   * @return
   */
  public static Boolean containedWord(String containedWord, String sourceText) {
    if (sourceText == null || containedWord == null) {
      return false;
    } else {
      return sourceText.contains(containedWord);
    }
  }

}
