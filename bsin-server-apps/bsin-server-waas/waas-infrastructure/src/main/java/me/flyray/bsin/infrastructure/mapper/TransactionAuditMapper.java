package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.flyray.bsin.domain.entity.TransactionAudit;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* @author Admin
*/

@Repository
@Mapper
public interface TransactionAuditMapper extends BaseMapper<TransactionAudit> {

}




