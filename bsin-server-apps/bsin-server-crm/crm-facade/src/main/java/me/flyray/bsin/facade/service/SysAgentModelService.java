package me.flyray.bsin.facade.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.domain.entity.SysAgentModel;
import me.flyray.bsin.facade.response.GradeVO;

import java.util.List;
import java.util.Map;

/**
* @description 针对表【crm_sys_agent_model(合伙伙伴模型表)】的数据库操作Service
* @createDate 2025-07-20 19:40:48
*/
public interface SysAgentModelService {

    /**
     * 添加
     */
    public SysAgentModel config(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);


    /**
     * 编辑
     */
    public SysAgentModel edit(Map<String, Object> requestMap);


    /**
     * 下分页所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);

    /**
     * 详情
     */
    public SysAgentModel getDetail(Map<String, Object> requestMap);

}
