package me.flyray.bsin.server.init;

import me.flyray.bsin.domain.entity.SensitiveWords;
import me.flyray.bsin.infrastructure.mapper.SensitiveWordsMapper;
import me.flyray.bsin.wordFilter.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author leonard
 * @description: 项目启动的时候执行该方法，在spring容器初始化的时候执行该方法，执行顺序在静态代码块和构造方法之后
 * @createDate 2024/01/2024/1/24 /15/13
 */
@Component
public class PostConstructInit {

  @Autowired private SensitiveWordsMapper sensitiveWordsMapper;

  static {
    InetAddress ip = null;
    try {
      ip = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    System.out.println("IP Address1 : " + ip.getHostAddress());
  }

  public PostConstructInit() {
    InetAddress ip = null;
    try {
      ip = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    System.out.println("IP Address2 : " + ip.getHostAddress());
  }

  @PostConstruct
  public void init() {
    InetAddress ip = null;
    try {
      ip = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    System.out.println("IP Address3 : " + ip.getHostAddress());
    // 系统敏感词加载
    if (SensitiveWordFilter.wordList == null) {
      System.out.println("系统系统敏感词加载");
      SensitiveWords sensitiveWords = sensitiveWordsMapper.selectSysSensitiveWords(true);
      if (sensitiveWords != null) {
        SensitiveWordFilter.loadWordFromString(sensitiveWords.getContent());
      }
    }
  }
}
