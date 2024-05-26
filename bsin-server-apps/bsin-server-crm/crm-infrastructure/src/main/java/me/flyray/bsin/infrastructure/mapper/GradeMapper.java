package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.domain.Grade;

/**
* @author bolei
* @description 针对表【crm_grade(客户等级划分配置)】的数据库操作Mapper
* @createDate 2023-09-19 23:06:17
* @Entity me.flyray.bsin.domain.Grade
*/

@Repository
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {

}




