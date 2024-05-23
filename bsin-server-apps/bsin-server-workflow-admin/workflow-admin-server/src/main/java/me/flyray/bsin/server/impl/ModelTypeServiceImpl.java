package me.flyray.bsin.server.impl;

import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.constants.ResponseCode;
import me.flyray.bsin.context.BsinServiceContext;
import me.flyray.bsin.domain.entity.ModelType;
import me.flyray.bsin.domain.response.ModelTypeTree;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.facade.service.BsinAdminModelTypeService;
import me.flyray.bsin.infrastructure.biz.ModelTypeBiz;
import me.flyray.bsin.infrastructure.mapper.ModelTypeMapper;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.shenyu.client.apache.dubbo.annotation.ShenyuDubboService;
import org.apache.shenyu.client.apidocs.annotations.ApiDoc;
import org.apache.shenyu.client.apidocs.annotations.ApiModule;
import org.apache.shenyu.client.dubbo.common.annotation.ShenyuDubboClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模型类型
 */
@Slf4j
@DubboService
@ApiModule(value = "modelType")
@ShenyuDubboService("/modelType")
public class ModelTypeServiceImpl implements BsinAdminModelTypeService {
    @Autowired
    private ModelTypeMapper modelTypeMapper;
    @Autowired
    private ModelTypeBiz modelTypeBiz;

    /**
     * 查看所有模型类型
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getModelTypeTree")
    @ApiDoc(desc = "getModelTypeTree")
    public List<ModelTypeTree> getModelTypeTree(Map<String, Object> requestMap) {
        String tenantId = (String) requestMap.get("tenantId");
        List<ModelType> modelTypes = modelTypeMapper.getModelTypeListByTenantId(tenantId);
        // 组装成父子的树形目录结构
        // 查询所有的一级机构(parentId=-1)
        List<ModelTypeTree> treeList = modelTypes.stream().filter(modelType -> modelType.getParentId().equals("-1"))
                .map(m -> {
                    ModelTypeTree levelModelType = new ModelTypeTree(m.getId(), m.getTypeCode(), m.getTypeName(),
                            m.getParentId(), m.getDescription(),m.getDelFlag(),m.getCreateTime(),
                            m.getUpdateTime(), modelTypeBiz.getModelTypeTree(m, modelTypes));
                    return levelModelType;
                }).collect(Collectors.toList());
        return treeList;
    }

    /**
     * ID,名称模糊，编号查看模型类型
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/getModelTypeList")
    @ApiDoc(desc = "getModelTypeList")
    public List<ModelType> getModelTypeList(Map<String, Object> requestMap) {
        ModelType modelType = BsinServiceContext.getReqBodyDto(ModelType.class, requestMap);
        List<ModelType> typeByNameAndCode = modelTypeMapper.selectModelTypeList(modelType);
        return typeByNameAndCode;
    }

    /**
     *生成新的模型类型
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/add")
    @ApiDoc(desc = "add")
    public void add(Map<String, Object> requestMap) {
        LoginUser user = LoginInfoContextHelper.getLoginUser();
        ModelType modelType = BsinServiceContext.getReqBodyDto(ModelType.class, requestMap);
        try{
            modelTypeMapper.insertModelType(modelType);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new BusinessException(ResponseCode.MODEL_TYPE_ADD_FAIL);
        }
    }

    /**
     * 修改模型类型
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/edit")
    @ApiDoc(desc = "edit")
    public void edit(Map<String, Object> requestMap) {
        ModelType modelType = BsinServiceContext.getReqBodyDto(ModelType.class, requestMap);
        try{
            modelTypeMapper.updateModelTypeById(modelType);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new BusinessException(ResponseCode.MODEL_TYPE_UPDATE_FAIL);
        }
    }

    /**
     * 删除模型类型
     * @param requestMap
     * @return
     */
    @Override
    @ShenyuDubboClient("/delete")
    @ApiDoc(desc = "delete")
    public void delete(Map<String, Object> requestMap) {
        String id=(String)requestMap.get("id");
        modelTypeMapper.deleteById(id);
    }
}
