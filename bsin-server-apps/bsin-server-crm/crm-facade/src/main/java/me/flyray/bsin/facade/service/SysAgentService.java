package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.domain.entity.SysAgent;
import me.flyray.bsin.facade.response.GradeVO;

import java.util.List;
import java.util.Map;

/**
 * 系统代理商服务
 */

public interface SysAgentService {

    /**
     * 商户登录
     */
    public Map<String, Object> login(Map<String, Object> requestMap);

    /**
     * 添加
     */
    public SysAgent add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);


    /**
     * 编辑
     */
    public SysAgent edit(Map<String, Object> requestMap);


    /**
     * 商户下分页所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 查询等级、权益、条件详情
     */
    public SysAgent getDetail(Map<String, Object> requestMap);

}
