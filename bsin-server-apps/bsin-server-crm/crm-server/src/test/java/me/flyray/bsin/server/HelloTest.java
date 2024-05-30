package me.flyray.bsin.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：bolei
 * @date ：Created in 2021/12/22 10:31
 * @description：单元测试示例
 * @modified By：
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloTest {

    @Autowired
    HelloService helloService;

    @Test
    public void hello() throws ClassNotFoundException {
        Map<String ,Object> map = new HashMap<>();
        helloService.add(map);
    }
}
