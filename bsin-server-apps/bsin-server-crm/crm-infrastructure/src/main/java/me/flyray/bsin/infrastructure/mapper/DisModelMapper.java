package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.DisModel;
import org.apache.ibatis.annotations.Param;

/**
* @author rednet
* @description 针对表【crm_dis_model(分销模型表)】的数据库操作Mapper
* @createDate 2024-10-25 17:14:10
* @Entity generator.domain.DisModel
*/
public interface DisModelMapper extends BaseMapper<DisModel> {

    void update(DisModel user, UpdateWrapper dismodel);

}




