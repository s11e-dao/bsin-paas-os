package me.flyray.bsin.server.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.domain.domain.Hello;
import me.flyray.bsin.infrastructure.mapper.HelloMapper;
import me.flyray.bsin.server.utils.RespBodyHandler;

import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
/**
 * @author ：bolei
 * @date ：Created in 2021/11/30 16:23
 * @description：hello world 实现
 * @modified By：
 */

@ShenyuDubboService(path = "/hello", timeout = 6000)
@ApiModule(value = "hello")
@Service
@Slf4j
public class HelloServiceImpl implements HelloService {

  @Autowired private HelloMapper helloMapper;

  @ApiDoc(desc = "add")
  @ShenyuDubboClient("/add")
  @Override
  public Map<String, Object> add(Map<String, Object> requestMap) throws ClassNotFoundException {
    Hello hello = new Hello();
    String name = (String) requestMap.get("name");
    // TODO 雪花算法生成ID
    Hello hello1 = new Hello();
    Class<?> aClass = hello1.getClass();
    // Class<?> aClass = Class.forName("me.flyray.bsin.server.domain.Hello");
    Field[] fields = aClass.getDeclaredFields(); // 获取父类的所有属性
    for (Field field : fields) {
      System.out.println(field);
    }
    hello.setId("1");
    hello.setName(name);
    hello.setDesc("test");
    helloMapper.insert(hello);
    Map<String, Object> m = new HashMap<String, Object>();
    m.put("data", "2");
    return m;
  }

  @ApiDoc(desc = "getList")
  @ShenyuDubboClient("/getList")
  @Override
  public Map<String, Object> getList(Map<String, Object> requestMap) {
    return null;
  }

  /**
   * 分页查询示例
   *
   * @param requestMap
   * @return
   */
  @ApiDoc(desc = "getPageList")
  @ShenyuDubboClient("/getPageList")
  @Override
  public Map<String, Object> getPageList(Map<String, Object> requestMap) {
    Map<String, Object> pagination = (Map<String, Object>) requestMap.get("pagination");
    PageHelper.startPage((Integer) pagination.get("pageNum"), (Integer) pagination.get("pageSize"));
    List<Hello> list = helloMapper.listPage();
    PageInfo<Hello> pageInfo = new PageInfo<Hello>(list);
    return RespBodyHandler.setRespPageInfoBodyDto(pageInfo);
  }

}
