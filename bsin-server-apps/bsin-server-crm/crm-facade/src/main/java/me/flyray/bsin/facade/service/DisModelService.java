package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.DisModel;
import me.flyray.bsin.domain.entity.Grade;

import java.util.Map;

/**
* @author rednet
* @description 针对表【crm_dis_model(分销模型表)】的数据库操作Service
* @createDate 2024-10-25 17:14:10
*/
public interface DisModelService {
    /**
     * 添加
     */
    public DisModel update(Map<String, Object> requestMap);

}
