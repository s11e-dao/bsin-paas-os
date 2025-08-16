package me.flyray.bsin.facade.mcp.config;

import me.flyray.bsin.facade.mcp.tools.CrmMcpToolService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiMcpToolsConfig {

  @Bean
  ToolCallbackProvider tools(@Qualifier("crmMcpToolService") CrmMcpToolService crmMcpToolService) {
    ToolCallback[] toolCallbacks = ToolCallbacks.from(crmMcpToolService);
    return ToolCallbackProvider.from(toolCallbacks);
  }

}
