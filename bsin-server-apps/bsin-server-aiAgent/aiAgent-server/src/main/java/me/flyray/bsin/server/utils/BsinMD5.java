package me.flyray.bsin.server.utils;

import me.flyray.bsin.exception.BusinessException;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author leonard
 * @description
 * @createDate 2023/12/2023/12/19 /11/13
 */
public class BsinMD5 {
  public static String getMD5Hex(String str) {
    try {
      // 生成一个MD5加密计算摘要
      MessageDigest md = MessageDigest.getInstance("MD5");
      // 输入字符串转换得到的字节数组,计算md5函数
      md.update(str.getBytes());
      // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
      // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
      return new BigInteger(1, md.digest()).toString(16);
    } catch (Exception e) {
      throw new BusinessException("100000", "MD5加密出现错误");
    }
  }

  public static byte[] getMD5Bytes(String str) {
    try {
      // 生成一个MD5加密计算摘要
      MessageDigest md = MessageDigest.getInstance("MD5");
      // 计算md5函数
      md.update(str.getBytes());
      // digest()最后确定返回md5 hash值，返回值为8为字符串
      return md.digest();
    } catch (Exception e) {
      throw new BusinessException("100000", "MD5加密出现错误");
    }
  }
}
