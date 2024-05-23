package me.flyray.bsin.server.engine;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/7/20 19:51
 * @desc
 */
public interface BsinAiCopilotEngine {

  Map<String, Object> startExecutors(Map<String, Object> requestMap);
}
