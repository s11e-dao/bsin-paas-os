package org.apache.shenyu.plugin.rulesEngine;

import org.apache.shenyu.common.dto.PluginData;
import org.apache.shenyu.common.dto.RuleData;
import org.apache.shenyu.common.dto.SelectorData;
import org.apache.shenyu.plugin.base.handler.PluginDataHandler;
import org.springframework.stereotype.Component;

@Component
public class RulesEnginePluginDataHandler implements PluginDataHandler {
    @Override
    public void handlerPlugin(PluginData pluginData) {
        System.out.println("handlerPlugin: " + pluginData);
    }

    @Override
    public void removePlugin(PluginData pluginData) {
        System.out.println("removePlugin: " + pluginData);
    }

    @Override
    public void handlerSelector(SelectorData selectorData) {
        System.out.println("handlerSelector: " + selectorData);
    }

    @Override
    public void removeSelector(SelectorData selectorData) {
        System.out.println("removeSelector: " + selectorData);
    }

    @Override
    public void handlerRule(RuleData ruleData) {
        System.out.println("handlerRule: " + ruleData);
    }

    @Override
    public void removeRule(RuleData ruleData) {
        System.out.println("removeRule: " + ruleData);
    }

    @Override
    public String pluginNamed() {
        return "rulesEngine";
    }

}
