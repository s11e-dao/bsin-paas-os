package me.flyray.bsin.server.push;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

/**
 * @author leonard ClassName:PushTemplateUtil.java date:2023-04-24 15:21 Description: 模板推送工具类
 */
@UtilityClass
public class PushTemplateUtil {

  @SneakyThrows
  public void sendMessage(String openId, String key1, String key2, WxMpService weixinService) {
    WxMpTemplateMessage templateMessage =
        WxMpTemplateMessage.builder()
            .toUser(openId)
            .templateId("mhRrvWIuHsot7MyTOd4niFotOGM62h-Z3-yLtJkSLnU")
            .url(
                "https://mp.weixin.qq.com/debug/cgi-bin/sandboxinfo?action=showinfo&t=sandbox/index")
            .build();

    templateMessage
        .addData(new WxMpTemplateData("first", "来自火源兽的回复..."))
        .addData(new WxMpTemplateData("key1", key1))
        .addData(new WxMpTemplateData("key2", key2))
        .addData(new WxMpTemplateData("remark", "回复结束！！！"));

    weixinService.getTemplateMsgService().sendTemplateMsg(templateMessage);
  }
}
