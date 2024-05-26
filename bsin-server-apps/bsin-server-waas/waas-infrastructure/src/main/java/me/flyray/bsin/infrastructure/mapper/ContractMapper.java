package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.domain.Contract;

/**
* @author bolei
* @description 针对表【da_contract】的数据库操作Mapper
* @createDate 2023-06-29 20:06:00
* @Entity generator.domain.Contract
*/

@Repository
@Mapper
public interface ContractMapper extends BaseMapper<Contract> {

}




