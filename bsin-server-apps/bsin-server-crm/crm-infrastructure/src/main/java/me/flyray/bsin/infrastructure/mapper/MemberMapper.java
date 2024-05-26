package me.flyray.bsin.infrastructure.mapper;

import me.flyray.bsin.domain.domain.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author bolei
* @description 针对表【crm_member】的数据库操作Mapper
* @createDate 2023-08-07 10:00:12
* @Entity me.flyray.bsin.infrastructure.domain.Member
*/

@Repository
@Mapper
public interface MemberMapper extends BaseMapper<Member> {

}




