package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.ModelType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ModelTypeMapper extends BaseMapper<ModelType> {
    /**
     * 查看所有模型类型
     * @param
     * @return
     */
    List<ModelType> getModelTypeListByTenantId(@Param("tenantId") String tenantId);


    /**
     * 生成新的模型类型
     * @param
     * @return
     */
    void insertModelType(ModelType modelType);

    /**
     * 修改模型类型
     * @param
     * @return
     */
    void updateModelTypeById(ModelType modelType);

    /**
     * 删除模型类型
     * @param
     * @return
     */
    void deleteById(String id);

    /**
     * ID,名称模糊，编号查看模型类型
     * @param
     * @return
     */
    List<ModelType> selectModelTypeList(ModelType modelType);

}
