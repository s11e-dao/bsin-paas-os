package me.flyray.bsin.facade.service;

import java.util.Map;

public interface ImageGenerationService {

  /** 文生图： 1、 */
  public Map<String, Object> generateImage(Map<String, Object> requestMap);

  /** 文生图： 1、通过文档知识库 */
  public Map<String, Object> generateImageByDocument(Map<String, Object> requestMap);
}
