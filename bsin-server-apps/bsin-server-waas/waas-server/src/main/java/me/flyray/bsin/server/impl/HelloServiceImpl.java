package me.flyray.bsin.server.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import me.flyray.bsin.domain.entity.Hello;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.facade.service.HelloService;
import me.flyray.bsin.infrastructure.mapper.HelloMapper;
import me.flyray.bsin.server.utils.RespBodyHandler;

/**
 * @author ：bolei
 * @date ：Created in 2021/11/30 16:23
 * @description：hello world 实现
 * @modified By：
 */

@Slf4j
@ShenyuDubboService(path = "/hello", timeout = 6000)
@ApiModule(value = "hello")
@Service
public class HelloServiceImpl implements HelloService {

    @Autowired
    private HelloMapper helloMapper;

    @Override
    public Map<String, Object> add(Map<String, Object> requestMap) throws ClassNotFoundException {
        Hello hello2 = BsinServiceContext.getReqBodyDto(Hello.class, requestMap);
        Hello hello = new Hello();
        String name = (String) requestMap.get("name");
        // TODO 雪花算法生成ID
        Hello hello1 = new Hello();
        Class<?> aClass = hello1.getClass();
        //Class<?> aClass = Class.forName("me.flyray.bsin.server.domain.Hello");
        Field[] fields = aClass.getDeclaredFields();//获取父类的所有属性
        for (Field field : fields) {
            System.out.println(field);
        }
        hello.setId("1");
        hello.setName(name);
        hello.setDesc("test");
        Map<String, Object> m = new HashMap<String,Object>();
        m.put("data","2");
        return m;
    }

    @Override
    public Map<String, Object> getList(Map<String, Object> requestMap) {
        return null;
    }

    /**
     * 分页查询示例
     * @param requestMap
     * @return
     */
    @Override
    public Map<String, Object> getPageList(Map<String, Object> requestMap) {
        Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
        PageHelper.startPage((Integer) pagination.get("pageNum"),(Integer) pagination.get("pageSize"));
        PageInfo<Hello> pageInfo = new PageInfo<Hello>(null);
        return RespBodyHandler.setRespPageInfoBodyDto(pageInfo);
    }
}
