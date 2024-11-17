package me.flyray.bsin.mqtt.service.config;

import lombok.Data;
import me.flyray.bsin.mqtt.service.config.model.MqttClientOptions;
import me.flyray.bsin.mqtt.service.config.model.MqttProtocolEnum;
import me.flyray.bsin.mqtt.service.config.model.MqttUseEnum;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author leonard
 * @date 2024/11/17
 * @version 0.1
 */
@Configuration
@Data
@ConfigurationProperties
public class MqttConfiguration {

  private static Map<MqttUseEnum, MqttClientOptions> mqtt;

  public void setMqtt(Map<MqttUseEnum, MqttClientOptions> mqtt) {
    MqttConfiguration.mqtt = mqtt;
  }

  /**
   * Get the configuration options of the basic link of the mqtt client.
   *
   * @return
   */
  static MqttClientOptions getBasicClientOptions() {
    if (!mqtt.containsKey(MqttUseEnum.BASIC)) {
      throw new Error(
          "Please configure the basic mqtt connection parameters first, otherwise application cannot be started.");
    }
    return mqtt.get(MqttUseEnum.BASIC);
  }

  /**
   * Get the mqtt address of the basic link.
   *
   * @return
   */
  public static String getBasicMqttAddress() {
    return getMqttAddress(getBasicClientOptions());
  }

  /**
   * Splice the mqtt address according to the parameters of different clients.
   *
   * @param options
   * @return
   */
  private static String getMqttAddress(MqttClientOptions options) {
    StringBuilder addr =
        new StringBuilder()
            .append(options.getProtocol().getProtocolAddr())
            .append(options.getHost().trim())
            .append(":")
            .append(options.getPort());
    if ((options.getProtocol() == MqttProtocolEnum.WS
            || options.getProtocol() == MqttProtocolEnum.WSS)
        && StringUtils.hasText(options.getPath())) {
      addr.append(options.getPath());
    }
    return addr.toString();
  }

  @Bean
  public MqttConnectOptions mqttConnectOptions() {
    MqttClientOptions customizeOptions = getBasicClientOptions();
    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    mqttConnectOptions.setServerURIs(new String[] {getBasicMqttAddress()});
    mqttConnectOptions.setUserName(customizeOptions.getUsername());
    mqttConnectOptions.setPassword(
        StringUtils.hasText(customizeOptions.getPassword())
            ? customizeOptions.getPassword().toCharArray()
            : new char[0]);
    mqttConnectOptions.setAutomaticReconnect(true);
    mqttConnectOptions.setKeepAliveInterval(10);
    return mqttConnectOptions;
  }

  @Bean
  public MqttPahoClientFactory mqttClientFactory() {
    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    factory.setConnectionOptions(mqttConnectOptions());
    return factory;
  }
}
