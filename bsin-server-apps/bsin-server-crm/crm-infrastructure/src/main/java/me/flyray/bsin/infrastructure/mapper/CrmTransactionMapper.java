package me.flyray.bsin.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.domain.entity.CrmTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
* @author Admin
* @description 针对表【crm_transaction(交易记录;)】的数据库操作Mapper
*/

@Repository
@Mapper
public interface CrmTransactionMapper extends BaseMapper<CrmTransaction> {


}




