package me.flyray.bsin.facade.service;

import me.flyray.bsin.domain.entity.ModelType;
import me.flyray.bsin.domain.response.ModelTypeTree;

import java.util.List;
import java.util.Map;

/**
 * @author huangzh
 * @ClassName ModelTypeSVC
 * @DATE 2020/9/28 15:15
 */

public interface BsinAdminModelTypeService {
    /**
     * 查看所有模型类型
     * @param requestMap
     * @return
     */
    List<ModelTypeTree> getModelTypeTree(Map<String, Object> requestMap);

    /**
     * ID,名称模糊，编号查看模型类型
     * @param requestMap
     * @return
     */
    List<ModelType> getModelTypeList(Map<String, Object> requestMap);

    /**
     * 生成新的模型类型
     * @param requestMap
     * @return
     */
    void add(Map<String, Object> requestMap);

    /**
     * 修改模型类型
     * @param requestMap
     * @return
     */
    void edit(Map<String, Object> requestMap);

    /**
     * 删除模型类型
     * @param requestMap
     * @return
     */
    void delete(Map<String, Object> requestMap);
}
