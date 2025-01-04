package me.flyray.bsin.server.context;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.flyray.bsin.domain.entity.DecisionRule;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import me.flyray.bsin.infrastructure.mapper.DecisionRuleMapper;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class DecisionEngineContextBuilder {

    // 此类本身就是单例的
    private final KieServices kieServices = KieServices.get();
    // kie文件系统，需要缓存，如果每次添加规则都是重新new一个的话，则可能出现问题。即之前加到文件系统中的规则没有了
    private final KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
    // 可以理解为构建 kmodule.xml
    private final KieModuleModel kieModuleModel = kieServices.newKieModuleModel();

    @Getter
    private KieContainer kieContainer;

    @Autowired
    private DecisionRuleMapper decisionRuleMapper;
    @Autowired
    private BsinServiceInvoke bsinServiceInvoke;

    @PreDestroy
    public void destroy() {
        if (null != kieContainer) {
            kieContainer.dispose();
        }
    }

    /**
     * 初始化规则引擎，将数据库中规则加载到规则库中
     */
    public void decisionEngineInitial(){
        // 查询数据库中的规则
        List<DecisionRule> decisionRules = decisionRuleMapper.getAllDecisionRuleList();
        for (DecisionRule decisionRule : decisionRules) {
            //TODO 根据规则类型解析规则内容,解析出ksession
            addOrUpdateRule(decisionRule);
        }
    }

    public KieSession buildDecisionEngine_back(DecisionRule decisionRule, String kieSessionModelName){
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(decisionRule.getContent(), ResourceType.DRL);
        // 创建KieBase是一个成本很大的
        KieBase kieBase = kieHelper.build(EqualityBehaviorOption.IDENTITY);
        log.info("{}", kieBase);
        // 创建KieSession成本小
        KieSession kieSession = kieBase.newKieSession();
        return kieSession;
    }

    /**
     * 构建决策引擎上下文
     */
    public KieSession buildDecisionEngine(DecisionRule decisionRule, String kieSessionModelName){
        // 判断该kbase是否存在
        addOrUpdateRule(decisionRule);
        //8.从容器中获取一个会话，这里和a处添加的是一个key，否则找不到 找不到任何一个会报异常
        KieSession kieSession = kieContainer.newKieSession(kieSessionModelName);
        return kieSession;
    }

    /**
     * 处理决策结果
     */
    public <T> Map<String, Object>  buildResMessage(T objct){
        Map<String, Object> resultMap = new HashMap<>();
        // TODO 处理决策结果
        return resultMap;
    }

    /**
     * 判断该kbase是否存在
     */
    public boolean existsKieBase(String kieBaseName) {
        if (null == kieContainer) {
            return false;
        }
        Collection<String> kieBaseNames = kieContainer.getKieBaseNames();
        if (kieBaseNames.contains(kieBaseName)) {
            return true;
        }
        log.info("需要创建KieBase:{}", kieBaseName);
        return false;
    }

    /**
     * 添加或更新 drools 规则
     */
    public void addOrUpdateRule(DecisionRule decisionRule) {
        // 获取kbase的名称
        String kieBaseName = decisionRule.getKieBaseName();
        // 判断该kbase是否存在
        boolean existsKieBase = existsKieBase(kieBaseName);
        // 该对象对应kmodule.xml中的kbase标签
        KieBaseModel kieBaseModel = null;
        if (!existsKieBase) {
            // 创建一个kbase
            kieBaseModel = kieModuleModel.newKieBaseModel(kieBaseName);
            // 不是默认的kieBase
            kieBaseModel.setDefault(false);
            // 设置该KieBase需要加载的包路径
            kieBaseModel.addPackage(decisionRule.getKiePackageName());
            //TODO 设置kieSession, 一个kbase下存在多个kieSession
            kieBaseModel.newKieSessionModel(kieBaseName + "-session")
                    // 不是默认session
                    .setDefault(false);
        } else {
            // 获取到已经存在的kbase对象
            kieBaseModel = kieModuleModel.getKieBaseModels().get(kieBaseName);
            // 获取到packages
            List<String> packages = kieBaseModel.getPackages();
            if (!packages.contains(decisionRule.getKiePackageName())) {
                kieBaseModel.addPackage(decisionRule.getKiePackageName());
                log.info("kieBase:{}添加一个新的包:{}", kieBaseName, decisionRule.getKiePackageName());
            } else {
                kieBaseModel = null;
            }
        }
        String file = "src/main/resources/" + decisionRule.getKiePackageName() + "/" + decisionRule.getSerialNo() + ".drl";
        log.info("加载虚拟规则文件:{}", file);
        kieFileSystem.write(file, decisionRule.getContent());

        if (kieBaseModel != null) {
            String kmoduleXml = kieModuleModel.toXML();
            log.info("加载kmodule.xml:[\n{}]", kmoduleXml);
            kieFileSystem.writeKModuleXML(kmoduleXml);
        }
        // 构建KieContainer对象
        buildKieContainer();
    }

    /**
     * 构建KieContainer
     */
    private KieContainer buildKieContainer() {
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        // 通过KieBuilder构建KieModule下所有的KieBase
        kieBuilder.buildAll();
        // 获取构建过程中的结果
        Results results = kieBuilder.getResults();
        // 获取错误信息
        List<Message> messages = results.getMessages(Message.Level.ERROR);
        if (null != messages && !messages.isEmpty()) {
            for (Message message : messages) {
                log.error(message.getText());
            }
        }
        // KieContainer只有第一次时才需要创建，之后就是使用这个
        if (null == kieContainer) {
            kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
        } else {
            // 实现动态更新
            ((KieContainerImpl) kieContainer).updateToKieModule((InternalKieModule) kieBuilder.getKieModule());
        }
        return kieContainer;
    }

    /**
     * 加工处理事实对象
     * 1、解析before配置的指标字段
     * 2、获取事实指标参数：从请求参数获取或是通过泛化调用获取
     * @param decisionRule
     * @return
     */
    public Map<String, Object> buildDecisionFact(DecisionRule decisionRule, Map executeParams) {
        log.info("请求参数: {}", executeParams);
        // 根据decisionRule中json before配置获取指标字段事实
        if(true){
            // 泛化调用获取事实指标对象，从对象获取指标字段

        }
        // 直接获取请求参数指标

        Map<String, Object> params = new HashMap<>();
        // 创建要处理的Map对象，事实数据的处理
        // params.put("sex", executeParams.getParams().get("sex"));

        return params;
    }

    /**
     * 处理规则触发后的调用
     * @param decisionRule
     * @param globalMap
     */
    public void handleThenResult(DecisionRule decisionRule, Map<String, Object> globalMap) {

        log.info("规则触发结果参数: {}", globalMap);
        // 根据decisionRule中json after配置获取指标字段事实
        if(true){
            // 泛化调用获取事实指标对象，从对象获取指标字段

        }

    }
}
