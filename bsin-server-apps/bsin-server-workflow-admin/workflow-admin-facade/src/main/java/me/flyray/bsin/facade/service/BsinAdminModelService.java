package me.flyray.bsin.facade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import me.flyray.bsin.domain.entity.ActDeModel;
import me.flyray.bsin.domain.request.ModelRepresentationDTO;
import me.flyray.bsin.domain.response.DefinitionResp;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelRepresentation;


import java.util.Map;


public interface BsinAdminModelService {
    /**
     * 保存对应model的属性信息
     * @param modelRepresentationDTO
     * @return
     */
    ModelRepresentation saveModel(ModelRepresentationDTO modelRepresentationDTO);

    /**
     * 保存对应model的xml信息
     * @param requestMap
     * @return
     */
    Model saveModelXML(Map<String, Object> requestMap);


    /**
     * 修改模型
     * @param requestMap
     * @return
     */
    ModelRepresentation updateModel(Map<String, Object> requestMap);

    /**
     * 删除模型
     * @param requestMap
     * @return
     */
    void deleteModel(Map<String, Object> requestMap);

    /**
     * 分页查询所有模型
     * @param requestMap
     * @return
     */
    PageInfo<ActDeModel> getPageListModel(Map<String, Object> requestMap);

    /**
     * 部署/发布模型
     * @param requestMap
     * @return
     */
    Map<String, Object> deployModel(Map<String, Object> requestMap);

    /**
     * 模型预览
     * @param requestMap
     * @return
     */
    Map<String, Object> getModelById(Map<String, Object> requestMap);

    /**
     * 历史版本
     * @param requestMap
     * @return
     */
    ResultListDataRepresentation getHistoryVersions(Map<String, Object> requestMap);

    /**
     * 发布版本
     * @param requestMap
     * @return
     */
    DefinitionResp getModelDefinitionVersions(Map<String, Object> requestMap);

    /**
     * 保存表单
     */
    public void saveForm(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 保存表单信息
     */
    public ModelRepresentation saveFormModel(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 分页查询所有表单模型
     */
    public PageInfo<ActDeModel> getPageListFromModel(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 获取表单信息
     */
    public Model getFormInfo(Map<String, Object> requestMap) throws ClassNotFoundException;

    /**
     * 部署表单
     */
    public void deployForm(Map<String, Object> requestMap) throws ClassNotFoundException;

}
