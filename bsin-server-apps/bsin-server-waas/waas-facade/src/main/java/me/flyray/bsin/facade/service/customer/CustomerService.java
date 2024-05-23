package me.flyray.bsin.facade.service.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.flyray.bsin.domain.entity.customer.CustomerBase;
import me.flyray.bsin.utils.BsinResultEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author bolei
 * @date 2023/6/27
 * @desc 客户服务
 */
public interface CustomerService {

  /** 分页查询客户信息 */
  IPage<CustomerBase> getPageList(@Param("requestMap") Map<String, Object> requestMap);

}
