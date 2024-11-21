package me.flyray.bsin.mqtt.domain.enums;

import static me.flyray.bsin.mqtt.domain.enums.TopicConst.*;

import java.util.Arrays;
import java.util.regex.Pattern;
import lombok.Getter;
import me.flyray.bsin.mqtt.domain.channal.ChannelName;

/**
 * @author yihui wang
 * @version 1.0
 * @description: TODO
 * @date 2023/7/26 14:28
 */
@Getter
public enum TopicEnum {
  STATE_ENVENTS(
      Pattern.compile("^" + MY_BASIC_PRE + REGEX_SN + ENVENTS_TEST + "$"),
      ChannelName.ENVENTS_INBOUND_TEST),
  UNKNOWN(Pattern.compile("^.*$"), ChannelName.DEFAULT);

  Pattern pattern;

  String beanName;

  TopicEnum(Pattern pattern, String beanName) {
    this.pattern = pattern;
    this.beanName = beanName;
  }

  public static TopicEnum find(String topic) {
    return Arrays.stream(TopicEnum.values())
        .filter(topicEnum -> topicEnum.pattern.matcher(topic).matches())
        .findAny()
        .orElse(UNKNOWN);
  }
}
