package me.flyray.bsin.facade.mcp.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class CrmMcpToolService {

    @Tool(description = "新增会员客户")
    public String add(@ToolParam(description = "会员名称") String name) {
        System.out.println("新增会员：" + name);
        System.out.println("新增成功");
        return "新增成功";
    }

}
