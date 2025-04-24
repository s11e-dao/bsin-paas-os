package me.flyray.bsin.facade.mcp.config;

import me.flyray.bsin.facade.mcp.OpenMeteoService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiToolsConfig {

    @Bean
    ToolCallbackProvider tools(OpenMeteoService openMeteoService) {
        ToolCallback[] toolCallbacks = ToolCallbacks.from(openMeteoService) ;
        return ToolCallbackProvider.from(toolCallbacks) ;
    }

}
