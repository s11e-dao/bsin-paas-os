package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import me.flyray.bsin.domain.entity.ContractProtocol;

/**
 * mapper
 * @author makejava
 */

@Repository
@Mapper
public interface ContractProtocolMapper extends BaseMapper<ContractProtocol> {

    ContractProtocol getContractProtocolByContract(String contract);

}
