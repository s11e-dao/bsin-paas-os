package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.CustomerBase;
import me.flyray.bsin.domain.entity.Grade;
import me.flyray.bsin.domain.entity.MemberGrade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
* @author bolei
* @description 针对表【crm_member_grade(客户等级)】的数据库操作Mapper
* @createDate 2023-07-26 16:05:42
* @Entity me.flyray.bsin.infrastructure.domain.MemberGrade
*/

@Repository
@Mapper
public interface MemberGradeMapper extends BaseMapper<MemberGrade> {

    Grade selectMemberGrade(String customerNo);

    List<CustomerBase> selectMemberListByGrade(String gradeNo,  String ccy);


    IPage<CustomerBase> selectMemberPageListByGrade(@Param("page") IPage<?> page, String gradeNo);

//    List<Member> selectMemberGradeList(String gradeNo);

}




