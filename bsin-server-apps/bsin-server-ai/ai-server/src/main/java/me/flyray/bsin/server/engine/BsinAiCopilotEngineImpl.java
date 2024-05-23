package me.flyray.bsin.server.engine;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/7/21 9:09
 * @desc
 */
public class BsinAiCopilotEngineImpl implements BsinAiCopilotEngine {

  @Override
  public Map<String, Object> startExecutors(Map<String, Object> requestMap) {
    String modelKey = (String) requestMap.get("modelKey");
    String variables = (String) requestMap.get("variables");
    // 获取任务模型
    // 社区助手：自我介绍，贡献建议

    // 解析模型

    /**
     * 节点类型： startEvent type: 'start', (开始节点) data{
     *
     * <p>}
     */

    /** 节点类型： llm type: 'llm', (配置) data{ chatgpt apiKey model temperature } */

    /**
     * 节点类型： roleDefinition type: 'roleDefinition',(配置) type: roleDefinition data{ roleName
     * roleDescription }
     */

    /** 节点类型： ai-agent 调用大模型，(有输入输出) type: 'aiAgent', data{ 调用llm input output } */

    /** 节点类型： fetchApi 调用api (有输入输出) type: 'fetchApi', data{ url content-type input output } */

    /** 节点类型： email 邮件节点 type: 'email', data{ url content-type input output } */

    /** 节点类型： prompt 输入 type: 'prompt', data{ content } */

    // 执行模型

    // 与知识库交互

    // 记录执行结果

    return null;
  }
}
