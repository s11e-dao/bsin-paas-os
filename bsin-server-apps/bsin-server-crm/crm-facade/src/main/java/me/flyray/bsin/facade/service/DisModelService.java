package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.DisModel;
import me.flyray.bsin.domain.entity.Grade;

import java.util.Map;

/**
* @author bolei
* @description 针对表【crm_dis_model(分销模型表)】的数据库操作Service
* @createDate 2024-10-25 17:14:10
*/
public interface DisModelService {

    /**
     * 添加
     */
    public DisModel add(Map<String, Object> requestMap);

    /**
     * 删除
     */
    public void delete(Map<String, Object> requestMap);

    /**
     * 编辑
     */
    public DisModel edit(Map<String, Object> requestMap);


    /**
     * 详情
     */
    public DisModel getDetail(Map<String, Object> requestMap);

    /**
     * 租户下所有
     */
    public IPage<?> getPageList(Map<String, Object> requestMap);


    DisModel update(Map<String, Object> requestMap);
}
